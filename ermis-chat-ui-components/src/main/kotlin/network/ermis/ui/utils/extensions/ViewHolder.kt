
package io.getstream.chat.android.ui.common.extensions.internal

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

internal val RecyclerView.ViewHolder.context: Context
    get() = itemView.context
