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

class VolumeKeyTriggerConfigTest {

    @Test
    fun customTriggerCountOfTwo() {
        val trigger = VolumeKeyTrigger(triggerCount = 2)
        val first = trigger.onVolumeDown(VolumeKeyTrigger.KEY_VOLUME_DOWN, now = 1_000L)
        assertFalse(first.triggerEscape)
        trigger.onVolumeUp(VolumeKeyTrigger.KEY_VOLUME_DOWN)
        val second = trigger.onVolumeDown(VolumeKeyTrigger.KEY_VOLUME_DOWN, now = 1_300L)
        assertTrue(second.triggerEscape)
    }

    @Test
    fun windowExpiryResetsSequence() {
        val trigger = VolumeKeyTrigger(triggerCount = 2, triggerWindowMs = 500L)
        trigger.onVolumeDown(VolumeKeyTrigger.KEY_VOLUME_DOWN, now = 1_000L)
        trigger.onVolumeUp(VolumeKeyTrigger.KEY_VOLUME_DOWN)
        val late = trigger.onVolumeDown(VolumeKeyTrigger.KEY_VOLUME_DOWN, now = 2_500L)
        assertFalse(late.triggerEscape)
    }

    @Test
    fun configureChangesBehaviorAtRuntime() {
        val trigger = VolumeKeyTrigger(triggerCount = 3)
        trigger.onVolumeDown(VolumeKeyTrigger.KEY_VOLUME_DOWN, now = 1_000L)
        trigger.onVolumeUp(VolumeKeyTrigger.KEY_VOLUME_DOWN)
        trigger.configure(2, VolumeKeyTrigger.TRIGGER_WINDOW_MS)
        val second = trigger.onVolumeDown(VolumeKeyTrigger.KEY_VOLUME_DOWN, now = 1_300L)
        assertTrue(second.triggerEscape)
    }

    @Test
    fun normalizeClampsValues() {
        assertEquals(VolumeKeyTrigger.MIN_TRIGGER_COUNT, VolumeKeyTrigger.normalizeTriggerCount(0))
        assertEquals(VolumeKeyTrigger.MAX_TRIGGER_COUNT, VolumeKeyTrigger.normalizeTriggerCount(99))
        assertEquals(5, VolumeKeyTrigger.normalizeTriggerCount(5))
        assertEquals(VolumeKeyTrigger.MIN_PRESS_MS, VolumeKeyTrigger.normalizeTriggerWindowMs(1L))
        assertEquals(VolumeKeyTrigger.MAX_PRESS_MS, VolumeKeyTrigger.normalizeLongPressMs(999_999L))
        assertEquals(1_500L, VolumeKeyTrigger.normalizeLongPressMs(1_500L))
    }

    @Test
    fun constructorClampsOutOfRangeCount() {
        val trigger = VolumeKeyTrigger(triggerCount = 0)
        repeat(VolumeKeyTrigger.MIN_TRIGGER_COUNT - 1) { offset ->
            trigger.onVolumeDown(VolumeKeyTrigger.KEY_VOLUME_DOWN, now = 1_000L + offset * 100L)
            trigger.onVolumeUp(VolumeKeyTrigger.KEY_VOLUME_DOWN)
        }
        val last = trigger.onVolumeDown(VolumeKeyTrigger.KEY_VOLUME_DOWN, now = 1_600L)
        assertTrue(last.triggerEscape)
    }
}
