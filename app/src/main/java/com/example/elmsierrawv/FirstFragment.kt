package com.example.elmsierrawv

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.elmsierrawv.databinding.FragmentFirstBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var webview: WebView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    )
    {
        Log.e("error:", error.toString());
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        webview = binding.webview

        webview.settings.javaScriptEnabled = true
        webview.webViewClient = object: WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                // Handle the error
                //Toast.makeText(applicationContext, "Error: ${error?.description}", Toast.LENGTH_SHORT).show()
            }
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                // Here, you can handle the navigation event however you want
                // For example, you can prevent the WebView from navigating to the requested URL
                // by returning true from this method

                // To cancel the navigation event:
                // return true;
                if (request != null) {
                    val intent = Intent(Intent.ACTION_VIEW, request?.url)
                    startActivity(intent)
                }
                // To allow the navigation event to continue:
                return true;
            }
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return try {
                    val url = URL(request.url.toString())
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    connection.setDoInput(true)
                    connection.setRequestMethod("GET")

                    // Add any custom headers to the request
                    // connection.setRequestProperty("My-Header", "My-Value")

                    // Send the request and get the response
                    connection.connect()
                    val inputStream: InputStream = connection.getInputStream()

                    // Modify the response headers if needed
                    val responseHeaders: MutableMap<String, String> = HashMap()
                    responseHeaders["Access-Control-Allow-Origin"] = "*"

                    // Return the modified response to the WebView
                    WebResourceResponse("text/html", "UTF-8", 200, "OK", responseHeaders, inputStream)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }
        }

        val applicationContext: Context = requireContext().applicationContext
        val assetManager: AssetManager = applicationContext.assets

        try {
            val inputStream: InputStream = assetManager.open("index.html")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
                stringBuilder.append("\n")
            }

            webview.loadDataWithBaseURL(null, stringBuilder.toString(), "text/html", "UTF-8", null)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}