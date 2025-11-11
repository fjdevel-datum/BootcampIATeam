import React from 'react';
import { motion } from 'framer-motion';
import { Users, FileText, DollarSign, TrendingUp } from 'lucide-react';

export const AdminDashboard: React.FC = () => {
  const stats = [
    { label: 'Total Usuarios', value: '125', icon: Users, color: 'bg-blue-500' },
    { label: 'Reportes Pendientes', value: '42', icon: FileText, color: 'bg-yellow-500' },
    { label: 'Total Gastos', value: '$45,230', icon: DollarSign, color: 'bg-green-500' },
    { label: 'Aprobaciones', value: '89', icon: TrendingUp, color: 'bg-purple-500' },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-neutral-950 mb-2">
          Bienvenido al Panel de Administración
        </h1>
        <p className="text-gray-600">
          Gestiona usuarios, reportes y aprobaciones desde este panel centralizado
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <motion.div
              key={stat.label}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="bg-white rounded-lg shadow-sm border border-gray-200 p-6"
            >
              <div className="flex items-center justify-between mb-4">
                <div className={`${stat.color} w-12 h-12 rounded-lg flex items-center justify-center`}>
                  <Icon className="w-6 h-6 text-white" />
                </div>
              </div>
              <p className="text-2xl font-bold text-neutral-950 mb-1">
                {stat.value}
              </p>
              <p className="text-sm text-gray-600">
                {stat.label}
              </p>
            </motion.div>
          );
        })}
      </div>

      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
        <div className="text-center">
          <div className="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <FileText className="w-10 h-10 text-gray-400" />
          </div>
          <h2 className="text-xl font-semibold text-neutral-950 mb-2">
            Panel de Control
          </h2>
          <p className="text-gray-600 max-w-md mx-auto">
            Utiliza el menú lateral para navegar entre las diferentes secciones del sistema de administración.
          </p>
        </div>
      </div>
    </div>
  );
};
