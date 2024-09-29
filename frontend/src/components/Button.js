import styled from 'styled-components';

const Button = styled.button`
  background-color: ${({ theme }) => theme.buttonBg};
  color: ${({ theme }) => theme.buttonText};
  padding: 15px 30px;
  margin: 10px;
  border-radius: 10px;
  font-size: 1.2rem;
  font-weight: 500;
  cursor: pointer;
  border: none;
  text-decoration: none;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  // Auto adjust width for form submit buttons
  ${({ type }) =>
    type === 'submit' &&
    `
    width: 100%;
    margin: 20px 0 0 0;
  `}

  &:hover {
    background-color: ${({ theme }) => theme.buttonBgHover || theme.buttonBg};
    transform: translateY(-3px);
  }

  &:active {
    transform: translateY(0);
  }

  &:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
`;

export default Button;
