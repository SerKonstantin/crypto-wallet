import { useNavigate } from 'react-router-dom';
import axiosClient from '../utils/axiosClient';

const usePostRequestWithFeedback = () => {
  const navigate = useNavigate();

  return async ({ url, data, successMessage, redirectTo, setErrors }) => {
    try {
      await axiosClient.post(url, data);
      sessionStorage.setItem('flashMessage', successMessage);
      sessionStorage.setItem('flashType', 'success');
      navigate(redirectTo);
    } catch (err) {
      if (err.response && err.response.data && err.response.data.errors) {
        setErrors(err.response.data.errors);
      } else if (err.response) {
        setErrors({ form: 'An error occurred. Please try again later.' });
      } else if (err.request) {
        setErrors({
          form: 'Unable to reach the server. Please ensure the backend is running.',
        });
      } else {
        setErrors({ form: 'An unexpected error occurred. Please try again.' });
      }
    }
  };

  // console.log('usePostRequestWithFeedback function called');
  // return async ({ url, data, successMessage, redirectTo, setErrors }) => {
  //   console.log('Function returned from usePostRequestWithFeedback called');
  // };
};

export default usePostRequestWithFeedback;
