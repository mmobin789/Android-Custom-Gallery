package mobin.customgallery.multipicker.ui.gallery.viewmodel

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import mobin.customgallery.multipicker.ui.gallery.model.GalleryPicture
import java.util.*

class GalleryViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private var startingRow = 0
    private var rowsToLoad = 0
    private var allLoaded = false

    fun getImagesFromGallery(
        context: Context,
        pageSize: Int,
        list: (List<GalleryPicture>) -> Unit
    ) {
        compositeDisposable.add(
            Single.fromCallable {
                fetchGalleryImages(context, pageSize)
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    list(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun getGallerySize(context: Context): Int {
        val cursor = getGalleryCursor(context)
        val rows = cursor!!.count
        cursor.close()
        return rows
    }

    private fun fetchGalleryImages(context: Context, rowsPerLoad: Int): List<GalleryPicture> {
        val galleryImageUrls = LinkedList<GalleryPicture>()
        val cursor = getGalleryCursor(context)

        if (cursor != null && !allLoaded) {
            val totalRows = cursor.count

            allLoaded = rowsToLoad == totalRows
            if (rowsToLoad < rowsPerLoad) {
                rowsToLoad = rowsPerLoad
            }

            for (i in startingRow until rowsToLoad) {
                cursor.moveToPosition(i)
                val dataColumnIndex =
                    cursor.getColumnIndex(MediaStore.MediaColumns._ID) //get column index
                galleryImageUrls.add(GalleryPicture(getImageUri(cursor.getString(dataColumnIndex)).toString())) //get Image path from column index

            }
            Log.i("TotalGallerySize", "$totalRows")
            Log.i("GalleryStart", "$startingRow")
            Log.i("GalleryEnd", "$rowsToLoad")

            startingRow = rowsToLoad

            if (rowsPerLoad > totalRows || rowsToLoad >= totalRows)
                rowsToLoad = totalRows
            else {
                if (totalRows - rowsToLoad <= rowsPerLoad)
                    rowsToLoad = totalRows
                else
                    rowsToLoad += rowsPerLoad
            }

            cursor.close()
            Log.i("PartialGallerySize", " ${galleryImageUrls.size}")
        }
        return galleryImageUrls
    }

    private fun getGalleryCursor(context: Context): Cursor? {
        val externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val columns = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATE_MODIFIED)
        val orderBy = MediaStore.MediaColumns.DATE_MODIFIED //order data by modified
        return context.contentResolver
            .query(
                externalUri,
                columns,
                null,
                null,
                "$orderBy DESC"
            )//get all data in Cursor by sorting in DESC order
    }

    private fun getImageUri(path: String) = ContentUris.withAppendedId(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        path.toLong()
    )

    override fun onCleared() {
        compositeDisposable.clear()
    }
}