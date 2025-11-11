import React, { useState, useEffect, useRef } from 'react';
import { motion } from 'framer-motion';
import { ApiService } from '../services/apiService';
import { ImageUploadService } from '../services/imageUploadService';
import { Country, Category, CostCenter, Expense, UpdateInvoiceRequest } from '../types/api';
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

interface EditInvoiceFormData {
  country: string;
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

interface EditInvoiceModalProps {
  isOpen: boolean;
  onClose: () => void;
  expenseData: Expense | null;
  onSuccess?: () => void;
  viewOnly?: boolean; // Modo solo visualización
}

const currencies = [
  { value: 'USD', label: 'USD - Dólar' },
];

export const EditInvoiceModal: React.FC<EditInvoiceModalProps> = ({
  isOpen,
  onClose,
  expenseData,
  onSuccess,
  viewOnly = false, // Por defecto es editable
}) => {
  const [countries, setCountries] = useState<Country[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [costCenters, setCostCenters] = useState<CostCenter[]>([]);
  const [loadingCountries, setLoadingCountries] = useState(false);
  const [loadingCategories, setLoadingCategories] = useState(false);
  const [loadingCostCenters, setLoadingCostCenters] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<FieldError[]>([]);
  const [shouldShowMainModal, setShouldShowMainModal] = useState(true);
  const [imageUrl, setImageUrl] = useState<string>('');
  const [loadingImage, setLoadingImage] = useState(false);
  const [imageError, setImageError] = useState<string>('');
  const fetchingDataRef = useRef(false);
  
  // Hook de notificación
  const { notificationState, showNotification, hideNotification } = useNotification();
  
  const [formData, setFormData] = useState<EditInvoiceFormData>({
    country: '',
    vendorName: '',
    invoiceDate: '',
    totalAmount: '',
    currency: 'USD',
    concept: '',
    category: '',
    costCenter: '',
    clientVisited: '',
    notes: '',
  });

  // Reset modal state when opening
  useEffect(() => {
    if (isOpen) {
      setShouldShowMainModal(true);
      setFieldErrors([]);
    }
  }, [isOpen]);

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

  // Pre-populate form when expenseData changes
  useEffect(() => {
    if (expenseData && isOpen) {
      setFormData({
        country: expenseData.countryId != null ? expenseData.countryId.toString() : '',
        vendorName: expenseData.vendorName || '',
        invoiceDate: expenseData.invoiceDate || '',
        totalAmount: expenseData.totalAmount?.toString() || '',
        currency: expenseData.currency || 'USD',
        concept: expenseData.concept || '',
        category: expenseData.categoryId?.toString() || '',
        costCenter: expenseData.costCenterId?.toString() || '',
        clientVisited: expenseData.clientVisited || '',
        notes: expenseData.notes || '',
      });

      // Load invoice image from OpenKM
      const loadImage = async () => {
        if (expenseData.path) {
          setLoadingImage(true);
          setImageError('');
          setImageUrl(''); // Clear previous image
          try {
            const dataUrl = await ImageUploadService.downloadInvoiceImage(expenseData.path);
            setImageUrl(dataUrl);
          } catch (error) {
            console.error('Error loading invoice image:', error);
            setImageError('No se pudo cargar la imagen de la factura');
            setImageUrl(''); // Ensure imageUrl is empty on error
          } finally {
            setLoadingImage(false);
          }
        } else {
          setImageUrl('');
          setLoadingImage(false);
          setImageError('No hay imagen disponible');
        }
      };

      loadImage();
    }
  }, [expenseData, isOpen]);

  const handleInputChange = (
    field: keyof EditInvoiceFormData,
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
    if (!formData.vendorName?.trim()) {
      errors.push({ field: 'vendorName', message: 'El nombre del proveedor es obligatorio' });
    }
    if (!formData.invoiceDate) {
      errors.push({ field: 'invoiceDate', message: 'La fecha de la factura es obligatoria' });
    } else {
      // Validar que la fecha pertenezca al mes y año actual
      const invoiceDate = new Date(`${formData.invoiceDate}T00:00:00`); // Usar T00:00:00 para evitar problemas de zona horaria
      const currentDate = new Date();
      if (invoiceDate.getFullYear() !== currentDate.getFullYear() || invoiceDate.getMonth() !== currentDate.getMonth()) {
        errors.push({ field: 'invoiceDate', message: 'La fecha debe pertenecer al mes y año actual.' });
      }
    }
    if (!formData.totalAmount?.trim()) {
      errors.push({ field: 'totalAmount', message: 'El monto total es obligatorio' });
    } else {
      const amount = parseFloat(formData.totalAmount);
      if (isNaN(amount) || amount <= 0) {
        errors.push({ field: 'totalAmount', message: 'El monto debe ser un número mayor a 0' });
      }
    }
    if (!formData.currency) {
      errors.push({ field: 'currency', message: 'La moneda es obligatoria' });
    }
    if (!formData.concept?.trim()) {
      errors.push({ field: 'concept', message: 'El concepto es obligatorio' });
    }
    if (!formData.category) {
      errors.push({ field: 'category', message: 'La categoría es obligatoria' });
    }

    setFieldErrors(errors);
    return errors.length === 0;
  };

  const getFieldError = (field: string): string | undefined => {
    return fieldErrors.find(error => error.field === field)?.message;
  };

  const handleSubmit = async () => {
    if (isSubmitting) return;
    
    // Validate form
    if (!validateForm()) {
      return;
    }
    
    if (!expenseData) {
      showNotification('error');
      return;
    }
    
    setIsSubmitting(true);
    
    try {
      // Prepare update data
      const updateData: UpdateInvoiceRequest = {
        idInvoice: expenseData.idInvoice,
        id: expenseData.id,
        countryId: parseInt(formData.country),
        vendorName: formData.vendorName,
        invoiceDate: formData.invoiceDate,
        totalAmount: parseFloat(formData.totalAmount),
        currency: formData.currency,
        concept: formData.concept,
        categoryId: parseInt(formData.category),
        costCenterId: formData.costCenter ? parseInt(formData.costCenter) : null,
        clientVisited: formData.clientVisited,
        notes: formData.notes,
      };

      // Call update API
      await ApiService.updateCompleteInvoice(updateData);
      
      // Show success notification and close modal
      setShouldShowMainModal(false);
      onClose();
      showNotification('success');
      
      // Call onSuccess callback if provided
      if (onSuccess) {
        onSuccess();
      }
    } catch (error) {
      console.error('Error updating invoice:', error);
      showNotification('error');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    if (!isSubmitting) {
      setFieldErrors([]);
      setShouldShowMainModal(true);
      onClose();
    }
  };

  return (
    <>
      <Dialog open={isOpen && shouldShowMainModal} onOpenChange={handleClose}>
        <DialogContent className="max-w-6xl max-h-[95vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle className="text-2xl text-neutral-950">
              {viewOnly ? 'Visualizar Factura' : 'Editar Factura'}
            </DialogTitle>
            <DialogDescription className="text-base">
              {viewOnly ? (
                <>
                  ℹ️ <strong>Información:</strong> Esta factura no puede ser modificada.
                </>
              ) : (
                <>
                  ⚠️ <strong>Importante:</strong> Revisa cuidadosamente la información antes de guardar los cambios.
                </>
              )}
            </DialogDescription>
          </DialogHeader>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-4">
            {/* Image Preview Column - Show if loading, has image, or has error with path */}
            {(loadingImage || imageUrl || (expenseData?.path && imageError)) && (
              <motion.div
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.3 }}
                className="lg:col-span-1"
              >
                <div className="border rounded-lg p-4 bg-gray-50 sticky top-4">
                  <h3 className="text-sm font-semibold mb-3 text-neutral-950">
                    Imagen de la Factura
                  </h3>
                  <div className="relative w-full aspect-[3/4] bg-gray-200 rounded-lg overflow-hidden">
                    {loadingImage ? (
                      <div className="absolute inset-0 flex items-center justify-center bg-gradient-to-br from-gray-100 to-gray-200">
                        <div className="text-center">
                          {/* Animated circles loader */}
                          <div className="relative w-20 h-20 mx-auto mb-4">
                            <div className="absolute top-0 left-0 w-full h-full">
                              <div className="w-20 h-20 border-4 border-gray-300 border-t-[#f23030] rounded-full animate-spin"></div>
                            </div>
                            <div className="absolute top-0 left-0 w-full h-full flex items-center justify-center">
                              <div className="text-2xl">📄</div>
                            </div>
                          </div>
                          
                          {/* Loading text with pulse animation */}
                          <div className="space-y-2">
                            <p className="text-sm font-medium text-gray-700 animate-pulse">
                              Cargando imagen...
                            </p>
                            <div className="flex justify-center gap-1">
                              <span className="w-2 h-2 bg-[#f23030] rounded-full animate-bounce" style={{ animationDelay: '0ms' }}></span>
                              <span className="w-2 h-2 bg-[#f23030] rounded-full animate-bounce" style={{ animationDelay: '150ms' }}></span>
                              <span className="w-2 h-2 bg-[#f23030] rounded-full animate-bounce" style={{ animationDelay: '300ms' }}></span>
                            </div>
                          </div>
                        </div>
                      </div>
                    ) : imageError ? (
                      <div className="absolute inset-0 flex items-center justify-center p-4">
                        <div className="text-center">
                          <span className="text-4xl">📄</span>
                          <p className="mt-2 text-sm text-gray-600">{imageError}</p>
                        </div>
                      </div>
                    ) : (
                      <motion.img
                        src={imageUrl}
                        alt="Factura"
                        className="w-full h-full object-contain"
                        initial={{ opacity: 0, scale: 0.9 }}
                        animate={{ opacity: 1, scale: 1 }}
                        transition={{ duration: 0.3 }}
                      />
                    )}
                  </div>
                </div>
              </motion.div>
            )}

            {/* Form Columns - Adjust grid based on image presence */}
            <div className={`${(loadingImage || imageUrl || (expenseData?.path && imageError)) ? 'lg:col-span-2' : 'lg:col-span-3'} grid grid-cols-1 lg:grid-cols-2 gap-6`}>
              {/* Left Column - Form Fields */}
              <motion.div
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.3 }}
                className="flex flex-col gap-4"
              >
              {/* Invoice Information Section */}
              <div className="border rounded-lg p-4 bg-gray-50">
                <h3 className="text-sm font-semibold mb-3 text-neutral-950">
                  Información de la Factura
                </h3>
                
