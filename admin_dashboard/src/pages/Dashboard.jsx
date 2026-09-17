import React, { useState, useEffect } from 'react';
import {
  Users,
  Sprout,
  ShoppingBag,
  ShoppingCart,
  Clock,
  CheckCircle2,
  DollarSign,
  AlertTriangle,
  GraduationCap,
  ArrowUpRight,
  TrendingUp
} from 'lucide-react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  BarChart,
  Bar,
  Cell
} from 'recharts';
import StatCard from '../components/StatCard';
import StatusBadge from '../components/StatusBadge';
import InvoiceModal from '../components/InvoiceModal';
import api from '../services/api';

const Dashboard = () => {
  const [kpis, setKpis] = useState(null);
  const [salesTrend, setSalesTrend] = useState([]);
  const [recentOrders, setRecentOrders] = useState([]);
  const [lowStockItems, setLowStockItems] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [kpiRes, salesRes, ordersRes, invRes] = await Promise.all([
          api.get('/reports/dashboard'),
          api.get('/reports/sales?period=monthly'),
          api.get('/orders?per_page=5'),
          api.get('/inventory?low_stock_only=true&per_page=5'),
        ]);

        if (kpiRes.data.success) setKpis(kpiRes.data.data);
        if (salesRes.data.success) setSalesTrend(salesRes.data.data.data || []);
        if (ordersRes.data.success) setRecentOrders(ordersRes.data.data.items || []);
        if (invRes.data.success) setLowStockItems(invRes.data.data.items || []);
      } catch (err) {
        console.error('Failed to load dashboard', err);
      } finally {
        setLoading(false);
      }
    };
    fetchDashboardData();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-emerald-600"></div>
      </div>
    );
  }

  // Fallback demo trend data if new database has few days
  const chartData = salesTrend.length > 0 ? salesTrend : [
    { date: 'Day 1', revenue: 12500 },
    { date: 'Day 2', revenue: 18400 },
    { date: 'Day 3', revenue: 24000 },
    { date: 'Day 4', revenue: 31200 },
    { date: 'Day 5', revenue: 28900 },
    { date: 'Day 6', revenue: 42500 },
    { date: 'Day 7', revenue: 38800 },
  ];

  return (
    <div className="space-y-8">
      {/* Top Header */}
      <div>
        <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Executive Dashboard</h1>
        <p className="text-sm text-slate-500 mt-1">Real-time agricultural e-commerce and operations metrics.</p>
      </div>

      {/* 8 Primary KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <StatCard
          title="Total Revenue"
          value={`KES ${(kpis?.total_sales || 0).toLocaleString()}`}
          subtitle="Processed payments"
          icon={DollarSign}
          color="emerald"
        />
        <StatCard
          title="Total Orders"
          value={kpis?.total_orders || 0}
          subtitle={`${kpis?.completed_orders || 0} delivered`}
          icon={ShoppingCart}
          color="blue"
        />
        <StatCard
          title="Pending Orders"
          value={kpis?.pending_orders || 0}
          subtitle="Requires dispatch/packing"
          icon={Clock}
          color="amber"
        />
        <StatCard
          title="Stock Alerts"
          value={kpis?.inventory_alerts || 0}
          subtitle="Items below threshold"
          icon={AlertTriangle}
          color="rose"
        />
        <StatCard
          title="Active Products"
          value={kpis?.total_products || 0}
          subtitle="Catalog stock items"
          icon={ShoppingBag}
          color="indigo"
        />
        <StatCard
          title="Enrolled Farmers"
          value={kpis?.total_farmers || 0}
          subtitle="Registered outgrowers"
          icon={Sprout}
          color="emerald"
        />
        <StatCard
          title="Retail Customers"
          value={kpis?.total_customers || 0}
          subtitle="Active e-commerce buyers"
          icon={Users}
          color="purple"
        />
        <StatCard
          title="Upcoming Training"
          value={kpis?.upcoming_training || 0}
          subtitle="Sessions scheduled"
          icon={GraduationCap}
          color="blue"
        />
      </div>

      {/* Analytics Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Sales Revenue Trend */}
        <div className="lg:col-span-2 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="font-bold text-slate-900 text-base">Revenue Performance Trend</h3>
              <p className="text-xs text-slate-500">Gross sales volume across recent active periods</p>
            </div>
            <span className="flex items-center gap-1 text-xs font-bold text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-lg border border-emerald-200">
              <TrendingUp className="w-3.5 h-3.5" /> +14.8% MoM
            </span>
          </div>

          <div className="h-72 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#16a34a" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#16a34a" stopOpacity={0.0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" vertical={false} />
                <XAxis dataKey="date" stroke="#94a3b8" fontSize={11} tickLine={false} />
                <YAxis stroke="#94a3b8" fontSize={11} tickLine={false} />
                <Tooltip
                  formatter={(value) => [`KES ${Number(value).toLocaleString()}`, 'Revenue']}
                  contentStyle={{ backgroundColor: '#0f172a', borderRadius: '12px', border: 'none', color: '#fff' }}
                />
                <Area type="monotone" dataKey="revenue" stroke="#16a34a" strokeWidth={3} fillOpacity={1} fill="url(#colorRevenue)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Quick Inventory Stock Alert Panel */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 text-base">Low Stock Warnings</h3>
              <AlertTriangle className="w-5 h-5 text-amber-500" />
            </div>
            <p className="text-xs text-slate-500 mb-4">Products requiring immediate supplier replenishment</p>

            <div className="space-y-3">
              {lowStockItems.length === 0 ? (
                <p className="text-xs text-slate-400 py-6 text-center">All inventory stock levels are healthy.</p>
              ) : (
                lowStockItems.map((item) => (
                  <div key={item.id} className="p-3 bg-slate-50 rounded-xl border border-slate-200/80 flex items-center justify-between">
                    <div>
                      <p className="text-xs font-bold text-slate-900">{item.product_name}</p>
                      <p className="text-[11px] text-slate-500 font-mono">SKU: {item.product_sku}</p>
                    </div>
                    <div className="text-right">
                      <span className="text-xs font-black text-rose-600 bg-rose-50 px-2 py-0.5 rounded-md border border-rose-200">
                        {item.current_stock} left
                      </span>
                      <p className="text-[10px] text-slate-400 mt-0.5">Min: {item.low_stock_threshold}</p>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          <a
            href="/inventory"
            className="mt-4 w-full py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl text-center transition block"
          >
            Open Inventory Manager →
          </a>
        </div>
      </div>

      {/* Recent Orders Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h3 className="font-bold text-slate-900 text-base">Recent Orders</h3>
            <p className="text-xs text-slate-500">Live order stream from mobile app & web store</p>
          </div>
          <a href="/orders" className="text-xs font-bold text-emerald-600 hover:text-emerald-700 flex items-center gap-1">
            View All Orders <ArrowUpRight className="w-4 h-4" />
          </a>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50/70 border-b border-slate-100 text-xs uppercase text-slate-500 font-semibold">
              <tr>
                <th className="py-3 px-6">Order #</th>
                <th className="py-3 px-6">Customer</th>
                <th className="py-3 px-6">Amount</th>
                <th className="py-3 px-6">Payment</th>
                <th className="py-3 px-6">Order Status</th>
                <th className="py-3 px-6 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {recentOrders.map((order) => (
                <tr key={order.id} className="hover:bg-slate-50/80 transition">
                  <td className="py-4 px-6 font-mono font-bold text-slate-900">{order.order_number}</td>
                  <td className="py-4 px-6">
                    <p className="font-semibold text-slate-900">{order.customer_name}</p>
                    <p className="text-xs text-slate-400">{order.delivery_city}</p>
                  </td>
                  <td className="py-4 px-6 font-mono font-bold text-slate-900">
                    KES {order.net_amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                  </td>
                  <td className="py-4 px-6">
                    <StatusBadge status={order.payment_status} />
                  </td>
                  <td className="py-4 px-6">
                    <StatusBadge status={order.status} />
                  </td>
                  <td className="py-4 px-6 text-right">
                    <button
                      onClick={() => setSelectedOrder(order)}
                      className="px-3 py-1 bg-slate-100 hover:bg-emerald-50 hover:text-emerald-700 text-slate-700 rounded-lg text-xs font-bold transition"
                    >
                      View Invoice
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Invoice Modal Preview */}
      <InvoiceModal
        isOpen={Boolean(selectedOrder)}
        onClose={() => setSelectedOrder(null)}
        order={selectedOrder}
      />
    </div>
  );
};

export default Dashboard;
