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

class DaemonServiceTest {
    @Test fun specialUseForegroundTypeOnlyAvailableOnAndroid14AndLater() {
        assertFalse(DaemonService.supportsSpecialUseForegroundType(26))
        assertFalse(DaemonService.supportsSpecialUseForegroundType(29))
        assertFalse(DaemonService.supportsSpecialUseForegroundType(33))
        assertTrue(DaemonService.supportsSpecialUseForegroundType(34))
    }

    @Test fun android14UsesSpecialUseForegroundTypeWhileOlderDevicesUseLegacyCall() {
        assertFalse(DaemonService.supportsSpecialUseForegroundType(27))
        assertFalse(DaemonService.supportsSpecialUseForegroundType(30))
        assertTrue(DaemonService.supportsSpecialUseForegroundType(35))
    }
}
