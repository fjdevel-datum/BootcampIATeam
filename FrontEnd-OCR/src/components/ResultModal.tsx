import React, { useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { CheckCircle, XCircle } from 'lucide-react';

interface ResultModalProps {
  isOpen: boolean;
  onClose: () => void;
  type: 'success' | 'error';
  title?: string; // Opcional, se mantiene por compatibilidad pero no se usa
  message?: string; // Opcional, se mantiene por compatibilidad pero no se usa
}

export const ResultModal: React.FC<ResultModalProps> = ({
  isOpen,
  onClose,
  type,
}) => {
  // Auto-cerrar después de 6 segundos
  useEffect(() => {
    if (isOpen) {
      const timer = setTimeout(() => {
        onClose();
      }, 6000);

      return () => clearTimeout(timer);
    }
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const isSuccess = type === 'success';
  const IconComponent = isSuccess ? CheckCircle : XCircle;
  const message = isSuccess 
    ? 'Operación realizada con éxito.' 
    : 'Ha ocurrido un error.';
  const bgColor = isSuccess 
    ? 'bg-gradient-to-r from-green-500 to-green-600' 
    : 'bg-gradient-to-r from-red-500 to-red-600';

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0, y: -100 }}
        animate={{ opacity: 1, y: 0 }}
        exit={{ opacity: 0, y: -100 }}
        transition={{ type: "spring", stiffness: 500, damping: 30 }}
        className="fixed z-[100]
                   top-3 left-3 right-3
                   xs:top-4 xs:left-4 xs:right-4
                   sm:top-5 sm:left-auto sm:right-5 sm:max-w-sm
                   md:top-6 md:right-6 md:max-w-md
                   lg:max-w-lg
                   mx-auto sm:mx-0"
      >
        <div className={`${bgColor} rounded-lg shadow-2xl overflow-hidden backdrop-blur-sm`}>
          {/* Barra de progreso animada */}
          <motion.div
            className="h-0.5 sm:h-1 bg-white bg-opacity-30"
            initial={{ width: '100%' }}
            animate={{ width: '0%' }}
            transition={{ duration: 6, ease: 'linear' }}
          />
          
          {/* Contenido del toast */}
          <div className="flex items-center gap-2 xs:gap-3 sm:gap-4 
                         p-2.5 xs:p-3 sm:p-4 md:p-5">
            {/* Ícono animado */}
            <motion.div
              initial={{ scale: 0, rotate: -180 }}
              animate={{ scale: 1, rotate: 0 }}
              transition={{ type: "spring", delay: 0.1, stiffness: 200 }}
              className="flex-shrink-0"
            >
              <div className="w-9 h-9 xs:w-10 xs:h-10 sm:w-12 sm:h-12 md:w-14 md:h-14
                            rounded-full bg-white bg-opacity-20 
                            flex items-center justify-center backdrop-blur-sm
                            shadow-inner">
                <IconComponent 
                  className="w-5 h-5 xs:w-6 xs:h-6 sm:w-7 sm:h-7 md:w-8 md:h-8 text-white" 
                  strokeWidth={2.5} 
                />
              </div>
            </motion.div>
            
            {/* Mensaje */}
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.2 }}
              className="flex-1 min-w-0 pr-1 xs:pr-2"
            >
              <p className="text-white font-medium 
                           text-xs xs:text-sm sm:text-base md:text-lg
                           leading-snug xs:leading-normal sm:leading-relaxed
                           break-words">
                {message}
              </p>
            </motion.div>

            {/* Botón de cerrar opcional para móviles */}
            <motion.button
              initial={{ opacity: 0, scale: 0 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.3 }}
              onClick={onClose}
              className="flex-shrink-0 w-6 h-6 xs:w-7 xs:h-7 sm:w-8 sm:h-8
                       rounded-full bg-white bg-opacity-20 hover:bg-opacity-30
                       flex items-center justify-center
                       transition-all duration-200
                       active:scale-95
                       sm:hidden"
              aria-label="Cerrar notificación"
            >
              <span className="text-white text-sm xs:text-base font-bold">✕</span>
            </motion.button>
          </div>
        </div>
      </motion.div>
    </AnimatePresence>
  );
};