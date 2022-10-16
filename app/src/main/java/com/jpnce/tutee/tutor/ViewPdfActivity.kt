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
import com.jpnce.tutee.adapter.viewNotesAdapter.viewholder
import com.jpnce.tutee.student.StudentRealDashboard
import com.jpnce.tutee.student.StudentResetPasswordActivity
import com.jpnce.tutee.student.StudentViewNotes
import com.jpnce.tutee.student.StudentDashboard
import com.jpnce.tutee.student.StudentSignin
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ViewPdfActivity constructor() : AppCompatActivity() {
    var urls: String? = null
    var pdfView: PDFView? = null
    var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pdf)
        pdfView = findViewById(R.id.abc)

        // Firstly we are showing the progress
        // dialog when we are loading the pdf
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Loading..")
        dialog!!.show()

        // getting url of pdf using getItentExtra
        urls = getIntent().getStringExtra("url")
        RetrivePdfStream().execute(urls)
    }

    // Retrieving the pdf file using url
    internal inner class RetrivePdfStream constructor() :
        AsyncTask<String?, Void?, InputStream?>() {

        // Here load the pdf and dismiss the dialog box
        override fun onPostExecute(inputStream: InputStream?) {
            pdfView!!.fromStream(inputStream).load()
            dialog!!.dismiss()
        }

        override fun doInBackground(vararg params: String?): InputStream? {
            var inputStream: InputStream? = null
            try {

                // adding url
                val url: URL = URL(params.get(0))
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                // if url connection response code is 200 means ok the execute
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = BufferedInputStream(urlConnection.getInputStream())
                }
            } // if error return null
            catch (e: IOException) {
                return null
            }
            return inputStream
        }
    }
}