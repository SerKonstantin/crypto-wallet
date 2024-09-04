import 'regenerator-runtime/runtime'; // Ensure regenerator-runtime is available
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import Dashboard from '../components/dashboard/Dashboard';
import useFetchWallets from '../components/dashboard/useFetchWallets';
import useFetchTransactions from '../components/dashboard/useFetchTransactions';

jest.mock('../components/dashboard/useFetchWallets');
jest.mock('../components/dashboard/useFetchTransactions');

describe('Dashboard Component', () => {
  test('renders wallets and transactions', () => {
    useFetchWallets.mockReturnValue({
      wallets: [
        {
          id: 1,
          name: 'Wallet 1',
          address: '0x1234567890abcdef',
          balance: '1.0',
        },
      ],
      error: null,
    });

    useFetchTransactions.mockReturnValue({
      transactions: [
        {
          id: 1,
          type: 'SEND',
          amount: '1000000000000000000',
          status: 'COMPLETED',
        },
      ],
      error: null,
    });

    render(<Dashboard />);

    expect(
      screen.getByText((content, element) => content.includes('Wallet 1'))
    ).toBeInTheDocument();
    expect(
      screen.getByText((content, element) =>
        content.includes('0x1234567890abcdef')
      )
    ).toBeInTheDocument();
    expect(
      screen.getByText((content, element) => content.includes('SEND: 1 ETH'))
    ).toBeInTheDocument();
  });

  test('shows error messages', () => {
    useFetchWallets.mockReturnValue({
      wallets: [],
      error: 'Custom error with wallets',
    });

    useFetchTransactions.mockReturnValue({
      transactions: [],
      error: 'Custom error with transactions',
    });

    render(<Dashboard />);

    expect(
      screen.getByText((content, element) =>
        content.includes("You don't have any wallets yet")
      )
    ).toBeInTheDocument();
    expect(
      screen.getByText((content, element) =>
        content.includes('Custom error with wallets')
      )
    ).toBeInTheDocument();
    expect(
      screen.getByText((content, element) =>
        content.includes('Custom error with transactions')
      )
    ).toBeInTheDocument();
  });
});
