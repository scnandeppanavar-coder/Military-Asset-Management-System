import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { useToast } from '../../components/Toast';
import { ConfirmModal } from '../../components/ConfirmModal';
import {
  Building2,
  Plus,
  Search,
  Edit2,
  Trash2,
  MapPin,
  CheckCircle,
  XCircle,
  X
} from 'lucide-react';

export const Bases = () => {
  const { addToast } = useToast();

  const [bases, setBases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  // Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingBase, setEditingBase] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    baseCode: '',
    baseName: '',
    location: '',
    status: 'ACTIVE',
  });

  // Delete
  const [deleteTarget, setDeleteTarget] = useState(null);

  const loadBases = async () => {
    setLoading(true);
    try {
      const res = await api.getBases();
      setBases(res.data || []);
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to load military bases', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBases();
  }, []);

  const handleOpenCreate = () => {
    setEditingBase(null);
    setFormData({
      baseCode: '',
      baseName: '',
      location: '',
      status: 'ACTIVE',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (b) => {
    setEditingBase(b);
    setFormData({
      baseCode: b.baseCode,
      baseName: b.baseName,
      location: b.location || '',
      status: b.status || 'ACTIVE',
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    if (!formData.baseCode.trim() || !formData.baseName.trim()) {
      addToast('Base code and name are required.', 'warning');
      return;
    }

    setSubmitting(true);
    try {
      const payload = {
        baseCode: formData.baseCode.trim().toUpperCase(),
        baseName: formData.baseName.trim(),
        location: formData.location.trim(),
        status: formData.status,
      };

      if (editingBase) {
        await api.updateBase(editingBase.id, payload);
        addToast('Military base updated successfully.', 'success');
      } else {
        await api.createBase(payload);
        addToast('New military base facility registered.', 'success');
      }

      setIsModalOpen(false);
      loadBases();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Operation failed', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await api.deleteBase(deleteTarget.id);
      addToast(`Base ${deleteTarget.baseName} decommissioned.`, 'success');
      loadBases();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to delete base', 'error');
    } finally {
      setDeleteTarget(null);
    }
  };

  const filteredBases = bases.filter((b) => {
    if (!searchTerm) return true;
    const term = searchTerm.toLowerCase();
    return (
      b.baseCode?.toLowerCase().includes(term) ||
      b.baseName?.toLowerCase().includes(term) ||
      b.location?.toLowerCase().includes(term)
    );
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header and Action */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            Military Installations & Base Facilities
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
            Strategic command bases, depots, and forward operating stations
          </p>
        </div>

        <button
          onClick={handleOpenCreate}
          id="btn-add-base"
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <Plus size={16} />
          <span>Register New Base</span>
        </button>
      </div>

      {/* Search Bar */}
      <div className="card" style={{ padding: '16px 20px' }}>
        <div style={{ position: 'relative', maxWidth: '400px' }}>
          <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
          <input
            type="text"
            id="bases-search-input"
            className="form-control"
            placeholder="Search bases by code, name, location..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ paddingLeft: '36px' }}
          />
        </div>
      </div>

      {/* Bases Table */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Base Code</th>
                <th>Installation Name</th>
                <th>Geographical Location</th>
                <th>Operational Status</th>
                <th>Established Date</th>
                <th style={{ textAlign: 'center' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={6} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>Loading military bases...</div>
                  </td>
                </tr>
              ) : filteredBases.length === 0 ? (
                <tr>
                  <td colSpan={6} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>No bases found.</div>
                  </td>
                </tr>
              ) : (
                filteredBases.map((b) => (
                  <tr key={b.id}>
                    <td>
                      <code style={{ fontWeight: 700, color: '#38bdf8', backgroundColor: 'rgba(56, 189, 248, 0.1)', padding: '2px 8px', borderRadius: '4px' }}>
                        {b.baseCode}
                      </code>
                    </td>
                    <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{b.baseName}</td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-secondary)' }}>
                        <MapPin size={14} color="var(--accent-amber)" />
                        <span>{b.location || 'Undisclosed'}</span>
                      </div>
                    </td>
                    <td>
                      {b.status === 'ACTIVE' ? (
                        <span className="badge badge-success">ACTIVE OPERATIONAL</span>
                      ) : (
                        <span className="badge badge-danger">{b.status}</span>
                      )}
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8125rem' }}>
                      {b.createdAt ? String(b.createdAt).substring(0, 10) : '—'}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
                        <button
                          onClick={() => handleOpenEdit(b)}
                          title="Edit base"
                          style={{ background: 'none', border: 'none', color: '#38bdf8', cursor: 'pointer', padding: '4px' }}
                        >
                          <Edit2 size={15} />
                        </button>
                        <button
                          onClick={() => setDeleteTarget(b)}
                          title="Decommission base"
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

      {/* Modal: Add/Edit Base */}
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
              maxWidth: '480px',
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
                {editingBase ? `Edit Base: ${editingBase.baseCode}` : 'Register New Military Base'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSave} style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Base Tactical Code *</label>
                <input
                  type="text"
                  id="base-form-code"
                  className="form-control"
                  placeholder="e.g. ALPHA-1, ECHO-HQ"
                  value={formData.baseCode}
                  onChange={(e) => setFormData({ ...formData, baseCode: e.target.value })}
                  required
                />
              </div>

              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Base Installation Name *</label>
                <input
                  type="text"
                  id="base-form-name"
                  className="form-control"
                  placeholder="e.g. Alpha Garrison Command"
                  value={formData.baseName}
                  onChange={(e) => setFormData({ ...formData, baseName: e.target.value })}
                  required
                />
              </div>

              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Geographical Location</label>
                <input
                  type="text"
                  id="base-form-location"
                  className="form-control"
                  placeholder="e.g. Northern Sector, Grid 44"
                  value={formData.location}
                  onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                />
              </div>

              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Operational Status</label>
                <select
                  className="form-select"
                  id="base-form-status"
                  value={formData.status}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                >
                  <option value="ACTIVE">ACTIVE (Fully Operational)</option>
                  <option value="INACTIVE">INACTIVE (Standby)</option>
                  <option value="DECOMMISSIONED">DECOMMISSIONED</option>
                </select>
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
                  id="base-form-submit-btn"
                  className="btn btn-primary"
                  disabled={submitting}
                >
                  {submitting ? 'Saving...' : editingBase ? 'Update Base' : 'Save Base'}
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
        title={`Decommission Base: ${deleteTarget?.baseName}?`}
        message={`Are you sure you want to remove base "${deleteTarget?.baseName}" (${deleteTarget?.baseCode})? Warning: This base must not have active inventory or movements.`}
      />
    </div>
  );
};
