import React, { useState } from 'react';
import { MessageSquare, Search, Filter, Smartphone } from 'lucide-react';
import { SmsRecord, Device } from '../types';

interface SmsPageProps {
  sms: SmsRecord[];
  devices: Device[];
}

export const SmsPage: React.FC<SmsPageProps> = ({ sms, devices }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedDevice, setSelectedDevice] = useState<string>('all');
  const [typeFilter, setTypeFilter] = useState<'ALL' | 'INCOMING' | 'OUTGOING'>('ALL');

  const filtered = sms.filter((item) => {
    const matchesSearch =
      item.address.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.message.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesDevice = selectedDevice === 'all' || item.device_id === selectedDevice;
    const matchesType = typeFilter === 'ALL' || item.type === typeFilter;
    return matchesSearch && matchesDevice && matchesType;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-white font-['Outfit']">
            SMS Xabarlari Explorer
          </h2>
          <p className="text-sm text-slate-400">
            Foydalanuvchi roziligi bilan sinxronlangan barcha kiruvchi va chiquvchi SMSlar
          </p>
        </div>

        {/* Filter controls */}
        <div className="flex flex-wrap items-center gap-3">
          <div className="relative">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="SMS matni yoki raqam..."
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
            {(['ALL', 'INCOMING', 'OUTGOING'] as const).map(t => (
              <button
                key={t}
                onClick={() => setTypeFilter(t)}
                className={`px-2.5 py-1 rounded-lg font-medium transition ${
                  typeFilter === t ? 'bg-purple-600 text-white' : 'text-slate-400 hover:text-white'
                }`}
              >
                {t === 'ALL' ? 'Barchasi' : t === 'INCOMING' ? 'Kiruvchi' : 'Chiquvchi'}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* SMS List */}
      <div className="rounded-2xl bg-slate-900/90 border border-slate-800 divide-y divide-slate-800/60 overflow-hidden">
        {filtered.length === 0 ? (
          <div className="p-12 text-center text-slate-400 text-xs">
            SMS xabarlari topilmadi.
          </div>
        ) : (
          filtered.map((item) => (
            <div key={item.id} className="p-4 hover:bg-slate-800/30 transition text-xs">
              <div className="flex items-center justify-between mb-2">
                <div className="flex items-center gap-3">
                  <span className="font-mono text-purple-400 bg-purple-500/10 px-2 py-0.5 rounded border border-purple-500/20 font-semibold">
                    {item.device_id}
                  </span>
                  <span className="font-bold text-sm text-white">{item.address}</span>
                  <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                    item.type === 'INCOMING' ? 'bg-blue-500/15 text-blue-400' : 'bg-emerald-500/15 text-emerald-400'
                  }`}>
                    {item.type}
                  </span>
                </div>
                <span className="text-slate-400 text-[11px]">
                  {new Date(item.timestamp).toLocaleString()}
                </span>
              </div>
              <p className="text-slate-200 leading-relaxed text-sm bg-slate-800/40 p-3 rounded-xl border border-slate-800/60">
                {item.message}
              </p>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
