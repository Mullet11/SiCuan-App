# SiCuan

SiCuan adalah aplikasi mobile manajemen keuangan pribadi yang dirancang untuk membantu mahasiswa mencatat pemasukan, pengeluaran, mengatur anggaran, serta memantau kondisi keuangan harian dengan lebih mudah.

Aplikasi ini dikembangkan sebagai kelanjutan dari rancangan PRD SiCuan dengan fokus utama pada pencatatan transaksi, dashboard keuangan, local database, API pihak ketiga, dan penerapan arsitektur aplikasi mobile yang rapi.

## Anggota Kelompok

1. Muhammad Rakha’ Athallah - Frontend, Backend, Arsitektur Aplikasi
2. Clarissa Dhea Allisya - UI/UX, Dokumentasi, Testing

## Tech Stack

* Kotlin
* Jetpack Compose
* Material 3
* Navigation Compose
* ViewModel
* Coroutines
* Room Database
* Retrofit
* OkHttp Logging Interceptor
* Clean Architecture
* MVVM
* Git & GitHub

## Design System

SiCuan menggunakan design system dengan nuansa hijau modern yang disesuaikan untuk aplikasi keuangan mahasiswa.

### Warna Utama

* Primary Light: `#3F6355`
* Primary Dark: `#2D6A4F`
* Background Light: `#E6FFF6`
* Background Dark: `#081C15`
* Surface Light: `#D6F0E2`
* Surface Dark: `#1B4332`
* Text Light: `#1A3C34`
* Text Dark: `#F1FAEE`
* Danger / Expense: `#BA1A1A`
* Income / Success: `#3F6355`

### Typography

Typeface utama yang direncanakan adalah Plus Jakarta Sans dengan skala typography:

* Headline Large
* Headline Medium
* Headline Small
* Title Large
* Title Medium
* Body Large
* Body Medium
* Label Medium
* Label Small

## Struktur Project

```text
com.example.sicuan
├── data
│   ├── local
│   │   ├── dao
│   │   ├── database
│   │   └── entity
│   ├── remote
│   │   ├── api
│   │   └── dto
│   └── repository
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── presentation
│   ├── component
│   ├── navigation
│   ├── viewmodel
│   └── screen
│       ├── splash
│       ├── dashboard
│       ├── transaction
│       ├── budget
│       ├── insight
│       └── profile
│
├── ui
│   └── theme
│
└── MainActivity.kt
```

## Arsitektur Aplikasi

SiCuan menggunakan pendekatan Clean Architecture dan MVVM.

### Presentation Layer

Berisi tampilan aplikasi, navigasi, komponen UI, dan ViewModel.

Contoh folder:

* `presentation/screen`
* `presentation/navigation`
* `presentation/component`
* `presentation/viewmodel`

### Domain Layer

Berisi model utama, repository interface, dan use case aplikasi.

Contoh folder:

* `domain/model`
* `domain/repository`
* `domain/usecase`

### Data Layer

Berisi implementasi repository, database lokal, entity Room, DAO, API service, dan DTO.

Contoh folder:

* `data/local`
* `data/remote`
* `data/repository`

## Fitur yang Direncanakan

### Fitur Wajib UAS

* Minimal 6 screen aplikasi
* List transaksi recycle-able menggunakan LazyColumn
* State management menggunakan ViewModel
* MVVM
* Clean Architecture
* Fetching data dari API pihak ketiga
* Local database menggunakan Room
* BREAD transaksi:

    * Browse transaksi
    * Read detail transaksi
    * Edit transaksi
    * Add transaksi
    * Delete transaksi

### Fitur Utama SiCuan

* Dashboard keuangan
* Daftar transaksi
* Tambah transaksi manual
* Detail transaksi
* Edit transaksi
* Hapus transaksi
* Anggaran bulanan
* Insight keuangan dari API
* Profil pengguna

### Fitur Tambahan yang Direncanakan

* Scan struk menggunakan OCR
* Notifikasi anggaran
* Target keuangan
* Edukasi finansial
* Asisten AI keuangan

## Progress Pengembangan

### Day 1 - Project Setup

Status: Selesai

Yang sudah dikerjakan:

* Setup dependency utama
* Setup struktur folder Clean Architecture
* Setup theme dan resource SiCuan
* Setup komponen UI dasar
* Setup Navigation Compose
* Membuat route screen
* Membuat skeleton screen aplikasi
* Menghubungkan MainActivity dengan SiCuanNavGraph
* Aplikasi berhasil dijalankan tanpa crash

Screen yang sudah dibuat:

* Splash Screen
* Dashboard Screen
* Transaction List Screen
* Add Transaction Screen
* Transaction Detail Screen
* Edit Transaction Screen
* Budget Screen
* Insight Screen
* Profile Screen

## Cara Menjalankan Project

1. Clone repository ini.
2. Buka project menggunakan Android Studio.
3. Pastikan koneksi internet aktif untuk Gradle Sync.
4. Klik `Sync Project with Gradle Files`.
5. Jalankan aplikasi pada emulator atau perangkat Android.
6. Pastikan aplikasi terbuka dari Splash Screen dan dapat berpindah ke Dashboard.

## Branching Strategy

Repository menggunakan strategi branch:

* `main`: branch stabil untuk versi final
* `develop`: branch integrasi pengembangan
* `feature/*`: branch pengerjaan fitur

Contoh branch:

```text
feature/day-1-project-setup
feature/transaction-bread
feature/room-database
feature/api-insight
```

## Status Saat Ini

Aplikasi masih berada pada tahap awal pengembangan. Fitur yang sudah tersedia saat ini adalah pondasi UI, theme, navigation, dan screen skeleton. Fitur database, BREAD transaksi, API, dan logic ViewModel akan dikerjakan pada tahap berikutnya.
