import React, { useState, useEffect } from 'react';
import { GraduationCap, Plus, Calendar, MapPin, Users, Award, CheckCircle2, Clock } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import Modal from '../components/Modal';
import api from '../services/api';

const TrainingsPage = () => {
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [selectedSessionBookings, setSelectedSessionBookings] = useState(null);
  const [bookingsList, setBookingsList] = useState([]);
  const [formData, setFormData] = useState({
    trainer_id: 1,
    title: '',
    description: '',
    category: 'Export Standards',
    training_date: '',
    start_time: '09:00',
    end_time: '13:00',
    location: 'AAA Training Academy, Naivasha Center',
    capacity: '30'
  });

  const fetchSessions = async () => {
    try {
      setLoading(true);
      const res = await api.get('/trainings');
      if (res.data.success) {
        setSessions(res.data.data.items);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSessions();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await api.post('/trainings', formData);
      setIsCreateModalOpen(false);
      fetchSessions();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to create training session');
    }
  };

  const openBookings = async (session) => {
    try {
      setSelectedSessionBookings(session);
      const res = await api.get(`/trainings/${session.id}/bookings`);
      if (res.data.success) {
        setBookingsList(res.data.data);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const markCompleteAndCertify = async (bookingId) => {
    try {
      const res = await api.post(`/trainings/bookings/${bookingId}/complete`);
      alert('Farmer certified successfully!');
      if (selectedSessionBookings) {
        openBookings(selectedSessionBookings);
      }
      fetchSessions();
    } catch (err) {
      alert(err.response?.data?.message || 'Action failed');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Farmer Training Academy</h1>
          <p className="text-sm text-slate-500 mt-1">Curate agronomic sessions, track farmer attendance and issue certificates.</p>
        </div>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="flex items-center gap-2 px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-sm font-bold shadow-md shadow-emerald-700/20 transition"
        >
          <Plus className="w-4 h-4" /> Schedule Training Session
        </button>
      </div>

      {/* Sessions Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {loading ? (
          <div className="col-span-full py-12 text-center text-slate-400">Loading training programs...</div>
        ) : (
          sessions.map((s) => (
            <div key={s.id} className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 flex flex-col justify-between hover:shadow-md transition">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <span className="text-[10px] font-bold px-2.5 py-1 rounded-md bg-emerald-50 text-emerald-800 border border-emerald-200 uppercase">
                    {s.category}
                  </span>
                  <StatusBadge status={s.status} />
                </div>

                <h3 className="font-bold text-slate-900 text-base leading-snug">{s.title}</h3>
                <p className="text-xs text-slate-500 mt-2 line-clamp-3 leading-relaxed">{s.description}</p>

                <div className="space-y-2 mt-4 pt-3 border-t border-slate-100 text-xs text-slate-600">
                  <div className="flex items-center gap-2">
                    <Calendar className="w-3.5 h-3.5 text-slate-400" />
                    <span>{s.training_date} • {s.start_time} - {s.end_time}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <MapPin className="w-3.5 h-3.5 text-slate-400" />
                    <span>{s.location}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <GraduationCap className="w-3.5 h-3.5 text-slate-400" />
                    <span className="font-semibold">{s.trainer_name}</span>
                  </div>
                </div>

                {/* Capacity Progress Bar */}
                <div className="mt-4 pt-3 border-t border-slate-100">
                  <div className="flex justify-between text-xs mb-1">
                    <span className="text-slate-500">Booked Seats</span>
                    <span className="font-bold text-slate-800">{s.booked_count} / {s.capacity}</span>
                  </div>
                  <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                    <div
                      className={`h-full rounded-full ${s.is_full ? 'bg-rose-500' : 'bg-emerald-600'}`}
                      style={{ width: `${Math.min(100, (s.booked_count / s.capacity) * 100)}%` }}
                    />
                  </div>
                </div>
              </div>

              <div className="mt-6 pt-3 border-t border-slate-100">
                <button
                  onClick={() => openBookings(s)}
                  className="w-full py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition flex items-center justify-center gap-1.5"
                >
                  <Users className="w-3.5 h-3.5" /> View Farmer Bookings ({s.booked_count})
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Schedule Session Modal */}
      <Modal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        title="Schedule Agronomic Training Session"
        size="lg"
      >
        <form onSubmit={handleCreate} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Training Title</label>
            <input
              type="text"
              required
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              placeholder="e.g. GlobalG.A.P. Export Quality Compliance Masterclass"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Category</label>
              <input
                type="text"
                required
                value={formData.category}
                onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
                placeholder="Export Standards, IPM, Irrigation"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Max Capacity (Seats)</label>
              <input
                type="number"
                required
                value={formData.capacity}
                onChange={(e) => setFormData({ ...formData, capacity: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm font-mono"
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Training Date</label>
              <input
                type="date"
                required
                value={formData.training_date}
                onChange={(e) => setFormData({ ...formData, training_date: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Start Time</label>
              <input
                type="time"
                required
                value={formData.start_time}
                onChange={(e) => setFormData({ ...formData, start_time: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase mb-1">End Time</label>
              <input
                type="time"
                required
                value={formData.end_time}
                onChange={(e) => setFormData({ ...formData, end_time: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Location / Training Center</label>
            <input
              type="text"
              required
              value={formData.location}
              onChange={(e) => setFormData({ ...formData, location: e.target.value })}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              placeholder="e.g. AAA Training Academy, Naivasha Center"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Description & Learning Outcomes</label>
            <textarea
              rows={3}
              required
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              placeholder="Curriculum summary, materials provided..."
            />
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setIsCreateModalOpen(false)}
              className="px-4 py-2 bg-slate-100 text-slate-700 rounded-xl text-sm font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2 bg-emerald-600 text-white rounded-xl text-sm font-bold shadow-md shadow-emerald-700/20"
            >
              Publish Training
            </button>
          </div>
        </form>
      </Modal>

      {/* Bookings Modal */}
      <Modal
        isOpen={Boolean(selectedSessionBookings)}
        onClose={() => setSelectedSessionBookings(null)}
        title={`Attendees: ${selectedSessionBookings?.title}`}
        size="lg"
      >
        <div className="space-y-4">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 font-semibold uppercase">
                <tr>
                  <th className="py-2.5 px-3">Farmer</th>
                  <th className="py-2.5 px-3">Farm</th>
                  <th className="py-2.5 px-3">Contact</th>
                  <th className="py-2.5 px-3">Booking Status</th>
                  <th className="py-2.5 px-3 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {bookingsList.length === 0 ? (
                  <tr>
                    <td colSpan="5" className="py-6 text-center text-slate-400">No farmers registered yet.</td>
                  </tr>
                ) : (
                  bookingsList.map((b) => (
                    <tr key={b.id}>
                      <td className="py-3 px-3 font-bold text-slate-900">{b.farmer_name}</td>
                      <td className="py-3 px-3 text-slate-600">{b.farm_name}</td>
                      <td className="py-3 px-3 text-slate-500 font-mono">{b.farmer_phone || '—'}</td>
                      <td className="py-3 px-3">
                        <StatusBadge status={b.status} />
                      </td>
                      <td className="py-3 px-3 text-right">
                        {b.status !== 'COMPLETED' ? (
                          <button
                            onClick={() => markCompleteAndCertify(b.id)}
                            className="px-3 py-1 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg font-bold text-xs transition inline-flex items-center gap-1 shadow-xs"
                          >
                            <Award className="w-3.5 h-3.5" /> Mark Passed & Certify
                          </button>
                        ) : (
                          <span className="text-emerald-700 font-semibold flex items-center justify-end gap-1">
                            <CheckCircle2 className="w-3.5 h-3.5" /> Certified
                          </span>
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default TrainingsPage;
