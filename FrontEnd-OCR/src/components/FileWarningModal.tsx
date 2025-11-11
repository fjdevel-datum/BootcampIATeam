import React from 'react';
import { AlertTriangle, X } from 'lucide-react';
import { Button } from './ui/button';
import { Dialog } from './ui/dialog';

interface FileWarningModalProps {
  isOpen: boolean;
  onClose: () => void;
  fileName?: string;
  fileType?: string;
}

export const FileWarningModal: React.FC<FileWarningModalProps> = ({
  isOpen,
  onClose,
  fileName,
  fileType
}) => {
  if (!isOpen) return null;

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
        <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
          <div className="flex items-center justify-between p-6 border-b">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-amber-100 flex items-center justify-center">
                <AlertTriangle className="w-5 h-5 text-amber-600" />
              </div>
              <h2 className="text-lg font-semibold text-neutral-950">
                Tipo de archivo no válido
              </h2>
            </div>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
          
          <div className="p-6">
            <div className="mb-4">
              <p className="text-gray-700 mb-2">
                El archivo seleccionado no es una imagen válida.
              </p>
              {fileName && (
                <div className="bg-gray-50 rounded-lg p-3 mb-3">
                  <p className="text-sm text-gray-600">
                    <span className="font-medium">Archivo:</span> {fileName}
                  </p>
                  {fileType && (
                    <p className="text-sm text-gray-600">
                      <span className="font-medium">Tipo:</span> {fileType}
                    </p>
                  )}
                </div>
              )}
              <p className="text-gray-700">
                Por favor, selecciona solo archivos de imagen en formato:
              </p>
              <ul className="list-disc list-inside mt-2 text-gray-600 text-sm">
                <li>JPG / JPEG</li>
                <li>PNG</li>
              </ul>
            </div>
          </div>
          
          <div className="flex justify-end gap-3 p-6 border-t bg-gray-50">
            <Button
              onClick={onClose}
              className="bg-[#f23030] hover:bg-[#d92828] text-white"
            >
              Entendido
            </Button>
          </div>
        </div>
      </div>
    </Dialog>
  );
};