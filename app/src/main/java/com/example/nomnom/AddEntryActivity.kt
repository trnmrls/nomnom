package com.example.nomnom

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AddEntryActivity : AppCompatActivity() {

    // PHOTO PICKER
    private val photoPicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                val entryPhoto =
                    findViewById<ImageView>(R.id.entryPhoto)

                val photoPlaceholder =
                    findViewById<LinearLayout>(
                        R.id.photoPlaceholder
                    )

                entryPhoto.setImageURI(uri)
                photoPlaceholder.visibility = View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_entry)

        // BACK BUTTON
        findViewById<TextView>(R.id.backButton)
            .setOnClickListener {
                finish()
            }

        // UPLOAD PHOTO
        findViewById<Button>(R.id.uploadPhotoButton)
            .setOnClickListener {
                photoPicker.launch("image/*")
            }

        // DATE PICKER
        val dateInput =
            findViewById<EditText>(R.id.dateInput)

        dateInput.setOnClickListener {

            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    dateInput.setText(
                        "%02d/%02d/%04d".format(
                            selectedMonth + 1,
                            selectedDay,
                            selectedYear
                        )
                    )
                },
                year,
                month,
                day
            ).show()
        }

        // TIME PICKER
        val timeInput =
            findViewById<EditText>(R.id.timeInput)

        timeInput.setOnClickListener {

            val calendar = Calendar.getInstance()

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->

                    val displayHour =
                        if (selectedHour % 12 == 0) 12
                        else selectedHour % 12

                    val amPm =
                        if (selectedHour < 12) "AM"
                        else "PM"

                    timeInput.setText(
                        "%02d:%02d %s".format(
                            displayHour,
                            selectedMinute,
                            amPm
                        )
                    )
                },
                hour,
                minute,
                false
            ).show()
        }

        // STAR RATING
        val ratingBar =
            findViewById<RatingBar>(R.id.entryRating)

        val ratingDescription =
            findViewById<TextView>(
                R.id.ratingDescription
            )

        ratingBar.rating = 0f

        ratingBar.setOnRatingBarChangeListener {
                _, rating, _ ->

            ratingDescription.text = when (rating.toInt()) {
                1 -> "Not my favorite"
                2 -> "Could be better"
                3 -> "Pretty good!"
                4 -> "Really delicious!"
                5 -> "Absolutely delicious!"
                else -> "Tap a star to rate your meal"
            }
        }

        // SAVE ENTRY VALIDATION
        findViewById<Button>(R.id.saveEntryButton)
            .setOnClickListener {

                if (validateEntry()) {

                    Toast.makeText(
                        this,
                        "Entry validated successfully! (UI Demo)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // FORM VALIDATION
    private fun validateEntry(): Boolean {

        val dishName =
            findViewById<EditText>(R.id.dishNameInput)

        val mealCategory =
            findViewById<RadioGroup>(
                R.id.mealCategoryGroup
            )

        val date =
            findViewById<EditText>(R.id.dateInput)

        val time =
            findViewById<EditText>(R.id.timeInput)

        val location =
            findViewById<EditText>(R.id.locationInput)

        val rating =
            findViewById<RatingBar>(R.id.entryRating)

        // DISH NAME
        if (dishName.text.toString().trim().isEmpty()) {
            dishName.error = "Please enter a dish name"
            dishName.requestFocus()
            return false
        }

        // MEAL CATEGORY
        if (mealCategory.checkedRadioButtonId == -1) {
            Toast.makeText(
                this,
                "Please select a meal category",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // DATE
        if (date.text.toString().trim().isEmpty()) {
            date.error = "Please select a date"
            date.requestFocus()
            return false
        }

        // TIME
        if (time.text.toString().trim().isEmpty()) {
            time.error = "Please select a time"
            time.requestFocus()
            return false
        }

        // LOCATION
        if (location.text.toString().trim().isEmpty()) {
            location.error = "Please enter a location"
            location.requestFocus()
            return false
        }

        // STAR RATING
        if (rating.rating == 0f) {
            Toast.makeText(
                this,
                "Please rate your food",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }
}
