import React, { useState } from 'react';
import { Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastProvider } from './components/Toast';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Sidebar } from './components/Sidebar';
import { Navbar } from './components/Navbar';

// Pages
import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { Purchases } from './pages/Purchases';
import { Transfers } from './pages/Transfers';
import { Assignments } from './pages/Assignments';
import { Expenditures } from './pages/Expenditures';

// Admin Pages
import { Users } from './pages/admin/Users';
import { Bases } from './pages/admin/Bases';
import { Equipment } from './pages/admin/Equipment';
import { AuditLogs } from './pages/admin/AuditLogs';

// App Layout with Sidebar, Navbar, and Content area
const MainLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div style={{ display: 'flex', minHeight: '100vh', backgroundColor: 'var(--bg-primary)' }}>
      {/* Sidebar */}
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      {/* Main Content Area */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
        <Navbar onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} />
        <main style={{ flex: 1, padding: '24px', overflowY: 'auto' }}>
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export const App = () => {
  return (
    <AuthProvider>
      <ToastProvider>
        <Routes>
          {/* Public Auth Route */}
          <Route path="/login" element={<Login />} />

          {/* Protected Application Routes */}
          <Route element={<ProtectedRoute />}>
            <Route element={<MainLayout />}>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              
              {/* Dashboard: All roles */}
              <Route path="/dashboard" element={<Dashboard />} />

              {/* Purchases: All roles */}
              <Route path="/purchases" element={<Purchases />} />

              {/* Transfers: All roles */}
              <Route path="/transfers" element={<Transfers />} />

              {/* Assignments: Admin & Base Commander only */}
              <Route
                path="/assignments"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN', 'BASE_COMMANDER']}>
                    <Assignments />
                  </ProtectedRoute>
                }
              />

              {/* Expenditures: Admin & Base Commander only */}
              <Route
                path="/expenditures"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN', 'BASE_COMMANDER']}>
                    <Expenditures />
                  </ProtectedRoute>
                }
              />

              {/* Admin Only Routes */}
              <Route
                path="/users"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <Users />
                  </ProtectedRoute>
                }
              />

              <Route
                path="/bases"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <Bases />
                  </ProtectedRoute>
                }
              />

              <Route
                path="/equipment"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <Equipment />
                  </ProtectedRoute>
                }
              />

              <Route
                path="/audit-logs"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <AuditLogs />
                  </ProtectedRoute>
                }
              />
            </Route>
          </Route>

          {/* Catch-all redirect */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </ToastProvider>
    </AuthProvider>
  );
};

export default App;
