import React from 'react';
import { useParams } from 'react-router-dom';
import withApiData from '../hoc/withApiData';
import useFetchWalletBalances from '../hooks/useFetchWalletBalances';
import ErrorDisplay from '../components/ErrorDisplay';
import { Container } from '../styles/CommonStyles';

function WalletDetailsContent({ data }) {
  const { walletsWithBalances, fetchError } = useFetchWalletBalances(data);

  if (!walletsWithBalances || walletsWithBalances.length === 0) return null;

  return (
    <Container>
      {fetchError && <ErrorDisplay errors={[fetchError]} />}

      <h2>Wallet Name: {walletsWithBalances[0].name}</h2>
      <p>Balance: {walletsWithBalances[0].balance}</p>
    </Container>
  );
}

function WalletDetails() {
  const { slug } = useParams();
  const WalletDetailsWithData = withApiData(WalletDetailsContent, [
    () => `/wallets/${slug}`,
  ]);

  return <WalletDetailsWithData />;
}

export default WalletDetails;
