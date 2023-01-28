package com.example.schedulev2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.schedulev2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding;
    private lateinit var user : FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root);

        user = FirebaseAuth.getInstance();

        checkIfUserIsLogged();

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
                        Toast.makeText(this, "Неможливо увійти!", Toast.LENGTH_SHORT).show();
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