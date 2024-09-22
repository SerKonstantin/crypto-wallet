import React from 'react';
import { Link } from 'react-router-dom';

function Home() {
  return (
    <div>
      <h1>Welcome to Crypto Wallet App</h1>
      <p>
        Manage your Ethereum wallets in one place. The Crypto Wallet App allows
        you to add multiple wallets and perform transactions. Currently, the app
        only supports Ethereum transactions.
      </p>

      <p>
        For demonstration purposes, the app uses the Sepolia testnet, allowing
        you to explore wallet management features safely without using real
        assets. Try it out now!
      </p>

      <Link to="/login">
        <button>Login</button>
      </Link>
      <Link to="/register">
        <button>Register</button>
      </Link>

      <h3>Main Features:</h3>
      <ul>
        <li>Register an account with secure authentication using JWT tokens</li>
        <li>Add and manage multiple Ethereum wallets</li>
        <li>Wallet balances are fetched directly from the blockchain</li>
        <li>Send and receive Ethereum transactions</li>
        <li>View transactions history</li>
      </ul>

      <h3>Technologies Used:</h3>
      <ul>
        <li>React, JavaScript (Frontend)</li>
        <li>Spring Boot, PostgreSQL (Backend)</li>
        <li>Web3.js for blockchain interaction</li>
        <li>Docker, Sentry, GitHub Actions CI</li>
      </ul>
    </div>
  );
}

export default Home;
