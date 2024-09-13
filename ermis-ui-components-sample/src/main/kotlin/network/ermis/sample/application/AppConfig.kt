package network.ermis.sample.application

import network.ermis.sample.data.ermis.SampleChain

object AppConfig {
    private val allChain: List<SampleChain> = listOf(
        SampleChain(chainId = 1, name = "Ethereum", image = "", currency = "ETH"),
        SampleChain(chainId = 42161, name = "Arbitrum One", image = "", currency = "ETH"),
        SampleChain(chainId = 137, name = "Polygon", image = "", currency = "MATIC"),
        SampleChain(chainId = 43114, name = "Avalanche C-Chain", image = "", currency = "AVAX"),
        SampleChain(chainId = 56, name = "BNB Smart Chain", image = "", currency = "BNB"),
        SampleChain(chainId = 10, name = "OP", image = "", currency = "ETH"),
        SampleChain(chainId = 100, name = "Gnosis", image = "", currency = "XDAI"),
        SampleChain(chainId = 324, name = "zkSync", image = "", currency = "ETH"),
        SampleChain(chainId = 7777777, name = "Zora", image = "", currency = "ETH"),
        SampleChain(chainId = 8453, name = "Base", image = "", currency = "ETH"),
        SampleChain(chainId = 42220, name = "Celo", image = "", currency = "CELO"),
        SampleChain(chainId = 1313161554, name = "Aurora", image = "", currency = "ETH"),
    )

    fun getChainById(chainId: Int): SampleChain? {
        val chainFilter = allChain.filter { it.chainId == chainId }
        return if (chainFilter.isEmpty()) {
            null
        } else {
            chainFilter.first()
        }
    }

    fun getChainByName(chainName: String): SampleChain? {
        val chainFilter = allChain.filter { it.name == chainName }
        return if (chainFilter.isEmpty()) {
            null
        } else {
            chainFilter.first()
        }
    }

    fun getListChainByIds(ids: List<Int>) : List<SampleChain> {
        val chains = ArrayList<SampleChain> ()
        ids.forEach { id ->
            allChain.forEach { chain ->
                if (chain.chainId == id) {
                    chains.add(chain)
                }
            }
        }
        return chains
    }

}
