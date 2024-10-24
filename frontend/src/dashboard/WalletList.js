import React, { useRef, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  WalletCard,
  WalletInfoRow,
  WalletListContainer,
  ScrollButton,
  Description,
  SmallText,
  WalletAddress,
  TextLink,
  SectionHeading,
} from '../styles/CommonStyles';

function WalletList({ wallets }) {
  const listRef = useRef(null);
  const navigate = useNavigate();
  const [scrollPosition, setScrollPosition] = useState(0);
  const [cardWidth, setCardWidth] = useState(300);
  const [isLeftDisabled, setIsLeftDisabled] = useState(true);
  const [isRightDisabled, setIsRightDisabled] = useState(false);

  useEffect(() => {
    if (listRef.current) {
      const firstCard = listRef.current.querySelector('li');
      if (firstCard) {
        const cardWidth = firstCard.offsetWidth + 15;
        setCardWidth(cardWidth);
      }
    }
  }, [wallets]);

  useEffect(() => {
    if (listRef.current) {
      const maxScroll =
        listRef.current.scrollWidth - listRef.current.clientWidth;
      setIsLeftDisabled(scrollPosition <= 0);
      setIsRightDisabled(scrollPosition >= maxScroll);
    }
  }, [scrollPosition, wallets.length]);

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
