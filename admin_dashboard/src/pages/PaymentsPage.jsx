import React, { useState, useEffect } from 'react';
import { CreditCard, Search, Filter, RotateCcw, CheckCircle2 } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import api from '../services/api';

const PaymentsPage = () => {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('');

  const fetchPayments = async () => {
    try {
      setLoading(true);
      const params = new URLSearchParams();
      if (statusFilter) params.append('status', statusFilter);
      const res = await api.get(`/payments?${params.toString()}`);
      if (res.data.success) {
        setPayments(res.data.data.items);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPayments();
  }, [statusFilter]);

  const handleRefund = async (paymentId) => {
    const reason = prompt('Enter reason for transaction refund:');
    if (!reason) return;

    try {
      await api.post(`/payments/${paymentId}/refund`, { reason });
      alert('Payment refunded successfully');
      fetchPayments();
    } catch (err) {
      alert(err.response?.data?.message || 'Refund failed');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Payments & Financial Transactions</h1>
          <p className="text-sm text-slate-500 mt-1">Audit M-Pesa, card and bank payments with transaction references.</p>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Filter className="w-4 h-4 text-slate-400" />
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:border-emerald-600"
          >
            <option value="">All Payment Statuses</option>
            <option value="SUCCESS">SUCCESS</option>
            <option value="PENDING">PENDING</option>
            <option value="FAILED">FAILED</option>
            <option value="REFUNDED">REFUNDED</option>
          </select>
        </div>
        <span className="text-xs text-slate-500 font-semibold">{payments.length} Transactions</span>
      </div>

      {/* Payments Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50/70 border-b border-slate-100 text-xs uppercase text-slate-500 font-semibold">
              <tr>
                <th className="py-3.5 px-6">Transaction Ref</th>
                <th className="py-3.5 px-6">Order #</th>
                <th className="py-3.5 px-6">Customer</th>
                <th className="py-3.5 px-6">Method</th>
                <th className="py-3.5 px-6">Amount (KES)</th>
                <th className="py-3.5 px-6">Payment Date</th>
                <th className="py-3.5 px-6">Status</th>
                <th className="py-3.5 px-6 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 font-mono">
              {loading ? (
                <tr>
                  <td colSpan="8" className="py-8 text-center text-slate-400 font-sans">Loading financial logs...</td>
                </tr>
              ) : payments.length === 0 ? (
                <tr>
                  <td colSpan="8" className="py-8 text-center text-slate-400 font-sans">No payment records found.</td>
                </tr>
              ) : (
                payments.map((p) => (
                  <tr key={p.id} className="hover:bg-slate-50/80 transition">
                    <td className="py-4 px-6 font-bold text-slate-900">{p.transaction_reference}</td>
                    <td className="py-4 px-6 text-xs text-slate-600 font-sans font-semibold">{p.order_number}</td>
                    <td className="py-4 px-6 text-xs font-sans text-slate-800">{p.user_name}</td>
                    <td className="py-4 px-6 font-sans">
                      <span className="px-2 py-0.5 rounded-md bg-slate-100 text-slate-700 text-xs font-bold">
                        {p.payment_method}
                      </span>
                    </td>
                    <td className="py-4 px-6 font-bold text-slate-900">
                      KES {p.amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                    </td>
                    <td className="py-4 px-6 text-xs font-sans text-slate-500">
                      {p.paid_at ? new Date(p.paid_at).toLocaleString() : '—'}
                    </td>
                    <td className="py-4 px-6 font-sans">
                      <StatusBadge status={p.status} />
                    </td>
                    <td className="py-4 px-6 text-right font-sans">
                      {p.status === 'SUCCESS' && (
                        <button
                          onClick={() => handleRefund(p.id)}
                          className="px-2.5 py-1 bg-rose-50 hover:bg-rose-100 text-rose-700 rounded-lg text-xs font-bold transition inline-flex items-center gap-1"
                        >
                          <RotateCcw className="w-3 h-3" /> Refund
                        </button>
                      )}
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

export default PaymentsPage;
