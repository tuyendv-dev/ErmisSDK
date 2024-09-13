package network.ermis.ui.common.state.messages

/**
 * The input for a new message
 */
public data class MessageInput(
    val text: String = "",
    val source: Source = Source.Default,
) {

    public sealed class Source {
        /**
         * The initial value, used when the source is not specified
         */
        public data object Default : Source()

        /**
         * The message was created by the user
         */
        public data object External : Source()

        public sealed class Internal : Source()

        /**
         * The message was created by the user
         */
        public data object Edit : Internal()

        /**
         * The message was created internally by the SDK
         */
        public data object CommandSelected : Internal()

        /**
         * The message was created internally by the SDK
         */
        public data object MentionSelected : Internal()
    }
}
