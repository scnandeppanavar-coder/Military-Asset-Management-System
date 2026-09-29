import React from 'react';
import { X, ArrowUpRight, ArrowDownLeft, ShoppingBag, Equal } from 'lucide-react';

export const NetMovementModal = ({ isOpen, onClose, data, filterInfo }) => {
  if (!isOpen) return null;

  const purchases = data?.purchases || 0;
  const transferIn = data?.transferIn || 0;
  const transferOut = data?.transferOut || 0;
  const netMovement = data?.netMovement !== undefined ? data.netMovement : (purchases + transferIn - transferOut);

  return (
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
      onClick={onClose}
    >
      <div
        style={{
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-medium)',
          borderRadius: 'var(--radius-lg)',
          width: '100%',
          maxWidth: '480px',
          boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.5), 0 8px 10px -6px rgba(0, 0, 0, 0.5)',
          overflow: 'hidden',
          animation: 'fadeIn 0.15s ease-out',
        }}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Modal Header */}
        <div
          style={{
            padding: '16px 20px',
            borderBottom: '1px solid var(--border-subtle)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            backgroundColor: 'rgba(255, 255, 255, 0.02)',
          }}
        >
          <div>
            <h3 style={{ margin: 0, fontSize: '1.125rem', fontWeight: 700, color: 'var(--text-primary)' }}>
              Net Movement Breakdown
            </h3>
            <p style={{ margin: '4px 0 0', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
              Live audited calculation formula: Purchases + Transfer In − Transfer Out
            </p>
          </div>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              color: 'var(--text-secondary)',
              cursor: 'pointer',
              padding: '4px',
              borderRadius: '6px',
            }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Filter context tag */}
        {filterInfo && (
          <div
            style={{
              padding: '8px 20px',
              backgroundColor: 'rgba(56, 189, 248, 0.05)',
              borderBottom: '1px solid var(--border-subtle)',
              fontSize: '0.75rem',
              color: '#7dd3fc',
              display: 'flex',
              gap: '12px',
              flexWrap: 'wrap',
            }}
          >
            <span>Base: <strong>{filterInfo.baseName || 'All Bases'}</strong></span>
            <span>Equipment: <strong>{filterInfo.equipmentName || 'All Equipment'}</strong></span>
            {filterInfo.dateRange && <span>Period: <strong>{filterInfo.dateRange}</strong></span>}
          </div>
        )}

        {/* Modal Body: Math Breakdown */}
        <div style={{ padding: '24px 20px' }}>
          <div
            style={{
              display: 'flex',
              flexDirection: 'column',
              gap: '14px',
              fontFamily: 'monospace',
              fontSize: '0.9375rem',
            }}
          >
            {/* Purchases */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '10px 14px',
                borderRadius: '6px',
                backgroundColor: 'rgba(34, 197, 94, 0.08)',
                border: '1px solid rgba(34, 197, 94, 0.2)',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: 'var(--text-primary)' }}>
                <ShoppingBag size={18} color="#22c55e" />
                <span style={{ fontWeight: 600 }}>Purchases (Acquisitions)</span>
              </div>
              <span style={{ color: '#4ade80', fontWeight: 700, fontSize: '1.0625rem' }}>
                +{purchases.toLocaleString()}
              </span>
            </div>

            {/* Transfer In */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '10px 14px',
                borderRadius: '6px',
                backgroundColor: 'rgba(56, 189, 248, 0.08)',
                border: '1px solid rgba(56, 189, 248, 0.2)',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: 'var(--text-primary)' }}>
                <ArrowDownLeft size={18} color="#38bdf8" />
                <span style={{ fontWeight: 600 }}>Transfer In (Inter-Base Inflow)</span>
              </div>
              <span style={{ color: '#38bdf8', fontWeight: 700, fontSize: '1.0625rem' }}>
                +{transferIn.toLocaleString()}
              </span>
            </div>

            {/* Transfer Out */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '10px 14px',
                borderRadius: '6px',
                backgroundColor: 'rgba(239, 68, 68, 0.08)',
                border: '1px solid rgba(239, 68, 68, 0.2)',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: 'var(--text-primary)' }}>
                <ArrowUpRight size={18} color="#f87171" />
                <span style={{ fontWeight: 600 }}>Transfer Out (Inter-Base Outflow)</span>
              </div>
              <span style={{ color: '#f87171', fontWeight: 700, fontSize: '1.0625rem' }}>
                -{transferOut.toLocaleString()}
              </span>
            </div>

            {/* Divider line */}
            <div
              style={{
                borderBottom: '2px dashed var(--border-medium)',
                margin: '6px 0',
              }}
            />

            {/* Net Movement Total */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '14px',
                borderRadius: '8px',
                backgroundColor: 'rgba(255, 255, 255, 0.04)',
                border: '1px solid var(--border-medium)',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: 'var(--text-primary)' }}>
                <Equal size={20} color="var(--accent-amber)" />
                <span style={{ fontWeight: 700, fontSize: '1.0625rem' }}>Calculated Net Movement</span>
              </div>
              <span
                style={{
                  fontWeight: 800,
                  fontSize: '1.375rem',
                  color: netMovement >= 0 ? '#4ade80' : '#f87171',
                }}
              >
                {netMovement >= 0 ? `+${netMovement.toLocaleString()}` : netMovement.toLocaleString()}
              </span>
            </div>
          </div>
        </div>

        {/* Modal Footer */}
        <div
          style={{
            padding: '12px 20px',
            borderTop: '1px solid var(--border-subtle)',
            backgroundColor: 'rgba(255, 255, 255, 0.02)',
            display: 'flex',
            justifyContent: 'flex-end',
          }}
        >
          <button
            onClick={onClose}
            className="btn btn-secondary"
            style={{ minWidth: '90px' }}
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};