                <div className="grid grid-cols-1 gap-4">
                  {/* Country */}
                  <div>
                    <Label htmlFor="country" className="flex items-center gap-1">
                      País <span className="text-red-500">*</span>
                    </Label>
                    <Select
                      value={formData.country}
                      onValueChange={(value) => handleInputChange('country', value)}
                      disabled={loadingCountries || viewOnly}
                    >
                      <SelectTrigger id="country" className={getFieldError('country') ? 'border-red-500' : ''}>
                        <SelectValue placeholder="Selecciona un país" />
                      </SelectTrigger>
                      <SelectContent>
                        {loadingCountries ? (
                          <SelectItem value="loading" disabled>
                            Cargando países...
                          </SelectItem>
                        ) : countries.length === 0 ? (
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
                      <p className="text-red-500 text-xs mt-1">{getFieldError('country')}</p>
                    )}
                  </div>

                  {/* Vendor Name */}
                  <div>
                    <Label htmlFor="vendorName" className="flex items-center gap-1">
                      Proveedor <span className="text-red-500">*</span>
                    </Label>
                    <Input
                      id="vendorName"
                      value={formData.vendorName}
                      onChange={(e) => handleInputChange('vendorName', e.target.value)}
                      placeholder="Nombre del proveedor"
                      className={getFieldError('vendorName') ? 'border-red-500' : ''}
                      disabled={viewOnly}
                    />
                    {getFieldError('vendorName') && (
                      <p className="text-red-500 text-xs mt-1">{getFieldError('vendorName')}</p>
                    )}
                  </div>

