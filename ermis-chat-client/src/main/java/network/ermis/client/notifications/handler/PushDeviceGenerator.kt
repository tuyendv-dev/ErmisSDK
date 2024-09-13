package network.ermis.client.notifications.handler

import android.content.Context
import network.ermis.core.models.Device

/**
 * Generator responsible for providing information needed to register the push notifications provider
 */
public interface PushDeviceGenerator {
    /**
     * Checks if push notification provider is valid for this device
     */
    public fun isValidForThisDevice(context: Context): Boolean

    /**
     * Called when this [PushDeviceGenerator] has been selected to be used.
     */
    public fun onPushDeviceGeneratorSelected()

    /**
     * Asynchronously generates a [Device] and calls [onDeviceGenerated] callback once it's ready
     */
    public fun asyncGenerateDevice(onDeviceGenerated: (device: Device) -> Unit)
}
