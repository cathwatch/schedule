package com.example.schedule.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.schedule.data.*
import com.example.schedule.databinding.FragmentScheduleBinding
import com.google.android.material.tabs.TabLayout
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

class Schedule : Fragment() {

    lateinit var binding : FragmentScheduleBinding;
    lateinit var tab_layout : TabLayout;
    lateinit var view_pager_shimmer : ViewPager;
    lateinit var db : FirebaseDatabase;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentScheduleBinding.inflate(layoutInflater);

        tab_layout = binding.tabLayout;
        view_pager_shimmer = binding.viewPagerShimmer;
        db = Firebase.database;

        shimmer();
        getShedule();

        return binding.root;
    }

    private fun getShedule() {
        lifecycleScope.launch {
            db = Firebase.database;
            val group_name = getGroupName(db);

            val viewPager = binding.viewPager;

            val adapter = PagerAdapter(childFragmentManager);

            val lessons = Lesson().genLessons(db, binding.root.context, group_name);
            val advertisements = Advertisement().genAdvertisement(db, binding.root.context);
            val votes = Vote().genVote(db, binding.root.context);
            val events = Event().genEvent(db, binding.root.context);

            adapter.addFragment(EventFragment(lessons), "Розклад");
            adapter.addFragment(EventFragment(advertisements), "Оголошення");
            adapter.addFragment(EventFragment(votes), "Голосування");
            adapter.addFragment(EventFragment(events),  "Події");

            view_pager_shimmer.visibility = View.INVISIBLE;
            viewPager.visibility= View.VISIBLE;

            viewPager.adapter = adapter;

            viewPager.currentItem = view_pager_shimmer.currentItem;

            tab_layout.setupWithViewPager(viewPager);
        }
    }

    private fun shimmer() {
        var adapter_shimmer = PagerAdapter(childFragmentManager);

        val lessons = Lesson().genPlaceholder(context);
        val advertisements = Advertisement().genPlaceholder(context);
        val votes = Vote().genPlaceholder(context);
        val events = Event().genPlaceholder(context);

        adapter_shimmer.addFragment(EventFragment(lessons), "Розклад");
        adapter_shimmer.addFragment(EventFragment(advertisements), "Оголошення");
        adapter_shimmer.addFragment(EventFragment(votes), "Голосування");
        adapter_shimmer.addFragment(EventFragment(events), "Події");

        view_pager_shimmer.adapter = adapter_shimmer;
        binding.shimmerView.startShimmerAnimation();
        tab_layout.setupWithViewPager(view_pager_shimmer);
    }

    private suspend fun getGroupName(db: FirebaseDatabase): String? {
        val user_token = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val user_info = db.getReference("User").orderByChild("id").equalTo("$user_token")

        var group_name : String? = null

        GlobalScope.launch {
            user_info.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val d = snapshot.children.first()
                    val json = Gson().toJson(d.value)
                    val data = Gson().fromJson(json, User::class.java)
                    group_name = data.group
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
        delay(2000)

        return group_name
    }

    class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private var fragmentList = ArrayList<Fragment>()
        private var fragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        public fun clear() {
            fragmentList.clear();
            notifyDataSetChanged();
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList[position]
        }
    }
}