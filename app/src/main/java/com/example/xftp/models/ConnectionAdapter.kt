package com.example.xftp.models

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.xftp.MainActivity
import com.example.xftp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionAdapter(
    private val connectionList: MutableList<ConnectionModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ConnectionAdapter.ConnectionViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(itemView: View)
    }

    inner class ConnectionViewHolder(view: View): RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                val myClient: xFTPClient = xFTPClient.getInstance()
                myClient.connect(view.context, connectionList[adapterPosition])
            }
            view.setOnLongClickListener {
                showDeleteDialog(view, this@ConnectionAdapter, connectionList, adapterPosition)
                true
            }
        }

        val usernameTextView: TextView = view.findViewById(R.id.connection_list_username)
        val hostTextView: TextView = view.findViewById(R.id.connection_list_host)
        val portTextView: TextView = view.findViewById(R.id.connection_list_port)

        fun bind(itemView: View, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClick(itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.connection_list_item, parent, false)
        return ConnectionViewHolder(view)
    }

    override fun getItemCount(): Int = connectionList.size

    override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
        val item = connectionList[position]
        holder.hostTextView.text = item.host
        holder.usernameTextView.text = item.username
        holder.portTextView.text = item.port.toString()
        holder.bind(holder.itemView, listener)
    }

    fun addConnection(connection: ConnectionModel) {
        connectionList.add(0, connection)
        notifyItemInserted(0)
    }
}

private fun showDeleteDialog(view: View, adapter: ConnectionAdapter, connectionList: MutableList<ConnectionModel>, position: Int) {
    val builder = AlertDialog.Builder(view.context)
        .setMessage("Do you want to delete this connection?")
    builder.setPositiveButton("Yes") { _, _ ->
        if (position != RecyclerView.NO_POSITION) {
            val item = connectionList[position]
            connectionList.removeAt(position)
            CoroutineScope(Dispatchers.IO).launch {
                val db = ConnectionDatabase.getDatabase(view.context)
                val dao = db.connectionDao()
                dao.deleteConnection(item)
                withContext(Dispatchers.Main) {
                    Toast.makeText(view.context, "Connection deleted", Toast.LENGTH_SHORT).show()
                    adapter.notifyItemRemoved(position)
                }
            }
        }
    }
    builder.setNegativeButton("No", null)
    val dialog = builder.create()
    dialog.show()
}