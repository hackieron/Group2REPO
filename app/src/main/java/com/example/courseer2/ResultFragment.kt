package com.example.courseer2

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
            dataBaseHandler.setCurrentQuestionIndex(109)
            // Create a list to store selected chip text
            val selectedChipsList = mutableListOf<String>()

            // Iterate over the ChipGroup's children to find selected chips
// Iterate over the ChipGroup's children to find selected chips
            for (i in 0 until resultChipGroup.childCount) {
                val chipLayout = resultChipGroup.getChildAt(i) as LinearLayout
                val chip = chipLayout.findViewById<Chip>(R.id.resultChip)

                if(chip != null) {

                    chip.setChipBackgroundColorResource(R.color.white)
                    chip.chipStrokeWidth =
                        resources.getDimension(R.dimen.chip_stroke) // Set stroke width
                    chip.setChipStrokeColorResource(R.color.gray)
                    if (chip.isChecked) {
                        selectedChipsList.add(chip.text.toString())
                    }
                }
            }

            // Call the insertPreferences method in the database handler
            dataBaseHandler.insertFields(selectedChipsList)
            val keywords:List<String>
            keywords = mutableListOf()
            val interpretation:List<String>
            interpretation = mutableListOf()
            if (selectedChipsList.contains("Accountancy")) {
                keywords.addAll(listOf("Accountancy", "Accounting", "Accountant"))
                dataBaseHandler.insertInterpretation("Accountancy", "Masters of financial insight, accountants predict, analyze, and audit financial data, ensuring the economic health and integrity of organizations.")
            }

            if (selectedChipsList.contains("Architecture")) {
                keywords.addAll(listOf("Architecture", "Interior Design"))
                dataBaseHandler.insertInterpretation("Architecture", "Transforming concepts into reality, architects craft spaces that harmonize functionality and aesthetics, creating environments tailored to clients' needs.")
            }
            if (selectedChipsList.contains("Broadcasting")) {
                keywords.addAll(listOf("News Reporter", "News Writing", "Sports Writing"))
                dataBaseHandler.insertInterpretation("Broadcasting", "Professionals in broadcasting or communications may work as News Reporters, engaging in News Writing or Sports Writing.")
            }

            if (selectedChipsList.contains("Communications")) {
                keywords.addAll(listOf("News Reporter", "News Writing", "Sports Writing"))
                dataBaseHandler.insertInterpretation("Communications", "Professionals in broadcasting or communications may work as News Reporters, engaging in News Writing or Sports Writing.")
            }

            if (selectedChipsList.contains("Business")) {
                keywords.addAll(listOf("Business", "Entrepreneurship", "Entrepreneur"))
                dataBaseHandler.insertInterpretation("Business", "Navigating the complexities of commerce, business professionals orchestrate cash flows, sales strategies, and revenue streams to drive success.")
            }

            if (selectedChipsList.contains("Marketing")) {
                keywords.addAll(listOf("Marketing", "Advertise", "Advertising"))
                dataBaseHandler.insertInterpretation("Marketing", "Deciphering the art of persuasion, marketers identify the most effective media channels to showcase products or services and captivate target audiences.")
            }

            if (selectedChipsList.contains("Computer Science")) {
                keywords.addAll(listOf("Game Developer", "Software Developer", "Web Developer"))
                dataBaseHandler.insertInterpretation("Computer Science", "Developers in this field design and create games, software, and applications, shaping the digital landscape with innovative solutions.")
            }
            if (selectedChipsList.contains("Information Technology")) {

                keywords.addAll(listOf("Game Developer", "Software Developer", "Web Developer"))
                dataBaseHandler.insertInterpretation("Information Technology", "Developers in this field design and create games, software, and applications, shaping the digital landscape with innovative solutions.")

            }

            if (selectedChipsList.contains("Digital Arts")) {
                keywords.addAll(listOf("Digital Art", "Graphic Artist"))
                dataBaseHandler.insertInterpretation("Digital Arts", "Digital artists, animators, and designers bring creativity to life, shaping visual narratives and captivating audiences in the digital realm.")
            }

            if (selectedChipsList.contains("Engineering")) {
                keywords.addAll(listOf("Structural Design", "Engineering", "Infrastructures"))
                dataBaseHandler.insertInterpretation("Engineering", "Crafting the backbone of society, engineers design public infrastructures, develop machinery, and innovate solutions for a better tomorrow.")
            }

            if (selectedChipsList.contains("Law")) {
                keywords.addAll(listOf("Constitutional Law", "Law", "Criminal Law"))
                dataBaseHandler.insertInterpretation("Law", "Legal professionals navigate the intricacies of justice, specializing in areas such as tax law, commercial law, and international law, to uphold the principles of legality.")
            }

            if (selectedChipsList.contains("Medical Technology")) {
                keywords.addAll(listOf("Medical Technology", "Medic", "Medicine"))
                dataBaseHandler.insertInterpretation("Medical Technology", "Working behind the scenes, medical technologists conduct tests on various body fluids, contributing crucial information for diagnoses and treatments.")
            }

            if (selectedChipsList.contains("Pharmacy")) {
                keywords.addAll(listOf("Pharmacy", "Medicine", "Pharmacist"))
                dataBaseHandler.insertInterpretation("Pharmacy", "Pharmacists play a vital role in healthcare, selecting and dispensing medications to patients based on prescription orders and ensuring their well-being.")
            }

            if (selectedChipsList.contains("Psychology")) {
                keywords.addAll(listOf("Psychology", "Psychiatrist", "Psychiatric"))
                dataBaseHandler.insertInterpretation("Psychology", "Professionals in psychology may work as Psychologists or Psychiatrists, specializing in various mental health aspects.")
            }

            if (selectedChipsList.contains("Aeronautics")) {
                keywords.addAll(listOf("Aeronautics", "Flight", "Aircraft"))
                dataBaseHandler.insertInterpretation("Aeronautics", "Professionals in this field facilitate operational control of commercial flights, ensuring safe and efficient air travel.")
            }

            if (selectedChipsList.contains("Education")) {
                keywords.addAll(listOf("Education", "Teacher", "Teaching"))
                dataBaseHandler.insertInterpretation("Education", "Shaping young minds, educators teach specific subjects, design curricula, and guide students in their academic journeys.")
            }

            if (selectedChipsList.contains("Public Administration")) {
                keywords.addAll(listOf("Public Administration", "Government", "Policies"))
                dataBaseHandler.insertInterpretation("Public Administration", "Serving as the bridge between government and the public, professionals in public administration work to develop policies that benefit society.")
            }

            if (selectedChipsList.contains("Criminal Justice")) {
                keywords.addAll(listOf("Jail", "Police", "Criminal Justice"))
                dataBaseHandler.insertInterpretation("Criminal Justice", "Operating on the frontlines of law enforcement, professionals in criminal justice ensure public safety by investigating and addressing criminal activities.")
            }

            if (selectedChipsList.contains("Culinary")) {
                keywords.addAll(listOf("Culinary", "Food Production", "Food Preparation"))
                dataBaseHandler.insertInterpretation("Culinary", "Culinary experts create culinary masterpieces, from food preparation to presentation, tantalizing taste buds and creating memorable dining experiences.")
            }

            if (selectedChipsList.contains("Agriculture")) {
                keywords.addAll(listOf("Crop Production", "Farmer", "Agriculture"))
                dataBaseHandler.insertInterpretation("Agriculture", "From cultivating crops to managing livestock, agricultural professionals contribute to food production and sustainability.")
            }


            dataBaseHandler.processFieldScores()
            dataBaseHandler.insertPreferences(keywords)
            // Access the hosting activity
            val userViewActivity = activity as? UserView

            // Check if the hosting activity is UserView and not null
            userViewActivity?.onBottomNavItemClicked(userViewActivity.findViewById(R.id.bottom_recom))
        }
        // Add new chips for the top categories
