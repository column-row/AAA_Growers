import React from 'react';
import { FileText, Printer, X, Sprout, Truck, CreditCard } from 'lucide-react';
import StatusBadge from './StatusBadge';

const InvoiceModal = ({ isOpen, onClose, order }) => {
  if (!isOpen || !order) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="relative bg-white rounded-3xl shadow-2xl border border-slate-200 max-w-3xl w-full p-8 overflow-hidden">
        {/* Actions header */}
        <div className="flex justify-between items-center pb-4 border-b border-slate-200 mb-6 print:hidden">
          <div className="flex items-center gap-2">
            <FileText className="w-5 h-5 text-emerald-600" />
            <h3 className="font-bold text-lg text-slate-900">Order Invoice #{order.order_number}</h3>
          </div>
          <div className="flex items-center gap-3">
            <button
              onClick={() => window.print()}
              className="flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-sm font-semibold transition"
            >
              <Printer className="w-4 h-4" />
              Print Receipt
            </button>
            <button onClick={onClose} className="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition">
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Invoice Printable Area */}
        <div className="space-y-6">
          {/* Company & Order Info */}
          <div className="flex justify-between items-start pb-6 border-b border-slate-200">
            <div>
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-lg bg-emerald-600 flex items-center justify-center text-white">
                  <Sprout className="w-5 h-5" />
                </div>
                <span className="text-xl font-bold text-slate-900">AAA GROWERS</span>
              </div>
              <p className="text-xs text-slate-500 mt-1">Old Airport North Road, Nairobi, Kenya</p>
              <p className="text-xs text-slate-500">info@aaagrowers.co.ke | +254 700 000 001</p>
            </div>
            <div className="text-right">
              <h2 className="text-lg font-bold text-slate-900 font-mono">{order.order_number}</h2>
              <p className="text-xs text-slate-500 mt-1">Placed: {new Date(order.placed_at).toLocaleDateString()}</p>
              <div className="flex justify-end gap-2 mt-2">
                <StatusBadge status={order.status} />
                <StatusBadge status={order.payment_status} />
              </div>
            </div>
          </div>

          {/* Customer & Delivery details */}
          <div className="grid grid-cols-2 gap-4 bg-slate-50 p-4 rounded-xl text-sm">
            <div>
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Customer Details</p>
              <p className="font-bold text-slate-900 mt-1">{order.customer_name}</p>
              <p className="text-slate-600 text-xs">{order.customer_email}</p>
              <p className="text-slate-600 text-xs">{order.delivery_phone}</p>
            </div>
            <div>
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Delivery Destination</p>
              <p className="text-slate-800 text-xs mt-1">{order.delivery_address}</p>
              <p className="text-slate-800 text-xs font-semibold">{order.delivery_city}</p>
              {order.notes && <p className="text-xs text-amber-700 italic mt-1">Notes: {order.notes}</p>}
            </div>
          </div>

          {/* Itemized Table */}
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b border-slate-200 text-xs uppercase text-slate-500 font-semibold bg-slate-50/70">
                <th className="py-2.5 px-3">Item Description</th>
                <th className="py-2.5 px-3 text-right">Unit Price</th>
                <th className="py-2.5 px-3 text-center">Qty</th>
                <th className="py-2.5 px-3 text-right">Subtotal</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {order.items?.map((item) => (
                <tr key={item.id}>
                  <td className="py-3 px-3">
                    <p className="font-semibold text-slate-900">{item.product_name}</p>
                    <p className="text-xs text-slate-400 font-mono">SKU: {item.product_sku || item.product_id}</p>
                  </td>
                  <td className="py-3 px-3 text-right font-mono text-slate-700">KES {item.unit_price.toFixed(2)}</td>
                  <td className="py-3 px-3 text-center font-medium text-slate-900">{item.quantity}</td>
                  <td className="py-3 px-3 text-right font-mono font-semibold text-slate-900">KES {item.subtotal.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Totals Calculation */}
          <div className="flex justify-end pt-4 border-t border-slate-200">
            <div className="w-64 space-y-2 text-sm">
              <div className="flex justify-between text-slate-600 text-xs">
                <span>Subtotal:</span>
                <span className="font-mono">KES {order.total_amount.toFixed(2)}</span>
              </div>
              {order.discount_amount > 0 && (
                <div className="flex justify-between text-emerald-600 text-xs">
                  <span>Discount:</span>
                  <span className="font-mono">- KES {order.discount_amount.toFixed(2)}</span>
                </div>
              )}
              <div className="flex justify-between text-slate-600 text-xs">
                <span>Shipping Fee:</span>
                <span className="font-mono">KES {order.shipping_fee.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-base font-bold text-slate-900 pt-2 border-t border-slate-200">
                <span>Net Total:</span>
                <span className="text-emerald-700 font-mono">KES {order.net_amount.toFixed(2)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default InvoiceModal;
