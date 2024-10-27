import React, { useState } from 'react';
import usePostRequestWithFeedback from '../hooks/usePostRequestWithFeedback';
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
  const performPostRequestWithFeedback = usePostRequestWithFeedback();

  const handleSubmit = async event => {
    event.preventDefault();

    setErrors({});
    const data = {
      email,
      password,
      nickname,
    };

    performPostRequestWithFeedback({
      url: '/profile',
      data,
      successMessage: 'Registration successful! Please sign in.',
      redirectTo: '/login',
      setErrors,
    });
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
