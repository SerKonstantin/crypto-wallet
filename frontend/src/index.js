import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

const fontLink = document.createElement('link');
fontLink.href =
  'https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&family=Roboto:wght@400;500&display=swap';
fontLink.rel = 'stylesheet';

document.head.appendChild(fontLink);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
