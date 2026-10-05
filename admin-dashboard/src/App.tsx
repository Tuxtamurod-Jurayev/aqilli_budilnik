import React, { useState, useEffect } from 'react';
import { Sidebar } from './components/Sidebar';
import { Navbar } from './components/Navbar';
import { DashboardPage } from './pages/DashboardPage';
import { DevicesPage } from './pages/DevicesPage';
import { DeviceDetailPage } from './pages/DeviceDetailPage';
import { SmsPage } from './pages/SmsPage';
import { CallsPage } from './pages/CallsPage';
import { GalleryPage } from './pages/GalleryPage';
import { AlarmsPage } from './pages/AlarmsPage';
import { LogsPage } from './pages/LogsPage';
import { SettingsPage } from './pages/SettingsPage';
import { api, getServerConfig } from './services/api';
import { Device, Permission, Alarm, SmsRecord, CallRecord, MediaRecord, ActivityLog } from './types';

export const App: React.FC = () => {
  const [currentTab, setCurrentTab] = useState<string>('dashboard');
  const [devices, setDevices] = useState<Device[]>([]);
  const [selectedDevice, setSelectedDevice] = useState<Device | null>(null);
  const [devicePermissions, setDevicePermissions] = useState<Permission | null>(null);
  const [allPermissions, setAllPermissions] = useState<Record<string, Permission>>({});
  const [sms, setSms] = useState<SmsRecord[]>([]);
  const [calls, setCalls] = useState<CallRecord[]>([]);
  const [media, setMedia] = useState<MediaRecord[]>([]);
  const [alarms, setAlarms] = useState<Alarm[]>([]);
  const [logs, setLogs] = useState<ActivityLog[]>([]);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const loadData = async () => {
    setIsRefreshing(true);
    try {
      const devList = await api.getDevices();
      setDevices(devList);

      const targetDeviceId = selectedDevice ? selectedDevice.device_id : undefined;

      const [smsList, callList, mediaList, alarmList, logList] = await Promise.all([
        api.getSmsRecords(targetDeviceId),
        api.getCallRecords(targetDeviceId),
        api.getMediaRecords(targetDeviceId),
        api.getAlarms(targetDeviceId),
        api.getActivityLogs(targetDeviceId)
      ]);

      setSms(smsList);
      setCalls(callList);
      setMedia(mediaList);
      setAlarms(alarmList);
      setLogs(logList);

      const permsMap: Record<string, Permission> = {};
      for (const d of devList) {
        const p = await api.getPermissions(d.device_id);
        if (p) permsMap[d.device_id] = p;
      }
      setAllPermissions(permsMap);

      if (selectedDevice) {
        const perms = await api.getPermissions(selectedDevice.device_id);
        setDevicePermissions(perms);
      }
    } catch (e) {
      console.error('Error loading data:', e);
    } finally {
      setIsRefreshing(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [selectedDevice]);

  // Periodic Auto-refresh
  useEffect(() => {
    const config = getServerConfig();
    const interval = setInterval(() => {
      loadData();
    }, config.autoRefreshInterval * 1000);
    return () => clearInterval(interval);
  }, []);

  const handleSelectDevice = async (device: Device | null) => {
    setSelectedDevice(device);
    if (device) {
      const perms = await api.getPermissions(device.device_id);
      setDevicePermissions(perms);
      setCurrentTab('device-detail');
    } else {
      setDevicePermissions(null);
    }
  };

  const handleToggleAlarm = async (id: string) => {
    await api.toggleAlarm(id);
    const updated = await api.getAlarms(selectedDevice ? selectedDevice.device_id : undefined);
    setAlarms(updated);
  };

  const handleCreateAlarm = async (alarm: Omit<Alarm, 'id' | 'created_at'>) => {
    await api.createAlarm(alarm);
    const updated = await api.getAlarms(selectedDevice ? selectedDevice.device_id : undefined);
    setAlarms(updated);
  };

  const onlineCount = devices.filter(d => d.status === 'online').length;

  return (
    <div className="flex h-screen w-screen overflow-hidden bg-[#0b0f19] text-slate-100 font-['Inter',sans-serif]">
      {/* Sidebar Navigation */}
      <Sidebar
        currentTab={currentTab}
        onSelectTab={(tab) => {
          setCurrentTab(tab);
          if (tab !== 'device-detail') {
            // Keep selected device context if desired
          }
        }}
        onlineCount={onlineCount}
        totalDevices={devices.length}
      />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col h-full overflow-hidden">
        {/* Top Navbar */}
        <Navbar
          devices={devices}
          selectedDevice={selectedDevice}
          onSelectDevice={handleSelectDevice}
          onRefresh={loadData}
          isRefreshing={isRefreshing}
        />

        {/* Dynamic Page Views */}
        <main className="flex-1 p-6 overflow-y-auto">
          {currentTab === 'dashboard' && (
            <DashboardPage
              devices={devices}
              sms={sms}
              calls={calls}
              media={media}
              alarms={alarms}
              logs={logs}
              onSelectDevice={handleSelectDevice}
              onNavigateTab={(tab) => setCurrentTab(tab)}
            />
          )}

          {currentTab === 'devices' && (
            <DevicesPage
              devices={devices}
              onSelectDevice={handleSelectDevice}
            />
          )}

          {currentTab === 'device-detail' && selectedDevice && (
            <DeviceDetailPage
              device={selectedDevice}
              permissions={devicePermissions}
              sms={sms}
              calls={calls}
              media={media}
              alarms={alarms}
              logs={logs}
              onBack={() => setCurrentTab('devices')}
              onToggleAlarm={handleToggleAlarm}
              onCreateAlarm={handleCreateAlarm}
            />
          )}

          {currentTab === 'sms' && (
            <SmsPage sms={sms} devices={devices} />
          )}

          {currentTab === 'calls' && (
            <CallsPage calls={calls} devices={devices} />
          )}

          {currentTab === 'gallery' && (
            <GalleryPage media={media} devices={devices} />
          )}

          {currentTab === 'alarms' && (
            <AlarmsPage
              alarms={alarms}
              devices={devices}
              onToggleAlarm={handleToggleAlarm}
              onCreateAlarm={handleCreateAlarm}
            />
          )}

          {currentTab === 'permissions' && (
            <div className="space-y-4">
              <h2 className="text-2xl font-bold tracking-tight text-white font-['Outfit']">
                Barcha Qurilmalar Ruxsatlar Auditi
              </h2>
              <p className="text-sm text-slate-400">
                Foydalanuvchilar tomonidan tasdiqlangan SMS, Qo'ng'iroqlar, Galereya va Tizim ruxsatlari ro'yxati
              </p>
              <div className="rounded-2xl bg-slate-900 border border-slate-800 overflow-hidden">
                <table className="w-full text-left text-xs">
                  <thead>
                    <tr className="border-b border-slate-800 text-slate-400 uppercase tracking-wider text-[11px] bg-slate-950/40">
                      <th className="p-4">Qurilma</th>
                      <th className="p-4">Device ID</th>
                      <th className="p-4">SMS</th>
                      <th className="p-4">Qo'ng'iroqlar</th>
                      <th className="p-4">Galereya</th>
                      <th className="p-4">Aniq Budilnik</th>
                      <th className="p-4">Batareya Cheklovi</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800/60">
                    {devices.map(d => {
                      const p = allPermissions[d.device_id];
                      return (
                        <tr
                          key={d.device_id}
                          className="hover:bg-slate-800/30 transition cursor-pointer"
                          onClick={() => handleSelectDevice(d)}
                        >
                          <td className="p-4 font-semibold text-white">{d.device_name}</td>
                          <td className="p-4 font-mono text-purple-400">{d.device_id}</td>
                          <td className="p-4">
                            <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${p?.sms ? 'bg-emerald-500/20 text-emerald-400' : 'bg-rose-500/20 text-rose-400'}`}>
                              {p?.sms ? 'GRANTED' : 'DENIED'}
                            </span>
                          </td>
                          <td className="p-4">
                            <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${p?.call_log ? 'bg-emerald-500/20 text-emerald-400' : 'bg-rose-500/20 text-rose-400'}`}>
                              {p?.call_log ? 'GRANTED' : 'DENIED'}
                            </span>
                          </td>
                          <td className="p-4">
                            <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${p?.media ? 'bg-emerald-500/20 text-emerald-400' : 'bg-rose-500/20 text-rose-400'}`}>
                              {p?.media ? 'GRANTED' : 'DENIED'}
                            </span>
                          </td>
                          <td className="p-4">
                            <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${p?.exact_alarm ? 'bg-emerald-500/20 text-emerald-400' : 'bg-rose-500/20 text-rose-400'}`}>
                              {p?.exact_alarm ? 'GRANTED' : 'DENIED'}
                            </span>
                          </td>
                          <td className="p-4">
                            <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${p?.battery_optimization_ignored ? 'bg-emerald-500/20 text-emerald-400' : 'bg-amber-500/20 text-amber-400'}`}>
                              {p?.battery_optimization_ignored ? 'IGNORED' : 'RESTRICTED'}
                            </span>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {currentTab === 'logs' && (
            <LogsPage logs={logs} devices={devices} />
          )}

          {currentTab === 'settings' && (
            <SettingsPage />
          )}
        </main>
      </div>
    </div>
  );
};
