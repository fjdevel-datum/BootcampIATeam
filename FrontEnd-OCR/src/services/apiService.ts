import { User, Card, Country, Category, CostCenter, InvoiceRequest, InvoiceResponse, InvoiceFieldRequest, CompleteInvoiceRequest, UpdateInvoiceRequest, ExpenseGroup } from '../types/api';

const BASE_URL = '/api';

export class ApiService {
  /**
   * Fetch all users
   * @returns Promise<User[]> - Array of all users
   */
  static async getAllUsers(): Promise<User[]> {
    try {
      const response = await fetch(`${BASE_URL}/users`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error fetching users: ${response.status} ${response.statusText}`);
      }

      const usersData: User[] = await response.json();
      return usersData;
    } catch (error) {
      console.error('Error fetching users:', error);
      throw error;
    }
  }

  /**
   * Fetch user data by ID
   * @param userId - The ID of the user to fetch
   * @returns Promise<User> - User data
   */
  static async getUserById(userId: number): Promise<User> {
    try {
      const response = await fetch(`${BASE_URL}/users/${userId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error fetching user: ${response.status} ${response.statusText}`);
      }

      const userData: User = await response.json();
      return userData;
    } catch (error) {
      console.error('Error fetching user data:', error);
      throw error;
    }
  }

  /**
   * Fetch user cards by user ID
   * @param userId - The ID of the user whose cards to fetch
   * @returns Promise<Card[]> - Array of user cards
   */
  static async getUserCards(userId: number): Promise<Card[]> {
    try {
      const response = await fetch(`${BASE_URL}/cards/user/${userId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error fetching user cards: ${response.status} ${response.statusText}`);
      }

      const cardsData: Card[] = await response.json();
      return cardsData;
    } catch (error) {
      console.error('Error fetching user cards:', error);
      throw error;
    }
  }

  /**
   * Fetch all countries
   * @returns Promise<Country[]> - Array of countries
   */
  static async getCountries(): Promise<Country[]> {
    try {
      const response = await fetch(`${BASE_URL}/countries`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error fetching countries: ${response.status} ${response.statusText}`);
      }

      const countriesData: Country[] = await response.json();
      return countriesData;
    } catch (error) {
      console.error('Error fetching countries:', error);
      throw error;
    }
  }

  /**
   * Fetch all active categories
   * @returns Promise<Category[]> - Array of active categories
   */
  static async getCategories(): Promise<Category[]> {
    try {
      const response = await fetch(`${BASE_URL}/categories`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error fetching categories: ${response.status} ${response.statusText}`);
      }

      const categoriesData: Category[] = await response.json();
      // Filter only active categories
      return categoriesData.filter(category => category.isActive);
    } catch (error) {
      console.error('Error fetching categories:', error);
      throw error;
    }
  }

  /**
   * Fetch all active cost centers
   * @returns Promise<CostCenter[]> - Array of active cost centers
   */
  static async getCostCenters(): Promise<CostCenter[]> {
    try {
      const response = await fetch(`${BASE_URL}/cost-centers`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error fetching cost centers: ${response.status} ${response.statusText}`);
      }

      const costCentersData: CostCenter[] = await response.json();
      // Filter only active cost centers
      return costCentersData.filter(costCenter => costCenter.isActive);
    } catch (error) {
      console.error('Error fetching cost centers:', error);
      throw error;
    }
  }

