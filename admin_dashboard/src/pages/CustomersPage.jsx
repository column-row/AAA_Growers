import React, { useState, useEffect } from 'react';
import { UserCheck, Search, Mail, Phone, MapPin, ShoppingCart } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import api from '../services/api';

const CustomersPage = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    const fetchCustomers = async () => {
      try {
        setLoading(true);
        const res = await api.get(`/users?role=CUSTOMER&search=${search}`);
        if (res.data.success) {
          setCustomers(res.data.data.items);
        }
      } catch (err) {
        console.error('Failed to load customers', err);
      } finally {
        setLoading(false);
      }
    };
    fetchCustomers();
  }, [search]);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Customers Directory</h1>
          <p className="text-sm text-slate-500 mt-1">E-commerce retail and wholesale produce buyers.</p>
        </div>
      </div>

      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search customers by name, email or phone..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-9 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-600 transition"
          />
        </div>
        <span className="text-xs text-slate-500 font-semibold">{customers.length} Registered Customers</span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {loading ? (
          <div className="col-span-full py-12 text-center text-slate-400">Loading customers...</div>
        ) : customers.length === 0 ? (
          <div className="col-span-full py-12 text-center text-slate-400">No customers found.</div>
        ) : (
          customers.map((c) => (
            <div key={c.id} className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition">
              <div className="flex items-center justify-between mb-4">
                <div className="w-12 h-12 rounded-2xl bg-purple-100 text-purple-800 font-bold flex items-center justify-center text-lg border border-purple-200">
                  {c.first_name[0]}
                </div>
                <StatusBadge status={c.status} />
              </div>

              <h3 className="font-bold text-slate-900 text-base">{c.full_name}</h3>
              <p className="text-xs text-slate-400 font-mono mt-0.5">ID #{c.id.toString().padStart(4, '0')}</p>

              <div className="space-y-2 mt-4 text-xs text-slate-600">
                <div className="flex items-center gap-2">
                  <Mail className="w-3.5 h-3.5 text-slate-400" />
                  <span>{c.email}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Phone className="w-3.5 h-3.5 text-slate-400" />
                  <span className="font-mono">{c.phone || 'No phone recorded'}</span>
                </div>
                <div className="flex items-center gap-2">
                  <MapPin className="w-3.5 h-3.5 text-slate-400" />
                  <span>{c.city || 'Nairobi'} - {c.address || 'Standard Delivery'}</span>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default CustomersPage;
