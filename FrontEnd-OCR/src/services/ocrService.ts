import { OcrResponse, OcrServiceResult } from '../types/api';

const API_BASE_URL = '/api';

export class OcrService {
  /**
   * Analyze an image file and extract invoice data using OCR
   * @param file - Image file to analyze
   * @returns Promise<OcrServiceResult> - Processing result with invoice data or error
   */
  static async analyzeImage(file: File): Promise<OcrServiceResult> {
    try {
      console.log('Enviando archivo:', file.name, 'Tipo:', file.type, 'Tamaño:', file.size);

      const response = await fetch(`${API_BASE_URL}/ocr`, {
        method: 'POST',
        body: file,
        headers: {
          'Content-Type': file.type,
        },
      });

      console.log('Respuesta del servidor:', response.status, response.statusText);

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Error del servidor:', errorText);
        return {
          success: false,
          error: `Error del servidor (${response.status}): ${response.statusText}`
        };
      }

      const result: OcrResponse = await response.json();
      console.log('Resultado del OCR:', result);
      
      // Validar la respuesta según el status
      if (result.status === 'ok' || result.status === 'success') {
        if (result.invoice_data) {
          return {
            success: true,
            data: result.invoice_data,
            ocrText: result.ocr_text,
            processingTime: result.processing_time_ms
          };
        } else {
          return {
            success: false,
            error: 'No se pudieron extraer los datos de la factura. Los datos están incompletos.'
          };
        }
      } else {
        // Status diferente de 'ok' o 'success'
        const errorMessage = result.error_message || 'No es posible capturar los datos de la factura';
        return {
          success: false,
          error: `${errorMessage}. Por favor, intente de nuevo con una imagen más clara.`
        };
      }
    } catch (error) {
      console.error('Error en OCR:', error);
      return {
        success: false,
        error: 'Error de conexión. No es posible procesar la imagen en este momento. Por favor, intente de nuevo.'
      };
    }
  }
}