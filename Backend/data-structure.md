\# SiCuan Firebase Data Structure



\## Overview



SiCuan menggunakan Firebase sebagai backend cloud untuk authentication dan backup data pengguna.



Aplikasi menggunakan pendekatan local-first:



\- Room sebagai local database utama.

\- Firebase Authentication untuk identitas user.

\- Cloud Firestore untuk backup transaksi dan budget.

\- Firebase Storage disiapkan untuk penyimpanan file struk OCR.



\## Authentication



SiCuan menggunakan Firebase Anonymous Authentication.



Setiap pengguna mendapatkan UID otomatis dari Firebase Authentication.



UID ini digunakan sebagai owner data pada Firestore dan Storage.



\## Firestore Structure



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



\## Storage Structure



receipts

└── {uid}

&#x20;   ├── images

&#x20;   │   └── {receiptId}.jpg

&#x20;   └── pdf

&#x20;       └── {receiptId}.pdf



\## Security Concept



Firestore dan Storage dibatasi berdasarkan Firebase Auth UID.



User hanya dapat membaca dan menulis data miliknya sendiri.



\## Implemented Collections



Saat ini collection yang sudah aktif digunakan aplikasi:



\- users/{uid}/transactions

\- users/{uid}/budgets



Collection berikut disiapkan untuk pengembangan fitur PRD berikutnya:



\- users/{uid}/targets

\- users/{uid}/literacy

\- users/{uid}/aiLogs

\- receipts/{uid}/images

\- receipts/{uid}/pdf

