import React from 'react';
import TotalBalance from './TotalBalance';
import WalletList from './WalletList';
import useFetchWallets from './useFetchWallets';
import RecentTransactions from './RecentTransactions';
import useFetchTransactions from './useFetchTransactions';
import ErrorDisplay from './../common/ErrorDisplay';

function Dashboard() {
  const { wallets, error: walletsError } = useFetchWallets();
  const { transactions, error: transactionsError } = useFetchTransactions();
  const errors = [walletsError, transactionsError].filter(Boolean);

  return (
    <div>
      <ErrorDisplay errors={errors} />
      <TotalBalance wallets={wallets} />
      <WalletList wallets={wallets} />
      <RecentTransactions transactions={transactions} />
    </div>
  );
}

export default Dashboard;
