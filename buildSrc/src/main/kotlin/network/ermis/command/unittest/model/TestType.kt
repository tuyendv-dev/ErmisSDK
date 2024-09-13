package network.ermis.command.unittest.model

enum class TestType(val testCommand: String) {
    JAVA_LIBRARY_TEST("test"),
    ANDROID_LIBRARY_TEST("testDebugUnitTest"),
}
