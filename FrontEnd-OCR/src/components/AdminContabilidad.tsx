import React, { useState, useEffect, useRef } from 'react';
import { motion } from 'framer-motion';
import { Search, CheckCircle, Eye, CreditCard, ChevronDown, ChevronUp, Download } from 'lucide-react';
import { ApiService } from '../services/apiService';
import { User, ExpenseGroup, Card } from '../types/api';
import { Input } from './ui/input';
import { Button } from './ui/button';
import { AdminReportDetailModal } from './AdminReportDetailModal';
import { ResultModal } from './ResultModal';
import { useNotification } from '../hooks/useNotification';
import { exportReportToExcel } from '../utils/excelExport';

interface CardWithReports {
  card: Card;
  reports: ExpenseGroup[];
}

export const AdminContabilidad: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [cardReports, setCardReports] = useState<CardWithReports[]>([]);
  const [expandedCards, setExpandedCards] = useState<Set<number>>(new Set());
  const [searchTerm, setSearchTerm] = useState('');
  const [loadingUsers, setLoadingUsers] = useState(false);
  const [loadingReports, setLoadingReports] = useState(false);
  const [approvingReports, setApprovingReports] = useState<Set<string>>(new Set());
  const [selectedReport, setSelectedReport] = useState<ExpenseGroup | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const fetchingUsersRef = useRef(false);
  const fetchingReportsRef = useRef(false);
  
  // Hook de notificación
  const { notificationState, showNotification, hideNotification } = useNotification();

  useEffect(() => {
    const fetchUsers = async () => {
      if (fetchingUsersRef.current) return;

      fetchingUsersRef.current = true;
      setLoadingUsers(true);

      try {
        const usersData = await ApiService.getAllUsers();
        setUsers(usersData);
      } catch (error) {
        console.error('Error fetching users:', error);
        setUsers([]);
      } finally {
        setLoadingUsers(false);
        fetchingUsersRef.current = false;
      }
    };

    fetchUsers();
  }, []);

  const fetchUserReports = async (userId: number) => {
    if (fetchingReportsRef.current) return;

    fetchingReportsRef.current = true;
    setLoadingReports(true);
    setExpandedCards(new Set()); // Reset expanded cards

    try {
      const cards = await ApiService.getUserCards(userId);

      const cardsWithReports: CardWithReports[] = [];
      for (const card of cards) {
        const cardExpenses = await ApiService.getCardExpenses(card.id);
        cardsWithReports.push({
          card,
          reports: cardExpenses
        });
      }

      setCardReports(cardsWithReports);
      // Expandir todas las tarjetas por defecto
      setExpandedCards(new Set(cardsWithReports.map(cr => cr.card.id)));
    } catch (error) {
      console.error('Error fetching user reports:', error);
      setCardReports([]);
    } finally {
      setLoadingReports(false);
      fetchingReportsRef.current = false;
    }
  };

  const toggleCardExpansion = (cardId: number) => {
    setExpandedCards(prev => {
      const newSet = new Set(prev);
      if (newSet.has(cardId)) {
        newSet.delete(cardId);
      } else {
        newSet.add(cardId);
      }
      return newSet;
    });
  };

  const handleUserSelect = async (user: User) => {
    setSelectedUser(user);
    await fetchUserReports(user.id);
  };

  const handleApproveReport = async (report: ExpenseGroup, cardId: number) => {
    // Crear una key única para este reporte
    const reportKey = `${cardId}-${report.month}`;
    
    // Verificar si ya está en proceso de aprobación
    if (approvingReports.has(reportKey)) {
      return;
    }
    
    // Agregar a la lista de reportes en proceso
    setApprovingReports(prev => new Set(prev).add(reportKey));
    
    try {
      // Llamar al endpoint de aprobación
      await ApiService.approveExpenseGroup(cardId, report.month);
      
      // Mostrar notificación de éxito
      showNotification('success');
      
      // Recargar los reportes del usuario para reflejar los cambios
      if (selectedUser) {
        await fetchUserReports(selectedUser.id);
      }
    } catch (error) {
      console.error('Error approving report:', error);
      showNotification('error');
    } finally {
      // Remover de la lista de reportes en proceso
      setApprovingReports(prev => {
        const newSet = new Set(prev);
        newSet.delete(reportKey);
        return newSet;
      });
    }
  };

  const handleViewDetail = (report: ExpenseGroup) => {
    setSelectedReport(report);
    setIsDetailModalOpen(true);
  };

  const handleExportReport = (report: ExpenseGroup, card: Card) => {
    if (!selectedUser) return;

    exportReportToExcel({
      userName: selectedUser.name,
      cardNumber: card.maskedCardNumber,
      cardHolder: card.holderName,
      bank: card.issuerBank,
      report: report
    });
  };

  const filteredUsers = users.filter(user =>
    user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-neutral-950 mb-2">Contabilidad</h1>
        <p className="text-gray-600">
          Gestiona y aprueba los reportes de gastos de los usuarios
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1 bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-4 border-b border-gray-200">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
              <Input
                placeholder="Buscar usuario..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>

          <div className="overflow-y-auto max-h-[calc(100vh-20rem)]">
            {loadingUsers ? (
              <div className="p-8 text-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[#f23030] mx-auto mb-4"></div>
                <p className="text-sm text-gray-600">Cargando usuarios...</p>
              </div>
            ) : filteredUsers.length === 0 ? (
              <div className="p-8 text-center">
                <p className="text-sm text-gray-600">No se encontraron usuarios</p>
              </div>
            ) : (
              filteredUsers.map((user) => (
                <button
                  key={user.id}
                  onClick={() => handleUserSelect(user)}
                  className={`w-full text-left p-4 border-b border-gray-100 hover:bg-gray-50 transition-colors ${
                    selectedUser?.id === user.id ? 'bg-blue-50 border-l-4 border-l-[#f23030]' : ''
                  }`}
                >
                  <p className="font-medium text-neutral-950">{user.name}</p>
                  <p className="text-sm text-gray-600">{user.email}</p>
                </button>
              ))
            )}
          </div>
        </div>

        <div className="lg:col-span-2 bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-6 border-b border-gray-200">
            <h2 className="text-xl font-semibold text-neutral-950">
              {selectedUser ? `Reportes de ${selectedUser.name}` : 'Reportes'}
            </h2>
          </div>

          <div className="p-6">
            {!selectedUser ? (
              <div className="text-center py-12">
                <p className="text-gray-600">Selecciona un usuario para ver sus reportes</p>
              </div>
            ) : loadingReports ? (
              <div className="text-center py-12">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[#f23030] mx-auto mb-4"></div>
                <p className="text-sm text-gray-600">Cargando reportes...</p>
              </div>
            ) : cardReports.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-600">Sin datos</p>
                <p className="text-sm text-gray-500 mt-2">
                  Este usuario no tiene reportes disponibles
                </p>
              </div>
            ) : (
              <div className="space-y-4">
                {cardReports.map((cardReport, cardIndex) => {
                  const isExpanded = expandedCards.has(cardReport.card.id);
                  const totalReports = cardReport.reports.length;
                  const totalAmount = cardReport.reports.reduce((sum, report) => sum + report.total, 0);
                  
                  return (
                    <motion.div
                      key={cardReport.card.id}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: cardIndex * 0.1 }}
                      className="border border-gray-200 rounded-lg overflow-hidden"
                    >
                      {/* Card Header */}
                      <button
                        onClick={() => toggleCardExpansion(cardReport.card.id)}
                        className="w-full bg-gradient-to-r from-gray-50 to-gray-100 hover:from-gray-100 hover:to-gray-200 p-4 flex items-center justify-between transition-colors"
                      >
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 bg-white rounded-lg flex items-center justify-center shadow-sm">
                            <CreditCard className="w-5 h-5 text-[#f23030]" />
                          </div>
                          <div className="text-left">
                            <div className="flex items-center gap-2">
                              <h3 className="text-base font-semibold text-neutral-950">
                                {cardReport.card.maskedCardNumber}
                              </h3>
                              <span className="text-xs px-2 py-0.5 bg-blue-100 text-blue-800 rounded-full font-medium">
                                {cardReport.card.cardType}
                              </span>
                            </div>
                            <p className="text-sm text-gray-600 mt-0.5">
                              {cardReport.card.holderName} • {cardReport.card.issuerBank}
                            </p>
                          </div>
                        </div>
                        <div className="flex items-center gap-4">
                          <div className="text-right">
                            <p className="text-xs text-gray-500">Total reportes</p>
                            <p className="text-sm font-semibold text-neutral-950">{totalReports}</p>
                          </div>
                          <div className="text-right">
                            <p className="text-xs text-gray-500">Total gastos</p>
                            <p className="text-base font-bold text-[#f23030]">${totalAmount.toFixed(2)}</p>
                          </div>
                          {isExpanded ? (
                            <ChevronUp className="w-5 h-5 text-gray-400" />
                          ) : (
                            <ChevronDown className="w-5 h-5 text-gray-400" />
                          )}
                        </div>
                      </button>

                      {/* Card Reports Table */}
                      {isExpanded && (
                        <motion.div
                          initial={{ opacity: 0, height: 0 }}
                          animate={{ opacity: 1, height: 'auto' }}
                          exit={{ opacity: 0, height: 0 }}
                          transition={{ duration: 0.3 }}
                          className="overflow-hidden"
                        >
                          {cardReport.reports.length === 0 ? (
                            <div className="p-8 text-center bg-gray-50">
                              <p className="text-sm text-gray-600">
                                No hay reportes para esta tarjeta
                              </p>
                            </div>
                          ) : (
                            <div className="overflow-x-auto">
                              <table className="w-full">
                                <thead className="bg-gray-50 border-y border-gray-200">
                                  <tr>
                                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                      Periodo
                                    </th>
                                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                      Gastos
                                    </th>
                                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                      Total
                                    </th>
                                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                      Estado
                                    </th>
                                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                      Acciones
                                    </th>
                                  </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                  {cardReport.reports.map((report, reportIndex) => {
                                    const reportKey = `${cardReport.card.id}-${report.month}`;
                                    const isApproving = approvingReports.has(reportKey);
                                    
                                    return (
                                      <motion.tr
                                        key={`${report.month}-${reportIndex}`}
                                        initial={{ opacity: 0, x: -10 }}
                                        animate={{ opacity: 1, x: 0 }}
                                        transition={{ delay: reportIndex * 0.05 }}
                                        className="hover:bg-gray-50"
                                      >
                                        <td className="px-4 py-4 whitespace-nowrap text-sm text-neutral-950">
                                          {report.month}
                                        </td>
                                        <td className="px-4 py-4 whitespace-nowrap text-sm text-gray-600">
                                          {report.count} {report.count === 1 ? 'gasto' : 'gastos'}
                                        </td>
                                        <td className="px-4 py-4 whitespace-nowrap text-sm font-semibold text-neutral-950">
                                          ${report.total.toFixed(2)}
                                        </td>
                                        <td className="px-4 py-4 whitespace-nowrap">
                                          <span
                                            className={`px-3 py-1 text-xs font-semibold rounded-full ${
                                              report.status.toUpperCase() === 'APROBADO'
                                                ? 'bg-green-100 text-green-800'
                                                : report.status.toUpperCase() === 'PENDIENTE'
                                                ? 'bg-yellow-100 text-yellow-800'
                                                : 'bg-red-100 text-red-800'
                                            }`}
                                          >
                                            {report.status}
                                          </span>
                                        </td>
                                        <td className="px-4 py-4 whitespace-nowrap">
                                          <div className="flex items-center gap-2">
                                            <Button
                                              size="sm"
                                              onClick={() => handleApproveReport(report, cardReport.card.id)}
                                              className="bg-green-600 hover:bg-green-700 text-white text-xs"
                                              disabled={report.status.toUpperCase() === 'APROBADO' || isApproving}
                                            >
                                              {isApproving ? (
                                                <>
                                                  <span className="animate-spin mr-1">⏳</span>
                                                  Aprobando...
                                                </>
                                              ) : (
                                                <>
                                                  <CheckCircle className="w-3 h-3 mr-1" />
                                                  Aprobar
                                                </>
                                              )}
                                            </Button>
                                            <Button
                                              size="sm"
                                              variant="outline"
                                              onClick={() => handleViewDetail(report)}
                                              className="text-xs"
                                            >
                                              <Eye className="w-3 h-3 mr-1" />
                                              Ver detalle
                                            </Button>
                                            {report.status.toUpperCase() === 'APROBADO' && (
                                              <Button
                                                size="sm"
                                                variant="outline"
                                                onClick={() => handleExportReport(report, cardReport.card)}
                                                className="text-xs text-blue-600 border-blue-300 hover:bg-blue-50"
                                              >
                                                <Download className="w-3 h-3 mr-1" />
                                                Excel
                                              </Button>
                                            )}
                                          </div>
                                        </td>
                                      </motion.tr>
                                    );
                                  })}
                                </tbody>
                              </table>
                            </div>
                          )}
                        </motion.div>
                      )}
                    </motion.div>
                  );
                })}
              </div>
            )}
          </div>
        </div>
      </div>

      {selectedReport && (
        <AdminReportDetailModal
          isOpen={isDetailModalOpen}
          onClose={() => {
            setIsDetailModalOpen(false);
            setSelectedReport(null);
          }}
          report={selectedReport}
        />
      )}

      {/* Notificación Toast */}
      <ResultModal
        isOpen={notificationState.isOpen}
        onClose={hideNotification}
        type={notificationState.type}
      />
    </div>
  );
};
