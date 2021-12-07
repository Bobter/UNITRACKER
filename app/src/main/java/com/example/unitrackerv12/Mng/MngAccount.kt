package com.example.unitrackerv12.Mng

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitrackerv12.IngrReg.SignInActivity
import com.example.unitrackerv12.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_mngaccount.*
import kotlinx.android.synthetic.main.activity_signup.*

class MngAccount : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mngaccount)
        buttonCerrarSes.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this,SignInActivity::class.java))
        }
    }


}


