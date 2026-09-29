import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../services/api';
import { useToast } from '../components/Toast';
import {
  ArrowLeftRight,
  Plus,
  Search,
  Filter,
  ArrowRight,
  ShieldAlert,
  Building,
  Calendar,
  Layers,
  X
} from 'lucide-react';

export const Transfers = () => {
  const { user } = useAuth();
  const { addToast } = useToast();

  const [transfers, setTransfers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);

  // Filters
  const [searchTerm, setSearchTerm] = useState('');
  const [filterBaseId, setFilterBaseId] = useState(user?.baseId ? String(user.baseId) : '');

  // Create Transfer Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [newTransfer, setNewTransfer] = useState({
    fromBaseId: user?.baseId ? String(user.baseId) : '',
    toBaseId: '',
    equipmentTypeId: '',
    quantity: '',
    transferDate: new Date().toISOString().split('T')[0],
    referenceNumber: '',
    remarks: '',
  });

  useEffect(() => {
    const fetchMetadata = async () => {
      try {
        const [basesRes, equipRes] = await Promise.all([
          api.getBases().catch(() => ({ data: [] })),
          api.getEquipmentTypes().catch(() => ({ data: [] })),
        ]);
        setBases(basesRes.data || []);
        setEquipmentTypes(equipRes.data || []);
      } catch (err) {
        console.error('Error fetching metadata', err);
      }
    };
    fetchMetadata();
  }, []);

  const loadTransfers = async () => {
    setLoading(true);
    try {
      const params = {};
      if (user?.role === 'BASE_COMMANDER' && user?.baseId) {
        params.baseId = user.baseId;
      } else if (filterBaseId) {
        params.baseId = filterBaseId;
      }

      const res = await api.getTransfers(params);
      setTransfers(res.data || []);
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to load transfer history', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTransfers();
  }, [filterBaseId]);

  const handleCreateTransfer = async (e) => {
    e.preventDefault();

    if (!newTransfer.fromBaseId || !newTransfer.toBaseId || !newTransfer.equipmentTypeId || !newTransfer.quantity) {
      addToast('Please fill out all required fields.', 'warning');
      return;
    }

    if (String(newTransfer.fromBaseId) === String(newTransfer.toBaseId)) {
      addToast('Source and Destination base cannot be the same.', 'error');
      return;
    }

    if (Number(newTransfer.quantity) <= 0) {
      addToast('Transfer quantity must be greater than zero.', 'warning');
      return;
    }

    setSubmitting(true);
    try {
      await api.createTransfer({
        fromBaseId: Number(newTransfer.fromBaseId),
        toBaseId: Number(newTransfer.toBaseId),
        equipmentTypeId: Number(newTransfer.equipmentTypeId),
        quantity: Number(newTransfer.quantity),
        transferDate: newTransfer.transferDate,
        referenceNumber: newTransfer.referenceNumber.trim() || `TRF-${Date.now().toString().slice(-6)}`,
        remarks: newTransfer.remarks,
      });

      addToast('Transfer dispatched! Inventory automatically updated (TRANSFER_OUT & TRANSFER_IN).', 'success');
      setIsModalOpen(false);
      setNewTransfer({
        fromBaseId: user?.baseId ? String(user.baseId) : '',
        toBaseId: '',
        equipmentTypeId: '',
        quantity: '',
        transferDate: new Date().toISOString().split('T')[0],
        referenceNumber: '',
        remarks: '',
      });
      loadTransfers();
    } catch (err) {
      const errMsg = err?.response?.data?.message || err?.message || 'Transfer failed';
      addToast(`Transfer Rejected: ${errMsg}`, 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const filteredTransfers = transfers.filter((t) => {
    if (!searchTerm) return true;
    const term = searchTerm.toLowerCase();
    return (
      t.referenceNumber?.toLowerCase().includes(term) ||
      t.fromBaseName?.toLowerCase().includes(term) ||
      t.toBaseName?.toLowerCase().includes(term) ||
      t.equipmentName?.toLowerCase().includes(term) ||
      t.remarks?.toLowerCase().includes(term) ||
      t.createdByName?.toLowerCase().includes(term)
    );
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header and Action */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            Inter-Base Asset Transfers
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
            Transactional movement between bases (Atomic TRANSFER_OUT & TRANSFER_IN with inventory validation)
          </p>
        </div>

        <button
          onClick={() => {
            setNewTransfer({
              fromBaseId: user?.baseId ? String(user.baseId) : (bases[0]?.id ? String(bases[0].id) : ''),
              toBaseId: bases[1]?.id ? String(bases[1].id) : '',
              equipmentTypeId: equipmentTypes[0]?.id ? String(equipmentTypes[0].id) : '',
              quantity: '',
              transferDate: new Date().toISOString().split('T')[0],
              referenceNumber: `TRF-${Date.now().toString().slice(-6)}`,
              remarks: '',
            });
            setIsModalOpen(true);
          }}
          id="btn-create-transfer"
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <Plus size={16} />
          <span>Dispatch New Transfer</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="card" style={{ padding: '16px 20px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '14px', alignItems: 'center' }}>
          {/* Search */}
          <div style={{ position: 'relative' }}>
            <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
            <input
              type="text"
              id="transfers-search-input"
              className="form-control"
              placeholder="Search reference, bases, equipment..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{ paddingLeft: '36px' }}
            />
          </div>

          {/* Base Filter */}
          <div>
            {user?.role === 'BASE_COMMANDER' ? (
              <input
                type="text"
                className="form-control"
                value={`Jurisdiction: ${user?.baseName || 'Assigned'}`}
                disabled
                style={{ opacity: 0.8, cursor: 'not-allowed' }}
              />
            ) : (
              <select
                id="transfers-filter-base"
                className="form-select"
                value={filterBaseId}
                onChange={(e) => setFilterBaseId(e.target.value)}
              >
                <option value="">All Bases Movement History</option>
                {bases.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.baseCode} — {b.baseName}
                  </option>
                ))}
              </select>
            )}
          </div>
        </div>
      </div>

      {/* Transfers History Table */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Source Base (Out)</th>
                <th style={{ textAlign: 'center' }}>Route</th>
                <th>Destination Base (In)</th>
                <th>Equipment Asset</th>
                <th style={{ textAlign: 'right' }}>Quantity</th>
                <th>Reference #</th>
                <th>Status</th>
                <th>Authorized By</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={9} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>Loading transfer ledger...</div>
                  </td>
                </tr>
              ) : filteredTransfers.length === 0 ? (
                <tr>
                  <td colSpan={9} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>No transfers found matching current criteria.</div>
                  </td>
                </tr>
              ) : (
                filteredTransfers.map((t) => (
                  <tr key={t.id}>
                    <td style={{ whiteSpace: 'nowrap', fontWeight: 600 }}>{t.transferDate}</td>
                    <td>
                      <span className="badge badge-danger">{t.fromBaseName || `Base #${t.fromBaseId}`}</span>
                    </td>
                    <td style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                      <ArrowRight size={14} style={{ display: 'inline' }} />
                    </td>
                    <td>
                      <span className="badge badge-success">{t.toBaseName || `Base #${t.toBaseId}`}</span>
                    </td>
                    <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{t.equipmentName}</td>
                    <td style={{ textAlign: 'right', fontWeight: 700, color: 'var(--text-primary)' }}>
                      {t.quantity.toLocaleString()} {t.unit || 'units'}
                    </td>
                    <td>
                      <code style={{ color: 'var(--accent-blue)', backgroundColor: 'rgba(56, 189, 248, 0.08)', padding: '2px 6px', borderRadius: '4px' }}>
                        {t.referenceNumber}
                      </code>
                    </td>
                    <td>
                      <span className="badge badge-info">{t.status || 'COMPLETED'}</span>
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8125rem' }}>{t.createdByName || 'Officer'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create Transfer Modal */}
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
              maxWidth: '540px',
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
                Dispatch Asset Transfer
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleCreateTransfer} style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
              {/* Source Base */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Origin / Source Base (Transfer Out) *</label>
                {user?.role === 'BASE_COMMANDER' ? (
                  <input type="text" className="form-control" value={user?.baseName || 'Assigned Base'} disabled />
                ) : (
                  <select
                    className="form-select"
                    id="new-transfer-from-base"
                    value={newTransfer.fromBaseId}
                    onChange={(e) => setNewTransfer({ ...newTransfer, fromBaseId: e.target.value })}
                    required
                  >
                    <option value="">Select Origin Base</option>
                    {bases.map((b) => (
                      <option key={b.id} value={b.id}>
                        {b.baseCode} — {b.baseName}
                      </option>
                    ))}
                  </select>
                )}
              </div>

              {/* Destination Base */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Destination Base (Transfer In) *</label>
                <select
                  className="form-select"
                  id="new-transfer-to-base"
                  value={newTransfer.toBaseId}
                  onChange={(e) => setNewTransfer({ ...newTransfer, toBaseId: e.target.value })}
                  required
                >
                  <option value="">Select Destination Base</option>
                  {bases
                    .filter((b) => String(b.id) !== String(newTransfer.fromBaseId))
                    .map((b) => (
                      <option key={b.id} value={b.id}>
                        {b.baseCode} — {b.baseName}
                      </option>
                    ))}
                </select>
              </div>

              {/* Equipment Type */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Equipment Asset *</label>
                <select
                  className="form-select"
                  id="new-transfer-equipment"
                  value={newTransfer.equipmentTypeId}
                  onChange={(e) => setNewTransfer({ ...newTransfer, equipmentTypeId: e.target.value })}
                  required
                >
                  <option value="">Select Equipment</option>
                  {equipmentTypes.map((eq) => (
                    <option key={eq.id} value={eq.id}>
                      {eq.name} ({eq.category})
                    </option>
                  ))}
                </select>
              </div>

              {/* Quantity and Date */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Transfer Quantity *</label>
                  <input
                    type="number"
                    id="new-transfer-quantity"
                    min="1"
                    className="form-control"
                    placeholder="e.g. 5"
                    value={newTransfer.quantity}
                    onChange={(e) => setNewTransfer({ ...newTransfer, quantity: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Dispatch Date *</label>
                  <input
                    type="date"
                    id="new-transfer-date"
                    className="form-control"
                    value={newTransfer.transferDate}
                    onChange={(e) => setNewTransfer({ ...newTransfer, transferDate: e.target.value })}
                    required
                  />
                </div>
              </div>

              {/* Reference */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Waybill / Transfer Reference #</label>
                <input
                  type="text"
                  id="new-transfer-reference"
                  className="form-control"
                  placeholder="TRF-2026-XXXX"
                  value={newTransfer.referenceNumber}
                  onChange={(e) => setNewTransfer({ ...newTransfer, referenceNumber: e.target.value })}
                />
              </div>

              {/* Remarks */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Operational Notes / Reason</label>
                <input
                  type="text"
                  id="new-transfer-remarks"
                  className="form-control"
                  placeholder="Reason for inter-base reallocation..."
                  value={newTransfer.remarks}
                  onChange={(e) => setNewTransfer({ ...newTransfer, remarks: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '8px' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setIsModalOpen(false)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  id="new-transfer-submit-btn"
                  className="btn btn-primary"
                  disabled={submitting}
                >
                  {submitting ? 'Validating Stock & Transferring...' : 'Execute Inter-Base Transfer'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
