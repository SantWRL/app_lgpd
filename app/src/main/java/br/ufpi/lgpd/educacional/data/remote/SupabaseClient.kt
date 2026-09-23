package br.ufpi.lgpd.educacional.data.remote

import br.ufpi.lgpd.educacional.util.NetworkConstants
import br.ufpi.lgpd.educacional.util.SupabaseConfig
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Cliente REST minimalista para o Supabase (PostgREST), sem dependências extras.
 * Usa HttpURLConnection + org.json, já incluídos no Android.
 */
object SupabaseClient {

    private const val JSON = "application/json"

    class SupabaseException(message: String, val code: Int) : Exception(message)

    private fun requireConfig(): Pair<String, String> {
        check(SupabaseConfig.isEnabled) { "Supabase não configurado" }
        return SupabaseConfig.URL.trimEnd('/') to SupabaseConfig.ANON_KEY
    }

    /**
     * GET em uma tabela. Retorna o JSONArray cru de linhas.
     * Ex.: get("leaderboard", "device_id,display_name,total_points", "total_points.desc")
     */
    fun get(
        table: String,
        columns: String = "*",
        order: String? = null,
        limit: Int? = null,
        filters: Map<String, String> = emptyMap()
    ): JSONArray {
        val (baseUrl, key) = requireConfig()
        val params = buildList {
            add("select=$columns")
            filters.forEach { (k, v) -> add("$k=$v") }
            order?.let { add("order=$it") }
            limit?.let { add("limit=$it") }
        }.joinToString("&")

        val url = URL("$baseUrl/rest/v1/$table?$params")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = NetworkConstants.SCRAPER_TIMEOUT_MS
            readTimeout = NetworkConstants.SCRAPER_TIMEOUT_MS
            setRequestProperty("apikey", key)
            setRequestProperty("Authorization", "Bearer $key")
            setRequestProperty("Accept", JSON)
        }

        try {
            return readBody(conn)
        } finally {
            conn.disconnect()
        }
    }

    /**
     * Chamada RPC (PostgREST /rpc/<fn>). Retorna o corpo cru como String
     * (JSONObject/JSONArray conforme a function).
     */
    fun rpc(function: String, args: JSONObject): String {
        val (baseUrl, key) = requireConfig()
        val url = URL("$baseUrl/rest/v1/rpc/$function")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = NetworkConstants.SCRAPER_TIMEOUT_MS
            readTimeout = NetworkConstants.SCRAPER_TIMEOUT_MS
            doOutput = true
            setRequestProperty("apikey", key)
            setRequestProperty("Authorization", "Bearer $key")
            setRequestProperty("Content-Type", JSON)
            setRequestProperty("Accept", JSON)
        }

        try {
            conn.outputStream.use { it.write(args.toString().toByteArray()) }
            val code = conn.responseCode
            val body = if (code in 200..299) {
                conn.inputStream?.bufferedReader()?.readText().orEmpty()
            } else {
                conn.errorStream?.bufferedReader()?.readText().orEmpty()
            }
            if (code !in 200..299) {
                throw SupabaseException("Supabase rpc $function falhou ($code): $body", code)
            }
            return body
        } finally {
            conn.disconnect()
        }
    }

    /**
     * Upsert em uma tabela (requer on_conflict no PostgREST).
     * Usa a anon key; as policies de RLS definem o que pode ser escrito.
     */
    fun upsert(table: String, jsonBody: String, onConflict: String) {
        val (baseUrl, key) = requireConfig()
        val url = URL("$baseUrl/rest/v1/$table?on_conflict=$onConflict")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = NetworkConstants.SCRAPER_TIMEOUT_MS
            readTimeout = NetworkConstants.SCRAPER_TIMEOUT_MS
            doOutput = true
            setRequestProperty("apikey", key)
            setRequestProperty("Authorization", "Bearer $key")
            setRequestProperty("Content-Type", JSON)
            setRequestProperty("Prefer", "resolution=merge-duplicates")
        }

        conn.outputStream.use { it.write(jsonBody.toByteArray()) }

        val code = conn.responseCode
        if (code !in 200..299) {
            val err = conn.errorStream?.bufferedReader()?.readText().orEmpty()
            throw SupabaseException("Supabase upsert falhou ($code): $err", code)
        }
        conn.disconnect()
    }

    private fun readBody(conn: HttpURLConnection): JSONArray {
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val body = stream?.bufferedReader()?.readText().orEmpty()

        if (code !in 200..299) {
            throw SupabaseException("Supabase GET falhou ($code): $body", code)
        }
        return JSONArray(body)
    }
}
