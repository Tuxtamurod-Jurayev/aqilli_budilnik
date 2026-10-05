import React from 'react';
import { Search, RefreshCw, Bell, Shield, Smartphone, Trash2 } from 'lucide-react';
import { Device } from '../types';
import { api } from '../services/api';

interface NavbarProps {
  devices: Device[];
  selectedDevice: Device | null;
  onSelectDevice: (device: Device | null) => void;
  onRefresh: () => void;
  isRefreshing: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({
  devices,
  selectedDevice,
  onSelectDevice,
  onRefresh,
  isRefreshing
}) => {
  const handleClearAll = async () => {
    if (window.confirm("Barcha eski va sinov qurilmalar ma'lumotlarini tozalashni xohlaysizmi? Yangi ulangan Poco va boshqa faol telefonlar avtomatik ro'yxatga olinadi.")) {
      await api.clearAllData();
      onSelectDevice(null);
      onRefresh();
    }
  };
  return (
    <header className="h-16 px-6 bg-[#0f172a]/90 backdrop-blur border-b border-slate-800 flex items-center justify-between sticky top-0 z-30">
      {/* Device Selector */}
      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2 bg-slate-900 border border-slate-800 rounded-xl px-3 py-1.5">
          <Smartphone className="w-4 h-4 text-purple-400" />
          <span className="text-xs text-slate-400 font-medium">Faol Qurilma:</span>
          <select
            value={selectedDevice ? selectedDevice.device_id : 'all'}
            onChange={(e) => {
              if (e.target.value === 'all') {
                onSelectDevice(null);
              } else {
                const found = devices.find(d => d.device_id === e.target.value);
                onSelectDevice(found || null);
              }
            }}
            className="bg-transparent text-sm font-semibold text-slate-200 outline-none cursor-pointer pr-2"
          >
            <option value="all" className="bg-slate-900 text-slate-200">Barcha Qurilmalar ({devices.length})</option>
            {devices.map((d) => (
              <option key={d.device_id} value={d.device_id} className="bg-slate-900 text-slate-200">
                {d.device_name} ({d.device_id}) - {d.battery}%
              </option>
            ))}
          </select>
        </div>

        {selectedDevice && (
          <div className="flex items-center gap-2">
            <span className={`w-2 h-2 rounded-full ${selectedDevice.status === 'online' ? 'bg-emerald-400 animate-pulse' : 'bg-rose-400'}`}></span>
            <span className="text-xs font-medium text-slate-300">
              {selectedDevice.status === 'online' ? 'Tarmoqda' : 'Aloqa uzilgan'}
            </span>
          </div>
        )}
      </div>

      {/* Right Controls */}
      <div className="flex items-center gap-3">
        {/* Clear All Stale Mock Data Button */}
        <button
          onClick={handleClearAll}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-rose-500/10 hover:bg-rose-500/20 text-rose-300 border border-rose-500/30 text-xs font-medium transition"
          title="Barcha eski va sinov ma'lumotlarini tozalash"
        >
          <Trash2 className="w-3.5 h-3.5" />
          <span>Tozalash</span>
        </button>

        {/* Refresh Button */}
        <button
          onClick={onRefresh}
          disabled={isRefreshing}
          className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-medium transition"
          title="Ma'lumotlarni yangilash"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${isRefreshing ? 'animate-spin text-purple-400' : ''}`} />
          <span>{isRefreshing ? 'Yangilanmoqda...' : 'Sinxronlash'}</span>
        </button>

        {/* Security Badge */}
        <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-semibold">
          <Shield className="w-3.5 h-3.5" />
          <span>RLS & Consent Himoyasi</span>
        </div>

        {/* Admin Avatar */}
        <div className="flex items-center gap-2.5 pl-2 border-l border-slate-800">
          <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-purple-500 to-indigo-600 flex items-center justify-center font-bold text-xs text-white">
            AD
          </div>
          <div className="hidden sm:block text-left text-xs">
            <div className="font-semibold text-slate-200">Administrator</div>
            <div className="text-[11px] text-slate-400">Super Admin</div>
          </div>
        </div>
      </div>
    </header>
  );
};
