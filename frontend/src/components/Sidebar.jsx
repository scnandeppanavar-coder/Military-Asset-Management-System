import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  ShoppingCart,
  ArrowLeftRight,
  ClipboardList,
  Flame,
  Users,
  Building2,
  Shield,
  FileText,
  X
} from 'lucide-react';

export const Sidebar = ({ isOpen, onClose }) => {
  const { user } = useAuth();
  const role = user?.role;

  const navItems = [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard, roles: ['ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER'] },
    { to: '/purchases', label: 'Purchases', icon: ShoppingCart, roles: ['ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER'] },
    { to: '/transfers', label: 'Transfers', icon: ArrowLeftRight, roles: ['ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER'] },
    { to: '/assignments', label: 'Assignments', icon: ClipboardList, roles: ['ADMIN', 'BASE_COMMANDER'] },
    { to: '/expenditures', label: 'Expenditures', icon: Flame, roles: ['ADMIN', 'BASE_COMMANDER'] },
  ];

  const adminItems = [
    { to: '/users', label: 'Users Management', icon: Users, roles: ['ADMIN'] },
    { to: '/bases', label: 'Military Bases', icon: Building2, roles: ['ADMIN'] },
    { to: '/equipment', label: 'Equipment Catalog', icon: Shield, roles: ['ADMIN'] },
    { to: '/audit-logs', label: 'Audit Trail Logs', icon: FileText, roles: ['ADMIN'] },
  ];

  return (
    <>
      {/* Mobile backdrop */}
      {isOpen && (
        <div
          onClick={onClose}
          style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(0,0,0,0.6)',
            zIndex: 40,
            display: 'block'
          }}
        />
      )}

      <aside
        style={{
          width: 'var(--sidebar-width)',
          backgroundColor: 'var(--bg-secondary)',
          borderRight: '1px solid var(--border-subtle)',
          display: 'flex',
          flexDirection: 'column',
          zIndex: 50,
          transition: 'transform 0.3s ease',
          position: 'fixed',
          top: 0,
          bottom: 0,
          left: 0,
          transform: isOpen ? 'translateX(0)' : 'translateX(-100%)',
          '@media (min-width: 769px)': {
            position: 'static',
            transform: 'none'
          }
        }}
        className={`sidebar-aside ${isOpen ? 'open' : ''}`}
      >
        {/* Brand Header */}
        <div
          style={{
            height: 'var(--header-height)',
            padding: '0 20px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            borderBottom: '1px solid var(--border-subtle)',
            backgroundColor: 'rgba(11, 17, 32, 0.6)'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div
              style={{
                width: '36px',
                height: '36px',
                borderRadius: '8px',
                background: 'linear-gradient(135deg, #1d4ed8, #06b6d4)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 0 10px rgba(6,182,212,0.4)'
              }}
            >
              <Shield size={20} color="#ffffff" />
            </div>
            <div>
              <div style={{ fontWeight: 800, fontSize: '1rem', letterSpacing: '0.05em', color: '#f8fafc' }}>
                MAMS
              </div>
              <div style={{ fontSize: '0.6875rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.08em' }}>
                Tactical Asset System
              </div>
            </div>
          </div>

          <button
            onClick={onClose}
            className="mobile-close-btn"
            style={{
              background: 'none',
              border: 'none',
              color: 'var(--text-secondary)',
              cursor: 'pointer',
              display: 'none'
            }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Navigation Links */}
        <div style={{ flex: 1, padding: '16px 12px', overflowY: 'auto' }}>
          <div style={{ fontSize: '0.6875rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.08em', padding: '0 12px 8px' }}>
            Operational Command
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
            {navItems
              .filter((item) => item.roles.includes(role))
              .map((item) => {
                const Icon = item.icon;
                return (
                  <NavLink
                    key={item.to}
                    to={item.to}
                    onClick={() => {
                      if (window.innerWidth < 769) onClose();
                    }}
                    style={({ isActive }) => ({
                      display: 'flex',
                      alignItems: 'center',
                      gap: '12px',
                      padding: '10px 14px',
                      borderRadius: 'var(--radius-md)',
                      fontSize: '0.875rem',
                      fontWeight: isActive ? '600' : '500',
                      color: isActive ? '#38bdf8' : 'var(--text-secondary)',
                      backgroundColor: isActive ? 'rgba(56, 189, 248, 0.12)' : 'transparent',
                      borderLeft: isActive ? '3px solid #38bdf8' : '3px solid transparent',
                      textDecoration: 'none',
                      transition: 'all 0.15s'
                    })}
                  >
                    <Icon size={18} />
                    <span>{item.label}</span>
                  </NavLink>
                );
              })}
          </div>

          {/* Admin Navigation */}
          {role === 'ADMIN' && (
            <div style={{ marginTop: '24px' }}>
              <div style={{ fontSize: '0.6875rem', fontWeight: 700, color: 'var(--accent-amber)', textTransform: 'uppercase', letterSpacing: '0.08em', padding: '0 12px 8px' }}>
                HQ System Administration
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                {adminItems.map((item) => {
                  const Icon = item.icon;
                  return (
                    <NavLink
                      key={item.to}
                      to={item.to}
                      onClick={() => {
                        if (window.innerWidth < 769) onClose();
                      }}
                      style={({ isActive }) => ({
                        display: 'flex',
                        alignItems: 'center',
                        gap: '12px',
                        padding: '10px 14px',
                        borderRadius: 'var(--radius-md)',
                        fontSize: '0.875rem',
                        fontWeight: isActive ? '600' : '500',
                        color: isActive ? '#fbbf24' : 'var(--text-secondary)',
                        backgroundColor: isActive ? 'rgba(251, 191, 36, 0.1)' : 'transparent',
                        borderLeft: isActive ? '3px solid #fbbf24' : '3px solid transparent',
                        textDecoration: 'none',
                        transition: 'all 0.15s'
                      })}
                    >
                      <Icon size={18} />
                      <span>{item.label}</span>
                    </NavLink>
                  );
                })}
              </div>
            </div>
          )}
        </div>

        {/* Security Clearance Footer */}
        <div
          style={{
            padding: '16px',
            borderTop: '1px solid var(--border-subtle)',
            backgroundColor: 'rgba(11, 17, 32, 0.6)'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div
              style={{
                width: '32px',
                height: '32px',
                borderRadius: '50%',
                backgroundColor: role === 'ADMIN' ? 'var(--accent-amber)' : 'var(--accent-blue)',
                color: '#0f172a',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 700,
                fontSize: '0.8125rem'
              }}
            >
              {user?.username?.charAt(0).toUpperCase()}
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: '0.8125rem', fontWeight: 600, color: 'var(--text-primary)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {user?.fullName || user?.username}
              </div>
              <div style={{ fontSize: '0.6875rem', color: 'var(--text-muted)' }}>
                {user?.role}
              </div>
            </div>
          </div>
          {user?.baseName && (
            <div
              style={{
                marginTop: '8px',
                fontSize: '0.6875rem',
                color: '#38bdf8',
                backgroundColor: 'rgba(56, 189, 248, 0.08)',
                padding: '4px 8px',
                borderRadius: '4px',
                border: '1px solid rgba(56, 189, 248, 0.2)'
              }}
            >
              Base: {user.baseName}
            </div>
          )}
        </div>
      </aside>

      <style>{`
        @media (min-width: 769px) {
          .sidebar-aside {
            position: static !important;
            transform: none !important;
          }
          .mobile-close-btn {
            display: none !important;
          }
        }
        @media (max-width: 768px) {
          .mobile-close-btn {
            display: flex !important;
          }
        }
      `}</style>
    </>
  );
};
