import React from 'react';
import { SectionHeading, Description } from '../styles/CommonStyles';

function TotalBalance({ wallets }) {
  const totalBalance = wallets.reduce((total, wallet) => {
    return (
      total + parseFloat(wallet.balance !== undefined ? wallet.balance : 0)
    );
  }, 0);

  return (
    <div>
      <SectionHeading>Total Balance</SectionHeading>
      <Description>
        {wallets.some(wallet => wallet.balance === undefined)
          ? 'Fetching...'
          : `${totalBalance.toFixed(6)} ETH`}
      </Description>
    </div>
  );
}

export default TotalBalance;
