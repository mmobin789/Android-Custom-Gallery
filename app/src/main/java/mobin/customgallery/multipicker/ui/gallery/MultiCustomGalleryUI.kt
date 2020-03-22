package mobin.customgallery.multipicker.ui.gallery

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_multi_gallery_ui.*
import kotlinx.android.synthetic.main.toolbar.*
import mobin.customgallery.multipicker.R
import mobin.customgallery.multipicker.ui.gallery.adapter.GalleryPicturesAdapter
import mobin.customgallery.multipicker.ui.gallery.adapter.SpaceItemDecoration
import mobin.customgallery.multipicker.ui.gallery.model.GalleryPicture
import mobin.customgallery.multipicker.ui.gallery.viewmodel.GalleryViewModel

class MultiCustomGalleryUI : AppCompatActivity() {

    private lateinit var adapter: GalleryPicturesAdapter
    private val galleryViewModel: GalleryViewModel by viewModels<GalleryViewModel>()

    private lateinit var pictures: ArrayList<GalleryPicture>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_gallery_ui)
        requestReadStoragePermission()
    }

    private fun requestReadStoragePermission() {
        val readStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                readStorage
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(readStorage), 3)
        } else init()
    }

    private fun init() {
        // galleryViewModel = ViewModelProviders.of(this)[GalleryViewModel::class.java] /** @deprecated */
        updateToolbar(0)
        val layoutManager = GridLayoutManager(this, 3)
        rv.layoutManager = layoutManager
        rv.addItemDecoration(SpaceItemDecoration(8))
        pictures = ArrayList(galleryViewModel.getGallerySize(this))
        adapter = GalleryPicturesAdapter(pictures, 10)
        rv.adapter = adapter

        adapter.setOnClickListener { galleryPicture ->
            showToast(getImageUri(galleryPicture.path).path!!)
        }

        adapter.setAfterSelectionListener {
            updateToolbar(getSelectedItemsCount())
        }

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (layoutManager.findLastVisibleItemPosition() == pictures.lastIndex) {
                    loadPictures(5)
                }
            }
        })

        tvDone.setOnClickListener {
            super.onBackPressed()

        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
        loadPictures(5)
    }


    private fun getSelectedItemsCount() = adapter.getSelectedItems().size

    private fun loadPictures(pageSize: Int) {
        galleryViewModel.getImagesFromGallery(this, pageSize) {
            if (it.isNotEmpty()) {
                pictures.addAll(it)
                adapter.notifyItemRangeInserted(pictures.size, it.size)
            }
            Log.i("GalleryListSize", "${pictures.size}")
        }
    }

    private fun updateToolbar(selectedItems: Int) {
        val data = if (selectedItems == 0) {
            tvDone.visibility = View.GONE
            getString(R.string.txt_gallery)
        } else {
            tvDone.visibility = View.VISIBLE
            "$selectedItems/${adapter.getSelectionLimit()}"
        }
        tvTitle.text = data
    }

    override fun onBackPressed() {
        if (adapter.removedSelection()) {
            updateToolbar(0)
        } else {
            super.onBackPressed()
        }
    }

    private fun showToast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            init()
        else {
            showToast("Permission Required to Fetch Gallery.")
            super.onBackPressed()
        }
    }

    companion object {
        fun getImageUri(path: String): Uri {
            return ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                path.toLong()
            )
        }
    }
}
