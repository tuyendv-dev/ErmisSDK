package network.ermis.sample.feature.userlogin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.coinbase.android.nativesdk.CoinbaseWalletSDK
import com.coinbase.android.nativesdk.message.request.Account
import com.coinbase.android.nativesdk.message.request.Web3JsonRPC
import com.coinbase.android.nativesdk.message.response.ActionResult
import com.walletconnect.web3.modal.client.Modal
import com.walletconnect.web3.modal.client.Web3Modal
import com.walletconnect.web3.modal.ui.openWeb3Modal
import network.ermis.state.utils.EventObserver
import network.ermis.chat.ui.sample.R
import network.ermis.sample.application.App
import network.ermis.sample.common.navigateSafely
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.FragmentUserLoginBinding
import io.getstream.log.taggedLogger

class UserLoginFragment : Fragment() {

    private val logger by taggedLogger("Chat:UserLoginFragment")
    private var _binding: FragmentUserLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserLoginViewModel by viewModels()
    private var andressWallet = ""
    private var mTypeConnector = Modal.ConnectorType.WALLET_CONNECT

    private val client by lazy {
        CoinbaseWalletSDK(
            appContext = requireContext(),
            domain = Uri.parse("https://ermischat.com"),
            openIntent = { intent -> launcher.launch(intent) }
        )
    }
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserLoginBinding.inflate(inflater, container, false)
        initWeb3Model()
        return binding.root
    }

    private fun initWeb3Model() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.data ?: return@registerForActivityResult
            client.handleResponse(uri)
        }
        Web3Modal.disconnect() { it ->
            logger.i { "Web3Modal.disconnect onError $it" }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        observeStateAndEvents()
        viewModel.init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViews() {
        Web3Modal.setDelegate(web3ModalModalDelegate)
        binding.loginWalletButton.setOnClickListener {
            // val chain = Modal.Model.Chain(
            //     chainName = "OP Mainnet",
            //     chainNamespace = "eip155",
            //     chainReference = "10",
            //     requiredMethods = listOf("personal_sign", "eth_signTypedData", "eth_sendTransaction"),
            //     optionalMethods = listOf("wallet_switchEthereumChain", "wallet_addEthereumChain"),
            //     events = listOf("chainChanged", "accountsChanged"),
            //     token = Modal.Model.Token(name = "Ether", symbol = "ETH", decimal = 18),
            //     chainImage = null,
            //     rpcUrl = "https://mainnet.optimism.io, blockExplorerUrl=https://explorer.optimism.io")
            //     Web3Modal.setChains(listOf(chain))
            findNavController().openWeb3Modal(shouldOpenChooseNetwork = true) {
                logger.e { "Web3Modal findNavController().openWeb3Modal onError= $it" }
            }
        }
        binding.btnGotoErmis.setOnClickListener {
            viewModel.userSelectPlasform(false)
        }
        binding.btnGotoSdk.setOnClickListener {
            viewModel.userSelectPlasform(true)
        }
    }

    private fun observeStateAndEvents() {
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is UserLoginViewModel.UiEvent.RedirectToChannels -> {
                        navigateSafely(R.id.action_userLoginFragment_to_homeFragment)
                    }
                    is UserLoginViewModel.UiEvent.RedirectToProjects -> {
                        navigateSafely(R.id.action_userLoginFragment_to_projectListFragment)
                    }
                    is UserLoginViewModel.UiEvent.Error -> {
                        binding.loadingProgressBar.isVisible = false
                        showToast(it.errorMessage ?: getString(R.string.backend_error_info))
                    }
                    is UserLoginViewModel.UiEvent.RedirectToLogin -> {
                        navigateSafely(R.id.action_userLoginFragment_to_customLoginFragment)
                    }
                    is UserLoginViewModel.UiEvent.ShowUiLogin -> {
                        binding.loginContainer.isVisible = true
                    }
                    is UserLoginViewModel.UiEvent.Loading -> binding.loadingProgressBar.isVisible = it.show
                    is UserLoginViewModel.UiEvent.ShowUiSelectPlatform -> binding.layoutSelectPlatform.isVisible = true
                    is UserLoginViewModel.UiEvent.WalletConnectSuccess -> signETHtypedDataWaless(it.challenge, it.address)
                }
            },
        )
    }

    private val web3ModalModalDelegate = object : Web3Modal.ModalDelegate {
        override fun onSessionApproved(approvedSession: Modal.Model.ApprovedSession) {
            // Triggered when receives the session approval from wallet
            when (approvedSession) {
                is Modal.Model.ApprovedSession.CoinbaseSession -> {
                    mTypeConnector = Modal.ConnectorType.COINBASE
                    logger.i { "Web3Modal.ModalDelegate onSessionApproved CoinbaseSession= ${approvedSession}" }
                }

                is Modal.Model.ApprovedSession.WalletConnectSession -> {
                    mTypeConnector = Modal.ConnectorType.WALLET_CONNECT
                    logger.i { "Web3Modal.ModalDelegate onSessionApproved WalletConnectSession= ${approvedSession}" }
                }
            }
            logger.i { "Web3Modal.ModalDelegate getAccount= ${Web3Modal.getAccount()}" }
            Web3Modal.getAccount()?.address?.let {
                andressWallet = it
                viewModel.connectWallet(it)
            }
            val chain = Web3Modal.getSelectedChain()
            val chainId: Int = try {
                chain?.chainReference?.toInt()!!
            } catch (e: Exception) {
                0
            }
            App.instance.userRepository.setChainId(chainId)
            logger.i { "Web3Modal.ModalDelegate chain= ${chain}" }
        }

        override fun onSessionRejected(rejectedSession: Modal.Model.RejectedSession) {
            // Triggered when receives the session rejection from wallet
            logger.i { "Web3Modal.ModalDelegate onSessionRejected rejectedSession= $rejectedSession" }
        }

        override fun onSessionUpdate(updatedSession: Modal.Model.UpdatedSession) {
            // Triggered when receives the session update from wallet
            logger.i { "Web3Modal.ModalDelegate onSessionUpdate updatedSession= $updatedSession" }
        }

        override fun onSessionExtend(session: Modal.Model.Session) {
            // Triggered when receives the session extend from wallet
            logger.i { "Web3Modal.ModalDelegate onSessionExtend session= $session" }
        }

        override fun onSessionEvent(sessionEvent: Modal.Model.SessionEvent) {
            // Triggered when the peer emits events that match the list of events agreed upon session settlement
            logger.i { "Web3Modal.ModalDelegate onSessionEvent sessionEvent= $sessionEvent" }
        }

        override fun onSessionDelete(deletedSession: Modal.Model.DeletedSession) {
            // Triggered when receives the session delete from wallet
            logger.i { "Web3Modal.ModalDelegate onSessionDelete deletedSession= $deletedSession" }
        }

        override fun onSessionRequestResponse(response: Modal.Model.SessionRequestResponse) {
            // Triggered when receives the session request response from wallet
            logger.i { "Web3Modal.ModalDelegate onSessionRequestResponse response= $response" }
            when (response.result) {
                is Modal.Model.JsonRpcResponse.JsonRpcResult -> {
                    (response.result as Modal.Model.JsonRpcResponse.JsonRpcResult).result.let {
                        viewModel.signinWallet(andressWallet, it)
                    }
                }

                is Modal.Model.JsonRpcResponse.JsonRpcError -> {
                }
            }
        }

        override fun onProposalExpired(proposal: Modal.Model.ExpiredProposal) {
            // Triggered when a proposal becomes expired
            logger.i { "Web3Modal.ModalDelegate onProposalExpired proposal= $proposal" }
        }

        override fun onRequestExpired(request: Modal.Model.ExpiredRequest) {
            // Triggered when a request becomes expired
            logger.i { "Web3Modal.ModalDelegate onRequestExpired request= $request" }
        }

        override fun onConnectionStateChange(state: Modal.Model.ConnectionState) {
            //Triggered whenever the connection state is changed
            logger.i { "Web3Modal.ModalDelegate onConnectionStateChange state= $state" }
        }

        override fun onError(error: Modal.Model.Error) {
            // Triggered whenever there is an issue inside the SDK
            logger.i { "Web3Modal.ModalDelegate onError onError= $error" }
        }
    }

    private fun getEthSignTypedData(change: String, account: String): String {
        val stringifiedParams = change.replace("\"", "\\\"")
        return "[\"$account\",\"$stringifiedParams\"]"
    }

    private fun signETHtypedDataWaless(challenge: String, account: String) {
        when (mTypeConnector) {
            Modal.ConnectorType.WALLET_CONNECT -> {
                // val params = getPersonalSignBody(challenge, account)
                val params = getEthSignTypedData(challenge, account)
                val requestParams = Modal.Params.Request(
                    // method = "personal_sign",
                    method = "eth_signTypedData",
                    params = params,
                )

                Web3Modal.request(
                    request = requestParams,
                    onSuccess = { data ->
                        /* callback that letting you know that you have successful request */
                        logger.i { "Web3Modal.request onSuccess $data" }
                    },
                    onError = { error ->
                        /* callback for error */
                        logger.e { "Web3Modal.request onError $error" }
                    }
                )
            }

            Modal.ConnectorType.COINBASE -> {
                logger.e { "Web3Modal SignTypedDataV3 requestParams account=$account challenge= $challenge" }
                val requestAccount = Web3JsonRPC.RequestAccounts().action()
                val signTypedDataV3 = Web3JsonRPC.SignTypedDataV3(
                    address = account, // address
                    typedDataJson = challenge // typed data JSON
                ).action()
                val handShakeActions = listOf(
                    requestAccount,
                    signTypedDataV3
                )
                client.initiateHandshake(
                    initialActions = handShakeActions
                ) { result: Result<List<ActionResult>>, account: Account? ->
                    result.onSuccess { actionResults: List<ActionResult> ->
                        var signature = ""
                        actionResults.last().let { returnValue -> // index, returnValue ->
                            val result = when (returnValue) {
                                is ActionResult.Result -> returnValue.value
                                is ActionResult.Error -> returnValue.message
                            }
                            signature += result.substring(1, result.length - 1) // bỏ dấu " ở đầu và cuối
                        }
                        logger.i { "personalSign onSuccess signal=$signature" }
                        viewModel.signinWallet(andressWallet, signature)
                    }
                    result.onFailure { err ->
                        logger.e { "personalSign onFailure err=$err" }
                    }
                }
            }
        }
    }
}
