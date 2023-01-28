package com.example.schedulev2

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.schedulev2.data.Lesson
import com.example.schedulev2.data.User
import com.example.schedulev2.databinding.FragmentScheduleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


private val DAYS_TITLES = listOf(
    "Понеділок",
    "Вівторок",
    "Середа",
    "Четвер",
    "П'ятниця",
    "Субота"
)

/**
 * A simple [Fragment] subclass.
 * Use the [Schedule.newInstance] factory method to
 * create an instance of this fragment.
 */
class Schedule : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentScheduleBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentScheduleBinding.inflate(layoutInflater);

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        lifecycleScope.launch {
            val progressDialog = ProgressDialog(binding.root.context);
            progressDialog.setMessage("Завантаження розкладу");
            progressDialog.setCancelable(false);
            progressDialog.show();

            var listChild: HashMap<String, MutableList<Lesson>> = hashMapOf();
            listChild = getDataFromBD();

            var ex = ExpandableListAdapter(binding.root.context, DAYS_TITLES, listChild);
            binding.expandableListView.setAdapter(ex);

            if (progressDialog.isShowing) {
                progressDialog.dismiss();
            }
        }

        return binding.root;
    }

    private suspend fun getDataFromBD() : HashMap<String, MutableList<Lesson>> {
        val db = Firebase.database;
        val group_name = getGroupName(db);
        val day_list = db.getReference("$group_name");

        var day_id = 0;
        var lesson_list : MutableList<Lesson> = mutableListOf();

        var lessons_in_day = HashMap<String, MutableList<Lesson>>();

        day_list.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var days = snapshot.children.forEach { day ->
                    var lessons = day.children.forEach { lesson ->
                        val json_lesson = Gson().toJson(lesson.value);
                        val data_lesson = Gson().fromJson(json_lesson, Lesson::class.java);

                        if (data_lesson.title != "") {
                            lesson_list.add(data_lesson);
                        }
                    };

                    lessons_in_day.put(DAYS_TITLES[day_id], lesson_list);
                    lesson_list = mutableListOf();
                    day_id++;
                };
            }

            override fun onCancelled(error: DatabaseError) {

            }
        });

        return lessons_in_day;
    }

    private suspend fun getGroupName(db: FirebaseDatabase): String? {
        val user_token = FirebaseAuth.getInstance().currentUser?.uid.toString();
        val user_info = db.getReference("User").orderByChild("id").equalTo("$user_token");

        var group_name : String? = null;

        GlobalScope.launch {
            user_info.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val d = snapshot.children.first();
                    val json = Gson().toJson(d.value);
                    val data = Gson().fromJson(json, User::class.java);
                    group_name = data.group;
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
        delay(2000);

        return group_name;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Schedule.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Schedule().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
