
package network.ermis.ui.navigation.destinations

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import network.ermis.ui.components.R
import network.ermis.ui.utils.extension.addSchemeToUrlIfNeeded

public class WebLinkDestination(context: Context, private val url: String) : ChatDestination(context) {

    override fun navigate() {
        try {
            val urlWithSchema = url.addSchemeToUrlIfNeeded()
            start(Intent(Intent.ACTION_VIEW, Uri.parse(urlWithSchema)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.ermis_ui_message_list_error_cannot_open_link, url),
                Toast.LENGTH_LONG,
            ).show()
        }
    }
}
