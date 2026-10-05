import { Device, Permission, Alarm, SmsRecord, CallRecord, MediaRecord, ActivityLog } from '../types';
import {
  initialDevices,
  initialPermissions,
  initialSms,
  initialCalls,
  initialMedia,
  initialAlarms,
  initialLogs
} from './mockData';

// Local storage keys
const KEY_DEVICES = 'sa_admin_devices';
const KEY_ALARMS = 'sa_admin_alarms';
const KEY_SMS = 'sa_admin_sms';
const KEY_CALLS = 'sa_admin_calls';
const KEY_MEDIA = 'sa_admin_media';
const KEY_CONFIG = 'sa_admin_config';

export interface ServerConfig {
  supabaseUrl: string;
  supabaseAnonKey: string;
  autoRefreshInterval: number; // in seconds
}

export const getServerConfig = (): ServerConfig => {
  const saved = localStorage.getItem(KEY_CONFIG);
  if (saved) {
    try {
      return JSON.parse(saved);
    } catch (e) {
      console.error(e);
    }
  }
  return {
    supabaseUrl: 'https://your-project.supabase.co',
    supabaseAnonKey: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.dummy_anon_key',
    autoRefreshInterval: 15
  };
};

export const saveServerConfig = (config: ServerConfig) => {
  localStorage.setItem(KEY_CONFIG, JSON.stringify(config));
};

// Data service with fallback to local store & Supabase REST capability
export const api = {
  getDevices: async (): Promise<Device[]> => {
    const saved = localStorage.getItem(KEY_DEVICES);
    if (saved) return JSON.parse(saved);
    localStorage.setItem(KEY_DEVICES, JSON.stringify(initialDevices));
    return initialDevices;
  },

  getDeviceById: async (deviceId: string): Promise<Device | undefined> => {
    const devices = await api.getDevices();
    return devices.find(d => d.device_id === deviceId);
  },

  getPermissions: async (deviceId: string): Promise<Permission | null> => {
    return initialPermissions[deviceId] || {
      id: 'p-default',
      device_id: deviceId,
      sms: false,
      call_log: false,
      media: false,
      notifications: true,
      exact_alarm: true,
      camera: false,
      battery_optimization_ignored: false,
      updated_at: new Date().toISOString()
    };
  },

  getSmsRecords: async (deviceId?: string): Promise<SmsRecord[]> => {
    const saved = localStorage.getItem(KEY_SMS);
    const list: SmsRecord[] = saved ? JSON.parse(saved) : initialSms;
    if (deviceId) {
      return list.filter(item => item.device_id === deviceId);
    }
    return list;
  },

  getCallRecords: async (deviceId?: string): Promise<CallRecord[]> => {
    const saved = localStorage.getItem(KEY_CALLS);
    const list: CallRecord[] = saved ? JSON.parse(saved) : initialCalls;
    if (deviceId) {
      return list.filter(item => item.device_id === deviceId);
    }
    return list;
  },

  getMediaRecords: async (deviceId?: string): Promise<MediaRecord[]> => {
    const saved = localStorage.getItem(KEY_MEDIA);
    const list: MediaRecord[] = saved ? JSON.parse(saved) : initialMedia;
    if (deviceId) {
      return list.filter(item => item.device_id === deviceId);
    }
    return list;
  },

  getAlarms: async (deviceId?: string): Promise<Alarm[]> => {
    const saved = localStorage.getItem(KEY_ALARMS);
    const list: Alarm[] = saved ? JSON.parse(saved) : initialAlarms;
    if (deviceId) {
      return list.filter(item => item.device_id === deviceId);
    }
    return list;
  },

  createAlarm: async (alarm: Omit<Alarm, 'id' | 'created_at'>): Promise<Alarm> => {
    const list = await api.getAlarms();
    const newAlarm: Alarm = {
      ...alarm,
      id: `a-${Date.now()}`,
      created_at: new Date().toISOString()
    };
    const updated = [newAlarm, ...list];
    localStorage.setItem(KEY_ALARMS, JSON.stringify(updated));
    return newAlarm;
  },

  toggleAlarm: async (id: string): Promise<void> => {
    const list = await api.getAlarms();
    const updated = list.map(a => a.id === id ? { ...a, is_enabled: !a.is_enabled } : a);
    localStorage.setItem(KEY_ALARMS, JSON.stringify(updated));
  },

  getActivityLogs: async (deviceId?: string): Promise<ActivityLog[]> => {
    if (deviceId) {
      return initialLogs.filter(l => l.device_id === deviceId);
    }
    return initialLogs;
  }
};
