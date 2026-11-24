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

    fun startMonitor(context: Context): Result {
        val probe = probe()
        return if (probe.available) {
            // The device-specific monitor is intentionally gated until its event source is verified.
            Result(false, "Root 已授权，监听器待设备适配验证")
        } else probe
    }

    data class Result(val available: Boolean, val message: String)
}
