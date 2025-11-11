import { useState, useCallback } from 'react';

export type NotificationType = 'success' | 'error';

interface NotificationState {
  isOpen: boolean;
  type: NotificationType;
}

interface UseNotificationReturn {
  notificationState: NotificationState;
  showNotification: (type: NotificationType) => void;
  hideNotification: () => void;
}

/**
 * Hook personalizado para gestionar notificaciones toast
 * 
 * Uso:
 * ```tsx
 * const { notificationState, showNotification, NotificationComponent } = useNotification();
 * 
 * // Mostrar notificación
 * showNotification('success');
 * showNotification('error');
 * 
 * // Renderizar en JSX
 * <NotificationComponent />
 * ```
 */
export const useNotification = (): UseNotificationReturn => {
  const [notificationState, setNotificationState] = useState<NotificationState>({
    isOpen: false,
    type: 'success',
  });

  const showNotification = useCallback((type: NotificationType) => {
    setNotificationState({
      isOpen: true,
      type,
    });
  }, []);

  const hideNotification = useCallback(() => {
    setNotificationState(prev => ({
      ...prev,
      isOpen: false,
    }));
  }, []);

  return {
    notificationState,
    showNotification,
    hideNotification,
  };
};
