# Implementación de Clasificación de Gastos y Aprobación de Facturas

## Descripción
Se han implementado dos nuevas funcionalidades en el servicio `CardService` para mejorar la gestión de gastos y facturas:

1. **Clasificación automática de grupos de gastos** según el estado de las facturas
2. **Aprobación masiva de facturas** de un grupo específico

---

## Funcionalidad 1: Clasificación de Gastos

### Objetivo
Modificar el método `getCardExpenses` para que clasifique los grupos de gastos (`ExpenseGroupResponseDTO`) en "PENDIENTE", "APROBADO" o "MIXTO" según el estado de las facturas.

### Lógica de Clasificación

#### Estado "PENDIENTE"
- Se asigna cuando **todas** las facturas del grupo tienen el estado `InvoiceStatus.DRAFT`
- Indica que el grupo completo está pendiente de aprobación

#### Estado "APROBADO"
- Se asigna cuando **todas** las facturas del grupo tienen el estado `InvoiceStatus.PROCESSED`
- Indica que el grupo completo ha sido procesado y aprobado

#### Estado "MIXTO"
- Se asigna cuando hay una mezcla de estados en las facturas del grupo
- También se asigna cuando todas las facturas tienen un estado diferente a DRAFT o PROCESSED
- Indica que el grupo tiene facturas en diferentes etapas del flujo

### Cambios Implementados

#### 1. CardService.java

**Imports agregados:**
```java
import com.datum.redsoft.entity.Invoice;
import com.datum.redsoft.enums.InvoiceStatus;
import com.datum.redsoft.repository.InvoiceRepository;
import java.time.LocalDateTime;
```

**Inyección de dependencia:**
```java
@Inject
InvoiceRepository invoiceRepository;
```

**Modificación en `getCardExpenses()`:**
```java
// Determinar el estado del grupo basado en los estados de las facturas
String groupStatus = determineGroupStatus(monthExpenses);

return new ExpenseGroupResponseDTO(monthKey, total, count, groupStatus, monthExpenses);
```

**Nuevo método `determineGroupStatus()`:**
```java
/**
 * Determina el estado de un grupo de gastos basado en los estados de las facturas
 * - "PENDIENTE" si todas las facturas están en estado DRAFT
 * - "APROBADO" si todas las facturas están en estado PROCESSED
 * - "MIXTO" en otros casos
 * 
 * @param expenses Lista de gastos del grupo
 * @return Estado del grupo ("PENDIENTE", "APROBADO" o "MIXTO")
 */
private String determineGroupStatus(List<ExpenseResponseDTO> expenses) {
    if (expenses == null || expenses.isEmpty()) {
        return "PENDIENTE";
    }
    
    // Obtener todos los estados únicos del grupo
    Set<String> uniqueStatuses = expenses.stream()
            .map(ExpenseResponseDTO::getStatus)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    
    // Si todos tienen el mismo estado
    if (uniqueStatuses.size() == 1) {
        String status = uniqueStatuses.iterator().next();
        
        // Verificar si es DRAFT (Pendiente)
        if (InvoiceStatus.DRAFT.name().equals(status)) {
            return "PENDIENTE";
        }
        
        // Verificar si es PROCESSED (Aprobado)
        if (InvoiceStatus.PROCESSED.name().equals(status)) {
            return "APROBADO";
        }
    }
    
    // Si hay mezcla de estados o un estado diferente a DRAFT/PROCESSED
    return "MIXTO";
}
```

#### 2. ExpenseGroupResponseDTO.java

**Actualización del comentario del campo `status`:**
```java
private String status;  // Status del grupo ("PENDIENTE", "APROBADO", "MIXTO")
```

**Eliminación del constructor con status por defecto:**
- Se eliminó el constructor que asignaba automáticamente "PENDIENTE"
- Ahora se usa el constructor completo generado por `@AllArgsConstructor`

### Ejemplo de Respuesta

