import React from 'react';
import { useNavigate } from 'react-router-dom';
import Web3 from 'web3';
import getWeb3Url from '../config/web3Config';
import { SectionHeading, Description } from '../styles/CommonStyles';
import styled from 'styled-components';

const TransactionList = styled.ul`
  list-style: none;
  padding: 0;
  margin: 20px auto;
  max-width: 840px;
`;

const TransactionCard = styled.li`
  border: 1px solid ${({ theme }) => theme.buttonBg};
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1rem;
  background-color: ${({ theme }) => theme.body};
  transition:
    background-color 0.3s ease,
    box-shadow 0.3s ease;
  cursor: pointer;

  &:hover {
    background-color: ${({ theme }) => theme.hoverBg};
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  }

  @media (max-width: 600px) {
    flex-direction: column;
    align-items: flex-start;
  }
`;

const TransactionInfo = styled.span`
  flex: 1;
  text-align: left;

  &:nth-child(2) {
    font-weight: bold;
  }

  &:nth-child(4) {
    color: ${({ theme }) => theme.mutedText || '#999'};
    font-size: 0.9rem;
  }

  @media (max-width: 600px) {
    margin-bottom: 8px;
  }
`;

function RecentTransactions({ transactions }) {
  const web3 = new Web3(new Web3.providers.HttpProvider(getWeb3Url()));
  const navigate = useNavigate();

  if (transactions.length === 0) {
    return (
      <div>
        <SectionHeading>Recent Transactions</SectionHeading>
        <Description>You don't have any transactions yet</Description>
      </div>
    );
  }

  const convertWeiToEth = wei => web3.utils.fromWei(wei, 'ether');

  return (
    <div>
      <SectionHeading>Recent Transactions</SectionHeading>
      <TransactionList>
        {transactions.map(transaction => (
          <TransactionCard
            key={transaction.id}
            onClick={() => navigate(`/transactions/${transaction.id}`)}
          >
            <TransactionInfo>{transaction.type}</TransactionInfo>
            <TransactionInfo>
              {convertWeiToEth(transaction.amount)} ETH
            </TransactionInfo>
            <TransactionInfo>{transaction.status}</TransactionInfo>
            <TransactionInfo>
              {new Date(transaction.createdAt).toLocaleString()}
            </TransactionInfo>
          </TransactionCard>
        ))}
      </TransactionList>
    </div>
  );
}

export default RecentTransactions;
