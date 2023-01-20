package com.example.schedule.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.schedule.MainActivity
import com.example.schedule.R
import com.example.schedule.data.Faculty
import com.example.schedule.data.Lesson
import com.example.schedule.databinding.FragmentMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


/**
 * A placeholder fragment containing a simple view.
 */
class CreateLessons : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var listView: ListView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pageViewModel = ViewModelProvider(this)[PageViewModel::class.java].apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState);

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root;

        val database = Firebase.database;

        val group_name = loadData() ?: return null;

        readData(database, group_name);

        return root
    }

    private fun readData(database: FirebaseDatabase, group_name: String) {
        var day_list : HashMap<Int, MutableList<Lesson?>> = hashMapOf();

        val db_request = database.getReference("$group_name");
        db_request.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in 0 until 6) {
                    val count_lessons_in_day = snapshot.child("$i").childrenCount;
                    val lessons = snapshot.child("$i");
                    var lessons_list : MutableList<Lesson?> = mutableListOf();

                    for (lesson in 0 until  count_lessons_in_day) {
                        val value = lessons.child("$lesson").getValue(Lesson::class.java);
                        if (value?.title != "") {
                            lessons_list.add(value);
                        }
                    }

                    day_list.put(i, lessons_list);
                }
                generateView(day_list);
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        });
    }

    private fun generateView(days_list: HashMap<Int, MutableList<Lesson?>>) {
        pageViewModel.text.observe(viewLifecycleOwner, Observer {
            var day_id = it - 1;

            for (lesson in 0 until days_list[day_id]!!.size) {
                if (days_list.get(day_id)?.isEmpty() == false) {
                    var cur_lesson = days_list[day_id]!![lesson];
                    var l: View = createLessonModel(
                        0,
                        cur_lesson?.time_start.toString(),
                        cur_lesson?.time_end.toString(),
                        cur_lesson?.title.toString(),
                        cur_lesson?.type.toString(),
                        cur_lesson?.location.toString(),
                        cur_lesson?.teacher.toString()
                    );
                    binding.entryList.addView(l);
                }
            }
        });
    }

    private fun loadData() : String {
        val sharedPreferences = context?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        return sharedPreferences?.getString("Group", null).toString();
    }

    private fun createLessonModel(
        id: Int,
        time_start: String,
        time_end: String,
        title: String,
        type: String,
        location: String,
        teacher: String
    ) : View {
        var model = View.inflate(context, R.layout.lesson_model, null);

        model.findViewById<TextView>(R.id.time_start).text = time_start;
        model.findViewById<TextView>(R.id.time_end).text = time_end;
        model.findViewById<TextView>(R.id.lesson_name).text = title;
        model.findViewById<TextView>(R.id.lessons_type).text = type;
        model.findViewById<TextView>(R.id.lesson_location).text = location;
        model.findViewById<TextView>(R.id.lesson_teacher).text = teacher;

        return model;
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): CreateLessons {
            return CreateLessons().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}