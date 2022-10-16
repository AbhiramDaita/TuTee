package com.jpnce.tutee.tutor

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
import android.util.Log
import android.view.*
import android.widget.*
import com.jpnce.tutee.adapter.viewNotesAdapter.viewholder
import com.jpnce.tutee.student.StudentRealDashboard
import com.jpnce.tutee.student.StudentResetPasswordActivity
import com.jpnce.tutee.student.StudentViewNotes
import com.jpnce.tutee.student.StudentDashboard
import com.jpnce.tutee.student.StudentSignin
import java.util.HashMap
import java.util.regex.Pattern

class AddParent() : AppCompatActivity() {
    var et_email: EditText? = null
    var et_password: EditText? = null
    var et_confirmPassword: EditText? = null
    var et_username: EditText? = null
    var et_subject: EditText? = null
    var et_child: EditText? = null
    lateinit var btn_Register: Button
    var emailRegex = ("^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$")
    var pat = Pattern.compile(emailRegex)
    var progressDialog: ProgressDialog? = null
    var mAuth: FirebaseAuth? = null
    var mUser: FirebaseUser? = null
    var reference: DatabaseReference? = null

    var teacherId: String? = ""
    var teacherEmail: String? = ""
    var teacherPassword: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_parent)
        et_username = findViewById(R.id.et_username)
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        et_subject = findViewById(R.id.et_subject)
        et_confirmPassword = findViewById(R.id.et_confirmPassword)
        et_child = findViewById(R.id.et_child)
        btn_Register = findViewById(R.id.btn_register)
        progressDialog = ProgressDialog(this)
        intent = getIntent()
        teacherId = intent.getStringExtra("teacherId")
        teacherEmail = intent.getStringExtra("email")
        teacherPassword = intent.getStringExtra("password")
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser
        btn_Register.setOnClickListener(View.OnClickListener { PerformAuth() })
    }

    private fun PerformAuth() {
        val email = et_email!!.text.toString()
        val password = et_password!!.text.toString()
        val confirmPassword = et_confirmPassword!!.text.toString()
        val username = et_username!!.text.toString()
        val subject = et_subject!!.text.toString()
        val child = et_child!!.text.toString()
        if (email.isEmpty()) {
            et_email!!.error = "Please Enter Email"
            return
        } else if (!pat.matcher(email).matches()) {
            et_email!!.error = "Please Enter a valid Email"
            return
        } else if (password.isEmpty()) {
            et_password!!.error = "Please input Password"
            return
        } else if (password.length < 6) {
            et_password!!.error = "Password too short"
            return
        } else if (confirmPassword != password) {
            et_confirmPassword!!.error = "Password doesn't matches"
            return
        } else if (subject.isEmpty()) {
            et_subject!!.error = "Please Enter Subject"
            return
        } else {
            progressDialog!!.setMessage("Creating Account....")
            progressDialog!!.setTitle("Creating")
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog!!.dismiss()
                    val firebaseUser = mAuth!!.currentUser
                    val userId = firebaseUser!!.uid
                    reference =
                        FirebaseDatabase.getInstance().reference.child(TutorSignUp.Companion.PARENTS_USER)
                    val hashMap = HashMap<String, String>()
                    hashMap["id"] = userId
                    hashMap["username"] = username
                    hashMap["email"] = email
                    hashMap["password"] = password
                    hashMap["subject"] = subject
                    hashMap["child"] = child
                    reference!!.push().setValue(hashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            loginTeacher(teacherEmail, teacherPassword)
                            sendUserToMainActivity()
                        }
                    }
                    Toast.makeText(this@AddParent, "Registration Successful", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    progressDialog!!.dismiss()
                    Log.d("TAG", "onComplete: " + task.exception)
                    Toast.makeText(this@AddParent, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loginTeacher(teacherEmail: String?, teacherPassword: String?) {
        mAuth!!.signInWithEmailAndPassword((teacherEmail)!!, (teacherPassword)!!)
            .addOnCompleteListener(
                OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendUserToMainActivity()
                    } else {
                        progressDialog!!.dismiss()
                    }
                })
    }

    private fun sendUserToMainActivity() {
        val intent = Intent(this@AddParent, TutorDashboard::class.java)
        intent.putExtra("email", teacherEmail)
        intent.putExtra("password", teacherPassword)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    //back button to previous Activity
    override fun onBackPressed() {
        startActivity(Intent(this, AddUser::class.java))
        return
    }
}