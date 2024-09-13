package network.ermis.sample.feature.customlogin

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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.coinbase.android.nativesdk.CoinbaseWalletSDK
import com.coinbase.android.nativesdk.message.request.Account
import com.coinbase.android.nativesdk.message.request.Web3JsonRPC
import com.coinbase.android.nativesdk.message.response.ActionResult
import com.walletconnect.web3.modal.client.Modal
import com.walletconnect.web3.modal.client.Web3Modal
import com.walletconnect.web3.modal.ui.openWeb3Modal
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.initToolbar
import network.ermis.sample.common.navigateSafely
import network.ermis.sample.common.showToast
import network.ermis.sample.common.trimmedText
import network.ermis.chat.ui.sample.databinding.FragmentCustomLoginBinding
import io.getstream.log.taggedLogger
import org.json.JSONObject

class CustomLoginFragment : Fragment() {
    private val logger by taggedLogger("Chat:CustomLoginFragment")
    private val viewModel: CustomLoginViewModel by viewModels()

    private var _binding: FragmentCustomLoginBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentCustomLoginBinding.inflate(inflater, container, false)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.data ?: return@registerForActivityResult
            client.handleResponse(uri)
        }
        Web3Modal.disconnect() { it ->
            logger.e { "Web3Modal.disconnect onError $it" }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val web3ModalModalDelegate = object : Web3Modal.ModalDelegate {
        override fun onSessionApproved(approvedSession: Modal.Model.ApprovedSession) {
            // Triggered when receives the session approval from wallet
            when (approvedSession) {
                is Modal.Model.ApprovedSession.CoinbaseSession -> {
                    mTypeConnector = Modal.ConnectorType.COINBASE
                    logger.e { "Web3Modal.ModalDelegate onSessionApproved CoinbaseSession= ${approvedSession}" }
                }

                is Modal.Model.ApprovedSession.WalletConnectSession -> {
                    mTypeConnector = Modal.ConnectorType.WALLET_CONNECT
                    logger.e { "Web3Modal.ModalDelegate onSessionApproved WalletConnectSession= ${approvedSession}" }
                }
            }
            logger.e { "Web3Modal.ModalDelegate getAccount= ${Web3Modal.getAccount()}" }
            Web3Modal.getAccount()?.address?.let {
                andressWallet = it
                viewModel.connectWallet(it)
            }
        }

        override fun onSessionRejected(rejectedSession: Modal.Model.RejectedSession) {
            // Triggered when receives the session rejection from wallet
            logger.e { "Web3Modal.ModalDelegate onSessionRejected rejectedSession= $rejectedSession" }
        }

        override fun onSessionUpdate(updatedSession: Modal.Model.UpdatedSession) {
            // Triggered when receives the session update from wallet
            logger.e { "Web3Modal.ModalDelegate onSessionUpdate updatedSession= $updatedSession" }
        }

        override fun onSessionExtend(session: Modal.Model.Session) {
            // Triggered when receives the session extend from wallet
            logger.e { "Web3Modal.ModalDelegate onSessionExtend session= $session" }
        }

        override fun onSessionEvent(sessionEvent: Modal.Model.SessionEvent) {
            // Triggered when the peer emits events that match the list of events agreed upon session settlement
            logger.e { "Web3Modal.ModalDelegate onSessionEvent sessionEvent= $sessionEvent" }
        }

        override fun onSessionDelete(deletedSession: Modal.Model.DeletedSession) {
            // Triggered when receives the session delete from wallet
            logger.e { "Web3Modal.ModalDelegate onSessionDelete deletedSession= $deletedSession" }
        }

        override fun onSessionRequestResponse(response: Modal.Model.SessionRequestResponse) {
            // Triggered when receives the session request response from wallet
            logger.e { "Web3Modal.ModalDelegate onSessionRequestResponse response= $response" }
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
            logger.e { "Web3Modal.ModalDelegate onProposalExpired proposal= $proposal" }
        }

        override fun onRequestExpired(request: Modal.Model.ExpiredRequest) {
            // Triggered when a request becomes expired
            logger.e { "Web3Modal.ModalDelegate onRequestExpired request= $request" }
        }

        override fun onConnectionStateChange(state: Modal.Model.ConnectionState) {
            //Triggered whenever the connection state is changed
            logger.e { "Web3Modal.ModalDelegate onConnectionStateChange state= $state" }
        }

        override fun onError(error: Modal.Model.Error) {
            // Triggered whenever there is an issue inside the SDK
            logger.e { "Web3Modal.ModalDelegate onError onError= $error" }
        }
    }

    private fun getPersonalSignBody(change: String, account: String): String {
        logger.e { "Web3Modal change= $change" }
        val jsonChange = JSONObject(change)
        logger.e { "Web3Modal pramsOne= $jsonChange" }
        val msg = jsonChange.toString().encodeToByteArray()
            .joinToString(separator = "", prefix = "0x") { eachByte -> "%02x".format(eachByte) }
        return "[\"$msg\",\"$account\"]"
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
                logger.e { "Web3Modal getEthSignTypedData requestParams params= $params" }
                val requestParams = Modal.Params.Request(
                    // method = "personal_sign",
                    method = "eth_signTypedData",
                    params = params,
                )

                Web3Modal.request(
                    request = requestParams,
                    onSuccess = { data ->
                        /* callback that letting you know that you have successful request */
                        logger.e { "Web3Modal.request onSuccess $data" }
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
                        logger.e { "personalSign onSuccess signal=$signature" }
                        viewModel.signinWallet(andressWallet, signature)
                    }
                    result.onFailure { err ->
                        logger.e { "personalSign onFailure err=$err" }
                    }
                }
            }
        }

        // val signTypedDataV3 = Web3JsonRPC.SignTypedDataV3(
        //     address = account, // address
        //     typedDataJson = challenge // typed data JSON
        // ).action()
        // val requestActions = listOf(signTypedDataV3)
        //
        // client.makeRequest(request = RequestContent.Request(actions = requestActions)) { result ->
        //     result.fold(
        //         onSuccess = { returnValues ->
        //             logger.e { "Web3Modal.request onSuccess returnValues= $returnValues" }
        //         },
        //         onFailure = { err ->
        //             logger.e { "Web3Modal.request onFailure $err" }
        //         }
        //     )
        // }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // initToolbar(binding.toolbar)
        Web3Modal.setDelegate(web3ModalModalDelegate)
        binding.loginWalletButton.setOnClickListener {
            findNavController().openWeb3Modal(shouldOpenChooseNetwork = true) {
                logger.e { "Web3Modal findNavController().openWeb3Modal onError= $it" }
            }
        }
        binding.loginButton.setOnClickListener {
            val userId = binding.userIdEditText.trimmedText
            val pass = binding.passWordEditText.trimmedText
            if (userId.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.userLogin(userId = userId, pass = pass)
            }
            // viewModel.loginButtonClicked(collectCredentials())
        }
        binding.goRegisterButton.apply {
            isVisible = false
            setOnClickListener {
                goToRegisterFragment()
            }
        }

        binding.apiKeyInputLayout.isVisible = false
        binding.userTokenInputLayout.isVisible = false
        binding.userNameInputLayout.isVisible = false

        viewModel.state.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is State.RedirectToChannels -> redirectToChannelsScreen()
                    is State.RedirectToProjectList -> redirectToProjectList()
                    is State.Loading -> showLoading()
                    is State.Error -> showErrorMessage(it.errorMessage)
                    is State.ValidationError -> showValidationErrors(it.invalidFields)
                    is State.WalletConnectSuccess -> signETHtypedDataWaless(it.challenge, it.address)
                }
            },
        )
    }

    private fun showLoading() {
        binding.loadingProgressBar.isVisible = true
        clearValidationErrors()
    }

    private fun showErrorMessage(errorMessage: String?) {
        binding.loadingProgressBar.isVisible = false
        showToast(errorMessage ?: getString(R.string.backend_error_info))
    }

    private fun showValidationErrors(invalidFields: List<ValidatedField>) {
        binding.loadingProgressBar.isVisible = false
        // invalidFields.forEach {
        //     when (it) {
        //         ValidatedField.API_KEY -> binding.apiKeyInputLayout
        //         ValidatedField.USER_ID -> binding.userIdInputLayout
        //         ValidatedField.USER_TOKEN -> binding.userTokenInputLayout
        //     }.run {
        //         error = getString(R.string.custom_login_validation_error)
        //     }
        // }
    }

    private fun clearValidationErrors() {
        // binding.apiKeyInputLayout.error = null
        // binding.userIdInputLayout.error = null
        // binding.userTokenInputLayout.error = null
    }

    private fun redirectToChannelsScreen() {
        findNavController().navigateSafely(R.id.action_customLoginFragment_to_homeFragment)
    }

    private fun redirectToProjectList() {
        findNavController().navigateSafely(R.id.action_customLoginFragment_to_projectListFragment)
    }

    private fun goToRegisterFragment() {
        findNavController().navigateSafely(R.id.action_customLoginFragment_to_customRegisterFragment)
    }
}
