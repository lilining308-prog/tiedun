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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdbGuardDaemonTest {

    @Test
    fun scriptChecksOwnServiceAndReappendsWhenMissing() {
        val s = AdbGuardDaemon.buildScript()
        assertTrue(s.contains("settings get secure enabled_accessibility_services"))
        assertTrue(s.contains(AdbGuardDaemon.SERVICE_COMPONENT))
        assertTrue(s.contains("settings put secure accessibility_enabled 1"))
        assertTrue(s.contains("sleep 2"))
    }

    @Test
    fun scriptNeverTouchesOtherPackagesOrUninstall() {
        val s = AdbGuardDaemon.buildScript()
        // 只允许 settings put/accessibility_enabled 两类写操作，绝不 pm uninstall / pm disable 其它应用
        assertFalse(s.contains("pm uninstall"))
        assertFalse(s.contains("pm disable"))
        assertFalse(s.contains("force-stop"))
    }
}
