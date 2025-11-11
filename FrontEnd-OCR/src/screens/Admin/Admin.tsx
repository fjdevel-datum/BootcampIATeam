import React, { useState } from 'react';
import { AdminLayout } from '../../components/AdminLayout';
import { AdminDashboard } from '../../components/AdminDashboard';
import { AdminContabilidad } from '../../components/AdminContabilidad';

export const Admin = (): JSX.Element => {
  const [activeSection, setActiveSection] = useState<string>('dashboard');

  const renderContent = () => {
    switch (activeSection) {
      case 'dashboard':
        return <AdminDashboard />;
      case 'contabilidad':
        return <AdminContabilidad />;
      default:
        return <AdminDashboard />;
    }
  };

  return (
    <AdminLayout activeSection={activeSection} onSectionChange={setActiveSection}>
      {renderContent()}
    </AdminLayout>
  );
};
