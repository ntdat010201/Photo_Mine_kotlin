package com.example.photomine.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photomine.R
import com.example.photomine.activities.CameraActivity
import com.example.photomine.activities.FavoriteActivity
import com.example.photomine.activities.PhotoViewActivity
import com.example.photomine.activities.SearchActivity
import com.example.photomine.activities.VideoActivity
import com.example.photomine.adapter.CollectionAdapter
import com.example.photomine.databinding.FragmentCollectionBinding
import com.example.photomine.viewmodel.ViewModel
import com.google.android.material.appbar.AppBarLayout
import org.koin.androidx.viewmodel.ext.android.viewModel


class CollectionFragment : Fragment() {
    private lateinit var binding: FragmentCollectionBinding
    private val viewModel: ViewModel by viewModel()

    private lateinit var adapter: CollectionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionBinding.inflate(layoutInflater)
        initData()
        initView()
        initListener()
        return binding.root
    }


    private fun initData() {

        adapter = CollectionAdapter(emptyList())
        binding.rcvCollection.adapter = adapter
        binding.rcvCollection.layoutManager = GridLayoutManager(requireContext(), 5)
        Log.d("DAT", "initData: ")

    }


    private fun initView() {
        toolbarAnimation()

    }

    private fun initListener() {
        scrollVisibilityCamera()

        binding.camera.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            startActivity(intent)
        }
        binding.imageVideo.setOnClickListener {
            val intent = Intent(requireContext(), VideoActivity::class.java)
            startActivity(intent)
        }
        binding.imageFavorite.setOnClickListener {
            val intent = Intent(requireContext(), FavoriteActivity::class.java)
            startActivity(intent)
        }
        adapter.onItemClickItem = { image ->
            val intent = Intent(requireContext(), PhotoViewActivity::class.java)
            intent.putExtra("model_image", image)
            startActivity(intent)
        }

        viewModel.imagesLiveData.observe(viewLifecycleOwner, Observer { images ->
            binding.searchView.setOnClickListener {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                intent.putExtra("images",ArrayList(images))
                startActivity(intent)
            }
        })

    }

    override fun onStart() {
        super.onStart()
        viewModel.loadImagesAndVideos()
        viewModel.imagesLiveData.observe(viewLifecycleOwner, Observer { images ->
            Log.d("DAT", "initData: ${images.size}")
            adapter.updateImage(images)
        })
    }


    private fun scrollVisibilityCamera() {
        binding.rcvCollection.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Kiểm tra hướng cuộn
                if (dy > 0) {
                    // Cuộn xuống, ẩn ImageView
                    binding.camera.visibility = View.GONE
                } else if (dy < 0) {
                    // Cuộn lên, hiển thị lại ImageView
                    binding.camera.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun toolbarAnimation() {
        binding.collapsingToolbarLayout.title = getString(R.string.app_name)

        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val currentScroll = -verticalOffset

            // Tính toán độ mờ (alpha) dựa trên vị trí cuộn
            val alpha = if (currentScroll < 600) {
                (currentScroll / 600f).coerceIn(0f, 1f)
            } else {
                1f
            }
            // Cập nhật độ mờ cho các thành phần (nếu cần)
            binding.Constraint.alpha = 1 - alpha
//            binding.textView.alpha = 1 - alpha

            // Ẩn ImageView khi cuộn lên vượt quá một ngưỡng nhất định
            if (currentScroll > 600) {
                binding.Constraint.visibility = View.GONE // Ẩn ImageView
            } else {
                binding.Constraint.visibility = View.VISIBLE // Hiện ImageView lại
            }
        })


    }

}