import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Buffer } from 'buffer';
import { generateMnemonic } from 'bip39';
import { HDNodeWallet } from 'ethers';
import axiosClient from '../utils/axiosClient';
import ErrorDisplay from '../components/ErrorDisplay';
// import FlashMessage from '../components/FlashMessage';

function CreateWallet() {
  const [walletName, setWalletName] = useState('');
  const [passphrase, setPassphrase] = useState('');
  const [confirmWord1, setConfirmWord1] = useState('');
  const [confirmWord2, setConfirmWord2] = useState('');
  const [wordIndex1, setWordIndex1] = useState(0);
  const [wordIndex2, setWordIndex2] = useState(0);
  const [isConfirmed, setIsConfirmed] = useState(false);
  const [errors, setErrors] = useState({});
  const [showPassphrase, setShowPassphrase] = useState(true);
  const [infoMessage, setInfoMessage] = useState('');
  const navigate = useNavigate();

  if (!window.Buffer) {
    window.Buffer = Buffer;
  }

  // Generate the passphrase when the page loads and select two words for confirmation
  useEffect(() => {
    try {
      const generatedPassphrase = generateMnemonic();
      setPassphrase(generatedPassphrase);

      const wordIndices = getRandomIndices(generatedPassphrase.split(' ')); // TODO words and indeces in list/arr
      setWordIndex1(wordIndices[0]);
      setWordIndex2(wordIndices[1]);
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
        setErrors([]);
        setInfoMessage('Passphrase copied to clipboard!');
        setTimeout(() => setInfoMessage(''), 3000);
      })
      .catch(() => {
        setErrors({
          copyToClipboard: 'Failed to copy passphrase to clipboard.',
        });
      });
  };

  // Confirm passphrase by asking for specific words
  const handlePassphraseConfirmation = () => {
    const words = passphrase.split(' ');

    if (!confirmWord1 || !confirmWord2) {
      setErrors({
        passphraseConfirmation:
          'Please enter both words to confirm your passphrase.',
      });
      return;
    }

    if (
      words[wordIndex1] === confirmWord1 &&
      words[wordIndex2] === confirmWord2
    ) {
      setIsConfirmed(true);
      setErrors([]);
    } else {
      setErrors({
        passphraseConfirmation:
          'Passphrase confirmation failed. Please try again or refresh page to try with a new passphrase.',
      });
    }
  };

  // Create wallet if passphrase is confirmed
  // TODO Add wallet name validation before saving
  const createWallet = async () => {
    if (!isConfirmed) {
      setErrors({ form: 'Please confirm your passphrase first.' });
      return;
    }

    try {
      const wallet = HDNodeWallet.fromPhrase(passphrase);
      const walletData = {
        name: walletName,
        address: wallet.address,
      };

      await axiosClient.post(`/wallets`, walletData);
      sessionStorage.setItem('flashMessage', 'Wallet created successfully!');
      sessionStorage.setItem('flashType', 'success');

      navigate('/dashboard');
    } catch (err) {
      if (err.response && err.response.data && err.response.data.errors) {
        setErrors(err.response.data.errors);
      } else if (err.response) {
        setErrors({
          form: 'An error occurred during wallet creation. Please try again later.',
        });
      } else if (err.request) {
        setErrors({
          form: 'Unable to reach the server. Please ensure the backend is running.',
        });
      } else {
        setErrors({ form: 'An unexpected error occurred. Please try again.' });
      }
    }
  };

  const confirmPassphraseSaved = () => {
    setShowPassphrase(false);
  };

  return (
    <div>
      <h1>Create Your Wallet</h1>

      {infoMessage && <div>{infoMessage}</div>}

      {/* Display Generated Passphrase */}
      {showPassphrase && (
        <div>
          <h3>Your Passphrase (Seed Phrase):</h3>
          <p>{passphrase}</p>
          <button onClick={copyPassphraseToClipboard}>Copy to Clipboard</button>
          <button onClick={confirmPassphraseSaved}>
            I have saved my passphrase
          </button>
          {errors.copyToClipboard && (
            <ErrorDisplay errors={errors.copyToClipboard} />
          )}
        </div>
      )}

      {/* Passphrase Confirmation */}
      {!showPassphrase && !isConfirmed && (
        <div>
          <h3>Confirm Your Passphrase</h3>
          <p>
            Please enter word #{wordIndex1 + 1} and word #{wordIndex2 + 1} from
            your passphrase:
          </p>
          <input
            type="text"
            value={confirmWord1}
            onChange={e => setConfirmWord1(e.target.value)}
            placeholder={`Enter word #${wordIndex1 + 1}`}
          />
          <input
            type="text"
            value={confirmWord2}
            onChange={e => setConfirmWord2(e.target.value)}
            placeholder={`Enter word #${wordIndex2 + 1}`}
          />
          <button onClick={handlePassphraseConfirmation}>
            Confirm Passphrase
          </button>
          {errors.passphraseConfirmation && (
            <ErrorDisplay errors={errors.passphraseConfirmation} />
          )}
        </div>
      )}

      {/* Wallet name input and create wallet button */}
      {isConfirmed && (
        <div>
          <h3>Passphrase confirmed!</h3>
          <div>
            <label>Wallet Name:</label>
            <input
              type="text"
              value={walletName}
              onChange={e => setWalletName(e.target.value)}
              placeholder="Enter wallet name"
            />
            {errors.name && <ErrorDisplay errors={errors.name} />}
          </div>
          <button onClick={createWallet}>Create Wallet</button>
          {errors.form && <ErrorDisplay errors={errors.form} />}
        </div>
      )}

      {/* Display the wallet address after creation     DELETE LATER*/}
      {/* {walletAddress && (
        <div>
          <h3>Wallet Address:</h3>
          <p>{walletAddress}</p>
        </div>
      )} */}
    </div>
  );
}

export default CreateWallet;
