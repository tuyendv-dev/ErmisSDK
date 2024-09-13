
package network.ermis.sample.application.debug

import android.app.Application
import android.os.Build
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import network.ermis.client.di.networkFlipper

object ApplicationConfigurator {

    const val HUAWEI_APP_ID = "104598359"
    const val XIAOMI_APP_ID = "2882303761520059340"
    const val XIAOMI_APP_KEY = "5792005994340"

    fun configureApp(application: Application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SoLoader.init(application, false)

            if (FlipperUtils.shouldEnableFlipper(application)) {
                AndroidFlipperClient.getInstance(application).apply {
                    addPlugin(InspectorFlipperPlugin(application, DescriptorMapping.withDefaults()))
                    addPlugin(DatabasesFlipperPlugin(application))
                    addPlugin(networkFlipper)
                }.start()
            }
        }
    }
}
