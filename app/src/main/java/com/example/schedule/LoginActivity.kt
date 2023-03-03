package com.example.schedule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.schedule.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding;
    private lateinit var user : FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root);

        supportActionBar?.title = "Вхід в аккаунт";

        user = FirebaseAuth.getInstance();

        checkIfUserIsLogged();

        binding.forgotPass.setOnClickListener {
            forgotPass();
        }

        binding.btnLogin.setOnClickListener {
            login();
        }
    }

    private fun checkIfUserIsLogged() {
        if (user.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java));
            finish();
        }
    }

    private fun forgotPass() {
        startActivity(Intent(this, ForgotPassword::class.java));
    }

    private fun login() {
        val email = binding.etEmail.text.toString();
        val password = binding.etPassword.text.toString();

        if (email.isNotEmpty() && password.isNotEmpty()) {

            user.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { mTask ->
                    if (mTask.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java));
                        finish();
                    }
                    else {
                        Toast.makeText(this, "Пароль не вірний!", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        else {
            Toast.makeText(this,
                "Поля: електронна адреса та пароль пусті!",
                Toast.LENGTH_SHORT).show();
        }
    }
}