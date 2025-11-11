import React from 'react';
import { LucideIcon } from 'lucide-react';
import { Button } from './ui/button';

interface ActionButton {
  id: string;
  label: string;
  icon: LucideIcon;
  onClick?: () => void;
  variant?: 'default' | 'destructive' | 'outline' | 'secondary' | 'ghost' | 'link';
}

interface ActionButtonListProps {
  actionButtons: ActionButton[];
  className?: string;
}

export const ActionButtonList: React.FC<ActionButtonListProps> = ({
  actionButtons,
  className = ''
}) => {
  return (
    <div className={`flex flex-wrap gap-3 ${className}`}>
      {actionButtons.map((button) => {
        const Icon = button.icon;

        return (
          <Button
            key={button.id}
            variant={button.variant || 'default'}
            onClick={button.onClick}
            className="flex items-center gap-2"
          >
            <Icon className="w-4 h-4" />
            <span>{button.label}</span>
          </Button>
        );
      })}
    </div>
  );
};
