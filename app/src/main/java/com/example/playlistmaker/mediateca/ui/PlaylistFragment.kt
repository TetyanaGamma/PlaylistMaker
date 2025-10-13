package com.example.playlistmaker.media.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.mediateca.ui.MediatekaFragment
import com.example.playlistmaker.mediateca.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding?=null
    private val binding get() = _binding!!

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
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}