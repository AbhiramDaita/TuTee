package com.jpnce.tutee.student

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import com.jpnce.tutee.R
import com.jpnce.tutee.tutor.AddStudent
import com.jpnce.tutee.tutor.AddParent
import com.jpnce.tutee.tutor.TutorDashboard
import com.google.firebase.storage.StorageReference
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.FirebaseDatabase
import android.app.Activity
import android.app.ProgressDialog
import com.google.android.gms.tasks.OnSuccessListener
import com.jpnce.tutee.tutor.putPDF
import com.jpnce.tutee.tutor.TutorRealDashboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.jpnce.tutee.tutor.TutorSignUp
import com.jpnce.tutee.tutor.AddUser
import com.jpnce.tutee.Model.NotesModel
import com.jpnce.tutee.adapter.viewNotesAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.DialogInterface
import com.jpnce.tutee.tutor.ViewPdfActivity
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.jpnce.tutee.tutor.TutorResetPasswordActivity
import com.jpnce.tutee.tutor.TutorSignInWithGoogle
import com.jpnce.tutee.MainActivity
import com.jpnce.tutee.tutor.TutorSignin
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.github.barteksc.pdfviewer.PDFView
import com.jpnce.tutee.tutor.ViewPdfActivity.RetrivePdfStream
import android.os.AsyncTask
import androidx.cardview.widget.CardView
import com.jpnce.tutee.tutor.AddNotes
import com.jpnce.tutee.tutor.ViewNotes
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.jpnce.tutee.parent.ParentResetPasswordActivity
import com.jpnce.tutee.parent.ParentDashboard
import com.jpnce.tutee.parent.ParentSignin
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.*
import android.widget.*
import com.google.android.gms.tasks.Task
import com.jpnce.tutee.adapter.viewNotesAdapter.viewholder
import com.jpnce.tutee.student.StudentRealDashboard
import com.jpnce.tutee.student.StudentResetPasswordActivity
import com.jpnce.tutee.student.StudentViewNotes
import com.jpnce.tutee.student.StudentDashboard
import com.jpnce.tutee.student.StudentSignin
import java.util.regex.Pattern

class StudentSignin constructor() : AppCompatActivity() {
    var et_email: EditText? = null
    var et_password: EditText? = null
    lateinit var btn_login: Button
    lateinit var tv_registerBtn: TextView
    lateinit var tv_forgotPassword: TextView
    var ll_loginWithGoogle: LinearLayout? = null
    var emailRegex: String = ("^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$")
    var childName: String = ""
    var pat: Pattern = Pattern.compile(emailRegex)
    var progressDialog: ProgressDialog? = null
    var mAuth: FirebaseAuth? = null
    var mUser: FirebaseUser? = null
    private var et_name: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        if (mAuth!!.getCurrentUser() != null) {
            Toast.makeText(
                this, "Already Logged In",
                Toast.LENGTH_LONG
            ).show()
            val intent: Intent = Intent(this, StudentRealDashboard::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE)
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            setContentView(R.layout.activity_student_signin)
            et_email = findViewById(R.id.et_email)
            et_password = findViewById(R.id.et_password)
            btn_login = findViewById(R.id.btn_login)
            tv_forgotPassword = findViewById(R.id.tv_forgotPassword)
            et_name = findViewById(R.id.et_name)
            tv_forgotPassword.setOnClickListener(object : View.OnClickListener {
                public override fun onClick(v: View) {
                    startActivity(
                        Intent(
                            this@StudentSignin,
                            StudentResetPasswordActivity::class.java
                        )
                    )
                }
            })
            progressDialog = ProgressDialog(this)
            mAuth = FirebaseAuth.getInstance()
            mUser = mAuth!!.getCurrentUser()
            btn_login.setOnClickListener(object : View.OnClickListener {
                public override fun onClick(v: View) {
                    performLogin()
                }
            })
        }
    }

    private fun performLogin() {
        val email: String = et_email!!.getText().toString()
        val password: String = et_password!!.getText().toString()
        childName = et_name!!.getText().toString()
        if (email.isEmpty()) {
            et_email!!.setError("Please Enter Email")
            return
        } else if (!pat.matcher(email).matches()) {
            et_email!!.setError("Please Enter a valid Email")
            return
        } else if (password.isEmpty()) {
            et_password!!.setError("Please input Password")
            return
        } else if (password.length < 6) {
            et_password!!.setError("Password too short")
            return
        } else {
            progressDialog!!.setMessage("Login in to your Account....")
            progressDialog!!.setTitle("Loading")
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
            mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                    public override fun onComplete(task: Task<AuthResult?>) {
                        if (task.isSuccessful()) {
                            progressDialog!!.dismiss()
                            sendUserToMainActivity()
                            Toast.makeText(
                                this@StudentSignin,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            progressDialog!!.dismiss()
                            Toast.makeText(this@StudentSignin, "Login Failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
        }
    }

    private fun sendUserToMainActivity() {
        val intent: Intent = Intent(this@StudentSignin, StudentRealDashboard::class.java)
        intent.putExtra("childName", childName)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    public override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        return
    }
}