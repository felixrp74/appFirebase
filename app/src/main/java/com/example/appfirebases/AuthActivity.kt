package com.example.appfirebases

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.appfirebases.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class AuthActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()

    }

    private fun setup(){

        title = "Authentication"

        binding.btnRegister.setOnClickListener {
            val email = binding.emailEditText
            val pass = binding.passwordEditText
            if(email.text.isNotEmpty() && pass.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.text.toString(),
                    pass.text.toString()).addOnCompleteListener{
                        if (it.isSuccessful){
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                    }
            }
        }

        binding.btnLogIn.setOnClickListener {
            val email = binding.emailEditText
            val pass = binding.passwordEditText
            if(email.text.isNotEmpty() && pass.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.text.toString(),
                        pass.text.toString()).addOnCompleteListener{
                        if (it.isSuccessful){
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                    }
            }
        }
    }

    private fun showHome(email: String?, provider:ProviderType) {
        val homeIntent:Intent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider)
        }
        startActivity(homeIntent)
    }

    private fun showAlert() {
        val builder=AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Auth error message")
        builder.setPositiveButton("Accept", null)
        val dialog = builder.create()
        dialog.show()
    }
}










