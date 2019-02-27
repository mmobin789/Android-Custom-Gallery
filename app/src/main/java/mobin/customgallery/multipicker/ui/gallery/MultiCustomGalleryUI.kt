package mobin.customgallery.multipicker.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
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
    private lateinit var galleryViewModel: GalleryViewModel

    private lateinit var pictures: ArrayList<GalleryPicture>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_gallery_ui)
        requestReadStoragePermission()
    }

    private fun requestReadStoragePermission() {
        val readStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, readStorage) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(readStorage), 3)
        } else init()
    }

    private fun init() {
        galleryViewModel = ViewModelProviders.of(this)[GalleryViewModel::class.java]
        updateToolbar(0)
        val layoutManager = GridLayoutManager(this, 3)
        rv.layoutManager = layoutManager
        rv.addItemDecoration(SpaceItemDecoration(8))
        pictures = ArrayList(galleryViewModel.getGallerySize(this))
        adapter = GalleryPicturesAdapter(pictures)
        rv.adapter = adapter



        adapter.setOnClickListener { position, isSelectionEnabled ->
            if (isSelectionEnabled) {
                val picture = adapter.getItem(position)
                picture.isSelected = if (picture.isSelected) {
                    false
                } else {
                    val selectionCriteriaSuccess = getSelectedItemsCount() < 10
                    if (!selectionCriteriaSuccess)
                        selectionLimitReached()

                    selectionCriteriaSuccess
                }

                adapter.notifyItemChanged(position)

                updateToolbar(getSelectedItemsCount())


            }

        }
        adapter.setOnLongClickListener { position ->
            val picture = adapter.getItem(position)

            picture.isSelected = if (picture.isSelected) {
                false
            } else {
                val selectionCriteriaSuccess = getSelectedItemsCount() < 10
                if (!selectionCriteriaSuccess)
                    selectionLimitReached()

                selectionCriteriaSuccess
            }
            adapter.notifyItemChanged(position)

            updateToolbar(getSelectedItemsCount())

        }
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (layoutManager.findLastVisibleItemPosition() == pictures.lastIndex) {
                    loadPictures(25)
                }
            }
        })

        tvDone.setOnClickListener {
            super.onBackPressed()

        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
        loadPictures(25)

    }


    private fun getSelectedItemsCount() = pictures.filter { it.isSelected }.size

    private fun selectionLimitReached() {
        showToast("10 pictures selected already.")
    }


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
        val title = getString(R.string.txt_gallery)
        val data = if (selectedItems == 0) {
            tvDone.visibility = View.GONE
            title
        } else {
            tvDone.visibility = View.VISIBLE
            "$title $selectedItems/10"
        }

        tvTitle.text = data
    }

    override fun onBackPressed() {
        if (!adapter.removedSelection()) {
            super.onBackPressed()
        } else {
            updateToolbar(0)
        }


    }

    private fun showToast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            init()
        else {
            showToast("Permission Required to Fetch Gallery.")
            super.onBackPressed()
        }
    }

}
