import React, { useState, useEffect } from 'react';
import WalletList from './WalletList';
import useFetchWallets from './useFetchWallets';
import TotalBalance from './TotalBalance';
// import RecentTransactions from './RecentTransactions';

function Dashboard() {
  const { wallets, error: walletsError } = useFetchWallets();

  return (
    <div>
      {walletsError && <p style={{ color: 'red' }}>{walletsError}</p>}
      <TotalBalance wallets={wallets} />
      <WalletList wallets={wallets} />
      {/* <RecentTransactions wallets={wallets} /> */}
    </div>
  );
}

export default Dashboard;