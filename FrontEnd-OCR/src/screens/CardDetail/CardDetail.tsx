import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, CreditCard, MapPin } from 'lucide-react';
import { Button } from '../../components/ui/button';
import { ExpenseList } from '../../components/ExpenseList';

export const CardDetail = (): JSX.Element => {
  const { cardId } = useParams<{ cardId: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  
  // Obtener los datos de la tarjeta del state
  const { cardData, userName, userEmail } = location.state || {};
  
  console.log("Card ID enviado:", cardId);
  console.log("Datos de la tarjeta recibidos:", cardData);

  // Usar datos reales si están disponibles, sino mostrar error
  if (!cardData) {
    return (
      <div className="min-h-screen bg-white flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-neutral-950 mb-4">
            Tarjeta no encontrada
          </h2>
          <p className="text-gray-600 mb-6">
            No se pudieron cargar los datos de la tarjeta.
          </p>
          <Button onClick={() => navigate('/')}>
            Volver al inicio
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white">
      <div className="max-w-6xl mx-auto px-4 py-6">
        <div className="mb-6">
          <Button
            variant="ghost"
            onClick={() => navigate('/')}
            className="flex items-center gap-2 text-gray-600 hover:text-neutral-950"
          >
            <ArrowLeft className="w-4 h-4" />
            Volver
          </Button>
        </div>

        <div className="flex items-center justify-center mb-6">
          <div className="w-[173px] h-[95px] [background:url(../image--datum-el-salvador-.png)_50%_50%_/_cover]" />
        </div>

        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-neutral-950 mb-2">
            Mi Tarjeta de Crédito
          </h1>
          <p className="text-gray-600 text-base mb-4">
            Gestiona tus gastos y visualiza tu historial
          </p>
          {userName && (
            <p className="text-sm text-gray-500">
              Titular: {userName} ({userEmail})
            </p>
          )}
        </div>

        {/* Información de la Tarjeta */}
        <div className="max-w-2xl mx-auto mb-8">
          <div className="bg-gradient-to-r from-gray-900 to-gray-700 rounded-xl p-6 text-white shadow-lg">
            <div className="flex items-center justify-between mb-4">
              <CreditCard className="w-8 h-8" />
              <span className="text-sm opacity-75">{cardData.cardType}</span>
            </div>
            
            <div className="space-y-4">
              <div>
                <p className="text-sm opacity-75 mb-1">Número de Tarjeta</p>
                <p className="text-xl font-mono tracking-wider">
                  {cardData.maskedCardNumber}
                </p>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm opacity-75 mb-1">Titular</p>
                  <p className="font-medium">{cardData.holderName}</p>
                </div>
                <div>
                  <p className="text-sm opacity-75 mb-1">Vencimiento</p>
                  <p className="font-medium">{cardData.expirationDate}</p>
                </div>
              </div>
              
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <MapPin className="w-4 h-4" />
                  <span className="text-sm">{cardData.companyName}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="max-w-3xl mx-auto">
          <ExpenseList cardId={cardId || ''} />
        </div>
      </div>
    </div>
  );
};
