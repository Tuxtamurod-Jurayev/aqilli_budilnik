import { Device, Permission, Alarm, SmsRecord, CallRecord, MediaRecord, ActivityLog } from '../types';

export const initialDevices: Device[] = [
  {
    id: 'd1',
    device_id: 'DEVICE-001',
    device_name: 'Samsung Galaxy A55',
    manufacturer: 'Samsung',
    model: 'SM-A556B',
    android_version: 'Android 15 (API 35)',
    app_version: '1.0.0',
    battery: 84,
    is_charging: true,
    network_status: 'Wi-Fi',
    status: 'online',
    last_seen: new Date(Date.now() - 2 * 60 * 1000).toISOString(),
    registered_at: new Date(Date.now() - 5 * 24 * 3600 * 1000).toISOString(),
  },
  {
    id: 'd2',
    device_id: 'DEVICE-002',
    device_name: 'Xiaomi Redmi Note 13 Pro',
    manufacturer: 'Xiaomi',
    model: '23117RA68G',
    android_version: 'Android 14 (API 34)',
    app_version: '1.0.0',
    battery: 62,
    is_charging: false,
    network_status: 'Cellular',
    status: 'online',
    last_seen: new Date(Date.now() - 8 * 60 * 1000).toISOString(),
    registered_at: new Date(Date.now() - 3 * 24 * 3600 * 1000).toISOString(),
  },
  {
    id: 'd3',
    device_id: 'DEVICE-003',
    device_name: 'Google Pixel 8 Pro',
    manufacturer: 'Google',
    model: 'Pixel 8 Pro',
    android_version: 'Android 15 (API 35)',
    app_version: '1.0.0',
    battery: 19,
    is_charging: false,
    network_status: 'Offline',
    status: 'offline',
    last_seen: new Date(Date.now() - 140 * 60 * 1000).toISOString(),
    registered_at: new Date(Date.now() - 10 * 24 * 3600 * 1000).toISOString(),
  },
  {
    id: 'd4',
    device_id: 'DEVICE-004',
    device_name: 'Honor Magic 6 Lite',
    manufacturer: 'Honor',
    model: 'ALI-NX1',
    android_version: 'Android 14 (API 34)',
    app_version: '1.0.0',
    battery: 95,
    is_charging: true,
    network_status: 'Wi-Fi',
    status: 'online',
    last_seen: new Date(Date.now() - 1 * 60 * 1000).toISOString(),
    registered_at: new Date(Date.now() - 1 * 24 * 3600 * 1000).toISOString(),
  }
];

export const initialPermissions: Record<string, Permission> = {
  'DEVICE-001': {
    id: 'p1',
    device_id: 'DEVICE-001',
    sms: true,
    call_log: true,
    media: true,
    notifications: true,
    exact_alarm: true,
    camera: true,
    battery_optimization_ignored: true,
    updated_at: new Date().toISOString()
  },
  'DEVICE-002': {
    id: 'p2',
    device_id: 'DEVICE-002',
    sms: true,
    call_log: false,
    media: true,
    notifications: true,
    exact_alarm: true,
    camera: true,
    battery_optimization_ignored: false,
    updated_at: new Date().toISOString()
  }
};

export const initialSms: SmsRecord[] = [
  {
    id: 'sms-1',
    device_id: 'DEVICE-001',
    address: '+998901234567',
    message: 'Assalomu alaykum, bugun soat 10:00 da uchrashuv bor.',
    type: 'INCOMING',
    timestamp: Date.now() - 25 * 60 * 1000,
  },
  {
    id: 'sms-2',
    device_id: 'DEVICE-001',
    address: '+998901234567',
    message: 'Va alaykum assalom, albatta bordim.',
    type: 'OUTGOING',
    timestamp: Date.now() - 20 * 60 * 1000,
  },
  {
    id: 'sms-3',
    device_id: 'DEVICE-001',
    address: 'PAYME',
    message: 'Karta hisobingizga 500,000 UZS qabul qilindi. Balans: 1,840,000 UZS.',
    type: 'INCOMING',
    timestamp: Date.now() - 120 * 60 * 1000,
  },
  {
    id: 'sms-4',
    device_id: 'DEVICE-002',
    address: '+998939876543',
    message: 'Hujjatlarni pochtaga yubordim, tekshirib bering.',
    type: 'INCOMING',
    timestamp: Date.now() - 60 * 60 * 1000,
  }
];

