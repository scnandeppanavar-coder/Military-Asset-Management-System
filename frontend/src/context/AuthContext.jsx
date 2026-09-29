import React, { createContext, useContext, useState, useEffect } from 'react';
import { login as apiLogin, getCurrentUser } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('mams_token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      const storedToken = localStorage.getItem('mams_token');
      const storedUser = localStorage.getItem('mams_user');

      if (storedToken && storedUser) {
        try {
          setUser(JSON.parse(storedUser));
          setToken(storedToken);
          // Refresh user data from server in background
          try {
            const freshUser = await getCurrentUser();
            const freshData = freshUser?.data || freshUser;
            setUser(freshData);
            localStorage.setItem('mams_user', JSON.stringify(freshData));
          } catch (e) {
            console.warn('Could not refresh user session profile');
          }
        } catch (err) {
          console.error('Failed to parse cached user data', err);
          logout();
        }
      }
      setLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (username, password) => {
    const response = await apiLogin({ username, password });
    const payload = response?.data || response;
    const { token: jwtToken, ...userData } = payload;
    
    setToken(jwtToken);
    setUser(userData);
    localStorage.setItem('mams_token', jwtToken);
    localStorage.setItem('mams_user', JSON.stringify(userData));
    return userData;
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('mams_token');
    localStorage.removeItem('mams_user');
  };

  const hasRole = (role) => {
    return user?.role === role;
  };

  const hasAnyRole = (roles = []) => {
    return roles.includes(user?.role);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token,
        loading,
        login,
        logout,
        hasRole,
        hasAnyRole
      }}
    >
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
