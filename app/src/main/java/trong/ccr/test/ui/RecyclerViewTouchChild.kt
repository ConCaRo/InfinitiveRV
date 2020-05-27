package trong.ccr.test.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * Custom RV - called RV Child
 * Responsible for getting touch event to from [RecyclerViewTouchParent] - RV Parent
 */
class RecyclerViewTouchChild : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    
    companion object {
        const val TAG = "RecyclerView"
        const val type: String = "RecyclerViewTouchChild"
    }
    var childDelegate = true                             // Check whether delegate to Child Rv
    var stopScrollParent = false                         // Check whether fling Parent Rv from Child Rv

    private var childListener: IChild? = null

    interface IChild {
        fun stopScrollParent()                          // Stop Parent Rv when Child Rv finish scrolling
        fun flingChildToParent(velocityY: Int)          // Fling from Child Rv to Parent Rv (scrolling up from Child Rv)
    }

    fun setListener(listener: IChild) {
        childListener = listener
    }

    init {
        onFlingListener = object : OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                showLog { Log.d(TAG, "$type onFling $velocityX $velocityY") }
                return false
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        showLog { Log.d(TAG, "$type dispatchTouchEvent " + event?.showAction()) }
        return super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        showLog { Log.d(TAG, "$type onInterceptTouchEvent " + event?.showAction()) }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        showLog { Log.d(TAG, "$type onTouchEvent " + event.showAction() + " " + scrollState.showState()) }
        if (childListener != null) {
            if (scrollState == SCROLL_STATE_IDLE && event?.action == MotionEvent.ACTION_UP) {
                childListener?.stopScrollParent()
            } else if (event.action == MotionEvent.ACTION_DOWN && (scrollState == SCROLL_STATE_SETTLING || scrollState == SCROLL_STATE_DRAGGING)) {
                // Stop scroll when user touch down of rv Child is scrolling
                stopScroll()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (childListener != null) {
            when (state) {
                SCROLL_STATE_DRAGGING -> showLog { Log.d(TAG, "$type  onScrollStateChanged SCROLL_STATE_DRAGGING") }
                SCROLL_STATE_IDLE -> {
                    showLog { Log.d(TAG, "$type onScrollStateChanged SCROLL_STATE_IDLE") }
                    // Fling Rv Parent from Child
                    /*val velocityY = getCurrentVelocity()
                    if(velocityY < 0 && !parentDelegate) {
                        flingParent?.invoke(velocityY.toInt())
                    }*/
                    // Stop scroll parent
                    if (stopScrollParent) {
                        childListener?.stopScrollParent()
                    }
                    stopScrollParent = true
                }
                SCROLL_STATE_SETTLING -> showLog { Log.d(TAG, "$type onScrollStateChanged SCROLL_STATE_SETTLING") }
            }
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        showLog { Log.d(TAG, "$type onScrolled $dx $dy") }
        if (childListener != null) {
            calculateDelegate()
        }
    }

    fun calculateDelegate() {
        if (!canScrollVertically(-1)) {
            // scroll to the Top
            childDelegate = false
            showLog { Log.d(TAG, "$type  onScrolled cannot scroll up parentDelegate ${childDelegate}") }
        } else {
            // scroll inside
            childDelegate = true
            showLog { Log.d(TAG, "$type  onScrolled scroll inside parentDelegate ${childDelegate}") }
        }
    }

    fun stopScrollChild() {
        stopScrollParent = false
        stopScroll()
    }
}