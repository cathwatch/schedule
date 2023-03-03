package com.example.schedule.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.schedule.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.delay

class AdvertisementModel (
    var desc : String? = null,
    var title : String? = null,
    var type : Int? = null
);

class Advertisement() {

    lateinit var context: Context;
    lateinit var db: FirebaseDatabase;

    public suspend fun genAdvertisement(db: FirebaseDatabase, context: Context): List<View> {
        this.db = db;
        this.context = context;

        val list = getAdvertisements(db);
        val views = createAdvertisementView(list);

        return views;
    }

    var get_data = false;

    private suspend fun getAdvertisements(db: FirebaseDatabase): MutableList<AdvertisementModel> {
        val ref = db.getReference("Event").child("0")

        var advertisement_list: MutableList<AdvertisementModel> = mutableListOf()

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { advert ->
                    val json = Gson().toJson(advert.value)
                    val data = Gson().fromJson(json, AdvertisementModel::class.java)

                    advertisement_list.add(data)
                }

                get_data = true;
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        if (advertisement_list.isEmpty()) {
            delayProject();
        }

        return advertisement_list
    }

    fun genPlaceholder(context: Context?) : List<View> {
        var list_view : MutableList<View> = mutableListOf();

        for (i in 0..5) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.advertisement_placeholder, null);
            list_view.add(view);
        }

        return list_view;
    }

    suspend fun delayProject() {
        if (get_data) {
            return
        } else {
            delay(10);
            delayProject()
        }
    }

    private fun createAdvertisementView(list: MutableList<AdvertisementModel>) : List<View> {
        var views : MutableList<View> = mutableListOf();
        for (obj in list) {
            var view: View = LayoutInflater.from(context).inflate(R.layout.advertisement, null)

            view.findViewById<TextView>(R.id.alert_message).text = obj.desc

            views.add(view);
        }
        return views;
    }
}