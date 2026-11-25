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

import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

/**
 * Shizuku 进程工具：缓存 newProcess 反射入口，统一提供
 * 双路排空 + 超时销毁的命令执行，避免管道塞满或无限挂死。
 */
internal object ShizukuProcess {

    private val newProcessMethod: Method? by lazy {
        runCatching {
            Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java, Array<String>::class.java, String::class.java
            ).apply { isAccessible = true }
        }.getOrNull()
    }

    fun isAvailable(): Boolean = newProcessMethod != null

    fun spawn(argv: Array<String>): Process? = runCatching {
        newProcessMethod?.invoke(null, argv, null, null) as? Process
    }.getOrNull()

    fun spawnShell(command: String): Process? = spawn(arrayOf("sh", "-c", command))

    data class CmdResult(val ok: Boolean, val output: String)

    /** 同步执行命令并回收输出：双路排空 + 超时销毁。 */
    fun runForOutput(command: String, timeoutMs: Long = 8000L): CmdResult {
        val process = spawnShell(command) ?: return CmdResult(false, "Shizuku newProcess 不可用")
        val outBuf = StringBuilder()
        val errBuf = StringBuilder()
        val outThread = drain(process.inputStream, outBuf)
        val errThread = drain(process.errorStream, errBuf)
        val finished = runCatching {
            process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
        }.getOrDefault(false)
        if (!finished) {
            runCatching { process.destroyForcibly() }
            return CmdResult(false, "命令超时（" + timeoutMs + "ms）")
        }
        runCatching { outThread.join(500) }
        runCatching { errThread.join(500) }
        val code = runCatching { process.exitValue() }.getOrDefault(-1)
        val output = listOf(outBuf.toString().trim(), errBuf.toString().trim())
            .filter { it.isNotEmpty() }
            .joinToString("\n")
        return CmdResult(code == 0, output)
    }

    private fun drain(stream: InputStream, into: StringBuilder): Thread =
        Thread {
            runCatching {
                BufferedReader(InputStreamReader(stream)).use { reader ->
                    while (true) {
                        val line = reader.readLine() ?: break
                        synchronized(into) { into.append(line).append('\n') }
                    }
                }
            }
        }.apply {
            isDaemon = true
            start()
        }
}
