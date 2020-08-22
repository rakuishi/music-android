package com.rakuishi.music.presentation.album_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rakuishi.music.R
import com.rakuishi.music.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_album_list.*

@AndroidEntryPoint
class AlbumListFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val albumListViewModel: AlbumListViewModel by viewModels()
    private val adapter: AlbumListAdapter = AlbumListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_album_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindUi()
        subscribeUi()
        albumListViewModel.retrieveAlbums()
    }

    private fun bindUi() {
        adapter.onClick = { album ->
            val directions = AlbumListFragmentDirections.showDetail(album.title, album)
            findNavController().navigate(directions)
        }
        adapter.onLongClick = { album -> mainViewModel.play(album.id.toString()) }
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    private fun subscribeUi() {
        albumListViewModel.albumList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}