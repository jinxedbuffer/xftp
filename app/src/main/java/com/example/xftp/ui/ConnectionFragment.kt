package com.example.xftp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xftp.R
import com.example.xftp.models.ConnectionModel
import com.example.xftp.models.FilesAdapter
import com.example.xftp.models.xFTPClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_connection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val xftpClient: xFTPClient = xFTPClient.getInstance()
        val recyclerView = view.findViewById<RecyclerView>(R.id.files_list)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)


        CoroutineScope(Dispatchers.IO).launch {

            // loading screen visible
            progressBar.visibility = ProgressBar.VISIBLE
            recyclerView.visibility = RecyclerView.INVISIBLE
            progressBar.isIndeterminate = true

            val files = xftpClient.getAllFiles(view.context, "/")
            withContext(Dispatchers.Main) {
                // loading screen invisible
                recyclerView.visibility = RecyclerView.VISIBLE
                progressBar.visibility = ProgressBar.INVISIBLE
                progressBar.isIndeterminate = false

                val adapter = FilesAdapter(files)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(view.context)
            }
        }

    }
}