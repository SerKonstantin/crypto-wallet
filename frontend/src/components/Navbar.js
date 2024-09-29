import React from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

const Nav = styled.nav`
  background-color: ${({ theme }) => theme.body};
  padding: 1rem;
`;

const NavList = styled.ul`
  display: flex;
  list-style: none;
  justify-content: center;
  margin: 0;
  padding: 0;
`;

const NavItem = styled.li`
  margin: 0 1rem;
`;

const StyledLink = styled(Link)`
  text-decoration: none;
  color: ${({ theme }) => theme.text};
  font-weight: bold;

  &:hover {
    opacity: 0.8;
  }
`;

const Navbar = () => {
  return (
    <Nav>
      <NavList>
        <NavItem>
          <StyledLink to="/">Home</StyledLink>
        </NavItem>
        <NavItem>
          <StyledLink to="/dashboard">Dashboard</StyledLink>
        </NavItem>
        <NavItem>
          <StyledLink to="/wallets">Wallets</StyledLink>
        </NavItem>
      </NavList>
    </Nav>
  );
};

export default Navbar;
