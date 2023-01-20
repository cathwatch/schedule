package com.example.schedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle;
import android.service.controls.ControlsProviderService
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.ExpandableListView
import com.example.schedule.data.Faculty
import com.example.schedule.data.Group
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ValueEventListener as ValueEventListener

class GroupSelectionActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val actionBar = supportActionBar;
        actionBar!!.title = "Виберіть групу";

        actionBar.setDisplayHomeAsUpEnabled(true);

        val database = Firebase.database;
        getFacultys(database);
    }

    fun updateLastInfo(database: FirebaseDatabase, faculty: MutableList<Faculty?>) {
        getGroups(database, faculty);
    }

    fun getFacultys(database: FirebaseDatabase?) {
        var facultyList : MutableList<Faculty?> = mutableListOf();

        val faculty = database?.getReference("Faculty");
        faculty?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = (snapshot.value as ArrayList<Int>).size - 1;

                for (i in 0..count) {
                    val value = snapshot.child("$i").getValue(Faculty::class.java);
                    facultyList.add(value);

                    println("${value?.title}");
                }

                updateLastInfo(database, facultyList);
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ControlsProviderService.TAG, "Failed to read value.", error.toException())
            }
        });
    }

    private fun getGroups(database: FirebaseDatabase, facultyList: MutableList<Faculty?>) {
        var groupList: MutableList<Group?> = mutableListOf<Group?>();

        val faculty = database.getReference("Group");
        faculty.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = (snapshot.value as ArrayList<Int>).size - 1;

                for (i in 0..count) {
                    val value = snapshot.child("$i").getValue(Group::class.java);
                    groupList.add(value);
                }
                updateView(groupList, facultyList);
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        });
    }

    private fun updateView(groupList: MutableList<Group?>, facultyList: MutableList<Faculty?>) {
        val listHeader = facultyList.map { it?.title };

        val listChild = HashMap<String, List<String>>();

        val groupHeaderList : MutableList<String> = mutableListOf();

        var titleList: MutableList<String> = mutableListOf();

        for (faculty in facultyList) {
            titleList = mutableListOf();
            for (group in groupList) {
                if (group?.parent == faculty?.id) {
                    titleList.add(group?.title.toString());
                }
            }

            listChild.put(faculty?.title.toString(), titleList);
        }

        val ex = ExpandableListAdapter(this, listHeader, listChild);

        this.findViewById<ExpandableListView>(R.id.expandable_list_view).setAdapter(ex);
    }
}