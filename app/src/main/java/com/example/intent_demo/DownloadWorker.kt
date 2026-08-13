package com.example.intentdemo

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class DownloadWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("DownloadWorker", "Starting background download of HTML file...")

        val urlString = "https://www.google.com" // Target URL to download

        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val content = inputStream.bufferedReader().use { it.readText() }

                // Save to a file in internal storage
                val file = File(applicationContext.filesDir, "downloaded_page.html")
                file.writeText(content)

                Log.d("DownloadWorker", "Download complete! File saved to: ${file.absolutePath}")
                Result.success()
            } else {
                Log.e("DownloadWorker", "Failed to download. Response code: ${connection.responseCode}")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DownloadWorker", "Error during download", e)
            Result.retry()
        }
    }
}
