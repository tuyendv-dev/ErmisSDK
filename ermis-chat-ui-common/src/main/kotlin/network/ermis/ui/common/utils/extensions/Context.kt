package network.ermis.ui.common.utils.extensions

import android.content.Context

/**
 * @param permission The permission we want to check if it was requested before.
 *
 * @return If the permission was requested before or not.
 */
public fun Context.wasPermissionRequested(permission: String): Boolean {
    return getSharedPreferences(PERMISSIONS_PREFS, Context.MODE_PRIVATE).getBoolean(permission, false)
}

/**
 * Saves to shared prefs that a permission has been requested.
 *
 * @param permission The permission in question.
 */
public fun Context.onPermissionRequested(permission: String) {
    return getSharedPreferences(PERMISSIONS_PREFS, Context.MODE_PRIVATE).edit().putBoolean(permission, true).apply()
}

private const val PERMISSIONS_PREFS = "stream_permissions"