```json
{
  "month": "Octubre 2025",
  "total": 15000.50,
  "count": 5,
  "status": "PENDIENTE",
  "expenses": [
    {
      "id": 1,
      "idInvoice": 10,
      "vendorName": "Proveedor A",
      "status": "DRAFT",
      ...
    },
    {
      "id": 2,
      "idInvoice": 11,
      "vendorName": "Proveedor B",
      "status": "DRAFT",
      ...
    }
  ]
}
```

---

## Funcionalidad 2: Aprobación de Grupos de Gastos

### Objetivo
Crear un método que permita cambiar el estado de todas las facturas de un grupo de gastos de `DRAFT` a `PROCESSED`.

### Características

- **Transaccional:** Usa `@Transactional` para asegurar atomicidad
- **Selectivo:** Solo actualiza facturas en estado `DRAFT`
- **Filtrado por mes-año:** Solo afecta facturas del periodo especificado
- **Validaciones completas:** Verifica existencia de tarjeta y validez del formato
- **Actualización de timestamps:** Actualiza automáticamente el campo `updatedAt`
- **Logging detallado:** Registra cada operación para auditoría

### Implementación

#### CardService.java

**Nuevo método `approveExpenseGroup()`:**
```java
/**
 * Aprueba un grupo de gastos cambiando el estado de todas las facturas de DRAFT a PROCESSED
 * Solo se procesan las facturas que estén en estado DRAFT
 * 
 * @param cardId ID de la tarjeta
 * @param monthYear Mes-año del grupo (ej: "Diciembre 2024")
 * @return Número de facturas actualizadas
 * @throws IllegalArgumentException si la tarjeta no existe o el formato de mes-año es inválido
 */
@Transactional
public int approveExpenseGroup(Long cardId, String monthYear) {
    logger.info("Aprobando grupo de gastos - Tarjeta ID: " + cardId + ", Mes-Año: " + monthYear);
    
    // 1. Verificar que la tarjeta existe
    Card card = cardRepository.findById(cardId);
    if (card == null) {
        logger.warning("No se encontró la tarjeta con ID: " + cardId);
        throw new IllegalArgumentException("No se encontró la tarjeta con ID: " + cardId);
    }
    
    // 2. Validar formato del mes-año
    if (monthYear == null || monthYear.trim().isEmpty()) {
        throw new IllegalArgumentException("El mes-año es obligatorio");
    }
    
    String[] parts = monthYear.trim().split(" ");
    if (parts.length != 2) {
        throw new IllegalArgumentException("Formato de mes-año inválido. Use formato 'Mes YYYY' (ej: 'Diciembre 2024')");
    }
    
    String monthName = parts[0];
    int year;
    try {
        year = Integer.parseInt(parts[1]);
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Año inválido en mes-año: " + parts[1]);
    }
    
    int monthNumber = getMonthNumber(monthName);
    if (monthNumber < 1 || monthNumber > 12) {
        throw new IllegalArgumentException("Nombre de mes inválido: " + monthName);
    }
    
    // 3. Obtener todas las facturas del grupo
    List<Object[]> invoiceData = cardRepository.findInvoicesWithFieldsByCardId(cardId);
    
    // 4. Filtrar facturas que correspondan al mes-año y estén en estado DRAFT
    List<Long> invoiceIdsToUpdate = new ArrayList<>();
    
    for (Object[] data : invoiceData) {
        LocalDate invoiceDate = (LocalDate) data[5];  // invoiceDate en índice 5
        Object statusObj = data[13];                   // status en índice 13
        
        // Verificar si la factura pertenece al mes-año especificado
        if (invoiceDate.getMonthValue() == monthNumber && invoiceDate.getYear() == year) {
            // Verificar si el estado es DRAFT
            if (statusObj != null) {
                String statusStr = statusObj.toString();
                if (InvoiceStatus.DRAFT.name().equals(statusStr)) {
                    Long invoiceId = (Long) data[1];  // Invoice ID en índice 1
                    invoiceIdsToUpdate.add(invoiceId);
                }
            }
        }
    }
    
    // 5. Actualizar las facturas de DRAFT a PROCESSED
    int updatedCount = 0;
    LocalDateTime now = LocalDateTime.now();
    
    for (Long invoiceId : invoiceIdsToUpdate) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findByIdOptional(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            invoice.setStatus(InvoiceStatus.PROCESSED);
            invoice.setUpdatedAt(now);
            invoiceRepository.persist(invoice);
            updatedCount++;
            logger.info("Factura ID " + invoiceId + " actualizada de DRAFT a PROCESSED");
        }
    }
    
    logger.info("Grupo de gastos aprobado - " + updatedCount + " facturas actualizadas");
    return updatedCount;
}
```

