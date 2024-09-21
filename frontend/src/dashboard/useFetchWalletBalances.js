import { useState, useEffect } from 'react';
import Web3 from 'web3';
import getWeb3Url from '../config/web3Config';

function useFetchWalletBalances(wallets) {
  const [walletsWithBalances, setWalletsWithBalances] = useState(wallets);
  const [fetchError, setFetchError] = useState(null);

  useEffect(() => {
    const fetchBalances = async () => {
      if (wallets.length === 0) return;

      const web3 = new Web3(new Web3.providers.HttpProvider(getWeb3Url()));

      const updatedWallets = await Promise.all(
        wallets.map(async wallet => {
          try {
            const balance = await web3.eth.getBalance(wallet.address);
            return {
              ...wallet,
              balance: web3.utils.fromWei(balance, 'ether'),
              error: null,
            };
          } catch (err) {
            console.error(
              `Error fetching balance for wallet ${wallet.address}:`,
              err
            );
            return {
              ...wallet,
              balance: undefined,
              error: 'Failed to fetch balance.',
            };
          }
        })
      );

      const hasError = updatedWallets.some(wallet => wallet.error);
      if (hasError) {
        setFetchError('Failed to fetch balances.');
      }

      setWalletsWithBalances(updatedWallets);
    };

    fetchBalances();
  }, [wallets]);

  return { walletsWithBalances, fetchError };
}

export default useFetchWalletBalances;
