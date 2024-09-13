package network.ermis.core.internal

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPEALIAS,
)
@RequiresOptIn(
    message = "This is internal API for the Ermis Chat libraries. Do not depend on this API in your own client code.",
    level = RequiresOptIn.Level.ERROR,
)
public annotation class InternalErmisChatApi
