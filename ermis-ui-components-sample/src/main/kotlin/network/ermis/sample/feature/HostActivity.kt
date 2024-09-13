package network.ermis.sample.feature

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.walletconnect.web3.modal.client.Web3Modal
import network.ermis.chat.ui.sample.R
import io.getstream.log.taggedLogger

const val EXTRA_CHANNEL_ID = "extra_channel_id"
const val EXTRA_CHANNEL_TYPE = "extra_channel_type"
const val EXTRA_MESSAGE_ID = "extra_message_id"
const val EXTRA_PARENT_MESSAGE_ID = "extra_parent_message_id"

class HostActivity : AppCompatActivity(R.layout.activity_main) {

    private val logger by taggedLogger("Chat:HostActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Web3Modal.register(this)
        if (savedInstanceState != null && lastNonConfigurationInstance == null) {
            // the application process was killed by the OS
            startActivity(packageManager.getLaunchIntentForPackage(packageName))
            finishAffinity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Web3Modal.unregister()
    }

    companion object {

        fun createLaunchIntent(
            context: Context,
            messageId: String,
            parentMessageId: String?,
            channelType: String,
            channelId: String,
        ) = Intent(context, HostActivity::class.java).apply {
            putExtra(EXTRA_CHANNEL_ID, channelId)
            putExtra(EXTRA_CHANNEL_TYPE, channelType)
            putExtra(EXTRA_MESSAGE_ID, messageId)
            putExtra(EXTRA_PARENT_MESSAGE_ID, parentMessageId)
        }
    }
}
