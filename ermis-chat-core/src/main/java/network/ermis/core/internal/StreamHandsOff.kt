package network.ermis.core.internal

/**
 * Indicates that the annotated code should not be modified without consulting  the team.
 * @param reason
 * For example:
 *
 * <pre>
 * &#064;StreamHandsOff(reason = "If you move a card, the house is collapsed")
 * public class HouseOfCards { }
</pre> *
 */
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
public annotation class StreamHandsOff(val reason: String)
