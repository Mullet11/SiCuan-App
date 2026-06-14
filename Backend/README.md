\# SiCuan Backend



Backend SiCuan menggunakan Firebase sebagai Backend-as-a-Service untuk mendukung kebutuhan aplikasi mobile SiCuan.



Backend ini digunakan untuk authentication, backup data cloud, dan persiapan penyimpanan file struk OCR.



\## Firebase Services



Firebase service yang digunakan:



\* Firebase Authentication

\* Cloud Firestore

\* Firebase Storage



\## Current Implementation Status



| Service                 | Status      | Description                                         |

| ----------------------- | ----------- | --------------------------------------------------- |

| Firebase Authentication | Implemented | Anonymous login otomatis untuk mendapatkan UID user |

| Cloud Firestore         | Implemented | Backup data transaksi dan budget                    |

| Firebase Storage        | Prepared    | Rules dan struktur disiapkan untuk file struk OCR   |

| Firestore Rules         | Implemented | Data dibatasi berdasarkan UID pengguna              |

| Storage Rules           | Prepared    | File receipt dibatasi berdasarkan UID pengguna      |



\## Architecture Concept



SiCuan menggunakan pendekatan local-first architecture.



Artinya, data utama aplikasi tetap disimpan di local database menggunakan Room agar aplikasi tetap bisa berjalan secara offline. Firebase digunakan sebagai backend cloud untuk authentication dan backup data.



```text

User Action

→ Room Local Database

→ Firebase Firestore Backup

```



Dengan pendekatan ini, aplikasi tetap stabil ketika tidak ada koneksi internet, tetapi tetap memiliki backend cloud untuk penyimpanan data pengguna.



\## Authentication



Aplikasi menggunakan Firebase Anonymous Authentication.



Setiap user mendapatkan UID otomatis dari Firebase. UID ini digunakan sebagai identitas pemilik data pada Firestore dan Storage.



Contoh UID digunakan pada path:



```text

users/{uid}

```



\## Firestore Collections



Struktur Firestore utama:



```text

users/{uid}/transactions

users/{uid}/budgets

users/{uid}/targets

users/{uid}/literacy

users/{uid}/aiLogs

```



Collection yang sudah aktif digunakan:



```text

users/{uid}/transactions

users/{uid}/budgets

```



Collection yang disiapkan untuk pengembangan fitur PRD berikutnya:



```text

users/{uid}/targets

users/{uid}/literacy

users/{uid}/aiLogs

```



\## Firestore Data Structure



```text

users

└── {uid}

&#x20;   ├── transactions

&#x20;   │   └── {transactionId}

&#x20;   │       ├── id

&#x20;   │       ├── title

&#x20;   │       ├── amount

&#x20;   │       ├── type

&#x20;   │       ├── category

&#x20;   │       ├── date

&#x20;   │       ├── note

&#x20;   │       ├── merchant

&#x20;   │       ├── createdAt

&#x20;   │       └── updatedAt

&#x20;   │

&#x20;   ├── budgets

&#x20;   │   └── {budgetId}

&#x20;   │       ├── id

&#x20;   │       ├── category

&#x20;   │       ├── limitAmount

&#x20;   │       ├── createdAt

&#x20;   │       └── updatedAt

&#x20;   │

&#x20;   ├── targets

&#x20;   │   └── {targetId}

&#x20;   │

&#x20;   ├── literacy

&#x20;   │   └── quizResult

&#x20;   │

&#x20;   └── aiLogs

&#x20;       └── {logId}

```



\## Storage Path



Firebase Storage disiapkan untuk menyimpan file struk OCR.



Struktur Storage:



```text

receipts/{uid}/images

receipts/{uid}/pdf

```



Detail struktur:



```text

receipts

└── {uid}

&#x20;   ├── images

&#x20;   │   └── {receiptId}.jpg

&#x20;   └── pdf

&#x20;       └── {receiptId}.pdf

```



\## Security Rules



Firestore dan Storage menggunakan validasi berdasarkan Firebase Auth UID.



Konsep validasi:



```js

request.auth != null \&\& request.auth.uid == userId

```