// Add new chips for the top categories with the highest score
        val highestScore = topCategories.firstOrNull()?.value
        val secondHighestScore = topCategories.getOrNull(1)?.value
        val thirdHigestScore = topCategories.getOrNull(2)?.value

        topCategories.filter { it.value == highestScore || it.value == secondHighestScore || it.value == thirdHigestScore }.forEachIndexed { index, entry ->
            val chipLayout = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_result_chip_layout, null) as LinearLayout

            // Set chip text
            val chip: Chip = chipLayout.findViewById(R.id.resultChip)
            val typeface = ResourcesCompat.getFont(requireContext(), R.font.alata)

            chip.text = "${entry.key}"
            chip.typeface = typeface
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            // Set progress bar
            val progressBar = chipLayout.findViewById<ProgressBar>(R.id.resultProgressBar)

// Calculate the width based on the screen size
            val screenWidth = resources.displayMetrics.widthPixels
            val progressBarWidth = (0.45 * screenWidth).toInt() // 30% of the screen width

// Set the calculated width to the ProgressBar
            val layoutParams = progressBar.layoutParams
            layoutParams.width = progressBarWidth
            progressBar.layoutParams = layoutParams

// Set the custom drawable as the progress drawable
            progressBar.progressDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_score_progress)


// Set the progress based on your existing calculation
            val progress = (entry.value.toFloat() / 6 * 100).toInt()
            progressBar.progress = progress

            // Enable chips if there are exactly 3 categories
            if (topCategories.size > 3) {
                proceedButton.isEnabled = false
                chip.isClickable = true
                // Set a checkable chip
                chip.isCheckable = true
                // Add a check change listener to the chip
                chip.setOnCheckedChangeListener { _, isChecked ->
                    // Handle chip selection change
                    if (isChecked) {
                        chip.setChipBackgroundColorResource(R.color.gold)
                        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        // Chip is selected
                        // You can perform actions when a chip is selected
                        selectedChipCount++
                    } else {
                        chip.setChipBackgroundColorResource(R.color.white)
                        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.textgray))
                        // Chip is unselected
                        // You can perform actions when a chip is unselected
                        selectedChipCount--
                    }

                    // Enable or disable the button based on the number of selected chips
                    if (selectedChipCount == 3) {
                        proceedButton.isEnabled = true
                    } else if (selectedChipCount != 3) {
                        proceedButton.isEnabled = false
                    }
                }
            }
            if(topCategories.size <= 3) {
                proceedButton.isEnabled = true
                chip.isChecked = true
                chip.isClickable = false
                chip.isCheckable = false
                chip.setChipBackgroundColorResource(R.color.gold)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
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