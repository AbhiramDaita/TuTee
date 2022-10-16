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
import com.jpnce.tutee.Model.tutor.StudentModel
import com.jpnce.tutee.adapter.tutor.StudentAdapter
import com.jpnce.tutee.adapter.viewNotesAdapter.viewholder
import com.jpnce.tutee.student.StudentRealDashboard
import com.jpnce.tutee.student.StudentResetPasswordActivity
import com.jpnce.tutee.student.StudentViewNotes
import com.jpnce.tutee.student.StudentDashboard
import com.jpnce.tutee.student.StudentSignin
import java.util.ArrayList

class TutorDashboard constructor() : AppCompatActivity() {
   lateinit var iv_addUser: ImageView
    var firebaseUser: FirebaseUser? = null
    var fab_addAss: FloatingActionButton? = null
    var email: String? = ""
    var password: String? = ""
    var mList: MutableList<StudentModel?> = ArrayList()
    lateinit var recyclerView: RecyclerView
    var mAdapter: StudentAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_dashboard)
        iv_addUser = findViewById(R.id.iv_addUser)
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser()
        val userId: String = firebaseUser!!.getUid()
        recyclerView = findViewById(R.id.rv_showAllSubject)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this@TutorDashboard))
        //recyclerView = findViewById(R.id.rv_showAllSubject);
        //LinearLayoutManager manager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(manager);
        // -recyclerView.setHasFixedSize(true);
        // recyclerView.setHasFixedSize(true);
        //   recyclerView.setLayoutManager(new LinearLayoutManager(TutorDashboard.this));
        intent = getIntent()
        email = intent.getStringExtra("email")
        password = intent.getStringExtra("password")
        allStudent
        iv_addUser.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val intent: Intent = Intent(this@TutorDashboard, AddUser::class.java)
                intent.putExtra("teacherId", userId)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                startActivity(intent)
            }
        })
    }

    private val allStudent: Unit
        private get() {
            val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
            if (firebaseUser!!.getUid() != null) {
                val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child(TutorSignUp.Companion.STUDENTS_USER).child(
                    firebaseUser.getUid()
                )
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        mList.clear()
                        for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                            val model: StudentModel? = dataSnapshot.getValue(
                                StudentModel::class.java
                            )
                            Log.d("TAG", "onDataChange: " + model!!.studentName)
                            mList.add(model)
                        }
                        mAdapter = StudentAdapter(this@TutorDashboard, mList, firebaseUser.getUid())
                        recyclerView!!.setAdapter(mAdapter)
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
            }
        }

    //back button to previous Activity
    public override fun onBackPressed() {
        startActivity(Intent(this, TutorRealDashboard::class.java))
        return
    }
}