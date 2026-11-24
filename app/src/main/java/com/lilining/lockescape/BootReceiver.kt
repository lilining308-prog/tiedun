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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 开机后按用户此前的选择恢复护盾运行状态：
 * - 若用户开启过 Root 守护 / 无障碍锁定，则重新拉起守护进程；
 * - 重新启动前台保活服务。
 *
 * 不会在用户从未授权的情况下自行开启任何锁定。
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_LOCKED_BOOT_COMPLETED &&
            action != "android.intent.action.QUICKBOOT_POWERON"
        ) {
            return
        }

        val keeper = Prefs.rootKeeperEnabled(context)
        val daemon = Prefs.rootDaemonEnabled(context)
        if (keeper || daemon) {
            RootMonitorDaemon.setKeeperEnabled(context, keeper)
            RootMonitorDaemon.start(context)
        }

        runCatching {
            context.startForegroundService(Intent(context, DaemonService::class.java))
        }
    }
}
