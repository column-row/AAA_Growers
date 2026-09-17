import React, { useState, useEffect } from 'react';
import { MessageSquare, Star, Mail, Phone, CheckCircle2, Clock } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import api from '../services/api';

const FeedbackPage = () => {
  const [activeTab, setActiveTab] = useState('feedback'); // feedback | contacts
  const [feedbacks, setFeedbacks] = useState([]);
  const [contacts, setContacts] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [fbRes, ctRes] = await Promise.all([
        api.get('/feedback'),
        api.get('/contacts')
      ]);
      if (fbRes.data.success) setFeedbacks(fbRes.data.data.items);
      if (ctRes.data.success) setContacts(ctRes.data.data.items);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleUpdateContactStatus = async (contactId, status) => {
    try {
      await api.put(`/contacts/${contactId}/status`, { status });
      fetchData();
    } catch (err) {
      alert('Failed to update contact status');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Customer Feedback & Inquiries</h1>
          <p className="text-sm text-slate-500 mt-1">Review ratings, service testimonials and wholesale contact requests.</p>
        </div>

        {/* Tab Buttons */}
        <div className="flex bg-slate-100 p-1 rounded-xl">
          <button
            onClick={() => setActiveTab('feedback')}
            className={`px-4 py-1.5 rounded-lg text-xs font-bold transition ${
              activeTab === 'feedback' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Ratings & Feedback ({feedbacks.length})
          </button>
          <button
            onClick={() => setActiveTab('contacts')}
            className={`px-4 py-1.5 rounded-lg text-xs font-bold transition ${
              activeTab === 'contacts' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Contact Inquiries ({contacts.length})
          </button>
        </div>
      </div>

      {activeTab === 'feedback' ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {loading ? (
            <div className="col-span-full py-12 text-center text-slate-400">Loading reviews...</div>
          ) : feedbacks.length === 0 ? (
            <div className="col-span-full py-12 text-center text-slate-400">No feedback submitted yet.</div>
          ) : (
            feedbacks.map((f) => (
              <div key={f.id} className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col justify-between hover:shadow-md transition">
                <div>
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex gap-0.5">
                      {[1, 2, 3, 4, 5].map((star) => (
                        <Star
                          key={star}
                          className={`w-4 h-4 ${star <= f.rating ? 'fill-amber-400 text-amber-400' : 'text-slate-200'}`}
                        />
                      ))}
                    </div>
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-slate-100 text-slate-700">
                      {f.category}
                    </span>
                  </div>

                  <p className="text-xs text-slate-700 italic leading-relaxed">"{f.comment}"</p>
                </div>

                <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
                  <div>
                    <p className="font-bold text-slate-900">{f.user_name}</p>
                    <p className="text-[11px] text-slate-400">{new Date(f.created_at).toLocaleDateString()}</p>
                  </div>
                  {f.is_reviewed && (
                    <span className="text-emerald-700 font-semibold text-[11px] flex items-center gap-1">
                      <CheckCircle2 className="w-3.5 h-3.5" /> Reviewed
                    </span>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      ) : (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50/70 border-b border-slate-100 text-xs uppercase text-slate-500 font-semibold">
                <tr>
                  <th className="py-3.5 px-6">Sender</th>
                  <th className="py-3.5 px-6">Subject</th>
                  <th className="py-3.5 px-6">Message</th>
                  <th className="py-3.5 px-6">Date</th>
                  <th className="py-3.5 px-6">Status</th>
                  <th className="py-3.5 px-6 text-right">Resolve</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {loading ? (
                  <tr>
                    <td colSpan="6" className="py-8 text-center text-slate-400">Loading messages...</td>
                  </tr>
                ) : contacts.length === 0 ? (
                  <tr>
                    <td colSpan="6" className="py-8 text-center text-slate-400">No contact inquiries.</td>
                  </tr>
                ) : (
                  contacts.map((c) => (
                    <tr key={c.id} className="hover:bg-slate-50/80 transition">
                      <td className="py-4 px-6">
                        <p className="font-bold text-slate-900">{c.full_name}</p>
                        <p className="text-xs text-slate-400 font-mono">{c.email}</p>
                        {c.phone && <p className="text-xs text-slate-400 font-mono">{c.phone}</p>}
                      </td>
                      <td className="py-4 px-6 font-semibold text-slate-900 text-xs">{c.subject}</td>
                      <td className="py-4 px-6 text-xs text-slate-600 max-w-[280px] leading-relaxed">
                        {c.message}
                      </td>
                      <td className="py-4 px-6 text-xs text-slate-400">
                        {new Date(c.created_at).toLocaleDateString()}
                      </td>
                      <td className="py-4 px-6">
                        <StatusBadge status={c.status} />
                      </td>
                      <td className="py-4 px-6 text-right space-x-1">
                        {c.status !== 'RESOLVED' && (
                          <button
                            onClick={() => handleUpdateContactStatus(c.id, 'RESOLVED')}
                            className="px-2.5 py-1 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 rounded-lg text-xs font-bold transition inline-flex items-center gap-1"
                          >
                            <CheckCircle2 className="w-3.5 h-3.5" /> Resolve
                          </button>
                        )}
                        {c.status === 'NEW' && (
                          <button
                            onClick={() => handleUpdateContactStatus(c.id, 'IN_PROGRESS')}
                            className="px-2.5 py-1 bg-blue-50 hover:bg-blue-100 text-blue-700 rounded-lg text-xs font-bold transition inline-flex items-center gap-1"
                          >
                            <Clock className="w-3.5 h-3.5" /> In Progress
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
      )}
    </div>
  );
};

export default FeedbackPage;
