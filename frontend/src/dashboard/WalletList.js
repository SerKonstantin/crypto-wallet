import React, { useRef, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import {
  Description,
  SmallText,
  TextLink,
  SectionHeading,
} from '../styles/CommonStyles';

const WalletListContainer = styled.div`
  display: flex;
  position: relative;
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
`;

const WalletCard = styled.li`
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
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-sizing: border-box;
  cursor: pointer;

  &:hover {
    background: ${({ theme }) => theme.hoverBg};
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  }
`;

const WalletInfoRow = styled.div`
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
`;

const WalletAddress = styled.p`
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

const ScrollButton = styled.button`
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
`;

function WalletList({ wallets }) {
  const listRef = useRef(null);
  const navigate = useNavigate();
  const [scrollPosition, setScrollPosition] = useState(0);
  const [cardWidth, setCardWidth] = useState(300);
  const [isLeftDisabled, setIsLeftDisabled] = useState(true);
  const [isRightDisabled, setIsRightDisabled] = useState(false);

  // Set card width based on screen size
  useEffect(() => {
    if (listRef.current) {
      const firstCard = listRef.current.querySelector('li');
      if (firstCard) {
        const cardWidth = firstCard.offsetWidth + 15;
        setCardWidth(cardWidth);
      }
    }
  }, [wallets]);

  // Disable scroll buttons at list start/end
  useEffect(() => {
    if (listRef.current) {
      const maxScroll =
        listRef.current.scrollWidth - listRef.current.clientWidth;
      setIsLeftDisabled(scrollPosition <= 0);
      setIsRightDisabled(scrollPosition >= maxScroll);
    }
  }, [scrollPosition, wallets.length]);

  // Actual scroll process on click
  const handleScroll = direction => {
    if (!listRef.current) return;

    const maxScroll = listRef.current.scrollWidth - listRef.current.clientWidth;
    const newScrollPosition =
      direction === 'left'
        ? scrollPosition - cardWidth
        : scrollPosition + cardWidth;

    if (newScrollPosition >= 0 && newScrollPosition <= maxScroll) {
      listRef.current.scrollTo({
        left: newScrollPosition,
        behavior: 'smooth',
      });
      setScrollPosition(newScrollPosition);
    }
  };

  const showScrollButtons = wallets.length > 3;

  if (wallets.length === 0) {
    return (
      <div>
        <SectionHeading>Wallets</SectionHeading>
        <Description>
          <TextLink to="/create-wallet">Create your first wallet</TextLink>
        </Description>
      </div>
    );
  }

  return (
    <div style={{ margin: '40px 0' }}>
      <WalletListContainer centered={wallets.length < 3}>
        {showScrollButtons && (
          <ScrollButton
            direction="left"
            onClick={() => handleScroll('left')}
            disabled={isLeftDisabled}
          >
            ◀
          </ScrollButton>
        )}

        <ul ref={listRef}>
          {wallets.map(wallet => (
            <WalletCard
              key={wallet.id}
              onClick={() => navigate(`/wallets/${wallet.slug}`)}
            >
              <WalletInfoRow>
                <strong>{wallet.name}</strong>
                <span>
                  {wallet.balance !== undefined
                    ? `${parseFloat(wallet.balance).toFixed(6)} ETH`
                    : 'Fetching...'}
                </span>
              </WalletInfoRow>
              <WalletAddress title={wallet.address}>
                {wallet.address.slice(0, 6)}...{wallet.address.slice(-6)}
              </WalletAddress>
            </WalletCard>
          ))}

          {/* Empty card for creating a wallet if user has just one wallet */}
          {wallets.length < 2 && (
            <WalletCard onClick={() => navigate('/create-wallet')}>
              <strong style={{ fontSize: '2rem', textAlign: 'center' }}>
                +
              </strong>
              <WalletAddress style={{ textAlign: 'center' }}>
                Add new wallet
              </WalletAddress>
            </WalletCard>
          )}
        </ul>

        {showScrollButtons && (
          <ScrollButton
            direction="right"
            onClick={() => handleScroll('right')}
            disabled={isRightDisabled}
          >
            ▶
          </ScrollButton>
        )}
      </WalletListContainer>

      {/* Wallet creation link in case 2 or more wallets */}
      {wallets.length >= 2 && (
        <SmallText>
          Need more wallets?{' '}
          <TextLink to="/create-wallet">Create a new wallet</TextLink>
        </SmallText>
      )}
    </div>
  );
}

export default WalletList;
