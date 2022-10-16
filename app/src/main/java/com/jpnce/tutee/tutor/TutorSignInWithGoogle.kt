package com.jpnce.tutee.tutor

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
import com.google.android.gms.tasks.Task
import com.jpnce.tutee.adapter.viewNotesAdapter.viewholder
import com.jpnce.tutee.student.StudentRealDashboard
import com.jpnce.tutee.student.StudentResetPasswordActivity
import com.jpnce.tutee.student.StudentViewNotes
import com.jpnce.tutee.student.StudentDashboard
import com.jpnce.tutee.student.StudentSignin
import java.util.HashMap

class TutorSignInWithGoogle constructor() : TutorSignin() {
    var mGoogleSignInClient: GoogleSignInClient? = null
    override var mAuth: FirebaseAuth? = null
    override var mUser: FirebaseUser? = null
    override var progressDialog: ProgressDialog? = null
    var reference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_sign_in_with_google)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Google Sign In...")
        progressDialog!!.show()
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("245921808652-ropn0it43laojjh8fnvv5fuq4mitor6m.apps.googleusercontent.com")
                .requestEmail()
                .build()
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.getCurrentUser()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.getIdToken())
            } catch (e: ApiException) {
                progressDialog!!.dismiss()
                finish()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                public override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful()) {
                        progressDialog!!.dismiss()
                        val firebaseUser: FirebaseUser? = mAuth!!.getCurrentUser()
                        val userId: String = firebaseUser!!.getUid()
                        reference = FirebaseDatabase.getInstance().getReference()
                            .child(TutorSignUp.Companion.TUTOR_USERS).child(userId)
                        val hashMap: HashMap<String, String?> = HashMap()
                        hashMap.put("id", userId)
                        hashMap.put("username", firebaseUser.getDisplayName())
                        hashMap.put("email", firebaseUser.getEmail())
                        reference!!.setValue(hashMap)
                            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                public override fun onComplete(task: Task<Void?>) {
                                    if (task.isSuccessful()) {
                                        updateUI(firebaseUser)
                                    }
                                }
                            })
                    } else {
                        progressDialog!!.dismiss()
                        finish()
                    }
                }
            })
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent: Intent = Intent(this@TutorSignInWithGoogle, TutorDashboard::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    companion object {
        private val RC_SIGN_IN: Int = 101
    }
}