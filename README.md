# SiCuan - Aplikasi Literasi & Manajemen Keuangan
SiCuan adalah aplikasi mobile berbasis Android yang dirancang untuk membantu pengguna mengelola keuangan pribadi, mencatat transaksi, memantau *financial plan*, dan meningkatkan literasi keuangan melalui modul edukasi yang dipersonalisasi. Aplikasi ini dibangun untuk memenuhi kriteria evaluasi UAS Mata Kuliah Mobile Development.
---
## 1. Cara Menjalankan Aplikasi
Aplikasi ini dapat dijalankan langsung melalui Android Studio atau dengan menginstal file APK.
### Menggunakan Android Studio (Pengembang)
1. *Clone* repositori ini atau ekstrak file *source code* ke dalam direktori lokal Anda.
2. Buka **Android Studio**, pilih **Open** dan arahkan ke folder utama proyek (di mana file `build.gradle.kts` tingkat proyek berada).
3. Tunggu hingga proses sinkronisasi *Gradle* selesai secara otomatis.
4. Pastikan Anda memiliki file `local.properties` di *root folder* proyek yang berisi API Key Gemini:
   ```properties
   GEMINI_API_KEY=AIzaSyB-xxxxxxxxxxxxxxxxx
   ```
5. Hubungkan *device* Android fisik Anda (dengan USB Debugging aktif) atau jalankan Android Emulator.
6. Klik tombol **Run (Shift + F10)** berwarna hijau di pojok kanan atas Android Studio.
### Menggunakan File APK (Pengguna Umum)
1. Salin file `app-debug.apk` atau `app-release.apk` ke *smartphone* Android.
2. Ketuk file tersebut untuk menginstal. Jika muncul peringatan keamanan, izinkan *"Install from unknown sources"*.
3. Buka aplikasi SiCuan. Aplikasi akan langsung terhubung ke *database cloud* dan Gemini AI *out-of-the-box* (API key sudah ter-*embed* di dalam APK).
---
## 2. Informasi API yang Digunakan
Aplikasi SiCuan mengintegrasikan berbagai API (baik pihak pertama maupun ketiga) untuk menghadirkan fitur-fitur mutakhir:
- **Firebase (Auth & Firestore)**: Digunakan sebagai *Backend as a Service* (BaaS) utama. Firebase Auth menangani autentikasi pengguna (Login/Register), sementara Firestore bertugas sebagai *Remote Database* untuk menyimpan data profil dan rencana keuangan secara *real-time*.
- **Google Gemini API**: Generative AI terintegrasi yang berfungsi sebagai "Asisten Keuangan Cerdas" di aplikasi. Gemini digunakan pada fitur *Optical Character Recognition* (OCR) untuk memindai struk belanja dan mengonversinya menjadi teks transaksi, serta memberikan analisis profil keuangan.
- **Supabase Storage**: Digunakan sebagai penyimpanan awan (*Cloud Storage*) alternatif yang tangguh untuk menyimpan aset media/gambar, seperti foto profil pengguna.
- **Frankfurter API (`api.frankfurter.dev`)**: REST API *open-source* eksternal yang di-*fetch* menggunakan Retrofit untuk mengambil data kurs mata uang dan analisis inflasi secara dinamis.
---
## 3. Struktur Folder
SiCuan secara ketat mengadopsi pola **Clean Architecture**, memisahkan kode berdasarkan *concern* agar mudah di-*maintain* dan *scalable*.
```text
app/src/main/java/com/example/sicuan/
│
├── data/           # Layer Data (Single Source of Truth)
│   ├── local/      # Implementasi Room Database (DAO, Entity, Database)
│   ├── remote/     # API Client (Retrofit) & Firebase/Supabase Services
│   └── repository/ # Implementasi konkret dari Repository (menggabungkan Local & Remote)
│
├── domain/         # Layer Domain (Business Logic Core)
│   ├── model/      # Data class murni (tidak bergantung pada framework Android)
│   ├── repository/ # Interface Repository
│   └── usecase/    # Kumpulan logika bisnis spesifik (misal: GetTransactionsUseCase)
│
├── presentation/   # Layer UI (Jetpack Compose & MVVM)
│   ├── component/  # Komponen UI Reusable (Button, Card, TopBar khusus SiCuan)
│   ├── navigation/ # NavGraph dan Route layar aplikasi
│   ├── screen/     # Kumpulan UI layar (Home, Edukasi, Transaksi, Profil)
│   └── viewmodel/  # ViewModel (penghubung UI State dengan Domain Usecase)
│
├── di/             # Dependency Injection (Modul injeksi framework)
├── util/           # Helper, Konstanta, Ekstensi Kotlin (PdfExportHelper, dll)
└── worker/         # Background Tasks dan Widget
```
---
## 4. Penjelasan Fitur Wajib dan Tambahan (Kriteria UAS)
Aplikasi ini memenuhi seluruh komponen penilaian:
### Komponen Wajib
- **List Bersifat Recycle-able**: Daftar transaksi dan riwayat edukasi diimplementasikan menggunakan `LazyColumn` pada Jetpack Compose, memastikan efisiensi memori meski data bertambah banyak secara dinamis dari *database*.
- **Responsif & Mempertahankan State**: Arsitektur MVVM memastikan aplikasi tetap menjaga data (State) saat terjadi rotasi layar (*Portrait* ke *Landscape*) berkat penggunaan `ViewModel` dan `StateFlow`.
- **Edukasi & Kalibrasi Dunning-Kruger**: 
  - Pengguna diberikan modul edukasi finansial yang **dipersonalisasi berdasarkan usia** (diambil otomatis dari tanggal lahir pengguna). 
  - Terdapat kuis pra-asesmen yang mengukur ekspektasi diri vs pengetahuan objektif pengguna guna mendeteksi *Dunning-Kruger Effect*.
