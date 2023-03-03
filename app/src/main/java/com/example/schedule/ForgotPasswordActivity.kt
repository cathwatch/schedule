package com.example.schedule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.schedule.LoginActivity
import com.example.schedule.databinding.ActivityForgotPasswordBinding
import com.example.schedule.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var binding : ActivityForgotPasswordBinding;

    private var mAuth: FirebaseAuth? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater);

        supportActionBar?.title = "Вхід в аккаунт";

        binding.btnResetPass.setOnClickListener {
            resetPass();
        }

        setContentView(binding.root);

        mAuth = FirebaseAuth.getInstance();
    }
    private fun resetPass() {
        val email = binding.etEmail.text.toString();

        mAuth!!.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                println("successful");
                // successful
            }
            else {
                println("failed");
                // failed
            }
        };
    }
}