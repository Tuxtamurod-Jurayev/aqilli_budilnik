export interface Device {
  id: string;
  device_id: string;
  device_name: string;
  manufacturer: string;
  model: string;
  android_version: string;
  app_version: string;
  battery: number;
  is_charging: boolean;
  network_status: string; // 'Wi-Fi' | 'Cellular' | 'Offline'
  status: 'online' | 'offline';
  last_seen: string;
  registered_at: string;
}

export interface Permission {
  id: string;
  device_id: string;
  sms: boolean;
  call_log: boolean;
  media: boolean;
  notifications: boolean;
  exact_alarm: boolean;
  camera: boolean;
  battery_optimization_ignored: boolean;
  updated_at: string;
}

export interface Alarm {
  id: string;
  device_id: string;
  remote_id?: number;
  time: string; // "07:30"
  label: string;
  is_enabled: boolean;
  repeat_days: string;
  difficulty: 'EASY' | 'MEDIUM' | 'HARD';
  question_count: number;
  max_volume: boolean;
  flashlight: boolean;
  vibration: boolean;
  created_at: string;
}

export interface SmsRecord {
  id: string;
  device_id: string;
  address: string;
  message: string;
  type: 'INCOMING' | 'OUTGOING' | 'DRAFT';
  timestamp: number;
  formatted_date?: string;
}

export interface CallRecord {
  id: string;
  device_id: string;
  number: string;
  name?: string;
  type: 'INCOMING' | 'OUTGOING' | 'MISSED' | 'REJECTED';
  duration: number; // in seconds
  timestamp: number;
  formatted_date?: string;
}

export interface MediaRecord {
  id: string;
  device_id: string;
  file_name: string;
  file_path: string;
  media_type: string;
  size: number; // bytes
  date_added: number;
  thumbnail_url?: string;
}

export interface ActivityLog {
  id: string;
  device_id: string;
  event_type: string;
  event_data: string | Record<string, any>;
  created_at: string;
}

export interface RemoteCommand {
  id: string;
  device_id: string;
  command: 'RING_ALARM' | 'STOP_ALARM' | 'TOGGLE_FLASHLIGHT' | 'VIBRATE' | 'FORCE_SYNC' | 'MATH_CHALLENGE';
  payload?: any;
  status: 'PENDING' | 'EXECUTED' | 'FAILED';
  result?: string;
  created_at: string;
}
