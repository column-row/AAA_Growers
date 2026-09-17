import React from 'react';

const StatusBadge = ({ status, type = 'default' }) => {
  const getStyle = (status) => {
    switch (status?.toUpperCase()) {
      case 'ACTIVE':
      case 'DELIVERED':
      case 'PAID':
      case 'SUCCESS':
      case 'COMPLETED':
      case 'RESOLVED':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200 ring-emerald-600/20';
      
      case 'PENDING':
      case 'NEW':
      case 'UPCOMING':
      case 'BOOKED':
        return 'bg-amber-50 text-amber-700 border-amber-200 ring-amber-600/20';

      case 'PROCESSING':
      case 'ASSIGNED':
      case 'IN_PROGRESS':
      case 'ONGOING':
        return 'bg-blue-50 text-blue-700 border-blue-200 ring-blue-600/20';

      case 'DISPATCHED':
      case 'IN_TRANSIT':
        return 'bg-indigo-50 text-indigo-700 border-indigo-200 ring-indigo-600/20';

      case 'CANCELLED':
      case 'FAILED':
      case 'INACTIVE':
      case 'SUSPENDED':
      case 'REVOKED':
      case 'EXPIRED':
        return 'bg-rose-50 text-rose-700 border-rose-200 ring-rose-600/20';

      case 'REFUNDED':
        return 'bg-purple-50 text-purple-700 border-purple-200 ring-purple-600/20';

      default:
        return 'bg-slate-50 text-slate-700 border-slate-200 ring-slate-600/20';
    }
  };

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ring-1 ring-inset ${getStyle(status)}`}>
      <span className="w-1.5 h-1.5 rounded-full mr-1.5 bg-current opacity-70"></span>
      {status ? status.replace('_', ' ') : 'N/A'}
    </span>
  );
};

export default StatusBadge;
