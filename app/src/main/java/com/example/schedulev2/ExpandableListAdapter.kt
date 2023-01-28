package com.example.schedulev2

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import com.example.schedulev2.data.Lesson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ExpandableListAdapter(val context: Context, val listOfHeaderData: List<String>, val listOfChildData: HashMap<String, MutableList<Lesson>>) : BaseExpandableListAdapter() {

    private lateinit var sharedpreferences: SharedPreferences;

    override fun getGroupCount(): Int {
        return listOfHeaderData.size;
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return if (listOfChildData.isEmpty()) {
            Toast.makeText(context, "Не вдалося завантажити", Toast.LENGTH_SHORT).show();
            0;
        } else {
            listOfChildData[listOfHeaderData[groupPosition]]!!.size;
        }
    }

    override fun getGroup(groupPosition: Int): String? {
        return listOfHeaderData[groupPosition];
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listOfChildData[listOfHeaderData[groupPosition]]!![childPosition];
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong();
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong();
    }

    override fun hasStableIds(): Boolean {
        return false;
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        val headerTitle = getGroup(groupPosition) as String;
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_header, parent, false);

        val listHeaderText = view.findViewById<AppCompatTextView>(R.id.list_header_text) as AppCompatTextView;

        listHeaderText.setTypeface(null, Typeface.BOLD);
        listHeaderText.text = headerTitle;

        return view;
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childText = getChild(groupPosition, childPosition) as Lesson;
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        val listItem = view.findViewById<ConstraintLayout>(R.id.lesson_model) as ConstraintLayout;
        listItem.findViewById<AppCompatTextView>(R.id.lesson_title).text = childText.title;
        listItem.findViewById<AppCompatTextView>(R.id.lesson_type).text = childText.type;
        listItem.findViewById<AppCompatTextView>(R.id.lesson_time_end).text = childText.time_end;
        listItem.findViewById<AppCompatTextView>(R.id.lesson_time_start).text = childText.time_start;
        listItem.findViewById<AppCompatTextView>(R.id.lesson_location).text = childText.location;

        return view;
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true;
    }
}