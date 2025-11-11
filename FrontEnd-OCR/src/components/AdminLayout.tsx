import React, { useState } from 'react';
import { Menu, X, LayoutDashboard, FileText, Receipt, DollarSign, Search, Bell } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';

interface AdminLayoutProps {
  children: React.ReactNode;
  activeSection: string;
  onSectionChange: (section: string) => void;
}

interface MenuItem {
  id: string;
  label: string;
  icon: React.ComponentType<any>;
}

const menuItems: MenuItem[] = [
  { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { id: 'ocr', label: 'OCR', icon: FileText },
  { id: 'documentos', label: 'Documentos', icon: Receipt },
  { id: 'gastos', label: 'Gastos', icon: Receipt },
  { id: 'contabilidad', label: 'Contabilidad', icon: DollarSign },
];

export const AdminLayout: React.FC<AdminLayoutProps> = ({
  children,
  activeSection,
  onSectionChange,
}) => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-[#f23030] text-white shadow-md fixed top-0 left-0 right-0 z-30 h-14">
        <div className="flex items-center justify-between h-full px-4">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 bg-white rounded flex items-center justify-center">
                <span className="text-[#f23030] font-bold text-sm">D</span>
              </div>
              <span className="font-semibold text-base">Administrador</span>
            </div>
          </div>

          <div className="flex items-center gap-4">
            <div className="hidden md:block relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
              <Input
                placeholder="Search"
                className="pl-10 bg-white text-gray-900 border-none w-64"
              />
            </div>

            <Button variant="ghost" size="icon" className="text-white hover:bg-red-600">
              <Bell className="w-5 h-5" />
            </Button>

            <div className="w-8 h-8 bg-white rounded-full flex items-center justify-center">
              <span className="text-[#f23030] font-semibold text-sm">A</span>
            </div>
          </div>
        </div>
      </header>

      <div className="flex pt-14">
        <aside
          className={`fixed left-0 top-14 h-[calc(100vh-3.5rem)] bg-white border-r border-gray-200 transition-all duration-300 z-20 ${
            isSidebarOpen ? 'w-60' : 'w-16'
          }`}
        >
          <div className="flex flex-col h-full">
            <div className="p-4 border-b border-gray-200 flex items-center justify-between">
              {isSidebarOpen && (
                <div className="flex items-center gap-2">
                  <div className="w-6 h-6 [background:url(../image--datum-el-salvador-.png)_50%_50%_/_contain_no-repeat]" />
                  <span className="text-sm font-medium text-gray-700">DATUM</span>
                </div>
              )}
              <Button
                variant="ghost"
                size="icon"
                onClick={() => setIsSidebarOpen(!isSidebarOpen)}
                className="text-gray-600 hover:text-gray-900"
              >
                <Menu className="w-5 h-5" />
              </Button>
            </div>

            <nav className="flex-1 overflow-y-auto py-4">
              {menuItems.map((item) => {
                const Icon = item.icon;
                const isActive = activeSection === item.id;

                return (
                  <button
                    key={item.id}
                    onClick={() => onSectionChange(item.id)}
                    className={`w-full flex items-center gap-3 px-4 py-3 text-sm transition-colors ${
                      isActive
                        ? 'bg-gray-100 text-neutral-950 border-r-4 border-[#f23030]'
                        : 'text-gray-600 hover:bg-gray-50 hover:text-neutral-950'
                    }`}
                  >
                    <Icon className={`w-5 h-5 flex-shrink-0 ${isActive ? 'text-[#f23030]' : ''}`} />
                    {isSidebarOpen && <span>{item.label}</span>}
                  </button>
                );
              })}
            </nav>
          </div>
        </aside>

        <main
          className={`flex-1 transition-all duration-300 ${
            isSidebarOpen ? 'ml-60' : 'ml-16'
          }`}
        >
          <div className="p-6">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
};
