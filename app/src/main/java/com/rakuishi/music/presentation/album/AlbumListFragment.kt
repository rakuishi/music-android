package com.rakuishi.music.presentation.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rakuishi.music.MainActivity
import com.rakuishi.music.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_album_list.*

@AndroidEntryPoint
class AlbumListFragment : Fragment() {

    companion object {
        fun newInstance() = AlbumListFragment()
    }

    private val viewModel: AlbumListViewModel by viewModels()
    private val adapter: AlbumAdapter = AlbumAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_album_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter.onClick = { album ->
            if (requireActivity() is MainActivity) {
                (requireActivity() as MainActivity).play(viewModel.retrieveSongs(album.id))
            }
        }
        adapter.onLongClick = { album ->
            if (requireActivity() is MainActivity) {
                (requireActivity() as MainActivity).play(viewModel.retrieveSongs(album.id))
            }
        }
        adapter.submitList(viewModel.retrieveAlbums())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }
}