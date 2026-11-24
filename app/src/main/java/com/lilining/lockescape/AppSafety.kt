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

object AppSafety {
    private val PROTECTED_PACKAGES = setOf(
        "android",
        "com.android.systemui",
        "com.android.settings",
        "com.android.phone",
        "com.android.server.telecom",
        "com.android.providers.settings",
        "com.android.providers.media",
        "com.android.permissioncontroller",
        "com.google.android.permissioncontroller",
        "com.android.inputmethod.latin",
        "com.google.android.inputmethod.latin",
        "moe.shizuku.privileged.api",
        "moe.shizuku.manager",
        "com.android.launcher3",
        "com.google.android.apps.nexuslauncher",
        "com.google.android.gms"
    )

    private val DEFAULT_WHITELIST_PACKAGES = setOf(
        "com.android.launcher",
        "com.android.launcher2",
        "com.android.launcher3",
        "com.google.android.apps.nexuslauncher",
        "com.miui.home",
        "com.huawei.android.launcher",
        "com.oppo.launcher",
        "com.coloros.launcher",
        "com.vivo.launcher",
        "com.sec.android.app.launcher",
        "com.microsoft.launcher",
        "com.teslacoilsw.launcher"
    )

    fun isProtectedPackage(pkg: String): Boolean = pkg in PROTECTED_PACKAGES

    fun isDefaultWhitelistedPackage(pkg: String): Boolean = pkg in DEFAULT_WHITELIST_PACKAGES

    fun isWhitelistedPackage(pkg: String, userWhitelist: Set<String>): Boolean =
        pkg in DEFAULT_WHITELIST_PACKAGES || pkg in userWhitelist

    fun shouldTrackTopPackage(
        pkg: String,
        userWhitelist: Set<String>,
        inputMethodPackage: String? = null
    ): Boolean =
        isValidPackageName(pkg) && pkg != inputMethodPackage &&
            !isProtectedPackage(pkg) && !isWhitelistedPackage(pkg, userWhitelist)

    fun isValidPackageName(pkg: String): Boolean =
        PACKAGE_NAME_REGEX.matches(pkg)

    fun normalizeUserWhitelist(packages: Set<String>): Set<String> =
        packages.asSequence()
            .map { it.trim() }
            .filter { isValidPackageName(it) }
            .toSet()

    private val PACKAGE_NAME_REGEX = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+$")
}
