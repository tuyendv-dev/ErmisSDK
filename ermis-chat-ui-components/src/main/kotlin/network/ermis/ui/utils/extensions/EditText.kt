
package network.ermis.ui.utils.extensions

import android.content.Context
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

public fun EditText.showKeyboardIfFocused() {
    if (isFocused) {
        post {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}

public fun EditText.focusAndShowKeyboard() {
    requestFocus()

    if (hasWindowFocus()) {
        showKeyboardIfFocused()
    } else {
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showKeyboardIfFocused()
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            },
        )
    }
}
