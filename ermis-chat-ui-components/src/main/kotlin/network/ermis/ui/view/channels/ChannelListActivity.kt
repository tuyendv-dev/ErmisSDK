
package network.ermis.ui.view.channels

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.FragmentContainerBinding

/**
 * An Activity representing a self-contained channel list screen. This Activity
 * is simply a thin wrapper around [ChannelListFragment].
 */
public open class ChannelListActivity : AppCompatActivity() {
    private lateinit var binding: FragmentContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, createChannelListFragment())
                .commit()
        }
    }

    /**
     * Creates an instance of [ChannelListFragment]. Override this method if you want to create an
     * instance of [ChannelListFragment] with custom arguments or if you want to create a subclass
     * of [ChannelListFragment].
     */
    protected open fun createChannelListFragment(): ChannelListFragment {
        return ChannelListFragment.newInstance {
            setFragment(ChannelListFragment())
            customTheme(R.style.ermisUiTheme_ChannelListScreen)
            showSearch(true)
            showHeader(true)
            headerTitle(getString(R.string.ermis_ui_channel_list_header_connected))
        }
    }

    public companion object {
        /**
         * Creates an Intent to start the [ChannelListActivity] or its subclass.
         *
         * @param context The context that will be used in the intent.
         * @param activityClass The Activity class that will be used in the intent.
         */
        @JvmStatic
        @JvmOverloads
        public fun createIntent(
            context: Context,
            activityClass: Class<out ChannelListActivity> = ChannelListActivity::class.java,
        ): Intent {
            return Intent(context, activityClass)
        }
    }
}
