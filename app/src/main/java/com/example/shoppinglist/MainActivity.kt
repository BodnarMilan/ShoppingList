package com.example.shoppinglist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val BUBBLES_KEY = "bubbles"
    private lateinit var editTextInput: EditText
    private lateinit var bubbleContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextInput = findViewById(R.id.editTextInput)
        bubbleContainer = findViewById(R.id.bubbleContainer)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Load saved bubbles from SharedPreferences
        loadBubbles()

        // Handle submit button click
        submitButton.setOnClickListener {
            val userInput = editTextInput.text.toString()
            if (userInput.isNotBlank()) {
                val bubbleId = userInput.replace(" ", "_") // Create a unique bubble ID based on input

                // Create a new bubble layout
                val bubbleLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                // Create the text bubble
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

                // Create the edit button
                val editButton = Button(this).apply {
                    text = "Szerkesztés"
                    setOnClickListener {
                        editTextInput.setText(userInput) // Set the input to the text bubble value
                        bubbleContainer.removeView(bubbleLayout) // Remove the old bubble
                    }
                }

                // Create the delete button
                val deleteButton = Button(this).apply {
                    text = "Törlés"
                    setOnClickListener {
                        bubbleContainer.removeView(bubbleLayout) // Remove the bubble
                        // Save the remaining bubbles
                        saveBubbles()
                    }
                }

                // Create the open button for each bubble
                val openButton = Button(this).apply {
                    text = "Megnyitás"
                    setOnClickListener {
                        // Open the secondary screen with the unique bubble ID
                        val intent = Intent(this@MainActivity, SecondActivity::class.java).apply {
                            putExtra("BUBBLE_ID", bubbleId) // Pass the unique bubble ID
                        }
                        startActivity(intent)
                    }
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 8, 8, 8)
                    }
                }

                // Add the text bubble, edit, delete, and open buttons to the layout
                bubbleLayout.addView(bubbleText)
                bubbleLayout.addView(editButton)
                bubbleLayout.addView(deleteButton)
                bubbleLayout.addView(openButton)

                // Add the bubble layout to the container
                bubbleContainer.addView(bubbleLayout)

                // Save the bubbles
                saveBubbles()

                // Clear the input field
                editTextInput.text.clear()
            }
        }
    }

    // Function to save bubbles to SharedPreferences
    private fun saveBubbles() {
        val sharedPreferences = getSharedPreferences("main_bubbles", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val bubbleList = mutableListOf<String>()
        for (i in 0 until bubbleContainer.childCount) {
            val bubbleLayout = bubbleContainer.getChildAt(i) as LinearLayout
            val bubbleText = (bubbleLayout.getChildAt(0) as TextView).text.toString()
            bubbleList.add(bubbleText)
        }

        // Save the bubbles list as a single string (separated by commas)
        editor.putString(BUBBLES_KEY, bubbleList.joinToString(","))
        editor.apply()
    }

    // Function to load bubbles from SharedPreferences
    private fun loadBubbles() {
        val sharedPreferences = getSharedPreferences("main_bubbles", Context.MODE_PRIVATE)
        val savedBubbles = sharedPreferences.getString(BUBBLES_KEY, "") ?: ""

        // Split the saved bubbles string and create TextViews for each bubble
        if (savedBubbles.isNotEmpty()) {
            val bubbleList = savedBubbles.split(",")
            for (bubbleText in bubbleList) {
                // Create a new bubble layout
                val bubbleLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                // Create the text bubble
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

                // Create the edit button
                val editButton = Button(this).apply {
                    text = "Szerkesztés"
                    setOnClickListener {
                        editTextInput.setText(bubbleText) // Set the input to the text bubble value
                        bubbleContainer.removeView(bubbleLayout) // Remove the old bubble
                    }
                }

                // Create the delete button
                val deleteButton = Button(this).apply {
                    text = "Törlés"
                    setOnClickListener {
                        bubbleContainer.removeView(bubbleLayout) // Remove the bubble
                        // Save the remaining bubbles
                        saveBubbles()
                    }
                }

                // Create the open button for each bubble
                val openButton = Button(this).apply {
                    text = "Megnyitás"
                    setOnClickListener {
                        // Open the secondary screen with the unique bubble ID
                        val intent = Intent(this@MainActivity, SecondActivity::class.java).apply {
                            putExtra("BUBBLE_ID", bubbleText.replace(" ", "_")) // Pass the bubble ID
                        }
                        startActivity(intent)
                    }
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 8, 8, 8)
                    }
                }

                // Add the text bubble, edit, delete, and open buttons to the layout
                bubbleLayout.addView(textView)
                bubbleLayout.addView(editButton)
                bubbleLayout.addView(deleteButton)
                bubbleLayout.addView(openButton)

                // Add the bubble layout to the container
                bubbleContainer.addView(bubbleLayout)
            }
        }
    }
}
