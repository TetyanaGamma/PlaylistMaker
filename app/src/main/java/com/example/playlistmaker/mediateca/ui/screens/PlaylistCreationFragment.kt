package com.example.playlistmaker.mediateca.ui.screens

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.widget.addTextChangedListener

open class PlaylistCreationFragment : Fragment() {

    protected var _binding: FragmentPlaylistCreationBinding? = null
    protected val binding get() = _binding!!

    protected open val viewModel: PlaylistCreationViewModel by viewModel()
    protected var selectedImageUri: Uri? = null

    protected open val radiusInPx by lazy { (8f * resources.displayMetrics.density).toInt() }

    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            selectedImageUri = uri
            Glide.with(this)
                .load(uri ?: R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(radiusInPx))
                .into(binding.imageAddPhoto)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupBackHandler()
        restoreState(savedInstanceState)
    }

    private fun setupUI() {
        val nameEditText = binding.inputPlaylistName.editText
        val descEditText = binding.inputPlaylistDescription.editText

        val doneListener = TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(v)
                v.clearFocus()
                true
            } else false
        }
        nameEditText?.setOnEditorActionListener(doneListener)
        descEditText?.setOnEditorActionListener(doneListener)

        // Кнопка создаётся активной только если есть название
        binding.buttonCreatePlaylist.isEnabled = false
        nameEditText?.addTextChangedListener { s ->
            binding.buttonCreatePlaylist.isEnabled = !s.isNullOrBlank()
        }

        binding.imageAddPhoto.setOnClickListener { openImagePicker() }
        binding.buttonCreatePlaylist.setOnClickListener { savePlaylist() }
    }

    private fun setupBackHandler() {
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleExit()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)
        binding.newPlaylistToolbar.setNavigationOnClickListener { handleExit() }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return

        binding.inputPlaylistName.editText?.setText(savedInstanceState.getString(KEY_NAME, ""))
        binding.inputPlaylistDescription.editText?.setText(savedInstanceState.getString(KEY_DESC, ""))

        val uriString = savedInstanceState.getString(KEY_IMAGE_URI)
        if (!uriString.isNullOrEmpty()) {
            selectedImageUri = Uri.parse(uriString)
            Glide.with(this)
                .load(selectedImageUri ?: R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(radiusInPx))
                .into(binding.imageAddPhoto)
        }
    }

    private fun openImagePicker() {
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun savePlaylist() {
        val name = binding.inputPlaylistName.editText?.text?.toString()?.trim()
        if (name.isNullOrBlank()) return

        val desc = binding.inputPlaylistDescription.editText?.text?.toString()?.trim()

        viewModel.createPlaylist(name, desc, selectedImageUri) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Плейлист \"$name\" создан", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Ошибка при создании плейлиста", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showKeyboard(view: View?) {
        view?.requestFocus()
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View?) {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun handleExit() {
        val nameEntered = !binding.inputPlaylistName.editText?.text.isNullOrBlank()
        val descEntered = !binding.inputPlaylistDescription.editText?.text.isNullOrBlank()
        val imageSelected = selectedImageUri != null

        if (!nameEntered && !descEntered && !imageSelected) {
            findNavController().popBackStack()
            return
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_question))
            .setMessage(getString(R.string.dialog_negative_message))
            .setNegativeButton(getString(R.string.dialog_cancle)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.dialog_finish)) { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_NAME, binding.inputPlaylistName.editText?.text?.toString())
        outState.putString(KEY_DESC, binding.inputPlaylistDescription.editText?.text?.toString())
        outState.putString(KEY_IMAGE_URI, selectedImageUri?.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_NAME = "key_name"
        private const val KEY_DESC = "key_desc"
        private const val KEY_IMAGE_URI = "key_image_uri"
    }
}
