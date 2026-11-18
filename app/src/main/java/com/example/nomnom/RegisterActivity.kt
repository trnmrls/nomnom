package com.example.nomnom

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        
        auth = FirebaseAuth.getInstance()
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<LinearLayout>(R.id.llLogin)?.setOnClickListener {
            finish()
        }

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnCreateAccount = findViewById<LinearLayout>(R.id.btnCreateAccount)

        val cbLength = findViewById<CheckBox>(R.id.cbLength)
        val cbLowercase = findViewById<CheckBox>(R.id.cbLowercase)
        val cbUppercase = findViewById<CheckBox>(R.id.cbUppercase)
        val cbNumber = findViewById<CheckBox>(R.id.cbNumber)
        val cbSymbol = findViewById<CheckBox>(R.id.cbSymbol)

        etPassword.setupPasswordVisibilityToggle()
        etConfirmPassword.setupPasswordVisibilityToggle()

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val p = s.toString()
                cbLength.isChecked = p.length >= 8
                cbLowercase.isChecked = p.any { it.isLowerCase() }
                cbUppercase.isChecked = p.any { it.isUpperCase() }
                cbNumber.isChecked = p.any { it.isDigit() }
                cbSymbol.isChecked = p.any { !it.isLetterOrDigit() }
            }
        })

        btnCreateAccount.setOnClickListener {
            val username = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!cbLength.isChecked || !cbLowercase.isChecked || !cbUppercase.isChecked || !cbNumber.isChecked || !cbSymbol.isChecked) {
                Toast.makeText(this, "Please meet all password requirements", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                            
                            // Initialize Cloud Firestore
                            val db = FirebaseFirestore.getInstance()
                            
                            // Create a new user with email and username
                            val userMap = hashMapOf(
                                "username" to username,
                                "email" to email
                            )
                            
                            // Add a new document with the user's UID as the ID
                            db.collection("users").document(user.uid)
                                .set(userMap)
                                .addOnSuccessListener {
                                    startActivity(Intent(this, WelcomeActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(baseContext, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    } else {
                        Toast.makeText(baseContext, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

    }
}
