import React, { useState } from 'react';
import { AlarmClock, Plus, Smartphone, Clock, Volume2, Zap, HelpCircle } from 'lucide-react';
import { Alarm, Device } from '../types';

interface AlarmsPageProps {
  alarms: Alarm[];
  devices: Device[];
  onToggleAlarm: (id: string) => void;
  onCreateAlarm: (alarm: Omit<Alarm, 'id' | 'created_at'>) => void;
}

export const AlarmsPage: React.FC<AlarmsPageProps> = ({
  alarms,
  devices,
  onToggleAlarm,
  onCreateAlarm
}) => {
  const [showModal, setShowModal] = useState(false);
  const [selectedDevice, setSelectedDevice] = useState(devices[0]?.device_id || 'DEVICE-001');
  const [time, setTime] = useState('07:00');
  const [label, setLabel] = useState('Ertalabki uyg\'onish');
  const [difficulty, setDifficulty] = useState<'EASY' | 'MEDIUM' | 'HARD'>('MEDIUM');
  const [questionCount, setQuestionCount] = useState(3);
  const [maxVolume, setMaxVolume] = useState(true);
  const [flashlight, setFlashlight] = useState(true);
  const [vibration, setVibration] = useState(true);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-white font-['Outfit']">
            Aqlli Budilniklar Boshqaruvi
          </h2>
          <p className="text-sm text-slate-400">
            Android qurilmalardagi matematik masalali budilniklarni masofadan monitoring qilish va yuborish
          </p>
        </div>

        <button
          onClick={() => setShowModal(true)}
          className="px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-medium text-sm transition shadow-lg shadow-purple-600/20 flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          <span>Yangi Budilnik Yuborish</span>
        </button>
      </div>

      {/* Alarms Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {alarms.map((alarm) => {
          const device = devices.find(d => d.device_id === alarm.device_id);
          return (
            <div
              key={alarm.id}
              className="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 hover:border-purple-500/40 transition group flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between">
                  <span className="font-mono text-xs font-bold px-2 py-0.5 rounded bg-purple-500/15 text-purple-300 border border-purple-500/25">
                    {alarm.device_id} {device ? `(${device.device_name})` : ''}
                  </span>
                  <button
                    onClick={() => onToggleAlarm(alarm.id)}
                    className={`px-2.5 py-1 rounded-lg text-xs font-bold transition ${
                      alarm.is_enabled
                        ? 'bg-purple-600 text-white'
                        : 'bg-slate-800 text-slate-400 hover:text-white'
                    }`}
                  >
                    {alarm.is_enabled ? 'FAOL' : 'O\'CHIQ'}
                  </button>
                </div>

                <div className="mt-4 flex items-baseline gap-2">
                  <span className="text-3xl font-extrabold font-mono text-white tracking-tight">
                    {alarm.time}
                  </span>
                  <span className="text-xs text-slate-400">{alarm.repeat_days || 'Bir martalik'}</span>
                </div>

                <div className="mt-2 text-sm font-semibold text-slate-200">
                  {alarm.label}
                </div>
              </div>

              {/* Challenge details */}
              <div className="mt-5 pt-3 border-t border-slate-800/80 text-xs text-slate-400 space-y-1.5">
                <div className="flex justify-between items-center">
                  <span className="flex items-center gap-1.5">
                    <HelpCircle className="w-3.5 h-3.5 text-purple-400" />
                    Topshiriq:
                  </span>
                  <span className="font-semibold text-purple-300">
                    {alarm.question_count} ta {alarm.difficulty} misol
                  </span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="flex items-center gap-1.5">
                    <Volume2 className="w-3.5 h-3.5 text-teal-400" />
                    Max Ovoz:
                  </span>
                  <span className="font-medium text-slate-300">{alarm.max_volume ? 'Maksimal (100%)' : 'Standart'}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="flex items-center gap-1.5">
                    <Zap className="w-3.5 h-3.5 text-amber-400" />
                    Stroboskop Fonar:
                  </span>
                  <span className="font-medium text-slate-300">{alarm.flashlight ? 'Yoniq' : 'O\'chiq'}</span>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Modal: Create Remote Alarm */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-lg rounded-2xl bg-slate-900 border border-slate-800 p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="font-bold text-lg text-white font-['Outfit']">
                Masofaviy Budilnik O'rnatish
              </h3>
              <button
                onClick={() => setShowModal(false)}
                className="text-slate-400 hover:text-white text-sm"
              >
                Yopish
              </button>
            </div>

            <div className="space-y-3 text-xs">
              <div>
                <label className="block text-slate-400 mb-1 font-medium">Qabul qiluvchi qurilma</label>
                <select
                  value={selectedDevice}
                  onChange={(e) => setSelectedDevice(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-slate-800 border border-slate-700 text-white focus:outline-none"
                >
                  {devices.map(d => (
                    <option key={d.device_id} value={d.device_id}>{d.device_name} ({d.device_id})</option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-400 mb-1 font-medium">Vaqt (HH:MM)</label>
                  <input
                    type="time"
                    value={time}
                    onChange={(e) => setTime(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl bg-slate-800 border border-slate-700 text-white focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-slate-400 mb-1 font-medium">Qiyinchilik darajasi</label>
                  <select
                    value={difficulty}
                    onChange={(e) => setDifficulty(e.target.value as any)}
                    className="w-full px-3 py-2 rounded-xl bg-slate-800 border border-slate-700 text-white focus:outline-none"
                  >
                    <option value="EASY">Oson</option>
                    <option value="MEDIUM">O'rta</option>
                    <option value="HARD">Qiyin</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-slate-400 mb-1 font-medium">Budilnik eslatma matni</label>
                <input
                  type="text"
                  value={label}
                  onChange={(e) => setLabel(e.target.value)}
                  placeholder="Masalan: Ertalabki mashg'ulot"
                  className="w-full px-3 py-2 rounded-xl bg-slate-800 border border-slate-700 text-white focus:outline-none"
                />
              </div>

              <div className="pt-2 border-t border-slate-800 space-y-2">
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={maxVolume}
                    onChange={(e) => setMaxVolume(e.target.checked)}
                    className="rounded bg-slate-800 border-slate-700 text-purple-600 focus:ring-0"
                  />
                  <span className="text-slate-200">Maksimal tovush balandligi (100% Volume Lock)</span>
                </label>
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={flashlight}
                    onChange={(e) => setFlashlight(e.target.checked)}
                    className="rounded bg-slate-800 border-slate-700 text-purple-600 focus:ring-0"
                  />
                  <span className="text-slate-200">Kamera chirog'i (Stroboskopik Flashlight)</span>
                </label>
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={vibration}
                    onChange={(e) => setVibration(e.target.checked)}
                    className="rounded bg-slate-800 border-slate-700 text-purple-600 focus:ring-0"
                  />
                  <span className="text-slate-200">Kuchli tebranish (Vibration)</span>
                </label>
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-3 border-t border-slate-800">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 rounded-xl text-xs text-slate-400 hover:text-white"
              >
                Bekor qilish
              </button>
              <button
                onClick={() => {
                  onCreateAlarm({
                    device_id: selectedDevice,
                    time,
                    label,
                    is_enabled: true,
                    repeat_days: 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY',
                    difficulty,
                    question_count: questionCount,
                    max_volume: maxVolume,
                    flashlight,
                    vibration
                  });
                  setShowModal(false);
                }}
                className="px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-semibold text-xs transition"
              >
                Qurilmaga Yuborish (FCM / Sync)
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
