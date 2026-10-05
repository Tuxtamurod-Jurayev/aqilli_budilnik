import React from 'react';
import {
  LayoutDashboard,
  Smartphone,
  MessageSquare,
  PhoneCall,
  Image,
  AlarmClock,
  ShieldCheck,
  History,
  Settings,
  Flame
} from 'lucide-react';

interface SidebarProps {
  currentTab: string;
  onSelectTab: (tab: string) => void;
  onlineCount: number;
  totalDevices: number;
}

export const Sidebar: React.FC<SidebarProps> = ({
  currentTab,
  onSelectTab,
  onlineCount,
  totalDevices
}) => {
  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'devices', label: 'Qurilmalar', icon: Smartphone, badge: `${onlineCount}/${totalDevices}` },
    { id: 'sms', label: 'SMS Xabarlar', icon: MessageSquare },
    { id: 'calls', label: 'Qo\'ng\'iroqlar', icon: PhoneCall },
    { id: 'gallery', label: 'Galereya & Media', icon: Image },
    { id: 'alarms', label: 'Aqlli Budilniklar', icon: AlarmClock },
    { id: 'permissions', label: 'Ruxsatlar Audit', icon: ShieldCheck },
    { id: 'logs', label: 'Faollik Loglari', icon: History },
    { id: 'settings', label: 'Tizim Sozlamalari', icon: Settings },
  ];

  return (
    <aside className="w-64 bg-[#0f172a] border-r border-slate-800 flex flex-col shrink-0">
      {/* Brand Header */}
      <div className="h-16 px-6 flex items-center gap-3 border-b border-slate-800/80">
        <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-purple-600 to-indigo-500 flex items-center justify-center shadow-lg shadow-purple-500/20">
          <AlarmClock className="w-6 h-6 text-white" />
        </div>
        <div>
          <h1 className="font-bold text-base tracking-tight text-white flex items-center gap-1.5">
            Smart Alarm
            <span className="text-[10px] uppercase font-extrabold px-1.5 py-0.5 rounded bg-purple-500/20 text-purple-300 border border-purple-500/30">MDM</span>
          </h1>
          <p className="text-xs text-slate-400">Device Management</p>
        </div>
      </div>

      {/* Navigation Links */}
      <div className="flex-1 py-4 px-3 space-y-1 overflow-y-auto">
        <div className="px-3 pb-2 text-[11px] font-semibold uppercase tracking-wider text-slate-400">
          Asosiy Boshqaruv
        </div>
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = currentTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onSelectTab(item.id)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${
                isActive
                  ? 'bg-purple-600/15 text-purple-400 border border-purple-500/30 shadow-sm'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <div className="flex items-center gap-3">
                <Icon className={`w-4 h-4 ${isActive ? 'text-purple-400' : 'text-slate-400'}`} />
                <span>{item.label}</span>
              </div>
              {item.badge && (
                <span className={`text-[11px] font-semibold px-2 py-0.5 rounded-full ${
                  isActive ? 'bg-purple-500 text-white' : 'bg-slate-800 text-slate-400'
                }`}>
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* System Status Banner */}
      <div className="p-4 border-t border-slate-800/80">
        <div className="p-3 rounded-xl bg-slate-900/80 border border-slate-800 text-xs">
          <div className="flex items-center justify-between mb-1.5">
            <span className="text-slate-400 font-medium flex items-center gap-1.5">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
              Server Ulanishi
            </span>
            <span className="text-emerald-400 font-semibold">Faol</span>
          </div>
          <p className="text-[11px] text-slate-400 leading-relaxed">
            Realtime heartbeat va ma'lumotlar oqimi sinxronlanmoqda.
          </p>
        </div>
      </div>
    </aside>
  );
};
