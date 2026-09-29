import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import { useToast } from '../../components/Toast';
import {
  FileText,
  Search,
  Filter,
  RotateCcw,
  Calendar,
  ShieldCheck,
  Activity,
  Terminal,
  Clock
} from 'lucide-react';

export const AuditLogs = () => {
  const { addToast } = useToast();

  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  // Filters
  const [actionFilter, setActionFilter] = useState('');
  const [entityFilter, setEntityFilter] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');

  const loadLogs = async () => {
    setLoading(true);
    try {
      const params = {};
      if (actionFilter) params.action = actionFilter;
      if (entityFilter) params.entityType = entityFilter;
      if (searchTerm) params.search = searchTerm;
      if (dateFrom) params.from = dateFrom;
      if (dateTo) params.to = dateTo;

      const res = await api.getAuditLogs(params);
      setLogs(res.data || []);
    } catch (err) {
      addToast(err?.response?.data?.message || 'Failed to load audit logs', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLogs();
  }, [actionFilter, entityFilter]);

  const handleApplyFilter = (e) => {
    e.preventDefault();
    loadLogs();
  };

  const handleReset = () => {
    setActionFilter('');
    setEntityFilter('');
    setSearchTerm('');
    setDateFrom('');
    setDateTo('');
    setTimeout(() => {
      loadLogs();
    }, 50);
  };

  const getActionBadge = (action) => {
    switch (action) {
      case 'CREATE':
        return <span className="badge badge-success">CREATE</span>;
      case 'PURCHASE':
        return <span className="badge badge-success">PURCHASE</span>;
      case 'TRANSFER':
        return <span className="badge badge-info">TRANSFER</span>;
      case 'ASSIGN':
        return <span className="badge badge-warning">ASSIGN</span>;
      case 'EXPEND':
        return <span className="badge badge-danger">EXPEND</span>;
      case 'DELETE':
        return <span className="badge badge-danger">DELETE</span>;
      case 'LOGIN':
        return <span className="badge badge-info">LOGIN</span>;
      case 'LOGOUT':
        return <span className="badge badge-info">LOGOUT</span>;
      default:
        return <span className="badge badge-info">{action}</span>;
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            Immutable Security Audit Trail
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: '0.8125rem', color: 'var(--text-muted)' }}>
            Cryptographically sealed operational audit events, logins, modifications, and inventory ledger movements
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span
            style={{
              padding: '6px 12px',
              borderRadius: '6px',
              backgroundColor: 'rgba(34, 197, 94, 0.1)',
              border: '1px solid rgba(34, 197, 94, 0.3)',
              fontSize: '0.75rem',
              color: '#4ade80',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
            }}
          >
            <ShieldCheck size={14} />
            <span>AUDIT COMPLIANCE: ENFORCED</span>
          </span>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="card" style={{ padding: '16px 20px' }}>
        <form onSubmit={handleApplyFilter}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px', alignItems: 'flex-end' }}>
            {/* Search */}
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Search Keywords</label>
              <div style={{ position: 'relative' }}>
                <Search size={14} color="var(--text-muted)" style={{ position: 'absolute', left: '10px', top: '50%', transform: 'translateY(-50%)' }} />
                <input
                  type="text"
                  id="audit-search-input"
                  className="form-control"
                  placeholder="Operator, ID, action..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  style={{ paddingLeft: '32px' }}
                />
              </div>
            </div>

            {/* Action Filter */}
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Event Action</label>
              <select
                id="audit-action-filter"
                className="form-select"
                value={actionFilter}
                onChange={(e) => setActionFilter(e.target.value)}
              >
                <option value="">All Actions</option>
                <option value="LOGIN">LOGIN</option>
                <option value="PURCHASE">PURCHASE</option>
                <option value="TRANSFER">TRANSFER</option>
                <option value="ASSIGN">ASSIGN</option>
                <option value="EXPEND">EXPEND</option>
                <option value="CREATE">CREATE</option>
                <option value="UPDATE">UPDATE</option>
                <option value="DELETE">DELETE</option>
              </select>
            </div>

            {/* Entity Type Filter */}
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Target Entity</label>
              <select
                id="audit-entity-filter"
                className="form-select"
                value={entityFilter}
                onChange={(e) => setEntityFilter(e.target.value)}
              >
                <option value="">All Entities</option>
                <option value="USER">USER</option>
                <option value="PURCHASE">PURCHASE</option>
                <option value="TRANSFER">TRANSFER</option>
                <option value="ASSIGNMENT">ASSIGNMENT</option>
                <option value="EXPENDITURE">EXPENDITURE</option>
                <option value="BASE">BASE</option>
                <option value="EQUIPMENT_TYPE">EQUIPMENT_TYPE</option>
              </select>
            </div>

            {/* Date From */}
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">From Date</label>
              <input
                type="date"
                id="audit-from-date"
                className="form-control"
                value={dateFrom}
                onChange={(e) => setDateFrom(e.target.value)}
              />
            </div>

            {/* Date To */}
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">To Date</label>
              <input
                type="date"
                id="audit-to-date"
                className="form-control"
                value={dateTo}
                onChange={(e) => setDateTo(e.target.value)}
              />
            </div>

            {/* Submit & Reset */}
            <div style={{ display: 'flex', gap: '8px' }}>
              <button
                type="submit"
                id="audit-filter-apply-btn"
                className="btn btn-primary"
                style={{ flex: 1 }}
                disabled={loading}
              >
                Filter
              </button>
              <button
                type="button"
                id="audit-filter-reset-btn"
                className="btn btn-secondary"
                onClick={handleReset}
                title="Reset filters"
              >
                <RotateCcw size={16} />
              </button>
            </div>
          </div>
        </form>
      </div>

      {/* Audit Logs Table */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Timestamp (UTC)</th>
                <th>Operator</th>
                <th>Action</th>
                <th>Entity Target</th>
                <th style={{ textAlign: 'center' }}>Target ID</th>
                <th>Event Description</th>
                <th>Client IP</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={7} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>Retrieving audit ledger...</div>
                  </td>
                </tr>
              ) : logs.length === 0 ? (
                <tr>
                  <td colSpan={7} style={{ textAlign: 'center', padding: '32px' }}>
                    <div style={{ color: 'var(--text-muted)' }}>No audit events found.</div>
                  </td>
                </tr>
              ) : (
                logs.map((log) => (
                  <tr key={log.id}>
                    <td style={{ whiteSpace: 'nowrap', fontFamily: 'monospace', fontSize: '0.8125rem', color: 'var(--text-secondary)' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                        <Clock size={12} color="var(--text-muted)" />
                        <span>{log.timestamp ? String(log.timestamp).replace('T', ' ').substring(0, 19) : '—'}</span>
                      </div>
                    </td>
                    <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                      <code style={{ color: '#38bdf8' }}>{log.username || 'SYSTEM'}</code>
                    </td>
                    <td>{getActionBadge(log.action)}</td>
                    <td style={{ fontWeight: 500, color: 'var(--text-secondary)' }}>{log.entityType}</td>
                    <td style={{ textAlign: 'center' }}>
                      {log.entityId ? (
                        <span style={{ fontFamily: 'monospace', color: 'var(--text-muted)' }}>#{log.entityId}</span>
                      ) : (
                        '—'
                      )}
                    </td>
                    <td style={{ color: 'var(--text-primary)', maxWidth: '340px' }}>
                      {log.description}
                    </td>
                    <td style={{ fontFamily: 'monospace', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      {log.ipAddress || '127.0.0.1'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
