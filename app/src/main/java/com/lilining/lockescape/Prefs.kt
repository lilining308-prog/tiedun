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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 轻量本地存储：记录无障碍服务捕获到的当前顶层包名、逃生日志。
 */
object Prefs {
    private const val NAME = "lock_escape_prefs"
    private const val KEY_TOP_PACKAGE = "top_package"
    private const val KEY_TOP_OBSERVED_AT = "top_observed_at"
    private const val KEY_LOGS = "escape_logs"
    private const val KEY_EXECUTION_MODE = "execution_mode"
    private const val KEY_USER_WHITELIST = "user_whitelist"
    private const val KEY_ROOT_KEEPER = "root_accessibility_keeper"
    private const val KEY_ROOT_DAEMON = "root_monitor_enabled"
    private const val KEY_SUPPRESS_ALWAYS_ON = "suppress_always_on"
    private const val KEY_TRIGGER_COUNT = "trigger_count"
    private const val KEY_TRIGGER_WINDOW_MS = "trigger_window_ms"
    private const val KEY_LONG_PRESS_MS = "long_press_ms"
    private const val MAX_LOGS = 20

    fun currentTopPackage(ctx: Context): String =
        lastObservedPackage(ctx).takeIf { isRecent(ctx) } ?: ""

    /** Revalidate the observed target and current whitelist immediately before each action. */
    fun canActOn(ctx: Context, pkg: String): Boolean {
        val sp = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val observedAt = sp.getLong(KEY_TOP_OBSERVED_AT, 0L)
        val whitelist = userWhitelist(ctx)
        return sp.getString(KEY_TOP_PACKAGE, "") == pkg && observedAt > 0L &&
            System.currentTimeMillis() - observedAt in 0..RescueTarget.EXPIRY_MS &&
            ProtectedTargets.from(ctx).canActOn(pkg, whitelist)
    }

