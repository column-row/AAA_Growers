import React from 'react';
import { Award, CheckCircle2, Download, Printer, ShieldCheck, X } from 'lucide-react';

const CertificateModal = ({ isOpen, onClose, certificate }) => {
  if (!isOpen || !certificate) return null;

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/70 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="relative bg-white rounded-3xl shadow-2xl border border-slate-200 max-w-4xl w-full p-8 overflow-hidden">
        {/* Controls header */}
        <div className="flex justify-between items-center pb-4 border-b border-slate-200 mb-6 print:hidden">
          <div className="flex items-center gap-2">
            <Award className="w-6 h-6 text-amber-600" />
            <h3 className="font-bold text-lg text-slate-900">Certificate of Completion</h3>
          </div>
          <div className="flex items-center gap-3">
            <button
              onClick={handlePrint}
              className="flex items-center gap-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-sm font-semibold shadow-sm transition"
            >
              <Printer className="w-4 h-4" />
              Print Certificate
            </button>
            <button
              onClick={onClose}
              className="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Certificate Container */}
        <div
          id="printable-certificate"
          className="relative bg-gradient-to-br from-amber-50/40 via-white to-emerald-50/30 p-10 rounded-2xl border-8 border-double border-amber-600/40 shadow-inner text-center"
        >
          {/* Watermark Logo */}
          <div className="absolute inset-0 flex items-center justify-center opacity-5 pointer-events-none">
            <Award className="w-96 h-96 text-emerald-900" />
          </div>

          <div className="relative z-10">
            {/* Header Badge */}
            <div className="flex justify-center mb-3">
              <div className="inline-flex items-center gap-2 bg-emerald-800 text-amber-300 px-6 py-2 rounded-full font-bold tracking-widest text-xs uppercase shadow-md border border-amber-400/40">
                <ShieldCheck className="w-4 h-4 text-amber-300" />
                AAA Growers Academy of Agricultural Excellence
              </div>
            </div>

            <h1 className="text-3xl sm:text-4xl font-serif font-black text-slate-900 tracking-wide mt-4">
              CERTIFICATE OF COMPETENCY
            </h1>
            <p className="text-xs uppercase tracking-widest text-emerald-800 font-semibold mt-1">
              Export Horticulture & Agricultural Standards
            </p>

            <div className="w-32 h-1 bg-gradient-to-r from-amber-500 via-emerald-600 to-amber-500 mx-auto my-6 rounded-full" />

            <p className="text-slate-600 italic text-sm">This is to officially certify that</p>
            <h2 className="text-2xl sm:text-3xl font-bold text-slate-900 font-serif my-3 text-emerald-900 underline decoration-amber-500/60 underline-offset-8">
              {certificate.farmer_name}
            </h2>
            <p className="text-xs text-slate-500 font-medium">
              Farm: <span className="font-semibold text-slate-700">{certificate.farm_name || 'Naivasha Farm Cluster'}</span>
              {certificate.farmer_national_id && ` • National ID: ${certificate.farmer_national_id}`}
            </p>

            <p className="text-slate-600 text-sm max-w-xl mx-auto mt-6 leading-relaxed">
              has successfully completed comprehensive practical field training and evaluation in
            </p>

            <div className="bg-emerald-900/5 border border-emerald-800/20 rounded-xl p-4 my-4 max-w-xl mx-auto">
              <h3 className="font-bold text-lg text-emerald-900">{certificate.title || certificate.training_title}</h3>
            </div>

            {/* Footer with Signatures & QR Hash */}
            <div className="grid grid-cols-3 gap-6 pt-10 mt-6 border-t border-slate-300/70 text-left items-end">
              <div>
                <p className="text-xs font-semibold text-slate-800 border-b border-slate-400 pb-1">
                  {certificate.trainer_name || 'Dr. Samuel Kipchoge'}
                </p>
                <p className="text-[10px] text-slate-500 uppercase mt-0.5">Lead Agronomist & Assessor</p>
                <p className="text-[10px] text-slate-500">Date Issued: {certificate.issue_date}</p>
              </div>

              {/* Official Stamp */}
              <div className="text-center flex flex-col items-center">
                <div className="w-20 h-20 rounded-full border-4 border-amber-600/60 bg-amber-500/10 flex flex-col items-center justify-center text-amber-800 p-1 rotate-[-8deg] shadow-sm">
                  <CheckCircle2 className="w-6 h-6 text-emerald-700" />
                  <span className="text-[8px] font-black uppercase tracking-tighter">AAA GROWERS</span>
                  <span className="text-[7px] font-semibold text-emerald-900">OFFICIAL SEAL</span>
                </div>
              </div>

              <div className="text-right">
                <p className="text-xs font-semibold text-slate-800 border-b border-slate-400 pb-1 font-mono">
                  {certificate.certificate_number}
                </p>
                <p className="text-[10px] text-slate-500 uppercase mt-0.5">Certificate ID</p>
                <p className="text-[9px] text-slate-400 font-mono truncate max-w-[200px]" title={certificate.verification_hash}>
                  Hash: {certificate.verification_hash?.slice(0, 16)}...
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CertificateModal;
