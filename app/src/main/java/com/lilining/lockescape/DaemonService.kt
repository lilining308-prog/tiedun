/*
 * Copyright (C) 2026 liyan
 *
 * tiedun 铁盾 is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of
 * the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *
 * SPDX-License-Identifier: MulanPSL-2.0
 */

package com.lilining.lockescape

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder

/**
 * 前台保活服务：保证无障碍服务被杀后能尽快恢复，并作为逃生常驻入口。
 */
class DaemonService : Service() {

    companion object {
        private const val CHANNEL_ID = "escape_daemon"
        private const val NOTIFICATION_ID = 1001

        internal fun supportsSpecialUseForegroundType(sdkInt: Int): Boolean =
            sdkInt >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startAsForeground()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "锁机逃生守护",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun startAsForeground() {
        val contentIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("锁机逃生已就绪")
            .setContentText("长按音量下 1.5s 或连按 4 次触发逃生")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .build()
        if (supportsSpecialUseForegroundType(Build.VERSION.SDK_INT)) {
            startSpecialUseForeground(notification)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    @TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun startSpecialUseForeground(notification: Notification) {
        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null
}
