import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { useToast } from '../../components/Toast';
import { ConfirmModal } from '../../components/ConfirmModal';
import {
  Shield,
  Plus,
  Search,
  Edit2,
  Trash2,
  Tag,
  Layers,
  X
} from 'lucide-react';

export const Equipment = () => {
  const { addToast } = useToast();

  const [equipmentList, setEquipmentList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  // Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    category: 'Vehicle',
    description: '',
    unit: 'units',
  });

  // Delete
  const [deleteTarget, setDeleteTarget] = useState(null);

  const loadEquipment = async () => {
    setLoading(true);
    try {
      const res = await api.getEquipmentTypes();
      setEquipmentList(res.data || []);
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to load equipment catalog', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadEquipment();
  }, []);

  const handleOpenCreate = () => {
    setEditingItem(null);
    setFormData({
      name: '',
      category: 'Vehicle',
      description: '',
      unit: 'units',
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (item) => {
    setEditingItem(item);
    setFormData({
      name: item.name,
      category: item.category || 'Vehicle',
      description: item.description || '',
      unit: item.unit || 'units',
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    if (!formData.name.trim() || !formData.unit.trim()) {
      addToast('Name and unit are required.', 'warning');
      return;
    }

    setSubmitting(true);
    try {
      const payload = {
        name: formData.name.trim(),
        category: formData.category.trim(),
        description: formData.description.trim(),
        unit: formData.unit.trim(),
      };

      if (editingItem) {
        await api.updateEquipmentType(editingItem.id, payload);
        addToast('Equipment type updated successfully.', 'success');
      } else {
        await api.createEquipmentType(payload);
        addToast('New equipment category cataloged.', 'success');
      }

      setIsModalOpen(false);
      loadEquipment();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Operation failed', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await api.deleteEquipmentType(deleteTarget.id);
      addToast(`Equipment "${deleteTarget.name}" removed from catalog.`, 'success');
      loadEquipment();
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to delete equipment item', 'error');
    } finally {
      setDeleteTarget(null);
    }
  };

  const filteredEquipment = equipmentList.filter((item) => {
    if (!searchTerm) return true;
    const term = searchTerm.toLowerCase();
    return (
      item.name?.toLowerCase().includes(term) ||
      item.category?.toLowerCase().includes(term) ||
      item.description?.toLowerCase().includes(term) ||
      item.unit?.toLowerCase().includes(term)
    );
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header and Action */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            Equipment Catalog & Asset Classification
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
            Master taxonomy of generic military assets, weapons, protective equipment, and vehicles
          </p>
        </div>

        <button
          onClick={handleOpenCreate}
          id="btn-add-equipment"
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <Plus size={16} />
          <span>Add Equipment Type</span>
        </button>
      </div>

      {/* Search Bar */}
      <div className="card" style={{ padding: '16px 20px' }}>
        <div style={{ position: 'relative', maxWidth: '400px' }}>
          <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
          <input
            type="text"
            id="equipment-search-input"
            className="form-control"
            placeholder="Search items by name, category, unit..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ paddingLeft: '36px' }}
          />
        </div>
      </div>

      {/* Equipment Table */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Asset Name</th>
                <th>Category</th>
                <th>Description / Specification</th>
                <th>Standard Unit</th>
                <th>Catalog Date</th>
                <th style={{ textAlign: 'center' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={6} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>Loading equipment catalog...</div>
                  </td>
                </tr>
              ) : filteredEquipment.length === 0 ? (
                <tr>
                  <td colSpan={6} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>No equipment types found.</div>
                  </td>
                </tr>
              ) : (
                filteredEquipment.map((item) => (
                  <tr key={item.id}>
                    <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <Shield size={16} color="#38bdf8" />
                        <span>{item.name}</span>
                      </div>
                    </td>
                    <td>
                      <span className="badge badge-info">{item.category}</span>
                    </td>
                    <td style={{ color: 'var(--text-secondary)', maxWidth: '280px' }}>{item.description || '—'}</td>
                    <td>
                      <code style={{ color: 'var(--accent-amber)', backgroundColor: 'rgba(251, 191, 36, 0.08)', padding: '2px 8px', borderRadius: '4px' }}>
                        {item.unit}
                      </code>
                    </td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.8125rem' }}>
                      {item.createdAt ? String(item.createdAt).substring(0, 10) : '—'}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
                        <button
                          onClick={() => handleOpenEdit(item)}
                          title="Edit equipment"
                          style={{ background: 'none', border: 'none', color: '#38bdf8', cursor: 'pointer', padding: '4px' }}
                        >
                          <Edit2 size={15} />
                        </button>
                        <button
                          onClick={() => setDeleteTarget(item)}
                          title="Delete equipment"
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

      {/* Modal: Add/Edit Equipment */}
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
                {editingItem ? `Edit Equipment: ${editingItem.name}` : 'Catalog New Equipment Type'}
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
                <label className="form-label">Equipment Model / Name *</label>
                <input
                  type="text"
                  id="equipment-form-name"
                  className="form-control"
                  placeholder="e.g. Armored Personnel Carrier MK-II"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Category *</label>
                  <select
                    className="form-select"
                    id="equipment-form-category"
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  >
                    <option value="Vehicle">Vehicle</option>
                    <option value="Weapon">Weapon</option>
                    <option value="Ammunition">Ammunition</option>
                    <option value="Communication Equipment">Communication Equipment</option>
                    <option value="Protective Equipment">Protective Equipment</option>
                    <option value="Medical Equipment">Medical Equipment</option>
                    <option value="Surveillance Equipment">Surveillance Equipment</option>
                    <option value="Tactical Gear">Tactical Gear</option>
                  </select>
                </div>

                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label">Unit of Measure *</label>
                  <input
                    type="text"
                    id="equipment-form-unit"
                    className="form-control"
                    placeholder="e.g. units, rounds, sets"
                    value={formData.unit}
                    onChange={(e) => setFormData({ ...formData, unit: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group" style={{ margin: 0 }}>
                <label className="form-label">Description / Specifications</label>
                <textarea
                  id="equipment-form-description"
                  className="form-control"
                  rows="3"
                  placeholder="Technical specifications, load capacity, caliber..."
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
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
                  id="equipment-form-submit-btn"
                  className="btn btn-primary"
                  disabled={submitting}
                >
                  {submitting ? 'Saving...' : editingItem ? 'Update Asset' : 'Save Asset'}
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
        title={`Remove ${deleteTarget?.name}?`}
        message={`Are you sure you want to remove "${deleteTarget?.name}" from the equipment catalog? Note: Deletion will be rejected if existing inventory or transactions reference this item.`}
      />
    </div>
  );
};
