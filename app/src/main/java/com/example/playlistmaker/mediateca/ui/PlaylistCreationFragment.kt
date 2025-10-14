package com.example.playlistmaker.mediateca.ui

import android.Manifest
import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreationBinding
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreationFragment : Fragment() {

    private var _binding: FragmentPlaylistCreationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistCreationViewModel by viewModel()


    private val requester = PermissionRequester.instance()
    private var selectedImageUri: Uri? = null

    // Photo Picker (Android 13+)
    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (!isAdded || _binding == null) return@registerForActivityResult
            selectedImageUri = uri
            if (uri != null) {
                binding.imageAddPhoto.setImageURI(uri)
            } else {
                binding.imageAddPhoto.setImageResource(R.drawable.placeholder)
            }
        }

    // Старый способ (Android 12 и ниже)
    private val legacyImagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!isAdded || _binding == null) return@registerForActivityResult
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                selectedImageUri = uri
                if (uri != null) {
                    binding.imageAddPhoto.setImageURI(uri)
                } else {
                    binding.imageAddPhoto.setImageResource(R.drawable.placeholder)
                }
            }
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

        binding.buttonCreatePlaylist.isEnabled = false

        val nameEditText = binding.inputPlaylistName.editText
        val descEditText = binding.inputPlaylistDescription.editText

        // Показываем клавиатуру при фокусе
        nameEditText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showKeyboard(nameEditText)
            else hideKeyboard(nameEditText)
        }
        descEditText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showKeyboard(descEditText)
            else hideKeyboard(descEditText)
        }

        // Обработка клавиши Done на клавиатуре
        val doneListener = TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                hideKeyboard(v)
                v.clearFocus()
                true
            } else false
        }
        nameEditText?.setOnEditorActionListener(doneListener)
        descEditText?.setOnEditorActionListener(doneListener)

        // Следим за полем "Название" для активации кнопки
        nameEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonCreatePlaylist.isEnabled = !s.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Клик по области обложки
        binding.imageAddPhoto.setOnClickListener { openImagePicker() }

        binding.buttonCreatePlaylist.setOnClickListener { savePlaylist() }

        // Обработка системной кнопки Back и тулбарной кнопки
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleExit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)
        binding.newPlaylistToolbar.setNavigationOnClickListener { handleExit() }
    }

    private fun openImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            lifecycleScope.launch {
                val result = requester.request(Manifest.permission.READ_EXTERNAL_STORAGE).first()
                when (result) {
                    is PermissionResult.Granted -> {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        legacyImagePicker.launch(intent)
                    }

                    else -> {
                        selectedImageUri = null
                        if (isAdded && _binding != null) {
                            binding.imageAddPhoto.setImageResource(R.drawable.placeholder)
                        }
                    }
                }
            }
        }
    }

    private fun savePlaylist() {
        val name = binding.inputPlaylistName.editText?.text?.toString()?.trim()
        if (name.isNullOrBlank()) return // Название обязательно

        val desc = binding.inputPlaylistDescription.editText?.text?.toString()?.trim() ?: ""
        val cover = selectedImageUri?.toString() ?: "android.resource://${requireContext().packageName}/${R.drawable.placeholder}"

        // Создаём плейлист через ViewModel
        viewModel.createPlaylist(name, desc, cover)

        // Закрываем экран
        findNavController().popBackStack()
    }

    private fun showKeyboard(editText: View?) {
        editText?.requestFocus()
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View?) {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun handleExit() {
        if (!isAdded || _binding == null) return

        val nameEntered = !binding.inputPlaylistName.editText?.text.isNullOrBlank()
        val descEntered = !binding.inputPlaylistDescription.editText?.text.isNullOrBlank()
        val imageSelected = selectedImageUri != null

        if (!nameEntered && !descEntered && !imageSelected) {
            findNavController().popBackStack()

        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")
                .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Завершить") { _, _ ->
                    if (isAdded && _binding != null) findNavController().popBackStack()
                }
                .create()
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
