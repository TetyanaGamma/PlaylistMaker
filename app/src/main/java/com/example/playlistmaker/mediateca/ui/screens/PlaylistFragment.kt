package com.example.playlistmaker.media.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.mediateca.ui.adapters.PlaylistsAdapter
import com.example.playlistmaker.mediateca.ui.screens.MediatekaFragmentDirections
import com.example.playlistmaker.mediateca.ui.screens.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaylistsAdapter

    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPlayList.setOnClickListener {
            // Получаем главный контроллер из RootActivity
            val mainNavController = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.rootFragmentContainerView)
                ?.findNavController()

            mainNavController?.let { navController ->
                // Проверяем, что мы сейчас на экране медиатеки
                if (navController.currentDestination?.id == R.id.mediatekaFragment) {
                    navController.navigate(R.id.action_mediateka_to_playlistCreation)
                }
            }
        }

        adapter = PlaylistsAdapter(emptyList()) { playlist ->
            val mainNavController = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.rootFragmentContainerView)
                ?.findNavController()

            mainNavController?.let { navController ->
                // Проверяем, что мы сейчас на экране медиатеки
                if (navController.currentDestination?.id == R.id.mediatekaFragment) {
                    val action = MediatekaFragmentDirections
                        .actionMediatekaToOpenPlaylistFragment(playlist.playlistId)
                    navController.navigate(action)
                }
            }
        }
        binding.recyclerPlaylists.adapter = adapter
        binding.recyclerPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)

        // Список плейлистов
        viewModel.playlists.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            binding.noPlaylistsPlaceholder.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}