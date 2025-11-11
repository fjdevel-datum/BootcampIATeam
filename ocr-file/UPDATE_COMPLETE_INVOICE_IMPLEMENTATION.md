# Implementación de UpdateCompleteInvoice

## Descripción
Se ha implementado la funcionalidad para actualizar una factura completa (Invoice + InvoiceField) en una sola transacción atómica.

## Archivos Creados

### 1. UpdateCompleteInvoiceRequest.java
**Ubicación:** `src/main/java/com/datum/redsoft/dto/request/UpdateCompleteInvoiceRequest.java`

**Campos:**
- `idInvoice` (Long) - ID del Invoice a actualizar
- `id` (Long) - ID del InvoiceField a actualizar
- `countryId` (Long, opcional) - ID del país (para actualizar Invoice)
- `vendorName` (String, opcional) - Nombre del proveedor
- `invoiceDate` (LocalDate, opcional) - Fecha de la factura
- `totalAmount` (BigDecimal, opcional) - Monto total (min: 0.01)
- `currency` (String, opcional) - Moneda
- `concept` (String, opcional) - Concepto
- `categoryId` (Long, opcional) - ID de la categoría
- `costCenterId` (Long, opcional) - ID del centro de costo
- `clientVisited` (String, opcional) - Cliente visitado
- `notes` (String, opcional) - Notas adicionales

**Campos Excluidos (NO se actualizan):**
- `path` - Ruta del archivo original
- `fileName` - Nombre del archivo
- `cardId` - ID de la tarjeta asociada
- `status` - Estado de la factura

## Modificaciones a Archivos Existentes

### 2. IInvoiceService.java
**Cambio:** Se agregó el método:
```java
/**
 * Actualiza una factura completa con InvoiceField en una sola transacción
 * No actualiza path, fileName, cardId ni status
 */
Optional<CompleteInvoiceResponseDTO> updateCompleteInvoice(UpdateCompleteInvoiceRequest request);
```

### 3. InvoiceService.java
**Cambios:**
- Se agregó el import: `import com.datum.redsoft.dto.request.UpdateCompleteInvoiceRequest;`
- Se implementó el método `updateCompleteInvoice()` con las siguientes características:
  - Anotación `@Transactional` para asegurar atomicidad
  - Validación de existencia de Invoice e InvoiceField
  - Actualización de campos de Invoice (solo countryId)
  - Actualización de campos de InvoiceField (todos los campos del request)
  - Validación de entidades relacionadas (Country, Category, CostCenter)
  - Actualización automática de timestamps (updatedAt)
  - Retorno de `Optional<CompleteInvoiceResponseDTO>`
  - Manejo de excepciones con rollback automático
  - Logging detallado de todas las operaciones

### 4. InvoiceController.java
**Cambios:**
- Se agregó el import: `import com.datum.redsoft.dto.request.UpdateCompleteInvoiceRequest;`
- Se agregó el endpoint PUT:

```java
/**
 * Actualiza una factura completa con campos en una sola transacción
 * No actualiza path, fileName, cardId ni status
 * PUT /api/invoices/complete
 */
@PUT
@Path("/complete")
public Response updateCompleteInvoice(@Valid UpdateCompleteInvoiceRequest request)
```

## Endpoint REST

### PUT /api/invoices/complete

**Request Body:**
```json
{
  "idInvoice": 123,
  "id": 456,
  "countryId": 2,
  "vendorName": "Proveedor Actualizado",
  "invoiceDate": "2024-01-15",
  "totalAmount": 1500.00,
  "currency": "USD",
  "concept": "Concepto actualizado",
  "categoryId": 3,
  "costCenterId": 4,
  "clientVisited": "Cliente XYZ",
  "notes": "Notas actualizadas"
}
```

**Respuestas:**

**200 OK - Actualización exitosa:**
```json
{
  "invoiceId": 123,
  "userName": "Juan Pérez",
  "cardMaskedNumber": "****1234",
  "companyName": "Empresa ABC",
  "countryName": "México",
  "path": "/files/invoice123.pdf",
  "fileName": "invoice123.pdf",
  "status": "APPROVED",
  "invoiceCreatedAt": "2024-01-01T10:00:00",
  "invoiceUpdatedAt": "2024-01-15T14:30:00",
  "invoiceFieldId": 456,
  "vendorName": "Proveedor Actualizado",
  "invoiceDate": "2024-01-15",
  "totalAmount": 1500.00,
  "currency": "USD",
  "concept": "Concepto actualizado",
  "categoryName": "Transporte",
  "costCenterName": "Centro de Costo Norte",
  "clientVisited": "Cliente XYZ",
  "notes": "Notas actualizadas",
  "fieldCreatedAt": "2024-01-01T10:00:00",
  "fieldUpdatedAt": "2024-01-15T14:30:00"
}
```

**400 Bad Request - Error de validación:**
```json
"Error de validación: País no encontrado con ID: 999"
```

**404 Not Found - Entidad no encontrada:**
```json
"Invoice o InvoiceField no encontrado con los IDs proporcionados"
```

**500 Internal Server Error - Error del servidor:**
```json
"Error interno del servidor al procesar la transacción"
```

## Características Principales

### 1. Transaccionalidad
- **@Transactional:** Garantiza que todas las operaciones se completen exitosamente o se reviertan en caso de error
- **Rollback automático:** Si ocurre cualquier excepción, todos los cambios se revierten
- **Atomicidad:** Los cambios en Invoice e InvoiceField se aplican juntos o no se aplican

