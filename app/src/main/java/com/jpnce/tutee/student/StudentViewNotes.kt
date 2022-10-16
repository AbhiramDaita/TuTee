package com.jpnce.tutee.student

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import com.jpnce.tutee.R
import com.jpnce.tutee.tutor.AddStudent
import com.jpnce.tutee.tutor.AddParent
import com.jpnce.tutee.tutor.TutorDashboard
import android.widget.EditText
import com.google.firebase.storage.StorageReference
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.FirebaseDatabase
import android.app.Activity
import android.app.ProgressDialog
import com.google.android.gms.tasks.OnSuccessListener
import com.jpnce.tutee.tutor.putPDF
import android.widget.Toast
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
import android.net.Uri
import com.jpnce.tutee.tutor.ViewPdfActivity
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.widget.TextView
import android.widget.LinearLayout
import android.view.WindowManager
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
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.view.Gravity
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
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import android.os.Build
import android.util.Log
import com.jpnce.tutee.adapter.viewNotesAdapter.viewholder
import com.jpnce.tutee.student.StudentRealDashboard
import com.jpnce.tutee.student.StudentResetPasswordActivity
import com.jpnce.tutee.student.StudentViewNotes
import com.jpnce.tutee.student.StudentDashboard
import com.jpnce.tutee.student.StudentSignin
import java.util.ArrayList

class StudentViewNotes constructor() : AppCompatActivity() {
    var arrayList: ArrayList<NotesModel?> = ArrayList()
    var adapter: viewNotesAdapter? = null
    lateinit var rv_viewnotes: RecyclerView
    var childName: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_view_notes)
        rv_viewnotes = findViewById(R.id.rv_viewNotes)
        rv_viewnotes.setHasFixedSize(true)
        rv_viewnotes.setLayoutManager(LinearLayoutManager(this@StudentViewNotes))
        intent = getIntent()
        childName = intent.getStringExtra("childName")
        Toast.makeText(this@StudentViewNotes, "child name : " + childName, Toast.LENGTH_SHORT)
            .show()
        loadNotes()
    }

    private fun loadNotes() {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        Log.d("TAG1", "inside load notes")
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child("notes")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            public override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TAG1", "loading notes")
                arrayList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                    for (dataSnapshot1: DataSnapshot in dataSnapshot.getChildren()) {
                        val model: NotesModel? = dataSnapshot1.getValue(NotesModel::class.java)
                        Log.d("TAG1", "onDataChange: " + dataSnapshot1)
                        arrayList.add(model)
                    }
                }
                Log.d("TAG1", "arraylist size : " + arrayList.size)
                adapter = viewNotesAdapter(this@StudentViewNotes, arrayList, childName){index -> onView(index)}
                rv_viewnotes!!.setAdapter(adapter)
            }

            public override fun onCancelled(error: DatabaseError) {

                // we are showing that error message in toast
                Toast.makeText(this@StudentViewNotes, "Error Loading Notes", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    //back button to previous Activity
    public override fun onBackPressed() {
        startActivity(Intent(this, StudentRealDashboard::class.java))
        return
    }
    private fun onView(index : Int){

        var pdfViewIntent = Intent(Intent.ACTION_VIEW)
        pdfViewIntent.setDataAndType(Uri.parse(arrayList[index]!!.url),"application/pdf")
        var chooser = Intent.createChooser(pdfViewIntent,"Choose an app");
        startActivity(chooser)
    }
}