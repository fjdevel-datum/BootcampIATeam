import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { User } from 'lucide-react';
import { Card } from '../types/api';

interface UserCardProps {
  userName: string;
  userEmail: string;
  cards: Card[];
  onCardSelect?: (cardId: number) => void;
  className?: string;
}

export const UserCard: React.FC<UserCardProps> = ({
  userName,
  userEmail,
  cards,
  onCardSelect,
  className = ''
}) => {
  const navigate = useNavigate();
  const [selectedCard, setSelectedCard] = useState<number | null>(null);

  const handleCardClick = (card: Card) => {
    setSelectedCard(card.id);
    onCardSelect?.(card.id);
    
    // Enviar todos los datos de la tarjeta via state
    navigate(`/card/${card.id}`, {
      state: {
        cardData: {
          id: card.id,
          maskedCardNumber: card.maskedCardNumber,
          holderName: card.holderName,
          expirationDate: card.expirationDate,
          companyName: card.companyName, // Será usado como país
          status: card.status,
          issuerBank: card.issuerBank,
          cardType: card.cardType
        },
        userName: userName,
        userEmail: userEmail
      }
    });
  };

  return (
    <div className={`bg-white rounded-2xl shadow-sm border border-gray-100 p-6 w-full ${className}`}>
      <div className="flex items-center gap-4 mb-6">
        <div className="w-14 h-14 rounded-full bg-[#f23030] flex items-center justify-center">
          <User className="w-7 h-7 text-white" strokeWidth={2} />
        </div>
        <div className="flex flex-col">
          <span className="text-neutral-950 font-normal text-base leading-6">
            Nombre: {userName}
          </span>
          <span className="text-[#717182] font-normal text-sm leading-5">
            Email: {userEmail}
          </span>
        </div>
      </div>

      <div className="mb-3">
        <span className="text-[#717182] font-normal text-sm leading-5">
          Selecciona tu tarjeta:
        </span>
      </div>

      <div className="grid grid-cols-1 gap-3">
        {cards.filter(card => card.status === 'ACTIVE').length === 0 ? (
          <div className="text-center py-6 text-[#717182]">
            <p className="text-sm">No hay tarjetas activas disponibles</p>
          </div>
        ) : (
          cards.filter(card => card.status === 'ACTIVE').map((card) => (
          <button
            key={card.id}
            onClick={() => handleCardClick(card)}
            className={`flex flex-col items-start justify-center p-4 rounded-lg border transition-all ${
              selectedCard === card.id
                ? 'border-[#f23030] bg-red-50'
                : 'border-gray-200 bg-white hover:border-gray-300'
            }`}
          >
            <div className="flex items-center justify-between w-full mb-2">
              <span className="text-sm font-medium text-neutral-950">
                {card.maskedCardNumber}
              </span>
              <span className={`text-xs px-2 py-1 rounded-full ${
                card.status === 'ACTIVE' 
                  ? 'bg-green-100 text-green-800' 
                  : 'bg-gray-100 text-gray-600'
              }`}>
                {card.status}
              </span>
            </div>
            <span className="text-xs text-[#717182] mb-1">
              {card.companyName}
            </span>
            <span className="text-xs text-[#717182]">
              {card.issuerBank} - {card.cardType}
            </span>
          </button>
          ))
        )}
      </div>
    </div>
  );
};
