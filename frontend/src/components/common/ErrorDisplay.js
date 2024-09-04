import React from 'react';

function ErrorDisplay({ errors }) {
  if (!errors || errors.length === 0) {
    return null;
  }

  return (
    <div style={{ color: 'red' }}>
      {errors.map((error, index) => (
        <p key={index}>{error}</p>
      ))}
    </div>
  );
}

export default ErrorDisplay;
