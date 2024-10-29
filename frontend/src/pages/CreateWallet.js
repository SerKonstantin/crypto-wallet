import React, { useState, useEffect } from 'react';
import { Buffer } from 'buffer';
import { generateMnemonic } from 'bip39';
import { HDNodeWallet } from 'ethers';
import usePostRequestWithFeedback from '../hooks/usePostRequestWithFeedback';
import ErrorDisplay from '../components/ErrorDisplay';
import Button from '../components/Button';
import {
  Container,
  ButtonGroup,
  SectionHeading,
  Form,
  FormField,
  Input,
  Label,
} from '../styles/CommonStyles';
import {
  PassphraseGrid,
  PassphraseWord,
  NumberedLabel,
  WarningMessage,
  InfoMessage,
} from '../styles/CreateWalletStyles';

function CreateWallet() {
  const [step, setStep] = useState('intro');
  const [walletName, setWalletName] = useState('');
  const [passphrase, setPassphrase] = useState('');
  const [confirmWords, setConfirmWords] = useState(['', '']);
  const [wordIndices, setWordIndices] = useState([]);
  const [errors, setErrors] = useState({});
  const [infoMessage, setInfoMessage] = useState('');
  const performPostRequestWithFeedback = usePostRequestWithFeedback();

  if (!window.Buffer) {
    window.Buffer = Buffer;
  }

  // Generate the passphrase when the page loads and select two words for confirmation
  useEffect(() => {
    try {
      const generatedPassphrase = generateMnemonic();
      setPassphrase(generatedPassphrase);

      const indices = getRandomIndices(generatedPassphrase.split(' '));
      setWordIndices(indices);
    } catch (e) {
      setErrors({
        passphraseGeneration:
          'Failed to generate passphrase. Please refresh the page or try again later.',
      });
    }
  }, []);

  const getRandomIndices = words => {
    const firstIndex = Math.floor(Math.random() * words.length);
    let secondIndex;
    do {
      secondIndex = Math.floor(Math.random() * words.length);
    } while (secondIndex === firstIndex);
    return [firstIndex, secondIndex];
  };

  const copyPassphraseToClipboard = () => {
    navigator.clipboard
      .writeText(passphrase)
      .then(() => {
        setErrors({});
        setInfoMessage('Passphrase copied to clipboard!');
        setTimeout(() => setInfoMessage(''), 3000);
      })
      .catch(() => {
        setErrors({
          copyToClipboard: 'Failed to copy passphrase to clipboard.',
        });
      });
  };

  const handleConfirmWordChange = (index, value) => {
    setConfirmWords(prevWords => {
      const newWords = [...prevWords];
      newWords[index] = value.trim().toLowerCase();
      return newWords;
    });
  };

  const handlePassphraseConfirmation = () => {
    const words = passphrase.split(' ');

    if (!confirmWords[0] || !confirmWords[1]) {
      setErrors({
        passphraseConfirmation:
          'Please enter both words to confirm your passphrase.',
      });
      return;
    }

    if (
      words[wordIndices[0]] === confirmWords[0] &&
      words[wordIndices[1]] === confirmWords[1]
    ) {
      setStep('walletCreation');
      setErrors({});
    } else {
      setErrors({
        passphraseConfirmation:
          'Passphrase confirmation failed. Please try again or refresh page to try with a new passphrase.',
      });
    }
  };

  // TODO Add wallet name validation before saving
  const createWallet = async () => {
    if (step !== 'walletCreation') {
      setErrors({ form: 'Please confirm your passphrase first.' });
      return;
    }

    const wallet = HDNodeWallet.fromPhrase(passphrase);
    const walletData = {
      name: walletName,
      address: wallet.address,
    };

    performPostRequestWithFeedback({
      url: '/wallets',
      data: walletData,
      successMessage: 'Wallet has been created successfully!',
      redirectTo: '/dashboard',
      setErrors,
    });
  };

  if (errors.passphraseGeneration) {
    return (
      <div>
        <h2>Error</h2>
        <p>{errors.passphraseGeneration}</p>
        <button onClick={() => window.location.reload()}>Try Again</button>
      </div>
    );
  }

  if (step === 'intro') {
    return (
      <Container>
        <SectionHeading>Create Your Wallet</SectionHeading>
        <WarningMessage>
          Your passphrase (seed phrase) is the only way to access your wallet.
          <br />
          <strong>Important:</strong> If you lose it, you’ll lose access to your
          wallet forever. If someone else gains access, they can access your
          funds. Ensure you save it securely.
        </WarningMessage>
        <Button onClick={() => setStep('showPassphrase')}>I Understand</Button>
      </Container>
    );
  }

  if (step === 'showPassphrase') {
    const passphraseWords = passphrase ? passphrase.split(' ') : [];
    return (
      <Container>
        <SectionHeading>Your Passphrase (Seed Phrase):</SectionHeading>
        <PassphraseGrid>
          {passphraseWords.map((word, index) => (
            <PassphraseWord key={index}>
              <NumberedLabel>{index + 1}</NumberedLabel> {word}
            </PassphraseWord>
          ))}
        </PassphraseGrid>
        <ButtonGroup>
          <Button onClick={copyPassphraseToClipboard}>Copy to Clipboard</Button>
          <Button onClick={() => setStep('confirmPassphrase')}>
            I have saved my passphrase
          </Button>
        </ButtonGroup>
        {errors.copyToClipboard && (
          <ErrorDisplay errors={errors.copyToClipboard} />
        )}
        {infoMessage && <InfoMessage>{infoMessage}</InfoMessage>}
      </Container>
    );
  }

  if (step === 'confirmPassphrase') {
    return (
      <Container>
        <SectionHeading>Confirm Your Passphrase</SectionHeading>
        <p>
          Please enter word #{wordIndices[0] + 1} and word #{wordIndices[1] + 1}{' '}
          from your passphrase:
        </p>
        <Form>
          <FormField>
            <Label>Word #{wordIndices[0] + 1}</Label>
            <Input
              type="text"
              value={confirmWords[0]}
              onChange={e => handleConfirmWordChange(0, e.target.value)}
              placeholder={`Enter word #${wordIndices[0] + 1}`}
            />
          </FormField>
          <FormField>
            <Label>Word #{wordIndices[1] + 1}</Label>
            <Input
              type="text"
              value={confirmWords[1]}
              onChange={e => handleConfirmWordChange(1, e.target.value)}
              placeholder={`Enter word #${wordIndices[1] + 1}`}
            />
          </FormField>
          <Button type="button" onClick={handlePassphraseConfirmation}>
            Confirm Passphrase
          </Button>
        </Form>
        {errors.passphraseConfirmation && (
          <ErrorDisplay errors={errors.passphraseConfirmation} />
        )}
      </Container>
    );
  }

  if (step === 'walletCreation') {
    return (
      <Container>
        <SectionHeading>Passphrase confirmed!</SectionHeading>
        <Form>
          <FormField>
            <Label>Wallet Name:</Label>
            <Input
              type="text"
              value={walletName}
              onChange={e => setWalletName(e.target.value.trim())}
              placeholder="Enter wallet name"
            />
          </FormField>
          <Button type="button" onClick={createWallet}>
            Create Wallet
          </Button>
          {errors['wallet name'] && (
            <ErrorDisplay errors={errors['wallet name']} />
          )}
        </Form>
        {errors.form && <ErrorDisplay errors={errors.form} />}
      </Container>
    );
  }
}

export default CreateWallet;