Artinya user hanya bisa membaca dan menulis data miliknya sendiri.



\## Firestore Rules



Rules Firestore berada pada file:



```text

Backend/firestore.rules

```



Rules yang digunakan:



```js

rules\_version = '2';



service cloud.firestore {

&#x20; match /databases/{database}/documents {



&#x20;   match /users/{userId} {

&#x20;     allow read, write: if request.auth != null

&#x20;       \&\& request.auth.uid == userId;



&#x20;     match /{document=\*\*} {

&#x20;       allow read, write: if request.auth != null

&#x20;         \&\& request.auth.uid == userId;

&#x20;     }

&#x20;   }

&#x20; }

}

```



\## Storage Rules



Rules Storage berada pada file:



```text

Backend/storage.rules

```



Rules yang disiapkan:



```js

rules\_version = '2';



service firebase.storage {

&#x20; match /b/{bucket}/o {



&#x20;   match /receipts/{userId}/{allPaths=\*\*} {

&#x20;     allow read, write: if request.auth != null

&#x20;       \&\& request.auth.uid == userId;

&#x20;   }

&#x20; }

}

```



\## Android Setup



1\. Buat Firebase Project.

2\. Register Android app dengan package name:



```text

com.example.sicuan

```



3\. Download file:



```text

google-services.json

```



4\. Letakkan file tersebut di:



```text

Frontend/app/google-services.json

```



5\. Enable Anonymous Authentication.

6\. Enable Cloud Firestore.

7\. Publish Firestore Rules.

8\. Jalankan aplikasi Android.



\## Android Firebase Dependencies



Dependency Firebase yang digunakan pada Android app:



```kotlin

implementation(platform("com.google.firebase:firebase-bom:34.14.1"))

implementation("com.google.firebase:firebase-auth")

implementation("com.google.firebase:firebase-firestore")

implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

```



\## Data Flow



\### Add Transaction



```text

User input transaction

→ Save to Room local database

→ Backup to Cloud Firestore

```



\### Update Transaction



```text

User edits transaction

→ Update Room local database

→ Update Cloud Firestore document

```



\### Delete Transaction



```text

User deletes transaction

→ Delete from Room local database

→ Delete from Cloud Firestore

```



\### Add Budget



```text

User input budget

→ Save to Room local database

→ Backup to Cloud Firestore

```



\### Update Budget



```text

User edits budget

→ Update Room local database

→ Update Cloud Firestore document

```



\### Delete Budget



```text

User deletes budget

→ Delete from Room local database

→ Delete from Cloud Firestore

```



\## Implemented Backend Features



Fitur backend yang sudah berjalan:



\* Firebase Anonymous Authentication

\* UID user tampil pada halaman Profile

\* Backup transaksi ke Cloud Firestore

\* Update transaksi ke Cloud Firestore

\* Delete transaksi dari Cloud Firestore

\* Backup budget ke Cloud Firestore

\* Update budget ke Cloud Firestore

\* Delete budget dari Cloud Firestore

\* Firestore security rules berbasis UID user

\* Storage rules disiapkan untuk file struk OCR



\## Planned Backend Features



Fitur backend yang disiapkan untuk pengembangan berikutnya:



\* Upload file struk OCR ke Firebase Storage

\* Backup target tabungan ke Firestore

\* Backup hasil quiz literasi ke Firestore

\* Penyimpanan log AI Assistant ke Firestore

\* Sinkronisasi data dari Firestore ke Room



\## Repository Structure



```text

Backend

├── README.md

├── data-structure.md

├── firebase.json

├── firestore.indexes.json

├── firestore.rules

├── storage.rules

└── seed

&#x20;   └── sample-firestore-data.json

```



\## Important Notes



File rahasia seperti service account, private key, dan API key tidak boleh dipush ke repository.



File `google-services.json` digunakan oleh Android app untuk konfigurasi Firebase. Untuk kebutuhan development dan build lokal, file ini harus berada di:



```text

Frontend/app/google-services.json

```



Jika repository bersifat public, pastikan tidak ada credential sensitif seperti service account key yang ikut terupload.



