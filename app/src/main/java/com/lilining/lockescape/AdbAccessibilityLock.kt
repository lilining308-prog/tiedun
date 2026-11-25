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

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku

/**
 * ADB 式无障碍锁定：执行与 `adb shell settings put` 完全相同的命令，
 * 通过 Shizuku 特权通道运行，无需 root。
 *
 * 也可由用户手动在电脑上执行 [manualLockCommand] 完成同样的锁定。
 *
 * 安全边界：只追加/移除本应用自己的无障碍服务条目，不改动其它应用的服务。
 */
object AdbAccessibilityLock {

    const val PKG = "com.lilining.lockescape"
    const val SERVICE_COMPONENT = "com.lilining.lockescape/.EscapeAccessibilityService"
    private const val SERVICES_SETTING = "secure enabled_accessibility_services"
    private const val ENABLED_SETTING = "secure accessibility_enabled"

    /** 电脑端等价命令（用户可自行 adb 执行）。 */
    val manualLockCommand: String =
        "adb shell settings put " + SERVICES_SETTING + " \"" + SERVICE_COMPONENT + "\"\n" +
            "adb shell settings put " + ENABLED_SETTING + " 1"

    fun isShizukuReady(): Boolean = try {
        Shizuku.pingBinder() && (Shizuku.isPreV11() ||
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED)
    } catch (_: Throwable) {
        false
    }

    /**
     * 锁定：把本应用无障碍服务写入 enabled_accessibility_services。
     * 读取与写入各走一次 ShizukuProcess（写入与启用合并为单条命令）。
     */
    fun lock(): Result {
        if (!isShizukuReady()) return Result(false, "Shizuku 未就绪，可手动执行 adb 命令")
        val get = ShizukuProcess.runForOutput("settings get " + SERVICES_SETTING)
        if (!get.ok) return Result(false, "读取无障碍设置失败：" + get.output)
        val current = normalizeServices(get.output)
        if (containsOwn(current)) return Result(true, "无障碍已处于锁定状态")
        val next = mergeServiceEntry(current, SERVICE_COMPONENT)
        val put = ShizukuProcess.runForOutput(
            "settings put " + SERVICES_SETTING + " \"" + next + "\" &&" +
                " settings put " + ENABLED_SETTING + " 1"
        )
        return if (put.ok) Result(true, "ADB 式无障碍锁定成功")
        else Result(false, "写入无障碍列表失败：" + put.output)
    }

    /** 解除锁定：仅移除本应用的条目，保留其它应用无障碍服务。 */
    fun unlock(): Result {
        if (!isShizukuReady()) return Result(false, "Shizuku 未就绪")
        val get = ShizukuProcess.runForOutput("settings get " + SERVICES_SETTING)
        if (!get.ok) return Result(false, "读取无障碍设置失败：" + get.output)
        val current = normalizeServices(get.output)
        if (!containsOwn(current)) return Result(true, "未处于锁定状态")
        val remaining = removeServiceEntry(current, PKG)
        val put = ShizukuProcess.runForOutput(
            "settings put " + SERVICES_SETTING + " \"" + remaining + "\""
        )
        return if (put.ok) Result(true, "已解除 ADB 式无障碍锁定")
        else Result(false, "解除失败：" + put.output)
    }

    fun isLocked(): Boolean {
        val get = ShizukuProcess.runForOutput("settings get " + SERVICES_SETTING)
        return get.ok && containsOwn(normalizeServices(get.output))
    }

    internal fun normalizeServices(raw: String): String {
        val trimmed = raw.trim()
        return if (trimmed == "null") "" else trimmed
    }

    internal fun containsOwn(current: String): Boolean = current.contains(PKG)

    internal fun mergeServiceEntry(current: String, component: String): String =
        if (current.isEmpty()) component else "$current:$component"

    internal fun removeServiceEntry(current: String, pkg: String): String =
        current.split(':')
            .filter { it.isNotBlank() && !it.startsWith("$pkg/") && it != pkg }
            .joinToString(":")

    data class Result(val ok: Boolean, val message: String)
}
