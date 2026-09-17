import React, { useState, useEffect } from 'react';
import { BarChart3, Download, Calendar, Filter, DollarSign, ShoppingCart, Boxes, GraduationCap, Printer } from 'lucide-react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  Legend,
  AreaChart,
  Area
} from 'recharts';
import StatCard from '../components/StatCard';
import api from '../services/api';

const ReportsPage = () => {
  const [reportType, setReportType] = useState('sales'); // sales | orders | inventory | training
  const [period, setPeriod] = useState('monthly'); // daily | weekly | monthly | annual
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [reportData, setReportData] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchReport = async () => {
    try {
      setLoading(true);
      const params = new URLSearchParams();
      if (startDate) params.append('start_date', startDate);
      if (endDate) params.append('end_date', endDate);

      let endpoint = '';
      if (reportType === 'sales') {
        params.append('period', period);
        endpoint = `/reports/sales?${params.toString()}`;
      } else if (reportType === 'orders') {
        endpoint = `/reports/orders?${params.toString()}`;
      } else if (reportType === 'inventory') {
        endpoint = '/reports/inventory';
      } else if (reportType === 'training') {
        endpoint = '/reports/training';
      }

      const res = await api.get(endpoint);
      if (res.data.success) {
        setReportData(res.data.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReport();
  }, [reportType, period]);

  const exportCSV = () => {
    if (!reportData) return;
    let csvContent = 'data:text/csv;charset=utf-8,';

    if (reportType === 'sales' && reportData.data) {
      csvContent += 'Date,Transactions,Revenue (KES)\n';
      reportData.data.forEach((r) => {
        csvContent += `${r.date},${r.transactions},${r.revenue}\n`;
      });
    } else if (reportType === 'orders' && reportData.orders) {
      csvContent += 'Order Number,Customer,Status,Payment Status,Net Amount (KES),Date\n';
      reportData.orders.forEach((o) => {
        csvContent += `${o.order_number},"${o.customer_name}",${o.status},${o.payment_status},${o.net_amount},${o.placed_at}\n`;
      });
    } else if (reportType === 'inventory' && reportData.items) {
      csvContent += 'Product,SKU,Current Stock,Threshold,Reorder Qty\n';
      reportData.items.forEach((i) => {
        csvContent += `"${i.product_name}",${i.product_sku},${i.current_stock},${i.low_stock_threshold},${i.reorder_quantity}\n`;
      });
    }

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `AAA_Growers_${reportType}_report.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Executive Analytics & Reports</h1>
          <p className="text-sm text-slate-500 mt-1">Export-grade sales analytics, order throughput, inventory valuations and training metrics.</p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={exportCSV}
            className="flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold shadow-sm transition"
          >
            <Download className="w-4 h-4" /> Export CSV Data
          </button>
          <button
            onClick={() => window.print()}
            className="p-2 bg-white border border-slate-200 hover:bg-slate-50 rounded-xl text-slate-600 transition"
            title="Print Report"
          >
            <Printer className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Module Selector & Filter Ribbon */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm space-y-4">
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-100 pb-3">
          <div className="flex flex-wrap gap-2">
            <button
              onClick={() => setReportType('sales')}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition flex items-center gap-1.5 ${
                reportType === 'sales' ? 'bg-emerald-600 text-white' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
              }`}
            >
              <DollarSign className="w-3.5 h-3.5" /> Sales Performance
            </button>
            <button
              onClick={() => setReportType('orders')}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition flex items-center gap-1.5 ${
                reportType === 'orders' ? 'bg-emerald-600 text-white' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
              }`}
            >
              <ShoppingCart className="w-3.5 h-3.5" /> Orders Throughput
            </button>
            <button
              onClick={() => setReportType('inventory')}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition flex items-center gap-1.5 ${
                reportType === 'inventory' ? 'bg-emerald-600 text-white' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
              }`}
            >
              <Boxes className="w-3.5 h-3.5" /> Inventory & Valuation
            </button>
            <button
              onClick={() => setReportType('training')}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition flex items-center gap-1.5 ${
                reportType === 'training' ? 'bg-emerald-600 text-white' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
              }`}
            >
              <GraduationCap className="w-3.5 h-3.5" /> Training & Certifications
            </button>
          </div>

          {reportType === 'sales' && (
            <div className="flex bg-slate-100 p-1 rounded-xl text-xs font-semibold">
              {['daily', 'weekly', 'monthly', 'annual'].map((p) => (
                <button
                  key={p}
                  onClick={() => setPeriod(p)}
                  className={`px-3 py-1 rounded-lg uppercase tracking-wider text-[10px] font-bold transition ${
                    period === p ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-500 hover:text-slate-900'
                  }`}
                >
                  {p}
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Date Range Picker */}
        <div className="flex flex-wrap items-center gap-3 text-xs">
          <span className="font-bold text-slate-500 uppercase tracking-wider flex items-center gap-1">
            <Calendar className="w-3.5 h-3.5 text-slate-400" /> Custom Range:
          </span>
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-xl"
          />
          <span className="text-slate-400">to</span>
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-xl"
          />
          <button
            onClick={fetchReport}
            className="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 text-white rounded-xl font-bold transition"
          >
            Apply Filter
          </button>
        </div>
      </div>

      {/* Report Content Panels */}
      {loading ? (
        <div className="py-16 text-center text-slate-400">Generating analytics report...</div>
      ) : reportData && (
        <div className="space-y-6">
          {/* Sales Report View */}
          {reportType === 'sales' && (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
                <StatCard
                  title="Total Revenue (KES)"
                  value={`KES ${reportData.total_revenue.toLocaleString()}`}
                  icon={DollarSign}
                  color="emerald"
                />
                <StatCard
                  title="Completed Payments"
                  value={reportData.total_transactions}
                  icon={ShoppingCart}
                  color="blue"
                />
                <StatCard
                  title="Average Order Value"
                  value={`KES ${Math.round(reportData.average_order_value).toLocaleString()}`}
                  icon={BarChart3}
                  color="purple"
                />
              </div>

              <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
                <h3 className="font-bold text-slate-900 mb-4">Sales Trend Visualizer</h3>
                <div className="h-72 w-full">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={reportData.data || []}>
                      <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                      <XAxis dataKey="date" stroke="#94a3b8" fontSize={11} />
                      <YAxis stroke="#94a3b8" fontSize={11} />
                      <Tooltip
                        formatter={(val) => [`KES ${Number(val).toLocaleString()}`, 'Revenue']}
                        contentStyle={{ backgroundColor: '#0f172a', borderRadius: '12px', color: '#fff' }}
                      />
                      <Bar dataKey="revenue" fill="#16a34a" radius={[6, 6, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </div>
            </>
          )}

          {/* Orders Report View */}
          {reportType === 'orders' && (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
                <StatCard title="Total Orders" value={reportData.total_orders} icon={ShoppingCart} color="blue" />
                <StatCard title="Total Value" value={`KES ${reportData.total_value.toLocaleString()}`} icon={DollarSign} color="emerald" />
                <StatCard title="Status Breakdowns" value={Object.keys(reportData.status_distribution || {}).length} icon={Filter} color="amber" />
              </div>

              <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
                <h3 className="font-bold text-slate-900 mb-4">Order Status Distribution</h3>
                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-3">
                  {Object.entries(reportData.status_distribution || {}).map(([st, cnt]) => (
                    <div key={st} className="p-3 bg-slate-50 rounded-xl border border-slate-200 text-center">
                      <p className="text-[10px] uppercase font-bold text-slate-500">{st}</p>
                      <p className="text-xl font-extrabold text-slate-900 mt-1">{cnt}</p>
                    </div>
                  ))}
                </div>
              </div>
            </>
          )}

          {/* Inventory Report View */}
          {reportType === 'inventory' && (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
                <StatCard title="Stock Valuation" value={`KES ${reportData.total_stock_valuation.toLocaleString()}`} icon={DollarSign} color="emerald" />
                <StatCard title="Catalog SKUs" value={reportData.total_inventory_items} icon={Boxes} color="blue" />
                <StatCard title="Low Stock Warnings" value={reportData.low_stock_count} icon={Filter} color="rose" />
              </div>

              <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden p-6">
                <h3 className="font-bold text-slate-900 mb-4">Warehouse Stock Valuation Table</h3>
                <div className="overflow-x-auto">
                  <table className="w-full text-left text-xs">
                    <thead className="bg-slate-50 text-slate-500 uppercase font-semibold">
                      <tr>
                        <th className="py-2 px-3">Product Name</th>
                        <th className="py-2 px-3">SKU</th>
                        <th className="py-2 px-3 text-center">Current Units</th>
                        <th className="py-2 px-3 text-center">Low Threshold</th>
                        <th className="py-2 px-3 text-right">Status</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {reportData.items?.map((item) => (
                        <tr key={item.id}>
                          <td className="py-3 px-3 font-bold text-slate-900">{item.product_name}</td>
                          <td className="py-3 px-3 font-mono text-slate-400">{item.product_sku}</td>
                          <td className="py-3 px-3 text-center font-mono font-bold text-slate-800">{item.current_stock}</td>
                          <td className="py-3 px-3 text-center font-mono text-slate-500">{item.low_stock_threshold}</td>
                          <td className="py-3 px-3 text-right font-semibold">
                            <span className={item.is_low_stock ? 'text-rose-600' : 'text-emerald-600'}>
                              {item.is_low_stock ? 'Needs Restock' : 'Adequate'}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </>
          )}

          {/* Training Report View */}
          {reportType === 'training' && (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-4 gap-5">
                <StatCard title="Total Programs" value={reportData.total_sessions} icon={GraduationCap} color="blue" />
                <StatCard title="Upcoming Classes" value={reportData.upcoming_sessions} icon={Calendar} color="emerald" />
                <StatCard title="Farmer Enrollments" value={reportData.total_bookings} icon={ShoppingCart} color="purple" />
                <StatCard title="Certificates Issued" value={reportData.total_certifications_issued} icon={BarChart3} color="amber" />
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
};

export default ReportsPage;
