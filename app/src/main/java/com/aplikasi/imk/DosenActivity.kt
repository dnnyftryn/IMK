package com.aplikasi.imk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.firebaserealtimedatabse.adapter.AbsenAdapter
import com.aplikasi.imk.databinding.ActivityDosenBinding
import com.aplikasi.imk.model.Absen
import com.google.firebase.database.*
import com.google.firebase.database.R

class DosenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDosenBinding


    private lateinit var empRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var empList: ArrayList<Absen>
    private lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenBinding.inflate(layoutInflater)
        setContentView(binding.root)



        empRecyclerView = binding.rvEmp
        empRecyclerView.layoutManager = LinearLayoutManager(this)
        empRecyclerView.setHasFixedSize(true)

        empList = arrayListOf<Absen>()

        getAbsentData()
    }

    private fun getAbsentData() {
        dbRef = FirebaseDatabase.getInstance().getReference("Absent")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear()
                if (snapshot.exists()){
                    for (empSnap in snapshot.children){
                        val empData = empSnap.getValue(Absen::class.java)
                        empList.add(empData!!)
                    }
                    val mAdapter = AbsenAdapter(empList)
                    empRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : AbsenAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            deleteRecord(empList[position].absenId)
                        }

                    })

                    empRecyclerView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun deleteRecord(absenId: String?) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Absent").child(absenId!!)
        val mTask = dbRef.removeValue()

        mTask
            .addOnSuccessListener {
            Toast.makeText(this, "Employee data deleted", Toast.LENGTH_LONG).show()
                finish()
                startActivity(intent)
            }
            .addOnFailureListener{ error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }
    }
}