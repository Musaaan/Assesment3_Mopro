package com.musaan0129.assesment3.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musaan0129.assesment3.model.Desain
import com.musaan0129.assesment3.network.ApiStatus
import com.musaan0129.assesment3.network.DesainApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel: ViewModel() {
    var data = mutableStateOf(emptyList<Desain>())
        private set
    var status = MutableStateFlow(ApiStatus.LOADING)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set
    var deleteStatus = mutableStateOf<String?>(null)
        private set

    fun retrieveData(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = DesainApi.service.getDesain(token)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    suspend fun register(nama: String, email: String, password: String): String {
        var token = ""
        try {
            val result = DesainApi.service.register(
                nama,
                email,
                password
            )

            if (result.status == "success") {
                token = result.data ?: ""
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Failure: ${e.message}")
        }

        return token
    }

    fun saveData(token: String, judul: String, luas: String, harga: Double, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = DesainApi.service.postDesain(
                    token,
                    judul.toRequestBody("text/plain".toMediaTypeOrNull()),
                    luas.toRequestBody("text/plain".toMediaTypeOrNull()),
                    harga,
                    bitmap.toMultipartBody()
                )

                if (result.status == "success")
                    retrieveData(token)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun updateData(token: String, idDesain: Long, judul: String, luas: String, harga: Double, bitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imagePart = bitmap?.toMultipartBody()
                val result = DesainApi.service.updateDesain(
                    token,
                    idDesain,
                    "PUT".toRequestBody("text/plain".toMediaTypeOrNull()),
                    judul.toRequestBody("text/plain".toMediaTypeOrNull()),
                    luas.toRequestBody("text/plain".toMediaTypeOrNull()),
                    harga,
                    imagePart
                )

                if (result.status == "success")
                    retrieveData(token)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteData(token: String, desainId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = DesainApi.service.deleteDesain(token, desainId)
                if (result.status == "success") {
                    retrieveData(token)
                } else {
                    deleteStatus.value = result.message ?: "Gagal menghapus data"
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "Delete failure: ${e.message}")
                deleteStatus.value = "Terjadi kesalahan: ${e.message}"
            }
        }
    }

    fun clearDeleteStatus() {
        deleteStatus.value = null
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody)
    }

    fun clearMessage() {
        errorMessage.value = null
    }
}