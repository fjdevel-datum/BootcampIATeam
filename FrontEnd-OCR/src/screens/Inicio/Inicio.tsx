import React, { useRef, useState, useCallback, useEffect } from "react";
import { AnimatePresence } from "framer-motion";
import { Button } from "../../components/ui/button";
import { ScanIcon, UploadIcon, AlertCircle } from "lucide-react";
import { UserCard } from "../../components/UserCard";
import { LoadingAnimation } from "../../components/LoadingAnimation";
import { InvoiceFormModal } from "../../components/InvoiceFormModal";
import { FileWarningModal } from "../../components/FileWarningModal";
import { useImageUpload } from "../../hooks/useImageUpload";
import { useUserData } from "../../hooks/useUserData";

const actionButtons = [
  {
    id: "scan",
    icon: ScanIcon,
    title: "Escanear documento",
    subtitle: "Usa la cámara para capturar",
    variant: "default" as const,
    className: "bg-[#f23030] hover:bg-[#d92828] text-white border-none"
  },
  {
    id: "upload",
    icon: UploadIcon,
    title: "Subir archivo",
    subtitle: "Selecciona desde tu dispositivo",
    variant: "outline" as const,
    className: "bg-white hover:bg-gray-50 text-neutral-950 border-[0.8px] border-[#0000001a]"
  }
];

