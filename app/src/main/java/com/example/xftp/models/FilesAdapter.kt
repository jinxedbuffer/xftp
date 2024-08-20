package com.example.xftp.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.xftp.R
import org.apache.commons.net.ftp.FTPFile

class FilesAdapter(private val filesList: List<FTPFile>) : RecyclerView.Adapter<FilesAdapter.FilesViewHolder>() {
    inner class FilesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val filenameTextView = view.findViewById<TextView>(R.id.filename)
        val fileSizeTextView = view.findViewById<TextView>(R.id.filesize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_list_item, parent, false)
        return FilesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val file = filesList[position]
        holder.filenameTextView.text = file.name
        holder.fileSizeTextView.text = file.size.toString() + "B"
    }

}