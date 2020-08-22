package com.rakuishi.music.presentation.album_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.rakuishi.music.R
import com.rakuishi.music.presentation.MainViewModel
import com.rakuishi.music.util.id
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_album_detail.*

@AndroidEntryPoint
class AlbumDetailFragment : Fragment() {

    private val args: AlbumDetailFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val albumDetailViewModel: AlbumDetailViewModel by viewModels()
    private lateinit var adapter: AlbumDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_album_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindUi()
        subscribeUi()
        albumDetailViewModel.retrieveSongs(args.album.id)
    }

    private fun bindUi() {
        adapter = AlbumDetailAdapter(args.album)
        adapter.onAlbumClick = { album -> mainViewModel.play(album.id.toString()) }
        adapter.onMetadataClick =
            { metadata -> mainViewModel.play(args.album.id.toString(), metadata.id) }
        recyclerView.setHasFixedSize(true)
        recyclerView.addOnScrollListener(albumDetailViewModel.scrollListener)
        recyclerView.adapter = adapter
    }

    private fun subscribeUi() {
        albumDetailViewModel.metadataList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        albumDetailViewModel.actionBarElevation.observe(viewLifecycleOwner, Observer {
            (requireActivity() as AppCompatActivity).supportActionBar?.elevation = it
        })
    }
}