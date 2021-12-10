package com.example.unitrackerv12.IngrReg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*
import androidx.appcompat.app.AppCompatActivity
import com.example.unitrackerv12.R
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_signup.SU_Email
import kotlinx.android.synthetic.main.activity_signup.SU_Notificar
import kotlinx.android.synthetic.main.activity_signup.SU_Password
import kotlinx.android.synthetic.main.activity_signup.SignIn
import kotlinx.android.synthetic.main.activity_signup.buttonReg

class SignUpActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_signup)
        SignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonReg.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            if (SU_Email.text.toString().isNullOrEmpty() || SU_Password.text.toString()
                    .isNullOrEmpty() || SU_Numero.text.toString().isNullOrEmpty())
                SU_Notificar.text = "Debes llenar todos los datos..."
            else {
                //Modificar para añadir el número y nombres
                auth.createUserWithEmailAndPassword(
                    SU_Email.text.toString(),
                    SU_Password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            SU_Notificar.text =
                                "Usuario creado"
                            val user = auth.currentUser
                            updateUI(user)
                        } else {
                            SU_Notificar.text = "Hubo un error, por favor, vuelve a intentar."
                            updateUI(null)
                        }
                    }
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
    }
}