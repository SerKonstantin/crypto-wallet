import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ThemeWrapper from './styles/ThemeWrapper';
import Navbar from './components/Navbar';
import Colors from './pages/Colors';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './dashboard/Dashboard';
import CreateWallet from './pages/CreateWallet';
import WalletDetails from './pages/WalletDetails';
import TransactionDetails from './pages/TransactionDetails';
import FlashMessage from './components/FlashMessage';

function App() {
  return (
    <ThemeWrapper>
      <Router>
        <Navbar />
        <FlashMessage />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/colors" element={<Colors />} />
          <Route path="/create-wallet" element={<CreateWallet />} />
          <Route path="/wallets/:slug" element={<WalletDetails />} />
          <Route path="/transactions/:id" element={<TransactionDetails />} />
        </Routes>
      </Router>
      {/* TODO Footer */}
    </ThemeWrapper>
  );
}

export default App;