- **Aksesibilitas Materi**: Menyediakan "Kamus Finansial / Glosarium" *pop-up* terintegrasi.
### Komponen Tambahan (Nilai Plus)
- **Real-time Data**: Sinkronisasi data seketika memanfaatkan *Firebase Firestore*.
- **Cache Strategy dengan Local Database**: Aplikasi dapat bekerja optimal (*offline support* parsial) menggunakan **Room Database** sebagai *local caching*.
- **Integrasi Artificial Intelligence (AI)**: OCR *Scanning* struk menggunakan Gemini API.
- **Manajemen Memori Kelas Industri**: Proyek ini telah mengintegrasikan *LeakCanary* untuk memantau kebocoran memori (aman dari *Memory Leak*) serta mengaktifkan R8/ProGuard pada *Build Release*.
---
## 5. Penjelasan Arsitektur
SiCuan diarsiteki dengan perpaduan **Clean Architecture** dan pola desain **MVVM (Model-View-ViewModel)**.
- **Pemisahan Kekhawatiran (*Separation of Concerns*)**:
  Kode dibagi menjadi 3 *layer* independen: `Presentation`, `Domain`, dan `Data`. Aturannya, *Presentation Layer* hanya tahu cara menampilkan UI dan tidak peduli dari mana data berasal. *Domain Layer* berisi logika bisnis murni. *Data Layer* mengurus jaringan (Retrofit/Firebase) dan *database local* (Room).
- **MVVM dengan Jetpack Compose**:
  *UI Layer* sepenuhnya dibangun dengan deklaratif UI (Jetpack Compose). `ViewModel` memaparkan `State` (biasanya melalui `StateFlow`). Saat State berubah (misal karena balasan dari API), layar Compose akan melakukan *Recomposition* otomatis secara reaktif.
- **Repository Pattern**:
  Aplikasi ini menggunakan pola *Repository* untuk menentukan apakah data harus ditarik dari *Local Database* (Room) demi kecepatan/akses *offline*, atau dari *Remote API/Firebase* jika membutuhkan data *real-time* terbaru (*Single Source of Truth*).
- **Dependency Injection**:
  Kelas dan *dependencies* tidak dibuat secara statis, melainkan disuntikkan secara dinamis di modul `di/`. Ini membuat kode sangat modular dan sangat mudah untuk dilakukan pengetesan (*Unit Testing*).
