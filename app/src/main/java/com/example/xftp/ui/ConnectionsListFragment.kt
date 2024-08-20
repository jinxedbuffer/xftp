package com.example.xftp.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xftp.R
import com.example.xftp.models.ConnectionAdapter
import com.example.xftp.models.ConnectionDao
import com.example.xftp.models.ConnectionDatabase
import com.example.xftp.models.ConnectionModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionsListFragment : Fragment(), ConnectionAdapter.OnItemClickListener {
    private lateinit var db: ConnectionDatabase
    private lateinit var dao: ConnectionDao
    private lateinit var items: MutableList<ConnectionModel>
    private lateinit var adapter: ConnectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_connections_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up database
        db = ConnectionDatabase.getDatabase(view.context)
        dao = db.connectionDao()

        // load already added connections
        items = mutableListOf()
        CoroutineScope(Dispatchers.IO).launch {
            items.addAll(dao.getAllConnections())
        }
        adapter = ConnectionAdapter(items,this)

        // set up recycler view
        val connectionListView = view.findViewById<RecyclerView>(R.id.connection_list)
        connectionListView.layoutManager = LinearLayoutManager(view.context)
        connectionListView.adapter = adapter

        // handling FAB button click
        val addNewConnectionBtn = view.findViewById<FloatingActionButton>(R.id.addNewConnectionBtn)
        addNewConnectionBtn.setOnClickListener {
            showDialog(view.context)
        }

    }

    private fun showDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.new_connection, null)

        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Add new FTP connection")
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)

        val dialog = builder.create()

        dialog.setOnShowListener {
            val addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            addButton.setOnClickListener {

                // get inputs
                val host = dialogView.findViewById<EditText>(R.id.new_host).text.toString()
                val port = dialogView.findViewById<EditText>(R.id.new_port).text.toString().toIntOrNull() ?: 21
                val username = dialogView
                    .findViewById<EditText>(R.id.new_username)
                    .text.toString().ifEmpty { "anonymous" }
                val password = dialogView.findViewById<EditText>(R.id.new_password).text.toString()

                // validate input
                if (host.isEmpty() ) {
                    Toast.makeText(context, "Host cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    val newConnection = ConnectionModel(host = host, port = port, username = username, password = password)
                    lateinit var updatedConnection: ConnectionModel
                    CoroutineScope(Dispatchers.IO).launch {
                        val newId = dao.insertConnection(newConnection)
                        updatedConnection = newConnection.copy(id = newId.toInt())
                        withContext(Dispatchers.Main) {
                            adapter.addConnection(updatedConnection)
                        }
                    }
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    override fun onItemClick(itemView: View) {
        val collectionFragment = ConnectionFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, collectionFragment)
            .addToBackStack(null)
            .commit()
    }

}