package com.example.util.simpletimetracker.feature_capture.view

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.util.simpletimetracker.domain.pending.model.PendingRecord
import com.example.util.simpletimetracker.domain.pending.model.PendingRecordStatus
import com.example.util.simpletimetracker.domain.pending.repo.PendingRecordRepo
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
 * 只负责收集一段文字并**先落库**，不关心文字是打字来的还是输入法语音来的，
 * 也不做解析。任何网络请求都必须发生在这之后。
 */
@AndroidEntryPoint
class CaptureActivity : AppCompatActivity() {

    @Inject
    lateinit var pendingRecordRepo: PendingRecordRepo

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

        root.addView(title)
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

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
