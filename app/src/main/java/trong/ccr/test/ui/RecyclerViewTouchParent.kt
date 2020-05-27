package trong.ccr.test.ui

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import trong.ccr.test.ui.parent.ParentRvAdapter
import kotlin.math.max
import kotlin.math.min

/**
 * Custom RV for Online RV supporting infinitive scrolling of list - called RV Parent
 * Responsible for delegate touch event to [RecyclerViewTouchChild] - RV Child
 */
class RecyclerViewTouchParent : RecyclerView, RecyclerViewTouchChild.IChild {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int ) :  super(context, attrs, defStyle)

    companion object {
        const val TAG = "RecyclerView"
        const val type: String = "Parent"
    }
    var delegateView: RecyclerViewTouchChild? = null

    private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var touchDownY = 0                              // Store Touch Down Y coordinate for calculating scroll up/down for first time touch

    private var delegate = false                            // Check whether delegate to Child Rv

    private var lastChildDelegate = true                    // store last delegate of Child Rv
    private var lastDelegate = true                         // store last delegate of Parent Rv
    private var lastEvent: MotionEvent? = null              // store last MotionEvent

    private var canFlingChild = false                       // Check whether fling Child Rv from Parent Rv

    private var listener: IParent? = null

    interface IParent {
        fun triggerChildTouchTop(touchTop: Boolean)
    }

    fun setListener(listener: IParent) {
        this.listener = listener
    }

    init {
        onFlingListener = object: OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                showLog { Log.d(TAG, "$type onFling $velocityX $velocityY") }
                return false
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        showLog { Log.d(TAG, "$type dispatchTouchEvent " + event?.showAction()) }
        if (event?.action == MotionEvent.ACTION_UP) {
            // resetDelegate() -> redundant
            touchDownY = 0
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        showLog { Log.d(TAG, "$type onInterceptTouchEvent ${event?.showAction()} ${event?.x}  ${event?.y}") }
        if(event?.action == MotionEvent.ACTION_DOWN && touchDownY == 0) {
            touchDownY = event?.y.toInt()
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        showLog { Log.d(TAG, "$type onTouchEvent ${event?.showAction()} ${event?.x}  ${event?.y}") }
        if (event.action == MotionEvent.ACTION_MOVE) {  // First time user touch and scroll up/down
            if (touchDownY != 0 && scrollState == SCROLL_STATE_DRAGGING) {
                val y = (event.y + 0.5f).toInt()
                var dy = touchDownY - y
                dy = if (dy > 0) {
                    max(0, dy - touchSlop)
                } else {
                    min(0, dy + touchSlop)
                }
                Log.v(TAG, "$type onTouchEvent dy ${dy}")
                when {
                    dy == 0 -> {
                        // no drag
                        showLog { Log.d(TAG, "$type onTouchEvent scrolling no") }
                    }
                    dy > 0 -> {
                        showLog { Log.d(TAG, "$type onTouchEvent scrolling down") }
                        calculateDelegate(0, true)
                        delegateView?.calculateDelegate()
                    }
                    dy < 0 -> {
                        showLog { Log.d(TAG, "$type onTouchEvent scrolling up") }
                        calculateDelegate(0, false)
                        delegateView?.calculateDelegate()
                    }
                }

                // Solve touch down from parent -> different touch down from child when delegating
                // Simulate action Down for Child from beginning touch: stop Child scrolling
                if (lastEvent != null && lastEvent?.action == MotionEvent.ACTION_MOVE && (delegate || delegateView?.childDelegate == true)) {
                    lastEvent?.action = MotionEvent.ACTION_DOWN
                    delegateView?.onTouchEvent(lastEvent!!)
                }
                touchDownY = 0
            } else {       // when touch and scrolling up/down
                // Simulate action Down Child from Child to Parent: prevent jump in scroll of parent
                if (lastChildDelegate && delegateView?.childDelegate == false) {
                    calculateDelegate(0)
                    // Simulate action Down for Parent
                    if (lastEvent?.action == MotionEvent.ACTION_MOVE) {
                        lastEvent?.action = MotionEvent.ACTION_DOWN
                        showLog { Log.d(TAG, "$type onTouchEvent child to parent") }
                        // stop scroll Child
                        delegateView?.stopScrollChild()
                        super.onTouchEvent(lastEvent)
                    }
                }
                // Simulate action Down Child from Parent to Child: prevent jump in scroll of child
                if (!lastDelegate && delegate) {
                    delegateView?.calculateDelegate()
                    // Simulate action Down for Child
                    if (lastEvent != null && lastEvent?.action == MotionEvent.ACTION_MOVE) {
                        lastEvent?.action = MotionEvent.ACTION_DOWN
                        showLog { Log.d(TAG, "$type  onTouchEvent parent to child") }
                        delegateView?.onTouchEvent(lastEvent!!)
                    }
                }
            }
        }
        // Store last Event, last Delegate, last ChildDelegate
        lastChildDelegate = delegateView?.childDelegate ?: false
        lastDelegate = delegate
        lastEvent = event

        showLog { Log.d(TAG, "$type onTouchEvent ${event?.showAction()} delegate ${delegate}  parentDelegate ${delegateView?.childDelegate}") }
        if (delegateView != null && (delegate || delegateView?.childDelegate == true)) {
            delegateView?.onTouchEvent(event)
            reset(event)
            return true
        } else {
            reset(event)
            return super.onTouchEvent(event)
        }
    }

    // Reset listener
    private fun reset(event: MotionEvent?) {
        if(event?.action == MotionEvent.ACTION_UP) {
            resetDelegate()
            delegateView?.setListener(this)
        }
    }

    // Reset all when action is UP
    private fun resetDelegate() {
        showLog { Log.d(TAG, "$type resetDelegate") }
        delegate = false
        delegateView?.childDelegate = false
        canFlingChild = true
        touchDownY = 0
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        when(state) {
            SCROLL_STATE_DRAGGING -> {
                showLog { Log.d(TAG, "$type onScrollStateChanged SCROLL_STATE_DRAGGING") }
                // detect dragging of parent
                delegateView?.calculateDelegate()
                calculateDelegate(0)
            }
            SCROLL_STATE_IDLE -> {
                showLog { Log.d(TAG, "$type onScrollStateChanged SCROLL_STATE_IDLE") }
                val mCurrVelocity = getCurrentVelocityY()
                delegateView?.calculateDelegate()
                calculateDelegate(1)
                if (mCurrVelocity > 0 && delegate && delegateView?.childDelegate == false && canFlingChild) {
                    // Fling Child from Parent
                    showLog { Log.d(TAG, "$type onScrollStateChanged FLING CHILD") }
                    delegateView?.fling(0, mCurrVelocity.toInt())
                    resetDelegate()
                }
            }
            SCROLL_STATE_SETTLING -> Log.d(TAG, "$type onScrollStateChanged SCROLL_STATE_SETTLING")
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        showLog { Log.d(TAG, "$type onScrolled $dx $dy") }
        calculateDelegate(dy)
    }

    private fun calculateDelegate(dy: Int, scrollingDown: Boolean = false) {
        val lastVisibleChild = getChildAt(childCount - 1)
        val lastVisiblePos = getChildAdapterPosition(lastVisibleChild)
        val total = adapter?.itemCount ?: 0
        val viewpagerViewHolder = findViewHolderForAdapterPosition(adapter?.itemCount!! - 1) as? ParentRvAdapter.ViewpagerViewHolder
        if(viewpagerViewHolder != null) {
            val rectTabs = Rect()
            viewpagerViewHolder?.mView?.getGlobalVisibleRect(rectTabs)
            val rectRecyclerView = Rect()
            this?.getGlobalVisibleRect(rectRecyclerView)
            if(rectTabs.top == rectRecyclerView.top) {
                if (dy != 0) {
                    delegate = true
                    showLog { Log.d(TAG, "$type onScrolled touch top ${rectTabs.top} ${rectTabs.left}") }
                } else if (dy == 0 && scrollingDown) {
                    delegate = true
                    showLog { Log.d(TAG, "$type stop srolling down ${rectTabs.top} ${rectTabs.left}") }
                } else {
                    delegate = false
                }
                listener?.triggerChildTouchTop(true)
            } else {
                listener?.triggerChildTouchTop(false)
            }

            if (total > 0 && lastVisiblePos >= (total - 1) && !canScrollVertically(1)) {
                showLog { Log.d(TAG, "$type onScrolled cannot Scroll down state ${scrollState}") }
            }
        }
    }

    override fun stopScrollParent() {
        // set scroll state  = Idle when Paren/Child completely scroll
        // stop scroll from Child prevent trigger FLING child again?
        if (scrollState != SCROLL_STATE_IDLE) {
            // Really just need to set State = SCROLL_STATE_IDLE, but cannot because of no public method
            showLog { Log.d(TAG, "$type stopScrollParent FORCE STOP") }
            canFlingChild = false
            stopScroll()
        }
        /*} else {
            // prevent loop call stop scroll with call fling from child manually
            // sometimes happens error cannot touch again
            delegateView?.stopScrollParent = null
        }*/
    }

    override fun flingChildToParent(velocityY: Int) {
        // Fling Parent from Child
        /*delegateView?.flingParent = {
            calculateDelegate(0, true)
            if(delegate) {
                Log.d(TAG, "$type onScrollStateChanged FLING PARENT")
                fling(0, it)
            }
            resetDelegate()
        }*/
    }
}