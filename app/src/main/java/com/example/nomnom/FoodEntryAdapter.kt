
package com.example.nomnom

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodEntryAdapter(
    private var entries: List<FoodEntry>,
    private val onDeleteClick: (FoodEntry) -> Unit
) : RecyclerView.Adapter<FoodEntryAdapter.FoodViewHolder>() {

    // VIEW HOLDER
    class FoodViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val image: ImageView =
            view.findViewById(R.id.foodImage)

        val name: TextView =
            view.findViewById(R.id.foodName)

        val category: TextView =
            view.findViewById(R.id.foodCategory)

        val location: TextView =
            view.findViewById(R.id.foodLocation)

        val rating: RatingBar =
            view.findViewById(R.id.foodRating)

        val deleteButton: ImageButton =
            view.findViewById(R.id.deleteFoodButton)
    }

    // CREATE FOOD CARD
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_food_entry,
                parent,
                false
            )

        return FoodViewHolder(view)
    }

    // DISPLAY FOOD ENTRY DATA
    override fun onBindViewHolder(
        holder: FoodViewHolder,
        position: Int
    ) {

        val entry = entries[position]

        // DISH NAME
        holder.name.text = entry.name

        // MEAL CATEGORY
        holder.category.text = entry.category.uppercase()

        // LOCATION
        holder.location.text = entry.location

        // STAR RATING
        holder.rating.rating = entry.rating

        // FOOD PHOTO
        holder.image.setImageDrawable(null)

        entry.photoUri?.let { photo ->
            holder.image.setImageURI(
                Uri.parse(photo)
            )
        }

        // DELETE BUTTON
        holder.deleteButton.setOnClickListener {
            onDeleteClick(entry)
        }
    }

    // NUMBER OF FOOD CARDS
    override fun getItemCount(): Int {
        return entries.size
    }

    // UPDATE CARDS FOR SEARCH AND FILTERS
    fun updateEntries(newEntries: List<FoodEntry>) {

        entries = newEntries

        notifyDataSetChanged()
    }
}
