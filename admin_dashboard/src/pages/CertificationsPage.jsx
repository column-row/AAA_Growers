import React, { useState, useEffect } from 'react';
import { Award, Search, Eye, ShieldCheck, Printer, CheckCircle2 } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import CertificateModal from '../components/CertificateModal';
import api from '../services/api';

const CertificationsPage = () => {
  const [certs, setCerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCert, setSelectedCert] = useState(null);

  const fetchCerts = async () => {
    try {
      setLoading(true);
      const res = await api.get('/certifications');
      if (res.data.success) {
        setCerts(res.data.data.items);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCerts();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Farmer Certifications Registry</h1>
          <p className="text-sm text-slate-500 mt-1">Official verified credentials issued to outgrowers upon completing masterclasses.</p>
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50/70 border-b border-slate-100 text-xs uppercase text-slate-500 font-semibold">
              <tr>
                <th className="py-3.5 px-6">Certificate #</th>
                <th className="py-3.5 px-6">Certified Farmer</th>
                <th className="py-3.5 px-6">Competency Title</th>
                <th className="py-3.5 px-6">Issue Date</th>
                <th className="py-3.5 px-6">Verification Hash</th>
                <th className="py-3.5 px-6">Status</th>
                <th className="py-3.5 px-6 text-right">Certificate View</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 font-mono">
              {loading ? (
                <tr>
                  <td colSpan="7" className="py-8 text-center text-slate-400 font-sans">Loading certificates...</td>
                </tr>
              ) : certs.length === 0 ? (
                <tr>
                  <td colSpan="7" className="py-8 text-center text-slate-400 font-sans">No certifications issued yet.</td>
                </tr>
              ) : (
                certs.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-50/80 transition">
                    <td className="py-4 px-6 font-bold text-slate-900">{c.certificate_number}</td>
                    <td className="py-4 px-6 font-sans">
                      <p className="font-bold text-slate-900">{c.farmer_name}</p>
                      <p className="text-xs text-slate-500">{c.farm_name}</p>
                    </td>
                    <td className="py-4 px-6 font-sans text-xs font-semibold text-slate-800">
                      {c.title}
                    </td>
                    <td className="py-4 px-6 font-sans text-xs text-slate-500">{c.issue_date}</td>
                    <td className="py-4 px-6 text-xs text-slate-400 font-mono truncate max-w-[120px]" title={c.verification_hash}>
                      {c.verification_hash.slice(0, 12)}...
                    </td>
                    <td className="py-4 px-6 font-sans">
                      <StatusBadge status={c.status} />
                    </td>
                    <td className="py-4 px-6 text-right font-sans">
                      <button
                        onClick={() => setSelectedCert(c)}
                        className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition inline-flex items-center gap-1 shadow-xs"
                      >
                        <Eye className="w-3.5 h-3.5" /> View / Print
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Printable Certificate Modal */}
      <CertificateModal
        isOpen={Boolean(selectedCert)}
        onClose={() => setSelectedCert(null)}
        certificate={selectedCert}
      />
    </div>
  );
};

export default CertificationsPage;
