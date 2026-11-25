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

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * ADB/Shizuku 无障碍守护：由 Shizuku 以 shell 身份拉起单条常驻 sh 循环（免 root），
 * 每 2 秒校验 enabled_accessibility_services，若本应用条目被移除则自动补回并启用。
 *
 * 与 RootMonitorDaemon 的差别：
 * - 进程通道为 Shizuku（shell 身份），不依赖 su；
 * - 脚本不做任何文件 I/O（shell 无权读写应用私有目录），
 *   运行状态由应用侧 isRunning / AdbAccessibilityLock.isLocked 推断。
 *
 * 安全边界（硬性）：
 * - 只补回本应用自己的无障碍服务条目，绝不移除其它应用条目；
 * - 绝不阻止卸载、绝不修改包管理器安装状态；
 * - 可随时停止守护进程，停止后不再改写任何系统设置。
 */
object AdbGuardDaemon {

    internal const val PKG = "com.lilining.lockescape"
    internal const val SERVICE_COMPONENT = "com.lilining.lockescape/.EscapeAccessibilityService"
    internal const val INTERVAL_SECONDS = 2

    @Volatile
    private var process: Process? = null

    /**
     * 生成守护脚本。全部为常量拼接，无用户可控内容；
     * shell 变量使用 \$ 转义，避免被 Kotlin 字符串模板吞掉。
     */
    internal fun buildScript(): String = listOf(
        "while :; do",
        "  cur=$(settings get secure enabled_accessibility_services 2>/dev/null)",
        "  [ \"\$cur\" = \"null\" ] && cur=\"\"",
        "  case \":\$cur:\" in",
        "    *\":$PKG/\"*) : ;;",
        "    *)",
        "      if [ -z \"\$cur\" ]; then",
        "        settings put secure enabled_accessibility_services \"$SERVICE_COMPONENT\"",
        "      else",
        "        settings put secure enabled_accessibility_services \"\$cur:$SERVICE_COMPONENT\"",
        "      fi",
        "      settings put secure accessibility_enabled 1",
        "      ;;",
        "  esac",
        "  sleep $INTERVAL_SECONDS",
        "done"
    ).joinToString("\n")

    fun isRunning(): Boolean {
        val p = process ?: return false
        return try {
            p.exitValue()
            false
        } catch (_: IllegalThreadStateException) {
            true
        }
    }

    /** 拉起守护循环。返回是否成功启动。 */
    @Synchronized
    fun start(): Boolean {
        if (isRunning()) return true
        val p = ShizukuProcess.spawnShell(buildScript()) ?: return false
        discardAsync(p.inputStream)
        discardAsync(p.errorStream)
        process = p
        return true
    }

    /** 停止守护进程：销毁远端 sh 循环。 */
    @Synchronized
    fun stop() {
        process?.let { p -> runCatching { p.destroyForcibly() } }
        process = null
    }

    private fun discardAsync(stream: java.io.InputStream) {
        Thread {
            runCatching {
                BufferedReader(InputStreamReader(stream)).use { reader ->
                    while (reader.readLine() != null) { /* discard */ }
                }
            }
        }.apply {
            isDaemon = true
            start()
        }
    }
}
