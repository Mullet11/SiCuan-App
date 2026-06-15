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
com.example.sicuan
├── data/
│   ├── local/             # Penyimpanan lokal offline (Room DB)
│   │   ├── dao/           # Antarmuka query SQL (Insert, Update, Select)
│   │   ├── database/      # Konfigurasi & Inisialisasi Room Database
│   │   └── entity/        # Struktur tabel database (Transaction, Plan, dll)
│   ├── remote/            # Jaringan dan API eksternal
│   │   ├── api/           # Retrofit client & Endpoint Frankfurter API
│   │   ├── dto/           # Data Transfer Object (Format mentah dari API)
│   │   └── firebase/      # Sinkronisasi ke Firestore (Cloud DB)
│   └── repository/        # Implementasi Single Source of Truth (Lokal vs Cloud)
│
├── domain/
│   ├── model/             # Kelas data murni aplikasi (Business Logic)
│   ├── repository/        # Interface/Kontrak abstrak untuk Repository
│   └── usecase/           # Spesifik 1 tindakan (contoh: GetTransactionsUseCase)
│
├── presentation/
│   ├── component/         # UI "Lego" yang bisa dipakai berulang (Tombol, Card)
│   ├── navigation/        # Rute NavGraph Jetpack Compose
│   ├── viewmodel/         # Otak UI (Manajemen State & pemanggil UseCase)
│   └── screen/            # Tampilan layar UI sebenarnya
│       ├── budget/
│       ├── dashboard/
│       ├── insight/       # Layar edukasi usia & Dunning-Kruger kuis
│       ├── ocr/           # Fitur scan struk (ML Kit Text Recognition)
│       ├── profile/
│       ├── splash/
│       └── transaction/
│
├── di/                    # Dependency Injection (Modul framework)
├── util/                  # Kelas Helper yang sudah disatukan (PdfHelper, dll)
├── worker/                # Background process & Widget aplikasi
├── ui/
│   └── theme/             # Warna, Tipografi, dan Tema Jetpack Compose
└── MainActivity.kt        # Entry point pertama kali aplikasi dibuka
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
