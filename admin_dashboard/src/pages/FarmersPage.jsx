import React, { useState, useEffect } from 'react';
import { Sprout, Search, Award, GraduationCap, MapPin, Calendar } from 'lucide-react';
import api from '../services/api';

const FarmersPage = () => {
  const [farmers, setFarmers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFarmers = async () => {
      try {
        setLoading(true);
        const res = await api.get('/farmers');
        if (res.data.success) {
          setFarmers(res.data.data.items);
        }
      } catch (err) {
        console.error('Failed to load farmers', err);
      } finally {
        setLoading(false);
      }
    };
    fetchFarmers();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Farmers & Outgrowers Hub</h1>
          <p className="text-sm text-slate-500 mt-1">Directory of registered agricultural outgrowers, acreage & certifications.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {loading ? (
          <div className="col-span-full py-12 text-center text-slate-400">Loading farmers...</div>
        ) : farmers.length === 0 ? (
          <div className="col-span-full py-12 text-center text-slate-400">No registered farmers found.</div>
        ) : (
          farmers.map((f) => (
            <div key={f.id} className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition">
              <div className="flex items-center justify-between mb-4">
                <div className="w-12 h-12 rounded-2xl bg-emerald-100 text-emerald-800 font-bold flex items-center justify-center text-lg border border-emerald-200">
                  <Sprout className="w-6 h-6" />
                </div>
                <span className="text-xs font-bold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-lg border border-emerald-200">
                  {f.farm_size_acres} Acres
                </span>
              </div>

              <h3 className="font-bold text-slate-900 text-base">{f.full_name}</h3>
              <p className="text-xs font-semibold text-emerald-800 mt-0.5">{f.farm_name}</p>

              <div className="space-y-2 mt-4 text-xs text-slate-600 border-t border-slate-100 pt-3">
                <div className="flex items-center gap-2">
                  <MapPin className="w-3.5 h-3.5 text-slate-400" />
                  <span>{f.farm_location}</span>
                </div>
                <p className="text-slate-500">
                  <span className="font-semibold text-slate-700">Crops:</span> {f.crops_grown || 'Fresh Produce'}
                </p>
                <p className="text-slate-500">
                  <span className="font-semibold text-slate-700">Experience:</span> {f.farming_experience_years} Years
                </p>
                {f.national_id && (
                  <p className="text-slate-400 font-mono text-[11px]">National ID: {f.national_id}</p>
                )}
              </div>

              <div className="grid grid-cols-2 gap-2 mt-4 pt-3 border-t border-slate-100 text-center">
                <div className="bg-slate-50 p-2 rounded-xl">
                  <p className="text-[10px] text-slate-400 uppercase font-semibold">Trainings</p>
                  <p className="text-sm font-bold text-slate-800 mt-0.5 flex items-center justify-center gap-1">
                    <GraduationCap className="w-3.5 h-3.5 text-blue-600" /> {f.total_trainings_attended}
                  </p>
                </div>
                <div className="bg-slate-50 p-2 rounded-xl">
                  <p className="text-[10px] text-slate-400 uppercase font-semibold">Certificates</p>
                  <p className="text-sm font-bold text-slate-800 mt-0.5 flex items-center justify-center gap-1">
                    <Award className="w-3.5 h-3.5 text-amber-600" /> {f.certifications_count}
                  </p>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default FarmersPage;
