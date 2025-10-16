package com.example.playlistmaker.mediateca.ui.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.mediateca.ui.screens.FavoriteTracksViewModel
import com.example.playlistmaker.player.ui.screens.AudioplayerFragment
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    interface OnTrackClickListener {
        fun openPlayer(track: Track)
    }

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteTracksViewModel by viewModel()
    private val adapter = TrackAdapter()
    private var listener: OnTrackClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? OnTrackClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()

        adapter.setOnTrackClickListener { track ->
            // Навигация через родительский NavController
            findNavController().navigate(
                R.id.action_mediateka_to_audioplayer,
                Bundle().apply { putParcelable(AudioplayerFragment.Companion.TRACK_EXTRA, track) }
            )
        }
    }

    private fun setupRecyclerView() {
        binding.favouriteTracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoriteTracksFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteTracksViewModel.FavoriteTracksState.Empty -> {
                    binding.favouriteTracksRecyclerView.isVisible = false
                    binding.emptyStateContainer.isVisible = true
                }

                is FavoriteTracksViewModel.FavoriteTracksState.Content -> {
                    binding.favouriteTracksRecyclerView.isVisible = true
                    binding.emptyStateContainer.isVisible = false
                    adapter.tracks.clear()
                    adapter.tracks.addAll(state.tracks)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}