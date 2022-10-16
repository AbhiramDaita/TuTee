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
import android.view.*
import android.widget.*
import com.google.android.gms.tasks.Task
import com.jpnce.tutee.adapter.viewNotesAdapter.viewholder
import com.jpnce.tutee.student.StudentRealDashboard
import com.jpnce.tutee.student.StudentResetPasswordActivity
import com.jpnce.tutee.student.StudentViewNotes
import com.jpnce.tutee.student.StudentDashboard
import com.jpnce.tutee.student.StudentSignin
import java.util.HashMap
import java.util.regex.Pattern

class TutorSignUp constructor() : AppCompatActivity() {
    var et_email: EditText? = null
    var et_password: EditText? = null
    var et_confirmPassword: EditText? = null
    var et_username: EditText? = null
    lateinit var btn_Register: Button
    lateinit var tv_loginBtn: TextView
    lateinit var ll_registerWithGoogle: LinearLayout
    var emailRegex: String = ("^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$")
    var pat: Pattern = Pattern.compile(emailRegex)
    var progressDialog: ProgressDialog? = null
    var mAuth: FirebaseAuth? = null
    var mUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_sign_up)
        et_username = findViewById(R.id.et_username)
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        et_confirmPassword = findViewById(R.id.et_confirmPassword)
        btn_Register = findViewById(R.id.btn_register)
        tv_loginBtn = findViewById(R.id.tv_loginButton)
        ll_registerWithGoogle = findViewById(R.id.ll_registerWithGoogle)
        progressDialog = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.getCurrentUser()
        tv_loginBtn.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                startActivity(Intent(this@TutorSignUp, TutorSignin::class.java))
            }
        })
        btn_Register.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                PerformAuth()
            }
        })
        ll_registerWithGoogle.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                startActivity(Intent(this@TutorSignUp, TutorSignInWithGoogle::class.java))
            }
        })
    }

    private fun PerformAuth() {
        val email: String = et_email!!.getText().toString()
        val password: String = et_password!!.getText().toString()
        val confirmPassword: String = et_confirmPassword!!.getText().toString()
        val username: String = et_username!!.getText().toString()
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
        } else if (!(confirmPassword == password)) {
            et_confirmPassword!!.setError("Password doesn't matches")
            return
        } else {
            progressDialog!!.setMessage("Creating your Account....")
            progressDialog!!.setTitle("Creating")
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
            mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                    public override fun onComplete(task: Task<AuthResult?>) {
                        if (task.isSuccessful()) {
                            progressDialog!!.dismiss()
                            val firebaseUser: FirebaseUser? = mAuth!!.getCurrentUser()
                            val userId: String = firebaseUser!!.getUid()
                            reference =
                                FirebaseDatabase.getInstance().getReference().child(TUTOR_USERS)
                                    .child(userId)
                            val hashMap: HashMap<String, String> = HashMap()
                            hashMap.put("id", userId)
                            hashMap.put("username", username)
                            hashMap.put("email", email)
                            reference!!.setValue(hashMap)
                                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                    public override fun onComplete(task: Task<Void?>) {
                                        if (task.isSuccessful()) {
                                            sendUserToMainActivity(userId, email, password)
                                        }
                                    }
                                })
                            Toast.makeText(
                                this@TutorSignUp,
                                "Registration Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            progressDialog!!.dismiss()
                            Toast.makeText(
                                this@TutorSignUp,
                                "Registration Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }
    }

    private fun sendUserToMainActivity(userId: String, email: String, password: String) {
        val intent: Intent = Intent(this@TutorSignUp, TutorRealDashboard::class.java)
        intent.putExtra("email", email)
        intent.putExtra("userId", email)
        intent.putExtra("password", password)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    companion object {
        val TUTOR_USERS: String = "TutorUsers"
        val STUDENTS_USER: String = "StudentUsers"
        val PARENTS_USER: String = "ParentsUsers"
        val ATTENDANCE: String = "Attendance"
        val REMARKS: String = "Remarks"
    }
}