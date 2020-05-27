package trong.ccr.test.ui

import android.view.MotionEvent
import android.widget.OverScroller
import androidx.recyclerview.widget.RecyclerView

const val showLog = true

fun MotionEvent.showAction(): String {
    return when (this.getAction()) {
        MotionEvent.ACTION_DOWN -> "DOWN"
        MotionEvent.ACTION_MOVE -> "MOVE"
        MotionEvent.ACTION_UP -> "UP"
        MotionEvent.ACTION_CANCEL -> "CANCEL"
        else -> this.action.toString()
    }
}

fun Int.showState(): String {
    return when (this) {
        RecyclerView.SCROLL_STATE_DRAGGING -> "DRAGGING"
        RecyclerView.SCROLL_STATE_SETTLING -> "SETTLING"
        RecyclerView.SCROLL_STATE_IDLE -> "IDLE"
        else -> this.toString()
    }
}

/**
 * Calculate the velocity Y of [RecyclerViewTouchParent] for flinging Parent to Child
 */
fun RecyclerViewTouchParent.getCurrentVelocityY(): Float {
    val viewFlinger = this::class.java.superclass?.getDeclaredField("mViewFlinger")
    viewFlinger?.isAccessible = true
    val a = viewFlinger?.type?.getDeclaredField("mOverScroller")
    a?.isAccessible = true
    val overScroller = a?.get(viewFlinger?.get(this)) as? OverScroller
    val fieldScrollerY = OverScroller::class.java.getDeclaredField("mScrollerY")
    fieldScrollerY.isAccessible = true
    val fieldVelocityY = fieldScrollerY.type.getDeclaredField("mCurrVelocity")
    fieldVelocityY.isAccessible = true
    val mCurrVelocity = fieldVelocityY.get(fieldScrollerY.get(overScroller)) as Float
    return mCurrVelocity
}

fun RecyclerViewTouchChild.showLog(func: () -> Unit) {
    if(showLog) {
        func.invoke()
    }
}

fun RecyclerViewTouchParent.showLog(func: () -> Unit) {
    if(showLog) {
        func.invoke()
    }
}

fun CustomViewPager.showLog(func: () -> Unit) {
    if(showLog) {
        func.invoke()
    }
}