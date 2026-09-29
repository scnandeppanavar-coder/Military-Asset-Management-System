import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../services/api';
import { useToast } from '../components/Toast';
import { ConfirmModal } from '../components/ConfirmModal';
import {
  ShoppingCart,
  Plus,
  Search,
  Filter,
  Trash2,
  Calendar,
  Building,
  Layers,
  FileText,
  X
} from 'lucide-react';

export const Purchases = () => {
  const { user } = useAuth();
  const { addToast } = useToast();

  const [purchases, setPurchases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);

  // Search & Filters
  const [searchTerm, setSearchTerm] = useState('');
  const [filterBaseId, setFilterBaseId] = useState(user?.baseId ? String(user.baseId) : '');
  const [filterEquipmentId, setFilterEquipmentId] = useState('');

  // Add Purchase Modal state
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [newPurchase, setNewPurchase] = useState({
    baseId: user?.baseId ? String(user.baseId) : '',
    equipmentTypeId: '',
    quantity: '',
    purchaseDate: new Date().toISOString().split('T')[0],
    referenceNumber: '',
    remarks: '',
  });

  // Delete modal state
  const [deleteId, setDeleteId] = useState(null);

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

  const loadPurchases = async () => {
    setLoading(true);
    try {
      const params = {};
      if (user?.role === 'BASE_COMMANDER' && user?.baseId) {
        params.baseId = user.baseId;
      } else if (filterBaseId) {
        params.baseId = filterBaseId;
      }
      if (filterEquipmentId) params.equipmentTypeId = filterEquipmentId;

      const res = await api.getPurchases(params);
      setPurchases(res.data || []);
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to load purchase records', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPurchases();
  }, [filterBaseId, filterEquipmentId]);

  const handleCreatePurchase = async (e) => {
    e.preventDefault();
    if (!newPurchase.baseId || !newPurchase.equipmentTypeId || !newPurchase.quantity || !newPurchase.purchaseDate) {
      addToast('Please fill in all mandatory fields.', 'warning');
      return;
    }

    if (Number(newPurchase.quantity) <= 0) {
      addToast('Quantity must be greater than zero.', 'warning');
      return;
    }

    setSubmitting(true);
    try {
      await api.createPurchase({
        baseId: Number(newPurchase.baseId),
        equipmentTypeId: Number(newPurchase.equipmentTypeId),
        quantity: Number(newPurchase.quantity),
        purchaseDate: newPurchase.purchaseDate,
        referenceNumber: newPurchase.referenceNumber.trim() || `PO-${Date.now().toString().slice(-6)}`,
        remarks: newPurchase.remarks,
      });
      addToast('Purchase logged successfully and inventory movement recorded!', 'success');
      setIsAddModalOpen(false);
      setNewPurchase({
        baseId: user?.baseId ? String(user.baseId) : '',
        equipmentTypeId: '',
        quantity: '',
        purchaseDate: new Date().toISOString().split('T')[0],
        referenceNumber: '',
        remarks: '',
      });
      loadPurchases();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to create purchase', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      await api.deletePurchase(deleteId);
      addToast('Purchase record deleted successfully.', 'success');
      loadPurchases();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to delete purchase record', 'error');
    } finally {
      setDeleteId(null);
    }
  };

  // Filter client-side search query on reference or equipment name
  const filteredPurchases = purchases.filter((p) => {
    const matchSearch =
      searchTerm === '' ||
      p.referenceNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.equipmentName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.remarks?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.createdByName?.toLowerCase().includes(searchTerm.toLowerCase());
    return matchSearch;
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header and Add Button */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            Asset Procurement & Purchases
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
            Acquisition records contributing directly to inventory intake (+Purchases)
          </p>
        </div>

        <button
          onClick={() => {
            setNewPurchase({
              baseId: user?.baseId ? String(user.baseId) : bases[0]?.id ? String(bases[0].id) : '',
              equipmentTypeId: equipmentTypes[0]?.id ? String(equipmentTypes[0].id) : '',
              quantity: '',
              purchaseDate: new Date().toISOString().split('T')[0],
              referenceNumber: `PO-${Date.now().toString().slice(-6)}`,
              remarks: '',
            });
            setIsAddModalOpen(true);
          }}
          id="btn-add-purchase"
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <Plus size={16} />
          <span>Record New Purchase</span>
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
              id="purchases-search-input"
              className="form-control"
              placeholder="Search reference, item, remarks..."
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
                value={`Base: ${user?.baseName || 'Assigned'}`}
                disabled
                style={{ opacity: 0.8, cursor: 'not-allowed' }}
              />
            ) : (
              <select
                id="purchases-filter-base"
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
              id="purchases-filter-equipment"
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

      {/* Table */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Base Facility</th>
                <th>Equipment Asset</th>
                <th>Category</th>
                <th style={{ textAlign: 'right' }}>Quantity</th>
                <th>PO Reference</th>
                <th>Remarks</th>
                <th>Recorded By</th>
                {user?.role === 'ADMIN' && <th style={{ textAlign: 'center' }}>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={user?.role === 'ADMIN' ? 9 : 8} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>Loading purchase ledger...</div>
                  </td>
                </tr>
              ) : filteredPurchases.length === 0 ? (
                <tr>
                  <td colSpan={user?.role === 'ADMIN' ? 9 : 8} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>No purchases found matching filter criteria.</div>
                  </td>
                </tr>
              ) : (
                filteredPurchases.map((p) => (
                  <tr key={p.id}>
                    <td style={{ whiteSpace: 'nowrap', fontWeight: 600 }}>{p.purchaseDate}</td>
                    <td>
                      <span className="badge badge-info">{p.baseName || `Base #${p.baseId}`}</span>
                    </td>
                    <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{p.equipmentName}</td>
                    <td style={{ color: 'var(--text-muted)' }}>{p.equipmentCategory || 'Standard'}</td>
                    <td style={{ textAlign: 'right', fontWeight: 700, color: '#4ade80' }}>
                      +{p.quantity.toLocaleString()} {p.unit || 'units'}
                    </td>
                    <td>
                      <code style={{ color: 'var(--accent-blue)', backgroundColor: 'rgba(56, 189, 248, 0.08)', padding: '2px 6px', borderRadius: '4px' }}>
                        {p.referenceNumber}
                      </code>
                    </td>
                    <td style={{ maxWidth: '200px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', color: 'var(--text-secondary)' }}>
                      {p.remarks || '—'}
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8125rem' }}>{p.createdByName || 'System'}</td>
                    {user?.role === 'ADMIN' && (
                      <td style={{ textAlign: 'center' }}>
                        <button
                          onClick={() => setDeleteId(p.id)}
                          title="Delete purchase entry"
                          style={{
                            background: 'none',
                            border: 'none',
                            color: '#f87171',
                            cursor: 'pointer',
                            padding: '4px',
                          }}
                        >
                          <Trash2 size={16} />
                        </button>
                      </td>
                    )}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Purchase Modal */}
      {isAddModalOpen && (
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
          onClick={() => setIsAddModalOpen(false)}
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
                Record Asset Purchase
              </h3>
              <button
                onClick={() => setIsAddModalOpen(false)}
                style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleCreatePurchase} style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
              {/* Receiving Base */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Receiving Base *</label>
                {user?.role === 'BASE_COMMANDER' ? (
                  <input type="text" className="form-control" value={user?.baseName || 'Assigned Base'} disabled />
                ) : (
                  <select
                    className="form-select"
                    id="new-purchase-base"
                    value={newPurchase.baseId}
                    onChange={(e) => setNewPurchase({ ...newPurchase, baseId: e.target.value })}
                    required
                  >
                    <option value="">Select Target Base</option>
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
                  id="new-purchase-equipment"
                  value={newPurchase.equipmentTypeId}
                  onChange={(e) => setNewPurchase({ ...newPurchase, equipmentTypeId: e.target.value })}
                  required
                >
                  <option value="">Select Equipment</option>
                  {equipmentTypes.map((eq) => (
                    <option key={eq.id} value={eq.id}>
                      {eq.name} ({eq.category}) — {eq.unit}
                    </option>
                  ))}
                </select>
              </div>

              {/* Quantity & Date row */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Quantity *</label>
                  <input
                    type="number"
                    id="new-purchase-quantity"
                    min="1"
                    className="form-control"
                    placeholder="e.g. 25"
                    value={newPurchase.quantity}
                    onChange={(e) => setNewPurchase({ ...newPurchase, quantity: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Purchase Date *</label>
                  <input
                    type="date"
                    id="new-purchase-date"
                    className="form-control"
                    value={newPurchase.purchaseDate}
                    onChange={(e) => setNewPurchase({ ...newPurchase, purchaseDate: e.target.value })}
                    required
                  />
                </div>
              </div>

              {/* Reference */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Procurement Reference #</label>
                <input
                  type="text"
                  id="new-purchase-reference"
                  className="form-control"
                  placeholder="PO-2026-XXXX"
                  value={newPurchase.referenceNumber}
                  onChange={(e) => setNewPurchase({ ...newPurchase, referenceNumber: e.target.value })}
                />
              </div>

              {/* Remarks */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Procurement Remarks</label>
                <input
                  type="text"
                  id="new-purchase-remarks"
                  className="form-control"
                  placeholder="Vendor contract or delivery details..."
                  value={newPurchase.remarks}
                  onChange={(e) => setNewPurchase({ ...newPurchase, remarks: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '8px' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setIsAddModalOpen(false)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  id="new-purchase-submit-btn"
                  className="btn btn-primary"
                  disabled={submitting}
                >
                  {submitting ? 'Recording Movement...' : 'Save & Post Inventory Movement'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      <ConfirmModal
        isOpen={Boolean(deleteId)}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Delete Purchase Record"
        message="Are you sure you want to remove this purchase record? Note: This action may alter audited inventory balances."
      />
    </div>
  );
};
