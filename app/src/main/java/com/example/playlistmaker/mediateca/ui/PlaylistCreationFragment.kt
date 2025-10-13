package com.example.playlistmaker.mediateca.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistCreationBinding

class PlaylistCreationFragment : Fragment() {

    private var _binding: FragmentPlaylistCreationBinding? = null
    private val binding get() = _binding!!

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
        nameEditText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) showKeyboard(nameEditText)
            else hideKeyboard(nameEditText)
        }
        descEditText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) showKeyboard(descEditText)
            else hideKeyboard(descEditText)
        }

        // Обработка клавиши Done на клавиатуре
        val doneListener = TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
