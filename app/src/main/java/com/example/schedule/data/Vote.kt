package com.example.schedule.data

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.view.marginRight
import com.example.schedule.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VoteModel (
    public var id: Int? = null,
    public var title: String? = null,
    public var type: Int? = null,
    public var answers: Array<Answer>? = null,
    public var desc: String? = null
);

class Vote() {
    lateinit var context: Context;
    lateinit var db: FirebaseDatabase;
    var btn_active = true;

    public suspend fun genVote(db: FirebaseDatabase, context: Context): List<View> {
        this.db = db;
        this.context = context;

        val list = getVotes(db);
        val views = createVotesView(list);

        return views;
    }

    var get_data = false;
    private suspend fun getVotes(db: FirebaseDatabase) : MutableList<VoteModel> {
        val ref = db.getReference("Event").child("2")

        var vote_list : MutableList<VoteModel> = mutableListOf()

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { advert ->
                    val json = Gson().toJson(advert.value)
                    val data = Gson().fromJson(json, VoteModel::class.java)

                    vote_list.add(data)
                }

                get_data = true;
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        if (vote_list.isEmpty()) {
            delayProject();
        }

        return vote_list
    }

    private suspend fun delayProject() {
        if (get_data) {
            return
        } else {
            delay(10);
            delayProject()
        }
    }

    private suspend fun createVotesView(list: MutableList<VoteModel>): List<View> {
        val cur_user_id = FirebaseAuth.getInstance().currentUser!!.uid
        val database = Firebase.database

        var views : MutableList<View> = mutableListOf();

        for (obj in list) {
            val has_user = findUserInVote(database, cur_user_id, obj.id!!);

            if (!has_user) {
                val view: View = LayoutInflater.from(context).inflate(R.layout.vote, null)

                view.findViewById<TextView>(R.id.alert_message).text = obj.desc

                val radio_group = view.findViewById<RadioGroup>(R.id.alert_group)

                for (answer in obj.answers!!) {
                    val radio_button = RadioButton(context)
                    radio_button.text = answer.title

                    radio_group.addView(radio_button)
                }

                val yes_btn = view.findViewById<Button>(R.id.alert_yes);
                yes_btn.setOnClickListener {
                    val checkedId = radio_group.checkedRadioButtonId
                    val answer = radio_group.indexOfChild(radio_group.findViewById(checkedId))

                    if (answer != -1) {
                        regVoteUser(obj.id, answer);
                    }

                    radio_group.isEnabled = false;
                    yes_btn.isEnabled = false;
                }

                views.add(view);
            }
        }

        return views;
    }

    @SuppressLint("ResourceAsColor")
    fun genPlaceholder(context: Context?) : List<View> {
        var list_view : MutableList<View> = mutableListOf();

        for (i in 0..5) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.vote_placeholder, null);

            list_view.add(view);
        }

        return list_view;
    }

    private suspend fun findUserInVote(database: FirebaseDatabase, user_id: String, vote_id: Int) : Boolean {
        val main_ref = database.getReference("EventResult").child("$vote_id")
        val user_ref = main_ref.child("users");

        var has_user = true;

        user_ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val has = snapshot.children.find { key ->
                    key.value == user_id;
                }

                has_user = has != null;
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        delay(250);

        return has_user;
    }

    private fun regVoteUser(id: Int?, answer: Int) {
        GlobalScope.launch {
            val cur_user = FirebaseAuth.getInstance().currentUser;

            val database = Firebase.database;
            val main_ref = database.getReference("EventResult").child("$id");

            val user_ref = main_ref.child("users").push();

            val result_ref = main_ref.child("$answer");

            var value: Int = 0;

            var req_ok = false;

            result_ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        value = snapshot.getValue(Int::class.java)!!;
                        req_ok = true;
                    } catch (e: Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

            delay(500);

            if (req_ok) {
                result_ref.setValue(value + 1)
                user_ref.setValue(cur_user?.uid)
            }

            req_ok = false;
            value = 0;
        }
    }
}