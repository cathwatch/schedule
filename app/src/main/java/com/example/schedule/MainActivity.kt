package com.example.schedule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.schedule.ui.main.SectionsPagerAdapter
import com.example.schedule.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager);
        val viewPager: ViewPager = binding.viewPager;
        viewPager.adapter = sectionsPagerAdapter;
        val tabs: TabLayout = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        bindButton();

        changeGroupName(loadData());
    }

    private fun bindButton() {
        var btn = binding.root.findViewById<Button>(R.id.toSelectionGroup);

        btn.setOnClickListener {
            var intent = Intent(this, GroupSelectionActivity::class.java)
            startActivity(intent);
        }
    }

    private fun loadData() : String {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Group", null).toString();
    }

    private fun changeGroupName(groupName: String) {
        binding.toSelectionGroup.text = if (groupName == "null") {"Виберіть групу"} else {groupName.toString()}
    }
}