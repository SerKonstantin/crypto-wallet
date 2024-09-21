import React, { useState, useEffect, useMemo } from 'react';
import ErrorDisplay from '../components/ErrorDisplay';

function withApiData(WrappedComponent, apiCallback) {
  return function ComponentWithApiData(props) {
    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState([]);
    const [hasError, setHasError] = useState(false);
    const [data, setData] = useState([]);

    // To support both array and non-array args
    const apiCallbacks = useMemo(() => {
      return Array.isArray(apiCallback) ? apiCallback : [apiCallback];
    }, []);

    useEffect(() => {
      const fetchData = async () => {
        setLoading(true);
        const results = [];
        const errorMessages = [];

        for (const callback of apiCallbacks) {
          try {
            const result = await callback();
            results.push(result.data);
            errorMessages.push(null);
          } catch (err) {
            setHasError(true);
            if (err.response) {
              errorMessages.push('An error occurred while fetching data.');
            } else if (err.request) {
              errorMessages.push(
                'Server is unavailable. Please try again later.'
              );
            } else {
              errorMessages.push(
                'An unexpected error occurred. Please try again later.'
              );
            }
          }
        }

        setData(results);
        setErrors(errorMessages.filter(Boolean));
        setLoading(false);
      };

      fetchData();
    }, [apiCallbacks]);

    if (loading) {
      return <div>Loading...</div>;
    }

    if (hasError) {
      return <ErrorDisplay errors={errors} />;
    }

    return <WrappedComponent data={data} {...props} />;
  };
}

export default withApiData;
