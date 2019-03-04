package mobin.customgallery.multipicker.ui.gallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.multi_gallery_listitem.*
import mobin.customgallery.multipicker.GlideApp
import mobin.customgallery.multipicker.R
import mobin.customgallery.multipicker.ui.gallery.model.GalleryPicture

class GalleryPicturesAdapter(private val list: List<GalleryPicture>) : RecyclerView.Adapter<GVH>() {

    private lateinit var onClick: (Int, Boolean) -> Unit
    private lateinit var onLongClick: (Int) -> Unit
    private lateinit var onClickComplete: () -> Unit
    private var isSelectionEnabled = false
    private val selectedIndexList = ArrayList<Int>(10)  // only 10 items are selectable.

    fun setOnClickListener(onClick: (Int, Boolean) -> Unit) {
        this.onClick = onClick
    }

    fun setOnLongClickListener(onLongClick: (Int) -> Unit) {
        this.onLongClick = onLongClick
    }

    fun setOnClickCompleteListener(onClickComplete: () -> Unit) {
        this.onClickComplete = onClickComplete
    }

    private fun checkSelection(position: Int) {
        if (isSelectionEnabled) {
            if (getItem(position).isSelected)
                selectedIndexList.add(position)
            else {
                selectedIndexList.remove(position)
                isSelectionEnabled = selectedIndexList.isNotEmpty()
            }
        }
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): GVH {
        val vh = GVH(LayoutInflater.from(p0.context).inflate(R.layout.multi_gallery_listitem, p0, false))
        vh.containerView.setOnClickListener {
            val position = vh.adapterPosition
            onClick(position, isSelectionEnabled)
            checkSelection(position)
            onClickComplete()

        }
        vh.containerView.setOnLongClickListener {
            val position = vh.adapterPosition
            isSelectionEnabled = true
            onLongClick(position)
            checkSelection(position)
            onClickComplete()



            isSelectionEnabled
        }
        return vh
    }

    fun getItem(position: Int) = list[position]

    override fun onBindViewHolder(p0: GVH, p1: Int) {
        val picture = list[p1]
        GlideApp.with(p0.containerView).load(picture.path).into(p0.ivImg)

        if (picture.isSelected) {
            p0.vSelected.visibility = View.VISIBLE
        } else {
            p0.vSelected.visibility = View.GONE
        }
    }

    override fun getItemCount() = list.size


    fun getSelectedItems() = selectedIndexList.map {
        list[it]
    }


    fun removedSelection(): Boolean {
        return if (isSelectionEnabled) {
            selectedIndexList.forEach {
                list[it].isSelected = false
            }
            isSelectionEnabled = false
            selectedIndexList.clear()
            notifyDataSetChanged()
            true

        } else false
    }
}