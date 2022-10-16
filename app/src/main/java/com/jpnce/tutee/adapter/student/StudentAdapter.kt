package com.jpnce.tutee.adapter.student

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
import android.content.Context
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
import android.widget.PopupWindow
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
import com.jpnce.tutee.Model.parent.StudentModel
import com.jpnce.tutee.adapter.viewNotesAdapter.viewholder
import com.jpnce.tutee.student.StudentRealDashboard
import com.jpnce.tutee.student.StudentResetPasswordActivity
import com.jpnce.tutee.student.StudentViewNotes
import com.jpnce.tutee.student.StudentDashboard
import com.jpnce.tutee.student.StudentSignin
import java.util.ArrayList

class StudentAdapter constructor(private val mContext: Context, mList: List<StudentModel>) :
    RecyclerView.Adapter<StudentAdapter.viewHolder>() {
    private var mList: List<StudentModel> = ArrayList()
    private val progressDialog: ProgressDialog

    init {
        this.mList = mList
        progressDialog = ProgressDialog(mContext)
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.student_layout_for_parent, parent, false)
        return viewHolder(view)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val model: StudentModel = mList.get(position)
        holder.tv_studentName.setText(model!!.studentName)
        holder.tv_studentSubject.setText(model.studentSubject)
        holder.tv_todayDate.setText(model.date)
        holder.tv_present.setText(model.present)
        holder.tv_remarks.setText(model.remarks)
    }

    public override fun getItemCount(): Int {
        return mList.size
    }

    inner class viewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_studentName: TextView
        val tv_studentSubject: TextView
        val tv_todayDate: TextView
        val tv_present: TextView
        val tv_remarks: TextView

        init {
            tv_studentName = itemView.findViewById(R.id.tv_studentName)
            tv_studentSubject = itemView.findViewById(R.id.tv_studentSubject)
            tv_todayDate = itemView.findViewById(R.id.tv_todayDate)
            tv_present = itemView.findViewById(R.id.tv_present)
            tv_remarks = itemView.findViewById(R.id.tv_remarks)
        }
    }
}