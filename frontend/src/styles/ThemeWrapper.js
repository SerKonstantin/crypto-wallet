import React, { useState } from 'react';
import { ThemeProvider } from 'styled-components';
import { theme1, theme2, theme3 } from './theme';
import GlobalStyles from './GlobalStyles';

const ThemeWrapper = ({ children }) => {
  const [currentTheme, setCurrentTheme] = useState(theme1);

  const handleThemeChange = event => {
    switch (event.target.value) {
      case 'theme1':
        setCurrentTheme(theme1);
        break;
      case 'theme2':
        setCurrentTheme(theme2);
        break;
      case 'theme3':
        setCurrentTheme(theme3);
        break;
      default:
        setCurrentTheme(theme1);
    }
  };

  return (
    <ThemeProvider theme={currentTheme}>
      <GlobalStyles />
      <div>
        <nav>
          <label htmlFor="themeSelector">Select Theme:</label>
          <select id="themeSelector" onChange={handleThemeChange}>
            <option value="theme1">Theme 1</option>
            <option value="theme2">Theme 2</option>
            <option value="theme3">Theme 3</option>
          </select>
        </nav>
        {children}
      </div>
    </ThemeProvider>
  );
};

export default ThemeWrapper;
