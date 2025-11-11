import React from 'react';
import { Home, Settings, User, Mail, Bell, Search } from 'lucide-react';
import { ActionButtonList } from './ActionButtonList';

export const ActionButtonListExample: React.FC = () => {
  const actionButtons = [
    {
      id: '1',
      label: 'Inicio',
      icon: Home,
      onClick: () => console.log('Inicio clicked'),
      variant: 'default' as const,
    },
    {
      id: '2',
      label: 'Configuración',
      icon: Settings,
      onClick: () => console.log('Configuración clicked'),
      variant: 'outline' as const,
    },
    {
      id: '3',
      label: 'Perfil',
      icon: User,
      onClick: () => console.log('Perfil clicked'),
      variant: 'secondary' as const,
    },
    {
      id: '4',
      label: 'Mensajes',
      icon: Mail,
      onClick: () => console.log('Mensajes clicked'),
      variant: 'ghost' as const,
    },
    {
      id: '5',
      label: 'Notificaciones',
      icon: Bell,
      onClick: () => console.log('Notificaciones clicked'),
      variant: 'outline' as const,
    },
    {
      id: '6',
      label: 'Buscar',
      icon: Search,
      onClick: () => console.log('Buscar clicked'),
      variant: 'default' as const,
    },
  ];

  return (
    <div className="p-8">
      <h2 className="text-2xl font-bold mb-6">Lista de Botones de Acción</h2>
      <ActionButtonList actionButtons={actionButtons} />
    </div>
  );
};
