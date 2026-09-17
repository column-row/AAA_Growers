import React, { useState, useEffect } from 'react';
import { Car, CheckCircle2, AlertCircle, Phone, MapPin, Truck } from 'lucide-react';
import api from '../services/api';

const DriversPage = () => {
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDrivers = async () => {
      try {
        setLoading(true);
        const res = await api.get('/drivers');
        if (res.data.success) {
          setDrivers(res.data.data);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchDrivers();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Fleet Drivers Directory</h1>
          <p className="text-sm text-slate-500 mt-1">Delivery couriers, vehicle registrations and live dispatch availability.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {loading ? (
          <div className="col-span-full py-12 text-center text-slate-400">Loading fleet drivers...</div>
        ) : drivers.map((d) => (
          <div key={d.id} className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 hover:shadow-md transition">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-indigo-100 text-indigo-800 font-bold flex items-center justify-center text-lg border border-indigo-200">
                <Car className="w-6 h-6" />
              </div>
              <span className={`text-xs font-bold px-2.5 py-1 rounded-full border ${
                d.is_available
                  ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                  : 'bg-amber-50 text-amber-700 border-amber-200'
              }`}>
                {d.is_available ? 'Available' : 'On Dispatch Route'}
              </span>
            </div>

            <h3 className="font-bold text-slate-900 text-base">{d.name}</h3>
            <p className="text-xs text-slate-400 font-mono mt-0.5">License: {d.license_number}</p>

            <div className="space-y-2 mt-4 pt-3 border-t border-slate-100 text-xs text-slate-600">
              <div className="flex items-center gap-2">
                <Truck className="w-3.5 h-3.5 text-slate-400" />
                <span className="font-bold text-slate-800">{d.vehicle_registration}</span>
                <span className="text-slate-400">• {d.vehicle_type}</span>
              </div>
              <div className="flex items-center gap-2">
                <Phone className="w-3.5 h-3.5 text-slate-400" />
                <span className="font-mono">{d.phone || 'No direct line recorded'}</span>
              </div>
              <div className="flex items-center gap-2">
                <MapPin className="w-3.5 h-3.5 text-slate-400" />
                <span>Station: {d.current_location || 'Nairobi Central Hub'}</span>
              </div>
            </div>

            <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
              <span className="text-slate-500">Completed Deliveries</span>
              <span className="font-bold font-mono text-slate-900">{d.total_deliveries} Orders</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default DriversPage;
