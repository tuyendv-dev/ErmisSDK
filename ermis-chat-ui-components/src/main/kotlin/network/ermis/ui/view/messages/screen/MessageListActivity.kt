
package network.ermis.ui.view.messages.screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.FragmentContainerBinding

/**
 * An Activity representing a self-contained chat screen. This Activity is simply
 * a thin wrapper around [MessageListFragment].
 */
public open class MessageListActivity : AppCompatActivity() {
    private lateinit var binding: FragmentContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val cid = requireNotNull(intent.getStringExtra(EXTRA_CID)) {
                "Channel cid must not be null"
            }
            val messageId = intent.getStringExtra(EXTRA_MESSAGE_ID)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, createMessageListFragment(cid, messageId))
                .commit()
        }
    }

    /**
     * Creates an instance of [MessageListFragment]. Override this method if you want to create an
     * instance of [MessageListFragment] with custom arguments or if you want to create a subclass
     * of [MessageListFragment].
     */
    protected open fun createMessageListFragment(cid: String, messageId: String?): MessageListFragment {
        return MessageListFragment.newInstance(cid) {
            setFragment(MessageListFragment())
            showHeader(true)
            messageId(messageId)
        }
    }

    public companion object {
        private const val EXTRA_CID: String = "extra_cid"
        private const val EXTRA_MESSAGE_ID: String = "extra_message_id"

        /**
         * Creates an Intent to start the [MessageListActivity] or its subclass.
         *
         * @param context The context that will be used in the intent.
         * @param cid The full channel id, i.e. "messaging:123".
         * @param messageId The ID of the selected message.
         * @param activityClass The Activity class that will be used in the intent.
         */
        @JvmStatic
        @JvmOverloads
        public fun createIntent(
            context: Context,
            cid: String,
            messageId: String? = null,
            activityClass: Class<out MessageListActivity> = MessageListActivity::class.java,
        ): Intent {
            return Intent(context, activityClass).apply {
                putExtra(EXTRA_CID, cid)
                putExtra(EXTRA_MESSAGE_ID, messageId)
            }
        }
    }
}
