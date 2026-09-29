import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../services/api';
import { useToast } from '../components/Toast';
import {
  Flame,
  Plus,
  Search,
  Filter,
  Building,
  Calendar,
  Layers,
  FileText,
  AlertTriangle,
  X
} from 'lucide-react';

export const Expenditures = () => {
  const { user } = useAuth();
  const { addToast } = useToast();

  const [expenditures, setExpenditures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);

  // Search & Filter
  const [searchTerm, setSearchTerm] = useState('');
  const [filterBaseId, setFilterBaseId] = useState(user?.baseId ? String(user.baseId) : '');
  const [filterEquipmentId, setFilterEquipmentId] = useState('');

  // Create Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [newExpenditure, setNewExpenditure] = useState({
    baseId: user?.baseId ? String(user.baseId) : '',
    equipmentTypeId: '',
    quantity: '',
    expenditureDate: new Date().toISOString().split('T')[0],
    reason: 'Operational Training Exercise',
    referenceNumber: '',
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

  const loadExpenditures = async () => {
    setLoading(true);
    try {
      const params = {};
      if (user?.role === 'BASE_COMMANDER' && user?.baseId) {
        params.baseId = user.baseId;
      } else if (filterBaseId) {
        params.baseId = filterBaseId;
      }
      if (filterEquipmentId) params.equipmentTypeId = filterEquipmentId;

      const res = await api.getExpenditures(params);
      setExpenditures(res.data || []);
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to load expenditure ledger', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadExpenditures();
  }, [filterBaseId, filterEquipmentId]);

  const handleCreateExpenditure = async (e) => {
    e.preventDefault();
    if (!newExpenditure.baseId || !newExpenditure.equipmentTypeId || !newExpenditure.quantity || !newExpenditure.reason.trim()) {
      addToast('Please fill in all mandatory fields.', 'warning');
      return;
    }

    if (Number(newExpenditure.quantity) <= 0) {
      addToast('Expenditure quantity must be greater than zero.', 'warning');
      return;
    }

    setSubmitting(true);
    try {
      await api.createExpenditure({
        baseId: Number(newExpenditure.baseId),
        equipmentTypeId: Number(newExpenditure.equipmentTypeId),
        quantity: Number(newExpenditure.quantity),
        expenditureDate: newExpenditure.expenditureDate,
        reason: newExpenditure.reason.trim(),
        referenceNumber: newExpenditure.referenceNumber.trim() || `EXP-${Date.now().toString().slice(-6)}`,
      });

      addToast('Expenditure recorded! Inventory balance decremented.', 'success');
      setIsModalOpen(false);
      setNewExpenditure({
        baseId: user?.baseId ? String(user.baseId) : '',
        equipmentTypeId: '',
        quantity: '',
        expenditureDate: new Date().toISOString().split('T')[0],
        reason: 'Operational Training Exercise',
        referenceNumber: '',
      });
      loadExpenditures();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Expenditure failed. Check if sufficient inventory exists at this base.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const filteredExpenditures = expenditures.filter((exp) => {
    if (!searchTerm) return true;
    const term = searchTerm.toLowerCase();
    return (
      exp.referenceNumber?.toLowerCase().includes(term) ||
      exp.reason?.toLowerCase().includes(term) ||
      exp.equipmentName?.toLowerCase().includes(term) ||
      exp.baseName?.toLowerCase().includes(term) ||
      exp.recordedByName?.toLowerCase().includes(term)
    );
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header and Action */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            Asset Expenditure & Consumption Ledger
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
            Inventory deductions resulting from live ammunition consumption, field wear, or tactical disposal
          </p>
        </div>

        <button
          onClick={() => {
            setNewExpenditure({
              baseId: user?.baseId ? String(user.baseId) : (bases[0]?.id ? String(bases[0].id) : ''),
              equipmentTypeId: equipmentTypes[0]?.id ? String(equipmentTypes[0].id) : '',
              quantity: '',
              expenditureDate: new Date().toISOString().split('T')[0],
              reason: 'Tactical Live Fire Exercise',
              referenceNumber: `EXP-${Date.now().toString().slice(-6)}`,
            });
            setIsModalOpen(true);
          }}
          id="btn-record-expenditure"
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <Plus size={16} />
          <span>Record Expenditure</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="card" style={{ padding: '16px 20px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '14px', alignItems: 'center' }}>
          {/* Search */}
          <div style={{ position: 'relative' }}>
            <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
            <input
              type="text"
              id="expenditures-search-input"
              className="form-control"
              placeholder="Search reference, reason, equipment..."
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
                id="expenditures-filter-base"
                className="form-select"
                value={filterBaseId}
                onChange={(e) => setFilterBaseId(e.target.value)}
              >
                <option value="">All Bases</option>
                {bases.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.baseCode} — {b.baseName}
                  </option>
                ))}
              </select>
            )}
          </div>

          {/* Equipment Filter */}
          <div>
            <select
              id="expenditures-filter-equipment"
              className="form-select"
              value={filterEquipmentId}
              onChange={(e) => setFilterEquipmentId(e.target.value)}
            >
              <option value="">All Equipment</option>
              {equipmentTypes.map((eq) => (
                <option key={eq.id} value={eq.id}>
                  {eq.name} ({eq.category})
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Expenditures Table */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Base Facility</th>
                <th>Equipment Asset</th>
                <th style={{ textAlign: 'right' }}>Deducted Qty</th>
                <th>Reason / Operational Purpose</th>
                <th>Reference #</th>
                <th>Recorded By</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={7} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>Loading expenditure logs...</div>
                  </td>
                </tr>
              ) : filteredExpenditures.length === 0 ? (
                <tr>
                  <td colSpan={7} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>No expenditures found matching criteria.</div>
                  </td>
                </tr>
              ) : (
                filteredExpenditures.map((exp) => (
                  <tr key={exp.id}>
                    <td style={{ whiteSpace: 'nowrap', fontWeight: 600 }}>{exp.expenditureDate}</td>
                    <td>
                      <span className="badge badge-info">{exp.baseName || `Base #${exp.baseId}`}</span>
                    </td>
                    <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{exp.equipmentName}</td>
                    <td style={{ textAlign: 'right', fontWeight: 700, color: '#f87171' }}>
                      -{exp.quantity.toLocaleString()} {exp.unit || 'units'}
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>{exp.reason}</td>
                    <td>
                      <code style={{ color: 'var(--accent-blue)', backgroundColor: 'rgba(56, 189, 248, 0.08)', padding: '2px 6px', borderRadius: '4px' }}>
                        {exp.referenceNumber}
                      </code>
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8125rem' }}>{exp.recordedByName || 'Officer'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal: Record Expenditure */}
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
                Record Asset Expenditure / Consumption
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleCreateExpenditure} style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
              {/* Base */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Base Facility *</label>
                {user?.role === 'BASE_COMMANDER' ? (
                  <input type="text" className="form-control" value={user?.baseName || 'Assigned Base'} disabled />
                ) : (
                  <select
                    className="form-select"
                    id="new-expenditure-base"
                    value={newExpenditure.baseId}
                    onChange={(e) => setNewExpenditure({ ...newExpenditure, baseId: e.target.value })}
                    required
                  >
                    <option value="">Select Base</option>
                    {bases.map((b) => (
                      <option key={b.id} value={b.id}>
                        {b.baseCode} — {b.baseName}
                      </option>
                    ))}
                  </select>
                )}
              </div>

              {/* Equipment Type */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Equipment Asset Type *</label>
                <select
                  className="form-select"
                  id="new-expenditure-equipment"
                  value={newExpenditure.equipmentTypeId}
                  onChange={(e) => setNewExpenditure({ ...newExpenditure, equipmentTypeId: e.target.value })}
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
                  <label className="form-label">Expenditure Quantity *</label>
                  <input
                    type="number"
                    id="new-expenditure-quantity"
                    min="1"
                    className="form-control"
                    placeholder="e.g. 10"
                    value={newExpenditure.quantity}
                    onChange={(e) => setNewExpenditure({ ...newExpenditure, quantity: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Expenditure Date *</label>
                  <input
                    type="date"
                    id="new-expenditure-date"
                    className="form-control"
                    value={newExpenditure.expenditureDate}
                    onChange={(e) => setNewExpenditure({ ...newExpenditure, expenditureDate: e.target.value })}
                    required
                  />
                </div>
              </div>

              {/* Reason */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Operational Reason / Justification *</label>
                <input
                  type="text"
                  id="new-expenditure-reason"
                  className="form-control"
                  placeholder="e.g. Live-fire combat training exercise, tactical degradation"
                  value={newExpenditure.reason}
                  onChange={(e) => setNewExpenditure({ ...newExpenditure, reason: e.target.value })}
                  required
                />
              </div>

              {/* Reference */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Expenditure Reference #</label>
                <input
                  type="text"
                  id="new-expenditure-reference"
                  className="form-control"
                  placeholder="EXP-2026-XXXX"
                  value={newExpenditure.referenceNumber}
                  onChange={(e) => setNewExpenditure({ ...newExpenditure, referenceNumber: e.target.value })}
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
                  id="new-expenditure-submit-btn"
                  className="btn btn-primary"
                  disabled={submitting}
                >
                  {submitting ? 'Checking Stock & Expending...' : 'Confirm Inventory Deduction'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
