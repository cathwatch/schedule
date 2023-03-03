package com.example.schedule.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.schedule.ChangePassword
import com.example.schedule.LoginActivity
import com.example.schedule.R
import com.example.schedule.data.User
import com.example.schedule.databinding.FragmentPersonBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File

class Person : Fragment() {

    private lateinit var binding : FragmentPersonBinding;
    private lateinit var shimmerLayout : FrameLayout;
    private lateinit var shimmerFrameLayout : ShimmerFrameLayout;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentPersonBinding.inflate(layoutInflater);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val db = Firebase.database;

        binding.settingMenu.setOnClickListener {
            toChangePasswordMenu();
        }

        binding.exitMenu.setOnClickListener {
            logOut();
        }

        shimmerLayout = binding.root.findViewById<FrameLayout>(R.id.shimmer_layout) as FrameLayout;
        shimmerFrameLayout = shimmerLayout.findViewById<ShimmerFrameLayout>(R.id.shimmer_container);

        shimmerFrameLayout.startShimmerAnimation();

        lifecycleScope.launch {
            getUser(db);
        }

        return binding.root;
    }

    private fun toChangePasswordMenu() {
        startActivity(Intent(binding.root.context, ChangePassword::class.java));
    }

    private fun logOut() {
        showLogOutPopup();
    }

    private fun showLogOutPopup(){
        val dialog_binding = LayoutInflater.from(context).inflate(R.layout.log_out_account_popup, null);

        val my_dialog = Dialog(binding.root.context)
        my_dialog.setContentView(dialog_binding)
        my_dialog.setCancelable(true)
        my_dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        my_dialog.show()

        val confirm_btn = dialog_binding.findViewById<Button>(R.id.alert_confirm)
        confirm_btn.setOnClickListener {
            var user = FirebaseAuth.getInstance();
            user.signOut();
            startActivity(Intent(binding.root.context, LoginActivity::class.java));
        }
        val cancel_btn = dialog_binding.findViewById<Button>(R.id.alert_cancel)
        cancel_btn.setOnClickListener {
            my_dialog.dismiss();
        }
    }

    private fun changeUserInfo(user: User?) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${user?.id}.jpg");
        val localfile = File.createTempFile("tempImage", "jpg");

        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath);
            binding.Avatar.setImageBitmap(bitmap);

            // stop shimmer
            shimmerLayout.visibility = View.INVISIBLE;
        }.addOnFailureListener {

        }
    }

    private suspend fun getUser(db: FirebaseDatabase): User? {
        val user_token = FirebaseAuth.getInstance().currentUser?.uid.toString();
        val user_info = db.getReference("User").orderByChild("id").equalTo("$user_token");

        var user : User? = null;

        user_info.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val d = snapshot.children.first();
                val json = Gson().toJson(d.value);
                val data = Gson().fromJson(json, User::class.java);
                user = data;

                binding.firstLastName.text = "${user?.last_name} ${user?.name} ${user?.surname}";
                binding.birthday.text = user?.birthday;

                binding.phoneNumber.text = user?.phone_number;
                binding.email.text = user?.email;

                changeUserInfo(user);

                println("user: $user");
            }

            override fun onCancelled(error: DatabaseError) {

            }
        });

        return user;
    }
}