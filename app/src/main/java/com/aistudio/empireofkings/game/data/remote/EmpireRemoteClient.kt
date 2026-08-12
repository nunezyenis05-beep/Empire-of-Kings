package com.aistudio.empireofkings.game.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/** Connection states exposed to the local-first UI. */
enum class ServerConnectionStatus {
    CHECKING,
    ONLINE,
    OFFLINE
}

/**
 * Small, safe bridge to the public Empire of Kings server.
 *
 * Room remains the source of truth for the offline game. This client only checks
 * the backend and is deliberately free of secrets or server API keys.
 */
data class RemoteAuthResult(
    val successful: Boolean,
    val token: String?
)

class EmpireRemoteClient(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .callTimeout(15, TimeUnit.SECONDS)
        .build()
) {
    suspend fun checkHealth(): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url("${BASE_URL}health")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty().replace(" ", "")
                response.isSuccessful && body.contains("\"ok\":true")
            }
        }.getOrDefault(false)
    }

    suspend fun authenticate(username: String, password: String, register: Boolean): RemoteAuthResult =
        withContext(Dispatchers.IO) {
            runCatching {
                val payload = JSONObject()
                    .put("username", username.trim())
                    .put("password", password)
                    .toString()
                val request = Request.Builder()
                    .url("${BASE_URL}api/auth/${if (register) "register" else "login"}")
                    .post(payload.toRequestBody(JSON_MEDIA_TYPE))
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    val body = response.body?.string().orEmpty()
                    val token = runCatching { JSONObject(body).optString("token").ifBlank { null } }.getOrNull()
                    RemoteAuthResult(response.isSuccessful, token)
                }
            }.getOrElse { RemoteAuthResult(false, null) }
        }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        const val BASE_URL = "https://empire-of-kings-server.onrender.com/"
    }
}
