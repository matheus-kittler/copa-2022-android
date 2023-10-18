package me.dio.copa.catar.notification.scheduler.extensions

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.dio.copa.catar.notification.scheduler.R

private const val CHANNEL_ID = "new_channel_video"
private const val NOTIFICATION_NAME = "Notificações"
private const val NOTIFICATION_INTENT_REQUEST_CODE = 0
private const val REQUEST_CODE_POST_NOTIFICATIONS = 1

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Context.showNotification(title: String, content: String) {
    createNotificationChannel()
    val notification = getNotification(title, content)

    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this as Activity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permissão Necessária")
                .setMessage("Para utilizar esta funcionalidade, precisamos da permissão para notificações.")
                .setPositiveButton("Aceitar Permissões") { _, _ ->
                    // Solicitar a permissão após o usuário concordar.
                    ActivityCompat.requestPermissions(
                        this as Activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_CODE_POST_NOTIFICATIONS
                    )
                }
                .setNegativeButton("Recusar") { _, _ ->
                }
                .show()
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            REQUEST_CODE_POST_NOTIFICATIONS
        )
    } else {
        NotificationManagerCompat
            .from(this)
            .notify(content.hashCode(), notification)
    }
}

private fun Context.createNotificationChannel() {
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(CHANNEL_ID, NOTIFICATION_NAME, importance)

    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        .createNotificationChannel(channel)
}

private fun Context.getNotification(title: String, content: String): Notification =
    NotificationCompat
        .Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_soccer)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setContentIntent(getOpenAppPendingIntent())
        .setAutoCancel(true)
        .build()

private fun Context.getOpenAppPendingIntent() = PendingIntent.getActivity(
    this,
    NOTIFICATION_INTENT_REQUEST_CODE,
    packageManager.getLaunchIntentForPackage(packageName),
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
)