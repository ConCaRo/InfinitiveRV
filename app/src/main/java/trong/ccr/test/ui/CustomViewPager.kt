package trong.ccr.test.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    companion object {
        const val TAG = "RecyclerView"
        const val type: String = "CustomViewPager"
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        showLog { Log.d(TAG, "$type dispatchTouchEvent " + event.showAction()) }
        return super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        showLog { Log.d(TAG, "$type onInterceptTouchEvent " + event.showAction() + "  " + event.x + "  " + event.y) }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        showLog { Log.d(TAG, "$type onTouchEvent " + event.showAction()) }
        return super.onTouchEvent(event)
    }
}