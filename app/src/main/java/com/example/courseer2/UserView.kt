package com.example.courseer2

import android.content.BroadcastReceiver
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import android.content.IntentFilter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import com.example.courseer2.databinding.ActivityUserViewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


import android.content.Context
import android.graphics.Rect

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import androidx.appcompat.app.AlertDialog

import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.Gravity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.FrameLayout.LayoutParams
import androidx.cardview.widget.CardView
import kotlin.math.max
import androidx.recyclerview.widget.RecyclerView



class UserView : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var selectedNavItem = R.id.bottom_prof
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityUserViewBinding
    private lateinit var viewModel: YourViewModel
    private lateinit var dataBaseHandler: DataBaseHandler
    private lateinit var imageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var strandTextView: TextView
    private val connectivityReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (!isInternetAvailable()) {
                // If no internet, show a dialog and go back to MainActivity
                showNoInternetDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerConnectivityReceiver()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        if (!isInternetAvailable()) {
            // If no internet, show a dialog and go back to MainActivity
            showNoInternetDialog()
            return
        }
        binding = ActivityUserViewBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

// Assuming you have defined the color 'gold' in your resources
        val goldColor = ContextCompat.getColor(this, R.color.gold)

// Set icon tint color
        bottomNavigationView.itemIconTintList = ColorStateList.valueOf(goldColor)

// Set text color
        bottomNavigationView.itemTextColor = ColorStateList.valueOf(goldColor)

        dataBaseHandler = DataBaseHandler(this)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.setDirection(DrawerArrowDrawable.ARROW_DIRECTION_END)

        toggle.drawerArrowDrawable.color = getResources().getColor(R.color.white)

        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        val navigationView = findViewById<NavigationView>(R.id.navigation_drawer)
        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()


        val params = navigationView.layoutParams as DrawerLayout.LayoutParams
        params.width = width
        navigationView.layoutParams = params
        bottomNavigationView.itemIconSize =
            resources.getDimensionPixelSize(R.dimen.bottom_nav_icon_size)
        bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.bottom_prof -> openFragment(UserProfile())
                R.id.bottom_apt -> openFragment(Aptitude())
                R.id.bottom_recom -> openFragment(CombinedRecommendFragment())
                R.id.bottom_fav -> openFragment(Favorites())

            }
            val view = bottomNavigationView.findViewById<View>(item.itemId)
            onBottomNavItemClicked(view)
            true
        }

        fragmentManager = supportFragmentManager
        openFragment(UserProfile())
        setTitleForFragment(UserProfile())
        binding.reset.setOnClickListener {
            // Create an AlertDialog.Builder
            val builder = AlertDialog.Builder(this)

            // Set the title and message for the dialog
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to reset your profile?\nWhat would be cleared?\n1. Interests and Career Preferences\n2. Career-Interest Test\n3. Recommendations")

            // Set positive and negative buttons
            builder.setPositiveButton("Yes") { _, _ ->
                // User clicked Yes, clear the database
                clearDatabase()
                Toast.makeText(this, "Profile is reset", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                // User clicked No, dismiss the dialog
                dialog.dismiss()
            }

            // Create and show the AlertDialog
            val alertDialog = builder.create()
            alertDialog.show()
        }

        val headerView = navigationView.getHeaderView(0)

        imageView = headerView.findViewById<ImageView>(R.id.imageView)
        nameTextView = headerView.findViewById<TextView>(R.id.nameTextView)
        strandTextView = headerView.findViewById<TextView>(R.id.strandTextView)
        loadImageAndUserDataFromDatabase()

        dataBaseHandler = DataBaseHandler(this)

        // Observe changes in your LiveData for user data
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
            binding.toolbar.navigationIcon = toggle.drawerArrowDrawable
        }

        val darkerGrayColor = ContextCompat.getColor(this, android.R.color.darker_gray)

