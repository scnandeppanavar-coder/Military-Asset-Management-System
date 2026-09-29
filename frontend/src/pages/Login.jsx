import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield, Lock, User, AlertCircle, ArrowRight } from 'lucide-react';

export const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = (location.state?.from?.pathname && location.state?.from?.pathname !== '/')
    ? location.state.from.pathname
    : '/dashboard';

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) {
      setError('Please provide both username and password.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await login(username, password);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err?.response?.data?.message || err?.message || 'Authentication failed. Please verify credentials.');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickLogin = (u, p) => {
    setUsername(u);
    setPassword(p);
    setError('');
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        backgroundColor: 'var(--bg-primary)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '24px',
        position: 'relative',
        backgroundImage: 'radial-gradient(ellipse at 50% 20%, rgba(30, 58, 138, 0.2) 0%, rgba(11, 17, 32, 0) 70%)',
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '460px',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-medium)',
          borderRadius: 'var(--radius-lg)',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.7)',
          overflow: 'hidden',
        }}
      >
        {/* Banner Top */}
        <div
          style={{
            backgroundColor: 'rgba(56, 189, 248, 0.08)',
            borderBottom: '1px solid var(--border-subtle)',
            padding: '8px 16px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            fontSize: '0.6875rem',
            color: 'var(--accent-blue)',
            fontWeight: 700,
            letterSpacing: '0.08em',
            textTransform: 'uppercase',
          }}
        >
          <span>Defense Logistics Portal</span>
          <span style={{ color: '#4ade80' }}>DEFCON 5 — NORMAL</span>
        </div>

        <div style={{ padding: '32px' }}>
          {/* Header */}
          <div style={{ textAlign: 'center', marginBottom: '28px' }}>
            <div
              style={{
                width: '54px',
                height: '54px',
                borderRadius: '12px',
                background: 'linear-gradient(135deg, #1e40af, #0284c7)',
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginBottom: '16px',
                boxShadow: '0 0 20px rgba(2, 132, 199, 0.4)',
              }}
            >
              <Shield size={28} color="#ffffff" />
            </div>
            <h1 style={{ margin: '0 0 6px', fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-primary)' }}>
              MAMS Access Portal
            </h1>
            <p style={{ margin: 0, fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
              Military Asset Management & Audited Movement System
            </p>
          </div>

          {/* Error Message */}
          {error && (
            <div
              style={{
                padding: '12px 14px',
                backgroundColor: 'rgba(239, 68, 68, 0.1)',
                border: '1px solid rgba(239, 68, 68, 0.3)',
                borderRadius: '6px',
                marginBottom: '20px',
                display: 'flex',
                alignItems: 'center',
                gap: '10px',
                color: '#f87171',
                fontSize: '0.8125rem',
              }}
            >
              <AlertCircle size={18} style={{ flexShrink: 0 }} />
              <span>{error}</span>
            </div>
          )}

          {/* Form */}
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '18px' }}>
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <User size={14} color="var(--accent-blue)" />
                Operator Username
              </label>
              <input
                type="text"
                id="login-username-input"
                className="form-control"
                placeholder="Enter authorized callsign..."
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                autoComplete="username"
                required
              />
            </div>

            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Lock size={14} color="var(--accent-blue)" />
                Security Password
              </label>
              <input
                type="password"
                id="login-password-input"
                className="form-control"
                placeholder="Enter password..."
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="current-password"
                required
              />
            </div>

            <button
              type="submit"
              id="login-submit-btn"
              disabled={loading}
              className="btn btn-primary"
              style={{
                width: '100%',
                padding: '12px',
                fontSize: '0.9375rem',
                fontWeight: 600,
                marginTop: '6px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '8px',
              }}
            >
              {loading ? (
                <span>Verifying Clearance...</span>
              ) : (
                <>
                  <span>Authenticate & Enter</span>
                  <ArrowRight size={18} />
                </>
              )}
            </button>
          </form>

          {/* Demo Account Quick Switcher */}
          <div style={{ marginTop: '28px', paddingTop: '20px', borderTop: '1px solid var(--border-subtle)' }}>
            <div
              style={{
                fontSize: '0.6875rem',
                fontWeight: 700,
                color: 'var(--text-muted)',
                textTransform: 'uppercase',
                letterSpacing: '0.08em',
                marginBottom: '10px',
                textAlign: 'center',
              }}
            >
              Quick Demo Clearance Switcher
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '8px' }}>
              <button
                type="button"
                id="quick-login-admin"
                onClick={() => handleQuickLogin('admin', 'Admin@123')}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  padding: '8px 12px',
                  backgroundColor: 'rgba(251, 191, 36, 0.08)',
                  border: '1px solid rgba(251, 191, 36, 0.25)',
                  borderRadius: '6px',
                  color: 'var(--text-primary)',
                  fontSize: '0.75rem',
                  cursor: 'pointer',
                  textAlign: 'left',
                }}
              >
                <div>
                  <span style={{ fontWeight: 700, color: 'var(--accent-amber)' }}>ADMIN</span>
                  <span style={{ color: 'var(--text-muted)', marginLeft: '6px' }}>admin / Admin@123</span>
                </div>
                <span style={{ fontSize: '0.6875rem', color: 'var(--text-muted)' }}>Global HQ</span>
              </button>

              <button
                type="button"
                id="quick-login-commander"
                onClick={() => handleQuickLogin('commander', 'Commander@123')}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  padding: '8px 12px',
                  backgroundColor: 'rgba(56, 189, 248, 0.08)',
                  border: '1px solid rgba(56, 189, 248, 0.25)',
                  borderRadius: '6px',
                  color: 'var(--text-primary)',
                  fontSize: '0.75rem',
                  cursor: 'pointer',
                  textAlign: 'left',
                }}
              >
                <div>
                  <span style={{ fontWeight: 700, color: '#38bdf8' }}>BASE COMMANDER</span>
                  <span style={{ color: 'var(--text-muted)', marginLeft: '6px' }}>commander / Commander@123</span>
                </div>
                <span style={{ fontSize: '0.6875rem', color: 'var(--text-muted)' }}>Alpha Garrison</span>
              </button>

              <button
                type="button"
                id="quick-login-logistics"
                onClick={() => handleQuickLogin('logistics', 'Logistics@123')}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  padding: '8px 12px',
                  backgroundColor: 'rgba(168, 85, 247, 0.08)',
                  border: '1px solid rgba(168, 85, 247, 0.25)',
                  borderRadius: '6px',
                  color: 'var(--text-primary)',
                  fontSize: '0.75rem',
                  cursor: 'pointer',
                  textAlign: 'left',
                }}
              >
                <div>
                  <span style={{ fontWeight: 700, color: '#c084fc' }}>LOGISTICS OFFICER</span>
                  <span style={{ color: 'var(--text-muted)', marginLeft: '6px' }}>logistics / Logistics@123</span>
                </div>
                <span style={{ fontSize: '0.6875rem', color: 'var(--text-muted)' }}>Alpha Logistics</span>
              </button>
            </div>
          </div>
        </div>

        {/* Footer Disclaimer */}
        <div
          style={{
            backgroundColor: 'rgba(11, 17, 32, 0.95)',
            borderTop: '1px solid var(--border-subtle)',
            padding: '12px 24px',
            fontSize: '0.6875rem',
            color: 'var(--text-muted)',
            textAlign: 'center',
          }}
        >
          Assessment Environment • Generic Equipment Catalog • ISO 27001 Audited
        </div>
      </div>
    </div>
  );
};
