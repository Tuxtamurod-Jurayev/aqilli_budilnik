import React, { useState } from 'react';
import { History, Search, Filter, ShieldCheck, AlertCircle } from 'lucide-react';
import { ActivityLog, Device } from '../types';

interface LogsPageProps {
  logs: ActivityLog[];
  devices: Device[];
}

export const LogsPage: React.FC<LogsPageProps> = ({ logs, devices }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedDevice, setSelectedDevice] = useState<string>('all');

  const filtered = logs.filter((log) => {
    const textData = typeof log.event_data === 'string' ? log.event_data : JSON.stringify(log.event_data);
    const matchesSearch =
      log.event_type.toLowerCase().includes(searchTerm.toLowerCase()) ||
      textData.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesDevice = selectedDevice === 'all' || log.device_id === selectedDevice;
    return matchesSearch && matchesDevice;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-white font-['Outfit']">
            Xavfsizlik va Faollik Audit Loglari
          </h2>
          <p className="text-sm text-slate-400">
            Tizim hodisalari, ruxsatlar o'zgarishi, budilnik o'chirilishi va sinxronizatsiya tarixi
          </p>
        </div>

        {/* Filter controls */}
        <div className="flex flex-wrap items-center gap-3">
          <div className="relative">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Hodisa turi yoki matni..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-9 pr-4 py-2 bg-slate-900 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500 w-56"
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
        </div>
      </div>

      {/* Logs Table */}
      <div className="rounded-2xl bg-slate-900/90 border border-slate-800 divide-y divide-slate-800/60 overflow-hidden">
        {filtered.length === 0 ? (
          <div className="p-12 text-center text-slate-400 text-xs">
            Audit loglari mavjud emas.
          </div>
        ) : (
          filtered.map((log) => (
            <div key={log.id} className="p-4 hover:bg-slate-800/30 transition text-xs flex items-start justify-between gap-4">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <span className="font-mono text-purple-400 bg-purple-500/10 px-2 py-0.5 rounded border border-purple-500/20 font-semibold">
                    {log.device_id}
                  </span>
                  <span className="font-bold text-white text-sm">
                    {log.event_type}
                  </span>
                </div>
                <div className="text-slate-300 font-mono text-[11px] bg-slate-800/50 px-2.5 py-1.5 rounded-lg border border-slate-800 inline-block">
                  {typeof log.event_data === 'string' ? log.event_data : JSON.stringify(log.event_data)}
                </div>
              </div>

              <div className="text-right text-[11px] text-slate-400 whitespace-nowrap">
                {new Date(log.created_at).toLocaleString()}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