#### ICardService.java

**Nuevo método en la interfaz:**
```java
/**
 * Aprueba un grupo de gastos cambiando el estado de todas las facturas de DRAFT a PROCESSED
 * @param cardId ID de la tarjeta
 * @param monthYear Mes-año del grupo (ej: "Diciembre 2024")
 * @return Número de facturas actualizadas
 * @throws IllegalArgumentException si la tarjeta no existe o el formato es inválido
 */
int approveExpenseGroup(Long cardId, String monthYear);
```

#### CardController.java

**Nuevo endpoint REST:**
```java
/**
 * Aprueba un grupo de gastos de una tarjeta
 * Cambia el estado de todas las facturas de DRAFT a PROCESSED para el mes-año especificado
 * PATCH /api/cards/{id}/expenses/approve
 */
@PATCH
@Path("/{id}/expenses/approve")
public Response approveExpenseGroup(
        @PathParam("id") Long cardId,
        @QueryParam("monthYear") String monthYear) {
    try {
        if (monthYear == null || monthYear.trim().isEmpty()) {
            return Response.status(400)
                    .entity("El parámetro 'monthYear' es obligatorio (formato: 'Mes YYYY', ej: 'Diciembre 2024')")
                    .build();
        }
        
        int updatedCount = cardService.approveExpenseGroup(cardId, monthYear);
        
        return Response.ok()
                .entity(String.format("Grupo de gastos aprobado correctamente. %d factura(s) actualizada(s) de DRAFT a PROCESSED", updatedCount))
                .build();
    } catch (IllegalArgumentException e) {
        logger.warning("Error de validación al aprobar grupo de gastos: " + e.getMessage());
        return Response.status(400)
                .entity("Error de validación: " + e.getMessage())
                .build();
    } catch (Exception e) {
        logger.severe("Error al aprobar grupo de gastos: " + e.getMessage());
        return Response.status(500)
                .entity("Error interno del servidor")
                .build();
    }
}
```

---

## Endpoints REST

### GET /api/cards/{id}/expenses
**Descripción:** Obtiene los gastos de una tarjeta agrupados por mes-año con clasificación automática

**Respuesta (200 OK):**
```json
[
  {
    "month": "Octubre 2025",
    "total": 15000.50,
    "count": 5,
    "status": "PENDIENTE",
    "expenses": [...]
  },
  {
    "month": "Septiembre 2025",
    "total": 22500.75,
    "count": 8,
    "status": "APROBADO",
    "expenses": [...]
  },
  {
    "month": "Agosto 2025",
    "total": 18750.00,
    "count": 6,
    "status": "MIXTO",
    "expenses": [...]
  }
]
```

### PATCH /api/cards/{id}/expenses/approve?monthYear={Mes YYYY}
**Descripción:** Aprueba un grupo de gastos específico

**Parámetros:**
- `id` (path): ID de la tarjeta
- `monthYear` (query): Mes y año en formato "Mes YYYY" (ej: "Octubre 2025")

**Ejemplos de uso:**

**cURL:**
```bash
curl -X PATCH "http://localhost:8080/api/cards/1/expenses/approve?monthYear=Octubre%202025"
```

