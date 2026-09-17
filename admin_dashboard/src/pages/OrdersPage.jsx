import React, { useState, useEffect } from 'react';
import { ShoppingCart, Search, Filter, Eye, RefreshCw, FileText, ChevronRight } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import InvoiceModal from '../components/InvoiceModal';
import Modal from '../components/Modal';
import api from '../services/api';

const ORDER_STATUSES = ['PENDING', 'PAID', 'PROCESSING', 'DISPATCHED', 'DELIVERED', 'CANCELLED'];

const OrdersPage = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('');
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [statusModalOrder, setStatusModalOrder] = useState(null);
  const [newStatus, setNewStatus] = useState('');
  const [statusNotes, setStatusNotes] = useState('');

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const params = new URLSearchParams();
      if (statusFilter) params.append('status', statusFilter);
      const res = await api.get(`/orders?${params.toString()}`);
      if (res.data.success) {
        setOrders(res.data.data.items);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [statusFilter]);

  const handleUpdateStatus = async (e) => {
    e.preventDefault();
    try {
      await api.put(`/orders/${statusModalOrder.id}/status`, {
        status: newStatus,
        notes: statusNotes
      });
      setStatusModalOrder(null);
      fetchOrders();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update order status');
    }
  };

  const openStatusChange = (order) => {
    setStatusModalOrder(order);
    setNewStatus(order.status);
    setStatusNotes('');
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Order Fulfilment & Management</h1>
          <p className="text-sm text-slate-500 mt-1">Track e-commerce orders through dispatch, payment and delivery.</p>
        </div>
        <button
          onClick={fetchOrders}
          className="p-2 bg-white border border-slate-200 hover:bg-slate-50 rounded-xl text-slate-600 transition"
        >
          <RefreshCw className="w-4 h-4" />
        </button>
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
            <option value="">All Statuses (Pending, Paid, Dispatched, Delivered)</option>
            {ORDER_STATUSES.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
        </div>
        <span className="text-xs text-slate-500 font-semibold">{orders.length} Total Orders</span>
      </div>

      {/* Orders Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50/70 border-b border-slate-100 text-xs uppercase text-slate-500 font-semibold">
              <tr>
                <th className="py-3.5 px-6">Order ID</th>
                <th className="py-3.5 px-6">Customer</th>
                <th className="py-3.5 px-6">Placed Date</th>
                <th className="py-3.5 px-6">Items</th>
                <th className="py-3.5 px-6">Total (KES)</th>
                <th className="py-3.5 px-6">Payment</th>
                <th className="py-3.5 px-6">Fulfilment Status</th>
                <th className="py-3.5 px-6 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="8" className="py-8 text-center text-slate-400">Loading orders...</td>
                </tr>
              ) : orders.length === 0 ? (
                <tr>
                  <td colSpan="8" className="py-8 text-center text-slate-400">No orders found.</td>
                </tr>
              ) : (
                orders.map((o) => (
                  <tr key={o.id} className="hover:bg-slate-50/80 transition">
                    <td className="py-4 px-6 font-mono font-bold text-slate-900">{o.order_number}</td>
                    <td className="py-4 px-6">
                      <p className="font-semibold text-slate-900">{o.customer_name}</p>
                      <p className="text-xs text-slate-400">{o.delivery_city}</p>
                    </td>
                    <td className="py-4 px-6 text-xs text-slate-500">
                      {new Date(o.placed_at).toLocaleDateString()}
                    </td>
                    <td className="py-4 px-6 text-xs font-semibold text-slate-700">
                      {o.items_count} items
                    </td>
                    <td className="py-4 px-6 font-mono font-extrabold text-slate-900">
                      KES {o.net_amount.toFixed(2)}
                    </td>
                    <td className="py-4 px-6">
                      <StatusBadge status={o.payment_status} />
                    </td>
                    <td className="py-4 px-6">
                      <button
                        onClick={() => openStatusChange(o)}
                        className="hover:scale-105 transition"
                        title="Click to change order status"
                      >
                        <StatusBadge status={o.status} />
                      </button>
                    </td>
                    <td className="py-4 px-6 text-right space-x-2">
                      <button
                        onClick={() => setSelectedOrder(o)}
                        className="px-3 py-1.5 bg-slate-100 hover:bg-emerald-50 hover:text-emerald-700 text-slate-700 rounded-xl text-xs font-bold transition inline-flex items-center gap-1"
                      >
                        <FileText className="w-3.5 h-3.5" /> Invoice
                      </button>
                      <button
                        onClick={() => openStatusChange(o)}
                        className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition"
                      >
                        Update
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Invoice Modal */}
      <InvoiceModal
        isOpen={Boolean(selectedOrder)}
        onClose={() => setSelectedOrder(null)}
        order={selectedOrder}
      />

      {/* Status Transition Modal */}
      <Modal
        isOpen={Boolean(statusModalOrder)}
        onClose={() => setStatusModalOrder(null)}
        title={`Change Status for Order #${statusModalOrder?.order_number}`}
      >
        <form onSubmit={handleUpdateStatus} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">New Workflow Status</label>
            <select
              value={newStatus}
              onChange={(e) => setNewStatus(e.target.value)}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm font-semibold"
            >
              {ORDER_STATUSES.map((s) => (
                <option key={s} value={s}>{s}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Update Notes / Dispatch Remarks</label>
            <textarea
              rows={2}
              value={statusNotes}
              onChange={(e) => setStatusNotes(e.target.value)}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              placeholder="e.g. Dispatched with Driver James Mwangi"
            />
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setStatusModalOrder(null)}
              className="px-4 py-2 bg-slate-100 text-slate-700 rounded-xl text-sm font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2 bg-emerald-600 text-white rounded-xl text-sm font-bold shadow-md shadow-emerald-700/20"
            >
              Update Status
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default OrdersPage;
