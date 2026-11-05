package com.example.util.simpletimetracker.feature_capture.view

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.util.simpletimetracker.domain.parser.ApiKeyRepo
import com.example.util.simpletimetracker.domain.pending.model.PendingRecord
import com.example.util.simpletimetracker.domain.pending.model.PendingRecordStatus
import com.example.util.simpletimetracker.domain.pending.repo.PendingRecordRepo
import com.example.util.simpletimetracker.domain.seed.CategorySeeder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 「记一笔」输入界面。
 *
 * 只负责收集一段文字并**先落库**，不关心文字是打字来的还是输入法语音来的。
 * 解析在后续阶段接入，且必须发生在这里之后。
 */
@AndroidEntryPoint
class CaptureActivity : AppCompatActivity() {

    @Inject
    lateinit var pendingRecordRepo: PendingRecordRepo

    @Inject
    lateinit var apiKeyRepo: ApiKeyRepo

    @Inject
    lateinit var categorySeeder: CategorySeeder

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val padding = (24 * resources.displayMetrics.density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, padding, padding, padding)
        }

        val title = TextView(this).apply {
            text = "记一笔"
            textSize = 20f
        }

        val settingsButton = Button(this).apply {
            text = "设置"
            setOnClickListener { showKeyDialog() }
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        header.addView(
            title,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
        )
        header.addView(settingsButton)

        input = EditText(this).apply {
            hint = "说点什么，或者打字…"
            minLines = 3
            gravity = Gravity.TOP
        }

        val saveButton = Button(this).apply {
            text = "保存"
            setOnClickListener {
                val text = input.text?.toString()?.trim().orEmpty()
                if (text.isEmpty()) {
                    Toast.makeText(this@CaptureActivity, "没有内容", Toast.LENGTH_SHORT).show()
                } else {
                    save(text)
                }
            }
        }

        root.addView(header)
        root.addView(
            input,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f,
            ),
        )
        root.addView(saveButton)

        setContentView(root)

        scope.launch { categorySeeder.ensure() }
    }

    private fun save(rawText: String) {
        scope.launch {
            pendingRecordRepo.add(
                PendingRecord(
                    rawText = rawText,
                    status = PendingRecordStatus.PENDING,
                    createdAt = System.currentTimeMillis(),
                ),
            )
            Toast.makeText(this@CaptureActivity, "已记录", Toast.LENGTH_SHORT).show()
            input.text?.clear()
        }
    }

    private fun showKeyDialog() {
        val edit = EditText(this).apply {
            setText(apiKeyRepo.get().orEmpty())
            hint = "sk-..."
            setPadding(48, 24, 48, 24)
        }

        AlertDialog.Builder(this)
            .setTitle("DeepSeek API Key")
            .setMessage("只保存在本机，不会上传到其他地方。")
            .setView(edit)
            .setPositiveButton("保存") { _, _ ->
                apiKeyRepo.set(edit.text?.toString().orEmpty())
                Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("清除") { _, _ ->
                apiKeyRepo.clear()
                Toast.makeText(this, "已清除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
