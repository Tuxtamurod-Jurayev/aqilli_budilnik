import React, { useState } from 'react';
import { PhoneCall, Search, Filter, PhoneIncoming, PhoneOutgoing, PhoneMissed } from 'lucide-react';
import { CallRecord, Device } from '../types';

interface CallsPageProps {
  calls: CallRecord[];
  devices: Device[];
}

export const CallsPage: React.FC<CallsPageProps> = ({ calls, devices }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedDevice, setSelectedDevice] = useState<string>('all');
  const [typeFilter, setTypeFilter] = useState<'ALL' | 'INCOMING' | 'OUTGOING' | 'MISSED'>('ALL');

  const filtered = calls.filter((c) => {
    const matchesSearch =
      c.number.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (c.name && c.name.toLowerCase().includes(searchTerm.toLowerCase()));
    const matchesDevice = selectedDevice === 'all' || c.device_id === selectedDevice;
    const matchesType = typeFilter === 'ALL' || c.type === typeFilter;
    return matchesSearch && matchesDevice && matchesType;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-white font-['Outfit']">
            Qo'ng'iroqlar Jurnali Explorer
          </h2>
          <p className="text-sm text-slate-400">
            Barcha ulangan Android qurilmalarning kiruvchi, chiquvchi va o'tkazib yuborilgan qo'ng'iroqlari
          </p>
        </div>

        {/* Filter controls */}
        <div className="flex flex-wrap items-center gap-3">
          <div className="relative">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Raqam yoki ism qidirish..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-9 pr-4 py-2 bg-slate-900 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500 w-52"
            />
          </div>

          <select
            value={selectedDevice}
            onChange={(e) => setSelectedDevice(e.target.value)}
            className="px-3 py-2 bg-slate-900 border border-slate-800 rounded-xl text-xs text-slate-300 focus:outline-none"
          >
            <option value="all">Barcha Qurilmalar</option>
            {devices.map(d => (
              <option key={d.device_id} value={d.device_id}>{d.device_name} ({d.device_id})</option>
            ))}
          </select>

          <div className="flex items-center bg-slate-900 border border-slate-800 rounded-xl p-1 text-xs">
            {(['ALL', 'INCOMING', 'OUTGOING', 'MISSED'] as const).map(t => (
              <button
                key={t}
                onClick={() => setTypeFilter(t)}
                className={`px-2.5 py-1 rounded-lg font-medium transition ${
                  typeFilter === t ? 'bg-purple-600 text-white' : 'text-slate-400 hover:text-white'
                }`}
              >
                {t === 'ALL' ? 'Barchasi' : t === 'INCOMING' ? 'Kiruvchi' : t === 'OUTGOING' ? 'Chiquvchi' : 'O\'tkazilgan'}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Calls Table */}
      <div className="rounded-2xl bg-slate-900/90 border border-slate-800 overflow-hidden">
        <table className="w-full text-left text-xs">
          <thead>
            <tr className="border-b border-slate-800 text-slate-400 uppercase tracking-wider text-[11px] bg-slate-950/40">
              <th className="p-4">Qurilma ID</th>
              <th className="p-4">Raqam / Kontakt</th>
              <th className="p-4">Turi</th>
              <th className="p-4">Davomiyligi</th>
              <th className="p-4">Sana va Vaqt</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60">
            {filtered.length === 0 ? (
              <tr>
                <td colSpan={5} className="p-8 text-center text-slate-400">
                  Qo'ng'iroqlar jurnali topilmadi.
                </td>
              </tr>
            ) : (
              filtered.map((c) => (
                <tr key={c.id} className="hover:bg-slate-800/30 transition">
                  <td className="p-4 font-mono font-semibold text-purple-400">
                    {c.device_id}
                  </td>
                  <td className="p-4 font-medium text-white">
                    <div>{c.number}</div>
                    {c.name && <div className="text-[11px] text-slate-400">{c.name}</div>}
                  </td>
                  <td className="p-4">
                    <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                      c.type === 'INCOMING' ? 'bg-blue-500/15 text-blue-400' :
                      c.type === 'OUTGOING' ? 'bg-emerald-500/15 text-emerald-400' :
                      'bg-rose-500/15 text-rose-400'
                    }`}>
                      {c.type === 'INCOMING' ? <PhoneIncoming className="w-3 h-3" /> :
                       c.type === 'OUTGOING' ? <PhoneOutgoing className="w-3 h-3" /> :
                       <PhoneMissed className="w-3 h-3" />}
                      {c.type}
                    </span>
                  </td>
                  <td className="p-4 text-slate-300">
                    {c.duration > 0 ? `${Math.floor(c.duration / 60)} daq ${c.duration % 60} soniya` : '--'}
                  </td>
                  <td className="p-4 text-slate-400">
                    {new Date(c.timestamp).toLocaleString()}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