    private fun lastObservedPackage(ctx: Context): String =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE).getString(KEY_TOP_PACKAGE, "") ?: ""

    private fun isRecent(ctx: Context): Boolean {
        val observedAt = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE).getLong(KEY_TOP_OBSERVED_AT, 0L)
        return observedAt > 0 && System.currentTimeMillis() - observedAt in 0..RescueTarget.EXPIRY_MS
    }

    fun saveCurrentTopPackage(ctx: Context, pkg: String) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOP_PACKAGE, pkg)
            .putLong(KEY_TOP_OBSERVED_AT, System.currentTimeMillis())
            .apply()
    }

    fun clearCurrentTopPackage(ctx: Context) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_TOP_PACKAGE)
            .remove(KEY_TOP_OBSERVED_AT)
            .apply()
    }

    /** 追加一条逃生日志（环形保留最近 MAX_LOGS 条，新日志在前） */
    fun addEscapeLog(ctx: Context, msg: String) {
        val sp = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val time = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logs = getEscapeLogs(ctx).toMutableList()
        logs.add(0, "[$time] $msg")
        if (logs.size > MAX_LOGS) {
            logs.subList(MAX_LOGS, logs.size).clear()
        }
        sp.edit().putString(KEY_LOGS, logs.joinToString("\n")).apply()
    }

    fun getEscapeLogs(ctx: Context): List<String> {
        val sp = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        val raw = sp.getString(KEY_LOGS, "") ?: ""
        return if (raw.isBlank()) emptyList() else raw.split("\n")
    }

    fun executionMode(ctx: Context): ShellExecutor.Mode {
        val raw = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getString(KEY_EXECUTION_MODE, ShellExecutor.Mode.AUTO.name)
        return ShellExecutor.Mode.entries.firstOrNull { it.name == raw } ?: ShellExecutor.Mode.AUTO
    }

    fun saveExecutionMode(ctx: Context, mode: ShellExecutor.Mode) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_EXECUTION_MODE, mode.name)
            .apply()
    }

    fun userWhitelist(ctx: Context): Set<String> {
        val raw = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getString(KEY_USER_WHITELIST, "") ?: ""
        return raw.split('\n')
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .let { AppSafety.normalizeUserWhitelist(it.toSet()) }
    }

    fun addUserWhitelistPackage(ctx: Context, pkg: String) {
        val normalized = pkg.trim()
        if (!AppSafety.isValidPackageName(normalized)) return
        val whitelist = userWhitelist(ctx).toMutableSet()
        whitelist.add(normalized)
        saveUserWhitelist(ctx, whitelist)
    }

    fun removeUserWhitelistPackage(ctx: Context, pkg: String) {
        val whitelist = userWhitelist(ctx).toMutableSet()
        whitelist.remove(pkg.trim())
        saveUserWhitelist(ctx, whitelist)
    }

    fun clearUserWhitelist(ctx: Context) {
        saveUserWhitelist(ctx, emptySet())
    }

    fun clearEscapeLogs(ctx: Context) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_LOGS)
            .apply()
    }

    private fun saveUserWhitelist(ctx: Context, whitelist: Set<String>) {
        val normalized = AppSafety.normalizeUserWhitelist(whitelist)
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USER_WHITELIST, normalized.sorted().joinToString("\n"))
            .apply()
    }

    /** Root 无障碍锁定总开关：仅当为 true 时守护进程才会改写无障碍设置。 */
    fun rootKeeperEnabled(ctx: Context): Boolean =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE).getBoolean(KEY_ROOT_KEEPER, false)

    fun saveRootKeeperEnabled(ctx: Context, enabled: Boolean) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ROOT_KEEPER, enabled)
            .apply()
    }

    /** Root 守护监听开关（前台包名采集）。 */
    fun rootDaemonEnabled(ctx: Context): Boolean =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE).getBoolean(KEY_ROOT_DAEMON, false)

    fun saveRootDaemonEnabled(ctx: Context, enabled: Boolean) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ROOT_DAEMON, enabled)
            .apply()
    }

    /** 持续压制常开：默认开启，逃生控制台打开即压制。 */
    fun suppressAlwaysOn(ctx: Context): Boolean =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE).getBoolean(KEY_SUPPRESS_ALWAYS_ON, true)

    fun saveSuppressAlwaysOn(ctx: Context, enabled: Boolean) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SUPPRESS_ALWAYS_ON, enabled)
            .apply()
    }

    /** 触发参数：连按次数（2..10，默认 TRIGGER_COUNT）。 */
    fun triggerCount(ctx: Context): Int =
        VolumeKeyTrigger.normalizeTriggerCount(
            ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
                .getInt(KEY_TRIGGER_COUNT, VolumeKeyTrigger.TRIGGER_COUNT)
        )

    fun saveTriggerCount(ctx: Context, value: Int) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_TRIGGER_COUNT, VolumeKeyTrigger.normalizeTriggerCount(value))
            .apply()
    }

    /** 触发参数：连按时间窗毫秒（300..5000，默认 TRIGGER_WINDOW_MS）。 */
    fun triggerWindowMs(ctx: Context): Long =
        VolumeKeyTrigger.normalizeTriggerWindowMs(
            ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
                .getLong(KEY_TRIGGER_WINDOW_MS, VolumeKeyTrigger.TRIGGER_WINDOW_MS)
        )

    fun saveTriggerWindowMs(ctx: Context, value: Long) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_TRIGGER_WINDOW_MS, VolumeKeyTrigger.normalizeTriggerWindowMs(value))
            .apply()
    }

    /** 触发参数：长按音量下时长毫秒（300..5000，默认 LONG_PRESS_MS）。 */
    fun longPressMs(ctx: Context): Long =
        VolumeKeyTrigger.normalizeLongPressMs(
            ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
                .getLong(KEY_LONG_PRESS_MS, VolumeKeyTrigger.LONG_PRESS_MS)
        )

    fun saveLongPressMs(ctx: Context, value: Long) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LONG_PRESS_MS, VolumeKeyTrigger.normalizeLongPressMs(value))
            .apply()
    }
}
