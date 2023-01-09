package com.arora.developers.demoproject

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserListAdapter(private val mCtx: Context, userList: MutableList<User>) :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder?>() {
    private val userList: MutableList<User>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.custom_row, parent, false)
        return UserViewHolder(view)
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val t: User = userList[position]
        holder.mtvID.setText("" + t.fiId)
        holder.mtvName.setText(t.fsFirstName + " " + t.fsLastName)
        holder.mtvEmail.setText(t.fsEmail)
        holder.mtvDOB.setText(t.fsDob)
        holder.itemView.findViewById<ImageView>(R.id.ivDelete)
            .setOnClickListener(View.OnClickListener {
                showDefaultDialog(mCtx, position)
            })
        holder.itemView.findViewById<ImageView>(R.id.ivEdit)
            .setOnClickListener(View.OnClickListener {
                val user: User = userList[position]
                val intent = Intent(mCtx, FormActivity::class.java)
                intent.putExtra("from", "edit")
                intent.putExtra("id", user.fiId)
                intent.putExtra("firstname", user.fsFirstName)
                intent.putExtra("email", user.fsEmail)
                intent.putExtra("lastname", user.fsLastName)
                intent.putExtra("pass", user.fsPassword)
                intent.putExtra("dob", user.fsDob)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                mCtx.startActivity(intent)
            })
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mtvID: TextView
        var mtvName: TextView
        var mtvDOB: TextView
        var mtvEmail: TextView


        init {
            mtvID = itemView.findViewById(R.id.id_txt)
            mtvName = itemView.findViewById(R.id.Name_txt)
            mtvDOB = itemView.findViewById(R.id.Dob_txt)
            mtvEmail = itemView.findViewById(R.id.Email_txt)

        }
    }

    init {
        this.userList = userList
    }

    private fun showDefaultDialog(context: Context, position: Int) {

        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Delete")
        alertDialog.setMessage("Are you sure you want to delete this user?")
        alertDialog.setPositiveButton(
            "yes"
        ) { _, _ ->
            MainScope().launch(Dispatchers.IO) {
                DatabaseClient.getInstance(context)
                    .userDao()
                    ?.delete(userList.get(position))
                withContext(Dispatchers.Main)
                {
                    userList.removeAt(position)
                    notifyDataSetChanged()
                }
            }
        }
        alertDialog.setNegativeButton(
            "No"
        ) { dialog, _ ->
            dialog?.dismiss()
        }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(mCtx.getColor(R.color.white))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(mCtx.getColor(R.color.white))
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mCtx.getColor(R.color.black))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mCtx.getColor(R.color.black))
    }


}