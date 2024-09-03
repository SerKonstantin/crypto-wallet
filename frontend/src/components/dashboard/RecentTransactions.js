import React, { useEffect, useState } from 'react';
import Web3 from 'web3';
import getWeb3Url from '../../config/web3Config';

function RecentTransactions({ transactions }) {
  const web3 = new Web3(new Web3.providers.HttpProvider(getWeb3Url()));

  return (
    <div>
      <h2>Recent Transactions</h2>
      <ul>
        {transactions.map(transaction => {
          const amount =
            transaction.amount !== undefined && transaction.amount !== null
              ? web3.utils.fromWei(transaction.amount.toString(), 'ether')
              : 'N/A';

          return (
            <li key={transaction.id}>
              <p>
                {transaction.type}: {amount} ETH
              </p>
              <p>Status: {transaction.status}</p>
              <p>
                Date:{' '}
                {new Date(Date.parse(transaction.createdAt)).toLocaleString()}
              </p>
            </li>
          );
        })}
      </ul>
    </div>
  );
}

export default RecentTransactions;
