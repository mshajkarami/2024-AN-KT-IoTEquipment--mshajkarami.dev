package com.irisaco.iotequipment.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import okhttp3.internal.http2.Http2Reader.Handler
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File
import java.io.FileOutputStream

object FileDownloader {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.16:8081/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val api = retrofit.create(FileDownloadApi::class.java)

    suspend fun downloadFile(
        context: Context,
        fileUrl: String,
        fileName: String
    ): File? {
        val file = File(context.getExternalFilesDir(null), fileName)
        if (file.exists()) return file

        return try {
            val response = api.downloadFile(fileUrl)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val file = File(context.getExternalFilesDir(null), fileName)
                    val inputStream = body.byteStream()
                    val outputStream = FileOutputStream(file)

                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    file
                }
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun openFile(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, getMimeType(file.name))
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "اپلیکیشنی برای باز کردن این فایل نصب نیست", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun getMimeType(fileName: String): String {
        return when {
            fileName.endsWith(".pdf") -> "application/pdf"
            fileName.endsWith(".doc") || fileName.endsWith(".docx") -> "application/msword"
            fileName.endsWith(".xls") || fileName.endsWith(".xlsx") -> "application/vnd.ms-excel"
            fileName.endsWith(".txt") -> "text/plain"
            else -> "*/*"
        }
    }


    interface FileDownloadApi {
        @GET
        @Streaming
        suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>
    }
}
