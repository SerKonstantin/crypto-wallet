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

function Register() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [errors, setErrors] = useState({});
  const navigate = useNavigate();

  const handleSubmit = async event => {
    event.preventDefault();
    setErrors({});

    try {
      await axiosClient.post(`/profile`, {
        email,
        password,
        nickname,
      });
      navigate('/login');
    } catch (err) {
      if (err.response && err.response.data && err.response.data.errors) {
        setErrors(err.response.data.errors);
      } else if (err.response) {
        setErrors({
          form: 'An error occurred during registration. Please try again later.',
        });
      } else if (err.request) {
        setErrors({
          form: 'Unable to reach the server. Please ensure the backend is running.',
        });
      } else {
        setErrors({ form: 'An unexpected error occurred. Please try again.' });
      }
    }
  };

  return (
    <Container>
      <SectionHeading>Create your account</SectionHeading>
      <Form onSubmit={handleSubmit}>
        <FormField>
          <Label>Email</Label>
          <Input
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />
          {errors.email && <ErrorDisplay errors={errors.email} />}
        </FormField>
        <FormField>
          <Label>Password</Label>
          <Input
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />
          {errors.password && <ErrorDisplay errors={errors.password} />}
        </FormField>
        <FormField>
          <Label>Nickname</Label>
          <Input
            type="text"
            value={nickname}
            onChange={e => setNickname(e.target.value)}
            required
          />
          {errors.nickname && <ErrorDisplay errors={errors.nickname} />}
        </FormField>
        {errors.form && <ErrorDisplay errors={errors.form} />}
        <Button type="submit">Register</Button>
      </Form>
      <Description>
        Already have an account? <TextLink to="/login">Login here</TextLink>
      </Description>
    </Container>
  );
}

export default Register;
