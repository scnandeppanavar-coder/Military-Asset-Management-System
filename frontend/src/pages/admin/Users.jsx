import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { useToast } from '../../components/Toast';
import { ConfirmModal } from '../../components/ConfirmModal';
import {
  Users as UsersIcon,
  Plus,
  Search,
  Edit2,
  Trash2,
  Shield,
  Building,
  Mail,
  User,
  Key,
  X
} from 'lucide-react';

export const Users = () => {
  const { addToast } = useToast();

  const [users, setUsers] = useState([]);
  const [bases, setBases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  // Add/Edit modal state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    fullName: '',
    email: '',
    role: 'LOGISTICS_OFFICER',
    baseId: '',
  });

  // Delete modal state
  const [deleteTarget, setDeleteTarget] = useState(null);

  const loadData = async () => {
    setLoading(true);
    try {
      const [usersRes, basesRes] = await Promise.all([
        api.getUsers(),
        api.getBases().catch(() => ({ data: [] })),
      ]);
      setUsers(usersRes.data || []);
      setBases(basesRes.data || []);
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to load user directory', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleOpenCreate = () => {
    setEditingUser(null);
    setFormData({
      username: '',
      password: '',
      fullName: '',
      email: '',
      role: 'LOGISTICS_OFFICER',
      baseId: bases[0]?.id ? String(bases[0].id) : '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (user) => {
    setEditingUser(user);
    setFormData({
      username: user.username,
      password: '', // Leave blank to keep unchanged
      fullName: user.fullName || '',
      email: user.email || '',
      role: user.role,
      baseId: user.baseId ? String(user.baseId) : '',
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    if (!formData.username.trim() || !formData.fullName.trim() || !formData.email.trim()) {
      addToast('Please fill out all required fields.', 'warning');
      return;
    }

    if (!editingUser && !formData.password.trim()) {
      addToast('Password is required for new accounts.', 'warning');
      return;
    }

    setSubmitting(true);
    try {
      const payload = {
        username: formData.username.trim(),
        fullName: formData.fullName.trim(),
        email: formData.email.trim(),
        role: formData.role,
        baseId: formData.baseId ? Number(formData.baseId) : null,
      };

      if (formData.password) {
        payload.password = formData.password;
      }

      if (editingUser) {
        await api.updateUser(editingUser.id, payload);
        addToast('Operator profile updated successfully.', 'success');
      } else {
        await api.createUser(payload);
        addToast('New system user created successfully.', 'success');
      }

      setIsModalOpen(false);
      loadData();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Operation failed.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await api.deleteUser(deleteTarget.id);
      addToast(`User ${deleteTarget.username} revoked.`, 'success');
      loadData();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to delete user', 'error');
    } finally {
      setDeleteTarget(null);
    }
  };

  const filteredUsers = users.filter((u) => {
    if (!searchTerm) return true;
    const term = searchTerm.toLowerCase();
    return (
      u.username?.toLowerCase().includes(term) ||
      u.fullName?.toLowerCase().includes(term) ||
      u.email?.toLowerCase().includes(term) ||
      u.role?.toLowerCase().includes(term) ||
      u.baseName?.toLowerCase().includes(term)
    );
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header and Action */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            Personnel Accounts & Security Roles
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
            Administrative management of operator access credentials, role-based clearances, and base jurisdiction
          </p>
        </div>

        <button
          onClick={handleOpenCreate}
          id="btn-add-user"
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <Plus size={16} />
          <span>Provision New User</span>
        </button>
      </div>

      {/* Search Bar */}
      <div className="card" style={{ padding: '16px 20px' }}>
        <div style={{ position: 'relative', maxWidth: '400px' }}>
          <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
          <input
            type="text"
            id="users-search-input"
            className="form-control"
            placeholder="Search operators by username, email, base..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ paddingLeft: '36px' }}
          />
        </div>
      </div>

      {/* Users Table */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Operator</th>
                <th>Full Name</th>
                <th>Email Address</th>
                <th>Role Clearance</th>
                <th>Assigned Jurisdiction Base</th>
                <th>Created</th>
                <th style={{ textAlign: 'center' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={7} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>Loading user roster...</div>
                  </td>
                </tr>
              ) : filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan={7} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>No accounts found.</div>
                  </td>
                </tr>
              ) : (
                filteredUsers.map((u) => (
                  <tr key={u.id}>
                    <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <div
                          style={{
                            width: '28px',
                            height: '28px',
                            borderRadius: '50%',
                            backgroundColor: u.role === 'ADMIN' ? 'var(--accent-amber)' : '#38bdf8',
                            color: '#0f172a',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontWeight: 700,
                            fontSize: '0.75rem',
                          }}
                        >
                          {u.username?.charAt(0).toUpperCase()}
                        </div>
                        <span>{u.username}</span>
                      </div>
                    </td>
                    <td>{u.fullName}</td>
                    <td style={{ color: 'var(--text-secondary)' }}>{u.email}</td>
                    <td>
                      {u.role === 'ADMIN' && <span className="badge badge-warning">ADMIN (Global)</span>}
                      {u.role === 'BASE_COMMANDER' && <span className="badge badge-info">BASE COMMANDER</span>}
                      {u.role === 'LOGISTICS_OFFICER' && <span className="badge badge-success">LOGISTICS OFFICER</span>}
                    </td>
                    <td>
                      {u.baseName ? (
                        <span style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{u.baseName}</span>
                      ) : (
                        <span style={{ color: 'var(--text-muted)', fontStyle: 'italic' }}>Global HQ (Unrestricted)</span>
                      )}
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8125rem' }}>
                      {u.createdAt ? String(u.createdAt).substring(0, 10) : '—'}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
                        <button
                          onClick={() => handleOpenEdit(u)}
                          title="Edit operator"
                          style={{ background: 'none', border: 'none', color: '#38bdf8', cursor: 'pointer', padding: '4px' }}
                        >
                          <Edit2 size={15} />
                        </button>
                        <button
                          onClick={() => setDeleteTarget(u)}
                          title="Delete user"
                          style={{ background: 'none', border: 'none', color: '#f87171', cursor: 'pointer', padding: '4px' }}
                        >
                          <Trash2 size={15} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal: Add / Edit User */}
      {isModalOpen && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.75)',
            backdropFilter: 'blur(4px)',
            zIndex: 100,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px',
          }}
          onClick={() => setIsModalOpen(false)}
        >
          <div
            style={{
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-medium)',
              borderRadius: 'var(--radius-lg)',
              width: '100%',
              maxWidth: '520px',
              overflow: 'hidden',
              boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.5)',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <div
              style={{
                padding: '16px 20px',
                borderBottom: '1px solid var(--border-subtle)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
              }}
            >
              <h3 style={{ margin: 0, fontSize: '1.125rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                {editingUser ? `Edit Operator: ${editingUser.username}` : 'Provision New System User'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSave} style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Username *</label>
                  <input
                    type="text"
                    id="user-form-username"
                    className="form-control"
                    value={formData.username}
                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                    disabled={Boolean(editingUser)}
                    required
                  />
                </div>

                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">{editingUser ? 'Password (Leave blank to keep)' : 'Password *'}</label>
                  <input
                    type="password"
                    id="user-form-password"
                    className="form-control"
                    placeholder={editingUser ? '••••••••' : 'Min 6 characters'}
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    required={!editingUser}
                  />
                </div>
              </div>

              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Full Name & Rank *</label>
                <input
                  type="text"
                  id="user-form-fullname"
                  className="form-control"
                  placeholder="e.g. Major General Sarah Vance"
                  value={formData.fullName}
                  onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                  required
                />
              </div>

              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Official Email *</label>
                <input
                  type="email"
                  id="user-form-email"
                  className="form-control"
                  placeholder="e.g. s.vance@mams.mil"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  required
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Role Clearance *</label>
                  <select
                    className="form-select"
                    id="user-form-role"
                    value={formData.role}
                    onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                    required
                  >
                    <option value="ADMIN">ADMIN (Full HQ Control)</option>
                    <option value="BASE_COMMANDER">BASE COMMANDER</option>
                    <option value="LOGISTICS_OFFICER">LOGISTICS OFFICER</option>
                  </select>
                </div>

                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Assigned Base</label>
                  <select
                    className="form-select"
                    id="user-form-base"
                    value={formData.baseId}
                    onChange={(e) => setFormData({ ...formData, baseId: e.target.value })}
                  >
                    <option value="">None / Global HQ</option>
                    {bases.map((b) => (
                      <option key={b.id} value={b.id}>
                        {b.baseCode} — {b.baseName}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setIsModalOpen(false)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  id="user-form-submit-btn"
                  className="btn btn-primary"
                  disabled={submitting}
                >
                  {submitting ? 'Saving...' : editingUser ? 'Update User' : 'Create User'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation */}
      <ConfirmModal
        isOpen={Boolean(deleteTarget)}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title={`Revoke Access for ${deleteTarget?.username}?`}
        message={`Are you sure you want to delete user account "${deleteTarget?.username}" (${deleteTarget?.fullName})? This action is recorded in the permanent audit trail.`}
      />
    </div>
  );
};
