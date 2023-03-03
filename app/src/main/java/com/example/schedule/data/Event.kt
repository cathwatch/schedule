package com.example.schedule.data

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.schedule.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.io.File
import java.util.Calendar

val MONTH_TITLE = listOf(
    "січня",
    "лютого",
    "березня",
    "квітня",
    "травня",
    "червня",
    "липня",
    "серпня",
    "вересня",
    "жовтня",
    "листопада",
    "грудня"
)

data class EventModel (
    public var contact_email : String? = null,
    public var contact_number : String? = null,
    public var data_time : Date? = null,
    public var desc : String? = null,
    public var image : String? = null,
    public var location : String? = null,
    public var title : String? = null,
    public var type : Int? = null
);

data class Date (
    var month: Int,
    var date: Int,
    var hourOfDay: Int,
    var minute: Int,
    var hourOfDayEnd: Int,
    var minuteEnd: Int
    );

class Event() {
    lateinit var context: Context;
    lateinit var db: FirebaseDatabase;

    public suspend fun genEvent(db: FirebaseDatabase, context: Context): List<View> {
        this.db = db;
        this.context = context;

        val list = getEvents(db);
        val views = createEventView(list);

        return views;
    }

    var get_data = false;

    private suspend fun getEvents(db: FirebaseDatabase) : MutableList<EventModel> {
        val ref = db.getReference("Event").child("1");

        var events : MutableList<EventModel> = mutableListOf();

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { advert ->
                    val json = Gson().toJson(advert.value);
                    val data = Gson().fromJson(json, EventModel::class.java);

                    events.add(data);
                }

                get_data = true;
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        if (events.isEmpty()) {
            delayProject();
        }

        return events;
    }

    fun genPlaceholder(context: Context?) : List<View> {
        var list_view : MutableList<View> = mutableListOf();

        for (i in 0..5) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.event_placeholder, null);
            list_view.add(view);
        }

        return list_view;
    }

    private suspend fun delayProject() {
        if (get_data) {
            return
        } else {
            delay(10);
            delayProject()
        }
    }

    private fun createEventView(list: MutableList<EventModel>): List<View> {
        var views : MutableList<View> = mutableListOf();

        for (obj in list) {
            val view: View = getView(obj);

            views.add(view);
        }

        return views;
    }

    private fun getView(obj: EventModel) : View {
        val view = LayoutInflater.from(context).inflate(R.layout.event, null);
        view.findViewById<TextView>(R.id.alert_message).text = obj.desc;
        view.findViewById<TextView>(R.id.alert_time).text = getTime(obj.data_time);
        view.findViewById<TextView>(R.id.alert_location).text = obj.location;


        // active email btn
//        val email = view.findViewById<TextView>(R.id.alert_contact_email);
//        email.text = obj.contact_email;
//
//        email.setOnClickListener {
//            val email_intent = Intent(android.content.Intent.ACTION_SEND);
//            email_intent.setType("plain/text");
//            email_intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayListOf(obj.contact_email));
//            email_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
//            email_intent.putExtra(android.content.Intent.EXTRA_TEXT,"");
//            startActivity(context ,Intent.createChooser(email_intent, "Send mail..."), null);
//        }

        val storageRef = FirebaseStorage.getInstance().reference.child("events/${obj.image}.jpg");
        val localfile = File.createTempFile("tempImage", "jpg");

        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath);
            view.findViewById<ImageView>(R.id.alert_avatar).setImageBitmap(bitmap);

        }.addOnFailureListener {

        }

        val btn = view.findViewById<Button>(R.id.btn_calendar);

        btn.setOnClickListener {
            addCalendarEvent(obj);
        }

        return view;
    }

    fun getTime(data: Date?) : String {

        val time = if (data?.hourOfDay.toString().length == 1)
            {
            "0${data?.hourOfDay}:${data?.minute}"
        }
        else {
            "${data?.hourOfDay}:${data?.minute}"
        }

        return "${data?.date} ${MONTH_TITLE[data?.month!!]}, ${time}";
    }

    private fun addCalendarEvent(event: EventModel) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR);

        val date = event.data_time;

        val startMillis: Long = Calendar.getInstance().run {
            set(currentYear, date?.month!!, date?.date!!, date?.hourOfDay!!, date?.minute!!)
            timeInMillis
        }
        val endMillis: Long = Calendar.getInstance().run {
            set(currentYear, date?.month!!, date?.date!!, date?.hourOfDayEnd!!, date?.minuteEnd!!)
            timeInMillis
        }

        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            .putExtra(CalendarContract.Events.TITLE, event.title)
            .putExtra(CalendarContract.Events.DESCRIPTION, event.desc)
            .putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        startActivity(context, intent, null);

        println("Done");
    }
}