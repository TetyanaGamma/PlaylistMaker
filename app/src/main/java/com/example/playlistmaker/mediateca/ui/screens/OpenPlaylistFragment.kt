package com.example.playlistmaker.media.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistOpenBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class OpenPlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistOpenBinding? = null
    private val binding get() = _binding!!

    private var playlistId: Int? = null
    private val viewModel: OpenPlaylistViewModel by viewModel()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistOpenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопка "Назад"
        binding.toolbarPlaylistOpen.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val playlistId = arguments?.getInt("playlistId") ?: return

        // Загружаем данные о плейлисте
        playlistId?.let {
            loadPlaylistInfo(it)
        }
    }

    private fun loadPlaylistInfo(id: Int) {
        lifecycleScope.launch {
            val info = viewModel.getPlaylistInfo(id)
            info?.let { playlistInfo ->
                val playlist = playlistInfo.playlist

                // Название
                binding.tvPlayListName.text = playlist.playlistName

                // Описание
                binding.tvDescripcionPlayList.text = playlist.playlistDescr ?: ""


                // Обложка
                val coverUri = playlist.playlistCoverUrl?.let { android.net.Uri.parse(it) }
                Glide.with(binding.ivCoverPlaylist.context)
                    .load(coverUri)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.ivCoverPlaylist)

                // Общая продолжительность
                val minutes = SimpleDateFormat("mm", Locale.getDefault())
                    .format(Date(playlistInfo.totalDuration))
                binding.playlistDuration.text = binding.root.context.resources.getQuantityString(
                    R.plurals.minutes_count, minutes.toInt(), minutes.toInt())

                // Количество треков
                binding.playlistTrackCount.text = binding.root.context.resources.getQuantityString(
                    R.plurals.track_count, playlist.trackCount, playlist.trackCount)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
