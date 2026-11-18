package com.example.nomnom

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class JournalActivity : AppCompatActivity() {

    // TEMPORARY FRONTEND DATA
    private val foodEntries = mutableListOf(
        FoodEntry(
            id = "1",
            name = "Truffle Mushroom Risotto",
            category = "Lunch",
            location = "Italian Restaurant",
            rating = 5f
        ),
        FoodEntry(
            id = "2",
            name = "Pistachio Gelato",
            category = "Snack",
            location = "Gelato Shop",
            rating = 4f
        )
    )

    private lateinit var adapter: FoodEntryAdapter
    private lateinit var emptyJournalContainer: View

    private var selectedCategory = "All"
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)

        // ADD ENTRY NAVIGATION
        findViewById<Button>(R.id.addMealButton)
            .setOnClickListener {
                startActivity(
                    Intent(this, AddEntryActivity::class.java)
                )
            }

        // RECYCLERVIEW
        val recyclerView =
            findViewById<RecyclerView>(R.id.foodRecyclerView)

        emptyJournalContainer =
            findViewById(R.id.emptyJournalContainer)

        adapter = FoodEntryAdapter(emptyList()) { entry ->
            showDeleteDialog(entry)
        }

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        // SEARCH BAR
        findViewById<EditText>(R.id.searchMeals)
            .addTextChangedListener { text ->
                searchQuery = text.toString()
                updateJournal()
            }

        // MEAL CATEGORY FILTERS
        setupFilter(R.id.filterAll, "All")
        setupFilter(R.id.filterBreakfast, "Breakfast")
        setupFilter(R.id.filterLunch, "Lunch")
        setupFilter(R.id.filterDinner, "Dinner")
        setupFilter(R.id.filterSnack, "Snack")

        updateFilterButtons()
        updateJournal()
    }

    // FILTER BUTTON CLICK LISTENERS
    private fun setupFilter(
        buttonId: Int,
        category: String
    ) {
        findViewById<Button>(buttonId)
            .setOnClickListener {
                selectedCategory = category
                updateFilterButtons()
                updateJournal()
            }
    }

    // UPDATE SELECTED FILTER COLORS
    private fun updateFilterButtons() {

        val filters = mapOf(
            R.id.filterAll to "All",
            R.id.filterBreakfast to "Breakfast",
            R.id.filterLunch to "Lunch",
            R.id.filterDinner to "Dinner",
            R.id.filterSnack to "Snack"
        )

        filters.forEach { (id, category) ->

            val button = findViewById<Button>(id)
            val selected = category == selectedCategory

            button.backgroundTintList =
                ColorStateList.valueOf(
                    getColor(
                        if (selected) R.color.primary
                        else R.color.card_peach
                    )
                )

            button.setTextColor(
                getColor(
                    if (selected) R.color.white
                    else R.color.text_primary
                )
            )
        }
    }

    // SEARCH AND FILTER FOOD ENTRIES
    private fun updateJournal() {

        val filteredEntries = foodEntries.filter { entry ->

            val matchesCategory =
                selectedCategory == "All" ||
                        entry.category.equals(
                            selectedCategory,
                            ignoreCase = true
                        )

            val matchesSearch =
                entry.name.contains(
                    searchQuery,
                    ignoreCase = true
                ) ||
                        entry.location.contains(
                            searchQuery,
                            ignoreCase = true
                        )

            matchesCategory && matchesSearch
        }

        // UPDATE RECYCLERVIEW
        adapter.updateEntries(filteredEntries)

        // SHOW OR HIDE EMPTY STATE
        emptyJournalContainer.visibility =
            if (filteredEntries.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    // DELETE CONFIRMATION DIALOG
    private fun showDeleteDialog(entry: FoodEntry) {

        val dialog = Dialog(this)

        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )

        dialog.setContentView(
            R.layout.dialog_delete_entry
        )

        dialog.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )

        // CONFIRM DELETE
        dialog.findViewById<Button>(
            R.id.confirmDeleteButton
        ).setOnClickListener {

            foodEntries.removeAll {
                it.id == entry.id
            }

            updateJournal()
            dialog.dismiss()
        }

        // CANCEL DELETE
        dialog.findViewById<Button>(
            R.id.cancelDeleteButton
        ).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.88).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}
