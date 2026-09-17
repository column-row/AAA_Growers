import React, { useState, useEffect } from 'react';
import { Truck, UserPlus, MapPin, CheckCircle2, Navigation, AlertCircle } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import Modal from '../components/Modal';
import api from '../services/api';

const DISPATCH_STATUSES = ['PENDING', 'ASSIGNED', 'IN_TRANSIT', 'DELIVERED', 'FAILED'];

const DispatchPage = () => {
  const [dispatches, setDispatches] = useState([]);
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [assignModalDispatch, setAssignModalDispatch] = useState(null);
  const [selectedDriverId, setSelectedDriverId] = useState('');
  const [statusModalDispatch, setStatusModalDispatch] = useState(null);
  const [newStatus, setNewStatus] = useState('');
  const [trackingNotes, setTrackingNotes] = useState('');

  const fetchDispatchesAndDrivers = async () => {
    try {
      setLoading(true);
      const [dspRes, drvRes] = await Promise.all([
        api.get('/dispatches'),
        api.get('/drivers')
      ]);
      if (dspRes.data.success) setDispatches(dspRes.data.data.items);
      if (drvRes.data.success) setDrivers(drvRes.data.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDispatchesAndDrivers();
  }, []);

  const handleAssignDriver = async (e) => {
    e.preventDefault();
    try {
      await api.put(`/dispatches/${assignModalDispatch.id}/assign-driver`, {
        driver_id: parseInt(selectedDriverId, 10)
      });
      setAssignModalDispatch(null);
      fetchDispatchesAndDrivers();
    } catch (err) {
      alert(err.response?.data?.message || 'Driver assignment failed');
    }
  };

  const handleStatusUpdate = async (e) => {
    e.preventDefault();
    try {
      await api.put(`/dispatches/${statusModalDispatch.id}/status`, {
        status: newStatus,
        notes: trackingNotes
      });
      setStatusModalDispatch(null);
      fetchDispatchesAndDrivers();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update dispatch status');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Logistics & Dispatch Fleet Control</h1>
          <p className="text-sm text-slate-500 mt-1">Assign drivers, monitor transit routes and verify cold-chain customer deliveries.</p>
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50/70 border-b border-slate-100 text-xs uppercase text-slate-500 font-semibold">
              <tr>
                <th className="py-3.5 px-6">Dispatch #</th>
                <th className="py-3.5 px-6">Order Ref</th>
                <th className="py-3.5 px-6">Assigned Driver</th>
                <th className="py-3.5 px-6">Destination Address</th>
                <th className="py-3.5 px-6">Logistics Status</th>
                <th className="py-3.5 px-6">Tracking Remarks</th>
                <th className="py-3.5 px-6 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="7" className="py-8 text-center text-slate-400">Loading dispatches...</td>
                </tr>
              ) : dispatches.length === 0 ? (
                <tr>
                  <td colSpan="7" className="py-8 text-center text-slate-400">No active dispatch orders.</td>
                </tr>
              ) : (
                dispatches.map((d) => (
                  <tr key={d.id} className="hover:bg-slate-50/80 transition">
                    <td className="py-4 px-6 font-mono font-bold text-slate-900">{d.dispatch_number}</td>
                    <td className="py-4 px-6 font-mono text-xs text-slate-600 font-semibold">{d.order_number}</td>
                    <td className="py-4 px-6">
                      {d.driver_name && d.driver_name !== 'Unassigned' ? (
                        <div>
                          <p className="font-bold text-slate-900 leading-none">{d.driver_name}</p>
                          <p className="text-xs text-slate-400 font-mono mt-1">{d.vehicle_reg || 'Vehicle Assigned'}</p>
                        </div>
                      ) : (
                        <button
                          onClick={() => {
                            setAssignModalDispatch(d);
                            setSelectedDriverId(drivers[0]?.id || '');
                          }}
                          className="px-2.5 py-1 bg-amber-50 hover:bg-amber-100 text-amber-800 rounded-lg text-xs font-bold transition inline-flex items-center gap-1 border border-amber-200"
                        >
                          <UserPlus className="w-3.5 h-3.5" /> Assign Driver
                        </button>
                      )}
                    </td>
                    <td className="py-4 px-6 text-xs text-slate-600 max-w-[220px] truncate" title={d.delivery_address}>
                      {d.delivery_address}
                    </td>
                    <td className="py-4 px-6">
                      <StatusBadge status={d.status} />
                    </td>
                    <td className="py-4 px-6 text-xs text-slate-500 italic max-w-[200px] truncate">
                      {d.tracking_notes || '—'}
                    </td>
                    <td className="py-4 px-6 text-right space-x-2">
                      <button
                        onClick={() => {
                          setStatusModalDispatch(d);
                          setNewStatus(d.status);
                          setTrackingNotes(d.tracking_notes || '');
                        }}
                        className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition inline-flex items-center gap-1"
                      >
                        Update Status
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Assign Driver Modal */}
      <Modal
        isOpen={Boolean(assignModalDispatch)}
        onClose={() => setAssignModalDispatch(null)}
        title={`Assign Fleet Driver to ${assignModalDispatch?.dispatch_number}`}
      >
        <form onSubmit={handleAssignDriver} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Select Available Driver</label>
            <select
              value={selectedDriverId}
              onChange={(e) => setSelectedDriverId(e.target.value)}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
            >
              {drivers.map((drv) => (
                <option key={drv.id} value={drv.id}>
                  {drv.name} ({drv.vehicle_registration} - {drv.vehicle_type}) {drv.is_available ? '• Available' : '• On Route'}
                </option>
              ))}
            </select>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setAssignModalDispatch(null)}
              className="px-4 py-2 bg-slate-100 text-slate-700 rounded-xl text-sm font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2 bg-emerald-600 text-white rounded-xl text-sm font-bold shadow-md shadow-emerald-700/20"
            >
              Confirm Assignment
            </button>
          </div>
        </form>
      </Modal>

      {/* Dispatch Status Update Modal */}
      <Modal
        isOpen={Boolean(statusModalDispatch)}
        onClose={() => setStatusModalDispatch(null)}
        title={`Update Status: ${statusModalDispatch?.dispatch_number}`}
      >
        <form onSubmit={handleStatusUpdate} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Transit Status</label>
            <select
              value={newStatus}
              onChange={(e) => setNewStatus(e.target.value)}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm font-semibold"
            >
              {DISPATCH_STATUSES.map((st) => (
                <option key={st} value={st}>{st}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Route / Milestone Notes</label>
            <textarea
              rows={2}
              value={trackingNotes}
              onChange={(e) => setTrackingNotes(e.target.value)}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              placeholder="e.g. Departed Nairobi Hub, arrived at customer gate..."
            />
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setStatusModalDispatch(null)}
              className="px-4 py-2 bg-slate-100 text-slate-700 rounded-xl text-sm font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2 bg-emerald-600 text-white rounded-xl text-sm font-bold shadow-md shadow-emerald-700/20"
            >
              Save Dispatch Update
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default DispatchPage;
