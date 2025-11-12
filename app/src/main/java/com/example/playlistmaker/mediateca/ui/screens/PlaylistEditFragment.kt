package com.example.playlistmaker.mediateca.ui.screens

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.domain.model.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.activity.OnBackPressedCallback

class PlaylistEditFragment : PlaylistCreationFragment() {

    override val viewModel: PlaylistEditViewModel by viewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Меняем заголовок и кнопку
        binding.newPlaylistToolbar.title = getString(R.string.edit_playlist)
        binding.buttonCreatePlaylist.text = getString(R.string.save)

        // Получаем ID плейлиста из аргументов
        val playlistId = arguments?.getInt(ARG_PLAYLIST_ID) ?: return
        viewModel.loadPlaylist(playlistId)

        // Наблюдаем за данными плейлиста
        viewModel.playlistData.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                populateFields(it)
            }
        }
        // Сохраняем изменения
        binding.buttonCreatePlaylist.setOnClickListener {
            saveChanges()
        }
        // Toolbar back button
        binding.newPlaylistToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        // Обработка системной кнопки Back
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Просто закрываем экран, игнорируя изменения
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun populateFields(playlist: Playlist) {
        binding.inputPlaylistName.editText?.setText(playlist.playlistName)
        binding.inputPlaylistDescription.editText?.setText(playlist.playlistDescr)
        selectedImageUri =
            playlist.playlistCoverUrl?.takeIf { it.isNotBlank() }?.let { Uri.parse(it) }

        if (selectedImageUri != null) {
            Glide.with(this)
                .load(selectedImageUri)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(radiusInPx))
                .into(binding.imageAddPhoto)
        } else {
            // Если нет картинки или строка пустая
            Glide.with(this)
                .load(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(radiusInPx))
                .into(binding.imageAddPhoto)
        }

        // Проверяем активность кнопки
        binding.buttonCreatePlaylist.isEnabled = playlist.playlistName.isNotBlank()
        binding.inputPlaylistName.editText?.addTextChangedListener { s ->
            binding.buttonCreatePlaylist.isEnabled = !s.isNullOrBlank()
        }
    }

    private fun saveChanges() {
        val name = binding.inputPlaylistName.editText?.text.toString().trim()
        val desc = binding.inputPlaylistDescription.editText?.text?.toString()?.trim()

        viewModel.updatePlaylist(name, desc, selectedImageUri) { success ->
            if (success) {
                // Отправляем результат назад (что плейлист обновлён)
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("playlist_updated", true)

                //  Возвращаемся на экран открытого плейлиста по ID
                val playlistId = arguments?.getInt(ARG_PLAYLIST_ID)
                val bundle = Bundle().apply {
                    putInt("playlistId", playlistId ?: return@updatePlaylist)
                }

                findNavController().navigate(
                    R.id.action_playlistEditFragment_to_OpenPlaylistFragment,
                    bundle
                )
            }
        }

    }


    companion object {
        const val ARG_PLAYLIST_ID = "arg_playlist_id"
    }
}
