package com.example.ecotrack

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var addActivityFab: FloatingActionButton
    private lateinit var activitiesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupViews()
        setupListeners()
        setupRecyclerView()
    }

    private fun setupViews() {
        topAppBar = findViewById(R.id.topAppBar)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        addActivityFab = findViewById(R.id.addActivityFab)
        activitiesRecyclerView = findViewById(R.id.activitiesRecyclerView)
        
        setSupportActionBar(topAppBar)
    }

    private fun setupListeners() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // Handle home navigation
                    true
                }
                R.id.menu_activities -> {
                    // Handle activities navigation
                    true
                }
                R.id.menu_profile -> {
                    // Handle profile navigation
                    true
                }
                else -> false
            }
        }

        addActivityFab.setOnClickListener {
            // TODO: Implement add activity functionality
            Snackbar.make(it, getString(R.string.add_activity), Snackbar.LENGTH_SHORT).show()
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    // Handle settings click
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        activitiesRecyclerView.layoutManager = LinearLayoutManager(this)
        // TODO: Set adapter for eco activities
    }
}
