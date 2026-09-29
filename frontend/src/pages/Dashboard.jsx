import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../services/api';
import { useToast } from '../components/Toast';
import { NetMovementModal } from '../components/NetMovementModal';
import {
  Boxes,
  ArrowLeftRight,
  ShieldCheck,
  ClipboardList,
  Flame,
  Filter,
  RotateCcw,
  Info,
  Calendar,
  Layers,
  Building
} from 'lucide-react';

export const Dashboard = () => {
  const { user } = useAuth();
  const { addToast } = useToast();

  const [loading, setLoading] = useState(true);
  const [dashboardData, setDashboardData] = useState({
    openingBalance: 0,
    purchases: 0,
    transferIn: 0,
    transferOut: 0,
    netMovement: 0,
    expended: 0,
    closingBalance: 0,
    assigned: 0,
  });

  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);

  // Filters state
  const [filters, setFilters] = useState({
    baseId: user?.baseId ? String(user.baseId) : '',
    equipmentTypeId: '',
    from: '',
    to: '',
  });

  // Net Movement Modal state
  const [isNetModalOpen, setIsNetModalOpen] = useState(false);
  const [netMovementDetails, setNetMovementDetails] = useState(null);

  // Load dropdown lists (bases, equipment)
  useEffect(() => {
    const fetchMetadata = async () => {
      try {
        const [basesRes, equipRes] = await Promise.all([
          api.getBases().catch(() => ({ data: [] })),
          api.getEquipmentTypes().catch(() => ({ data: [] })),
        ]);
        setBases(basesRes?.data || (Array.isArray(basesRes) ? basesRes : []));
        setEquipmentTypes(equipRes?.data || (Array.isArray(equipRes) ? equipRes : []));
      } catch (err) {
        console.error('Error fetching metadata', err);
      }
    };
    fetchMetadata();
  }, []);

  // Fetch Dashboard Stats
  const loadDashboard = async () => {
    setLoading(true);
    try {
      const params = {};
      // If user is restricted to a base, enforce it
      if (user?.role === 'BASE_COMMANDER' && user?.baseId) {
        params.baseId = user.baseId;
      } else if (filters.baseId) {
        params.baseId = filters.baseId;
      }

      if (filters.equipmentTypeId) params.equipmentTypeId = filters.equipmentTypeId;
      if (filters.from) params.from = filters.from;
      if (filters.to) params.to = filters.to;

      const res = await api.getDashboard(params);
      const data = res?.data || res;
      setDashboardData(data || {
        openingBalance: 0,
        purchases: 0,
        transferIn: 0,
        transferOut: 0,
        netMovement: 0,
        expended: 0,
        closingBalance: 0,
        assigned: 0,
      });
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to load dashboard metrics', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboard();
  }, [filters.baseId]);

  const handleApplyFilters = (e) => {
    e.preventDefault();
    loadDashboard();
  };

  const handleResetFilters = () => {
    setFilters({
      baseId: user?.baseId ? String(user.baseId) : '',
      equipmentTypeId: '',
      from: '',
      to: '',
    });
    setTimeout(() => {
      loadDashboard();
    }, 50);
  };

  const handleOpenNetMovementModal = async () => {
    try {
      const params = {};
      if (user?.role === 'BASE_COMMANDER' && user?.baseId) {
        params.baseId = user.baseId;
      } else if (filters.baseId) {
        params.baseId = filters.baseId;
      }
      if (filters.equipmentTypeId) params.equipmentTypeId = filters.equipmentTypeId;
      if (filters.from) params.from = filters.from;
      if (filters.to) params.to = filters.to;

      const res = await api.getNetMovement(params);
      const data = res?.data || res;
      setNetMovementDetails(data);
      setIsNetModalOpen(true);
    } catch (err) {
      // Fallback to dashboardData
      setNetMovementDetails({
        purchases: dashboardData?.purchases ?? 0,
        transferIn: dashboardData?.transferIn ?? 0,
        transferOut: dashboardData?.transferOut ?? 0,
        netMovement: dashboardData?.netMovement ?? 0,
      });
      setIsNetModalOpen(true);
    }
  };

  // Helper labels
  const selectedBaseName = bases.find((b) => String(b.id) === String(filters.baseId))?.baseName || (user?.baseName ? user.baseName : 'All Bases');
  const selectedEquipName = equipmentTypes.find((e) => String(e.id) === String(filters.equipmentTypeId))?.name || 'All Equipment';

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {/* Top Banner / Breadcrumb */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            Tactical Inventory Dashboard
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
            Real-time balance computation & verified inventory movement ledger
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <span
            style={{
              padding: '6px 12px',
              borderRadius: '6px',
              backgroundColor: 'rgba(56, 189, 248, 0.08)',
              border: '1px solid rgba(56, 189, 248, 0.25)',
              fontSize: '0.75rem',
              color: '#38bdf8',
              fontWeight: 600,
            }}
          >
            Clearance: {user?.role}
          </span>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="card" style={{ padding: '20px' }}>
        <form onSubmit={handleApplyFilters}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '16px' }}>
            <Filter size={16} color="var(--accent-blue)" />
            <span style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--text-primary)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Ledger Query & Scope Filters
            </span>
          </div>

          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: '16px',
              alignItems: 'flex-end',
            }}
          >
            {/* Base Filter */}
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Building size={14} /> Base Facility
              </label>
              {user?.role === 'BASE_COMMANDER' ? (
                <input
                  type="text"
                  className="form-control"
                  value={user?.baseName || 'Assigned Base'}
                  disabled
                  style={{ opacity: 0.8, cursor: 'not-allowed' }}
                />
              ) : (
                <select
                  id="dashboard-filter-base"
                  className="form-select"
                  value={filters.baseId}
                  onChange={(e) => setFilters({ ...filters, baseId: e.target.value })}
                >
                  <option value="">All Bases (Global HQ View)</option>
                  {bases.map((b) => (
                    <option key={b.id} value={b.id}>
                      {b.baseCode} — {b.baseName}
                    </option>
                  ))}
                </select>
              )}
            </div>

            {/* Equipment Type Filter */}
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Layers size={14} /> Equipment Category
              </label>
              <select
                id="dashboard-filter-equipment"
                className="form-select"
                value={filters.equipmentTypeId}
                onChange={(e) => setFilters({ ...filters, equipmentTypeId: e.target.value })}
              >
                <option value="">All Equipment Types</option>
                {equipmentTypes.map((eq) => (
                  <option key={eq.id} value={eq.id}>
                    {eq.name} ({eq.category})
                  </option>
                ))}
              </select>
            </div>

            {/* Date From */}
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Calendar size={14} /> From (Start Date)
              </label>
              <input
                type="date"
                id="dashboard-filter-from"
                className="form-control"
                value={filters.from}
                onChange={(e) => setFilters({ ...filters, from: e.target.value })}
              />
            </div>

            {/* Date To */}
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Calendar size={14} /> To (End Date)
              </label>
              <input
                type="date"
                id="dashboard-filter-to"
                className="form-control"
                value={filters.to}
                onChange={(e) => setFilters({ ...filters, to: e.target.value })}
              />
            </div>

            {/* Action Buttons */}
            <div style={{ display: 'flex', gap: '8px' }}>
              <button
                type="submit"
                id="dashboard-filter-apply-btn"
                className="btn btn-primary"
                style={{ flex: 1 }}
                disabled={loading}
              >
                Apply
              </button>
              <button
                type="button"
                id="dashboard-filter-reset-btn"
                className="btn btn-secondary"
                onClick={handleResetFilters}
                title="Reset filters"
              >
                <RotateCcw size={16} />
              </button>
            </div>
          </div>
        </form>
      </div>

      {/* 5 Core Inventory KPI Cards */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
          gap: '16px',
        }}
      >
        {/* 1. Opening Balance */}
        <div
          className="kpi-card"
          style={{
            borderLeft: '4px solid #64748b',
          }}
        >
          <div className="kpi-title" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span>Opening Balance</span>
            <Boxes size={18} color="#94a3b8" />
          </div>
          <div className="kpi-value" style={{ color: '#e2e8f0' }}>
            {loading ? '...' : (dashboardData?.openingBalance ?? 0).toLocaleString()}
          </div>
          <div className="kpi-subtitle">
            Net historical balance prior to query start date
          </div>
        </div>

        {/* 2. Net Movement (CLICKABLE BONUS FEATURE) */}
        <div
          className="kpi-card clickable"
          id="net-movement-card"
          onClick={handleOpenNetMovementModal}
          style={{
            borderLeft: '4px solid #38bdf8',
            cursor: 'pointer',
            position: 'relative',
            transition: 'all 0.2s ease',
          }}
          title="Click to view detailed itemized breakdown"
        >
          <div className="kpi-title" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span style={{ color: '#38bdf8' }}>Net Movement ↗</span>
            <ArrowLeftRight size={18} color="#38bdf8" />
          </div>
          <div
            className="kpi-value"
            style={{
              color: (dashboardData?.netMovement ?? 0) >= 0 ? '#38bdf8' : '#f87171',
            }}
          >
            {loading
              ? '...'
              : (dashboardData?.netMovement ?? 0) >= 0
              ? `+${(dashboardData?.netMovement ?? 0).toLocaleString()}`
              : (dashboardData?.netMovement ?? 0).toLocaleString()}
          </div>
          <div className="kpi-subtitle" style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#7dd3fc' }}>
            <Info size={12} />
            <span>Click for audited itemized breakdown</span>
          </div>
        </div>

        {/* 3. Closing Balance */}
        <div
          className="kpi-card"
          style={{
            borderLeft: '4px solid #22c55e',
          }}
        >
          <div className="kpi-title" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span style={{ color: '#4ade80' }}>Closing Balance</span>
            <ShieldCheck size={18} color="#22c55e" />
          </div>
          <div className="kpi-value" style={{ color: '#4ade80' }}>
            {loading ? '...' : (dashboardData?.closingBalance ?? 0).toLocaleString()}
          </div>
          <div className="kpi-subtitle">
            Opening + Net Movement − Expended
          </div>
        </div>

        {/* 4. Assigned (Separate tracking assumption) */}
        <div
          className="kpi-card"
          style={{
            borderLeft: '4px solid #a855f7',
          }}
        >
          <div className="kpi-title" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span style={{ color: '#c084fc' }}>Assigned</span>
            <ClipboardList size={18} color="#a855f7" />
          </div>
          <div className="kpi-value" style={{ color: '#c084fc' }}>
            {loading ? '...' : (dashboardData.assigned ?? dashboardData.assignedQuantity ?? 0).toLocaleString()}
          </div>
          <div className="kpi-subtitle">
            In field with personnel (does not deduct inventory)
          </div>
        </div>

        {/* 5. Expended */}
        <div
          className="kpi-card"
          style={{
            borderLeft: '4px solid #ef4444',
          }}
        >
          <div className="kpi-title" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span style={{ color: '#f87171' }}>Expended</span>
            <Flame size={18} color="#ef4444" />
          </div>
          <div className="kpi-value" style={{ color: '#f87171' }}>
            {loading ? '...' : (dashboardData.expended ?? dashboardData.expenditures ?? 0).toLocaleString()}
          </div>
          <div className="kpi-subtitle">
            Consumed in training, ops, or disposed
          </div>
        </div>
      </div>

      {/* Inventory Accounting Rules Reference Card */}
      <div
        className="card"
        style={{
          padding: '16px 20px',
          backgroundColor: 'rgba(15, 23, 42, 0.6)',
          border: '1px solid var(--border-subtle)',
          display: 'flex',
          flexDirection: 'column',
          gap: '8px',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--accent-amber)', fontSize: '0.8125rem', fontWeight: 600 }}>
          <Info size={16} />
          <span>Accounting Formulae Enforced by Backend Engine</span>
        </div>
        <div style={{ display: 'flex', gap: '24px', flexWrap: 'wrap', fontSize: '0.8125rem', color: 'var(--text-secondary)', fontFamily: 'monospace' }}>
          <div>
            <strong style={{ color: 'var(--text-primary)' }}>Net Movement:</strong> Purchases + Transfer In − Transfer Out
          </div>
          <div>
            <strong style={{ color: 'var(--text-primary)' }}>Closing Balance:</strong> Opening Balance + Net Movement − Expenditure
          </div>
          <div>
            <strong style={{ color: 'var(--text-primary)' }}>Personnel Assignments:</strong> Tracked on field roster separately (Non-inventory reducing)
          </div>
        </div>
      </div>

      {/* Net Movement Interactive Modal */}
      <NetMovementModal
        isOpen={isNetModalOpen}
        onClose={() => setIsNetModalOpen(false)}
        data={netMovementDetails || dashboardData}
        filterInfo={{
          baseName: selectedBaseName,
          equipmentName: selectedEquipName,
          dateRange: filters.from || filters.to ? `${filters.from || 'Start'} to ${filters.to || 'Present'}` : 'All Recorded Dates',
        }}
      />
    </div>
  );
};
