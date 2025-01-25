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

        // bubble betöltése a SharedPreferences-ből
        loadBubbles()

        // hozzáadás gomb kezelése
        submitButton.setOnClickListener {
            val userInput = editTextInput.text.toString()
            if (userInput.isNotBlank()) {
                val bubbleId = userInput.replace(" ", "_") // egyedi  bubble ID készítése input alapján

                // új bubble kiosztás
                val bubbleLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                // text bubble létrehozása
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

                // szerkesztés gomb létrehozása
                val editButton = Button(this).apply {
                    text = "Szerkesztés"
                    setOnClickListener {
                        editTextInput.setText(userInput) // input a következő text bubble értéke
                        bubbleContainer.removeView(bubbleLayout) // régi bubble törlése
                    }
                }

                // törlés gomb létrehozása
                val deleteButton = Button(this).apply {
                    text = "Törlés"
                    setOnClickListener {
                        bubbleContainer.removeView(bubbleLayout) // bubble törlése
                        // megmaradt bubble-ök mentése
                        saveBubbles()
                    }
                }

                // megnyitás gomb készítése minden bubble-höz
                val openButton = Button(this).apply {
                    text = "Megnyitás"
                    setOnClickListener {
                        // másodlagos beviteli megjelenítő kezeleése a unique bubble ID segítségével
                        val intent = Intent(this@MainActivity, SecondActivity::class.java).apply {
                            putExtra("BUBBLE_ID", bubbleId)
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

                // a text bubble, szerkesztés, törlés, megnyitás gombok layout-hoz való adása
                bubbleLayout.addView(bubbleText)
                bubbleLayout.addView(editButton)
                bubbleLayout.addView(deleteButton)
                bubbleLayout.addView(openButton)

                // kiosztás container-hez adása
                bubbleContainer.addView(bubbleLayout)

                // bubble-ök mentése
                saveBubbles()

                // input mező törlése
                editTextInput.text.clear()
            }
        }
    }

    // bubble mentése a SharedPreferences-be
    private fun saveBubbles() {
        val sharedPreferences = getSharedPreferences("main_bubbles", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val bubbleList = mutableListOf<String>()
        for (i in 0 until bubbleContainer.childCount) {
            val bubbleLayout = bubbleContainer.getChildAt(i) as LinearLayout
            val bubbleText = (bubbleLayout.getChildAt(0) as TextView).text.toString()
            bubbleList.add(bubbleText)
        }

        // bubble lista mentése egyszerű listaként (vesszővel tagolva)
        editor.putString(BUBBLES_KEY, bubbleList.joinToString(","))
        editor.apply()
    }

    // bubble betöltése a SharedPreferences-ből
    private fun loadBubbles() {
        val sharedPreferences = getSharedPreferences("main_bubbles", Context.MODE_PRIVATE)
        val savedBubbles = sharedPreferences.getString(BUBBLES_KEY, "") ?: ""

        // mentett bubble szövegek tagolása és TextViews készítése minden bubble-höz
        if (savedBubbles.isNotEmpty()) {
            val bubbleList = savedBubbles.split(",")
            for (bubbleText in bubbleList) {
                // új bubble layout
                val bubbleLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                // új text bubble
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

                // szerkesztés gomb
                val editButton = Button(this).apply {
                    text = "Szerkesztés"
                    setOnClickListener {
                        editTextInput.setText(bubbleText) // input a következő text bubble értéke
                        bubbleContainer.removeView(bubbleLayout) // régi bubble törlése
                    }
                }

                // törlés gomb
                val deleteButton = Button(this).apply {
                    text = "Törlés"
                    setOnClickListener {
                        bubbleContainer.removeView(bubbleLayout) // régi bubble törlése
                        // megmaradt bubble mentése
                        saveBubbles()
                    }
                }

                // minden bubble megnyitás gombjának elkészítése
                val openButton = Button(this).apply {
                    text = "Megnyitás"
                    setOnClickListener {
                        // másodlagos beviteli megjelenítő kezeleése a unique bubble ID segítségével
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

                // a text bubble, szerkesztés, törlés, megnyitás gombok layout-hoz való adása
                bubbleLayout.addView(textView)
                bubbleLayout.addView(editButton)
                bubbleLayout.addView(deleteButton)
                bubbleLayout.addView(openButton)

                // bubble layout container-hez való adása
                bubbleContainer.addView(bubbleLayout)
            }
        }
    }
}
