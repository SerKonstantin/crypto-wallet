import styled from 'styled-components';
import { Link } from 'react-router-dom';

export const Container = styled.div`
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
  text-align: center;
`;

export const Title = styled.h1`
  font-size: 3rem;
  margin-bottom: 30px;
  color: ${({ theme }) => theme.text};
`;

// Button group to center buttons
export const ButtonGroup = styled.div`
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 20px;
`;

export const SectionHeading = styled.h3`
  margin-top: 50px;
  font-size: 2rem;
  color: ${({ theme }) => theme.text};
  text-align: center;
`;

export const Description = styled.p`
  font-size: 1.2rem;
  line-height: 1.6;
  margin-bottom: 25px;
  color: ${({ theme }) => theme.text};
`;

export const Form = styled.form`
  max-width: ${({ maxWidth }) => maxWidth || '360px'};
  min-width: ${({ minWidth }) => minWidth || '200px'};
  margin: 40px auto 20px;
  display: flex;
  flex-direction: column;
  gap: ${({ gap }) => gap || '20px'};
`;

export const FormField = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 20px;
  width: 100%;
`;

export const Input = styled.input`
  padding: 12px;
  font-size: 1rem;
  border-radius: 10px;
  border: 1px solid #ccc;
  transition: border-color 0.2s;
  width: 100%;
  box-sizing: border-box;

  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.buttonBg};
  }
`;

export const Label = styled.label`
  font-size: 1rem;
  font-weight: 500;
  color: ${({ theme }) => theme.text};
  margin-bottom: 5px;
  text-align: left;
`;

export const TextLink = styled(Link)`
  color: ${({ theme }) => theme.buttonBg};
  text-decoration: none;
  font-weight: bold;

  &:hover {
    text-decoration: underline;
  }
`;
