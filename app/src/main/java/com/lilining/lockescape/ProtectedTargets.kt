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

import android.content.Context
import android.content.Intent
import android.provider.Settings

/** Resolve device-specific escape targets without relying solely on static launcher/IME lists. */
internal class ProtectedTargets(
    private val ownPackage: String,
    private val homePackage: String?,
    val inputMethodPackage: String?
) {
    fun canActOn(pkg: String, whitelist: Set<String>): Boolean =
        pkg != ownPackage && pkg != homePackage && pkg != inputMethodPackage &&
            AppSafety.shouldTrackTopPackage(pkg, whitelist, inputMethodPackage)

    companion object {
        fun resolvedHomePackage(pkg: String?): String? =
            pkg?.takeUnless { it.isBlank() || it == "android" }

        fun from(ctx: Context): ProtectedTargets {
            val home = resolvedHomePackage(ctx.packageManager.resolveActivity(
                Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
                0
            )?.activityInfo?.packageName)
            val ime = Settings.Secure.getString(
                ctx.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD
            )?.substringBefore('/')
            return ProtectedTargets(ctx.packageName, home, ime)
        }
    }
}
