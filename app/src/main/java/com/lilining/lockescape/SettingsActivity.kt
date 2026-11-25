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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lilining.lockescape.databinding.ActivitySettingsBinding

/**
 * 触发参数设置：连按次数 / 连按时间窗 / 长按时长。
 * 保存后即时生效（无障碍服务在按键事件中同步参数）。
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        refresh()

        binding.btnSave.setOnClickListener {
            val count = binding.etTriggerCount.text.toString().toIntOrNull()
            val window = binding.etTriggerWindowMs.text.toString().toLongOrNull()
            val longPress = binding.etLongPressMs.text.toString().toLongOrNull()
            if (count == null || window == null || longPress == null) {
                toast("请输入有效数字")
                return@setOnClickListener
            }
            Prefs.saveTriggerCount(this, count)
            Prefs.saveTriggerWindowMs(this, window)
            Prefs.saveLongPressMs(this, longPress)
            refresh()
            toast("已保存，触发参数即时生效")
        }

        binding.btnReset.setOnClickListener {
            Prefs.saveTriggerCount(this, VolumeKeyTrigger.TRIGGER_COUNT)
            Prefs.saveTriggerWindowMs(this, VolumeKeyTrigger.TRIGGER_WINDOW_MS)
            Prefs.saveLongPressMs(this, VolumeKeyTrigger.LONG_PRESS_MS)
            refresh()
            toast("已恢复默认参数")
        }
    }

    private fun refresh() {
        binding.etTriggerCount.setText(Prefs.triggerCount(this).toString())
        binding.etTriggerWindowMs.setText(Prefs.triggerWindowMs(this).toString())
        binding.etLongPressMs.setText(Prefs.longPressMs(this).toString())
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
