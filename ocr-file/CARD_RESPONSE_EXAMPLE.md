# Ejemplo de JSON para CardResponseDTO con companyId

## Antes del cambio:
```json
{
  "id": 1,
  "maskedCardNumber": "**** **** **** 1234",
  "holderName": "Juan Pérez",
  "cardType": "CREDIT",
  "expirationDate": "2027-12-31",
  "issuerBank": "Banco Nacional",
  "creditLimit": 50000.00,
  "status": "ACTIVE",
  "description": "Tarjeta corporativa principal",
  "userName": "Juan Pérez",
  "companyName": "Tech Solutions S.A.",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

## Después del cambio (con companyId):
```json
{
  "id": 1,
  "maskedCardNumber": "**** **** **** 1234",
  "holderName": "Juan Pérez",
  "cardType": "CREDIT",
  "expirationDate": "2027-12-31",
  "issuerBank": "Banco Nacional",
  "creditLimit": 50000.00,
  "status": "ACTIVE",
  "description": "Tarjeta corporativa principal",
  "userName": "Juan Pérez",
  "companyId": 5,
  "companyName": "Tech Solutions S.A.",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

## Nota:
- Se agregó el campo `companyId` que contiene el ID numérico de la empresa
- Si la tarjeta no tiene empresa asociada, `companyId` será `null`
- El campo `companyName` se mantiene para facilitar la visualización en el frontend
- Ambos campos provienen de la relación `card.company` en la entidad Card