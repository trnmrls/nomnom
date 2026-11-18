package com.example.nomnom

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        
        auth = FirebaseAuth.getInstance()
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<LinearLayout>(R.id.llCreateAccount)?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<LinearLayout>(R.id.btnLogin)

        etPassword.setupPasswordVisibilityToggle()

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}

// Reusable function to attach the toggle behavior
fun EditText.setupPasswordVisibilityToggle() {
    this.setOnTouchListener { _, event ->
        val drawableEndIndex = 2 // 0=Start, 1=Top, 2=End, 3=Bottom

        if (event.action == MotionEvent.ACTION_UP) {
            val drawableEnd = this.compoundDrawables[drawableEndIndex]
            if (drawableEnd != null) {
                // Check if the tap falls within the bounds of the end drawable
                val tapArea = this.right - this.paddingEnd - drawableEnd.bounds.width()
                if (event.rawX >= tapArea) {

                    val isCurrentlyVisible = this.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)

                    if (isCurrentlyVisible) {
                        // Hide Password
                        this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        this.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_lock, 0, R.drawable.ic_visibility_off, 0
                        )
                    } else {
                        // Show Password
                        this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        this.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_lock, 0, R.drawable.ic_visibility, 0 // Use your open eye icon here
                        )
                    }

                    // Maintain custom font styling if necessary
                    this.typeface = android.graphics.Typeface.DEFAULT

                    // Move cursor back to the end of the text
                    this.setSelection(this.text.length)

                    return@setOnTouchListener true
                }
            }
        }
        false
    }
}
