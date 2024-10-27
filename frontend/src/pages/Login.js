import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosClient from '../utils/axiosClient';
import ErrorDisplay from '../components/ErrorDisplay';
import {
  Container,
  SectionHeading,
  Form,
  FormField,
  Input,
  Label,
  TextLink,
  Description,
} from '../styles/CommonStyles';
import Button from '../components/Button';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState([]);
  const navigate = useNavigate();

  const handleSubmit = async event => {
    event.preventDefault();
    setErrors([]); // Clear previous error

    try {
      const response = await axiosClient.post(`/login`, {
        username: email,
        password,
      });
      sessionStorage.setItem('cryptoWalletAuthToken', response.data);
      navigate('/dashboard');
    } catch (err) {
      if (err.response) {
        setErrors(['Invalid email or password. Please try again.']);
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
    <Container>
      <SectionHeading>Please, sign in to continue</SectionHeading>

      <Form onSubmit={handleSubmit}>
        <FormField>
          <Label>Email</Label>
          <Input
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />
        </FormField>
        <FormField>
          <Label>Password</Label>
          <Input
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />
        </FormField>
        <ErrorDisplay errors={errors} />
        <Button type="submit">Sign In</Button>
      </Form>

      <Description>
        Don't have an account? <TextLink to="/register">Register here</TextLink>
      </Description>
    </Container>
  );
}

export default Login;
