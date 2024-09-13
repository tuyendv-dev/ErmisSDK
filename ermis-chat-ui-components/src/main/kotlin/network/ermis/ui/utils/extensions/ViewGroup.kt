
package network.ermis.ui.utils.extensions

import android.view.LayoutInflater
import android.view.ViewGroup

public inline val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(context)

internal val ViewGroup.streamThemeInflater: LayoutInflater
    get() = LayoutInflater.from(context.createStreamThemeWrapper())
