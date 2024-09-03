import React from 'react';

function TotalBalance({ wallets }) {
  const totalBalance = wallets.reduce((total, wallet) => {
    return total + parseFloat(wallet.balance);
  }, 0);

  return (
    <div>
      <h2>Total Balance</h2>
      <p>{totalBalance.toFixed(6)} ETH</p>
    </div>
  );
}

export default TotalBalance;
