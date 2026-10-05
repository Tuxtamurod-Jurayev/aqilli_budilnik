import { Device, Permission, Alarm, SmsRecord, CallRecord, MediaRecord, ActivityLog } from '../types';

// Cleared mock data: real connected devices (e.g. Poco, etc.) will be dynamically registered and displayed
export const initialDevices: Device[] = [];
export const initialPermissions: Record<string, Permission> = {};
export const initialSms: SmsRecord[] = [];
export const initialCalls: CallRecord[] = [];
export const initialMedia: MediaRecord[] = [];
export const initialAlarms: Alarm[] = [];
export const initialLogs: ActivityLog[] = [];
