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

class VolumeKeyTrigger {
    private var sequenceStartTime = 0L
    private var consecutiveCount = 0
    private var volumeDownPressed = false
    private var volumeUpPressed = false

    fun onVolumeDown(keyCode: Int, now: Long, isRepeat: Boolean = false): Decision {
        if (keyCode != KEY_VOLUME_DOWN && keyCode != KEY_VOLUME_UP) return Decision(false, false, false)
        if (isRepeat || (keyCode == KEY_VOLUME_DOWN && volumeDownPressed) ||
            (keyCode == KEY_VOLUME_UP && volumeUpPressed)) {
            return Decision(scheduleLongPress = false, cancelLongPress = false, triggerEscape = false)
        }

        val scheduleLongPress = keyCode == KEY_VOLUME_DOWN && !volumeDownPressed
        if (keyCode == KEY_VOLUME_DOWN) {
            volumeDownPressed = true
        } else if (keyCode == KEY_VOLUME_UP) {
            volumeUpPressed = true
        }

        if (consecutiveCount == 0 || now < sequenceStartTime || now - sequenceStartTime > TRIGGER_WINDOW_MS) {
            consecutiveCount = 0
            sequenceStartTime = now
        }
        consecutiveCount++

        val triggerEscape = consecutiveCount >= TRIGGER_COUNT
        if (triggerEscape) {
            consecutiveCount = 0
            sequenceStartTime = 0L
        }

        return Decision(
            scheduleLongPress = scheduleLongPress,
            cancelLongPress = false,
            triggerEscape = triggerEscape
        )
    }

    fun onVolumeUp(keyCode: Int): Decision {
        val cancelLongPress = keyCode == KEY_VOLUME_DOWN && volumeDownPressed
        if (keyCode == KEY_VOLUME_DOWN) {
            volumeDownPressed = false
        } else if (keyCode == KEY_VOLUME_UP) {
            volumeUpPressed = false
        }
        return Decision(
            scheduleLongPress = false,
            cancelLongPress = cancelLongPress,
            triggerEscape = false
        )
    }

    fun resetAfterTrigger() {
        consecutiveCount = 0
        sequenceStartTime = 0L
    }

    data class Decision(
        val scheduleLongPress: Boolean,
        val cancelLongPress: Boolean,
        val triggerEscape: Boolean
    )

    companion object {
        const val KEY_VOLUME_UP = 24
        const val KEY_VOLUME_DOWN = 25
        const val TRIGGER_COUNT = 4
        const val TRIGGER_WINDOW_MS = 1500L
        const val LONG_PRESS_MS = 1500L
    }
}
