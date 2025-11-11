import { ImageUploadRequest, ImageUploadResponse } from '../types/api';

// Use relative URL to leverage Vite proxy and avoid CORS issues
// This will be proxied to http://localhost:8082/api/images/upload
const IMAGE_UPLOAD_BASE_URL = '/api/images/upload';
const DOCUMENT_DOWNLOAD_BASE_URL = '/api/documents';

export class ImageUploadService {
  /**
   * Convert a File object to Base64 string
   * @param file - The file to convert
   * @returns Promise<string> - Base64 string without data URI prefix
   */
  static async fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      
      reader.onload = () => {
        const base64String = reader.result as string;
        // Remove the data URI prefix (e.g., "data:image/jpeg;base64,")
        const base64Data = base64String.split(',')[1];
        resolve(base64Data);
      };
      
      reader.onerror = (error) => {
        reject(new Error(`Error reading file: ${error}`));
      };
      
      reader.readAsDataURL(file);
    });
  }

  /**
   * Generate a unique filename for invoice
   * @param originalFileName - Original file name
   * @returns string - Generated filename with timestamp
   */
  static generateInvoiceFileName(originalFileName: string): string {
    const timestamp = new Date().getTime();
    const extension = originalFileName.split('.').pop() || 'jpg';
    return `factura_${timestamp}.${extension}`;
  }

  /**
   * Upload image to OpenKM via Quarkus backend
   * @param file - The image file to upload
   * @param destinationPath - The destination path in OpenKM (default: /okm:root/Facturas)
   * @param description - Optional description for the image
   * @returns Promise<ImageUploadResponse> - Upload response with path and fileName
   */
  static async uploadInvoiceImage(
    file: File,
    destinationPath: string = '/okm:root/Facturas',
    description?: string
  ): Promise<ImageUploadResponse> {
    try {
      // Convert file to Base64
      const imageData = await this.fileToBase64(file);
      
      // Generate unique filename
      const fileName = this.generateInvoiceFileName(file.name);
      
      // Determine MIME type
      const mimeType = file.type || 'image/jpeg';
      
      // Prepare request payload
      const uploadRequest: ImageUploadRequest = {
        fileName,
        destinationPath,
        imageData,
        description: description || `Factura - ${fileName}`,
        mimeType,
      };

      console.log('Uploading image to OpenKM:', {
        fileName,
        destinationPath,
        mimeType,
        size: file.size,
      });

      // Send request to Quarkus backend
      // IMAGE_UPLOAD_BASE_URL is already '/api/images/upload', so we just add '/json'
      const response = await fetch(`${IMAGE_UPLOAD_BASE_URL}/json`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(uploadRequest),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(
          `Error al subir imagen: ${response.status} ${response.statusText} - ${errorText}`
        );
      }

      const uploadResponse: ImageUploadResponse = await response.json();
      
      if (!uploadResponse.success) {
        throw new Error(
          uploadResponse.message || 'Error desconocido al subir la imagen'
        );
      }

      console.log('Image uploaded successfully:', uploadResponse);
      return uploadResponse;
      
    } catch (error) {
      console.error('Error uploading image:', error);
      throw error;
    }
  }

  /**
   * Validate if file is an image
   * @param file - The file to validate
   * @returns boolean - True if file is an image
   */
  static isValidImageFile(file: File): boolean {
    const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    return validTypes.includes(file.type);
  }

  /**
   * Validate file size (max 10MB)
   * @param file - The file to validate
   * @param maxSizeMB - Maximum size in MB (default: 10)
   * @returns boolean - True if file size is valid
   */
  static isValidFileSize(file: File, maxSizeMB: number = 10): boolean {
    const maxSizeBytes = maxSizeMB * 1024 * 1024;
    return file.size <= maxSizeBytes;
  }

  /**
   * Download image from OpenKM via Quarkus backend
   * @param path - The path of the document in OpenKM (e.g., /okm:root/Facturas/factura_123.jpg)
   * @returns Promise<string> - Data URL of the downloaded image
   */
  static async downloadInvoiceImage(path: string): Promise<string> {
    try {
      console.log('Downloading image from OpenKM:', path);

      // Encode the path for URL query parameter
      const encodedPath = encodeURIComponent(path);
      
      // Send request to Quarkus backend
      const response = await fetch(`${DOCUMENT_DOWNLOAD_BASE_URL}/download?path=${encodedPath}`, {
        method: 'GET',
        headers: {
          'Accept': 'application/octet-stream',
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(
          `Error al descargar imagen: ${response.status} ${response.statusText} - ${errorText}`
        );
      }

      // Convert response to blob
      const blob = await response.blob();
      
      // Convert blob to data URL
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onloadend = () => {
          resolve(reader.result as string);
        };
        reader.onerror = () => {
          reject(new Error('Error converting image to data URL'));
        };
        reader.readAsDataURL(blob);
      });
      
    } catch (error) {
      console.error('Error downloading image:', error);
      throw error;
    }
  }
}