                  {/* Invoice Date */}
                  <div>
                    <Label htmlFor="invoiceDate" className="flex items-center gap-1">
                      Fecha de Factura <span className="text-red-500">*</span>
                    </Label>
                    <Input
                      id="invoiceDate"
                      type="date"
                      value={formData.invoiceDate}
                      onChange={(e) => handleInputChange('invoiceDate', e.target.value)}
                      className={getFieldError('invoiceDate') ? 'border-red-500' : ''}
                      disabled={viewOnly}
                    />
                    {getFieldError('invoiceDate') && (
                      <p className="text-red-500 text-xs mt-1">{getFieldError('invoiceDate')}</p>
                    )}
                  </div>

                  {/* Total Amount and Currency */}
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <Label htmlFor="totalAmount" className="flex items-center gap-1">
                        Monto Total <span className="text-red-500">*</span>
                      </Label>
                      <Input
                        id="totalAmount"
                        type="number"
                        step="0.01"
                        value={formData.totalAmount}
                        onChange={(e) => handleInputChange('totalAmount', e.target.value)}
                        placeholder="0.00"
                        className={getFieldError('totalAmount') ? 'border-red-500' : ''}
                        disabled={viewOnly}
                      />
                      {getFieldError('totalAmount') && (
                        <p className="text-red-500 text-xs mt-1">{getFieldError('totalAmount')}</p>
                      )}
                    </div>

