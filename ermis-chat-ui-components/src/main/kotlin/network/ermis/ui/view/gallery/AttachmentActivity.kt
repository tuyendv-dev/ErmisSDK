
package network.ermis.ui.view.gallery

import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import network.ermis.core.models.AttachmentType
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.ActivityAttachmentBinding
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.load
import io.getstream.log.taggedLogger

/**
 * An Activity showing attachments such as websites, youtube and giphy.
 */
public class AttachmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttachmentBinding

    private val logger by taggedLogger("Chat:AttachmentActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAttachmentBinding.inflate(streamThemeInflater)
        setContentView(binding.root)

        configUIs()

        val type = intent.getStringExtra("type")
        val url = intent.getStringExtra("url")
        if (type.isNullOrEmpty() || url.isNullOrEmpty()) {
            logger.e { "This file can't be displayed. TYPE or URL is missing." }
            Toast.makeText(this, getString(R.string.ermis_ui_message_list_attachment_display_error), Toast.LENGTH_SHORT).show()
            return
        }
        showAttachment(type, url)
    }

    private fun configUIs() {
        binding.ivImage.isVisible = false
        binding.webView.isVisible = false

        // WebView
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                pluginState = WebSettings.PluginState.ON
            }
            webViewClient = AppWebViewClients()
        }
    }

    private fun showAttachment(type: String, url: String) {
        when (type) {
            AttachmentType.GIPHY -> showGiphy(url)
            else -> loadUrlToWeb(url)
        }
    }

    /**
     * Show web view with url
     *
     * @param url web url
     */
    private fun loadUrlToWeb(url: String?) {
        binding.ivImage.isVisible = false
        binding.webView.isVisible = true
        binding.progressBar.isVisible = true

        url?.let {
            binding.webView.loadUrl(it)
        }
    }

    /**
     * Play giphy with url
     *
     * @param url giphy url
     */
    private fun showGiphy(url: String?) {
        if (url == null) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
            return
        }
        binding.ivImage.isVisible = true
        binding.webView.isVisible = false

        binding.ivImage.load(
            data = url,
            placeholderResId = R.drawable.placeholder,
        )
    }

    private inner class AppWebViewClients : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            binding.progressBar.isVisible = false
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError?) {
            logger.e { "The load failed due to an unknown error: $error" }
            if (error == null) {
                return
            }
            Toast.makeText(this@AttachmentActivity, error.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
