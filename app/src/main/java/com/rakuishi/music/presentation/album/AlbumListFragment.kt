package com.rakuishi.music.presentation.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.rakuishi.music.R
import com.rakuishi.music.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_album_list.*

@AndroidEntryPoint
class AlbumListFragment : Fragment() {

    companion object {
        fun newInstance() = AlbumListFragment()
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private val albumViewModel: AlbumListViewModel by viewModels()
    private val adapter: AlbumAdapter = AlbumAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_album_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter.onClick = { album -> mainViewModel.play(album.id) }
        adapter.onLongClick = { album -> mainViewModel.play(album.id) }
        adapter.submitList(albumViewModel.retrieveAlbums())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }
}