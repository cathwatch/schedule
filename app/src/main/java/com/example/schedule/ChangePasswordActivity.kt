package com.example.schedule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.schedule.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangePassword : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(layoutInflater);

        binding.saveBtn.setOnClickListener {
            lifecycleScope.launch {
                changePassword();
            }
        }

        setContentView(binding.root);
    }

    private suspend fun changePassword() {
        val first_pass = binding.edtPassword.text.toString();
        val second_pass = binding.confirmedEdtPassword.text.toString();

        if (first_pass.length > 5) {
            if (first_pass.isEmpty()){
                Toast.makeText(binding.root.context, "Поле пароля пусте!", Toast.LENGTH_SHORT).show();
            }
            else {
                if (first_pass != second_pass) {
                    Toast.makeText(binding.root.context, "Пароль підтвердження веддений не вірно!", Toast.LENGTH_SHORT).show();
                }
                else {
                    val db = FirebaseAuth.getInstance();

                    db.currentUser?.updatePassword(first_pass);
                    Toast.makeText(binding.root.context, "Пароль оновлено!", Toast.LENGTH_SHORT).show();

                    startActivity(Intent(this, MainActivity::class.java));
                    finish();
                }
            }
        }
        else {
            Toast.makeText(binding.root.context, "Пароль містить менше 6 символів!", Toast.LENGTH_SHORT).show();
        }
    }
}