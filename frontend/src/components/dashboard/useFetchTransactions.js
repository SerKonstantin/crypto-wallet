import { useState, useEffect } from 'react';
import axios from 'axios';
import config from '../../config/config';

function useFetchTransactions() {
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const response = await axios.get(
          `${config.apiBaseUrl}/api/wallets/transactions`,
          {
            headers: {
              Authorization: `Bearer ${sessionStorage.getItem('cryptoWalletAuthToken')}`,
            },
          }
        );
        setTransactions(response.data);
      } catch (error) {
        console.error('Error fetching transactions:', error);
        setError('Failed to fetch transactions. Please try again later.');
      }
    };

    fetchTransactions();
  }, []);

  return { transactions, error };
}

export default useFetchTransactions;
