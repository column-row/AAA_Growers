import React, { useState, useEffect } from 'react';
import { Boxes, Plus, Minus, AlertTriangle, History, RefreshCw, CheckCircle2 } from 'lucide-react';
import Modal from '../components/Modal';
import api from '../services/api';

const InventoryPage = () => {
  const [inventory, setInventory] = useState([]);
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterLowStock, setFilterLowStock] = useState(false);
  const [adjustModalOpen, setAdjustModalOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [adjustForm, setAdjustForm] = useState({
    change_quantity: '',
    movement_type: 'RESTOCK',
    notes: '',
    reference_id: ''
  });

  const fetchInventory = async () => {
    try {
      setLoading(true);
      const [invRes, logRes] = await Promise.all([
        api.get(`/inventory?low_stock_only=${filterLowStock}`),
        api.get('/inventory/logs?limit=15')
      ]);
      if (invRes.data.success) setInventory(invRes.data.data.items);
      if (logRes.data.success) setLogs(logRes.data.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInventory();
  }, [filterLowStock]);

  const handleAdjustSubmit = async (e) => {
    e.preventDefault();
    try {
      const qty = parseInt(adjustForm.change_quantity, 10);
      await api.put(`/inventory/${selectedProduct.product_id}`, {
        change_quantity: qty,
        movement_type: adjustForm.movement_type,
        notes: adjustForm.notes,
        reference_id: adjustForm.reference_id
      });
      setAdjustModalOpen(false);
      fetchInventory();
    } catch (err) {
      alert(err.response?.data?.message || 'Stock adjustment failed');
    }
  };

  const openAdjust = (item, defaultType = 'RESTOCK') => {
    setSelectedProduct(item);
    setAdjustForm({
      change_quantity: defaultType === 'RESTOCK' ? '50' : '-10',
      movement_type: defaultType,
      notes: '',
      reference_id: `ADJ-${Date.now().toString().slice(-6)}`
    });
    setAdjustModalOpen(true);
  };

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Warehouse & Stock Control</h1>
          <p className="text-sm text-slate-500 mt-1">Real-time inventory levels, safety thresholds, and audit movement logs.</p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => setFilterLowStock(!filterLowStock)}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition flex items-center gap-2 border ${
              filterLowStock
                ? 'bg-rose-600 text-white border-rose-700'
                : 'bg-white text-slate-700 border-slate-200 hover:bg-slate-50'
            }`}
          >
            <AlertTriangle className="w-4 h-4" />
            {filterLowStock ? 'Showing Low Stock Only' : 'Filter Low Stock'}
          </button>
          <button
            onClick={fetchInventory}
            className="p-2 bg-white border border-slate-200 hover:bg-slate-50 rounded-xl text-slate-600"
            title="Refresh"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Main Stock Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50/70 border-b border-slate-100 text-xs uppercase text-slate-500 font-semibold">
              <tr>
                <th className="py-3.5 px-6">Produce Item</th>
                <th className="py-3.5 px-6">SKU Code</th>
                <th className="py-3.5 px-6 text-center">Current Stock</th>
                <th className="py-3.5 px-6 text-center">Safety Threshold</th>
                <th className="py-3.5 px-6">Health Status</th>
                <th className="py-3.5 px-6 text-right">Stock Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="6" className="py-8 text-center text-slate-400">Loading warehouse levels...</td>
                </tr>
              ) : (
                inventory.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50/80 transition">
                    <td className="py-4 px-6 font-bold text-slate-900">{item.product_name}</td>
                    <td className="py-4 px-6 text-xs text-slate-400 font-mono">{item.product_sku}</td>
                    <td className="py-4 px-6 text-center">
                      <span className="font-mono font-extrabold text-base text-slate-900">
                        {item.current_stock}
                      </span>
                    </td>
                    <td className="py-4 px-6 text-center text-xs font-mono text-slate-500">
                      {item.low_stock_threshold} units
                    </td>
                    <td className="py-4 px-6">
                      {item.is_out_of_stock ? (
                        <span className="inline-flex items-center gap-1 text-xs font-bold text-rose-700 bg-rose-50 px-2.5 py-1 rounded-full border border-rose-200">
                          <AlertTriangle className="w-3.5 h-3.5" /> Out of Stock
                        </span>
                      ) : item.is_low_stock ? (
                        <span className="inline-flex items-center gap-1 text-xs font-bold text-amber-700 bg-amber-50 px-2.5 py-1 rounded-full border border-amber-200">
                          <AlertTriangle className="w-3.5 h-3.5" /> Low Stock Warning
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1 text-xs font-bold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-full border border-emerald-200">
                          <CheckCircle2 className="w-3.5 h-3.5" /> Healthy Level
                        </span>
                      )}
                    </td>
                    <td className="py-4 px-6 text-right space-x-2">
                      <button
                        onClick={() => openAdjust(item, 'RESTOCK')}
                        className="px-2.5 py-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 rounded-lg text-xs font-bold transition inline-flex items-center gap-1"
                      >
                        <Plus className="w-3.5 h-3.5" /> Restock
                      </button>
                      <button
                        onClick={() => openAdjust(item, 'ADJUSTMENT')}
                        className="px-2.5 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold transition inline-flex items-center gap-1"
                      >
                        Adjust
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Movement Logs History */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
        <div className="flex items-center gap-2 mb-4">
          <History className="w-5 h-5 text-slate-600" />
          <h3 className="font-bold text-slate-900 text-base">Recent Stock Movement Logs</h3>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 text-slate-500 font-semibold uppercase">
              <tr>
                <th className="py-2.5 px-4">Date & Time</th>
                <th className="py-2.5 px-4">Product</th>
                <th className="py-2.5 px-4">Movement Type</th>
                <th className="py-2.5 px-4 text-center">Change Qty</th>
                <th className="py-2.5 px-4 text-center">Previous $\to$ New</th>
                <th className="py-2.5 px-4">Notes</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 font-mono">
              {logs.map((log) => (
                <tr key={log.id}>
                  <td className="py-2.5 px-4 text-slate-400 font-sans">
                    {new Date(log.created_at).toLocaleString()}
                  </td>
                  <td className="py-2.5 px-4 font-sans font-semibold text-slate-900">{log.product_name}</td>
                  <td className="py-2.5 px-4 font-sans">
                    <span className="px-2 py-0.5 rounded bg-slate-100 font-semibold text-slate-700">
                      {log.movement_type}
                    </span>
                  </td>
                  <td className={`py-2.5 px-4 text-center font-bold ${log.change_quantity > 0 ? 'text-emerald-600' : 'text-rose-600'}`}>
                    {log.change_quantity > 0 ? `+${log.change_quantity}` : log.change_quantity}
                  </td>
                  <td className="py-2.5 px-4 text-center text-slate-600">
                    {log.previous_stock} $\to$ {log.new_stock}
                  </td>
                  <td className="py-2.5 px-4 font-sans text-slate-500 italic">{log.notes || '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Stock Adjustment Modal */}
      <Modal
        isOpen={adjustModalOpen}
        onClose={() => setAdjustModalOpen(false)}
        title={`Adjust Stock: ${selectedProduct?.product_name}`}
      >
        <form onSubmit={handleAdjustSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Movement Type</label>
            <select
              value={adjustForm.movement_type}
              onChange={(e) => setAdjustForm({ ...adjustForm, movement_type: e.target.value })}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
            >
              <option value="RESTOCK">RESTOCK (Warehouse Intake / Supplier Delivery)</option>
              <option value="PURCHASE_ORDER">PURCHASE ORDER INTAKE</option>
              <option value="ADJUSTMENT">PHYSICAL AUDIT ADJUSTMENT</option>
              <option value="RETURN">CUSTOMER RETURN</option>
              <option value="DAMAGE">DAMAGE / SPOILAGE WRITE-OFF (Negative)</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">
              Quantity Change (+ to Add, - to Deduct)
            </label>
            <input
              type="number"
              required
              value={adjustForm.change_quantity}
              onChange={(e) => setAdjustForm({ ...adjustForm, change_quantity: e.target.value })}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm font-mono"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Reference Code</label>
            <input
              type="text"
              value={adjustForm.reference_id}
              onChange={(e) => setAdjustForm({ ...adjustForm, reference_id: e.target.value })}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm font-mono"
              placeholder="e.g. GRN-2026-004"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Reason / Notes</label>
            <textarea
              rows={2}
              value={adjustForm.notes}
              onChange={(e) => setAdjustForm({ ...adjustForm, notes: e.target.value })}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              placeholder="Reason for manual adjustment..."
            />
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setAdjustModalOpen(false)}
              className="px-4 py-2 bg-slate-100 text-slate-700 rounded-xl text-sm font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2 bg-emerald-600 text-white rounded-xl text-sm font-bold shadow-md shadow-emerald-700/20"
            >
              Commit Adjustment
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default InventoryPage;
