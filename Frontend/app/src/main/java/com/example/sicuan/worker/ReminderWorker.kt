package com.example.sicuan.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicuan.MainActivity
import com.example.sicuan.data.local.database.SiCuanDatabase
import java.util.Calendar

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_REMINDER = "sicuan_reminder_channel"
        const val CHANNEL_BUDGET_WARNING = "sicuan_budget_warning_channel"
        const val NOTIF_ID_REMINDER = 1
        const val NOTIF_ID_BUDGET_80 = 2
        const val NOTIF_ID_BUDGET_100 = 3
    }

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("sicuan_user_prefs", Context.MODE_PRIVATE)
        val isSmartNotifEnabled = prefs.getBoolean("smart_notif_enabled", true)
        if (!isSmartNotifEnabled) return Result.success()

        val db = SiCuanDatabase.getDatabase(applicationContext)
        val transactionDao = db.transactionDao()

        createNotificationChannels()

        val twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        val recentCount = transactionDao.getTransactionCountAfterDate(twentyFourHoursAgo)
        if (recentCount == 0) {
            showReminderNotification()
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        val monthlyExpense = transactionDao.getTotalExpenseAfterDate(startOfMonth)
        val monthlyIncome = transactionDao.getTotalIncomeAfterDate(startOfMonth)

        if (monthlyIncome > 0) {
            val ratio = monthlyExpense / monthlyIncome
            when {
                ratio >= 1.0 -> showBudgetWarning(
                    id = NOTIF_ID_BUDGET_100,
                    title = "⚠️ Pengeluaran Melebihi Batas!",
                    message = "Pengeluaranmu bulan ini sudah melebihi 100% dari pemasukan. Yuk evaluasi keuanganmu!"
                )
                ratio >= 0.8 -> showBudgetWarning(
                    id = NOTIF_ID_BUDGET_80,
                    title = "⚠️ Peringatan Anggaran 80%",
                    message = "Pengeluaranmu sudah mencapai ${(ratio * 100).toInt()}% dari pemasukan bulan ini. Hati-hati ya!"
                )
            }
        }

        return Result.success()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDER,
                "Pengingat Pencatatan",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pengingat untuk mencatat transaksi harian"
            }

            val budgetChannel = NotificationChannel(
                CHANNEL_BUDGET_WARNING,
                "Peringatan Anggaran",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Peringatan saat pengeluaran mendekati atau melebihi anggaran"
            }

            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(budgetChannel)
        }
    }

    private fun showReminderNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📝 Waktunya Mencatat!")
            .setContentText("Kamu belum mencatat transaksi hari ini. Yuk catat supaya cuanmu terkontrol!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Kamu belum mencatat transaksi hari ini. Yuk catat pemasukan atau pengeluaranmu supaya keuangan tetap terpantau! 💰"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIF_ID_REMINDER, notification)
    }

    private fun showBudgetWarning(id: Int, title: String, message: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, id, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_BUDGET_WARNING)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, notification)
    }
}
