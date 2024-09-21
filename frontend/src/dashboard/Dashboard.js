import React from 'react';
import TotalBalance from './TotalBalance';
import WalletList from './WalletList';
import RecentTransactions from './RecentTransactions';
import withApiData from '../hoc/withApiData';
import axiosClient from '../utils/axiosClient';
import useFetchWalletBalances from './useFetchWalletBalances';
import ErrorDisplay from '../components/ErrorDisplay';

function Dashboard({ data }) {
  const [wallets, transactions] = data;
  const { walletsWithBalances, fetchError } = useFetchWalletBalances(wallets);

  return (
    <div>
      {fetchError && <ErrorDisplay errors={[fetchError]} />}
      <TotalBalance wallets={walletsWithBalances || []} />
      <WalletList wallets={walletsWithBalances || []} />
      <RecentTransactions transactions={transactions || []} />
    </div>
  );
}

export default withApiData(Dashboard, [
  () => axiosClient.get(`/wallets`),
  () => axiosClient.get(`/wallets/transactions`),
]);
