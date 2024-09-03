import React from 'react';
import TotalBalance from './TotalBalance';
import WalletList from './WalletList';
import useFetchWallets from './useFetchWallets';
import RecentTransactions from './RecentTransactions';
import useFetchTransactions from './useFetchTransactions';

function Dashboard() {
  const { wallets, error: walletsError } = useFetchWallets();
  const { transactions, error: transactionsError } = useFetchTransactions();

  return (
    <div>
      {walletsError ||
        (transactionsError && (
          <p style={{ color: 'red' }}>{walletsError || transactionsError}</p>
        ))}
      <TotalBalance wallets={wallets} />
      <WalletList wallets={wallets} />
      <RecentTransactions transactions={transactions} />
    </div>
  );
}

export default Dashboard;