  /**
   * Create a new invoice
   * @param invoiceData - Invoice data to create
   * @returns Promise<InvoiceResponse> - Created invoice response
   */
  static async createInvoice(invoiceData: InvoiceRequest): Promise<InvoiceResponse> {
    try {
      const response = await fetch(`${BASE_URL}/invoices`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(invoiceData),
      });

      if (!response.ok) {
        throw new Error(`Error creating invoice: ${response.status} ${response.statusText}`);
      }

      const invoiceResponse: InvoiceResponse = await response.json();
      return invoiceResponse;
    } catch (error) {
      console.error('Error creating invoice:', error);
      throw error;
    }
  }

  /**
   * Create invoice field details
   * @param invoiceFieldData - Invoice field data to create
   * @returns Promise<void> - Success response
   */
  static async createInvoiceField(invoiceFieldData: InvoiceFieldRequest): Promise<void> {
    try {
      const response = await fetch(`${BASE_URL}/invoice-fields`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(invoiceFieldData),
      });

      if (!response.ok) {
        throw new Error(`Error creating invoice field: ${response.status} ${response.statusText}`);
      }
    } catch (error) {
      console.error('Error creating invoice field:', error);
      throw error;
    }
  }

  /**
   * Create complete invoice with all data in a single request
   * @param completeInvoiceData - Complete invoice data to create
   * @returns Promise<InvoiceResponse> - Created invoice response
   */
  static async createCompleteInvoice(completeInvoiceData: CompleteInvoiceRequest): Promise<InvoiceResponse> {
    try {
      const response = await fetch(`${BASE_URL}/invoices/complete`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(completeInvoiceData),
      });

      if (!response.ok) {
        throw new Error(`Error creating complete invoice: ${response.status} ${response.statusText}`);
      }

      const invoiceResponse: InvoiceResponse = await response.json();
      return invoiceResponse;
    } catch (error) {
      console.error('Error creating complete invoice:', error);
      throw error;
    }
  }

  /**
   * Update complete invoice with all data in a single request
   * @param updateInvoiceData - Complete invoice data to update
   * @returns Promise<InvoiceResponse> - Updated invoice response
   */
  static async updateCompleteInvoice(updateInvoiceData: UpdateInvoiceRequest): Promise<InvoiceResponse> {
    try {
      const response = await fetch(`${BASE_URL}/invoices/complete`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updateInvoiceData),
      });

      if (!response.ok) {
        throw new Error(`Error updating invoice: ${response.status} ${response.statusText}`);
      }

      const invoiceResponse: InvoiceResponse = await response.json();
      return invoiceResponse;
    } catch (error) {
      console.error('Error updating invoice:', error);
      throw error;
    }
  }

  /**
   * Get expenses for a specific card
   * @param cardId - The ID of the card to get expenses for
   * @returns Promise<ExpenseGroup[]> - Array of expense groups
   */
  static async getCardExpenses(cardId: number): Promise<ExpenseGroup[]> {
    try {
      const response = await fetch(`${BASE_URL}/cards/${cardId}/expenses`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error fetching card expenses: ${response.status} ${response.statusText}`);
      }

      const expensesData: ExpenseGroup[] = await response.json();
      return expensesData;
    } catch (error) {
      console.error('Error fetching card expenses:', error);
      throw error;
    }
  }

  /**
   * Approve expense group for a specific card and month/year
   * @param cardId - The ID of the card
   * @param monthYear - The month and year of the expense group (e.g., "October 2025")
   * @returns Promise<void> - Success response
   */
  static async approveExpenseGroup(cardId: number, monthYear: string): Promise<void> {
    try {
      const response = await fetch(`${BASE_URL}/cards/${cardId}/expenses/approve?monthYear=${encodeURIComponent(monthYear)}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error approving expense group: ${response.status} ${response.statusText}`);
      }
    } catch (error) {
      console.error('Error approving expense group:', error);
      throw error;
    }
  }

  /**
   * Update invoice fields for a given invoice ID
   * TODO: Implementar cuando el backend esté listo
   * @param invoiceId - The ID of the invoice to update
   * @param data - Partial or full invoice field payload
   */
  static async updateInvoiceFields(invoiceId: number, data: InvoiceFieldRequest): Promise<void> {
    // TODO: Implementar endpoint de actualización
    console.log('updateInvoiceFields - Pendiente de implementación:', { invoiceId, data });
    throw new Error('Funcionalidad de actualización pendiente de implementación en el backend');
  }
}