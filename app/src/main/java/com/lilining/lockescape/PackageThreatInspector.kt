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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

object PackageThreatInspector {
    fun inspect(ctx: Context, pkg: String): ThreatAssessment.Profile {
        val userWhitelist = Prefs.userWhitelist(ctx)
        val signals = ThreatAssessment.Signals(
            packageName = pkg,
            appLabel = appLabel(ctx, pkg),
            hasAccessibilityService = hasServiceWithPermission(ctx, pkg, Manifest.permission.BIND_ACCESSIBILITY_SERVICE),
            requestsOverlay = requestsPermission(ctx, pkg, Manifest.permission.SYSTEM_ALERT_WINDOW),
            hasDeviceAdminReceiver = hasReceiverWithPermission(ctx, pkg, Manifest.permission.BIND_DEVICE_ADMIN),
            isWhitelisted = AppSafety.isWhitelistedPackage(pkg, userWhitelist),
            isProtectedSystem = AppSafety.isProtectedPackage(pkg)
        )
        return ThreatAssessment.assess(signals)
    }

    private fun appLabel(ctx: Context, pkg: String): String = try {
        val ai = ctx.packageManager.getApplicationInfo(pkg, 0)
        ctx.packageManager.getApplicationLabel(ai).toString()
    } catch (_: PackageManager.NameNotFoundException) {
        ""
    }

    private fun requestsPermission(ctx: Context, pkg: String, permission: String): Boolean = try {
        val info = ctx.packageManager.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS)
        info.requestedPermissions?.contains(permission) == true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }

    private fun hasServiceWithPermission(ctx: Context, pkg: String, permission: String): Boolean = try {
        val info = ctx.packageManager.getPackageInfo(pkg, PackageManager.GET_SERVICES)
        info.services?.any { it.packageName == pkg && it.permission == permission } == true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }

    private fun hasReceiverWithPermission(ctx: Context, pkg: String, permission: String): Boolean = try {
        val info = ctx.packageManager.getPackageInfo(pkg, PackageManager.GET_RECEIVERS)
        info.receivers?.any { it.packageName == pkg && it.permission == permission } == true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
}
