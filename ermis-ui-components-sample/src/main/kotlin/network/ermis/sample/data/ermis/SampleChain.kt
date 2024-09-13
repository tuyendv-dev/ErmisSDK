package network.ermis.sample.data.ermis

data class SampleChain(
    val chainId: Int,
    val name: String,
    val image: String,
    val currency: String = "",
)