### 2. Validaciones
- Verifica que el Invoice exista antes de actualizar
- Verifica que el InvoiceField exista antes de actualizar
- Valida que el Country exista si se proporciona countryId
- Valida que la Category exista si se proporciona categoryId
- Valida que el CostCenter exista si se proporciona costCenterId
- Validación de tipos de datos con Jakarta Validation (@NotNull, @DecimalMin)

### 3. Campos Protegidos
Los siguientes campos **NO** se actualizan para mantener la integridad de los datos:
- `path` - Ruta del archivo original (inmutable)
- `fileName` - Nombre del archivo (inmutable)
- `cardId` - Tarjeta asociada (inmutable)
- `status` - Estado de la factura (inmutable)

### 4. Actualizaciones Parciales
- Todos los campos son opcionales (excepto idInvoice e id)
- Solo se actualizan los campos que se proporcionen en el request
- Los campos no proporcionados mantienen sus valores actuales

### 5. Logging
- Log de inicio de operación con IDs
- Log de cada campo actualizado
- Log de éxito o error
- Log de validaciones fallidas

## Flujo de Ejecución

1. **Recepción del Request:** El controller recibe el request con validación de Jakarta
2. **Inicio de Transacción:** Se inicia una transacción de base de datos
3. **Validación de Invoice:** Se verifica que exista el Invoice con el ID proporcionado
4. **Validación de InvoiceField:** Se verifica que exista el InvoiceField con el ID proporcionado
5. **Actualización de Invoice:**
   - Si se proporciona countryId, se valida y actualiza el Country
   - Se actualiza el timestamp updatedAt
   - Se persiste el Invoice
6. **Actualización de InvoiceField:**
   - Se actualizan todos los campos proporcionados en el request
   - Si se proporciona categoryId, se valida y actualiza la Category
   - Si se proporciona costCenterId, se valida y actualiza el CostCenter
   - Se actualiza el timestamp updatedAt
   - Se persiste el InvoiceField
7. **Commit de Transacción:** Si todo es exitoso, se confirman los cambios
8. **Retorno de Respuesta:** Se retorna el CompleteInvoiceResponseDTO con todos los datos actualizados

## Manejo de Errores

### IllegalArgumentException
- Se lanza cuando una entidad relacionada no existe (Country, Category, CostCenter)
- Se retorna Optional.empty() para que el controller devuelva 404

### RuntimeException
- Se lanza para otros errores no esperados
- Provoca rollback automático de la transacción
- El controller devuelve 500 Internal Server Error

## Ejemplo de Uso

### Actualizar solo el país:
```json
{
  "idInvoice": 123,
  "id": 456,
  "countryId": 2
}
```

### Actualizar solo campos de InvoiceField:
```json
{
  "idInvoice": 123,
  "id": 456,
  "vendorName": "Nuevo Proveedor",
  "totalAmount": 2000.00,
  "notes": "Monto actualizado"
}
```

### Actualización completa:
```json
{
  "idInvoice": 123,
  "id": 456,
  "countryId": 2,
  "vendorName": "Proveedor Actualizado",
  "invoiceDate": "2024-01-15",
  "totalAmount": 1500.00,
  "currency": "USD",
  "concept": "Servicio de consultoría",
  "categoryId": 3,
  "costCenterId": 4,
  "clientVisited": "Cliente ABC",
  "notes": "Factura aprobada y lista para pago"
}
```

## Testing

Para probar el endpoint, puedes usar:

### cURL:
```bash
curl -X PUT http://localhost:8080/api/invoices/complete \
  -H "Content-Type: application/json" \
  -d '{
    "idInvoice": 123,
    "id": 456,
    "totalAmount": 1500.00
  }'
```

### Swagger UI:
1. Acceder a http://localhost:8080/q/swagger-ui/
2. Buscar el endpoint PUT /api/invoices/complete
3. Hacer clic en "Try it out"
4. Ingresar el JSON de request
5. Hacer clic en "Execute"

## Compilación

El proyecto compila exitosamente:
```
mvn compile
[INFO] BUILD SUCCESS
```

## Notas Importantes

1. **Transaccionalidad:** La anotación @Transactional asegura que si falla cualquier operación, todo se revierte
2. **Campos Opcionales:** Todos los campos excepto idInvoice e id son opcionales
3. **Campos Inmutables:** path, fileName, cardId y status NO se pueden actualizar mediante este endpoint
4. **Validación de Relaciones:** Se validan todas las entidades relacionadas antes de actualizar
5. **Timestamps:** Los campos updatedAt se actualizan automáticamente en ambas entidades
6. **Logging:** Todas las operaciones se registran para auditoría y debugging

## Comparación con CreateCompleteInvoice

| Característica | CreateCompleteInvoice | UpdateCompleteInvoice |
|----------------|----------------------|----------------------|
| IDs requeridos | No (se generan) | Sí (idInvoice e id) |
| Campos requeridos | userId, companyId, path, fileName, vendorName, etc. | Solo idInvoice e id |
| Crea entidades | Sí | No, solo actualiza |
| Actualiza path/fileName | Sí (al crear) | No (inmutables) |
| Actualiza cardId | Sí (al crear) | No (inmutable) |
| Actualiza status | Sí (al crear) | No (inmutable) |
| Método HTTP | POST /api/invoices/complete | PUT /api/invoices/complete |
| Transaccional | Sí | Sí |
| Retorna | CompleteInvoiceResponseDTO | Optional&lt;CompleteInvoiceResponseDTO&gt; |
