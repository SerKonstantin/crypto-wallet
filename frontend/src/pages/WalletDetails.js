import React from 'react';
import { useParams } from 'react-router-dom';
import { Container } from '../styles/CommonStyles';

function WalletDetails() {
  const { slug } = useParams();

  return (
    <Container>
      <h1>Page Under Construction</h1>
      <p>I'm still working on this page. Please check back later!</p>
      <br></br>
      <p>Slug test: {slug}</p>
    </Container>
  );
}

export default WalletDetails;
