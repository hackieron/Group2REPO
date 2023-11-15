package com.example.courseer2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ResultFragment : Fragment() {

    private lateinit var topCategories: List<Map.Entry<String, Int>>
    private var selectedChipCount: Int = 0
    private lateinit var proceedButton: Button
    private lateinit var resultLayout: RelativeLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)
        proceedButton = view.findViewById(R.id.recommend)
        resultLayout = view.findViewById(R.id.resultLayout)
        // Display the top categories in the ChipGroup with progress bars
        displayTopCategories(view)

        return view
    }

    private fun displayTopCategories(view: View) {
        // Assuming you have a ChipGroup in your layout with the id "resultChipGroup"
        val resultChipGroup: ChipGroup = view.findViewById(R.id.resultChipGroup)
        val dataBaseHandler = DataBaseHandler(requireContext())
        proceedButton.setOnClickListener {
            // Create a list to store selected chip text
            val selectedChipsList = mutableListOf<String>()

            // Iterate over the ChipGroup's children to find selected chips
// Iterate over the ChipGroup's children to find selected chips
            for (i in 0 until resultChipGroup.childCount) {
                val chipLayout = resultChipGroup.getChildAt(i) as LinearLayout
                val chip = chipLayout.findViewById<Chip>(R.id.resultChip)

                if (chip.isChecked) {
                    selectedChipsList.add(chip.text.toString())
                }
            }

            // Call the insertPreferences method in the database handler
            dataBaseHandler.insertPreferences(selectedChipsList)

            resultLayout.visibility = View.GONE

            // Access the hosting activity
            val userViewActivity = activity as? UserView

            // Check if the hosting activity is UserView and not null
            userViewActivity?.onBottomNavItemClicked(userViewActivity.findViewById(R.id.bottom_recom))
        }
        // Add new chips for the top categories
        topCategories.forEachIndexed { index, entry ->
            val chipLayout = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_result_chip_layout, null) as LinearLayout

            // Set chip text
            val chip: Chip = chipLayout.findViewById(R.id.resultChip)
            chip.text = "${entry.key}"

            // Set progress bar
            val progressBar: ProgressBar = chipLayout.findViewById(R.id.resultProgressBar)
            val progress = (entry.value.toFloat() / 6 * 100).toInt()
            progressBar.progress = progress

            // Enable chips if there are exactly 3 categories
            if (topCategories.size != 3) {
                proceedButton.isEnabled = false
                chip.isClickable = true
                // Set a checkable chip
                chip.isCheckable = true
                // Add a check change listener to the chip
                chip.setOnCheckedChangeListener { _, isChecked ->
                    // Handle chip selection change
                    if (isChecked) {
                        // Chip is selected
                        // You can perform actions when a chip is selected
                        selectedChipCount++
                    } else {
                        // Chip is unselected
                        // You can perform actions when a chip is unselected
                        selectedChipCount--
                    }

                    // Enable or disable the button based on the number of selected chips
                    if (selectedChipCount == 3) {
                        proceedButton.isEnabled = true
                    }
                    else if(selectedChipCount != 3){
                        proceedButton.isEnabled = false
                    }
                }
            } else {

                proceedButton.isEnabled = true
                chip.isClickable = false
                chip.isCheckable = false
            }

            // Add chip to the ChipGroup
            resultChipGroup.addView(chipLayout)
        }
    }

    companion object {
        fun newInstance(topCategories: List<Map.Entry<String, Int>>): ResultFragment {
            val fragment = ResultFragment()
            fragment.topCategories = topCategories
            return fragment
        }
    }
}
