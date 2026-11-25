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
import org.junit.Test

class ShieldRepositoryTest {

    @Test
    fun sanitizeLabelReplacesSeparator() {
        assertEquals("a/b", ShieldRepository.sanitizeLabel("a|b"))
    }

    @Test
    fun sanitizeLabelStripsNewlines() {
        assertEquals("a  b", ShieldRepository.sanitizeLabel("a\r\nb"))
        assertEquals("a b", ShieldRepository.sanitizeLabel("a\nb"))
    }

    @Test
    fun sanitizeLabelTrimsWhitespace() {
        assertEquals("label", ShieldRepository.sanitizeLabel("  label  "))
    }

    @Test
    fun sanitizeLabelKeepsPlainLabel() {
        assertEquals("恶意应用", ShieldRepository.sanitizeLabel("恶意应用"))
    }
}
