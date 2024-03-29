package com.example.schedule

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.schedule.databinding.ActivityMainBinding
import com.example.schedule.fragments.Schedule
import com.example.schedule.fragments.Person
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        replaceFragment(Schedule());
        genFragments();
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager;
        val fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private fun genFragments() {
        binding.bottomNavigationView.setOnItemSelectedListener { it ->
            when(it.itemId) {
                R.id.schedule ->  {
                    supportActionBar?.title = "Розклад"
                    replaceFragment(Schedule())
                }
                R.id.person -> {
                    supportActionBar?.title = "Профіль"
                    replaceFragment(Person())
                }

                else -> {

                }
            }
            true
        }
    }
}