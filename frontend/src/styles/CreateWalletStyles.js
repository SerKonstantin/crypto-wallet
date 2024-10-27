import styled from 'styled-components';

export const PassphraseGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 15px;
  margin: 2rem 0;

  @media (max-width: 680px) {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  }
`;

export const PassphraseWord = styled.div`
  font-size: 1.3rem;
  font-weight: bold;
  padding: 1rem;
  border: 1px solid ${({ theme }) => theme.primary};
  border-radius: 8px;
  background-color: ${({ theme }) => theme.secondaryBackground};
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const NumberedLabel = styled.span`
  font-size: 0.8rem;
  color: ${({ theme }) => theme.labelColor};
  margin-bottom: 0.5rem;
`;

export const WarningMessage = styled.div`
  background-color: ${({ theme }) => theme.infoBg};
  color: ${({ theme }) => theme.infoText};
  padding: 1.5rem;
  border: 1px solid ${({ theme }) => theme.borderColor};
  border-radius: 8px;
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
  line-height: 1.4;
`;

export const InfoMessage = styled.div`
  background-color: ${({ theme }) => theme.infoBg};
  color: ${({ theme }) => theme.infoText};
  padding: 1rem;
  margin-top: 1rem;
  border: 1px solid ${({ theme }) => theme.borderColor};
  border-radius: 8px;
  font-size: 1rem;
`;
