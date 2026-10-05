import React from 'react';
import {
  Smartphone,
  Wifi,
  BatteryCharging,
  MessageSquare,
  PhoneCall,
  Image,
  AlarmClock,
  ShieldCheck,
  TrendingUp,
  ArrowRight,
  CheckCircle2,
  Clock
} from 'lucide-react';
import { Device, SmsRecord, CallRecord, MediaRecord, Alarm, ActivityLog } from '../types';

interface DashboardPageProps {
  devices: Device[];
  sms: SmsRecord[];
  calls: CallRecord[];
  media: MediaRecord[];
  alarms: Alarm[];
  logs: ActivityLog[];
  onSelectDevice: (device: Device) => void;
  onNavigateTab: (tab: string) => void;
}

export const DashboardPage: React.FC<DashboardPageProps> = ({
  devices,
  sms,
  calls,
  media,
  alarms,
  logs,
  onSelectDevice,
  onNavigateTab
}) => {
  const onlineDevices = devices.filter(d => d.status === 'online');
  const offlineDevices = devices.filter(d => d.status === 'offline');
  const activeAlarms = alarms.filter(a => a.is_enabled);
  const avgBattery = Math.round(devices.reduce((acc, d) => acc + d.battery, 0) / (devices.length || 1));

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-white font-['Outfit']">
            Tizim Monitoringi va Boshqaruv
          </h2>
          <p className="text-sm text-slate-400">
            Ulangan Android qurilmalar, telemetriya ko'rsatkichlari va faollik holati
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => onNavigateTab('devices')}
            className="px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-medium text-sm transition shadow-lg shadow-purple-600/20 flex items-center gap-2"
          >
            <Smartphone className="w-4 h-4" />
            <span>Qurilmalarni ko'rish</span>
          </button>
        </div>
      </div>

      {/* Primary KPI Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Total & Online Devices */}
        <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 shadow-sm relative overflow-hidden">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Jami Qurilmalar</span>
            <div className="w-9 h-9 rounded-xl bg-purple-500/10 text-purple-400 flex items-center justify-center">
              <Smartphone className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-extrabold text-white">{devices.length}</span>
            <span className="text-xs text-emerald-400 font-semibold flex items-center gap-1">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-ping"></span>
              {onlineDevices.length} online
            </span>
          </div>
          <div className="mt-2 text-xs text-slate-400 flex items-center gap-2">
            <span>{offlineDevices.length} ta aloqa uzilgan</span>
          </div>
        </div>

        {/* Battery Health Average */}
        <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">O'rtacha Quvvat</span>
            <div className="w-9 h-9 rounded-xl bg-teal-500/10 text-teal-400 flex items-center justify-center">
              <BatteryCharging className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-extrabold text-white">{avgBattery}%</span>
            <span className="text-xs text-teal-400 font-semibold">Barqaror</span>
          </div>
          <div className="mt-2 w-full bg-slate-800 rounded-full h-1.5 overflow-hidden">
            <div className="bg-teal-400 h-full rounded-full transition-all" style={{ width: `${avgBattery}%` }}></div>
          </div>
        </div>

        {/* SMS & Calls Records */}
        <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">SMS / Qo'ng'iroqlar</span>
            <div className="w-9 h-9 rounded-xl bg-blue-500/10 text-blue-400 flex items-center justify-center">
              <MessageSquare className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-extrabold text-white">{sms.length}</span>
            <span className="text-xs text-slate-400 font-normal">SMS | {calls.length} Qo'ng'iroq</span>
          </div>
          <div className="mt-2 text-xs text-slate-400 flex items-center gap-1.5">
            <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
            <span>Rozilik asosida yozilgan</span>
          </div>
        </div>

        {/* Active Alarms */}
        <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Faol Budilniklar</span>
            <div className="w-9 h-9 rounded-xl bg-amber-500/10 text-amber-400 flex items-center justify-center">
              <AlarmClock className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-3xl font-extrabold text-white">{activeAlarms.length}</span>
            <span className="text-xs text-amber-400 font-semibold">{alarms.length} jami</span>
          </div>
          <div className="mt-2 text-xs text-slate-400 flex items-center gap-1.5">
            <Clock className="w-3.5 h-3.5 text-amber-400" />
            <span>Matematik masalali</span>
          </div>
        </div>
      </div>

      {/* Main Grid: Devices Quick Overview & Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Connected Devices (2 Cols) */}
        <div className="lg:col-span-2 rounded-2xl bg-slate-900/90 border border-slate-800 p-6">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="font-bold text-base text-white font-['Outfit']">Ulangan Qurilmalar</h3>
              <p className="text-xs text-slate-400">So'nggi telemetriya ma'lumotlari</p>
            </div>
            <button
              onClick={() => onNavigateTab('devices')}
              className="text-xs font-semibold text-purple-400 hover:text-purple-300 flex items-center gap-1"
            >
              <span>Barchasini ko'rish</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="border-b border-slate-800 text-[11px] font-semibold text-slate-400 uppercase tracking-wider">
                  <th className="pb-3">Qurilma / Model</th>
                  <th className="pb-3">Device ID</th>
                  <th className="pb-3">Batareya</th>
                  <th className="pb-3">Tarmoq</th>
                  <th className="pb-3">Holat</th>
                  <th className="pb-3 text-right">Amal</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {devices.map((device) => (
                  <tr key={device.device_id} className="hover:bg-slate-800/40 transition">
                    <td className="py-3 font-medium text-white flex items-center gap-2.5">
                      <div className="w-7 h-7 rounded-lg bg-slate-800 flex items-center justify-center text-purple-400">
                        <Smartphone className="w-4 h-4" />
                      </div>
                      <div>
                        <div>{device.device_name}</div>
                        <div className="text-[11px] text-slate-400 font-normal">{device.android_version}</div>
                      </div>
                    </td>
                    <td className="py-3 text-xs font-mono text-slate-300">
                      <span className="px-2 py-0.5 rounded bg-slate-800 text-slate-300 font-semibold">
                        {device.device_id}
                      </span>
                    </td>
                    <td className="py-3">
                      <div className="flex items-center gap-2">
                        <span className={`text-xs font-semibold ${device.battery > 20 ? 'text-slate-200' : 'text-rose-400'}`}>
                          {device.battery}%
                        </span>
                        {device.is_charging && <BatteryCharging className="w-3.5 h-3.5 text-teal-400" />}
                      </div>
                    </td>
                    <td className="py-3 text-xs text-slate-300">
                      <span className="flex items-center gap-1.5">
                        <Wifi className="w-3 h-3 text-purple-400" />
                        {device.network_status}
                      </span>
                    </td>
                    <td className="py-3">
                      <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-semibold ${
                        device.status === 'online'
                          ? 'bg-emerald-500/15 text-emerald-400 border border-emerald-500/30'
                          : 'bg-rose-500/15 text-rose-400 border border-rose-500/30'
                      }`}>
                        <span className={`w-1.5 h-1.5 rounded-full ${device.status === 'online' ? 'bg-emerald-400 animate-pulse' : 'bg-rose-400'}`}></span>
                        {device.status === 'online' ? 'Online' : 'Offline'}
                      </span>
                    </td>
                    <td className="py-3 text-right">
                      <button
                        onClick={() => onSelectDevice(device)}
                        className="px-2.5 py-1 text-xs font-medium rounded-lg bg-purple-600/20 text-purple-300 hover:bg-purple-600/30 transition border border-purple-500/30"
                      >
                        Boshqarish
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Live Activity & Audit Stream (1 Col) */}
        <div className="rounded-2xl bg-slate-900/90 border border-slate-800 p-6 flex flex-col">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="font-bold text-base text-white font-['Outfit']">So'nggi Hodisalar</h3>
              <p className="text-xs text-slate-400">Qurilmalar faollik loglari</p>
            </div>
            <button
              onClick={() => onNavigateTab('logs')}
              className="text-xs font-semibold text-purple-400 hover:text-purple-300"
            >
              Barchasi
            </button>
          </div>

          <div className="space-y-3 flex-1 overflow-y-auto pr-1">
            {logs.map((log) => (
              <div key={log.id} className="p-3 rounded-xl bg-slate-800/40 border border-slate-800 text-xs">
                <div className="flex items-center justify-between text-slate-400 mb-1">
                  <span className="font-mono font-semibold text-purple-400">{log.device_id}</span>
                  <span className="text-[10px] text-slate-400">
                    {new Date(log.created_at).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </span>
                </div>
                <div className="font-medium text-slate-200">{log.event_type}</div>
                <div className="text-[11px] text-slate-400 mt-0.5 truncate">
                  {typeof log.event_data === 'string' ? log.event_data : JSON.stringify(log.event_data)}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
