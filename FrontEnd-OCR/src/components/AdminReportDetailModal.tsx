import React from 'react';
import { X, Calendar, DollarSign, Tag, Building } from 'lucide-react';
import { ExpenseGroup } from '../types/api';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from './ui/dialog';
import { Button } from './ui/button';

interface AdminReportDetailModalProps {
  isOpen: boolean;
  onClose: () => void;
  report: ExpenseGroup;
}

export const AdminReportDetailModal: React.FC<AdminReportDetailModalProps> = ({
  isOpen,
  onClose,
  report,
}) => {
  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-5xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-2xl flex items-center gap-2">
            Reporte {report.month}
          </DialogTitle>
        </DialogHeader>

        <div className="mt-6 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-blue-50 rounded-lg p-4 border border-blue-200">
              <p className="text-sm text-blue-600 mb-1">Estado</p>
              <p className="text-xl font-bold text-blue-900">{report.status}</p>
            </div>
            <div className="bg-green-50 rounded-lg p-4 border border-green-200">
              <p className="text-sm text-green-600 mb-1">Gasto Total</p>
              <p className="text-xl font-bold text-green-900">${report.total.toFixed(2)}</p>
            </div>
            <div className="bg-purple-50 rounded-lg p-4 border border-purple-200">
              <p className="text-sm text-purple-600 mb-1">Total Gastos</p>
              <p className="text-xl font-bold text-purple-900">{report.count}</p>
            </div>
          </div>

          <div className="bg-white rounded-lg border border-gray-200">
            <div className="bg-gray-50 px-6 py-3 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-neutral-950">Gastos</h3>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Fecha
                    </th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Proveedor
                    </th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Monto
                    </th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Categoría
                    </th>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Descripción
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {report.expenses.map((expense, index) => (
                    <tr key={expense.id} className="hover:bg-gray-50">
                      <td className="px-4 py-4 whitespace-nowrap text-sm text-gray-600">
                        <div className="flex items-center gap-2">
                          <Calendar className="w-4 h-4 text-gray-400" />
                          {expense.invoiceDate}
                        </div>
                      </td>
                      <td className="px-4 py-4 whitespace-nowrap text-sm font-medium text-neutral-950">
                        {expense.vendorName}
                      </td>
                      <td className="px-4 py-4 whitespace-nowrap text-sm font-semibold text-neutral-950">
                        <div className="flex items-center gap-2">
                          <DollarSign className="w-4 h-4 text-green-600" />
                          {expense.currency} {expense.totalAmount.toFixed(2)}
                        </div>
                      </td>
                      <td className="px-4 py-4 whitespace-nowrap">
                        <div className="flex items-center gap-2">
                          <Tag className="w-4 h-4 text-blue-600" />
                          <span className="text-sm text-gray-900">{expense.category}</span>
                        </div>
                        {expense.costCenterName && (
                          <div className="flex items-center gap-2 mt-1">
                            <Building className="w-3 h-3 text-purple-600" />
                            <span className="text-xs text-purple-600">{expense.costCenterName}</span>
                          </div>
                        )}
                      </td>
                      <td className="px-4 py-4 text-sm text-gray-600">
                        {expense.concept}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div className="flex justify-end">
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
