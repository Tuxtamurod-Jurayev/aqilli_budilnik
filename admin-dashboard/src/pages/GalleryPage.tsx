import React, { useState } from 'react';
import { Image, Video, Search, Smartphone, Folder } from 'lucide-react';
import { MediaRecord, Device } from '../types';

interface GalleryPageProps {
  media: MediaRecord[];
  devices: Device[];
}

export const GalleryPage: React.FC<GalleryPageProps> = ({ media, devices }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedDevice, setSelectedDevice] = useState<string>('all');
  const [typeFilter, setTypeFilter] = useState<'ALL' | 'IMAGE' | 'VIDEO'>('ALL');

  const filtered = media.filter((m) => {
    const matchesSearch = m.file_name.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesDevice = selectedDevice === 'all' || m.device_id === selectedDevice;
    const isVid = m.media_type.includes('video');
    const matchesType = typeFilter === 'ALL' || (typeFilter === 'VIDEO' ? isVid : !isVid);
    return matchesSearch && matchesDevice && matchesType;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-white font-['Outfit']">
            Galereya va Media Explorer
          </h2>
          <p className="text-sm text-slate-400">
            Foydalanuvchi ruxsat bergan fotosuratlar va videolar metama'lumotlari
          </p>
        </div>

        {/* Filter controls */}
        <div className="flex flex-wrap items-center gap-3">
          <div className="relative">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Fayl nomini qidirish..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-9 pr-4 py-2 bg-slate-900 border border-slate-800 rounded-xl text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500 w-52"
            />
          </div>

          <select
            value={selectedDevice}
            onChange={(e) => setSelectedDevice(e.target.value)}
            className="px-3 py-2 bg-slate-900 border border-slate-800 rounded-xl text-xs text-slate-300 focus:outline-none"
          >
            <option value="all">Barcha Qurilmalar</option>
            {devices.map(d => (
              <option key={d.device_id} value={d.device_id}>{d.device_name} ({d.device_id})</option>
            ))}
          </select>

          <div className="flex items-center bg-slate-900 border border-slate-800 rounded-xl p-1 text-xs">
            {(['ALL', 'IMAGE', 'VIDEO'] as const).map(t => (
              <button
                key={t}
                onClick={() => setTypeFilter(t)}
                className={`px-3 py-1 rounded-lg font-medium transition ${
                  typeFilter === t ? 'bg-purple-600 text-white' : 'text-slate-400 hover:text-white'
                }`}
              >
                {t === 'ALL' ? 'Barchasi' : t === 'IMAGE' ? 'Rasmlar' : 'Videolar'}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Media Grid */}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {filtered.length === 0 ? (
          <div className="col-span-full p-12 text-center text-slate-400 text-xs">
            Media fayllari topilmadi.
          </div>
        ) : (
          filtered.map((item) => {
            const isVideo = item.media_type.includes('video');
            return (
              <div
                key={item.id}
                className="p-3 rounded-2xl bg-slate-900/90 border border-slate-800 hover:border-purple-500/40 transition group flex flex-col justify-between"
              >
                <div className="w-full h-36 rounded-xl bg-slate-800/80 flex items-center justify-center text-slate-500 relative overflow-hidden mb-3">
                  {isVideo ? (
                    <Video className="w-10 h-10 text-purple-400/80" />
                  ) : (
                    <Image className="w-10 h-10 text-slate-500" />
                  )}
                  <span className="absolute top-2 left-2 text-[10px] font-mono font-bold px-1.5 py-0.5 rounded bg-black/70 text-purple-300">
                    {item.device_id}
                  </span>
                  <span className="absolute bottom-2 right-2 text-[9px] font-mono px-1.5 py-0.5 rounded bg-black/70 text-slate-200">
                    {isVideo ? 'MP4' : 'JPEG'}
                  </span>
                </div>

                <div>
                  <h5 className="font-semibold text-xs text-white truncate" title={item.file_name}>
                    {item.file_name}
                  </h5>
                  <div className="flex items-center justify-between text-[11px] text-slate-400 mt-1">
                    <span>{(item.size / 1024 / 1024).toFixed(2)} MB</span>
                    <span>{new Date(item.date_added).toLocaleDateString()}</span>
                  </div>
                  <div className="text-[10px] text-slate-400 truncate mt-1" title={item.file_path}>
                    {item.file_path}
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
