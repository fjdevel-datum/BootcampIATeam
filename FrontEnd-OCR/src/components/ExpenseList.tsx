import React, { useState, useEffect, useRef } from 'react';
import { motion } from 'framer-motion';
import { Calendar, Filter, Eye, Coffee, Car, ShoppingBag, DollarSign, BarChart2 } from 'lucide-react';
import { ApiService } from '../services/apiService';
import { ExpenseGroup, Category, Expense } from '../types/api';
import { ExpenseDashboardModal } from './ExpenseDashboardModal';
import { EditInvoiceModal } from './EditInvoiceModal';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from './ui/select';
import { Button } from './ui/button';

interface ExpenseListProps {
  cardId: string;
  className?: string;
}

const getIconComponent = (icon?: string) => {
  switch (icon?.toLowerCase()) {
    case 'coffee':
      return Coffee;
    case 'car':
      return Car;
    case 'cash':
    case 'dollar':
      return DollarSign;
    case 'shopping':
    case 'shop':
      return ShoppingBag;
    default:
      return DollarSign;
  }
};

const getStatusColor = (status: string) => {
  switch (status.toUpperCase()) {
    case 'APROBADO':
      return 'text-green-600 bg-green-50';
    case 'PENDIENTE':
      return 'text-yellow-600 bg-yellow-50';
    case 'RECHAZADO':
      return 'text-red-600 bg-red-50';
    default:
      return 'text-gray-600 bg-gray-50';
  }
};

