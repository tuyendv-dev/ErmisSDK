
package network.ermis.ui.font

public class ChatStyle {
    public var defaultTextStyle: TextStyle? = null
    public fun hasDefaultFont(): Boolean = defaultTextStyle?.hasFont() == true
}
