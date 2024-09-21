import axios from 'axios';
import config from '../config/config';

const axiosClient = axios.create({
  baseURL: config.apiBaseUrl,
});

const publicRoutes = ['/login', '/register'];

// Add token to any request except for public routes
axiosClient.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem('cryptoWalletAuthToken');

    if (!config.headers) {
      config.headers = {};
    }

    const isPublicRoute = publicRoutes.some(route =>
      config.url.includes(route)
    );

    if (token && !isPublicRoute) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }

    return config;
  },

  error => {
    return Promise.reject(error);
  }
);

export default axiosClient;
