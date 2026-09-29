# ⏰ Aqlli Budilnik — Loyihaning To‘liq Hujjati va Qo‘llanmasi

**Aqlli Budilnik** — bu Android operatsion tizimi uchun mo‘ljallangan zamonaviy, ishonchli va 100% offline ishlaydigan **Native Android mobil ilova**.

Ushbu ilovaning an’anaviy budilniklardan asosiy farqi shundaki, ertalab belgilangan vaqtda signal chalinganda, uni **3 ta matematik misolni to‘g‘ri yechmasdan turib odatiy usulda o‘chirib bo‘lmaydi**. Bu foydalanuvchini uyg‘onishga va uyqusirash holatidan tez chiqib, miyani faollashtirishga yordam beradi.

---

## 📑 Mundarija
1. [Loyiha G‘oyasi va Asosiy Xususiyatlari](#-loyiha-goyasi-va-asosiy-xususiyatlari)
2. [Texnologiyalar Stoki](#-texnologiyalar-stoki)
3. [Loyiha Arxitekturasi va Tuzilishi](#-loyiha-arxitekturasi-va-tuzilishi)
4. [Asosiy Modullar Tahlili](#-asosiy-modullar-tahlili)
   - [4.1. Ma'lumotlar Qatlami (Room Database)](#41-malumotlar-qatlami-room-database)
   - [4.2. Matematik Misollar Generatori (Math Engine)](#42-matematik-misollar-generatori-math-engine)
   - [4.3. Alarm Boshqaruvi va Fon Servisi (Alarm & Background)](#43-alarm-boshqaruvi-va-fon-servisi-alarm--background)
   - [4.4. Foydalanuvchi Interfeysi (Jetpack Compose UI)](#44-foydalanuvchi-interfeysi-jetpack-compose-ui)
5. [Qiyinchilik Darajalari va Misollar](#-qiyinchilik-darajalari-va-misollar)
6. [Lock Screen (Qulflangan Ekran) va Xavfsizlik Tizimi](#-lock-screen-qulflangan-ekran-va-xavfsizlik-tizimi)
7. [Android Tizim Ruxsatlari (Permissions)](#-android-tizim-ruxsatlari-permissions)
8. [O‘rnatish va Ishga Tushirish Bo‘yicha Qo‘llanma](#-ornatish-va-ishga-tushirish-boyicha-qollanma)
9. [Tayyor APK Fayl Haqida](#-tayyor-apk-fayl-haqida)

---

## 💡 Loyiha G‘oyasi va Asosiy Xususiyatlari

- **Stop tugmasining yo‘qligi:** Signal boshlanganda ilovaning o‘zida "Stop" yoki "Bekor qilish" tugmasi ko‘rsatilmaydi.
- **3 ta topshiriq talabi:** Foydalanuvchi ketma-ket 3 ta matematik topshiriqni yechishi shart (1/3, 2/3, 3/3).
- **Noto‘g‘ri javob sanksiyasi:** Noto‘g‘ri son kiritilsa, signal va vibratsiya to‘xtovsiz davom etadi.
- **Lock Screen integratsiyasi:** Telefon qulflangan, ekran o‘chiq bo‘lsa ham ekranni uyg‘otadi va Lock Screen ustida to‘liq ochiladi.
- **Power tugmasiga chidamlilik:** Foydalanuvchi telefonning yon tarafidagi Power tugmasini bosib ekranni o‘chirsa ham, musiqa va vibratsiya fonda to‘xtovsiz chalishda davom etadi. Ekran qayta yoqilganda qaysi misolda to‘xtagan bo‘lsa, o‘sha yerdan davom ettiriladi.
- **Qayta yuklash (Reboot) himoyasi:** Telefon o‘chib-yonsa (`BOOT_COMPLETED`), barcha faol signallar avtomatik qayta tiklanadi.
- **100% Offline:** Hech qanday internet talab etilmaydi, ma'lumotlar qurilmaning ichki xotirasida (Room DB) xavfsiz saqlanadi.

---

## 🛠 Texnologiyalar Stoki

| Komponent | Ishlatilgan Texnologiya | Izoh |
| :--- | :--- | :--- |
| **Dasturlash tili** | **Kotlin 2.0+** | Zamonaviy va xavfsiz Android tili |
| **Foydalanuvchi interfeysi** | **Jetpack Compose + Material 3** | Deklarativ, reaktiv va chiroyli dizayn tizimi |
| **Arxitektura** | **MVVM + Clean Architecture** | Kodning toza va qatlamlarga ajratilganligi |
| **Ma'lumotlar bazasi** | **Room Database + SQLite** | Tezkor lokal ma'lumotlar bazasi va Flow integratsiyasi |
| **Asinxronlik** | **Kotlin Coroutines + StateFlow** | Fon amallari va reaktiv holat boshqaruvi |
| **Signal rejalashtirish** | **Android AlarmManager** | Aniq vaqtda (`RTC_WAKEUP`) signallarni uyg‘otish |
| **Fon audiosi va vibratsiya**| **Foreground Service (MediaPlayback)** | Ovoz oqimini uzluksiz (`STREAM_ALARM`) ushlab turish |
| **Navigatsiya** | **Jetpack Navigation Compose** | Ekranlararo o‘tish boshqaruvi |

---

## 📁 Loyiha Arxitekturasi va Tuzilishi

Loyiha quyidagi mantiqiy modullarga ajratilgan:

```text
app/src/main/java/uz/smartalarm/aqllibudilnik/
│
├── AqlliBudilnikApp.kt          # Ilova kirish Application sinfi (Kanal va DB boshqaruvi)
├── MainActivity.kt              # Asosiy kirish oynasi va ruxsatnomalar boshqaruvi
│
├── alarm/                       # Budilnik fon va tizim boshqaruvi
│   ├── AlarmScheduler.kt        # AlarmManager yordamida signallarni rejalashtirish/bekor qilish
│   ├── AlarmReceiver.kt         # Belgilangan vaqt yetib kelganda qabul qiluvchi BroadcastReceiver
│   ├── AlarmService.kt          # Musiqa (loop), vibratsiya va bildirishnomani boshqaruvchi Foreground Service
│   └── BootReceiver.kt          # Telefon o‘chib yoqilganda barcha signallarni tiklovchi Receiver
│
├── data/                        # Ma'lumotlar bilan ishlash qatlami
│   ├── local/
│   │   ├── AlarmEntity.kt       # Room ma'lumotlar bazasi jadvali
│   │   ├── AlarmDao.kt          # Baza amallari (Insert, Update, Delete, Flow query)
│   │   ├── AppDatabase.kt       # Room Database konfiguratsiyasi
│   │   └── Converters.kt        # Hafta kunlari va enum konvertorlari
│   ├── model/
│   │   ├── Alarm.kt             # Budilnik domain modeli
│   │   ├── Difficulty.kt        # Qiyinchilik darajalari (Oson, O‘rta, Qiyin)
│   │   └── WeekDay.kt           # Hafta kunlari (Dush..Yak)
│   └── repository/
│       └── AlarmRepository.kt   # Baza va Scheduler o‘rtasidagi yagona ma'lumot manbai
│
├── math/                        # Matematik topshiriqlar mexanizmi
│   ├── MathQuestion.kt          # Misol ma'lumotlar modeli (savol, javob, daraja)
│   ├── MathEvaluator.kt         # Matematik amallar ustuvorligi (+, -, ×, ÷) bo‘yicha hisoblagich
│   └── MathQuestionGenerator.kt # Oson, o‘rta va qiyin misollarni avtomatik generatsiya qilish
│
└── ui/                          # Foydalanuvchi interfeysi (Jetpack Compose)
    ├── theme/                   # Ranglar, shriftlar va Dark/Light mavzular
    ├── home/                    # Bosh sahifa (Budilniklar ro‘yxati, ON/OFF, '+' tugmasi)
    ├── createedit/              # Budilnik yaratish va tahrirlash oynasi (Vaqt tanlash, kunlar)
    ├── ringing/                 # Qulflangan ekranda chiquvchi to‘liq ekranli Alarm va Matematika oynasi
    ├── settings/                # Standart sozlamalar sahifasi
    └── navigation/              # Navigatsiya yo‘nalishlari (Screen.kt, AppNavigation.kt)
```

---

## 🔍 Asosiy Modullar Tahlili

### 4.1. Ma'lumotlar Qatlami (Room Database)
- `AlarmEntity` orqali budilnik vaqti (soat, daqiqa), nomi, takrorlanish kunlari (`Set<WeekDay>`), qiyinchilik darajasi, vibratsiya holati saqlanadi.
- `AlarmDao` Kotlin `Flow` mexanizmidan foydalanadi, ya’ni budilnik qo‘shilishi yoki o‘chirilishi bilan bosh ekrandagi ro‘yxat avtomatik yangilanadi.

### 4.2. Matematik Misollar Generatori (Math Engine)
- Har safar budilnik chalinganda misollar tasodifiy generatsiya qilinadi.
- **Natijalar butun son bo‘lishi kafolatlangan:** Bo‘lish (`÷`) amallarida qoldiqli sonlar chiqmasligi uchun bo‘luvchi va natija ko‘paytirilib, bo‘linuvchi hosil qilinadi (masalan: `4 × 5 = 20` -> `20 ÷ 4 = 5`).
- **Amallar ustuvorligi:** Ko‘paytirish va bo‘lish amallari qo‘shish va ayirishdan oldin hisoblanadi.

### 4.3. Alarm Boshqaruvi va Fon Servisi
- `AlarmScheduler` foydalanuvchi tanlagan haftaning qaysi kunlari belgilanganiga qarab kelgusi eng yaqin vaqtni millisekundlarda hisoblab chiqadi va `AlarmManager.setExactAndAllowWhileIdle()` orqali ro‘yxatga oladi.
- Belgilangan daqiqa kelganda `AlarmReceiver` uyg‘onadi va `AlarmService` nomli `ForegroundService`ni ishga tushiradi.
- `AlarmService` audio oqimini `STREAM_ALARM` orqali to‘xtovsiz loop tarzida ijro etadi va vibratorni tebratadi.
- Telefonning Lock Screen oynasi ustida `AlarmRingingActivity` ochiladi.
- Faqatgina 3-misol to‘g‘ri yechilgandagina `AlarmService.stop()` chaqiriladi va ovoz to‘xtaydi.

### 4.4. Foydalanuvchi Interfeysi (Jetpack Compose UI)
- Zamonaviy qora va neon ranglar uyg‘unligidagi UI.
- Qulflangan ekranda foydalanuvchiga qulay bo‘lishi uchun ekranning o‘zida **katta sensorli raqamli klaviatura** joylashtirilgan. Tizim klaviaturasining ochilishi yoki ekranni to‘sib qo‘yishi muammosi yuzaga kelmaydi.

---

## 🧮 Qiyinchilik Darajalari va Misollar

1. **Oson (Easy):**
   - Kichik sonlar va 1 ta arifmetik amal:
   - `4 + 8 = 12`
   - `14 - 6 = 8`
   - `3 × 4 = 12`
   - `20 ÷ 4 = 5`

2. **O‘rta (Medium):**
   - 2 ta aralash amal va amallar ustuvorligi:
   - `15 ÷ 3 + 5 = 10`
   - `8 × 3 - 7 = 17`
   - `18 - 4 + 6 = 20`

3. **Qiyin (Hard):**
   - Bir nechta amallar va kattaroq sonlar:
   - `12 × 3 - 8 = 28`
   - `24 ÷ 4 + 17 = 23`
   - `15 + 8 × 2 = 31`

---

## 🔒 Lock Screen (Qulflangan Ekran) va Xavfsizlik Tizimi

Android xavfsizlik talablariga muvofiq, ilova qulflangan ekran ustida ishonchli ishlashi uchun quyidagi tizim funksiyalari yoqilgan:
- `setShowWhenLocked(true)` — ekran qulflangan paytda ekranni bevosita ochish;
- `setTurnScreenOn(true)` — o‘chirilgan telefon ekranini avtomatik yoritish;
- `FLAG_KEEP_SCREEN_ON` — foydalanuvchi misol yechayotganda ekranning o‘chib qolishini oldini olish;
- `OnBackPressedDispatcher` — misollar to‘liq yechilmasdan turib tizimning orqaga (Back) tugmasi bosilishiga ruxsat bermaydi.

---

## 🛡 Android Tizim Ruxsatlari (Permissions)

`AndroidManifest.xml` faylida quyidagi barcha zarur ruxsatnomalar belgilangan:
- `SCHEDULE_EXACT_ALARM` va `USE_EXACT_ALARM` — belgilangan soniyagacha aniq signal berish.
- `USE_FULL_SCREEN_INTENT` — to‘liq ekranli alarm oynasini ochish.
- `POST_NOTIFICATIONS` — Android 13+ da bildirishnomalar chiqarish.
- `WAKE_LOCK` — protsessorni kerakli vaqtda uyg‘otish.
- `VIBRATE` — tebranish hosil qilish.
- `FOREGROUND_SERVICE` va `FOREGROUND_SERVICE_MEDIA_PLAYBACK` — fon audio xizmati.
- `RECEIVE_BOOT_COMPLETED` — qurilma qayta yoqilganda signallarni tiklash.

---

## 💻 O‘rnatish va Ishga Tushirish Bo‘yicha Qo‘llanma

### Talablar:
- **Android Studio** (Ladybug / Koala yoki undan yangi versiya)
- **JDK 17** yoki **JDK 21**
- Minimal Android versiyasi: **Android 8.0 (API 26)** va undan yuqori

### Loyihani ochish va yurgizish:
1. Repozitoriyni klonlang:
   ```bash
   git clone https://github.com/Tuxtamurod-Jurayev/aqilli_budilnik.git
   ```
2. Android Studio-ni oching va `Open` tugmasi orqali loyiha papkasini tanlang.
3. Gradle avtomatik sinxronizatsiya qilinadi.
4. Emulyator yoki USB orqali ulangan haqiqiy Android telefonni tanlab, **Run 'app'** (`Shift + F10`) tugmasini bosing.

---

## 📦 Tayyor APK Fayl Haqida

Loyiha to‘liq kompilyatsiya qilingan va tayyor `.apk` fayl loyihaning asosiy papkasida joylashtirilgan:
- **`AqlliBudilnik-debug.apk`**

Ushbu faylni to‘g‘ridan-to‘g‘ri har qanday Android smartfonga o‘tkazib, ilovani o‘rnatish va sinab ko‘rish mumkin!
