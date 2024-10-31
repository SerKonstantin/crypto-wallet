import React from 'react';
import TotalBalance from './TotalBalance';
import WalletList from './WalletList';
import RecentTransactions from './RecentTransactions';
import withApiData from '../hoc/withApiData';
import useFetchWalletBalances from '../hooks/useFetchWalletBalances';
import ErrorDisplay from '../components/ErrorDisplay';
import { Container } from '../styles/CommonStyles';

function Dashboard({ data }) {
  const [wallets, transactions] = data;
  const { walletsWithBalances, fetchError } = useFetchWalletBalances(wallets);

  return (
    <Container>
      {fetchError && <ErrorDisplay errors={[fetchError]} />}
      <TotalBalance wallets={walletsWithBalances || []} />
      <WalletList wallets={walletsWithBalances || []} />
      <RecentTransactions transactions={transactions || []} />
    </Container>
  );
}

export default withApiData(Dashboard, ['/wallets', '/transactions']);
