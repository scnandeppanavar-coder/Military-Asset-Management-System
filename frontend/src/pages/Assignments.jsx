import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../services/api';
import { useToast } from '../components/Toast';
import {
  ClipboardList,
  Plus,
  Search,
  RotateCcw,
  CheckCircle2,
  Clock,
  UserCheck,
  Building,
  Layers,
  X
} from 'lucide-react';

export const Assignments = () => {
  const { user } = useAuth();
  const { addToast } = useToast();

  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);

  // Filter & Search
  const [searchTerm, setSearchTerm] = useState('');
  const [filterBaseId, setFilterBaseId] = useState(user?.baseId ? String(user.baseId) : '');
  const [filterStatus, setFilterStatus] = useState('');

  // Create Modal
  const [isAssignModalOpen, setIsAssignModalOpen] = useState(false);
  const [assigning, setAssigning] = useState(false);
  const [newAssignment, setNewAssignment] = useState({
    baseId: user?.baseId ? String(user.baseId) : '',
    equipmentTypeId: '',
    personnelName: '',
    quantity: '',
    assignedDate: new Date().toISOString().split('T')[0],
  });

  // Return Modal
  const [selectedForReturn, setSelectedForReturn] = useState(null);
  const [returnQuantity, setReturnQuantity] = useState('');
  const [returning, setReturning] = useState(false);

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

  const loadAssignments = async () => {
    setLoading(true);
    try {
      const params = {};
      if (user?.role === 'BASE_COMMANDER' && user?.baseId) {
        params.baseId = user.baseId;
      } else if (filterBaseId) {
        params.baseId = filterBaseId;
      }
      if (filterStatus) params.status = filterStatus;

      const res = await api.getAssignments(params);
      setAssignments(res.data || []);
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to load assignments', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAssignments();
  }, [filterBaseId, filterStatus]);

  const handleCreateAssignment = async (e) => {
    e.preventDefault();
    if (!newAssignment.baseId || !newAssignment.equipmentTypeId || !newAssignment.personnelName.trim() || !newAssignment.quantity) {
      addToast('Please fill out all required fields.', 'warning');
      return;
    }

    if (Number(newAssignment.quantity) <= 0) {
      addToast('Assignment quantity must be greater than zero.', 'warning');
      return;
    }

    setAssigning(true);
    try {
      await api.createAssignment({
        baseId: Number(newAssignment.baseId),
        equipmentTypeId: Number(newAssignment.equipmentTypeId),
        personnelName: newAssignment.personnelName.trim(),
        quantity: Number(newAssignment.quantity),
        assignedDate: newAssignment.assignedDate,
      });

      addToast('Equipment successfully checked out to personnel roster.', 'success');
      setIsAssignModalOpen(false);
      setNewAssignment({
        baseId: user?.baseId ? String(user.baseId) : '',
        equipmentTypeId: '',
        personnelName: '',
        quantity: '',
        assignedDate: new Date().toISOString().split('T')[0],
      });
      loadAssignments();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Assignment failed. Insufficient inventory or invalid request.', 'error');
    } finally {
      setAssigning(false);
    }
  };

  const handleReturnEquipment = async (e) => {
    e.preventDefault();
    if (!selectedForReturn || !returnQuantity) return;

    const qty = Number(returnQuantity);
    if (qty <= 0) {
      addToast('Return quantity must be at least 1.', 'warning');
      return;
    }

    const outstanding = selectedForReturn.outstandingQuantity !== undefined
      ? selectedForReturn.outstandingQuantity
      : (selectedForReturn.quantity - (selectedForReturn.returnedQuantity || 0));

    if (qty > outstanding) {
      addToast(`Return quantity (${qty}) exceeds remaining outstanding items (${outstanding}).`, 'error');
      return;
    }

    setReturning(true);
    try {
      await api.returnAssignment(selectedForReturn.id, {
        returnQuantity: qty,
      });
      addToast('Equipment return recorded successfully.', 'success');
      setSelectedForReturn(null);
      setReturnQuantity('');
      loadAssignments();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to process return', 'error');
    } finally {
      setReturning(false);
    }
  };

  const filteredAssignments = assignments.filter((a) => {
    if (!searchTerm) return true;
    const term = searchTerm.toLowerCase();
    return (
      a.personnelName?.toLowerCase().includes(term) ||
      a.equipmentName?.toLowerCase().includes(term) ||
      a.baseName?.toLowerCase().includes(term) ||
      a.assignedBy?.toLowerCase().includes(term)
    );
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header and Action */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            Personnel Equipment Assignments
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
            Field custody roster (Separately tracked; does not deduct physical base inventory)
          </p>
        </div>

        <button
          onClick={() => {
            setNewAssignment({
              baseId: user?.baseId ? String(user.baseId) : (bases[0]?.id ? String(bases[0].id) : ''),
              equipmentTypeId: equipmentTypes[0]?.id ? String(equipmentTypes[0].id) : '',
              personnelName: '',
              quantity: '',
              assignedDate: new Date().toISOString().split('T')[0],
            });
            setIsAssignModalOpen(true);
          }}
          id="btn-assign-equipment"
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <Plus size={16} />
          <span>Assign Equipment</span>
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
              id="assignments-search-input"
              className="form-control"
              placeholder="Search personnel, equipment..."
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
                id="assignments-filter-base"
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

          {/* Status Filter */}
          <div>
            <select
              id="assignments-filter-status"
              className="form-select"
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
            >
              <option value="">All Statuses</option>
              <option value="ACTIVE">ACTIVE (In Custody)</option>
              <option value="PARTIALLY_RETURNED">PARTIALLY RETURNED</option>
              <option value="RETURNED">RETURNED (Closed)</option>
            </select>
          </div>
        </div>
      </div>

      {/* Assignments Table */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Base</th>
                <th>Personnel / Operator</th>
                <th>Equipment Asset</th>
                <th style={{ textAlign: 'center' }}>Assigned Qty</th>
                <th style={{ textAlign: 'center' }}>Returned</th>
                <th style={{ textAlign: 'center' }}>Outstanding</th>
                <th>Status</th>
                <th>Assigned By</th>
                <th style={{ textAlign: 'center' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={10} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>Loading assignment roster...</div>
                  </td>
                </tr>
              ) : filteredAssignments.length === 0 ? (
                <tr>
                  <td colSpan={10} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>No assignments found matching criteria.</div>
                  </td>
                </tr>
              ) : (
                filteredAssignments.map((a) => {
                  const outstanding = a.outstandingQuantity !== undefined ? a.outstandingQuantity : (a.quantity - (a.returnedQuantity || 0));
                  return (
                    <tr key={a.id}>
                      <td style={{ whiteSpace: 'nowrap' }}>{a.assignedDate}</td>
                      <td>
                        <span className="badge badge-info">{a.baseName || `Base #${a.baseId}`}</span>
                      </td>
                      <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                          <UserCheck size={14} color="#38bdf8" />
                          <span>{a.personnelName}</span>
                        </div>
                      </td>
                      <td>{a.equipmentName}</td>
                      <td style={{ textAlign: 'center', fontWeight: 700 }}>{a.quantity}</td>
                      <td style={{ textAlign: 'center', color: '#4ade80' }}>{a.returnedQuantity || 0}</td>
                      <td style={{ textAlign: 'center', fontWeight: 700, color: outstanding > 0 ? '#f87171' : '#4ade80' }}>
                        {outstanding}
                      </td>
                      <td>
                        {a.status === 'ACTIVE' && <span className="badge badge-warning">ACTIVE</span>}
                        {a.status === 'PARTIALLY_RETURNED' && <span className="badge badge-info">PARTIALLY RETURNED</span>}
                        {a.status === 'RETURNED' && <span className="badge badge-success">RETURNED</span>}
                      </td>
                      <td style={{ color: 'var(--text-muted)', fontSize: '0.8125rem' }}>{a.assignedBy || 'Officer'}</td>
                      <td style={{ textAlign: 'center' }}>
                        {outstanding > 0 ? (
                          <button
                            onClick={() => {
                              setSelectedForReturn(a);
                              setReturnQuantity(String(outstanding));
                            }}
                            className="btn btn-secondary"
                            style={{ padding: '4px 10px', fontSize: '0.75rem' }}
                          >
                            Return
                          </button>
                        ) : (
                          <span style={{ fontSize: '0.75rem', color: '#4ade80', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '4px' }}>
                            <CheckCircle2 size={14} /> Closed
                          </span>
                        )}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal: Check Out Equipment */}
      {isAssignModalOpen && (
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
          onClick={() => setIsAssignModalOpen(false)}
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
                Assign Equipment to Personnel
              </h3>
              <button
                onClick={() => setIsAssignModalOpen(false)}
                style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleCreateAssignment} style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
              {/* Base */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Base Custody Station *</label>
                {user?.role === 'BASE_COMMANDER' ? (
                  <input type="text" className="form-control" value={user?.baseName || 'Assigned Base'} disabled />
                ) : (
                  <select
                    className="form-select"
                    id="new-assignment-base"
                    value={newAssignment.baseId}
                    onChange={(e) => setNewAssignment({ ...newAssignment, baseId: e.target.value })}
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

              {/* Personnel Name */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Personnel Rank & Full Name *</label>
                <input
                  type="text"
                  id="new-assignment-personnel"
                  className="form-control"
                  placeholder="e.g. Sgt. J. Miller (Squad 3)"
                  value={newAssignment.personnelName}
                  onChange={(e) => setNewAssignment({ ...newAssignment, personnelName: e.target.value })}
                  required
                />
              </div>

              {/* Equipment Type */}
              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Equipment Asset *</label>
                <select
                  className="form-select"
                  id="new-assignment-equipment"
                  value={newAssignment.equipmentTypeId}
                  onChange={(e) => setNewAssignment({ ...newAssignment, equipmentTypeId: e.target.value })}
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
                  <label className="form-label">Quantity *</label>
                  <input
                    type="number"
                    id="new-assignment-quantity"
                    min="1"
                    className="form-control"
                    placeholder="e.g. 2"
                    value={newAssignment.quantity}
                    onChange={(e) => setNewAssignment({ ...newAssignment, quantity: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Assigned Date *</label>
                  <input
                    type="date"
                    id="new-assignment-date"
                    className="form-control"
                    value={newAssignment.assignedDate}
                    onChange={(e) => setNewAssignment({ ...newAssignment, assignedDate: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '8px' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setIsAssignModalOpen(false)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  id="new-assignment-submit-btn"
                  className="btn btn-primary"
                  disabled={assigning}
                >
                  {assigning ? 'Checking Stock & Assigning...' : 'Confirm Assignment'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal: Return Equipment */}
      {selectedForReturn && (
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
          onClick={() => setSelectedForReturn(null)}
        >
          <div
            style={{
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-medium)',
              borderRadius: 'var(--radius-lg)',
              width: '100%',
              maxWidth: '440px',
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
                Process Equipment Return
              </h3>
              <button
                onClick={() => setSelectedForReturn(null)}
                style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleReturnEquipment} style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div style={{ backgroundColor: 'rgba(255, 255, 255, 0.04)', padding: '12px', borderRadius: '6px', fontSize: '0.8125rem' }}>
                <div>Personnel: <strong>{selectedForReturn.personnelName}</strong></div>
                <div>Equipment: <strong>{selectedForReturn.equipmentName}</strong></div>
                <div>Originally Assigned: <strong>{selectedForReturn.quantity}</strong></div>
                <div>Previously Returned: <strong>{selectedForReturn.returnedQuantity || 0}</strong></div>
                <div style={{ marginTop: '4px', color: '#f87171' }}>
                  Remaining to Return: <strong>{selectedForReturn.outstandingQuantity !== undefined ? selectedForReturn.outstandingQuantity : (selectedForReturn.quantity - (selectedForReturn.returnedQuantity || 0))}</strong>
                </div>
              </div>

              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Quantity Being Returned Now *</label>
                <input
                  type="number"
                  id="return-quantity-input"
                  min="1"
                  max={selectedForReturn.outstandingQuantity !== undefined ? selectedForReturn.outstandingQuantity : (selectedForReturn.quantity - (selectedForReturn.returnedQuantity || 0))}
                  className="form-control"
                  value={returnQuantity}
                  onChange={(e) => setReturnQuantity(e.target.value)}
                  required
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '8px' }}>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setSelectedForReturn(null)}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  id="submit-return-btn"
                  className="btn btn-primary"
                  disabled={returning}
                >
                  {returning ? 'Updating Ledger...' : 'Confirm Return'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
