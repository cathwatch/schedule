package com.example.schedule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.startActivity

class ExpandableListAdapter(val context: Context, val listOfHeaderData: List<String?>, val listOfChildData: HashMap<String, List<String>>) : BaseExpandableListAdapter() {

    private lateinit var sharedpreferences: SharedPreferences;

    override fun getGroupCount(): Int {
        return listOfHeaderData.size;
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listOfChildData[listOfHeaderData[groupPosition]]!!.size;
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
        val childText = getChild(groupPosition, childPosition) as String;
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        val listItemText = view.findViewById<AppCompatTextView>(R.id.list_item_text) as AppCompatTextView;
        listItemText.text = childText;

        listItemText.setOnClickListener {
            saveData(listItemText.text.toString());
        }

        return view;
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true;
    }

    private fun saveData(data: String) {
        context
        val sharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("Group", data)
        }.apply();

        Toast.makeText(context, "Група $data збережена", Toast.LENGTH_SHORT).show();

        backToMainScreen();
    }

    private fun backToMainScreen() {
        val intent = Intent(context, MainActivity::class.java);
        startActivity(context, intent, null);
    }
}