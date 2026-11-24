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

data class ProtectedTargetEntity(
    val packageName: String,
    val kind: String,
    val label: String,
    val updatedAt: Long = System.currentTimeMillis()
)

data class ShieldEventEntity(
    val id: Long = 0,
    val packageName: String,
    val label: String,
    val source: String,
    val action: String,
    val result: String,
    val occurredAt: Long = System.currentTimeMillis()
)
