import React, { useState } from 'react';
import {
  Smartphone,
  Search,
  Filter,
  Wifi,
  BatteryCharging,
  ShieldCheck,
  CheckCircle,
  XCircle,
  Clock,
  ArrowRight
} from 'lucide-react';
import { Device } from '../types';

interface DevicesPageProps {
  devices: Device[];
  onSelectDevice: (device: Device) => void;
}

export const DevicesPage: React.FC<DevicesPageProps> = ({ devices, onSelectDevice }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<'all' | 'online' | 'offline'>('all');

  const filtered = devices.filter((d) => {
    const matchesSearch =
      d.device_name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      d.device_id.toLowerCase().includes(searchTerm.toLowerCase()) ||
      d.model.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'all' || d.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-white font-['Outfit']">
            Barcha Qurilmalar ({devices.length})
          </h2>
          <p className="text-sm text-slate-400">
            Tizimga ro'yxatdan o'tgan barcha Android qurilmalar ro'yxati va holati
          </p>
        </div>

        {/* Search & Filter */}
        <div className="flex items-center gap-3">
          <div className="relative">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Qurilma yoki ID qidirish..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-9 pr-4 py-2 bg-slate-900 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500 w-56 sm:w-64"
            />
          </div>

          <div className="flex items-center bg-slate-900 border border-slate-800 rounded-xl p-1 text-xs">
            <button
              onClick={() => setStatusFilter('all')}
              className={`px-3 py-1 rounded-lg font-medium transition ${
                statusFilter === 'all' ? 'bg-purple-600 text-white' : 'text-slate-400 hover:text-white'
              }`}
            >
              Hammasi
            </button>
            <button
              onClick={() => setStatusFilter('online')}
              className={`px-3 py-1 rounded-lg font-medium transition ${
                statusFilter === 'online' ? 'bg-purple-600 text-white' : 'text-slate-400 hover:text-white'
              }`}
            >
              Online
            </button>
            <button
              onClick={() => setStatusFilter('offline')}
              className={`px-3 py-1 rounded-lg font-medium transition ${
                statusFilter === 'offline' ? 'bg-purple-600 text-white' : 'text-slate-400 hover:text-white'
              }`}
            >
              Offline
            </button>
          </div>
        </div>
      </div>

      {/* Device Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {filtered.map((device) => (
          <div
            key={device.device_id}
            className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 hover:border-purple-500/40 transition group relative overflow-hidden shadow-sm"
          >
            {/* Top row */}
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-purple-500/10 text-purple-400 flex items-center justify-center font-bold">
                  <Smartphone className="w-5 h-5" />
                </div>
                <div>
                  <h4 className="font-bold text-white text-base leading-tight group-hover:text-purple-300 transition">
                    {device.device_name}
                  </h4>
                  <span className="text-xs font-mono font-semibold text-purple-400">
                    {device.device_id}
                  </span>
                </div>
              </div>

              <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-semibold ${
                device.status === 'online'
                  ? 'bg-emerald-500/15 text-emerald-400 border border-emerald-500/30'
                  : 'bg-rose-500/15 text-rose-400 border border-rose-500/30'
              }`}>
                <span className={`w-1.5 h-1.5 rounded-full ${device.status === 'online' ? 'bg-emerald-400 animate-pulse' : 'bg-rose-400'}`}></span>
                {device.status === 'online' ? 'Online' : 'Offline'}
              </span>
            </div>

            {/* Device specs */}
            <div className="mt-4 pt-3 border-t border-slate-800/80 grid grid-cols-2 gap-3 text-xs">
              <div>
                <span className="text-slate-400 block text-[11px]">Tizim (OS)</span>
                <span className="font-medium text-slate-200">{device.android_version}</span>
              </div>
              <div>
                <span className="text-slate-400 block text-[11px]">Tarmoq turi</span>
                <span className="font-medium text-slate-200 flex items-center gap-1">
                  <Wifi className="w-3 h-3 text-purple-400" />
                  {device.network_status}
                </span>
              </div>
              <div>
                <span className="text-slate-400 block text-[11px]">Batareya holati</span>
                <div className="flex items-center gap-1.5 mt-0.5">
                  <span className={`font-bold ${device.battery > 20 ? 'text-teal-400' : 'text-rose-400'}`}>
                    {device.battery}%
                  </span>
                  {device.is_charging && <BatteryCharging className="w-3.5 h-3.5 text-teal-400" />}
                </div>
              </div>
              <div>
                <span className="text-slate-400 block text-[11px]">Oxirgi faollik</span>
                <span className="font-medium text-slate-300">
                  {new Date(device.last_seen).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </span>
              </div>
            </div>

            {/* Action button */}
            <div className="mt-5">
              <button
                onClick={() => onSelectDevice(device)}
                className="w-full py-2 px-3 rounded-xl bg-purple-600/15 hover:bg-purple-600/25 border border-purple-500/30 text-purple-300 hover:text-white text-xs font-semibold transition flex items-center justify-center gap-2"
              >
                <span>Boshqarish va Tafsilotlar</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
