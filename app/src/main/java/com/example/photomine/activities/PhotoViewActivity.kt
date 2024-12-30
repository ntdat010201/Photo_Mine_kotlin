package com.example.photomine.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.photomine.R
import com.example.photomine.databinding.ActivityPhotoViewBinding
import com.example.photomine.dialog.DetailDialogFragment
import com.example.photomine.dialog.DialogDeleteFragment
import com.example.photomine.model.ModelImage
import com.example.photomine.viewmodel.ViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotoViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoViewBinding
    private lateinit var image: ModelImage
    private val viewModel: ViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        initListener()
    }

    private fun initData() {
        image = (intent.getSerializableExtra("model_image") as? ModelImage)!!

    }

    private fun initView() {
        image.let {
//            Glide.with(this).load(image.imageFile).into(binding.photoImageView)
            Glide.with(this).load(image.imageFile).into(binding.photoImageView)
        }
    }


    private fun initListener() {
        import()
        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.more.setOnClickListener { view ->
            showPopupMenu(view, image)
        }

        binding.share.setOnClickListener {
            shareImage(image.imageFile)
        }

        binding.delete.setOnClickListener {
            val dialogDeleteFragment = DialogDeleteFragment(image)
            dialogDeleteFragment.show(supportFragmentManager, dialogDeleteFragment.tag)

        }

    }

    private fun shareImage(imageUri: String?) {
        // Kiểm tra xem ảnh có tồn tại hay không
        if (imageUri != null) {
            // Tạo URI từ đường dẫn ảnh
            val uri = Uri.parse(imageUri)

            // Tạo intent chia sẻ
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "kiểm tra hình ảnh!")
            }

            // Khởi chạy chooser cho phép người dùng chọn ứng dụng chia sẻ
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ hình ảnh"))
        } else {
            Toast.makeText(this, "Không tìm thấy hình ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    private fun import() {
        lifecycleScope.launch {
            val isFavorite = viewModel.isFileFavorite(image.imageFile)
            if (isFavorite) {
                binding.favorite.setImageResource(R.drawable.ic_favorite_red)
            } else {
                binding.favorite.setImageResource(R.drawable.ic_favorite_white)
            }

        }
        binding.favorite.setOnClickListener {
            lifecycleScope.launch {
                val isFavorite = viewModel.isFileFavorite(image.imageFile)
                if (isFavorite) { // nếu path tồn tại ko null
                    Log.d("DAT", "delete: ${image.imageFile}")
                    viewModel.removeFavoriteFile(image)
                    binding.favorite.setImageResource(R.drawable.ic_favorite_white)
                    Toast.makeText(this@PhotoViewActivity, "đã xóa", Toast.LENGTH_SHORT).show()
                } else { // ko tồn tại
                    Log.d("DAT", "add: ${image.imageFile}")
                    binding.favorite.setImageResource(R.drawable.ic_favorite_red)
                    viewModel.addFavoriteFile(image)
                    Toast.makeText(this@PhotoViewActivity, "đã thêm", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun showPopupMenu(view: View, image: ModelImage) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_more_options, popupMenu.menu)

        // Xử lý sự kiện khi chọn item trong menu
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.detail -> {
                    val dialog = DetailDialogFragment(image)
                    dialog.show(supportFragmentManager, "DetailDialog")
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

}