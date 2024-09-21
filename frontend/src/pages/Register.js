import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axiosClient from '../utils/axiosClient';
import ErrorDisplay from '../components/ErrorDisplay';

function Register() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [errors, setErrors] = useState([]);
  const navigate = useNavigate();

  const handleSubmit = async event => {
    event.preventDefault();
    setErrors([]);

    try {
      await axiosClient.post(`/profile`, {
        email,
        password,
        nickname,
      });
      navigate('/login');
    } catch (err) {
      if (err.response) {
        const message =
          err.response.status === 409
            ? 'User with the same email or nickname already exists.'
            : 'An error occurred during registration. Please try again later.';
        setErrors([message]);
      } else if (err.request) {
        setErrors([
          'Unable to reach the server. Please ensure the backend is running.',
        ]);
      } else {
        setErrors(['An unexpected error occurred. Please try again.']);
      }
    }
  };

  return (
    <div>
      <h2>Register</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Email:</label>
          <input
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Nickname:</label>
          <input
            type="text"
            value={nickname}
            onChange={e => setNickname(e.target.value)}
            required
          />
        </div>
        <ErrorDisplay errors={errors} />
        <button type="submit">Register</button>
      </form>
      <p>
        Already have an account? <Link to="/login">Login here</Link>
      </p>
    </div>
  );
}

export default Register;
