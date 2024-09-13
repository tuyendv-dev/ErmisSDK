package network.ermis.sample.application

import android.app.Application
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.web3.modal.client.Modal
import com.walletconnect.web3.modal.client.Web3Modal
import com.walletconnect.web3.modal.presets.Web3ModalChainsPresets
import network.ermis.sample.application.debug.ApplicationConfigurator
import network.ermis.sample.application.debug.DebugMetricsHelper
import network.ermis.client.ConfigBuilder
import network.ermis.client.utils.internal.toggle.ToggleService
import network.ermis.core.internal.InternalErmisChatApi
import network.ermis.sample.data.user.SampleUser
import network.ermis.sample.data.user.UserRepository
import io.getstream.log.taggedLogger

class App : Application() {

    // done for simplicity, a DI framework should be used in the real app
    val chatInitializer = ChatInitializer(context = this, autoTranslationEnabled = true)
    val userRepository = UserRepository(context = this)
    private val logger by taggedLogger("Chat:Application")

    override fun onCreate() {
        super.onCreate()
        initializeToggleService()
        chatInitializer.init(getApiKey())
        instance = this
        DebugMetricsHelper.init()
        ApplicationConfigurator.configureApp(this)
        val connectionType = ConnectionType.AUTOMATIC // ConnectionType.MANUAL
        val projectId = "9aed225a8d150bbf27be18f90dd3e387" // Get Project ID at https://cloud.walletconnect.com/
        val relayUrl = "relay.walletconnect.com"
        val serverUrl = "wss://$relayUrl?projectId=${projectId}"
        val appMetaData = Core.Model.AppMetaData(
            name = "Ermis chat",
            description = "Ermis chat demo app.",
            url = "https://ermis.network",
            icons = listOf("https://avatars.githubusercontent.com/u/37784886"),
            redirect = "kotlin-web3modal://request",
        )
        CoreClient.initialize(
            relayServerUrl = serverUrl,
            connectionType = connectionType,
            application = this,
            metaData = appMetaData
        ) {
            logger.e { "Web3Modal CoreClient.initialize error = ${it.throwable}" }
        }
        // val recommendedWalletsIds = listOf<String>(
        //     "1ae92b26df02f0abca6304df07debccd18262fdf5fe82daa81593582dac9a369",
        //     "4622a2b2d6af1c9844944291e5e7351a6aa24cd7b23099efac1b2fd875da31a0",
        //     "e7c4d26541a7fd84dbdfa9922d3ad21e936e13a7a0e44385d44f006139e44d3b",
        //     "c57ca95b47569778a828d19178114f4db188b89b763c899ba0be274e97267d96",
        //     "fd20dc426fb37566d803205b19bbc1d4096b248ac04548e3cfb6b3a38bd033aa"
        // )
        val initParams = Modal.Params.Init(core = CoreClient, coinbaseEnabled = true)//, recommendedWalletsIds = recommendedWalletsIds)
        Web3Modal.initialize(
            init = initParams,
            onSuccess = {
                // Callback will be called if initialization is successful
                logger.e { "Web3Modal.initialize onSuccess" }
            },
            onError = { error ->
                // Error will be thrown if there's an issue during initialization
                logger.e { "Web3Modal.initialize error= $error" }
            }
        )
        Web3Modal.setChains(Web3ModalChainsPresets.ethChains.values.toList())
    }

    private fun getApiKey(): String {
        val user = userRepository.getUser()
        return if (user != SampleUser.None) {
            user.apiKey
        } else {
            ConfigBuilder.API_KEY
        }
    }

    @OptIn(InternalErmisChatApi::class)
    private fun initializeToggleService() {
        ToggleService.init(
            applicationContext,
            emptyMap(),
        )
    }

    companion object {
        lateinit var instance: App
            private set
    }
}
