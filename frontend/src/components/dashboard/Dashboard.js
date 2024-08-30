import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Web3 from 'web3';
import config from '../../config';
import getWeb3Url from '../../web3Config';
import TotalBalance from './TotalBalance';
import WalletList from './WalletList';
// import RecentTransactions from './RecentTransactions';

function Dashboard() {
  const [wallets, setWallets] = useState([]);
  const [error, setError] = useState('');
  
    useEffect(() => {
      const fetchWalletsAndBalances = async () => {
        try {
          // Fetch the wallets from the backend
          const walletResponse = await axios.get(`${config.apiBaseUrl}/api/wallets`, {
            headers: {
              Authorization: `Bearer ${sessionStorage.getItem('cryptoWalletAuthToken')}`,
            },
          });

          // Map over the wallets and fetch the balance for each one
          // We do it here because wallet balances might change from sources, other then backend
          const web3 = new Web3(new Web3.providers.HttpProvider(getWeb3Url()));
          const walletsWithBalances = await Promise.all(
            walletResponse.data.map(async (wallet) => {
            const balance = await web3.eth.getBalance(wallet.address);
            return {
              ...wallet,
              balance: web3.utils.fromWei(balance, 'ether'),
            };
          })
        );

        setWallets(walletsWithBalances);

        } catch (web3Error) {
          console.error('Error fetching wallets or balances:', error);
          setError('An error occurred while fetching your wallets. Please try again later.');
        }
      };
  
      fetchWalletsAndBalances();
    }, []);

  return (
    <div>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <TotalBalance wallets={wallets} />
      <WalletList wallets={wallets} />
      {/* <RecentTransactions wallets={wallets} /> */}
    </div>
  );
}

export default Dashboard;