export const Inicio = (): JSX.Element => {
  const ScanIconComponent = actionButtons[0].icon;
  const UploadIconComponent = actionButtons[1].icon;
  
  // For now, using userId = 1. In a real app, this would come from authentication
  const userId = 1;
  const { user, cards, loading: userLoading, error: userError } = useUserData(userId);

  const fileInputRef = useRef<HTMLInputElement>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isWarningModalOpen, setIsWarningModalOpen] = useState(false);
  const [invalidFile, setInvalidFile] = useState<{ name: string; type: string } | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const { processing, error, processedData, processImage, reset } = useImageUpload();

  // Prevenir recarga de página
  useEffect(() => {
    const preventUnload = (e: BeforeUnloadEvent) => {
      if (isProcessing) {
        e.preventDefault();
        e.returnValue = '';
      }
    };

    window.addEventListener('beforeunload', preventUnload);
    return () => window.removeEventListener('beforeunload', preventUnload);
  }, [isProcessing]);

  const handleUploadClick = useCallback((e?: React.MouseEvent) => {
    e?.preventDefault();
    e?.stopPropagation();
    // Asegurarse de que el atributo 'capture' no esté presente para abrir el selector de archivos
    fileInputRef.current?.removeAttribute('capture');
    fileInputRef.current?.click();
  }, []);

  const handleScanClick = useCallback((e?: React.MouseEvent) => {
    e?.preventDefault();
    e?.stopPropagation();
    // Añadir el atributo 'capture' para abrir la cámara directamente
    fileInputRef.current?.setAttribute('capture', 'environment');
    fileInputRef.current?.click();
  }, []);

  const handleFileChange = useCallback(async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];

    if (!file) return;

    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
    if (!allowedTypes.includes(file.type)) {
      setInvalidFile({ name: file.name, type: file.type });
      setIsWarningModalOpen(true);
      // Limpiar el input
      if (fileInputRef.current) fileInputRef.current.value = '';
      return;
    }

    const maxSize = 10 * 1024 * 1024;
    if (file.size > maxSize) {
      alert('El archivo es demasiado grande. El tamaño máximo es 10MB');
      if (fileInputRef.current) fileInputRef.current.value = '';
      return;
    }

    setIsProcessing(true);
    const success = await processImage(file);
    setIsProcessing(false);

    if (success) {
      setIsModalOpen(true);
    }

    // Limpiar el valor del input para permitir seleccionar el mismo archivo de nuevo
    if (fileInputRef.current) fileInputRef.current.value = '';
  }, [processImage]);

  const handleCloseModal = () => {
    setIsModalOpen(false);
    reset();
  };

  const handleCloseWarningModal = () => {
    setIsWarningModalOpen(false);
    setInvalidFile(null);
  };

  return (
    <div className="bg-white w-full min-w-[402px] min-h-[874px] flex flex-col items-center justify-between py-5">
      <div className="w-[173px] h-[95px] [background:url(../image--datum-el-salvador-.png)_50%_50%_/_cover]" />

      <div className="w-full max-w-[359px] px-6 mb-6">
        {userLoading ? (
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 w-full">
            <div className="animate-pulse">
              <div className="flex items-center gap-4 mb-6">
                <div className="w-14 h-14 rounded-full bg-gray-200"></div>
                <div className="flex flex-col space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-32"></div>
                  <div className="h-3 bg-gray-200 rounded w-48"></div>
                </div>
              </div>
              <div className="mb-3">
                <div className="h-3 bg-gray-200 rounded w-24"></div>
              </div>
              <div className="h-16 bg-gray-200 rounded"></div>
            </div>
          </div>
        ) : userError ? (
          <div className="bg-white rounded-2xl shadow-sm border border-red-200 p-6 w-full">
            <div className="flex items-center gap-3 text-red-600">
              <AlertCircle className="w-5 h-5" />
              <div>
                <p className="font-semibold">Error al cargar datos del usuario</p>
                <p className="text-sm">{userError}</p>
              </div>
            </div>
          </div>
        ) : user ? (
          <UserCard
            userName={user.name}
            userEmail={user.email}
            cards={cards}
            onCardSelect={(cardId) => console.log('Selected card:', cardId)}
          />
        ) : null}
      </div>

      <div className="flex flex-col items-center gap-4 w-full max-w-[359px] px-6">
        <h2 className="[font-family:'Arimo',Helvetica] font-normal text-[#717182] text-base text-center tracking-[0] leading-6 mb-2">
          Procesa tus facturas rápidamente. Captura o sube las facturas a una buena calidad.
        </h2>
        
        {/* Botón Escanear - Solo visible en móvil */}
        <Button
          variant={actionButtons[0].variant}
          className={`${actionButtons[0].className} h-14 w-full rounded-lg flex items-center justify-center gap-3 sm:hidden`}
          onClick={handleScanClick}
          type="button"
        >
          <ScanIconComponent className="w-4 h-4" />
          <div className="flex flex-col items-start">
            <span className="[font-family:'Arimo',Helvetica] font-normal text-sm tracking-[0] leading-5">
              {actionButtons[0].title}
            </span>
            <span className="[font-family:'Arimo',Helvetica] font-normal text-xs tracking-[0] leading-4 opacity-80">
              {actionButtons[0].subtitle}
            </span>
          </div>
        </Button>
        
        {/* Separador - Solo visible en móvil cuando ambos botones son visibles */}
        <div className="[font-family:'Arimo',Helvetica] font-normal text-[#717182] text-sm text-center tracking-[0] leading-5 sm:hidden">
          - O -
        </div>
        
        {/* Botón Subir archivo - Visible en todas las pantallas */}
        <Button
          variant={actionButtons[1].variant}
          className={`${actionButtons[1].className} h-14 w-full rounded-lg flex items-center justify-center gap-3`}
          onClick={handleUploadClick}
          type="button"
        >
          <UploadIconComponent className="w-4 h-4" />
          <div className="flex flex-col items-start">
            <span className="[font-family:'Arimo',Helvetica] font-normal text-sm tracking-[0] leading-5">
              {actionButtons[1].title}
            </span>
            <span className="[font-family:'Arimo',Helvetica] font-normal text-[#717182] text-xs tracking-[0] leading-4">
              {actionButtons[1].subtitle}
            </span>
          </div>
        </Button>

        {error && (
          <div className="w-full mt-4 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
            <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
            <div className="flex-1">
              <p className="text-sm font-semibold text-red-900">Error al procesar la imagen</p>
              <p className="text-sm text-red-700 mt-1">{error}</p>
            </div>
          </div>
        )}
      </div>

      {/* Input único para subir archivo y escanear con cámara */}
      <input
        ref={fileInputRef}
        type="file"
        accept="image/jpeg,image/jpg,image/png"
        onChange={handleFileChange}
        className="hidden"
      />

      <AnimatePresence>
        {processing && <LoadingAnimation message="Procesando imagen..." />}
      </AnimatePresence>

      {processedData && (
        <InvoiceFormModal
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          imageUrl={processedData.imageUrl || ""}
          imageFile={processedData.originalFile}
          extractedData={{
            vendorName: processedData.data.vendorName,
            invoiceDate: processedData.data.invoiceDate,
            totalAmount: processedData.data.totalAmount,
            currency: processedData.data.currency,
            concept: "", // Estos campos no están en la nueva estructura del OCR
            category: "",
            costCenter: "",
          }}
          userCards={cards.filter(card => card.status === 'ACTIVE')}
          userId={userId}
        />
      )}

      <FileWarningModal
        isOpen={isWarningModalOpen}
        onClose={handleCloseWarningModal}
        fileName={invalidFile?.name}
        fileType={invalidFile?.type}
      />

      <div className="h-[100px]" />
    </div>
  );
};
