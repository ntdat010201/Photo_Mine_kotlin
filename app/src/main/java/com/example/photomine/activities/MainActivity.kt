package com.example.photomine.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.photomine.R
import com.example.photomine.adapter.ViewpagerActivityAdapter
import com.example.photomine.databinding.ActivityMainBinding
import com.example.photomine.fragment.CollectionFragment
import com.example.photomine.fragment.SettingFragment
import com.example.photomine.utils.Const.REQUEST_CODE
import com.example.photomine.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val collectionFragment = CollectionFragment()
    private val settingFragment = SettingFragment()
    private var adapter: ViewpagerActivityAdapter? = null
    private val viewModel: ViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        setNavigation()
        requestStoragePermission()
    }

    private fun setNavigation() {
        adapter = ViewpagerActivityAdapter(this)
        adapter!!.setFragments(collectionFragment, settingFragment)

        binding.viewPager2.adapter = adapter
        binding.viewPager2.offscreenPageLimit = 2
        binding.viewPager2.isUserInputEnabled = false   // khóa cuộn

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        binding.bottomNavigation.menu.findItem(R.id.menu_collection).isChecked =
                            true
                    }

                    else -> binding.bottomNavigation.menu.findItem(R.id.menu_setting).isChecked =
                        true
                }
            }
        })

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.menu_collection -> {
                    binding.viewPager2.currentItem = 0
                    true
                }

                R.id.menu_setting -> {
                    binding.viewPager2.currentItem = 1
                    true
                }

                else -> false
            }
        }

    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 trở lên
            val permissions = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_VIDEO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
            }
            if (permissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_CODE)
            } else {
                viewModel.loadImagesAndVideos()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 đến 12
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE
                )
            } else {
                viewModel.loadImagesAndVideos()
            }
        } else {
            // Android 9 trở xuống
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE
                )
            } else {
                viewModel.loadImagesAndVideos()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Quyền đã được cấp
                viewModel.loadImagesAndVideos()
            } else {
                // Quyền bị từ chối, xử lý theo nhu cầu ứng dụng
                Toast.makeText(this, "Quyền truy cập bị từ chối!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}