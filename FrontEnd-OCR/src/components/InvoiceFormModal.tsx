import React, { useState, useEffect, useRef } from 'react';
import { motion } from 'framer-motion';
import { ApiService } from '../services/apiService';
import { ImageUploadService } from '../services/imageUploadService';
import { Country, Category, CostCenter, Card, CompleteInvoiceRequest } from '../types/api';
import { ResultModal } from './ResultModal';
import { useNotification } from '../hooks/useNotification';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from './ui/dialog';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from './ui/select';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Textarea } from './ui/textarea';
import { Button } from './ui/button';

interface InvoiceFormData {
  country: string;
  cardId: string;
  vendorName: string;
  invoiceDate: string;
  totalAmount: string;
  currency: string;
  concept: string;
  category: string;
  costCenter: string;
  clientVisited: string;
  notes: string;
}

interface FieldError {
  field: string;
  message: string;
}

interface InvoiceFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  imageUrl: string;
  extractedData?: Partial<InvoiceFormData>;
  userCards?: Card[];
  userId: number;
  imageFile?: File; // Original image file for upload
}





const currencies = [
  { value: 'USD', label: 'USD - Dólar' },
];

export const InvoiceFormModal: React.FC<InvoiceFormModalProps> = ({
  isOpen,
  onClose,
  imageUrl,
  extractedData = {},
  userCards = [],
  userId,
  imageFile,
}) => {
  const [countries, setCountries] = useState<Country[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [costCenters, setCostCenters] = useState<CostCenter[]>([]);
  const [loadingCountries, setLoadingCountries] = useState(false);
  const [loadingCategories, setLoadingCategories] = useState(false);
  const [loadingCostCenters, setLoadingCostCenters] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [uploadingImage, setUploadingImage] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<FieldError[]>([]);
  const [shouldShowMainModal, setShouldShowMainModal] = useState(true);
  const fetchingDataRef = useRef(false);
  
  // Hook de notificación
  const { notificationState, showNotification, hideNotification } = useNotification();
  
  const [formData, setFormData] = useState<InvoiceFormData>({
    country: '',
    cardId: '',
    vendorName: extractedData.vendorName || '',
    invoiceDate: extractedData.invoiceDate || '',
    totalAmount: extractedData.totalAmount || '',
    currency: extractedData.currency || 'USD',
    concept: extractedData.concept || '',
    category: extractedData.category || '',
    costCenter: extractedData.costCenter || '',
    clientVisited: '',
    notes: '',
  });

  // Load data on component mount
  useEffect(() => {
    const fetchData = async () => {
      if (!isOpen) return;
      
      // Prevenir llamadas duplicadas
      if (fetchingDataRef.current) return;
      
      fetchingDataRef.current = true;
      setLoadingCountries(true);
      setLoadingCategories(true);
      setLoadingCostCenters(true);
      
      try {
        // Fetch all data in parallel
        const [countriesData, categoriesData, costCentersData] = await Promise.all([
          ApiService.getCountries(),
          ApiService.getCategories(),
          ApiService.getCostCenters(),
        ]);
        
        setCountries(countriesData);
        setCategories(categoriesData);
        setCostCenters(costCentersData);
      } catch (error) {
        console.error('Error loading form data:', error);
        // Fallback to empty arrays, user can still use the form
        setCountries([]);
        setCategories([]);
        setCostCenters([]);
      } finally {
        setLoadingCountries(false);
        setLoadingCategories(false);
        setLoadingCostCenters(false);
        fetchingDataRef.current = false;
      }
    };

    fetchData();
  }, [isOpen]);

  const handleInputChange = (
    field: keyof InvoiceFormData,
    value: string
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    // Clear field error when user starts typing
    if (fieldErrors.some(error => error.field === field)) {
      setFieldErrors(prev => prev.filter(error => error.field !== field));
    }
  };

  const validateForm = (): boolean => {
    const errors: FieldError[] = [];

    // Required fields
    if (!formData.country) {
      errors.push({ field: 'country', message: 'El país es obligatorio' });
    }
    if (!formData.cardId) {
      errors.push({ field: 'cardId', message: 'La tarjeta es obligatoria' });
    }
    if (!formData.vendorName.trim()) {
      errors.push({ field: 'vendorName', message: 'El nombre del proveedor es obligatorio' });
    }
    if (!formData.invoiceDate) {
      errors.push({ field: 'invoiceDate', message: 'La fecha de factura es obligatoria' });
    } else {
      // Validar que la fecha pertenezca al mes y año actual
      const invoiceDate = new Date(`${formData.invoiceDate}T00:00:00`); // Usar T00:00:00 para evitar problemas de zona horaria
      const currentDate = new Date();
      if (invoiceDate.getFullYear() !== currentDate.getFullYear() || invoiceDate.getMonth() !== currentDate.getMonth()) {
        errors.push({ field: 'invoiceDate', message: 'La fecha debe pertenecer al mes y año actual.' });
      }
    }
    if (!formData.totalAmount || parseFloat(formData.totalAmount) <= 0) {
      errors.push({ field: 'totalAmount', message: 'El monto total debe ser mayor a 0' });
    }
    if (!formData.currency) {
      errors.push({ field: 'currency', message: 'La moneda es obligatoria' });
    }
    if (!formData.concept.trim()) {
      errors.push({ field: 'concept', message: 'El concepto es obligatorio' });
    }
    if (!formData.category) {
      errors.push({ field: 'category', message: 'La categoría es obligatoria' });
    }

    // Optional fields don't need validation: costCenter, clientVisited, notes

    setFieldErrors(errors);
    return errors.length === 0;
  };

  const getFieldError = (field: string): string | undefined => {
    return fieldErrors.find(error => error.field === field)?.message;
  };

  const handleCloseModal = () => {
    onClose();
    // Reset form and errors when closing
    setFieldErrors([]);
    setShouldShowMainModal(true);
    setFormData({
      country: '',
      cardId: '',
      vendorName: extractedData.vendorName || '',
      invoiceDate: extractedData.invoiceDate || '',
      totalAmount: extractedData.totalAmount || '',
      currency: extractedData.currency || 'USD',
      concept: extractedData.concept || '',
      category: extractedData.category || '',
      costCenter: extractedData.costCenter || '',
      clientVisited: '',
      notes: '',
    });
  };

  const handleSubmit = async () => {
    if (isSubmitting) return;
    
    // Validate form
    if (!validateForm()) {
      return;
    }
    
    // Validate image file exists
    if (!imageFile) {
      showNotification('error');
      return;
    }
    
    setIsSubmitting(true);
    
    try {
      // Get selected card to extract company ID
      const selectedCard = userCards.find(card => card.id.toString() === formData.cardId);
      if (!selectedCard) {
        showNotification('error');
        setIsSubmitting(false);
        return;
      }

      // Step 1: Upload image to OpenKM
      setUploadingImage(true);
      console.log('Uploading image to OpenKM...');
      
      const uploadResponse = await ImageUploadService.uploadInvoiceImage(
        imageFile,
        '/okm:root/Facturas',
        `Factura de ${formData.vendorName.trim() || 'proveedor'} - ${formData.invoiceDate}`
      );
      
      console.log('Image uploaded successfully:', uploadResponse);
      setUploadingImage(false);

      // Step 2: Create invoice with path and fileName from upload response
      const completeInvoiceData: CompleteInvoiceRequest = {
        userId: userId,
        companyId: selectedCard.companyId,
        countryId: parseInt(formData.country),
        cardId: parseInt(formData.cardId),
        path: uploadResponse.path,
        fileName: uploadResponse.fileName,
        vendorName: formData.vendorName.trim(),
        invoiceDate: formData.invoiceDate,
        totalAmount: parseFloat(formData.totalAmount),
        currency: formData.currency,
        concept: formData.concept.trim(),
        categoryId: parseInt(formData.category),
        costCenterId: formData.costCenter ? parseInt(formData.costCenter) : null,
        clientVisited: formData.clientVisited.trim() || "",
        notes: formData.notes.trim() || "",
      };

      console.log('Creating complete invoice with data:', completeInvoiceData);
      const invoiceResponse = await ApiService.createCompleteInvoice(completeInvoiceData);
      console.log('Complete invoice created successfully:', invoiceResponse);

      // Show success notification first
      showNotification('success');
      
      // Close modal dialog immediately
      setShouldShowMainModal(false);
      
      // Delay onClose to keep component mounted while toast is visible (6 seconds)
      setTimeout(() => {
        onClose();
      }, 6500);
      
    } catch (error) {
      console.error('Error saving invoice:', error);
      setUploadingImage(false);
      showNotification('error');
    } finally {
      setIsSubmitting(false);
      setUploadingImage(false);
    }
  };

  return (
    <>
      <Dialog open={isOpen && shouldShowMainModal} onOpenChange={handleCloseModal}>
      <DialogContent className="max-w-6xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-2xl">Detalles de la Factura</DialogTitle>
          <DialogDescription className="text-amber-600 bg-amber-50 p-3 rounded-lg border border-amber-200">
            ⚠️ <strong>Importante:</strong> Por favor, verifique cuidadosamente toda la información extraída antes de enviar. 
            Asegúrese de que los datos del proveedor, fecha, monto y demás campos sean correctos, ya que esta información 
            será registrada permanentemente en el sistema.
          </DialogDescription>
        </DialogHeader>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-4">
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.3 }}
            className="flex flex-col gap-4"
          >
            <div className="border rounded-lg p-4 bg-gray-50">
              <h3 className="text-sm font-semibold mb-3 text-neutral-950">
                Imagen del Documento
              </h3>
              <img
                src={imageUrl}
                alt="Invoice preview"
                className="w-full h-auto rounded-lg shadow-sm object-contain max-h-[500px]"
              />
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.3, delay: 0.1 }}
            className="flex flex-col gap-4"
          >
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="country" className="flex items-center gap-1">
                    País <span className="text-red-500">*</span>
                  </Label>
                  <Select
                    value={formData.country}
                    onValueChange={(value) => {
                      if (value === "clear") {
                        handleInputChange('country', '');
                      } else {
                        handleInputChange('country', value);
                      }
                    }}
                    disabled={loadingCountries}
                  >
                    <SelectTrigger id="country" className={getFieldError('country') ? 'border-red-500' : ''}>
                      <SelectValue placeholder={loadingCountries ? "Cargando países..." : "Selecciona un país"} />
                    </SelectTrigger>
                    <SelectContent>
                      {formData.country && (
                        <SelectItem value="clear" className="text-red-600">
                          ✕ Deseleccionar
                        </SelectItem>
                      )}
                      {countries.length === 0 && !loadingCountries ? (
                        <SelectItem value="none" disabled>
                          No hay países disponibles
                        </SelectItem>
                      ) : (
                        countries.map((country) => (
                          <SelectItem key={country.id} value={country.id.toString()}>
                            {country.name}
                          </SelectItem>
                        ))
                      )}
                    </SelectContent>
                  </Select>
                  {getFieldError('country') && (
                    <p className="text-red-500 text-sm">{getFieldError('country')}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="cardId" className="flex items-center gap-1">
                    Tarjeta <span className="text-red-500">*</span>
                  </Label>
                  <Select
                    value={formData.cardId}
                    onValueChange={(value) => {
                      if (value === "clear") {
                        handleInputChange('cardId', '');
                      } else {
                        handleInputChange('cardId', value);
                      }
                    }}
                  >
                    <SelectTrigger id="cardId" className={getFieldError('cardId') ? 'border-red-500' : ''}>
                      <SelectValue placeholder="Selecciona una tarjeta" />
                    </SelectTrigger>
                    <SelectContent>
                      {formData.cardId && (
                        <SelectItem value="clear" className="text-red-600">
                          ✕ Deseleccionar
                        </SelectItem>
                      )}
                      {userCards.length === 0 ? (
                        <SelectItem value="none" disabled>
                          No hay tarjetas activas disponibles
                        </SelectItem>
                      ) : (
                        userCards.map((card) => (
                          <SelectItem key={card.id} value={card.id.toString()}>
                            {card.maskedCardNumber} - {card.companyName}
                          </SelectItem>
                        ))
                      )}
                    </SelectContent>
                  </Select>
                  {getFieldError('cardId') && (
                    <p className="text-red-500 text-sm">{getFieldError('cardId')}</p>
                  )}
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="vendorName" className="flex items-center gap-1">
                  Nombre del Proveedor <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="vendorName"
                  value={formData.vendorName}
                  onChange={(e) => handleInputChange('vendorName', e.target.value)}
                  placeholder="Ej: Acme Corp"
                  className={getFieldError('vendorName') ? 'border-red-500' : ''}
                />
                {getFieldError('vendorName') && (
                  <p className="text-red-500 text-sm">{getFieldError('vendorName')}</p>
                )}
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="invoiceDate" className="flex items-center gap-1">
                    Fecha de Factura <span className="text-red-500">*</span>
                  </Label>
                  <Input
                    id="invoiceDate"
                    type="date"
                    value={formData.invoiceDate}
                    onChange={(e) => handleInputChange('invoiceDate', e.target.value)}
                    className={getFieldError('invoiceDate') ? 'border-red-500' : ''}
                  />
                  {getFieldError('invoiceDate') && (
                    <p className="text-red-500 text-sm">{getFieldError('invoiceDate')}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="currency" className="flex items-center gap-1">
                    Moneda <span className="text-red-500">*</span>
                  </Label>
                  <Select
                    value={formData.currency}
                    onValueChange={(value) => {
                      if (value === "clear") {
                        handleInputChange('currency', '');
                      } else {
                        handleInputChange('currency', value);
                      }
                    }}
                  >
                    <SelectTrigger id="currency" className={getFieldError('currency') ? 'border-red-500' : ''}>
                      <SelectValue placeholder="Selecciona moneda" />
                    </SelectTrigger>
                    <SelectContent>
                      {formData.currency && (
                        <SelectItem value="clear" className="text-red-600">
                          ✕ Deseleccionar
                        </SelectItem>
                      )}
                      {currencies.map((currency) => (
                        <SelectItem key={currency.value} value={currency.value}>
                          {currency.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {getFieldError('currency') && (
                    <p className="text-red-500 text-sm">{getFieldError('currency')}</p>
                  )}
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="totalAmount" className="flex items-center gap-1">
                  Monto Total <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="totalAmount"
                  type="number"
                  step="0.01"
                  min="0.01"
                  value={formData.totalAmount}
                  onChange={(e) => handleInputChange('totalAmount', e.target.value)}
                  placeholder="0.00"
                  className={getFieldError('totalAmount') ? 'border-red-500' : ''}
                />
                {getFieldError('totalAmount') && (
                  <p className="text-red-500 text-sm">{getFieldError('totalAmount')}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="concept" className="flex items-center gap-1">
                  Concepto <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="concept"
                  value={formData.concept}
                  onChange={(e) => handleInputChange('concept', e.target.value)}
                  placeholder="Breve descripción del gasto"
                  className={getFieldError('concept') ? 'border-red-500' : ''}
                />
                {getFieldError('concept') && (
                  <p className="text-red-500 text-sm">{getFieldError('concept')}</p>
                )}
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="category" className="flex items-center gap-1">
                    Categoría <span className="text-red-500">*</span>
                  </Label>
                  <Select
                    value={formData.category}
                    onValueChange={(value) => {
                      if (value === "clear") {
                        handleInputChange('category', '');
                      } else {
                        handleInputChange('category', value);
                      }
                    }}
                  >
                    <SelectTrigger id="category" className={getFieldError('category') ? 'border-red-500' : ''}>
                      <SelectValue placeholder="Selecciona categoría" />
                    </SelectTrigger>
                    <SelectContent>
                      {formData.category && (
                        <SelectItem value="clear" className="text-red-600">
                          ✕ Deseleccionar
                        </SelectItem>
                      )}
                      {loadingCategories ? (
                        <SelectItem value="loading" disabled>
                          Cargando categorías...
                        </SelectItem>
                      ) : categories.length === 0 ? (
                        <SelectItem value="none" disabled>
                          No hay categorías disponibles
                        </SelectItem>
                      ) : (
                        categories.map((category) => (
                          <SelectItem key={category.id} value={category.id.toString()}>
                            {category.name}
                          </SelectItem>
                        ))
                      )}
                    </SelectContent>
                  </Select>
                  {getFieldError('category') && (
                    <p className="text-red-500 text-sm">{getFieldError('category')}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="costCenter">Centro de Costo (Opcional)</Label>
                  <Select
                    value={formData.costCenter}
                    onValueChange={(value) => {
                      if (value === "clear") {
                        handleInputChange('costCenter', '');
                      } else {
                        handleInputChange('costCenter', value);
                      }
                    }}
                  >
                    <SelectTrigger id="costCenter">
                      <SelectValue placeholder="Selecciona centro (opcional)" />
                    </SelectTrigger>
                    <SelectContent>
                      {formData.costCenter && (
                        <SelectItem value="clear" className="text-red-600">
                          ✕ Deseleccionar
                        </SelectItem>
                      )}
                      {loadingCostCenters ? (
                        <SelectItem value="loading" disabled>
                          Cargando centros de costo...
                        </SelectItem>
                      ) : costCenters.length === 0 ? (
                        <SelectItem value="none" disabled>
                          No hay centros de costo disponibles
                        </SelectItem>
                      ) : (
                        costCenters.map((center) => (
                          <SelectItem key={center.id} value={center.id.toString()}>
                            {center.name}
                          </SelectItem>
                        ))
                      )}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="clientVisited">Cliente Visitado (Opcional)</Label>
                <Input
                  id="clientVisited"
                  value={formData.clientVisited}
                  onChange={(e) => handleInputChange('clientVisited', e.target.value)}
                  placeholder="Nombre del cliente"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="notes">Notas Adicionales (Opcional)</Label>
                <Textarea
                  id="notes"
                  value={formData.notes}
                  onChange={(e) => handleInputChange('notes', e.target.value)}
                  placeholder="Añade cualquier información adicional..."
                  rows={3}
                />
              </div>

              <div className="flex gap-3 pt-4">
                <Button
                  onClick={handleSubmit}
                  disabled={isSubmitting}
                  className="flex-1 bg-[#f23030] hover:bg-[#d92828] text-white disabled:opacity-50"
                >
                  {uploadingImage ? (
                    <>
                      <span className="animate-spin mr-2">⏳</span>
                      Subiendo imagen...
                    </>
                  ) : isSubmitting ? (
                    <>
                      <span className="animate-spin mr-2">⏳</span>
                      Guardando factura...
                    </>
                  ) : (
                    'Guardar Factura'
                  )}
                </Button>
                <Button
                  onClick={handleCloseModal}
                  variant="outline"
                  className="flex-1"
                  disabled={isSubmitting}
                >
                  Cancelar
                </Button>
              </div>
            </div>
          </motion.div>
        </div>
      </DialogContent>
    </Dialog>

      {/* Notificación Toast */}
      <ResultModal
        isOpen={notificationState.isOpen}
        onClose={hideNotification}
        type={notificationState.type}
      />
    </>
  );
};
