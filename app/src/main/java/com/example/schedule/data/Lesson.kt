package com.example.schedule.data

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.startActivity
import com.example.schedule.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.net.URL


class LessonModel (
    public var time_start : String? = null,
    public var time_end : String? = null,
    public var title : String? = null,
    public var location : String? = null,
    public var type : String? = null,
    public var url : String? = null,
);

class Lesson () {
    lateinit var context: Context;
    lateinit var db: FirebaseDatabase;
    var group_name: String? = null;

    public suspend fun genLessons(db: FirebaseDatabase, context: Context, group_name: String?) : List<View> {
        this.db = db;
        this.group_name = group_name;
        this.context = context;

        val days = getFromBD();
        val lessons = genViews(days);

        return lessons;
    }

    fun genPlaceholder(context: Context?) : List<View> {
        var list_view : MutableList<View> = mutableListOf();

        for (i in 0..2) {
            val day_view: View = LayoutInflater.from(context).inflate(R.layout.day_header_placeholder, null);
            list_view.add(day_view);

            for (i in 0..3) {
                val lesson: View = LayoutInflater.from(context).inflate(R.layout.lesson_placeholder, null);
                list_view.add(lesson);
            }
        }

        return list_view;
    }

    private fun genViews(list: HashMap<String, MutableList<LessonModel>>): List<View> {
        var views : MutableList<View> = mutableListOf();
        for (i in list) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.day_header, null);
            val listHeaderText = view.findViewById<AppCompatTextView>(R.id.list_header_text) as AppCompatTextView;
            listHeaderText.setTypeface(null, Typeface.BOLD);
            listHeaderText.text = i.key;

            views.add(view);

            i.value.forEach { lesson ->
                val view: View = LayoutInflater.from(context).inflate(R.layout.lesson, null);

                view.findViewById<AppCompatTextView>(R.id.lesson_title).text = lesson.title;
                view.findViewById<AppCompatTextView>(R.id.lesson_type).text = lesson.type;
                view.findViewById<AppCompatTextView>(R.id.lesson_time_end).text = lesson.time_end;
                view.findViewById<AppCompatTextView>(R.id.lesson_time_start).text = lesson.time_start;


                if (lesson.url == null) {
                    view.findViewById<AppCompatTextView>(R.id.lesson_location).text = lesson.location;
                }
                else {
                    view.findViewById<AppCompatTextView>(R.id.lesson_location).text = lesson.url;

                    view.setOnClickListener {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(lesson.url))
                        startActivity(context, browserIntent, null);
                    }
                }

                views.add(view);
            }
        }

        return views;
    }

    var get_data = false;

    public suspend fun getFromBD(): HashMap<String, MutableList<LessonModel>> {
        val day_list = db.getReference("$group_name");

        var lesson_list : MutableList<LessonModel> = mutableListOf();
        var lessons_in_day = HashMap<String, MutableList<LessonModel>>();

        day_list.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { day ->
                    day.children.forEach {lesson ->
                        if (lesson.key != "day") {
                            val json_lesson = Gson().toJson(lesson.value)
                            val data_lesson = Gson().fromJson(json_lesson, LessonModel::class.java)

                            if (data_lesson.title != null) {
                                lesson_list.add(data_lesson)
                            }
                        }
                        else {
                            val day_name = lesson.getValue(String::class.java);
                            lessons_in_day[day_name.toString()] = lesson_list;
                            lesson_list = mutableListOf()
                        }
                    }
                }

                get_data = true;
            }

            override fun onCancelled(error: DatabaseError) {
            }
        });
        if (lessons_in_day.isEmpty()) {
            delayProject();
        }

        return lessons_in_day;
    }

    private suspend fun delayProject() {
        if (get_data) {
            return
        } else {
            delay(10);
            delayProject()
        }
    }
}