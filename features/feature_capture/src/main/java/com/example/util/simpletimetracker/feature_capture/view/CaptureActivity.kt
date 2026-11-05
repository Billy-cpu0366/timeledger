package com.example.util.simpletimetracker.feature_capture.view

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 「记一笔」输入界面。
 *
 * 只负责收集一段文字并触发保存动作，不关心文字是打字来的还是输入法语音来的，
 * 也不做解析。落库在 T1-4 接入。
 */
class CaptureActivity : AppCompatActivity() {

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
                    // T1-4 会替换为落库
                    Toast.makeText(this@CaptureActivity, "收到：$text", Toast.LENGTH_LONG).show()
                    input.text?.clear()
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
}
