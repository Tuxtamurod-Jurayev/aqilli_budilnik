# ⏰ Smart Alarm — Android Qurilma Monitoring va Aqlli Budilnik Tizimi

**Smart Alarm + Device Management** — bu foydalanuvchi roziligi (consent) asosida ishlaydigan, yuqori ishonchlilikka ega **Aqlli Budilnik** va **Android Qurilma Monitoringi (MDM) tizimi**.

Ushbu platforma 4 ta asosiy qismdan iborat:
1. **Android Ilova (Kotlin + Jetpack Compose + Room + WorkManager)**
2. **Backend Ma'lumotlar Bazasi (Supabase / PostgreSQL SQL sxemasi + RLS)**
3. **Administrator Web Paneli (React + TypeScript + Vite + Tailwind CSS)**
4. **Offline First va Ikki tomonlama Sinxronizatsiya Mexanizmi**

---

## 📑 Mundarija

1. [Tizim Arxitekturasi](#-tizim-arxitekturasi)
2. [Android APK Imkoniyatlari](#-android-apk-imkoniyatlari)
   - [2.1. Aqlli Budilnik (Math Engine)](#21-aqlli-budilnik-math-engine)
   - [2.2. Qurilma Telemetriyasi va Monitoring](#22-qurilma-telemetriyasi-va-monitoring)
   - [2.3. Ruxsatlar Markazi (Permission Manager)](#23-ruxsatlar-markazi-permission-manager)
   - [2.4. Fon Sinxronizatsiyasi (WorkManager + Supabase REST)](#24-fon-sinxronizatsiyasi-workmanager--supabase-rest)
3. [Supabase Ma'lumotlar Bazasi Sxemasi](#-supabase-malumotlar-bazasi-sxemasi)
4. [Administrator Web Paneli (React + Vite)](#-administrator-web-paneli-react--vite)
5. [Loyiha Fayllari Tuzilishi](#-loyiha-fayllari-tuzilishi)
6. [Ishga Tushirish va O‘rnatish Qo‘llanmasi](#-ishga-tushirish-va-ornatish-qollanmasi)

---

## 🏛 Tizim Arxitekturasi

```text
                     SMART ALARM SERVER
                            │
               ┌────────────┴────────────┐
               │                         │
        ADMIN WEB PANEL             SUPABASE DB
     (React + TypeScript)      (PostgreSQL + RLS)
               │                         │
               └────────────┬────────────┘
                            │
                   HTTPS REST API / FCM
                            │
               ┌────────────┴────────────┐
               │                         │
          PHONE 1                   PHONE 2
        (DEVICE-001)              (DEVICE-002)
         ┌─────┼─────┐             ┌─────┼─────┐
         │     │     │             │     │     │
       Alarm  SMS  Calls         Alarm  SMS  Calls
         │                         │
      Gallery                   Gallery
```

---

## 📱 Android APK Imkoniyatlari

### 2.1. Aqlli Budilnik (Math Engine)
- **O'chirish uchun 3 ta misol:** Ertalab signal chalinganda uni o'chirish uchun ketma-ket 3 ta matematik misolni to'g'ri yechish talab etiladi.
- **Maksimal Ovoz (100% Volume Lock):** Tizimdagi audio oqimini maksimal balandlikka ko'taradi va pasaytirishga yo'l qo'ymaydi.
- **Stroboskopik Fonar:** Kamera chirog'i (Flashlight) miltillab xonani yoritadi.
- **Lock Screen integratsiyasi:** Telefon qulflangan bo'lsa ham ekranni uyg'otadi.
- **Reboot himoyasi:** Telefon o'chib-yonsa (`BOOT_COMPLETED`), signallar qayta tiklanadi.

### 2.2. Qurilma Telemetriyasi va Monitoring
- **Avtomatik Device ID:** Har bir qurilmaga maxsus identifikator beriladi (`DEVICE-XXXXXX`).
- **Real-time Telemetriya:** Qurilma modeli, Android OS versiyasi, Ilova versiyasi, Batareya quvvati (%), Zaryadlanish holati va Tarmoq turi (Wi-Fi/Cellular/Offline).
- **Heartbeat Sinxronizatsiyasi:** Markaziy serverga har 15 daqiqada yoki foydalanuvchi talabiga ko'ra on-demand heartbeat yuboriladi.

### 2.3. Ruxsatlar Markazi (Permission Manager)
Foydalanuvchining shaffof roziligi asosida har bir modul alohida boshqariladi:
- **SMS xabarlari (`READ_SMS`):** Kiruvchi va chiquvchi SMS ma'lumotlarini serverga sinxronlash.
- **Qo'ng'iroqlar tarixi (`READ_CALL_LOG`):** Kiruvchi, chiquvchi va o'tkazib yuborilgan qo'ng'iroqlar.
- **Galereya va Media (`READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO`):** Rasm va video metama'lumotlari.
- **Aniq Budilnik (`SCHEDULE_EXACT_ALARM`):** Doze rejimida ham soniyasigacha aniq uyg'otish.
- **Kamera va Chiroq (`CAMERA`):** Stroboskopik chiroq.
- **Batareya optimizatsiyasi:** Ilovaning fonda to'xtab qolmasligi.

### 2.4. Fon Sinxronizatsiyasi (WorkManager + Supabase REST)
- `SyncWorker`: Tarmoq paydo bo'lganda yangi SMSlar, qo'ng'iroqlar, media metama'lumotlari va faollik loglarini xavfsiz JSON ko'rinishida Supabase REST API orqali uzatadi.
- **Offline First:** Internet bo'lmasa, budilnik va mahalliy monitoring uzluksiz ishlaydi. Aloqa tiklangach, to'plangan ma'lumotlar avtomatik uzatiladi.

---

## 🗄 Supabase Ma'lumotlar Bazasi Sxemasi

Barcha jadvallar `supabase/schema.sql` faylida to'liq yozilgan:
- `devices`: Barcha ro'yxatdan o'tgan telefonlar, batareya, status, last seen.
- `permissions`: Qurilma ruxsatlari auditi.
- `alarms`: Masofaviy va mahalliy budilniklar (ikki tomonlama sinxron).
- `sms_records`: Kiruvchi va chiquvchi SMSlar.
- `call_records`: Qo'ng'iroqlar tarixi va davomiyligi.
- `media_records`: Galereya fayllari metama'lumotlari.
- `activity_logs`: Xavfsizlik va audit loglari.
- **Row Level Security (RLS)** qoidalari va Realtime stream tayyorlangan.

---

## 💻 Administrator Web Paneli (React + Vite)

`admin-dashboard/` papkasida zamonaviy qorong'i rejimdagi (Dark Mode) boshqaruv paneli:
- **Dashboard:** Jami qurilmalar, online/offline, batareya ko'rsatkichlari, umumiy statistika.
- **Qurilmalar (Devices):** Qidiruv, filtr va qurilma kartalari.
- **Device Details (7 ta Tab):**
  - *Overview:* Tizim telemetriyasi, batareya va tarmoq.
  - *SMS:* Qidiruv, kiruvchi/chiquvchi filtri va xabarlar oqimi.
  - *Qo'ng'iroqlar:* Qo'ng'iroq turlari, davomiyligi va vaqti.
  - *Galereya:* Rasmlar va videolar metama'lumotlari.
  - *Budilniklar:* Qurilmaga masofadan budilnik o'rnatish (masala qiyinligi, ovoz, chiroq).
  - *Ruxsatlar:* Qurilma ruxsatlari auditi.
  - *Faollik Tarixi:* Audit trail.
- **Tizim Sozlamalari:** Supabase URL, Anon Key va auto-refresh boshqaruvi.

---

## 📁 Loyiha Fayllari Tuzilishi

```text
kalkulyator/
├── AqlliBudilnik-debug.apk     # Tayyor yangilangan Android o'rnatish fayli
├── supabase/
│   └── schema.sql              # Supabase PostgreSQL to'liq SQL migratsiyasi
├── admin-dashboard/            # React + TypeScript + Vite + Tailwind CSS Admin Panel
│   ├── src/
│   │   ├── components/         # Sidebar, Navbar, Cardlar
│   │   ├── pages/              # Dashboard, Devices, DeviceDetail, SMS, Calls, Gallery, Alarms, Logs
│   │   ├── services/           # API va Mock ma'lumotlar
│   │   ├── types.ts            # TypeScript interfeyslari
│   │   └── App.tsx
│   └── package.json
└── app/
    ├── src/main/AndroidManifest.xml
    └── src/main/java/uz/smartalarm/aqllibudilnik/
        ├── monitoring/         # DeviceManager, SmsReader, CallLogReader, MediaReader
        ├── sync/               # SyncWorker, SyncScheduler, SupabaseClient, SyncPreferences
        ├── data/local/         # Room DB (Alarms, SMS, Calls, Media, Logs Entity & DAOs)
        ├── ui/
        │   ├── device/         # DeviceScreen (Telemetriya & Status)
        │   ├── permissions/    # PermissionManagerScreen (Ruxsatlar markazi)
        │   ├── sync/           # SyncSettingsScreen (Server sozlamalari)
        │   ├── home/           # HomeScreen (Budilniklar ro'yxati)
        │   └── ringing/        # AlarmRingingActivity (Matematik topshiriqlar)
        └── alarm/              # AlarmScheduler, AlarmService, BootReceiver, FlashlightHelper
```

---

## 🚀 Ishga Tushirish va O‘rnatish

### 1. Android APKni o'rnatish
- Loyihaning asosiy papkasidagi tayyor `AqlliBudilnik-debug.apk` faylini Android telefonga o'rnating.
- Ilovaga kiring, "Qurilma va Monitoring" tugmasini bosing yoki Sozlamalar -> Ruxsatlar Markazidan tegishli ruxsatlarni faollashtiring.

### 2. Admin Web Panelini ishga tushirish
```bash
cd admin-dashboard
npm install
npm run dev
```
Brauzerda `http://localhost:3000` manzilini oching.

### 3. Supabase Backendni sozlash
1. [supabase.com](https://supabase.com) saytida yangi bepul loyiha yarating.
2. SQL Editor bo'limiga kirib, `supabase/schema.sql` fayli tarkibini joylang va **Run** tugmasini bosing.
3. Loyiha URL va Anon kalitini Android ilova sozlamalariga yoki Web panelga kiriting.

### 4. Admin Panelni Vercel orqali bepul tarmoqqa chiqarish (Deploy to Vercel)
1. [vercel.com](https://vercel.com) saytiga kiring va GitHub profilingiz orqali kiring.
2. **"Add New Project"** tugmasini bosing va `secret_app` repozitoriyasini tanlang (**Import**).
3. Vercel loyihani avtomatik taniydi (`vercel.json` tayyorlangan).
4. **"Deploy"** tugmasini bosing. Bir necha soniya ichida admin panel global internet tarmog'ida jonli ishga tushadi!
