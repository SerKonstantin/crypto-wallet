import React from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';
import Button from '../components/Button';
import {
  Container,
  Title,
  Description,
  ButtonGroup,
  SectionHeading,
} from '../styles/CommonStyles';

const ListContainer = styled.div`
  text-align: left;
  margin: 0 auto;
  max-width: 600px;
`;

const FeatureList = styled.ul`
  margin: 20px 0;
  padding-left: 20px;
`;

const FeatureItem = styled.li`
  font-size: 1.2rem;
  color: ${({ theme }) => theme.text};
  margin-bottom: 12px;
`;

const TechnologyList = styled(FeatureList)``;
const TechnologyItem = styled(FeatureItem)``;

function Home() {
  return (
    <Container>
      <Title>Welcome to Crypto Wallet App</Title>
      <Description>
        Manage your Ethereum wallets in one place. The Crypto Wallet App allows
        you to add multiple wallets and perform transactions. Currently, the app
        only supports Ethereum transactions.
      </Description>

      <Description>
        For demonstration purposes, the app uses the Sepolia testnet, allowing
        you to explore wallet management features safely without using real
        assets. Try it out now!
      </Description>

      <ButtonGroup>
        <Button as={Link} to="/login">
          Login
        </Button>
        <Button as={Link} to="/register">
          Register
        </Button>
      </ButtonGroup>

      <SectionHeading>Main Features:</SectionHeading>
      <ListContainer>
        <FeatureList>
          <FeatureItem>
            Register an account with secure authentication using JWT tokens
          </FeatureItem>
          <FeatureItem>Add and manage multiple Ethereum wallets</FeatureItem>
          <FeatureItem>
            Wallet balances are fetched directly from the blockchain
          </FeatureItem>
          <FeatureItem>Send and receive Ethereum transactions</FeatureItem>
          <FeatureItem>View transactions history</FeatureItem>
        </FeatureList>
      </ListContainer>

      <SectionHeading>Technologies Used:</SectionHeading>
      <ListContainer>
        <TechnologyList>
          <TechnologyItem>React, JavaScript (Frontend)</TechnologyItem>
          <TechnologyItem>Spring Boot, PostgreSQL (Backend)</TechnologyItem>
          <TechnologyItem>Web3.js for blockchain interaction</TechnologyItem>
          <TechnologyItem>Docker, Sentry, GitHub Actions CI</TechnologyItem>
        </TechnologyList>
      </ListContainer>
    </Container>
  );
}

export default Home;
