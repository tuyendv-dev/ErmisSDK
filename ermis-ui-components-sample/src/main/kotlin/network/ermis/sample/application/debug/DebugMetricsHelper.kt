
package network.ermis.sample.application.debug

import android.os.StrictMode

object DebugMetricsHelper {
    internal fun init() {
        StrictMode.ThreadPolicy.Builder().detectAll()
            .permitDiskReads()
            .permitDiskWrites()
            .penaltyLog()
            .build()
            .apply {
                StrictMode.setThreadPolicy(this)
            }

        StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .build()
            .apply {
                StrictMode.setVmPolicy(this)
            }
    }
}
