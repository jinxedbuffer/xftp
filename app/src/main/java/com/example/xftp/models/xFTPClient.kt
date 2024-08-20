package com.example.xftp.models

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply

class xFTPClient : FTPClient() {

    fun connect(context: Context, connection: ConnectionModel) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                super.connect(connection.host)
                val replyCode = super.getReplyCode()
                if (!FTPReply.isPositiveCompletion(replyCode)) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show()
                    }
                    super.disconnect()
                } else {
                    super.login(connection.username, connection.password)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Connection successful", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun getAllFiles(context: Context, path: String = "/"): List<FTPFile> {
        val allFiles = mutableListOf<FTPFile>()
        withContext(Dispatchers.IO) {
            super.changeWorkingDirectory(path)
            val files = super.listFiles()
            allFiles.addAll(files)
        }
        return allFiles.toList()
    }

    companion object {
        private var INSTANCE: xFTPClient? = null
        fun getInstance(): xFTPClient {
            if (INSTANCE == null) {
                INSTANCE = xFTPClient()
            }
            return INSTANCE!!
        }
    }
}