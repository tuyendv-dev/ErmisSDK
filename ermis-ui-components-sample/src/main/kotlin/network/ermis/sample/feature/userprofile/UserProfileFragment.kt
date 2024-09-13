package network.ermis.sample.feature.userprofile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import network.ermis.core.models.User
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.initToolbar
import network.ermis.sample.common.navigateSafely
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.FragmentUserProfileBinding
import network.ermis.sample.util.FilePath
import java.io.File
import java.io.IOException

class UserProfileFragment : Fragment() {

    private val viewModel: UserProfileViewModel by viewModels()

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private var mUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar(binding.toolbar)
        viewModel.getUserInfo()
        binding.btnUpdate.setOnClickListener {
            val name = binding.userNameEditText.text.toString().trim()
            if (name != mUser?.name) {
                viewModel.updateUserProfile(name = name)
            }
        }
        binding.ivChangeAvatar.setOnClickListener {
            imagePickerResultActivityLauncher.launch("image/*")
        }
        viewModel.state.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is State.Loading -> showLoading()
                    is State.Error -> showErrorMessage(it.errorMessage)
                    is State.GetUserProfileSuccess -> setDataUser(it.user)
                    is State.UpdateUserProfileSuccess -> updateSuccess(it.user)
                    is State.UpdateUserAvatarSuccess -> {
                        val user = mUser?.copy(image = it.avatar)
                        updateSuccess(user)
                    }
                    is State.GotoLogin -> navigateSafely(R.id.action_userProfileFragment_to_userLoginFragment)
                }
            },
        )
        binding.tvUserLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.sign_out_confirm))
                .setMessage("")
                .setPositiveButton(R.string.sign_out) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.signOut()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private var imagePickerResultActivityLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        //Do whatever you want to do with the result. the result is a Uri?
        if (imageUri != null) {
            // Handle the selected image
            try {
                val selectedFilePath: String = FilePath.getPath(requireContext(), imageUri).toString()
                val file = File(selectedFilePath)
                viewModel.updateUserAvater(file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showLoading() {
        binding.loadingProgressBar.isVisible = true
    }

    private fun showErrorMessage(errorMessage: String?) {
        binding.loadingProgressBar.isVisible = false
        showToast(errorMessage ?: getString(R.string.backend_error_info))
    }

    private fun setDataUser(user: User) {
        mUser = user
        binding.userAvatarView.setUser(user)
        binding.userNameEditText.setText(user.name)
        // binding.userPhoneEditText.setText(user.phone)
        // binding.userAboutEditText.setText(user.about)
    }

    private fun updateSuccess(user: User?) {
        binding.loadingProgressBar.isVisible = false
        showToast("Update success!")
        if (user != null) setDataUser(user)
    }
}
