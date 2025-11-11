package com.example.playlistmaker.media.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistOpenBinding
import com.example.playlistmaker.player.ui.screens.AudioplayerFragment
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class OpenPlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistOpenBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private val trackAdapter = TrackAdapter()

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
// Скрываем меню BottomSheet при открытии плейлиста
        binding.bottomSheetMenu.visibility = View.GONE

        setupBottomSheet()
        setupRecyclerView()
        observeTracks()
        observePlaylistInfo()
        setupShareButton()

        val playlistId = arguments?.getInt("playlistId") ?: return
        viewModel.loadPlaylistInfo(playlistId)
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding.bottomSheetPlaylists
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.isHideable = false // нельзя скрыть
      //  bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun setupRecyclerView() {
        binding.tracksListView.adapter = trackAdapter

        trackAdapter.setOnTrackClickListener { track ->

            findNavController().navigate(
                R.id.action_OpenPlaylistFragment_to_audioplayerFragment,
                Bundle().apply { putParcelable(AudioplayerFragment.Companion.TRACK_EXTRA, track) }
            )
        }
        trackAdapter.setOnTrackLongClickListener { track ->
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.delete_track_message))
                .setNegativeButton(getString(R.string.button_no)) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(getString(R.string.button_yes)) { dialog, _ ->
                    val playlistId = arguments?.getInt("playlistId") ?: return@setPositiveButton
                    viewModel.removeTrackFromPlaylist(playlistId, track.trackId)
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun observeTracks() {
        viewModel.playlistTracks.observe(viewLifecycleOwner, Observer { tracks ->
            if (tracks.isNullOrEmpty()) {
                binding.noTracksMessage.visibility = View.VISIBLE
                binding.tracksListView.visibility = View.GONE
            } else {
                binding.noTracksMessage.visibility = View.GONE
                binding.tracksListView.visibility = View.VISIBLE
                trackAdapter.tracks.clear()
                trackAdapter.tracks.addAll(tracks)
                trackAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun observePlaylistInfo() {
        viewModel.playlistInfo.observe(viewLifecycleOwner, Observer { info ->
            info?.let { playlistInfo ->
                val playlist = playlistInfo.playlist

                // Название
                binding.tvPlayListName.text = playlist.playlistName

                // Описание
                binding.tvDescripcionPlayList.text = playlist.playlistDescr ?: ""

                // Обложка
                val coverUri: Uri? = playlist.playlistCoverUrl?.let { Uri.parse(it) }
                Glide.with(binding.ivCoverPlaylist.context)
                    .load(coverUri)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.ivCoverPlaylist)

                // Общая продолжительность
                val minutes = SimpleDateFormat("mm", Locale.getDefault())
                    .format(Date(playlistInfo.totalDuration))
                binding.playlistDuration.text = binding.root.context.resources.getQuantityString(
                    R.plurals.minutes_count, minutes.toInt(), minutes.toInt()
                )

                // Количество треков
                binding.playlistTrackCount.text = binding.root.context.resources.getQuantityString(
                    R.plurals.track_count, playlist.trackCount, playlist.trackCount
                )
            }
        })
    }

    private fun setupShareButton() {
        binding.shareIcon.setOnClickListener {
            val data = viewModel.getShareMessage()
            if (data == null) {
                Toast.makeText(
                    requireContext(),
                    R.string.no_tracks_to_share,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val (playlistName, tracks) = data

            val builder = StringBuilder()
            builder.appendLine(playlistName)

            // Кол-во треков через plurals
            val trackCountText = resources.getQuantityString(
                R.plurals.track_count,
                tracks.size,
                tracks.size
            )
            builder.appendLine(trackCountText)
            builder.appendLine()

            tracks.forEachIndexed { index, track ->
                val minutes = (track.trackTimeMillis / 1000) / 60
                val seconds = (track.trackTimeMillis / 1000) % 60
                val duration = String.format("%02d:%02d", minutes, seconds)
                builder.appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
            }

            val messageToShare = builder.toString().trim()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, messageToShare)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