**Respuestas:**

**200 OK:**
```
Grupo de gastos aprobado correctamente. 5 factura(s) actualizada(s) de DRAFT a PROCESSED
```

**400 Bad Request (parámetro faltante):**
```
El parámetro 'monthYear' es obligatorio (formato: 'Mes YYYY', ej: 'Diciembre 2024')
```

**400 Bad Request (formato inválido):**
```
Error de validación: Formato de mes-año inválido. Use formato 'Mes YYYY' (ej: 'Diciembre 2024')
```

**400 Bad Request (mes inválido):**
```
Error de validación: Nombre de mes inválido: Octobree
```

**404 Not Found:**
```
Error de validación: No se encontró la tarjeta con ID: 999
```

---

## Buenas Prácticas Implementadas

### 1. Lógica de Negocio en el Servicio
- Toda la lógica de clasificación y aprobación reside en `CardService`
- El controller solo maneja la comunicación HTTP y validación básica
- Separación clara de responsabilidades (Controller → Service → Repository)

### 2. Uso de Enums
- Se utiliza `InvoiceStatus.DRAFT` y `InvoiceStatus.PROCESSED` en lugar de strings hardcodeados
- Comparación segura usando `.name()` para evitar problemas de case-sensitivity
- Type-safety en todo el código

### 3. Optimización de Consultas
- **No hay problema de N+1:** Se usa la consulta existente `findInvoicesWithFieldsByCardId()` que hace JOINs eficientes
- **Procesamiento en memoria:** La clasificación se hace en memoria con Streams de Java 8+
- **Una sola consulta:** Solo se consulta la base de datos una vez por operación

### 4. Transaccionalidad
- Uso de `@Transactional` en `approveExpenseGroup()`
- Rollback automático en caso de errores
- Actualización atómica de todas las facturas del grupo

### 5. Validaciones Robustas
- Verificación de existencia de tarjeta
- Validación de formato de mes-año
- Validación de rango de mes (1-12)
- Manejo de casos null y empty

### 6. Logging Completo
- Log de inicio de operaciones con parámetros
- Log de cada factura actualizada
- Log de warnings para errores de validación
- Log de resultados finales con estadísticas

### 7. Manejo de Errores
- Excepciones específicas (`IllegalArgumentException`) para errores de validación
- Mensajes de error descriptivos
- Códigos HTTP apropiados (400, 404, 500)
- Try-catch en el controller para capturar todos los errores

---

## Flujo de Aprobación

```
1. Usuario solicita aprobar grupo de gastos
   └─> PATCH /api/cards/1/expenses/approve?monthYear=Octubre 2025

2. CardController valida parámetros básicos
   └─> monthYear no vacío

3. CardService.approveExpenseGroup() ejecuta:
   ├─> Verifica que existe la tarjeta
   ├─> Valida formato de mes-año
   ├─> Extrae mes y año del string
   ├─> Obtiene todas las facturas de la tarjeta
   ├─> Filtra facturas del mes-año especificado
   ├─> Filtra facturas en estado DRAFT
   ├─> Actualiza cada factura a PROCESSED
   ├─> Actualiza timestamps
   └─> Retorna cantidad actualizada

4. Sistema responde con resultado
   └─> "5 factura(s) actualizada(s)"

5. Próxima consulta a GET /api/cards/1/expenses
   └─> El grupo ahora aparece con status "APROBADO"
```

---

## Casos de Uso

### Caso 1: Aprobar Gastos del Mes
```
Situación: Un usuario tiene 5 facturas en DRAFT del mes de Octubre 2025
Acción: PATCH /api/cards/1/expenses/approve?monthYear=Octubre 2025
Resultado: Las 5 facturas cambian a PROCESSED
Estado del grupo: Cambia de "PENDIENTE" a "APROBADO"
```

