import styled from 'styled-components';
import { Link } from 'react-router-dom';

export const Container = styled.div`
  max-width: 960px;
  margin: 0 auto;
  padding: 20px 20px;
  text-align: center;
`;

export const Title = styled.h1`
  font-size: 3rem;
  margin-bottom: 30px;
  color: ${({ theme }) => theme.text};
`;

// Button group to center buttons
export const ButtonGroup = styled.div`
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 20px;
`;

export const SectionHeading = styled.h3`
  margin-top: 50px;
  font-size: 2rem;
  color: ${({ theme }) => theme.text};
  text-align: center;
`;

export const Description = styled.p`
  font-size: 1.2rem;
  line-height: 1.6;
  margin-bottom: 25px;
  color: ${({ theme }) => theme.text};
`;

export const Form = styled.form`
  max-width: ${({ maxWidth }) => maxWidth || '360px'};
  min-width: ${({ minWidth }) => minWidth || '200px'};
  margin: 40px auto 20px;
  display: flex;
  flex-direction: column;
  gap: ${({ gap }) => gap || '10px'};
`;

export const FormField = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 20px;
  width: 100%;
`;

export const Input = styled.input`
  padding: 12px;
  font-size: 1rem;
  border-radius: 10px;
  border: 1px solid #ccc;
  transition: border-color 0.2s;
  width: 100%;
  box-sizing: border-box;

  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.buttonBg};
  }
`;

export const Label = styled.label`
  font-size: 1rem;
  font-weight: 500;
  color: ${({ theme }) => theme.text};
  margin-bottom: 5px;
  text-align: left;
`;

export const TextLink = styled(Link)`
  color: ${({ theme }) => theme.buttonBg};
  text-decoration: none;
  font-weight: bold;

  &:hover {
    text-decoration: underline;
  }
`;

//
//
//
//
//
export const WalletListContainer = styled.div`
  display: flex;
  position: relative;
  margin: 20px 0;
  overflow: hidden;
  max-width: 960px;
  width: 100%;
  min-width: 600px;
  justify-content: center;

  ul {
    display: flex;
    justify-content: ${({ centered }) => (centered ? 'center' : 'flex-start')};
    gap: 15px;
    overflow-x: auto;
    padding: 0;
    list-style: none;
    scroll-behavior: smooth;
    width: calc(100% - 120px);
    margin: 0 auto;
    padding: 10px 0;
    scrollbar-width: none;
  }

  // @media (max-width: 800px) {
  //   ul {
  //     flex-wrap: wrap;
  //   }
  }
`;

export const WalletCard = styled.li`
  flex: 0 0 calc((100% - 30px) / 3);
  max-width: calc((100% - 30px) / 3);

  padding: 20px;
  border: 1px solid ${({ theme }) => theme.buttonBg};
  border-radius: 10px;
  background: linear-gradient(
    135deg,
    ${({ theme }) => theme.body} 30%,
    #f9f9f9 100%
  );
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-sizing: border-box;
  cursor: pointer;

  &:hover {
    transform: translateY(-3px);
    transition: all 0.3s ease;
  }

  // @media (max-width: 800px) {
  //   flex: 0 0 calc((100% - 30px) / 2);
  // }
`;

export const WalletInfoRow = styled.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
`;

export const WalletAddress = styled.p`
  font-size: 0.85rem;
  color: ${({ theme }) => theme.text};
  text-align: left;
  margin: 5px 0;
  word-break: break-all;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
`;

export const ScrollButton = styled.button`
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: transparent;
  border: none;
  color: ${({ theme }) => theme.buttonBg};
  font-size: 2.5rem;
  cursor: pointer;

  width: 60px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;

  ${({ direction }) => (direction === 'left' ? 'left: 0;' : 'right: 0;')}

  &:hover {
    transform: translateY(calc(-50% - 3px));
    transition: all 0.3s ease;
    opacity: 1;
  }

  &:disabled {
    opacity: 0.4;
    cursor: default;
  }

  // @media (max-width: 800px) {
  //   display: none;
  // }
`;

export const TransactionCard = styled.li``;

export const TransactionInfoRow = styled.p``;
