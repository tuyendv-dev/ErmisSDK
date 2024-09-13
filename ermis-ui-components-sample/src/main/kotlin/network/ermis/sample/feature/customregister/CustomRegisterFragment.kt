package network.ermis.sample.feature.customregister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.initToolbar
import network.ermis.sample.common.navigateSafely
import network.ermis.sample.common.showToast
import network.ermis.sample.common.trimmedText
import network.ermis.chat.ui.sample.databinding.FragmentCustomRegisterBinding

class CustomRegisterFragment : Fragment() {

    private val viewModel: CustomRegisterViewModel by viewModels()

    private var _binding: FragmentCustomRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCustomRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar(binding.toolbar)
        binding.registerButton.setOnClickListener {
            val userId = binding.userIdEditText.trimmedText
            val userName = binding.userNameEditText.trimmedText
            val pass = binding.passWordEditText.trimmedText
            if (userId.isNotEmpty() && userName.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.registerLogin(userId = userId, userName = userName, pass = pass)
            }
            // viewModel.loginButtonClicked(collectCredentials())
        }
        viewModel.state.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is State.RedirectToChannels -> redirectToChannelsScreen()
                    is State.Loading -> showLoading()
                    is State.Error -> showErrorMessage(it.errorMessage)
                    is State.ValidationError -> showValidationErrors(it.invalidFields)
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
        //         // ValidatedField.API_KEY -> binding.apiKeyInputLayout
        //         ValidatedField.USER_ID -> binding.userIdInputLayout
        //         ValidatedField.USER_NAME -> binding.userNameInputLayout
        //     }.run {
        //         error = getString(R.string.custom_login_validation_error)
        //     }
        // }
    }

    private fun clearValidationErrors() {
        // binding.userIdInputLayout.error = null
    }

    private fun redirectToChannelsScreen() {
        findNavController().navigateSafely(R.id.action_customRegiserFragment_to_homeFragment)
    }
}
