package com.example.shoppinglist

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    private lateinit var bubbleId: String
    private val BUBBLES_KEY = "sub_bubbles"  // Key to store sub-bubbles for this bubble

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Get the bubble ID passed from MainActivity
        bubbleId = intent.getStringExtra("BUBBLE_ID") ?: "default_bubble"

        // Initialize the EditText input field here
        val editTextInput = findViewById<EditText>(R.id.editTextInput)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val backButton = findViewById<Button>(R.id.backButton)
        val bubbleContainer = findViewById<LinearLayout>(R.id.bubbleContainer)

        // Load saved sub-bubbles for the specific bubble
        loadBubbles(bubbleContainer, editTextInput)

        submitButton.setOnClickListener {
            val userInput = editTextInput.text.toString()
            if (userInput.isNotBlank()) {
                val bubbleLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                val bubbleText = TextView(this).apply {
                    text = userInput
                    setBackgroundResource(R.drawable.text_bubble_background)
                    setPadding(16, 8, 16, 8)
                    setTextColor(resources.getColor(android.R.color.white, null))
                    textSize = 16f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 8, 8, 8)
                    }
                }

                val editButton = Button(this).apply {
                    text = "Szerkesztés"
                    setOnClickListener {
                        editTextInput.setText(userInput)
                        bubbleContainer.removeView(bubbleLayout)
                    }
                }

                val deleteButton = Button(this).apply {
                    text = "Törlés"
                    setOnClickListener {
                        bubbleContainer.removeView(bubbleLayout)
                        saveBubbles(bubbleContainer)
                    }
                }

                bubbleLayout.addView(bubbleText)
                bubbleLayout.addView(editButton)
                bubbleLayout.addView(deleteButton)

                bubbleContainer.addView(bubbleLayout)

                // Save sub-bubbles
                saveBubbles(bubbleContainer)

                // Clear input field
                editTextInput.text.clear()
            }
        }

        backButton.setOnClickListener {
            finish() // Close this activity and return to MainActivity
        }
    }

    private fun saveBubbles(bubbleContainer: LinearLayout) {
        val sharedPreferences = getSharedPreferences(bubbleId, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val bubbleList = mutableListOf<String>()
        for (i in 0 until bubbleContainer.childCount) {
            val bubbleLayout = bubbleContainer.getChildAt(i) as LinearLayout
            val bubbleText = (bubbleLayout.getChildAt(0) as TextView).text.toString()
            bubbleList.add(bubbleText)
        }

        // Save as a comma-separated string
        editor.putString(BUBBLES_KEY, bubbleList.joinToString(","))
        editor.apply()
    }

    private fun loadBubbles(bubbleContainer: LinearLayout, editTextInput: EditText) {
        val sharedPreferences = getSharedPreferences(bubbleId, Context.MODE_PRIVATE)
        val savedBubbles = sharedPreferences.getString(BUBBLES_KEY, "") ?: ""

        if (savedBubbles.isNotEmpty()) {
            val bubbleList = savedBubbles.split(",")
            for (bubbleText in bubbleList) {
                val bubbleLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                val textView = TextView(this).apply {
                    text = bubbleText
                    setBackgroundResource(R.drawable.text_bubble_background)
                    setPadding(16, 8, 16, 8)
                    setTextColor(resources.getColor(android.R.color.white, null))
                    textSize = 16f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 8, 8, 8)
                    }
                }

                val editButton = Button(this).apply {
                    text = "Szerkesztés"
                    setOnClickListener {
                        editTextInput.setText(bubbleText)
                        bubbleContainer.removeView(bubbleLayout)
                    }
                }

                val deleteButton = Button(this).apply {
                    text = "Törlés"
                    setOnClickListener {
                        bubbleContainer.removeView(bubbleLayout)
                        saveBubbles(bubbleContainer)
                    }
                }

                bubbleLayout.addView(textView)
                bubbleLayout.addView(editButton)
                bubbleLayout.addView(deleteButton)

                bubbleContainer.addView(bubbleLayout)
            }
        }
    }
}
