package com.data.app.presentation.main.community.post_detail

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class HorizontalMarginItemDecoration(
    context: Context,
    @DimenRes private val horizontalMarginInDp: Int
) : RecyclerView.ItemDecoration() {

    private val horizontalMarginInPx: Int =
        context.resources.getDimensionPixelSize(horizontalMarginInDp)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }
}
