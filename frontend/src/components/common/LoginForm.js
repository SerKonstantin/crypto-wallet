import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import config from '../../config/config';

const apiClient = axios.create({
  baseURL: config.apiBaseUrl,
});

function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');  // Clear previous error

    try {
      const response = await apiClient.post('/api/login', { username: email, password });
      sessionStorage.setItem('cryptoWalletAuthToken', response.data);
      navigate('/dashboard');
    } catch (err) {
      // TODO check error messages
      if (err.response) {
        // Server responded with non 2xx status code
        var message = err.response.status === 401 ? 'Invalid credentials. Please try again.' : 'An error occurred. Please try again later.';
        setError(message);
      } else if (err.request) {
        // The request was made, but no response was received from server
        setError('Unable to reach the server. Please ensure the backend is running.');
      } else {
        // Request wasnt sent
        setError('An unexpected error occurred. Please try again.');
      }
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Email:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && <p>{error}</p>}
        <button type="submit">Login</button>
      </form>
    </div>
  );
}

export default LoginForm;
