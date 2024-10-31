import React, { useState, useEffect } from 'react';
import axiosClient from '../utils/axiosClient';
import ErrorDisplay from '../components/ErrorDisplay';
import { Container } from '../styles/CommonStyles';

function withApiData(WrappedComponent, paths) {
  return function ComponentWithApiData(props) {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState([]);
    const [data, setData] = useState([]);

    useEffect(() => {
      const fetchData = async () => {
        setLoading(true);
        setError(null);

        try {
          const responses = await Promise.all(
            paths.map(path => {
              const url = typeof path === 'function' ? path() : path;
              return axiosClient.get(url);
            })
          );
          setData(responses.map(response => response.data));
        } catch (err) {
          if (err.response) {
            setError('An error occurred while fetching data.');
          } else if (err.request) {
            setError('Server is unavailable. Please try again later.');
          } else {
            setError('An unexpected error occurred. Please try again later.');
          }
        }

        setLoading(false);
      };

      fetchData();
    }, [paths]);

    if (loading) {
      return (
        <Container>
          <div>Loading...</div>
        </Container>
      );
    }

    if (error) {
      return (
        <Container>
          <ErrorDisplay errors={error} />
        </Container>
      );
    }

    return <WrappedComponent data={data} {...props} />;
  };
}

export default withApiData;
