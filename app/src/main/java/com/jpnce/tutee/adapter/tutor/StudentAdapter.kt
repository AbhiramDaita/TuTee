package com.jpnce.tutee.adapter.tutor

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
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
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
import com.jpnce.tutee.Model.tutor.StudentModel
import com.jpnce.tutee.adapter.viewNotesAdapter.viewholder
import com.jpnce.tutee.student.StudentRealDashboard
import com.jpnce.tutee.student.StudentResetPasswordActivity
import com.jpnce.tutee.student.StudentViewNotes
import com.jpnce.tutee.student.StudentDashboard
import com.jpnce.tutee.student.StudentSignin
import java.time.LocalDate
import java.util.ArrayList
import java.util.HashMap

class StudentAdapter constructor(
    private val mContext: Context,
    mList: List<StudentModel?>,
    teacherId: String
) : RecyclerView.Adapter<StudentAdapter.viewHolder>() {
    private var mList: List<StudentModel?> = ArrayList()
    private var teacherId: String = ""
    private var studentRemarks: String = "0/10"
    private var studentAttendance: String = "absent"
    private val progressDialog: ProgressDialog

    init {
        this.mList = mList
        this.teacherId = teacherId
        progressDialog = ProgressDialog(mContext)
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.layout_student, parent, false)
        return viewHolder(view)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val model: StudentModel? = mList.get(position)
        holder.tv_studentName.setText(model!!.studentName)
        holder.tv_studentSubject.setText(model.studentSubject)
        val currentDate: LocalDate = LocalDate.now()
        val date: String = currentDate.toString()
        holder.tv_todayDate.setText(date)
        holder.btn_present.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                progressDialog.setMessage("Marking Attendance")
                progressDialog.setTitle("Attendance....")
                progressDialog.show()
                studentAttendance = "Present"
                markAsPresent(model, date)
            }
        })
        holder.btn_absent.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                progressDialog.setMessage("Marking Attendance")
                progressDialog.setTitle("Attendance....")
                progressDialog.show()
                studentAttendance = "Absent"
                markAsPresent(model, date)
            }
        })

    }

    private fun createDialog(model: StudentModel?, date: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.custom_remarks, null, false)
        builder.setView(view)
        val et_remarks: EditText = view.findViewById(R.id.et_studentRemarks)
        val btn_giveRemarks: Button = view.findViewById(R.id.btn_giveRemarks)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        btn_giveRemarks.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val remarks: String = et_remarks.getText().toString()
                if (remarks.isEmpty()) {
                    et_remarks.setError("Empty Remarks")
                } else {
                    progressDialog.setMessage("Adding Your Remarks")
                    progressDialog.setTitle("Adding...")
                    progressDialog.setCanceledOnTouchOutside(false)
                    studentRemarks = remarks + "/10"
                    markAsPresent(model, date)
                    alertDialog.dismiss()
                }
            }
        })
    }

    //    private void giveRemarks(StudentModel model, String date) {
    //        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    //        String userId = firebaseUser.getUid();
    //
    //        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(TutorSignUp.ATTENDANCE).child(model.getStudentName()).child(date);
    //        HashMap<String, String> hashMap = new HashMap<>();
    //        hashMap.put("id", userId);
    //        hashMap.put("studentName", model.getStudentName());
    //        hashMap.put("email", model.getStudentEmail());
    //        hashMap.put("studentSubject", model.getStudentSubject());
    //        hashMap.put("remarks", remarks+"/10");
    //        hashMap.put("date", date);
    //        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
    //            @Override
    //            public void onComplete(@NonNull Task<Void> task) {
    //                progressDialog.dismiss();
    //                if (task.isSuccessful()) {
    //                    Toast.makeText(mContext, "Remarks Added Successfully", Toast.LENGTH_SHORT).show();
    //                } else {
    //                    Toast.makeText(mContext, "Remarks Added Failed", Toast.LENGTH_SHORT).show();
    //                }
    //            }
    //        });
    //    }
    private fun markAsPresent(model: StudentModel?, date: String) {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        val userId: String = firebaseUser!!.getUid()
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child(TutorSignUp.Companion.ATTENDANCE)
                .child(model!!.studentName).child(date)
        val hashMap: HashMap<String, String?> = HashMap()
        hashMap.put("id", userId)
        hashMap.put("studentName", model.studentName)
        hashMap.put("email", model.studentEmail)
        hashMap.put("studentSubject", model.studentSubject)
        hashMap.put("present", studentAttendance)
        hashMap.put("remarks", studentRemarks)
        hashMap.put("date", date)
        reference.setValue(hashMap).addOnCompleteListener(object : OnCompleteListener<Void?> {
            public override fun onComplete(task: Task<Void?>) {
                progressDialog.dismiss()
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    public override fun getItemCount(): Int {
        return mList.size
    }

    inner class viewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_studentName: TextView
        val tv_studentSubject: TextView
        val tv_todayDate: TextView
        val btn_present: Button
        val btn_absent: Button


        init {
            tv_studentName = itemView.findViewById(R.id.tv_studentName)
            tv_studentSubject = itemView.findViewById(R.id.tv_studentSubject)
            tv_todayDate = itemView.findViewById(R.id.tv_todayDate)
            btn_present = itemView.findViewById(R.id.btn_present)
            btn_absent = itemView.findViewById(R.id.btn_absent)
        }
    }
}