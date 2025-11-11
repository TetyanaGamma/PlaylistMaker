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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistOpenBinding
import com.example.playlistmaker.player.ui.adapters.BottomSheetPlaylistAdapter
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
    private lateinit var bottomSheetMenuBehavior: BottomSheetBehavior<*>

    private val trackAdapter = TrackAdapter()
    private lateinit var bottomSheetAdapter: BottomSheetPlaylistAdapter

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

        setupBottomSheet()
        setupRecyclerView()
        observeTracks()
        observePlaylistInfo()
        setupShareButton()
        setupMenuBottomSheet()

        val playlistId = arguments?.getInt("playlistId") ?: return
        viewModel.loadPlaylistInfo(playlistId)
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding.bottomSheetPlaylists
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun setupMenuBottomSheet() {
        bottomSheetMenuBehavior = BottomSheetBehavior.from(binding.bottomSheetMenu)
        bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        // Затемнение фона
        binding.overlay.setOnClickListener {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay.visibility = View.GONE
        }

        // Кнопка меню
        binding.menuIcon.setOnClickListener {
            if (bottomSheetMenuBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.overlay.visibility = View.VISIBLE
            } else {
                bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.overlay.visibility = View.GONE
            }
        }

        // Отслеживание скрытия
        bottomSheetMenuBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.overlay.visibility = View.GONE
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // Инициализация адаптера с пустым списком, потом обновим
        bottomSheetAdapter = BottomSheetPlaylistAdapter(emptyList()) { clickedPlaylist ->
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay.visibility = View.GONE
        }
        binding.playListView.layoutManager = LinearLayoutManager(requireContext())
        binding.playListView.adapter = bottomSheetAdapter
    }

    private fun setupRecyclerView() {
        binding.tracksListView.adapter = trackAdapter

        trackAdapter.setOnTrackClickListener { track ->
            findNavController().navigate(
                R.id.action_OpenPlaylistFragment_to_audioplayerFragment,
                Bundle().apply { putParcelable(AudioplayerFragment.TRACK_EXTRA, track) }
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

                // Название и описание
                binding.tvPlayListName.text = playlist.playlistName
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

                // **Обновляем BottomSheetAdapter с текущим плейлистом**
                bottomSheetAdapter.updateData(listOf(playlist))
            }
        })
    }

    private fun setupShareButton() {
        binding.shareIcon.setOnClickListener {
            val messageToShare = viewModel.getShareMessage()
            if (messageToShare == null) {
                Toast.makeText(requireContext(), R.string.no_tracks_to_share, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
