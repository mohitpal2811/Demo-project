package com.arora.developers.demoproject

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class FormActivity : AppCompatActivity() {
    private var metFirstName: EditText? = null
    private var metLastName: EditText? = null
    private var metEmail: EditText? = null
    private var metPass: EditText? = null
    private var metDob: EditText? = null
    private var miId: Int = 0


    private var msFrom: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        metFirstName = findViewById(R.id.addFirstName_et)
        metLastName = findViewById(R.id.addLastName_et)
        metDob = findViewById(R.id.addDob_et)
        metEmail = findViewById(R.id.addEmail_et)
        metPass = findViewById(R.id.addPass_et)


        msFrom = intent.getStringExtra("from")
        if (msFrom == "edit") {
            findViewById<MaterialButton>(R.id.add_btn).setText("update")
            miId = intent.getIntExtra("id", 0)
            metDob!!.setText(intent.getStringExtra("dob"))
            metFirstName!!.setText(intent.getStringExtra("firstname"))
            metEmail!!.setText(intent.getStringExtra("email"))
            metLastName!!.setText(intent.getStringExtra("lastname"))
            metPass!!.setText(intent.getStringExtra("pass"))

        } else
            findViewById<MaterialButton>(R.id.add_btn).setText("Add")
        findViewById<MaterialButton>(R.id.add_btn).setOnClickListener(View.OnClickListener { saveTask() })
        val today = Calendar.getInstance()
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                val month = monthOfYear + 1
                today.set(Calendar.YEAR, year)
                today.set(Calendar.MONTH, monthOfYear)
                today.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val msg = "$dayOfMonth/$month/$year"
                metDob!!.setText(msg)
            }
        }

        val datePickerDialog = DatePickerDialog(
            this@FormActivity,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)

        )
        datePickerDialog.getDatePicker().setMaxDate(Date().time)
        findViewById<EditText>(R.id.addDob_et).setOnClickListener(View.OnClickListener {
            datePickerDialog.show()
        })


    }

    private fun saveTask() {
        val lsFirstName: String = metFirstName!!.text.toString()
        val lsLastName: String = metLastName!!.text.toString()
        val lsEmail: String = metEmail!!.text.toString()
        val lsPass: String = metPass!!.text.toString()
        val lsDob: String = metDob!!.text.toString()
        if (TextUtils.isEmpty(lsFirstName)) {
            metFirstName!!.error = "First Name required"
            metFirstName!!.requestFocus()
            return
        }
        if (TextUtils.isEmpty(lsLastName)) {
            metLastName!!.error = "Last Name required"
            metLastName!!.requestFocus()
            return
        }
        if (TextUtils.isEmpty(lsEmail)) {
            metEmail!!.error = "Email required"
            metEmail!!.requestFocus()
            return
        }

        if (TextUtils.isEmpty(lsDob)) {
            metDob!!.error = "DOB required"
            metDob!!.requestFocus()
            return
        }

        if (lsPass.length < 8) {
            metPass!!.error = "Password must be more than 8"
            metPass!!.requestFocus()
            return
        }
        if (!isValidEmail(lsEmail)) {
            metEmail!!.error = "Enter valid Email"
            metEmail!!.requestFocus()
            return
        }

        val user = User(
            miId,
            lsFirstName,
            lsLastName,
            lsDob,
            lsEmail,
            lsPass
        ) // <- Pass id, firstName, lastName, and age. Although id will be auto-generated because it is a primary key, we need to pass a value or zero (Don't worry, the Room library knows it is the primary key and is auto-generated).
        if (msFrom == "edit") {
            MainScope().launch(Dispatchers.IO) {
                DatabaseClient.getInstance(getApplicationContext())
                    .userDao()
                    ?.updateUser(user)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        getApplicationContext(),
                        "Successfully Updated!",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()

                }
            }


        } else {
            MainScope().launch(Dispatchers.IO) {
                DatabaseClient.getInstance(getApplicationContext())
                    .userDao()
                    ?.addUser(user)


                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        getApplicationContext(),
                        "Successfully added!",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()

                }
            }


        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}