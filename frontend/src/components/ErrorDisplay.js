import React from 'react';

function ErrorDisplay({ errors }) {
  // To support both array and non-array args
  const errorList = Array.isArray(errors) ? errors : [errors];

  if (!errorList || errorList.length === 0) {
    return null;
  }

  console.log(errorList);

  return (
    <div style={{ color: 'red' }}>
      {errorList.map((error, index) => (
        <p key={index} style={{ margin: '10px' }}>
          {error}
        </p>
      ))}
    </div>
  );
}

export default ErrorDisplay;
