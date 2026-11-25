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

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/** Root capability probe. It does not claim a device-level listener unless su responds. */
object RootChannel {
    fun probe(): Result {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val output = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            val exit = process.waitFor()
            if (exit == 0 && output.contains("uid=0")) Result(true, "Root 可用")
            else Result(false, "Root 未授权")
        } catch (_: Exception) {
            Result(false, "未找到可用 su")
        }
    }

    /** 启动 root 守护监听器（前台包名采集 + 可选无障碍锁定）。 */
    fun startMonitor(context: Context): Result {
        val probe = probe()
        return if (probe.available) {
            if (RootMonitorDaemon.start(context)) {
                Result(true, "Root 守护监听已启动")
            } else {
                Result(false, "Root 守护启动失败，请检查 su 授权")
            }
        } else probe
    }

    /** 停止 root 守护监听器。 */
    fun stopMonitor(context: Context) {
        RootMonitorDaemon.stop(context)
    }

    /** 守护监听器是否在运行。 */
    fun isMonitorRunning(): Boolean = RootMonitorDaemon.isRunning()

    data class Result(val available: Boolean, val message: String)
}
