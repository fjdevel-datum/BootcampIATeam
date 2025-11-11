import * as XLSX from 'xlsx';
import { ExpenseGroup } from '../types/api';

interface ExcelExportData {
  userName: string;
  cardNumber: string;
  cardHolder: string;
  bank: string;
  report: ExpenseGroup;
}

/**
 * Genera y descarga un archivo Excel con los detalles del reporte de gastos aprobado
 */
export const exportReportToExcel = (data: ExcelExportData): void => {
  const { userName, cardNumber, cardHolder, bank, report } = data;

  // Crear el workbook
  const wb = XLSX.utils.book_new();

  // Preparar datos del resumen
  const summaryData = [
    ['REPORTE DE GASTOS APROBADO'],
    [],
    ['Usuario:', userName],
    ['Tarjeta:', cardNumber],
    ['Titular:', cardHolder],
    ['Banco:', bank],
    ['Período:', report.month],
    ['Estado:', report.status],
    ['Total de Gastos:', report.count],
    ['Monto Total:', `$${report.total.toFixed(2)}`],
    [],
  ];

  // Preparar datos de los gastos
  const expensesHeader = [
    'Fecha',
    'Proveedor',
    'Concepto',
    'Categoría',
    'Centro de Costo',
    'Cliente Visitado',
    'Moneda',
    'Monto',
    'Estado',
    'Notas'
  ];

  const expensesData = report.expenses.map(expense => [
    expense.invoiceDate,
    expense.vendorName,
    expense.concept,
    expense.category,
    expense.costCenterName || '-',
    expense.clientVisited || '-',
    expense.currency,
    expense.totalAmount,
    expense.status,
    expense.notes || '-'
  ]);

  // Combinar resumen y gastos
  const allData = [
    ...summaryData,
    ['DETALLE DE GASTOS'],
    [],
    expensesHeader,
    ...expensesData,
    [],
    [],
    ['', '', '', '', '', '', 'TOTAL:', report.total.toFixed(2)]
  ];

  // Crear worksheet
  const ws = XLSX.utils.aoa_to_sheet(allData);

  // Definir anchos de columna
  ws['!cols'] = [
    { wch: 12 },  // Fecha
    { wch: 25 },  // Proveedor
    { wch: 30 },  // Concepto
    { wch: 20 },  // Categoría
    { wch: 20 },  // Centro de Costo
    { wch: 20 },  // Cliente Visitado
    { wch: 10 },  // Moneda
    { wch: 12 },  // Monto
    { wch: 12 },  // Estado
    { wch: 30 }   // Notas
  ];

  // Aplicar estilos al título
  if (ws['A1']) {
    ws['A1'].s = {
      font: { bold: true, sz: 16 },
      alignment: { horizontal: 'center' }
    };
  }

  // Merge cells para el título
  ws['!merges'] = [
    { s: { r: 0, c: 0 }, e: { r: 0, c: 9 } }  // Merge A1:J1
  ];

  // Agregar worksheet al workbook
  XLSX.utils.book_append_sheet(wb, ws, 'Reporte de Gastos');

  // Generar nombre de archivo
  const fileName = `Reporte_${report.month.replace(/\s+/g, '_')}_${cardNumber.replace(/\s+/g, '')}_${new Date().getTime()}.xlsx`;

  // Escribir y descargar el archivo
  XLSX.writeFile(wb, fileName);
};


