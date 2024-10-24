import React from 'react';
import { useParams } from 'react-router-dom';
import { Container } from '../styles/CommonStyles';

function TransactionDetails() {
  const { id } = useParams();

  return (
    <Container>
      <h1>Page Under Construction</h1>
      <p>I'm still working on this page. Please check back later!</p>
      <br></br>
      <p>Id catch test: {id}</p>
    </Container>
  );
}

export default TransactionDetails;
