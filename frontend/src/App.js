import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './dashboard/Dashboard';
import Wallets from './pages/Wallets';
import ThemeWrapper from './styles/ThemeWrapper';
import Navbar from './components/Navbar';

function App() {
  return (
    <ThemeWrapper>
      <Router>
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/wallets" element={<Wallets />} />
        </Routes>
      </Router>
      {/* TODO Footer */}
    </ThemeWrapper>
  );
}

export default App;
