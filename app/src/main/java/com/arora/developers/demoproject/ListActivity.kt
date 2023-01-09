package com.arora.developers.demoproject

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListActivity : AppCompatActivity() {
    private var mbAddUser: FloatingActionButton? = null
    private var mrUserList: RecyclerView? = null
    private var mlUserList: MutableList<User>? = null
    private var mlmLayoutManager: LinearLayoutManager? = null
    private var miUpper: Int = 10
    private var miLower: Int = 0
    private var mbPagination: Boolean = false
    private var mAdapter: UserListAdapter? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mrUserList = findViewById(R.id.recyclerview_tasks)
        mlmLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mrUserList!!.setLayoutManager(mlmLayoutManager)

        mbAddUser = findViewById(R.id.floating_button_add)
        mbAddUser!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ListActivity, FormActivity::class.java)
            intent.putExtra("from", "add")
            startActivity(intent)
        })


        getUsers(miUpper, miLower)

        val onScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (mlmLayoutManager != null && mlmLayoutManager!!.findLastCompletelyVisibleItemPosition() == mlUserList!!.size - 1 && recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) {

                        //Do pagination.. i.e. fetch new data
                        mbPagination = true
                        miLower = mlUserList!!.size
                        miUpper += 10
                        getUsers(miUpper, miLower)
                    }
                }
            }

        mrUserList!!.addOnScrollListener(onScrollListener)
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 2) {

                    findViewById<FrameLayout>(R.id.progress_bar_view).visibility = View.VISIBLE
                    MainScope().launch(Dispatchers.IO) {


                        var ldbUserDatabase = DatabaseClient.getInstance(getApplicationContext())
                        var llTempList = ldbUserDatabase.userDao()
                            ?.search(s.toString())?.toList()
                        if (llTempList!!.size > 0)
                            mlUserList = (llTempList as MutableList<User>?)!!
                        else {
                            mlUserList = emptyList<User>().toMutableList()
                            mrUserList!!.visibility = View.GONE
                        }
                        withContext(Dispatchers.Main) {
                            //update the UI
                            findViewById<FrameLayout>(R.id.progress_bar_view).visibility =
                                View.GONE

                            if (mlUserList!!.size > 0) {
                                mrUserList!!.visibility = View.VISIBLE
                                mrUserList!!.adapter =
                                    UserListAdapter(applicationContext, mlUserList!!)

                            } else
                                Toast.makeText(
                                    getApplicationContext(),
                                    "No Users Found",
                                    Toast.LENGTH_LONG
                                )
                                    .show()


                        }

                    }
                } else if (count == 0)
                    getUsers(10, 0)

            }
        }

        findViewById<EditText>(R.id.Search_et).addTextChangedListener(textWatcher)

    }

    private fun getUsers(fiUpper: Int, fiLower: Int) {
        findViewById<FrameLayout>(R.id.progress_bar_view).visibility = View.VISIBLE
        MainScope().launch(Dispatchers.IO) {


            var ldbUserDatabase = DatabaseClient.getInstance(getApplicationContext())

            var llTempList = ldbUserDatabase.userDao()
                ?.readAllData(fiUpper, fiLower)?.toList()
            if (!mbPagination) {
                if (llTempList!!.size > 0)
                    mlUserList = (llTempList as MutableList<User>?)!!
                else {
                    mlUserList = emptyList<User>().toMutableList()
                    mrUserList!!.visibility = View.GONE
                }

                /* mlUserList = (ldbUserDatabase.userDao()
                     ?.readAllData(fiUpper, fiLower)?.toList() as MutableList<User>?)!!*/
            } else
                mlUserList!!.addAll(
                    ldbUserDatabase.userDao()
                        ?.readAllData(fiUpper, fiLower)?.toList()!!
                )
            mAdapter = UserListAdapter(this@ListActivity, mlUserList!!)
            withContext(Dispatchers.Main) {
                //update the UI
                findViewById<FrameLayout>(R.id.progress_bar_view).visibility = View.GONE
                if (!mbPagination) {
                    if (mlUserList!!.size > 0) {
                        mrUserList!!.visibility = View.VISIBLE
                        mrUserList!!.adapter = mAdapter
                    } else
                        Toast.makeText(getApplicationContext(), "No Users Found", Toast.LENGTH_LONG)
                            .show()
                } else
                    mAdapter!!.notifyDataSetChanged()
            }

        }

    }

    override fun onResume() {
        super.onResume()
        mbPagination = false
        getUsers(10, 0)
    }


}