// Set icon tint color selector
        val iconTintList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(goldColor, darkerGrayColor)
        )

        bottomNavigationView.itemIconTintList = iconTintList

        val isFirstLaunch = getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE)
            .getBoolean("is_first_launch", true)

        // Show the guide button or sequence based on whether it's the first launch
        if (isFirstLaunch) {
            showGuideSequence(getGuideTargets())
            markGuideAsShown()
        }

        // Always show the guide button
        showGuideButton()

    }


    private fun registerConnectivityReceiver() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

    private fun clearDatabase() {
        // Perform the database clearing logic here
        val dataBaseHandler = DataBaseHandler(this)
        dataBaseHandler.clearAllData()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_FIRST_RUN, true)
        startActivity(intent)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        clearBottomNavigationSelection()

        when (item.itemId) {
            R.id.nav_programs -> {
                openFragment(Programs())
                selectedNavItem = R.id.nav_programs
                setTitleForFragment(Programs()) // Set the title for Programs fragment
            }

            R.id.nav_scholarship -> {
                openFragment(Scholarship())
                selectedNavItem = R.id.nav_scholarship
                setTitleForFragment(Scholarship()) // Set the title for Scholarship fragment
            }

            R.id.nav_feedback -> {
                openFragment(Feedback())
                selectedNavItem = R.id.nav_feedback
                setTitleForFragment(Feedback()) // Set the title for Feedback fragment
            }

            R.id.nav_faqs -> {
                openFragment(FAQs())
                selectedNavItem = R.id.nav_faqs
                setTitleForFragment(FAQs()) // Set the title for FAQs fragment
            }

            R.id.menu_item_close_app -> {
                finishAffinity()
            }
        }

        // Close the drawer properly
        binding.drawerLayout.closeDrawer(GravityCompat.END)

        return true
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        // Check if there is a network connection and it has internet capabilities
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No Internet Detected")
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Finish this activity and all activities in the back stack
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to exit?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()

        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun clearBottomNavigationSelection() {
        binding.bottomNavigation.menu.findItem(selectedNavItem)?.isChecked = false
    }

    private fun loadImageAndUserDataFromDatabase() {
        val db: SQLiteDatabase = dataBaseHandler.readableDatabase

        // Assuming you want to retrieve the last row's data
        val query = "SELECT image, name, strand FROM User ORDER BY userid DESC LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val imageData = cursor.getBlob(cursor.getColumnIndex("image"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val strand = cursor.getString(cursor.getColumnIndex("strand"))

            // Set the image
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            imageView.setImageBitmap(bitmap)

            // Set the name and strand to TextViews
            nameTextView.text = name
            strandTextView.text = strand
        }

        cursor.close()
        db.close()
    }

    fun onBottomNavItemClicked(view: View) {
        // Handle item clicks here
        val scaleFactor = 1.2f // You can adjust the scale factor as needed

        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", scaleFactor),
            PropertyValuesHolder.ofFloat("scaleY", scaleFactor)
        )

        scaleUp.duration = 200 // Adjust the duration of the animation as needed
        scaleUp.interpolator = OvershootInterpolator()

        scaleUp.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Perform the actual fragment transaction after the animation ends
                val fragment: Fragment = when (view.id) {
                    R.id.bottom_prof -> UserProfile()
                    R.id.bottom_apt -> Aptitude()
                    R.id.bottom_recom -> CombinedRecommendFragment()
                    R.id.bottom_fav -> Favorites()
                    else -> UserProfile() // Default fragment
                }

                openFragment(fragment)

                // Set the title based on the selected fragment
                setTitleForFragment(fragment)

                // Scale down to the original size after the fragment transaction
                val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    view,
                    PropertyValuesHolder.ofFloat("scaleX", 1f),
                    PropertyValuesHolder.ofFloat("scaleY", 1f)
                )
                scaleDown.duration = 200 // Adjust the duration of the animation as needed
                scaleDown.interpolator = AccelerateInterpolator()
                scaleDown.start()
            }
        })

        scaleUp.start()
    }

    private fun setTitleForFragment(fragment: Fragment) {
        // Set the title based on the selected fragment
        val title: String = when (fragment) {
            is UserProfile -> "Profile"
            is Aptitude -> "Aptitude"
            is CombinedRecommendFragment -> "Recommendations"
            is Favorites -> "Favorites"
            is Programs -> "Programs" // Add this line for Programs fragment
            is Scholarship -> "Scholarship" // Add this line for Scholarship fragment
            is Feedback -> "Feedback" // Add this line for Feedback fragment
            is FAQs -> "FAQs"

            else -> "Default Title" // Default title for unknown fragments
        }

        supportActionBar?.title = title
    }

    private fun isValidAdminLogin(username: String, password: String): Boolean {
        // Implement your admin login validation logic here

        // Example: Check if the username is "admin" and the password is "admin123"
        return username == "1" && password == "1"

        // Replace the example logic with your actual admin login validation
        // For a production app, you should use a secure authentication mechanism
        // such as Firebase Authentication, OAuth, or your own server-based authentication.
    }


    private fun showGuideButton() {
        val guideButton = findViewById<FloatingActionButton>(R.id.guidebtn)

        // If the guide button is already defined in XML, update its properties
        if (guideButton != null) {
            guideButton.setImageResource(R.drawable.guide)
            guideButton.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gold))
            guideButton.setOnClickListener {
                // Show the guide sequence when the button is clicked
                showGuideSequence(getGuideTargets())
            }

            guideButton.visibility = View.VISIBLE
        }
    }

    private fun calculateTargetRadius(view: View): Int {
        // Calculate the radius based on the size of the view
        val width = view.width
        val height = view.height
        return max(width, height) / 2
    }


    // Define your specific TapTargets for UserView
    private fun getGuideTargets(): List<TapTarget> {
        val targets = mutableListOf<TapTarget>()


        val view1 = findViewById<View>(R.id.constraintLayout4)
        if (view1 != null) {
            val radius = calculateTargetRadius(view1)
            targets.add(
                TapTarget.forView(view1, "Welcome to CourSeer!", "You successfully created your Profile. Here is your user information and preferences. ")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
                    .targetRadius(385)
            )
        }

        val view2 = findViewById<View>(R.id.tabLayout)
        if (view2 != null) {
            targets.add(
                TapTarget.forView(view2, "Click Based on Given Criteria", "To avoid confusion, we separated the Strand Based and Non-Strand Based Recommendations and even the Program and Scholarship.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view3 = findViewById<View>(R.id.seekBar)
        if (view3 != null) {
            targets.add(
                TapTarget.forView(view3, "Slide to See Different Recommendations", "This controls the amount of given recommendation based on your given preferences and aptitude examination result.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }



        val view4 = findViewById<View>(R.id.categoryName)
        if (view4 != null) {
            targets.add(
                TapTarget.forView(view4, "Categories", "The following are separated according to category based on criteria and fields.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view5 = findViewById<View>(R.id.titleTextView)
        if (view5 != null) {
            targets.add(
                TapTarget.forView(view5, "Tap the Title to See Description", "Click the title to see previews of the following program, scholarship, and FAQ's.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }


        val view6 = findViewById<View>(R.id.shortDescriptionTextView)
        if (view6 != null && view6.visibility == View.VISIBLE) {
            targets.add(
                TapTarget.forView(view6, "Tap to View Full Description", "Click here to see the full details regarding each program and scholarship.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue)
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
                    .targetRadius(85)
            )
        }

        val view7 = findViewById<View>(R.id.saveButton)
        if (view7 != null) {
            targets.add(
                TapTarget.forView(view7, "Tap the Star Icon", "Tap to add as Favorite and tap again to remove from list")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val cardView = findViewById<CardView>(R.id.searchCardView)
        if (cardView != null) {
            targets.add(
                TapTarget.forView(cardView, "Search Here", "Search words related to any programs, scholarships, or general inquiries.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue)
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view10 = findViewById<View>(R.id.characterCount)
        if (view10 != null) {
            targets.add(
                TapTarget.forView(view10, "Word Count", "Check the number of words being input.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view11 = findViewById<View>(R.id.submitButton)
        if (view11 != null) {
            targets.add(
                TapTarget.forView(view11, "Save and Submit", "After creating your feedback, click here to send us your comments and suggestions.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view12 = findViewById<View>(R.id.interpretation)
        if (view12 != null) {
            targets.add(
                TapTarget.forView(view12, "Career Aptitude Result", "See the result of your career aptitude exam and their interpretations.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .targetRadius(60)
                    .transparentTarget(true)
            )
        }

        val view13 = findViewById<View>(R.id.progressBar)
        if (view13 != null) {
            targets.add(
                TapTarget.forView(view13, "Percentage Score", "View how many percentage does each category have in your result.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view14 = findViewById<View>(R.id.bottom_prof)
        if (view14 != null) {
            targets.add(
                TapTarget.forView(view14, "User Profile", "Contains general information you provided and your aptitude examination.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view15 = findViewById<View>(R.id.bottom_apt)
        if (view15 != null) {
            targets.add(
                TapTarget.forView(view15, "Career Interest Test", "Serve as a comprehensive way to assess your preferences and what careers they correspond to.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view16 = findViewById<View>(R.id.bottom_recom)
        if (view16 != null) {
            targets.add(
                TapTarget.forView(view16, "Program Recommendation", "Contains all recommendations based on your given preferences and career-interest test once available")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view17 = findViewById<View>(R.id.bottom_fav)
        if (view17 != null) {
            targets.add(
                TapTarget.forView(view17, "Favorite", "Contain the list of programs and scholarship saved by you.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view18 = findViewById<View>(R.id.reset)
        if (view18 != null) {
            targets.add(
                TapTarget.forView(view18, "Reset Button", "You may reset your profile anytime if you think your preferences changed overtime")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view19 = findViewById<View>(R.id.toolbar)
        if (view19 is Toolbar) {
            targets.add(
                TapTarget.forToolbarNavigationIcon(view19, "Menu", "Discover other features of CourSeer by tapping this icon.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        val view21 = findViewById<View>(R.id.guidebtn)
        if (view21 != null) {
            targets.add(
                TapTarget.forView(view21, "Guide", "Click here anytime to navigate the application.")
                    .targetCircleColor(R.color.blue)
                    .outerCircleColor(R.color.blue) // Change this line
                    .outerCircleAlpha(0.95f)
                    .transparentTarget(true)
            )
        }

        return targets
    }




    private fun showGuideSequence(targets: List<TapTarget>) {
        val sequence = TapTargetSequence(this)
            .targets(targets)
            .listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    // Handle sequence finish if needed
                    // Make the guide button visible after all prompts are done
                    showGuideButton()
                }

                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    // Handle each step of the sequence
                }

                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                    // Handle sequence cancellation if needed
                }
            })

        sequence.start()
    }

    private fun markGuideAsShown() {
        // Mark that the guide has been shown
        getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("is_first_launch", false)
            .apply()
    }

    fun onGuideButtonClick(view: View) {
        showGuideSequence(getGuideTargets())
    }

}


