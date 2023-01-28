package com.example.schedulev2

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.schedulev2.data.User
import com.example.schedulev2.databinding.FragmentPersonBinding
import com.example.schedulev2.databinding.FragmentScheduleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.view.Change
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Person.newInstance] factory method to
 * create an instance of this fragment.
 */
class Person : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPersonBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentPersonBinding.inflate(layoutInflater);

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
        val db = Firebase.database;

        binding.settingMenu.setOnClickListener {
            toChangePasswordMenu();
        }

        binding.exitMenu.setOnClickListener {
            logOut();
        }

        lifecycleScope.launch {
            getUser(db);
        }

        return binding.root;
    }

    private fun toChangePasswordMenu() {
        startActivity(Intent(binding.root.context, ChangePassword::class.java));
    }

    private fun logOut() {
        var user = FirebaseAuth.getInstance();
        user.signOut();
        startActivity(Intent(binding.root.context, LoginActivity::class.java));
    }

    private fun changeUserInfo(user: User?, progressDialog: ProgressDialog) {

        val storageRef = FirebaseStorage.getInstance().reference.child("images/${user?.id}.jpg");
        val localfile = File.createTempFile("tempImage", "jpg");

        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath);
            binding.Avatar.setImageBitmap(bitmap);

            if (progressDialog.isShowing) {
                progressDialog.dismiss();
            }

        }.addOnFailureListener {

        }
    }

    private suspend fun getUser(db: FirebaseDatabase): User? {
        val user_token = FirebaseAuth.getInstance().currentUser?.uid.toString();
        val user_info = db.getReference("User").orderByChild("id").equalTo("$user_token");

        var user : User? = null;

        var progressDialog = ProgressDialog(binding.root.context);
        progressDialog.setMessage("Завантаження профіля");
        progressDialog.setCancelable(false);
        progressDialog.show();

        user_info.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val d = snapshot.children.first();
                val json = Gson().toJson(d.value);
                val data = Gson().fromJson(json, User::class.java);
                user = data;

                binding.firstLastName.text = "${user?.last_name} ${user?.name}";
                binding.surname.text = user?.surname;
                binding.birthday.text = user?.birthday;

                binding.phoneNumber.text = user?.phone_number;
                binding.email.text = user?.email;

                changeUserInfo(user, progressDialog);

                println("user: $user");
            }

            override fun onCancelled(error: DatabaseError) {

            }
        });

        return user;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Person.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Person().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}