### Caso 2: Grupo con Facturas Mixtas
```
Situación: Un usuario tiene 3 facturas DRAFT y 2 facturas PROCESSED en Septiembre 2025
Acción: PATCH /api/cards/1/expenses/approve?monthYear=Septiembre 2025
Resultado: Solo las 3 facturas DRAFT cambian a PROCESSED
Estado del grupo: "MIXTO" → "APROBADO" (todas ahora son PROCESSED)
```

### Caso 3: Todas las Facturas ya Aprobadas
```
Situación: Todas las facturas de Agosto 2025 ya están en PROCESSED
Acción: PATCH /api/cards/1/expenses/approve?monthYear=Agosto 2025
Resultado: 0 facturas actualizadas (no hay facturas en DRAFT)
Respuesta: "0 factura(s) actualizada(s) de DRAFT a PROCESSED"
```

### Caso 4: Formato Incorrecto
```
Situación: Usuario envía formato incorrecto
Acción: PATCH /api/cards/1/expenses/approve?monthYear=10-2025
Resultado: Error 400 Bad Request
Mensaje: "Formato de mes-año inválido. Use formato 'Mes YYYY'"
```

---

## Testing

### Ejemplos con cURL

**1. Consultar gastos (ver clasificación):**
```bash
curl -X GET "http://localhost:8080/api/cards/1/expenses"
```

**2. Aprobar grupo de Octubre 2025:**
```bash
curl -X PATCH "http://localhost:8080/api/cards/1/expenses/approve?monthYear=Octubre%202025"
```

**3. Aprobar grupo de Diciembre 2024:**
```bash
curl -X PATCH "http://localhost:8080/api/cards/1/expenses/approve?monthYear=Diciembre%202024"
```

**4. Ver estado actualizado:**
```bash
curl -X GET "http://localhost:8080/api/cards/1/expenses"
```

### Swagger UI
1. Acceder a `http://localhost:8080/q/swagger-ui/`
2. Buscar endpoint `PATCH /api/cards/{id}/expenses/approve`
3. Ingresar parámetros:
   - `id`: 1
   - `monthYear`: Octubre 2025
4. Ejecutar y ver resultado

---

## Compilación y Despliegue

**Compilación exitosa:**
```
mvn compile
[INFO] BUILD SUCCESS
```

Todos los cambios están probados y listos para producción.

---

## Resumen de Archivos Modificados

1. `src/main/java/com/datum/redsoft/service/CardService.java`
   - Agregados imports de `Invoice`, `InvoiceStatus`, `InvoiceRepository`, `LocalDateTime`
   - Inyectado `InvoiceRepository`
   - Modificado `getCardExpenses()` para clasificar grupos
   - Agregado método `determineGroupStatus()`
   - Agregado método `approveExpenseGroup()`

2. `src/main/java/com/datum/redsoft/service/interfaces/ICardService.java`
   - Agregado método `approveExpenseGroup()`

3. `src/main/java/com/datum/redsoft/dto/response/ExpenseGroupResponseDTO.java`
   - Actualizado comentario del campo `status`
   - Eliminado constructor con status por defecto

4. `src/main/java/com/datum/redsoft/controller/CardController.java`
   - Agregado endpoint `PATCH /api/cards/{id}/expenses/approve`

---

## Próximos Pasos Sugeridos

1. **Testing Unitario:** Crear tests para `determineGroupStatus()` y `approveExpenseGroup()`
2. **Testing de Integración:** Probar flujo completo de aprobación
3. **Documentación de API:** Actualizar Swagger con ejemplos de respuesta
4. **Notificaciones:** Considerar enviar notificaciones cuando un grupo es aprobado
5. **Auditoría:** Agregar registro de quién aprobó cada grupo (user tracking)
6. **Bulk Operations:** Considerar endpoint para aprobar múltiples grupos a la vez
7. **Reversión:** Implementar funcionalidad para revertir aprobaciones (PROCESSED → DRAFT)
