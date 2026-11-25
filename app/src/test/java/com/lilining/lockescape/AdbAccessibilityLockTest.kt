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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdbAccessibilityLockTest {

    @Test
    fun normalizeTreatsNullAndWhitespace() {
        assertEquals("", AdbAccessibilityLock.normalizeServices("null"))
        assertEquals("", AdbAccessibilityLock.normalizeServices("  null \n"))
        assertEquals("a/b", AdbAccessibilityLock.normalizeServices(" a/b \n"))
    }

    @Test
    fun mergeAppendsWithColonOrReturnsSingle() {
        assertEquals(AdbAccessibilityLock.SERVICE_COMPONENT,
            AdbAccessibilityLock.mergeServiceEntry("", AdbAccessibilityLock.SERVICE_COMPONENT))
        assertEquals("com.foo/.Bar:" + AdbAccessibilityLock.SERVICE_COMPONENT,
            AdbAccessibilityLock.mergeServiceEntry("com.foo/.Bar", AdbAccessibilityLock.SERVICE_COMPONENT))
    }

    @Test
    fun removeOnlyStripsOwnEntries() {
        val cur = "com.foo/.Bar:com.lilining.lockescape/.EscapeAccessibilityService:com.lilining.lockescape"
        assertEquals("com.foo/.Bar", AdbAccessibilityLock.removeServiceEntry(cur, AdbAccessibilityLock.PKG))
    }

    @Test
    fun containsOwnDetectsPresence() {
        assertTrue(AdbAccessibilityLock.containsOwn("com.lilining.lockescape/.EscapeAccessibilityService"))
        assertFalse(AdbAccessibilityLock.containsOwn("com.foo/.Bar"))
        assertFalse(AdbAccessibilityLock.containsOwn(""))
    }

    @Test
    fun manualCommandMatchesServiceComponent() {
        val cmd = AdbAccessibilityLock.manualLockCommand
        assertTrue(cmd.contains(AdbAccessibilityLock.SERVICE_COMPONENT))
        assertTrue(cmd.contains("accessibility_enabled"))
    }
}
