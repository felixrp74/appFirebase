package com.example.appfirebases

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appfirebases.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC
}

class HomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider")

        setup(email,provider.toString())

    }

    private fun setup( email:String, provider:String) {

        title = "Home"
        binding.tvEmail.text = email
        binding.tvProvider.text = provider

        binding.btnCloseSession.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }

}