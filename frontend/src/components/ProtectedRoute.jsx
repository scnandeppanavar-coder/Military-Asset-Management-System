import React from 'react';
import { Navigate, useLocation, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const ProtectedRoute = ({ children, allowedRoles = [] }) => {
  const { isAuthenticated, user } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(user?.role)) {
    return (
      <div style={{ padding: '40px', textAlign: 'center' }}>
        <h2 style={{ color: 'var(--accent-rose)', marginBottom: '12px' }}>Access Denied</h2>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '20px' }}>
          Your security clearance role (<strong>{user?.role}</strong>) does not have authorization to view this resource.
        </p>
        <Navigate to="/dashboard" replace />
      </div>
    );
  }

  return children ? children : <Outlet />;
};
