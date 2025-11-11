// @ts-nocheck
import React, { useMemo } from 'react';
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';
import { X, TrendingUp } from 'lucide-react';
import { Expense } from '../types/api';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from './ui/dialog';
import { Button } from './ui/button';

interface ExpenseDashboardModalProps {
  isOpen: boolean;
  onClose: () => void;
  expenses: Expense[];
  month: string;
  total: number;
}

// Colores para las categorías (paleta vibrante)
const COLORS = [
  '#FF6384', // Rosa
  '#36A2EB', // Azul
  '#FFCE56', // Amarillo
  '#4BC0C0', // Turquesa
  '#9966FF', // Púrpura
  '#FF9F40', // Naranja
  '#FF6384', // Rosa (repetir)
  '#C9CBCF', // Gris
  '#4BC0C0', // Turquesa (repetir)
  '#FF9F40', // Naranja (repetir)
];

export const ExpenseDashboardModal: React.FC<ExpenseDashboardModalProps> = ({
  isOpen,
  onClose,
  expenses,
  month,
  total,
}) => {
  // Agrupar gastos por categoría
  const categoryData = useMemo(() => {
    const grouped = expenses.reduce((acc, expense) => {
      const category = expense.category;
      if (!acc[category]) {
        acc[category] = {
          name: category,
          value: 0,
          count: 0,
        };
      }
      acc[category].value += expense.totalAmount;
      acc[category].count += 1;
      return acc;
    }, {} as Record<string, { name: string; value: number; count: number }>);

    // Convertir a array y ordenar por valor descendente
    return Object.values(grouped).sort((a, b) => b.value - a.value);
  }, [expenses]);

  // Custom tooltip - Retorna ReactElement para compatibilidad
  const CustomTooltip: React.FC<any> = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      const percentage = ((data.value / total) * 100).toFixed(1);
      return (
        <div className="bg-white border border-gray-200 rounded-lg shadow-lg p-3">
          <p className="font-semibold text-neutral-950">{data.name}</p>
          <p className="text-sm text-gray-600">
            Monto: <span className="font-semibold">${data.value.toFixed(2)}</span>
          </p>
          <p className="text-sm text-gray-600">
            Gastos: <span className="font-semibold">{data.count}</span>
          </p>
          <p className="text-sm text-gray-600">
            Porcentaje: <span className="font-semibold">{percentage}%</span>
          </p>
        </div>
      );
    }
    return null;
  };  // Custom legend
  const renderLegend = (props: any) => {
    const { payload } = props;
    return (
      <div className="flex flex-wrap gap-2 sm:gap-3 justify-center mt-4 max-h-40 sm:max-h-48 overflow-y-auto px-2">
        {payload.map((entry: any, index: number) => {
          const percentage = ((entry.payload.value / total) * 100).toFixed(1);
          return (
            <div
              key={`legend-${index}`}
              className="flex items-center gap-1.5 sm:gap-2 bg-gray-50 rounded-lg px-2 py-1.5 sm:px-3 sm:py-2 text-xs sm:text-sm flex-shrink-0"
            >
              <div
                className="w-2.5 h-2.5 sm:w-3 sm:h-3 rounded-full flex-shrink-0"
                style={{ backgroundColor: entry.color }}
              />
              <span className="font-medium text-neutral-950 truncate max-w-[120px] sm:max-w-none">
                {entry.value}
              </span>
              <span className="text-gray-600 whitespace-nowrap">
                ${entry.payload.value.toFixed(2)} ({percentage}%)
              </span>
            </div>
          );
        })}
      </div>
    );
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-2xl flex items-center gap-2">
            <TrendingUp className="w-6 h-6 text-[#f23030]" />
            Resumen de Gastos - {month}
          </DialogTitle>
          <DialogDescription>
            Análisis detallado de la distribución de gastos por categoría
          </DialogDescription>
        </DialogHeader>

        <div className="mt-6">
          {/* Estadísticas Rápidas */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
            <div className="bg-blue-50 rounded-lg p-4 border border-blue-200">
              <p className="text-sm text-blue-600 mb-1">Total Gastado</p>
              <p className="text-2xl font-bold text-blue-900">
                ${total.toFixed(2)}
              </p>
            </div>
            <div className="bg-green-50 rounded-lg p-4 border border-green-200">
              <p className="text-sm text-green-600 mb-1">Número de Gastos</p>
              <p className="text-2xl font-bold text-green-900">
                {expenses.length}
              </p>
            </div>
            <div className="bg-purple-50 rounded-lg p-4 border border-purple-200">
              <p className="text-sm text-purple-600 mb-1">Categorías</p>
              <p className="text-2xl font-bold text-purple-900">
                {categoryData.length}
              </p>
            </div>
          </div>

          {/* Gráfica de Anillos */}
          <div className="bg-white rounded-lg border border-gray-200 p-3 sm:p-6">
            <h3 className="text-base sm:text-lg font-semibold mb-3 sm:mb-4 text-neutral-950">
              Distribución por Categoría
            </h3>
            {categoryData.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-600">No hay datos para mostrar</p>
              </div>
            ) : (
              <div className="w-full" style={{ height: categoryData.length > 5 ? 500 : 400 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={categoryData}
                      cx="50%"
                      cy="45%"
                      innerRadius={window.innerWidth < 640 ? 50 : 80}
                      outerRadius={window.innerWidth < 640 ? 90 : 140}
                      fill="#8884d8"
                      paddingAngle={2}
                      dataKey="value"
                      label={window.innerWidth >= 640 ? (entry: any) => {
                        const name = entry.name;
                        const percent = entry.percent || 0;
                        // Truncar nombres largos
                        const shortName = name.length > 15 ? name.substring(0, 12) + '...' : name;
                        return `${shortName}: ${(percent * 100).toFixed(0)}%`;
                      } : false}
                      labelLine={window.innerWidth >= 640 ? { stroke: '#666', strokeWidth: 1 } : false}
                    >
                      {categoryData.map((_entry: any, index: number) => (
                        <Cell
                          key={`cell-${index}`}
                          fill={COLORS[index % COLORS.length]}
                        />
                      ))}
                    </Pie>
                    <Tooltip content={CustomTooltip as any} />
                    <Legend 
                      content={renderLegend as any}
                      verticalAlign={categoryData.length > 5 ? "bottom" : "bottom"}
                      wrapperStyle={{ paddingTop: categoryData.length > 5 ? '20px' : '10px' }}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            )}
          </div>

          {/* Tabla de Detalles */}
          <div className="mt-6 bg-white rounded-lg border border-gray-200">
            <div className="bg-gray-50 px-6 py-3 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-neutral-950">
                Detalle por Categoría
              </h3>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 hidden md:table-header-group">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Categoría
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Gastos
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Total
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      % del Total
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200 md:divide-y-0">
                  {categoryData.map((category, index) => {
                    const percentage = ((category.value / total) * 100).toFixed(1);
                    return (
                      <tr key={category.name} className="block md:table-row hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap flex items-center justify-between md:table-cell">
                          <span className="text-xs font-medium text-gray-500 uppercase md:hidden">Categoría</span>
                          <div className="flex items-center gap-2">
                            <div
                              className="w-3 h-3 rounded-full"
                              style={{ backgroundColor: COLORS[index % COLORS.length] }}
                            />
                            <span className="text-sm font-medium text-neutral-950">
                              {category.name}
                            </span>
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap flex items-center justify-between md:table-cell text-sm text-gray-600">
                          <span className="text-xs font-medium text-gray-500 uppercase md:hidden">Gastos</span>
                          {category.count}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap flex items-center justify-between md:table-cell text-sm font-semibold text-neutral-950">
                          <span className="text-xs font-medium text-gray-500 uppercase md:hidden">Total</span>
                          <span>${category.value.toFixed(2)}</span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap flex items-center justify-between md:table-cell">
                          <span className="text-xs font-medium text-gray-500 uppercase md:hidden">% del Total</span>
                          <div className="flex items-center gap-2 w-full justify-end">
                            <div className="flex-1 bg-gray-200 rounded-full h-2 max-w-[100px]">
                              <div
                                className="h-2 rounded-full"
                                style={{
                                  width: `${percentage}%`,
                                  backgroundColor: COLORS[index % COLORS.length],
                                }}
                              />
                            </div>
                            <span className="text-sm text-gray-600 min-w-[50px] text-right">
                              {percentage}%
                            </span>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>

          {/* Botón de Cerrar */}
          <div className="mt-6 flex justify-end">
            <Button onClick={onClose} variant="outline">
              <X className="w-4 h-4 mr-2" />
              Cerrar
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};
