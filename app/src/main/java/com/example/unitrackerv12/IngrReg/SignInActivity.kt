package com.example.unitrackerv12.IngrReg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.unitrackerv12.MapsActivity
import com.example.unitrackerv12.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signin.*


class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_signin)
        SignUp.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonInicSes.setOnClickListener {
            val inputMethodManager =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            if (SI_Email.text.toString().isNullOrEmpty() || SI_Password.text.toString().isNullOrEmpty())
                SI_Notificar.text = "Debes llenar todos los datos..."
            else {
                auth.signInWithEmailAndPassword(SI_Email.text.toString(),SI_Password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            SI_Notificar.text =  "Ingresando :)"
                            val user = auth.currentUser
                            updateUI(user, SI_Email.text.toString() )
                        } else
                            SI_Notificar.text = "e-mail o contraseña incorrectos"
                    }
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?, emailAdd: String) {
        if(currentUser !=null){
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("emailAddress", emailAdd);
            startActivity(intent)
            finish()
        }
    }

}
