package com.example.schedule.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.schedule.R
import com.example.schedule.databinding.EventFragmentBinding

class EventFragment(val list_view: List<View>) : Fragment() {
    private lateinit var binding: EventFragmentBinding;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = EventFragmentBinding.inflate(layoutInflater);

        val progress_dialog = showLoadingIndicator();

        if (list_view.isEmpty()) {
            isEmptyList();
        }
        else {
            showEvents(list_view);
        }

        dismissLoadingIndicator(progress_dialog);

        return binding.root;
    }

    private fun showEvents(views: List<View>) {
        for (view in views) {
            if (view.getParent() != null) {
                (view.getParent() as ViewGroup).removeView(view) // сасний фікс
            }
            binding.list.addView(view);
        }
    }

    private fun isEmptyList() {
        val empty_view = LayoutInflater.from(context).inflate(R.layout.is_empty_fragment, null);
        if (empty_view.getParent() != null) {
            (empty_view.getParent() as ViewGroup).removeView(empty_view) // сасний фікс
        }
        binding.list.addView(empty_view);
    }

    private fun showLoadingIndicator(): ProgressDialog {
        var progressDialogLoadSchedule = ProgressDialog(binding.root.context);
        progressDialogLoadSchedule.setMessage("Завантаження розкладу");
        progressDialogLoadSchedule.setCancelable(false);
        progressDialogLoadSchedule.show();
        return progressDialogLoadSchedule;
    }

    private fun dismissLoadingIndicator(progressDialogLoadSchedule: ProgressDialog) {
        progressDialogLoadSchedule.dismiss();
    }
}