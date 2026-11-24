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

/** Tracks an actionable window without accidentally targeting our own rescue UI. */
class RescueTarget {
    private var packageName = ""
    private var observedAt = 0L

    fun onWindow(pkg: String, ownPackage: String, whitelist: Set<String>,
                 inputMethodPackage: String? = null, nowMs: Long = System.currentTimeMillis()) {
        if (pkg == ownPackage) return
        // Returning to a launcher means the user left the suspect app — revoke.
        if (AppSafety.isDefaultWhitelistedPackage(pkg)) {
            clear()
            return
        }
        // Keyboard and other system UI windows must not become the rescue target, but
        // they also must not wipe a still-valid capture — otherwise the volume-key
        // trigger (which raises SystemUI) erases the package the user just selected.
        if (pkg == inputMethodPackage || AppSafety.isProtectedPackage(pkg)) {
            return
        }
        if (isActionable(pkg, ownPackage, whitelist, inputMethodPackage)) {
            packageName = pkg
            observedAt = nowMs
        } else {
            clear()
        }
    }

    fun current(nowMs: Long = System.currentTimeMillis()): String =
        if (observedAt != 0L && nowMs >= observedAt && nowMs - observedAt <= EXPIRY_MS) packageName else ""

    fun isActionable(pkg: String, ownPackage: String, whitelist: Set<String>,
                     inputMethodPackage: String? = null): Boolean =
        pkg != ownPackage && pkg != inputMethodPackage && AppSafety.isValidPackageName(pkg) &&
            !AppSafety.isProtectedPackage(pkg) && !AppSafety.isWhitelistedPackage(pkg, whitelist)

    fun canActOn(pkg: String, ownPackage: String, whitelist: Set<String>,
                 inputMethodPackage: String? = null, nowMs: Long = System.currentTimeMillis()): Boolean =
        current(nowMs) == pkg && isActionable(pkg, ownPackage, whitelist, inputMethodPackage)

    fun clear() {
        packageName = ""
        observedAt = 0L
    }

    companion object { const val EXPIRY_MS = 120_000L }
}
