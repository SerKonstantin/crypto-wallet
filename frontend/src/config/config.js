const environment = process.env.REACT_APP_ENV || 'development';

const config = {
  development: {
    apiBaseUrl: 'http://localhost:8080/api',
    infuraBaseUrl: 'https://sepolia.infura.io/v3/',
  },
  production: {
    apiBaseUrl: process.env.REACT_APP_API_BASE_URL,
    infuraBaseUrl: 'https://sepolia.infura.io/v3/', // TODO Change to mainnet endpoint for real use
  },
};

export default config[environment];