export const ExpenseList: React.FC<ExpenseListProps> = ({ cardId, className = '' }) => {
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [selectedPeriod, setSelectedPeriod] = useState<string>('all');
  const [selectedStatus, setSelectedStatus] = useState<string>('all');
  const [expenseGroups, setExpenseGroups] = useState<ExpenseGroup[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [loadingCategories, setLoadingCategories] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  
  // Estados para el modal de dashboard
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [selectedGroup, setSelectedGroup] = useState<ExpenseGroup | null>(null);
  
  // Estados para el modal de edición de factura
  const [isEditModalOpen, setIsEditModalOpen] = useState<boolean>(false);
  const [selectedExpenseForEdit, setSelectedExpenseForEdit] = useState<Expense | null>(null);
  const [isViewOnlyMode, setIsViewOnlyMode] = useState<boolean>(false);
  
  // Refs para prevenir llamadas duplicadas
  const fetchingCategoriesRef = useRef(false);
  const fetchingExpensesRef = useRef(false);

  // Cargar categorías al montar el componente
  useEffect(() => {
    const fetchCategories = async () => {
      // Prevenir llamadas duplicadas
      if (fetchingCategoriesRef.current) return;
      
      fetchingCategoriesRef.current = true;
      setLoadingCategories(true);
      
      try {
        console.log('Fetching categories...');
        const categoriesData = await ApiService.getCategories();
        setCategories(categoriesData);
        console.log('Categories loaded successfully:', categoriesData);
      } catch (err) {
        console.error('Error fetching categories:', err);
        // En caso de error, continuar sin categorías
        setCategories([]);
      } finally {
        setLoadingCategories(false);
        fetchingCategoriesRef.current = false;
      }
    };

    fetchCategories();
  }, []);

  // Cargar datos de gastos al montar el componente
  useEffect(() => {
    const fetchExpenses = async () => {
      if (!cardId || cardId === '') return;
      
      // Prevenir llamadas duplicadas
      if (fetchingExpensesRef.current) return;
      
      fetchingExpensesRef.current = true;
      setLoading(true);
      setError(null);
      
      try {
        console.log(`Fetching expenses for card ID: ${cardId}`);
        const expenses = await ApiService.getCardExpenses(parseInt(cardId));
        setExpenseGroups(expenses);
        console.log('Expenses loaded successfully:', expenses);
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Error al cargar los gastos';
        setError(errorMessage);
        console.error('Error fetching expenses:', err);
        // En caso de error, mostrar datos vacíos
        setExpenseGroups([]);
      } finally {
        setLoading(false);
        fetchingExpensesRef.current = false;
      }
    };

    fetchExpenses();
  }, [cardId]);

  // Filtrar gastos según los filtros seleccionados
  const filteredExpenseGroups = expenseGroups
    .filter(group => {
      // Filtro por estado
      if (selectedStatus !== 'all') {
        const statusMatch = group.status.toUpperCase() === selectedStatus.toUpperCase();
        if (!statusMatch) return false;
      }
      
      // Filtro por período - solo filtra grupos completos
      if (selectedPeriod !== 'all') {
        const periodMatch = group.month.toLowerCase().includes(selectedPeriod.toLowerCase());
        if (!periodMatch) return false;
      }
      return true;
    })
    .map(group => {
      // Filtro por categoría - filtra los gastos dentro de cada grupo
      if (selectedCategory === 'all') {
        return group; // Sin filtro de categoría, mostrar todos los gastos
      }
      
      // Filtrar los gastos del grupo por categoría
      const filteredExpenses = group.expenses.filter(expense => 
        expense.category.toLowerCase().includes(selectedCategory.toLowerCase())
      );
      
      // Retornar el grupo con los gastos filtrados
      return {
        ...group,
        expenses: filteredExpenses
      };
    });

  // Separar grupos por estado
  const pendingGroups = filteredExpenseGroups.filter(group => 
    group.status.toUpperCase() === 'PENDIENTE'
  );
  
  const approvedGroups = filteredExpenseGroups.filter(group => 
    group.status.toUpperCase() === 'APROBADO'
  );

  if (loading) {
    return (
      <div className={`w-full ${className}`}>
        <div className="flex items-center gap-2 mb-6">
          <Calendar className="w-5 h-5 text-neutral-950" />
          <h2 className="text-lg font-semibold text-neutral-950">
            Historial de Facturas.
          </h2>
        </div>
        <div className="flex items-center justify-center py-12">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-neutral-950 mx-auto mb-4"></div>
            <p className="text-gray-600">Cargando historial de gastos...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={`w-full ${className}`}>
        <div className="flex items-center gap-2 mb-6">
          <Calendar className="w-5 h-5 text-neutral-950" />
          <h2 className="text-lg font-semibold text-neutral-950">
            Historial de Facturas.
          </h2>
        </div>
        <div className="text-center py-12">
          <p className="text-red-600 mb-4">Error al cargar los gastos: {error}</p>
          <Button 
            onClick={() => window.location.reload()} 
            variant="outline"
          >
            Reintentar
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className={`w-full ${className}`}>
      <div className="flex items-center gap-2 mb-6">
        <Calendar className="w-5 h-5 text-neutral-950" />
        <h2 className="text-lg font-semibold text-neutral-950">
          Historial de Facturas.
        </h2>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="flex items-center gap-2">
          <Filter className="w-4 h-4 text-gray-500" />
          <Select value={selectedCategory} onValueChange={setSelectedCategory}>
            <SelectTrigger className="w-full">
              <SelectValue placeholder="Todas las categorías" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Todas las categorías</SelectItem>
              {loadingCategories ? (
                <SelectItem value="loading" disabled>
                  Cargando categorías...
                </SelectItem>
              ) : categories.length === 0 ? (
                <SelectItem value="none" disabled>
                  No hay categorías disponibles
                </SelectItem>
              ) : (
                categories.map((category) => (
                  <SelectItem 
                    key={category.id} 
                    value={category.name.toLowerCase()}
                  >
                    {category.name}
                  </SelectItem>
                ))
              )}
            </SelectContent>
          </Select>
        </div>

        <Select value={selectedStatus} onValueChange={setSelectedStatus}>
          <SelectTrigger className="w-full">
            <SelectValue placeholder="Todos los estados" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Todos los estados</SelectItem>
            <SelectItem value="PENDIENTE">Pendientes</SelectItem>
            <SelectItem value="APROBADO">Aprobadas</SelectItem>
          </SelectContent>
        </Select>

        <Select value={selectedPeriod} onValueChange={setSelectedPeriod}>
          <SelectTrigger className="w-full">
            <SelectValue placeholder="Todos los períodos" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Todos los períodos</SelectItem>
            <SelectItem value="2024">2024</SelectItem>
            <SelectItem value="2023">2023</SelectItem>
            <SelectItem value="2022">2022</SelectItem>
            <SelectItem value="2021">2021</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {filteredExpenseGroups.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600 mb-4">No se encontraron gastos para esta tarjeta.</p>
          <p className="text-sm text-gray-500">
            Los gastos aparecerán aquí una vez que se procesen las facturas.
          </p>
        </div>
      ) : (
        <div className="space-y-8">
          {/* Sección de Facturas Pendientes */}
          {pendingGroups.length > 0 && (
            <div className="space-y-4">
              <div className="flex items-center gap-2 pb-2 border-b-2 border-yellow-500">
                <div className="w-3 h-3 rounded-full bg-yellow-500"></div>
                <h3 className="text-lg font-bold text-neutral-950">
                  Facturas Pendientes
                </h3>
                <span className="text-sm text-gray-600">
                  ({pendingGroups.length} {pendingGroups.length === 1 ? 'grupo' : 'grupos'})
                </span>
              </div>
              
              <div className="space-y-6">
                {pendingGroups.map((group, groupIndex) => (
                  <motion.div
                    key={`pending-${group.month}`}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: groupIndex * 0.1 }}
                    className="space-y-3"
                  >
                    <div className="flex items-center justify-between">
                      <h4 className="text-base font-semibold text-neutral-950">
                        {group.month}
                      </h4>
                      <div className="text-xl font-bold text-neutral-950">
                        ${group.total.toFixed(2)}
                      </div>
                    </div>

                    <div className="flex items-center gap-2 mb-3">
                      <span className="text-sm text-gray-600">
                        {group.count} gastos
                      </span>
                      <span className={`text-xs font-semibold px-2 py-1 rounded ${getStatusColor(group.status)}`}>
                        {group.status}
                      </span>
                    </div>

                    <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 flex flex-col sm:flex-row sm:items-center sm:justify-between mb-3 gap-3 sm:gap-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <Calendar className="w-4 h-4 text-blue-600" />
                        <span className="text-sm text-blue-900">
                          Estado del reporte:
                        </span>
                        <span className={`text-sm font-semibold px-2 py-0.5 rounded ${getStatusColor(group.status)}`}>
                          {group.status}
                        </span>
                      </div>
                      <div className="flex items-center gap-2 self-end sm:self-center">
                        <Button
                          variant="ghost"
                          size="sm"
                          className="text-purple-600 hover:text-purple-700 hover:bg-purple-100"
                          onClick={() => {
                            setSelectedGroup(group);
                            setIsModalOpen(true);
                          }}
                        >
                          <BarChart2 className="w-4 h-4 mr-1" />
                          Resumen
                        </Button>
                      </div>
                    </div>

                    <div className="space-y-2">
                      {group.expenses.length === 0 ? (
                        <div className="bg-gray-50 border border-gray-200 rounded-lg p-6 text-center">
                          <p className="text-sm text-gray-600">
                            No hay gastos en esta categoría para este período.
                          </p>
                        </div>
                      ) : (
                        group.expenses.map((expense, expenseIndex) => {
                          const IconComponent = getIconComponent(expense.icon);
                          return (
                            <motion.div
                              key={expense.id}
                              initial={{ opacity: 0, x: -20 }}
                              animate={{ opacity: 1, x: 0 }}
                              transition={{ delay: (groupIndex * 0.1) + (expenseIndex * 0.05) }}
                              className="bg-white border border-gray-200 rounded-lg p-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 sm:gap-2 hover:shadow-md transition-shadow"
                            >
                              <div className="flex items-center gap-3 w-full">
                                <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center">
                                  <IconComponent className="w-5 h-5 text-gray-600 flex-shrink-0" />
                                </div>
                                <div>
                                  <div className="text-sm font-medium text-neutral-950">
                                    {expense.vendorName} - {expense.concept}
                                  </div>
                                  <div className="text-xs text-gray-500 mb-1">
                                    {expense.category}
                                    {expense.costCenterName && (
                                      <span className="ml-2 text-blue-600">
                                        • {expense.costCenterName}
                                      </span>
                                    )}
                                  </div>
                                  <div className="text-xs text-gray-500">
                                    {expense.invoiceDate} • {expense.currency}
                                  </div>
                                </div>
                              </div>

                              <div className="flex items-center justify-between w-full sm:w-auto sm:gap-4">
                                <div className="text-left sm:text-right">
                                  <div className="text-base font-semibold text-neutral-950">
                                    ${expense.totalAmount.toFixed(2)}
                                  </div>
                                  <div className="text-xs text-gray-500">
                                    {expense.status}
                                  </div>
                                </div>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  className="text-gray-600 hover:text-gray-900 -mr-2"
                                  onClick={() => {
                                    setSelectedExpenseForEdit(expense);
                                    // Solo permitir edición si el estado de la factura es BORRADOR
                                    setIsViewOnlyMode(expense.status.toUpperCase() !== 'BORRADOR');
                                    setIsEditModalOpen(true);
                                  }}
                                  title={expense.status.toUpperCase() === 'BORRADOR' ? 'Editar factura' : 'Visualizar factura (Solo lectura)'}
                                >
                                  <Eye className="w-4 h-4" />
                                </Button>
                              </div>
                            </motion.div>
                          );
                        })
                      )}
                    </div>
                  </motion.div>
                ))}
              </div>
            </div>
          )}

          {/* Sección de Facturas Aprobadas */}
          {approvedGroups.length > 0 && (
            <div className="space-y-4">
              <div className="flex items-center gap-2 pb-2 border-b-2 border-green-500">
                <div className="w-3 h-3 rounded-full bg-green-500"></div>
                <h3 className="text-lg font-bold text-neutral-950">
                  Facturas Aprobadas
                </h3>
                <span className="text-sm text-gray-600">
                  ({approvedGroups.length} {approvedGroups.length === 1 ? 'grupo' : 'grupos'})
                </span>
              </div>
              
              <div className="space-y-6">
                {approvedGroups.map((group, groupIndex) => (
                  <motion.div
                    key={`approved-${group.month}`}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: (pendingGroups.length + groupIndex) * 0.1 }}
                    className="space-y-3"
                  >
                    <div className="flex items-center justify-between">
                      <h4 className="text-base font-semibold text-neutral-950">
                        {group.month}
                      </h4>
                      <div className="text-xl font-bold text-neutral-950">
                        ${group.total.toFixed(2)}
                      </div>
                    </div>

                    <div className="flex items-center gap-2 mb-3">
                      <span className="text-sm text-gray-600">
                        {group.count} gastos
                      </span>
                      <span className={`text-xs font-semibold px-2 py-1 rounded ${getStatusColor(group.status)}`}>
                        {group.status}
                      </span>
                    </div>

                    <div className="bg-green-50 border border-green-200 rounded-lg p-3 flex flex-col sm:flex-row sm:items-center sm:justify-between mb-3 gap-3 sm:gap-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <Calendar className="w-4 h-4 text-green-600" />
                        <span className="text-sm text-green-900">
                          Estado del reporte:
                        </span>
                        <span className={`text-sm font-semibold px-2 py-0.5 rounded ${getStatusColor(group.status)}`}>
                          {group.status}
                        </span>
                        <span className="text-xs text-green-700 font-medium ml-2">
                          (Solo visualización)
                        </span>
                      </div>
                      <div className="flex items-center gap-2 self-end sm:self-center">
                        <Button
                          variant="ghost"
                          size="sm"
                          className="text-purple-600 hover:text-purple-700 hover:bg-purple-100"
                          onClick={() => {
                            setSelectedGroup(group);
                            setIsModalOpen(true);
                          }}
                        >
                          <BarChart2 className="w-4 h-4 mr-1" />
                          Resumen
                        </Button>
                      </div>
                    </div>

                    <div className="space-y-2">
                      {group.expenses.length === 0 ? (
                        <div className="bg-gray-50 border border-gray-200 rounded-lg p-6 text-center">
                          <p className="text-sm text-gray-600">
                            No hay gastos en esta categoría para este período.
                          </p>
                        </div>
                      ) : (
                        group.expenses.map((expense, expenseIndex) => {
                          const IconComponent = getIconComponent(expense.icon);
                          return (
                            <motion.div
                              key={expense.id}
                              initial={{ opacity: 0, x: -20 }}
                              animate={{ opacity: 1, x: 0 }}
                              transition={{ delay: ((pendingGroups.length + groupIndex) * 0.1) + (expenseIndex * 0.05) }}
                              className="bg-white border border-green-200 rounded-lg p-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 sm:gap-2 hover:shadow-md transition-shadow opacity-90"
                            >
                              <div className="flex items-center gap-3 w-full">
                                <div className="w-10 h-10 bg-green-50 rounded-lg flex items-center justify-center">
                                  <IconComponent className="w-5 h-5 text-green-600 flex-shrink-0" />
                                </div>
                                <div>
                                  <div className="text-sm font-medium text-neutral-950">
                                    {expense.vendorName} - {expense.concept}
                                  </div>
                                  <div className="text-xs text-gray-500 mb-1">
                                    {expense.category}
                                    {expense.costCenterName && (
                                      <span className="ml-2 text-blue-600">
                                        • {expense.costCenterName}
                                      </span>
                                    )}
                                  </div>
                                  <div className="text-xs text-gray-500">
                                    {expense.invoiceDate} • {expense.currency}
                                  </div>
                                </div>
                              </div>

                              <div className="flex items-center justify-between w-full sm:w-auto sm:gap-4">
                                <div className="text-left sm:text-right">
                                  <div className="text-base font-semibold text-neutral-950">
                                    ${expense.totalAmount.toFixed(2)}
                                  </div>
                                  <div className="text-xs text-green-600 font-medium">
                                    {expense.status}
                                  </div>
                                </div>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  className="text-gray-600 hover:text-gray-900 -mr-2"
                                  onClick={() => {
                                    setSelectedExpenseForEdit(expense);
                                    // Solo permitir edición si el estado de la factura es BORRADOR
                                    setIsViewOnlyMode(expense.status.toUpperCase() !== 'BORRADOR');
                                    setIsEditModalOpen(true);
                                  }}
                                  title={expense.status.toUpperCase() === 'BORRADOR' ? 'Editar factura' : 'Visualizar factura (Solo lectura)'}
                                >
                                  <Eye className="w-4 h-4" />
                                </Button>
                              </div>
                            </motion.div>
                          );
                        })
                      )}
                    </div>
                  </motion.div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Modal de Dashboard de Resumen */}
      {selectedGroup && (
        <ExpenseDashboardModal
          isOpen={isModalOpen}
          onClose={() => {
            setIsModalOpen(false);
            setSelectedGroup(null);
          }}
          expenses={selectedGroup.expenses}
          month={selectedGroup.month}
          total={selectedGroup.total}
        />
      )}

      {/* Modal de Edición de Factura */}
      <EditInvoiceModal
        isOpen={isEditModalOpen}
        onClose={() => {
          setIsEditModalOpen(false);
          setSelectedExpenseForEdit(null);
          setIsViewOnlyMode(false);
        }}
        expenseData={selectedExpenseForEdit}
        viewOnly={isViewOnlyMode}
        onSuccess={() => {
          // Recargar los gastos después de una actualización exitosa
          const fetchExpenses = async () => {
            if (!cardId || cardId === '') return;
            
            try {
              const expenses = await ApiService.getCardExpenses(parseInt(cardId));
              setExpenseGroups(expenses);
            } catch (err) {
              console.error('Error refreshing expenses:', err);
            }
          };
          fetchExpenses();
        }}
      />
    </div>
  );
};
