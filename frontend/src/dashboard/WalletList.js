import React from 'react';

function WalletList({ wallets }) {
  return (
    <div>
      <h2>Wallets</h2>
      {wallets.length > 0 ? (
        <ul>
          {wallets.map(wallet => (
            <li key={wallet.id}>
              <p>Name: {wallet.name}</p>
              <p>Address: {wallet.address}</p>
              <p>
                Balance:{' '}
                {wallet.balance !== undefined
                  ? `${parseFloat(wallet.balance).toFixed(6)} ETH`
                  : 'Fetching...'}
              </p>
            </li>
          ))}
        </ul>
      ) : (
        <p>You don't have any wallets yet</p>
      )}
    </div>
  );
}

export default WalletList;
