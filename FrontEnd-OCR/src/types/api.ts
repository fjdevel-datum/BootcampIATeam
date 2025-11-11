export interface Country {
  id: number;
  isoCode: string;
  name: string;
}

export interface Company {
  id: number;
  name: string;
  country: Country;
  address: string;
}

export interface User {
  id: number;
  email: string;
  name: string;
  keycloakId: string;
  role: string;
  company: Company;
  country: Country;
  status: string;
  createdAt: string;
  updatedAt: string;
  active: boolean;
  admin: boolean;
  roleDisplayName: string;
  statusDisplayName: string;
}

export interface Card {
  id: number;
  maskedCardNumber: string;
  holderName: string;
  cardType: string;
  expirationDate: string;
  issuerBank: string;
  creditLimit: number;
  status: string;
  description: string;
  userName: string;
  companyName: string;
  companyId: number;
  createdAt: string;
  updatedAt: string;
}

export interface Category {
  id: number;
  name: string;
  description: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CostCenter {
  id: number;
  code: string;
  name: string;
  description: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

// OCR Service Types
export interface InvoiceData {
  vendorName: string;
  invoiceDate: string;
  totalAmount: string;
  currency: string;
}

export interface OcrResponse {
  status: string;
  ocr_text: string;
  invoice_data: InvoiceData;
  processing_time_ms: number;
  error_message: string;
}

export interface OcrServiceResult {
  success: boolean;
  data?: InvoiceData;
  ocrText?: string;
  processingTime?: number;
  error?: string;
}

// Invoice API Types
export interface InvoiceRequest {
  userId: number;
  cardId: number;
  companyId: number;
  countryId: number;
  path: string;
  fileName: string;
}

export interface InvoiceResponse {
  id: number;
  userName: string;
  cardMaskedNumber: string;
  companyName: string;
  countryName: string;
  path: string;
  fileName: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface InvoiceFieldRequest {
  invoiceId: number;
  vendorName: string;
  invoiceDate: string;
  totalAmount: number;
  currency: string;
  concept: string;
  categoryId: number;
  costCenterId: number | null;
  countryId?: number | null; // optional: include when updating invoice's country
  clientVisited: string;
  notes: string;
}

// Complete invoice request for single endpoint
export interface CompleteInvoiceRequest {
  userId: number;
  companyId: number;
  countryId: number;
  cardId: number;
  path: string;
  fileName: string;
  vendorName: string;
  invoiceDate: string; // Format: "2022-03-10"
  totalAmount: number;
  currency: string;
  concept: string;
  categoryId: number;
  costCenterId: number | null; // Use 0 if no cost center is selected
  clientVisited: string;
  notes: string;
}

// Update invoice request
export interface UpdateInvoiceRequest {
  idInvoice: number;
  id: number; // Expense ID
  countryId: number;
  vendorName: string;
  invoiceDate: string; // Format: "2022-03-10"
  totalAmount: number;
  currency: string;
  concept: string;
  categoryId: number;
  costCenterId: number | null;
  clientVisited: string;
  notes: string;
}

// Expense API Types
export interface Expense {
  id: number;
  idInvoice: number;
  vendorName: string;
  concept: string;
  category: string;
  invoiceDate: string;
  totalAmount: number;
  currency: string;
  categoryId: number;
  costCenterId: number | null;
  countryId?: number | null;
  costCenterName: string | null;
  clientVisited: string | null;
  notes: string | null;
  status: string;
  icon: string;
  path?: string; // OpenKM document path
  fileName?: string; // OpenKM file name
}

export interface ExpenseGroup {
  month: string;
  total: number;
  count: number;
  status: string;
  expenses: Expense[];
}

// Image Upload Types
export interface ImageUploadRequest {
  fileName: string;
  destinationPath: string;
  imageData: string; // Base64 string
  description: string;
  mimeType: string;
}

export interface ImageUploadResponse {
  documentId: string;
  fileName: string;
  path: string;
  size: number;
  mimeType: string;
  uploadDate: string;
  message: string;
  success: boolean;
}