export const initialCalls: CallRecord[] = [
  {
    id: 'call-1',
    device_id: 'DEVICE-001',
    number: '+998901234567',
    name: 'Alisher Karimov',
    type: 'INCOMING',
    duration: 145,
    timestamp: Date.now() - 35 * 60 * 1000,
  },
  {
    id: 'call-2',
    device_id: 'DEVICE-001',
    number: '+998998887766',
    name: 'Dilshod Ish',
    type: 'OUTGOING',
    duration: 320,
    timestamp: Date.now() - 95 * 60 * 1000,
  },
  {
    id: 'call-3',
    device_id: 'DEVICE-001',
    number: '+998945554433',
    name: 'Noma\'lum',
    type: 'MISSED',
    duration: 0,
    timestamp: Date.now() - 180 * 60 * 1000,
  },
  {
    id: 'call-4',
    device_id: 'DEVICE-002',
    number: '+998912223344',
    name: 'Boshqaruv',
    type: 'INCOMING',
    duration: 78,
    timestamp: Date.now() - 50 * 60 * 1000,
  }
];

export const initialMedia: MediaRecord[] = [
  {
    id: 'm-1',
    device_id: 'DEVICE-001',
    file_name: 'IMG_20261002_143021.jpg',
    file_path: '/storage/emulated/0/DCIM/Camera/IMG_20261002_143021.jpg',
    media_type: 'image/jpeg',
    size: 3421500,
    date_added: Date.now() - 4 * 3600 * 1000,
  },
  {
    id: 'm-2',
    device_id: 'DEVICE-001',
    file_name: 'VID_20261001_180210.mp4',
    file_path: '/storage/emulated/0/DCIM/Camera/VID_20261001_180210.mp4',
    media_type: 'video/mp4',
    size: 28410000,
    date_added: Date.now() - 24 * 3600 * 1000,
  },
  {
    id: 'm-3',
    device_id: 'DEVICE-001',
    file_name: 'Screenshot_20261002_091522.png',
    file_path: '/storage/emulated/0/Pictures/Screenshots/Screenshot_20261002_091522.png',
    media_type: 'image/png',
    size: 1120000,
    date_added: Date.now() - 8 * 3600 * 1000,
  }
];

export const initialAlarms: Alarm[] = [
  {
    id: 'a-1',
    device_id: 'DEVICE-001',
    time: '07:00',
    label: 'Ertalabki uyg\'onish',
    is_enabled: true,
    repeat_days: 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY',
    difficulty: 'MEDIUM',
    question_count: 3,
    max_volume: true,
    flashlight: true,
    vibration: true,
    created_at: new Date().toISOString()
  },
  {
    id: 'a-2',
    device_id: 'DEVICE-001',
    time: '08:30',
    label: 'Uchrashuvga tayyorgarlik',
    is_enabled: false,
    repeat_days: 'SATURDAY,SUNDAY',
    difficulty: 'EASY',
    question_count: 3,
    max_volume: true,
    flashlight: false,
    vibration: true,
    created_at: new Date().toISOString()
  }
];

export const initialLogs: ActivityLog[] = [
  {
    id: 'l-1',
    device_id: 'DEVICE-001',
    event_type: 'SYNC_HEARTBEAT',
    event_data: { battery: 84, network: 'Wi-Fi', status: 'online' },
    created_at: new Date(Date.now() - 2 * 60 * 1000).toISOString()
  },
  {
    id: 'l-2',
    device_id: 'DEVICE-001',
    event_type: 'PERMISSION_GRANTED',
    event_data: { permission: 'READ_SMS', status: 'granted' },
    created_at: new Date(Date.now() - 60 * 60 * 1000).toISOString()
  },
  {
    id: 'l-3',
    device_id: 'DEVICE-001',
    event_type: 'ALARM_DISMISSED',
    event_data: { alarm_time: '07:00', math_solved: true, mistakes: 0 },
    created_at: new Date(Date.now() - 14 * 3600 * 1000).toISOString()
  }
];
