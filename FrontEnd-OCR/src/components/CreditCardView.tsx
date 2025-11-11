import React from 'react';
import { CreditCard } from 'lucide-react';

interface CreditCardViewProps {
  cardNumber: string;
  cardHolder: string;
  expiryDate: string;
  country: string;
  countryCode: string;
  className?: string;
}

export const CreditCardView: React.FC<CreditCardViewProps> = ({
  cardNumber,
  cardHolder,
  expiryDate,
  country,
  countryCode,
  className = ''
}) => {
  const formatCardNumber = (number: string) => {
    return number.replace(/(\d{4})/g, '$1 ').trim();
  };

  return (
    <div className={`relative ${className}`}>
      <div className="bg-gradient-to-br from-[#1e293b] to-[#334155] rounded-2xl p-6 shadow-2xl w-full max-w-md aspect-[1.586/1]">
        <div className="flex justify-between items-start mb-8">
          <div className="w-12 h-10 bg-gradient-to-br from-[#fbbf24] to-[#f59e0b] rounded-md" />
          <CreditCard className="w-8 h-8 text-white/80" />
        </div>

        <div className="mb-6">
          <div className="text-white text-2xl font-mono tracking-wider mb-1">
            {formatCardNumber(cardNumber)}
          </div>
        </div>

        <div className="flex justify-between items-end">
          <div>
            <div className="text-white/60 text-[10px] uppercase tracking-wider mb-1">
              Titular
            </div>
            <div className="text-white text-sm font-medium uppercase">
              {cardHolder}
            </div>
          </div>
          <div className="text-right">
            <div className="text-white/60 text-[10px] uppercase tracking-wider mb-1">
              Válida hasta
            </div>
            <div className="text-white text-sm font-medium">
              {expiryDate}
            </div>
          </div>
        </div>

        <div className="absolute top-4 right-4 text-white/40 text-xs font-semibold">
          {country}
        </div>
      </div>
    </div>
  );
};
