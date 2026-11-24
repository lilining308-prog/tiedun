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
import org.junit.Assert.assertTrue
import org.junit.Test

class ThreatAssessmentTest {
    @Test
    fun accessibilityOverlayAndAdminProduceHighRiskProfile() {
        val profile = ThreatAssessment.assess(
            ThreatAssessment.Signals(
                packageName = "com.bad.locker",
                hasAccessibilityService = true,
                requestsOverlay = true,
                hasDeviceAdminReceiver = true,
                isWhitelisted = false,
                isProtectedSystem = false
            )
        )

        assertEquals(ThreatAssessment.Risk.HIGH, profile.risk)
        assertTrue(profile.reasons.any { it.contains("无障碍") })
        assertTrue(profile.reasons.any { it.contains("悬浮窗") })
        assertTrue(profile.reasons.any { it.contains("设备管理器") })
    }

    @Test
    fun whitelistedPackageIsIgnoredEvenWithDangerousSignals() {
        val profile = ThreatAssessment.assess(
            ThreatAssessment.Signals(
                packageName = "com.safe.app",
                hasAccessibilityService = true,
                requestsOverlay = true,
                hasDeviceAdminReceiver = true,
                isWhitelisted = true,
                isProtectedSystem = false
            )
        )

        assertEquals(ThreatAssessment.Risk.IGNORED, profile.risk)
    }
}
