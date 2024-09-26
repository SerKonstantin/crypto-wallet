import { createGlobalStyle } from 'styled-components';

export const GlobalStyles = createGlobalStyle`
  body {
    margin: 0;
    padding: 0;
    background-color: ${({ theme }) => theme.body};
    color: ${({ theme }) => theme.text};
    font-family: 'Roboto', sans-serif;
    transition: all 0.25s linear;
  }

  h1, h2, h3 {
    font-family: 'Poppins', sans-serif;
  }

  p, li {
    font-family: 'Roboto', sans-serif;
  }
`;

export default GlobalStyles;
