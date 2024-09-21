import React from 'react';
import { Link } from 'react-router-dom';

function Home() {
  return (
    <div>
      <h1>Welcome to Crypto Wallet App</h1>
      <p>TODO Promo/Description</p>

      <Link to="/login">
        <button>Login</button>
      </Link>
      <Link to="/register">
        <button>Register</button>
      </Link>
    </div>
  );
}

export default Home;
