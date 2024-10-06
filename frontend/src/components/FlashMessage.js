import React, { useState, useEffect } from 'react';

const FlashMessage = () => {
  const [flashMessage, setFlashMessage] = useState(null);

  useEffect(() => {
    const message = sessionStorage.getItem('flashMessage');
    const type = sessionStorage.getItem('flashType');

    if (message) {
      setFlashMessage({ message, type });
      sessionStorage.removeItem('flashMessage');
      sessionStorage.removeItem('flashType');
    }
  }, []);

  if (!flashMessage) {
    return null;
  }

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        backgroundColor:
          flashMessage.type === 'success' ? '#e6ffed' : '#ffe6e6',
        padding: '15px',
        color: flashMessage.type === 'success' ? 'green' : 'red',
        border: `1px solid ${flashMessage.type === 'success' ? '#c2f0c2' : '#ffcccc'}`,
        borderRadius: '8px',
        boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.1)',
      }}
    >
      <div>
        {flashMessage.type === 'success' ? '✔️' : '❌'} {flashMessage.message}
      </div>
      <button
        style={{
          background: 'transparent',
          border: 'none',
          cursor: 'pointer',
          fontSize: '1.2rem',
        }}
        onClick={() => setFlashMessage(null)} // Add logic to dismiss the message
      >
        ✖️
      </button>
    </div>
  );
};

export default FlashMessage;
