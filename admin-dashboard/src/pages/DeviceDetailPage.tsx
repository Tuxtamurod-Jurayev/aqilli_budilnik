import React, { useState } from 'react';
import {
  Smartphone,
  Wifi,
  BatteryCharging,
  MessageSquare,
  PhoneCall,
  Image,
  AlarmClock,
  ShieldCheck,
  History,
  ArrowLeft,
  CheckCircle,
  XCircle,
  Clock,
  Search,
  Plus,
  Play,
  RotateCcw,
  Bell,
  BellOff,
  Zap,
  RefreshCw,
  Flame
} from 'lucide-react';
import { Device, Permission, Alarm, SmsRecord, CallRecord, MediaRecord, ActivityLog, RemoteCommand } from '../types';
import { api } from '../services/api';

interface DeviceDetailPageProps {
  device: Device;
  permissions: Permission | null;
  sms: SmsRecord[];
  calls: CallRecord[];
  media: MediaRecord[];
  alarms: Alarm[];
  logs: ActivityLog[];
  onBack: () => void;
  onToggleAlarm: (id: string) => void;
  onCreateAlarm: (alarm: Omit<Alarm, 'id' | 'created_at'>) => void;
}

export const DeviceDetailPage: React.FC<DeviceDetailPageProps> = ({
  device,
  permissions,
  sms,
  calls,
  media,
  alarms,
  logs,
  onBack,
  onToggleAlarm,
  onCreateAlarm
}) => {
  const [activeTab, setActiveTab] = useState<'overview' | 'sms' | 'calls' | 'gallery' | 'alarms' | 'permissions' | 'activity'>('overview');
  const [smsSearch, setSmsSearch] = useState('');
  const [callTypeFilter, setCallTypeFilter] = useState<'ALL' | 'INCOMING' | 'OUTGOING' | 'MISSED'>('ALL');
  const [showAddAlarm, setShowAddAlarm] = useState(false);

  // New alarm form state
  const [newAlarmTime, setNewAlarmTime] = useState('07:30');
  const [newAlarmLabel, setNewAlarmLabel] = useState('Ertalabki uyg\'onish');
  const [newAlarmDifficulty, setNewAlarmDifficulty] = useState<'EASY' | 'MEDIUM' | 'HARD'>('MEDIUM');

  // Remote Control state
  const [commandFeedback, setCommandFeedback] = useState<string | null>(null);
  const [isExecutingCmd, setIsExecutingCmd] = useState(false);

  const handleSendCommand = async (cmd: RemoteCommand['command'], label: string) => {
    setIsExecutingCmd(true);
    setCommandFeedback(`${label} buyrug'i qurilmaga yuborilmoqda...`);
    try {
      await api.sendRemoteCommand(device.device_id, cmd);
      setCommandFeedback(`✓ "${label}" buyrug'i muvaffaqiyatli yuborildi!`);
      setTimeout(() => setCommandFeedback(null), 3500);
    } catch (e) {
      setCommandFeedback(`Xatolik: ${e}`);
    } finally {
      setIsExecutingCmd(false);
    }
  };

  const filteredSms = sms.filter(s =>
    s.address.toLowerCase().includes(smsSearch.toLowerCase()) ||
    s.message.toLowerCase().includes(smsSearch.toLowerCase())
  );

  const filteredCalls = calls.filter(c =>
    callTypeFilter === 'ALL' || c.type === callTypeFilter
  );

  const tabs = [
    { id: 'overview', label: 'Umumiy Ma\'lumot', icon: Smartphone },
    { id: 'sms', label: `SMS (${sms.length})`, icon: MessageSquare },
    { id: 'calls', label: `Qo'ng'iroqlar (${calls.length})`, icon: PhoneCall },
    { id: 'gallery', label: `Galereya (${media.length})`, icon: Image },
    { id: 'alarms', label: `Budilniklar (${alarms.length})`, icon: AlarmClock },
    { id: 'permissions', label: 'Ruxsatlar', icon: ShieldCheck },
    { id: 'activity', label: 'Faollik Tarixi', icon: History },
  ];

  return (
    <div className="space-y-6">
      {/* Back button and Device Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div className="flex items-center gap-3">
          <button
            onClick={onBack}
            className="p-2 rounded-xl bg-slate-900 border border-slate-800 text-slate-400 hover:text-white transition"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-2xl font-bold tracking-tight text-white font-['Outfit']">
                {device.device_name}
              </h2>
              <span className="font-mono text-xs font-bold px-2 py-0.5 rounded bg-purple-500/20 text-purple-300 border border-purple-500/30">
                {device.device_id}
              </span>
            </div>
            <p className="text-xs text-slate-400">
              Oxirgi ulanish: {new Date(device.last_seen).toLocaleString()}
            </p>
          </div>
        </div>

        {/* Quick status pill */}
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-slate-900 border border-slate-800 text-xs">
            <span className={`w-2 h-2 rounded-full ${device.status === 'online' ? 'bg-emerald-400 animate-pulse' : 'bg-rose-400'}`}></span>
            <span className="font-semibold text-slate-200">{device.status === 'online' ? 'Online' : 'Offline'}</span>
          </div>
          <div className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-slate-900 border border-slate-800 text-xs">
            <BatteryCharging className="w-4 h-4 text-teal-400" />
            <span className="font-semibold text-slate-200">{device.battery}%</span>
          </div>
        </div>
      </div>

      {/* Tabs Row */}
      <div className="flex items-center gap-1 overflow-x-auto pb-1 border-b border-slate-800">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`flex items-center gap-2 px-4 py-2.5 rounded-t-xl text-xs font-semibold whitespace-nowrap transition border-b-2 ${
                isActive
                  ? 'border-purple-500 text-purple-400 bg-purple-500/10'
                  : 'border-transparent text-slate-400 hover:text-slate-200 hover:bg-slate-800/40'
              }`}
            >
              <Icon className="w-4 h-4" />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* TAB CONTENT: 1. OVERVIEW */}
      {activeTab === 'overview' && (
        <div className="space-y-6">
          {/* Remote Command Control Center */}
          <div className="p-6 rounded-2xl bg-gradient-to-r from-purple-950/40 via-slate-900 to-indigo-950/30 border border-purple-500/30 space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 border-b border-purple-500/20 pb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-purple-500/20 text-purple-400 flex items-center justify-center">
                  <Zap className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-base font-bold text-white font-['Outfit'] flex items-center gap-2">
                    Masofadan To'liq Boshqarish Pulti (MDM Control Center)
                    <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                      FAOL
                    </span>
                  </h3>
                  <p className="text-xs text-slate-400">
                    Qurilmaga to'g'ridan-to'g'ri masofaviy buyruqlar yuborish va harakatlarni darhol ishga tushirish
                  </p>
                </div>
              </div>

              {commandFeedback && (
                <div className="px-3.5 py-1.5 rounded-xl bg-purple-500/20 border border-purple-500/40 text-xs font-semibold text-purple-200 animate-pulse">
                  {commandFeedback}
                </div>
              )}
            </div>

            {/* Quick Action Control Buttons */}
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
              <button
                disabled={isExecutingCmd}
                onClick={() => handleSendCommand('RING_ALARM', 'Budilnikni Jiringlatish')}
                className="p-3.5 rounded-xl bg-rose-500/10 hover:bg-rose-500/20 border border-rose-500/30 text-rose-300 font-semibold text-xs flex flex-col items-center justify-center gap-2 transition hover:scale-[1.02] active:scale-95 disabled:opacity-50"
              >
                <Bell className="w-5 h-5 text-rose-400" />
                <span>Budilnikni Yoqish</span>
              </button>

              <button
                disabled={isExecutingCmd}
                onClick={() => handleSendCommand('STOP_ALARM', 'Budilnikni To\'xtatish')}
                className="p-3.5 rounded-xl bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-200 font-semibold text-xs flex flex-col items-center justify-center gap-2 transition hover:scale-[1.02] active:scale-95 disabled:opacity-50"
              >
                <BellOff className="w-5 h-5 text-slate-400" />
                <span>O'chirish</span>
              </button>

              <button
                disabled={isExecutingCmd}
                onClick={() => handleSendCommand('TOGGLE_FLASHLIGHT', 'Fonar (Chiroq)')}
                className="p-3.5 rounded-xl bg-amber-500/10 hover:bg-amber-500/20 border border-amber-500/30 text-amber-300 font-semibold text-xs flex flex-col items-center justify-center gap-2 transition hover:scale-[1.02] active:scale-95 disabled:opacity-50"
              >
                <Zap className="w-5 h-5 text-amber-400" />
                <span>Fonarni Yoqish</span>
              </button>

              <button
                disabled={isExecutingCmd}
                onClick={() => handleSendCommand('VIBRATE', 'Vibratsiya')}
                className="p-3.5 rounded-xl bg-teal-500/10 hover:bg-teal-500/20 border border-teal-500/30 text-teal-300 font-semibold text-xs flex flex-col items-center justify-center gap-2 transition hover:scale-[1.02] active:scale-95 disabled:opacity-50"
              >
                <RotateCcw className="w-5 h-5 text-teal-400" />
                <span>Titratish (3s)</span>
              </button>

              <button
                disabled={isExecutingCmd}
                onClick={() => handleSendCommand('FORCE_SYNC', 'Darhol Sinxronlash')}
                className="p-3.5 rounded-xl bg-indigo-500/10 hover:bg-indigo-500/20 border border-indigo-500/30 text-indigo-300 font-semibold text-xs flex flex-col items-center justify-center gap-2 transition hover:scale-[1.02] active:scale-95 disabled:opacity-50"
              >
                <RefreshCw className="w-5 h-5 text-indigo-400" />
                <span>Darhol Sync</span>
              </button>

              <button
                disabled={isExecutingCmd}
                onClick={() => handleSendCommand('MATH_CHALLENGE', 'Matematika Sinovi')}
                className="p-3.5 rounded-xl bg-purple-500/10 hover:bg-purple-500/20 border border-purple-500/30 text-purple-300 font-semibold text-xs flex flex-col items-center justify-center gap-2 transition hover:scale-[1.02] active:scale-95 disabled:opacity-50"
              >
                <Flame className="w-5 h-5 text-purple-400" />
                <span>Matematik Sinov</span>
              </button>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {/* Hardware & System */}
          <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 space-y-3">
            <h4 className="font-bold text-sm text-white font-['Outfit'] border-b border-slate-800 pb-2">
              Qurilma Parametrlari
            </h4>
            <div className="space-y-2 text-xs">
              <div className="flex justify-between">
                <span className="text-slate-400">Ishlab chiqaruvchi</span>
                <span className="font-semibold text-white">{device.manufacturer}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-400">Model kodi</span>
                <span className="font-semibold text-white">{device.model}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-400">Operatsion tizim</span>
                <span className="font-semibold text-white">{device.android_version}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-400">Ilova versiyasi</span>
                <span className="font-semibold text-white">v{device.app_version}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-400">Ro'yxatdan o'tgan</span>
                <span className="font-semibold text-white">{new Date(device.registered_at).toLocaleDateString()}</span>
              </div>
            </div>
          </div>

          {/* Network & Battery */}
          <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 space-y-3">
            <h4 className="font-bold text-sm text-white font-['Outfit'] border-b border-slate-800 pb-2">
              Telemetriya va Aloqa
            </h4>
            <div className="space-y-2 text-xs">
              <div className="flex justify-between items-center">
                <span className="text-slate-400">Batareya quvvati</span>
                <span className="font-bold text-teal-400">{device.battery}% {device.is_charging ? '(Zaryadlanmoqda)' : ''}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-slate-400">Tarmoq holati</span>
                <span className="font-semibold text-purple-400">{device.network_status}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-slate-400">Heartbeat statusi</span>
                <span className="text-emerald-400 font-semibold">Active Sync</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-slate-400">Sinxronizatsiya oraliq vaqti</span>
                <span className="text-slate-300 font-semibold">Har 15 daqiqada</span>
              </div>
            </div>
          </div>

          {/* Permissions Overview Summary */}
          <div className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 space-y-3">
            <h4 className="font-bold text-sm text-white font-['Outfit'] border-b border-slate-800 pb-2">
              Ruxsatlar Holati
            </h4>
            <div className="space-y-2 text-xs">
              <div className="flex justify-between items-center">
                <span className="text-slate-300">SMS ruxsati</span>
                <span className={`font-semibold ${permissions?.sms ? 'text-emerald-400' : 'text-rose-400'}`}>
                  {permissions?.sms ? 'BERILGAN' : 'YO\'Q'}
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-slate-300">Qo'ng'iroqlar jurnali</span>
                <span className={`font-semibold ${permissions?.call_log ? 'text-emerald-400' : 'text-rose-400'}`}>
                  {permissions?.call_log ? 'BERILGAN' : 'YO\'Q'}
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-slate-300">Galereya / Media</span>
                <span className={`font-semibold ${permissions?.media ? 'text-emerald-400' : 'text-rose-400'}`}>
                  {permissions?.media ? 'BERILGAN' : 'YO\'Q'}
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-slate-300">Aniq Budilnik</span>
                <span className={`font-semibold ${permissions?.exact_alarm ? 'text-emerald-400' : 'text-rose-400'}`}>
                  {permissions?.exact_alarm ? 'BERILGAN' : 'YO\'Q'}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    )}

      {/* TAB CONTENT: 2. SMS */}
      {activeTab === 'sms' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between gap-4">
            <div className="relative flex-1 max-w-md">
              <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
              <input
                type="text"
                placeholder="SMS yoki raqam bo'yicha qidiruv..."
                value={smsSearch}
                onChange={(e) => setSmsSearch(e.target.value)}
                className="w-full pl-9 pr-4 py-2 bg-slate-900 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500"
              />
            </div>
            <span className="text-xs text-slate-400">Jami: {filteredSms.length} ta xabar</span>
          </div>

          <div className="divide-y divide-slate-800 rounded-2xl bg-slate-900/90 border border-slate-800 overflow-hidden">
            {filteredSms.length === 0 ? (
              <div className="p-8 text-center text-xs text-slate-400">SMS xabarlari topilmadi.</div>
            ) : (
              filteredSms.map((item) => (
                <div key={item.id} className="p-4 hover:bg-slate-800/30 transition text-xs">
                  <div className="flex items-center justify-between mb-1.5">
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-sm text-purple-300">{item.address}</span>
                      <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                        item.type === 'INCOMING' ? 'bg-blue-500/20 text-blue-400' : 'bg-emerald-500/20 text-emerald-400'
                      }`}>
                        {item.type === 'INCOMING' ? 'KIRUVCHI' : 'CHIQUVCHI'}
                      </span>
                    </div>
                    <span className="text-slate-400 text-[11px]">
                      {new Date(item.timestamp).toLocaleString()}
                    </span>
                  </div>
                  <p className="text-slate-200 leading-relaxed text-sm bg-slate-800/30 p-2.5 rounded-xl border border-slate-800/50">
                    {item.message}
                  </p>
                </div>
              ))
            )}
          </div>
        </div>
      )}

      {/* TAB CONTENT: 3. CALLS */}
      {activeTab === 'calls' && (
        <div className="space-y-4">
          <div className="flex items-center gap-2">
            {(['ALL', 'INCOMING', 'OUTGOING', 'MISSED'] as const).map((type) => (
              <button
                key={type}
                onClick={() => setCallTypeFilter(type)}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition ${
                  callTypeFilter === type ? 'bg-purple-600 text-white' : 'bg-slate-900 border border-slate-800 text-slate-400 hover:text-white'
                }`}
              >
                {type === 'ALL' ? 'Barchasi' : type === 'INCOMING' ? 'Kiruvchi' : type === 'OUTGOING' ? 'Chiquvchi' : 'O\'tkazib yuborilgan'}
              </button>
            ))}
          </div>

          <div className="rounded-2xl bg-slate-900/90 border border-slate-800 overflow-hidden">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-slate-800 text-slate-400 uppercase tracking-wider text-[11px] bg-slate-950/40">
                  <th className="p-3.5">Raqam / Kontakt</th>
                  <th className="p-3.5">Turi</th>
                  <th className="p-3.5">Davomiyligi</th>
                  <th className="p-3.5">Vaqti</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {filteredCalls.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-800/30 transition">
                    <td className="p-3.5 font-medium text-white">
                      <div>{c.number}</div>
                      {c.name && <div className="text-[11px] text-slate-400 font-normal">{c.name}</div>}
                    </td>
                    <td className="p-3.5">
                      <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                        c.type === 'INCOMING' ? 'bg-blue-500/15 text-blue-400' :
                        c.type === 'OUTGOING' ? 'bg-emerald-500/15 text-emerald-400' :
                        'bg-rose-500/15 text-rose-400'
                      }`}>
                        {c.type}
                      </span>
                    </td>
                    <td className="p-3.5 text-slate-300">
                      {Math.floor(c.duration / 60)}m {c.duration % 60}s
                    </td>
                    <td className="p-3.5 text-slate-400">
                      {new Date(c.timestamp).toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB CONTENT: 4. GALLERY */}
      {activeTab === 'gallery' && (
        <div className="space-y-4">
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
            {media.map((m) => (
              <div key={m.id} className="rounded-2xl bg-slate-900 border border-slate-800 p-3 flex flex-col justify-between">
                <div className="w-full h-32 rounded-xl bg-slate-800 flex items-center justify-center text-slate-500 mb-2 overflow-hidden relative">
                  <Image className="w-8 h-8 text-slate-600" />
                  <span className="absolute bottom-1 right-1 text-[9px] px-1.5 py-0.5 rounded bg-black/60 text-white font-mono">
                    {m.media_type.includes('video') ? 'VIDEO' : 'IMAGE'}
                  </span>
                </div>
                <div>
                  <div className="font-semibold text-xs text-white truncate" title={m.file_name}>{m.file_name}</div>
                  <div className="text-[10px] text-slate-400 mt-0.5">{(m.size / 1024 / 1024).toFixed(2)} MB</div>
                  <div className="text-[10px] text-slate-500">{new Date(m.date_added).toLocaleDateString()}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB CONTENT: 5. ALARMS */}
      {activeTab === 'alarms' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h4 className="font-bold text-sm text-white font-['Outfit']">Masofaviy Budilnik Boshqaruvi</h4>
            <button
              onClick={() => setShowAddAlarm(true)}
              className="px-3 py-1.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white text-xs font-semibold flex items-center gap-1.5 shadow"
            >
              <Plus className="w-4 h-4" />
              <span>Yangi Budilnik Yuborish</span>
            </button>
          </div>

          {/* Add Alarm Modal */}
          {showAddAlarm && (
            <div className="p-4 rounded-2xl bg-slate-900 border border-purple-500/40 space-y-3">
              <h5 className="font-bold text-xs text-purple-300 uppercase tracking-wider">Qurilmaga Yangi Budilnik Yaratish</h5>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div>
                  <label className="block text-[11px] text-slate-400 mb-1">Vaqt (HH:MM)</label>
                  <input
                    type="time"
                    value={newAlarmTime}
                    onChange={(e) => setNewAlarmTime(e.target.value)}
                    className="w-full px-3 py-1.5 rounded-lg bg-slate-800 border border-slate-700 text-xs text-white focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-[11px] text-slate-400 mb-1">Budilnik Nomi</label>
                  <input
                    type="text"
                    value={newAlarmLabel}
                    onChange={(e) => setNewAlarmLabel(e.target.value)}
                    className="w-full px-3 py-1.5 rounded-lg bg-slate-800 border border-slate-700 text-xs text-white focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-[11px] text-slate-400 mb-1">Matematik Qiyinchilik</label>
                  <select
                    value={newAlarmDifficulty}
                    onChange={(e) => setNewAlarmDifficulty(e.target.value as any)}
                    className="w-full px-3 py-1.5 rounded-lg bg-slate-800 border border-slate-700 text-xs text-white focus:outline-none"
                  >
                    <option value="EASY">Oson (2 xonali qo'shish/ayirish)</option>
                    <option value="MEDIUM">O'rta (Ko'paytirish/Bo'lish)</option>
                    <option value="HARD">Qiyin (Aralash formulalar)</option>
                  </select>
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  onClick={() => setShowAddAlarm(false)}
                  className="px-3 py-1 rounded-lg text-xs text-slate-400 hover:text-white"
                >
                  Bekor qilish
                </button>
                <button
                  onClick={() => {
                    onCreateAlarm({
                      device_id: device.device_id,
                      time: newAlarmTime,
                      label: newAlarmLabel,
                      is_enabled: true,
                      repeat_days: 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY',
                      difficulty: newAlarmDifficulty,
                      question_count: 3,
                      max_volume: true,
                      flashlight: true,
                      vibration: true
                    });
                    setShowAddAlarm(false);
                  }}
                  className="px-4 py-1.5 rounded-lg bg-purple-600 hover:bg-purple-500 text-xs font-semibold text-white"
                >
                  Qurilmaga Yuborish (Sync)
                </button>
              </div>
            </div>
          )}

          {/* Alarms list */}
          <div className="space-y-3">
            {alarms.map((alarm) => (
              <div
                key={alarm.id}
                className="p-4 rounded-2xl bg-slate-900/90 border border-slate-800 flex items-center justify-between"
              >
                <div className="flex items-center gap-4">
                  <div className="text-2xl font-bold font-mono text-white tracking-tight">
                    {alarm.time}
                  </div>
                  <div>
                    <div className="font-semibold text-sm text-slate-200">{alarm.label}</div>
                    <div className="text-[11px] text-slate-400 flex items-center gap-2">
                      <span className="text-purple-400 font-semibold">{alarm.difficulty} daraja</span>
                      <span>•</span>
                      <span>{alarm.question_count} ta misol</span>
                      <span>•</span>
                      <span>Max Ovoz: {alarm.max_volume ? 'Ha' : 'Yo\'q'}</span>
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <button
                    onClick={() => onToggleAlarm(alarm.id)}
                    className={`px-3 py-1.5 rounded-xl text-xs font-bold transition ${
                      alarm.is_enabled
                        ? 'bg-purple-600 text-white shadow-sm'
                        : 'bg-slate-800 text-slate-400 hover:text-white'
                    }`}
                  >
                    {alarm.is_enabled ? 'FAOL' : 'O\'CHIQ'}
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB CONTENT: 6. PERMISSIONS */}
      {activeTab === 'permissions' && (
        <div className="space-y-4">
          <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800">
            <h4 className="font-bold text-sm text-white font-['Outfit'] mb-3">
              Qurilma Ruxsatlari Auditi (Consent & Status)
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
              <div className="p-3 rounded-xl bg-slate-800/40 border border-slate-800 flex items-center justify-between">
                <div>
                  <div className="font-semibold text-white">SMS Xabarlarini O'qish (READ_SMS)</div>
                  <div className="text-[11px] text-slate-400">Foydalanuvchi tasdiqlagan</div>
                </div>
                <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${permissions?.sms ? 'bg-emerald-500/20 text-emerald-400' : 'bg-rose-500/20 text-rose-400'}`}>
                  {permissions?.sms ? 'GRANTED' : 'DENIED'}
                </span>
              </div>

              <div className="p-3 rounded-xl bg-slate-800/40 border border-slate-800 flex items-center justify-between">
                <div>
                  <div className="font-semibold text-white">Qo'ng'iroqlar Tarixi (READ_CALL_LOG)</div>
                  <div className="text-[11px] text-slate-400">Kiruvchi va chiquvchi jurnallar</div>
                </div>
                <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${permissions?.call_log ? 'bg-emerald-500/20 text-emerald-400' : 'bg-rose-500/20 text-rose-400'}`}>
                  {permissions?.call_log ? 'GRANTED' : 'DENIED'}
                </span>
              </div>

              <div className="p-3 rounded-xl bg-slate-800/40 border border-slate-800 flex items-center justify-between">
                <div>
                  <div className="font-semibold text-white">Galereya / Media (READ_MEDIA)</div>
                  <div className="text-[11px] text-slate-400">Foto va video metama'lumotlari</div>
                </div>
                <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${permissions?.media ? 'bg-emerald-500/20 text-emerald-400' : 'bg-rose-500/20 text-rose-400'}`}>
                  {permissions?.media ? 'GRANTED' : 'DENIED'}
                </span>
              </div>

              <div className="p-3 rounded-xl bg-slate-800/40 border border-slate-800 flex items-center justify-between">
                <div>
                  <div className="font-semibold text-white">Aniq Budilnik (SCHEDULE_EXACT_ALARM)</div>
                  <div className="text-[11px] text-slate-400">Uyqu rejimida ham ishlaydi</div>
                </div>
                <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${permissions?.exact_alarm ? 'bg-emerald-500/20 text-emerald-400' : 'bg-rose-500/20 text-rose-400'}`}>
                  {permissions?.exact_alarm ? 'GRANTED' : 'DENIED'}
                </span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB CONTENT: 7. ACTIVITY */}
      {activeTab === 'activity' && (
        <div className="space-y-3">
          {logs.map((log) => (
            <div key={log.id} className="p-4 rounded-xl bg-slate-900 border border-slate-800 text-xs flex items-center justify-between">
              <div>
                <div className="font-semibold text-sm text-purple-300">{log.event_type}</div>
                <div className="text-slate-400 mt-1">
                  {typeof log.event_data === 'string' ? log.event_data : JSON.stringify(log.event_data)}
                </div>
              </div>
              <span className="text-slate-500 text-[11px]">
                {new Date(log.created_at).toLocaleString()}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
