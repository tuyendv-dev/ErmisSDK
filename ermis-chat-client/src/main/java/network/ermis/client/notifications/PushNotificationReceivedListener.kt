package network.ermis.client.notifications

public fun interface PushNotificationReceivedListener {
    public fun onPushNotificationReceived(channelType: String, channelId: String)
}
