import { useState } from 'react';
import { OcrService } from '../services/ocrService';
import { InvoiceData } from '../types/api';

interface ProcessedImageData {
  success: boolean;
  data: InvoiceData;
  ocrText?: string;
  processingTime?: number;
  imageUrl?: string;
  originalFile?: File; // Store original file for upload
}

export const useImageUpload = () => {
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [processedData, setProcessedData] = useState<ProcessedImageData | null>(null);

  const processImage = async (file: File): Promise<boolean> => {
    setProcessing(true);
    setError(null);
    setProcessedData(null);

    try {
      // Create object URL for image preview
      const imageUrl = URL.createObjectURL(file);
      
      const result = await OcrService.analyzeImage(file);

      if (result.success && result.data) {
        setProcessedData({
          success: true,
          data: result.data,
          ocrText: result.ocrText,
          processingTime: result.processingTime,
          imageUrl: imageUrl,
          originalFile: file, // Store original file
        });
        return true;
      } else {
        // Clean up object URL if processing failed
        URL.revokeObjectURL(imageUrl);
        setError(result.error || 'No es posible capturar los datos de la factura. Por favor, intente de nuevo.');
        return false;
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Error inesperado al procesar la imagen.';
      setError(errorMessage);
      return false;
    } finally {
      setProcessing(false);
    }
  };

  const reset = () => {
    // Clean up object URL when resetting
    if (processedData?.imageUrl) {
      URL.revokeObjectURL(processedData.imageUrl);
    }
    setProcessing(false);
    setError(null);
    setProcessedData(null);
  };

  return {
    processing,
    error,
    processedData,
    processImage,
    reset,
  };
};
