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

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lilining.lockescape.databinding.ActivityBlacklistBinding
import com.lilining.lockescape.databinding.ItemBlacklistBinding

/**
 * 黑名单（监测预警名单）管理：
 * 命中名单的应用进入前台时，护盾记录事件并弹出逃生控制台。
 * 黑名单不改变逃生动作的豁免规则（豁免由白名单控制）。
 */
class BlacklistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlacklistBinding
    private lateinit var repo: ShieldRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlacklistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repo = ShieldRepository(this)

        binding.btnAdd.setOnClickListener { addEntry() }
        refresh()
    }

    private fun addEntry() {
        val pkg = binding.etPkg.text.toString().trim()
        if (!AppSafety.isValidPackageName(pkg)) {
            toast("包名格式不正确")
            return
        }
        val label = binding.etLabel.text.toString().trim().ifBlank { pkg }
        repo.addBlacklist(pkg, label)
        binding.etPkg.text?.clear()
        binding.etLabel.text?.clear()
        refresh()
        toast("已加入黑名单：" + pkg)
    }

    private fun refresh() {
        binding.listContainer.removeAllViews()
        val entries = repo.blacklist()
        binding.tvEmpty.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
        for (entry in entries) {
            val item = ItemBlacklistBinding.inflate(layoutInflater, binding.listContainer, false)
            item.tvPkg.text = entry.packageName
            item.tvLabel.text = entry.label
            item.btnRemove.setOnClickListener {
                repo.remove(entry.packageName, "blacklist")
                refresh()
                toast("已移除：" + entry.packageName)
            }
            binding.listContainer.addView(item.root)
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
