import React from 'react';
import Web3 from 'web3';
import getWeb3Url from '../config/web3Config';
import {
  TransactionCard,
  TransactionInfoRow,
  Description,
} from '../styles/CommonStyles';

function RecentTransactions({ transactions }) {
  const web3 = new Web3(new Web3.providers.HttpProvider(getWeb3Url()));

  return (
    <div>
      <h2>Recent Transactions</h2>
      {transactions.length > 0 ? (
        <ul style={{ padding: 0, listStyle: 'none' }}>
          {transactions.map(transaction => {
            const amount =
              transaction.amount !== undefined && transaction.amount !== null
                ? web3.utils.fromWei(transaction.amount.toString(), 'ether')
                : 'N/A';

            return (
              <TransactionCard key={transaction.id}>
                <TransactionInfoRow>
                  <span>
                    <strong>{transaction.type}</strong>
                  </span>
                  <span>{amount} ETH</span>
                  <span>{transaction.status}</span>
                  <span>
                    {new Date(transaction.createdAt).toLocaleString()}
                  </span>
                </TransactionInfoRow>
              </TransactionCard>
            );
          })}
        </ul>
      ) : (
        <p>You don't have any transactions yet</p>
      )}
    </div>
  );
}

export default RecentTransactions;
