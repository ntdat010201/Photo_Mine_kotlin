package com.example.photomine.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photomine.adapter.SearchAdapter
import com.example.photomine.databinding.ActivitySearchBinding
import com.example.photomine.model.ModelImage

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var searchAdapter: SearchAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        initListener()
    }

    private fun initData() {
        val images: ArrayList<ModelImage> =
            intent.getSerializableExtra("images") as ArrayList<ModelImage>
        binding.rcvSearch.layoutManager = GridLayoutManager(this, 5)

        searchAdapter = SearchAdapter()
        binding.rcvSearch.adapter = searchAdapter

        searchFile(images)
    }


    private fun initView() {
        binding.searchView.isIconified = false
        binding.searchView.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchView, InputMethodManager.SHOW_IMPLICIT)

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                im.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }
        }
    }

    private fun initListener() {
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        searchAdapter?.onItemClickItem = { image ->
            val intent = Intent(this, PhotoViewActivity::class.java)
            intent.putExtra("model_image", image)
            startActivity(intent)
        }
    }

    private fun searchFile(images: ArrayList<ModelImage>) {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false


            override fun onQueryTextChange(newText: String?): Boolean {
                val mList = ArrayList<ModelImage>()

                if (!newText.isNullOrEmpty()) {
                    val userInput = newText.lowercase()
                    for (image in images) {
                        if (image.imageFile.lowercase().contains(userInput)) {
                            mList.add(image)
                        }
                    }
                }

                searchAdapter?.updateImage(mList)

                if (mList.isEmpty()) {
                    binding.rcvSearch.visibility = View.GONE
                } else {
                    binding.rcvSearch.visibility = View.VISIBLE
                }

                return true
            }


        })
    }
}