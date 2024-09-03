import React from 'react';

function WalletList({ wallets }) {
  return (
    <div>
      <h2>Wallets</h2>
      <ul>
        {wallets.map(wallet => (
          <li key={wallet.id}>
            <p>Name: {wallet.name}</p>
            <p>Address: {wallet.address}</p>
            <p>Balance: {parseFloat(wallet.balance).toFixed(6)} ETH</p>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default WalletList;
