import React, { useState } from 'react';
import { Settings, Save, Server, Shield, Database, RefreshCw } from 'lucide-react';
import { getServerConfig, saveServerConfig, ServerConfig } from '../services/api';

export const SettingsPage: React.FC = () => {
  const [config, setConfig] = useState<ServerConfig>(getServerConfig());
  const [savedMessage, setSavedMessage] = useState(false);

  const handleSave = () => {
    saveServerConfig(config);
    setSavedMessage(true);
    setTimeout(() => setSavedMessage(false), 2500);
  };

  return (
    <div className="space-y-6 max-w-4xl">
      {/* Header */}
      <div>
        <h2 className="text-2xl font-bold tracking-tight text-white font-['Outfit']">
          Tizim va Backend Sozlamalari
        </h2>
        <p className="text-sm text-slate-400">
          Supabase PostgreSQL ulanish parametrlari, API kalitlari va avto-yangilanish sozlamalari
        </p>
      </div>

      {/* Supabase Connection Config */}
      <div className="p-6 rounded-2xl bg-slate-900/90 border border-slate-800 space-y-5">
        <div className="flex items-center gap-3 pb-4 border-b border-slate-800">
          <div className="w-10 h-10 rounded-xl bg-purple-500/10 text-purple-400 flex items-center justify-center">
            <Server className="w-5 h-5" />
          </div>
          <div>
            <h4 className="font-bold text-sm text-white">Supabase Loyihasi Ulanishi</h4>
            <p className="text-xs text-slate-400">Android APK va Admin Web Panel bir xil backendga ulanadi</p>
          </div>
        </div>

        <div className="space-y-4 text-xs">
          <div>
            <label className="block text-slate-300 font-medium mb-1.5">
              Supabase Project URL
            </label>
            <input
              type="text"
              value={config.supabaseUrl}
              onChange={(e) => setConfig({ ...config, supabaseUrl: e.target.value })}
              placeholder="https://xyzcompany.supabase.co"
              className="w-full px-3.5 py-2.5 rounded-xl bg-slate-800 border border-slate-700 text-white focus:outline-none focus:border-purple-500 text-xs font-mono"
            />
            <span className="text-[11px] text-slate-400 mt-1 block">
              Supabase boshqaruv panelidagi Project Settings &rarr; API bo'limidan olinadi.
            </span>
          </div>

          <div>
            <label className="block text-slate-300 font-medium mb-1.5">
              Anon / Public API Key
            </label>
            <textarea
              rows={3}
              value={config.supabaseAnonKey}
              onChange={(e) => setConfig({ ...config, supabaseAnonKey: e.target.value })}
              placeholder="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
              className="w-full px-3.5 py-2.5 rounded-xl bg-slate-800 border border-slate-700 text-white focus:outline-none focus:border-purple-500 text-xs font-mono"
            />
            <span className="text-[11px] text-slate-400 mt-1 block">
              Mijoz tomoni (Android va Web) uchun mo'ljallangan RLS himoyalangan public anon kaliti.
            </span>
          </div>

          <div>
            <label className="block text-slate-300 font-medium mb-1.5">
              Realtime Yangilanish Oralig'i (Soniya)
            </label>
            <input
              type="number"
              min={5}
              max={120}
              value={config.autoRefreshInterval}
              onChange={(e) => setConfig({ ...config, autoRefreshInterval: parseInt(e.target.value) || 15 })}
              className="w-48 px-3.5 py-2.5 rounded-xl bg-slate-800 border border-slate-700 text-white focus:outline-none focus:border-purple-500 text-xs font-mono"
            />
          </div>
        </div>

        <div className="pt-4 border-t border-slate-800 flex items-center justify-between">
          <div>
            {savedMessage && (
              <span className="text-xs font-semibold text-emerald-400 flex items-center gap-1.5 animate-pulse">
                ✓ Sozlamalar muvaffaqiyatli saqlandi!
              </span>
            )}
          </div>
          <button
            onClick={handleSave}
            className="px-5 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-semibold text-xs transition shadow-lg shadow-purple-600/20 flex items-center gap-2"
          >
            <Save className="w-4 h-4" />
            <span>Saqlash</span>
          </button>
        </div>
      </div>

      {/* SQL Migration Scripts Reference */}
      <div className="p-6 rounded-2xl bg-slate-900/90 border border-slate-800 space-y-3 text-xs">
        <div className="flex items-center gap-3">
          <Database className="w-5 h-5 text-teal-400" />
          <h4 className="font-bold text-sm text-white">Database SQL Sxemasi</h4>
        </div>
        <p className="text-slate-400 leading-relaxed">
          Ushbu tizimning to'liq ma'lumotlar bazasi sxemasi loyihaning <code className="text-purple-300 bg-slate-800 px-1.5 py-0.5 rounded">supabase/schema.sql</code> faylida tayyorlangan. Unda <code className="text-slate-300">devices</code>, <code className="text-slate-300">permissions</code>, <code className="text-slate-300">sms_records</code>, <code className="text-slate-300">call_records</code>, <code className="text-slate-300">media_records</code>, <code className="text-slate-300">alarms</code> jadvallari va Row Level Security (RLS) qoidalari mavjud.
        </p>
      </div>
    </div>
  );
};
