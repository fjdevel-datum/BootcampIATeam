import { useState, useEffect, useRef } from 'react';
import { User, Card } from '../types/api';
import { ApiService } from '../services/apiService';

export const useUserData = (userId: number) => {
  const [user, setUser] = useState<User | null>(null);
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const fetchingRef = useRef(false);

  const fetchUserData = async () => {
    // Prevenir llamadas duplicadas
    if (fetchingRef.current) return;
    
    try {
      fetchingRef.current = true;
      setLoading(true);
      setError(null);

      // Fetch user data and cards in parallel
      const [userData, cardsData] = await Promise.all([
        ApiService.getUserById(userId),
        ApiService.getUserCards(userId)
      ]);

      setUser(userData);
      setCards(cardsData);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Error desconocido al cargar los datos del usuario';
      setError(errorMessage);
      console.error('Error fetching user data:', err);
    } finally {
      setLoading(false);
      fetchingRef.current = false;
    }
  };

  useEffect(() => {
    if (userId) {
      fetchUserData();
    }
  }, [userId]);

  const refetch = () => {
    fetchUserData();
  };

  return {
    user,
    cards,
    loading,
    error,
    refetch
  };
};