package network.ermis

object Configuration {
    const val compileSdk = 34
    const val targetSdk = 34
    const val sampleTargetSdk = 34
    const val minSdk = 23
    const val majorVersion = 1
    const val minorVersion = 0
    const val patchVersion = 0
    const val versionName = "$majorVersion.$minorVersion.$patchVersion"
    const val snapshotVersionName = "$majorVersion.$minorVersion.${patchVersion + 1}-SNAPSHOT"
    const val artifactGroup = "network.ermis"
}
