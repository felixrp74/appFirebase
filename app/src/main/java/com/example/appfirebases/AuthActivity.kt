package com.example.appfirebases

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.appfirebases.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class AuthActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAuthBinding
    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup
        setup()
        session()

    }

    override fun onStart() {
        super.onStart()
        binding.authLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email",null)
        val provider = prefs.getString("provider",null)

        if (email != null && provider != null){
            binding.authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup(){

        title = "Authentication"

        binding.btnGoogle.setOnClickListener {
            //Configuration Google
            val googleconf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this,googleconf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)

        }

        binding.btnRegister.setOnClickListener {
            val email = binding.emailEditText
            val pass = binding.passwordEditText
            if(email.text.isNotEmpty() && pass.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.text.toString(), pass.text.toString())
                    .addOnCompleteListener{
                        if (it.isSuccessful){
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }else{
                            showAlert("REGISTER ERROR")
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
                            showAlert("LOGIN ERROR")
                        }
                    }
            }
        }
    }

    private fun showAlert(error:String) {
        val builder=AlertDialog.Builder(this)
        builder.setTitle(error)
        builder.setMessage("Auth error message")
        builder.setPositiveButton("Accept", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String?, provider:ProviderType) {
        val homeIntent:Intent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null){

                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful){
                            showHome(account.email,ProviderType.BASIC)
                        }else{
                            showAlert("GOOGLE ERROR")
                        }
                    }

                }
            }catch (e:ApiException){
                showAlert("GOOGLE ERROR")
            }
        }
    }

}