                    <div>
                      <Label htmlFor="currency" className="flex items-center gap-1">
                        Moneda <span className="text-red-500">*</span>
                      </Label>
                      <Select
                        value={formData.currency}
                        onValueChange={(value) => handleInputChange('currency', value)}
                        disabled={viewOnly}
                      >
                        <SelectTrigger id="currency" className={getFieldError('currency') ? 'border-red-500' : ''}>
                          <SelectValue placeholder="Moneda" />
                        </SelectTrigger>
                        <SelectContent>
                          {currencies.map((curr) => (
                            <SelectItem key={curr.value} value={curr.value}>
                              {curr.label}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                      {getFieldError('currency') && (
                        <p className="text-red-500 text-xs mt-1">{getFieldError('currency')}</p>
                      )}
                    </div>
                  </div>

                  {/* Concept */}
                  <div>
                    <Label htmlFor="concept" className="flex items-center gap-1">
                      Concepto <span className="text-red-500">*</span>
                    </Label>
                    <Input
                      id="concept"
                      value={formData.concept}
                      onChange={(e) => handleInputChange('concept', e.target.value)}
                      placeholder="Ej: Compra de suministros"
                      className={getFieldError('concept') ? 'border-red-500' : ''}
                      disabled={viewOnly}
                    />
                    {getFieldError('concept') && (
                      <p className="text-red-500 text-xs mt-1">{getFieldError('concept')}</p>
                    )}
                  </div>
                </div>
              </div>
            </motion.div>

            {/* Right Column - Additional Details */}
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.3, delay: 0.1 }}
              className="flex flex-col gap-4"
            >
              {/* Classification Section */}
              <div className="border rounded-lg p-4 bg-gray-50">
                <h3 className="text-sm font-semibold mb-3 text-neutral-950">
                  Clasificación
                </h3>
                
                <div className="grid grid-cols-1 gap-4">
                  {/* Category */}
                  <div>
                    <Label htmlFor="category" className="flex items-center gap-1">
                      Categoría <span className="text-red-500">*</span>
                    </Label>
                    <Select
                      value={formData.category}
                      onValueChange={(value) => handleInputChange('category', value)}
                      disabled={loadingCategories || viewOnly}
                    >
                      <SelectTrigger id="category" className={getFieldError('category') ? 'border-red-500' : ''}>
                        <SelectValue placeholder="Selecciona una categoría" />
                      </SelectTrigger>
                      <SelectContent>
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
                      <p className="text-red-500 text-xs mt-1">{getFieldError('category')}</p>
                    )}
                  </div>

                  {/* Cost Center */}
                  <div>
                    <Label htmlFor="costCenter">
                      Centro de Costo (Opcional)
                    </Label>
                    <Select
                      value={formData.costCenter}
                      onValueChange={(value) => {
                        if (value === "clear") {
                          handleInputChange('costCenter', '');
                        } else {
                          handleInputChange('costCenter', value);
                        }
                      }}
                      disabled={loadingCostCenters || viewOnly}
                    >
                      <SelectTrigger id="costCenter">
                        <SelectValue placeholder="Selecciona un centro de costo" />
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
                              {center.code} - {center.name}
                            </SelectItem>
                          ))
                        )}
                      </SelectContent>
                    </Select>
                  </div>

                  {/* Client Visited */}
                  <div>
                    <Label htmlFor="clientVisited">
                      Cliente Visitado (Opcional)
                    </Label>
                    <Input
                      id="clientVisited"
                      value={formData.clientVisited}
                      onChange={(e) => handleInputChange('clientVisited', e.target.value)}
                      placeholder="Nombre del cliente visitado"
                      disabled={viewOnly}
                    />
                  </div>

                  {/* Notes */}
                  <div>
                    <Label htmlFor="notes">
                      Notas Adicionales (Opcional)
                    </Label>
                    <Textarea
                      id="notes"
                      value={formData.notes}
                      onChange={(e) => handleInputChange('notes', e.target.value)}
                      placeholder="Agrega cualquier información adicional relevante..."
                      rows={4}
                      className="resize-none"
                      disabled={viewOnly}
                    />
                  </div>
                </div>
              </div>
            </motion.div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex flex-col-reverse sm:flex-row justify-end gap-3 mt-6 pt-4 border-t">
            <Button
              type="button"
              variant="outline"
              onClick={handleClose}
              disabled={isSubmitting}
              className="w-full sm:w-auto"
            >
              {viewOnly ? 'Cerrar' : 'Cancelar'}
            </Button>
            {!viewOnly && (
              <Button
                type="button"
                onClick={handleSubmit}
                disabled={isSubmitting}
                className="w-full sm:w-auto bg-[#f23030] hover:bg-[#d92828] text-white"
              >
                {isSubmitting ? (
                  <>
                    <span className="animate-spin mr-2">⏳</span>
                    Guardando cambios...
                  </>
                ) : (
                  'Guardar Cambios'
                )}
              </Button>
            )}
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
