package trong.ccr.test.ui.parent

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.item_list_parent.view.*
import kotlinx.android.synthetic.main.item_pager.view.*
import trong.ccr.test.R
import trong.ccr.test.ui.RecyclerViewTouchChild
import trong.ccr.test.ui.child.ChildFragment
import trong.ccr.test.ui.parent.ParentDummyContent.DummyParentItem

class ParentRvAdapter(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val mValues: List<DummyParentItem>,
    private val hasViewpager: Boolean = true,
    val delegateView: (RecyclerViewTouchChild) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DATA = Int.MAX_VALUE - 1
        private const val VIEW_TYPE_VIEWPAGER = Int.MAX_VALUE - 3
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasViewpager && position == itemCount - 1) {
            VIEW_TYPE_VIEWPAGER
        } else {
            VIEW_TYPE_DATA
        }
    }

    override fun getItemCount(): Int = mValues.size + if (hasViewpager) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent?.context)
        return when (viewType) {
            VIEW_TYPE_VIEWPAGER -> {
                val view = layoutInflater.inflate(R.layout.item_pager, parent, false)
                ViewpagerViewHolder(view)
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_list_parent, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewpagerViewHolder -> holder.bindData()
            is ItemViewHolder -> holder.bind(mValues[position])
        }
    }

    inner class ItemViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        private val mIdView: TextView = mView.item_number
        private val mContentView: TextView = mView.content

        fun bind(item: DummyParentItem) {
            mIdView.text = item.id
            mContentView.text = item.content

            with(mView) {
                tag = item
            }
        }

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }

    inner class ViewpagerViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val viewPager: ViewPager = mView.view_pager
        val tabs: TabLayout = mView.tabs
        var sectionsPagerAdapter: SectionsPagerAdapter? = null

        fun bindData() {
            if (sectionsPagerAdapter == null) {
                sectionsPagerAdapter =
                    SectionsPagerAdapter(
                        context,
                        fragmentManager
                    )
                viewPager.adapter = sectionsPagerAdapter
                tabs.setupWithViewPager(viewPager)

                viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) { }
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int) {
                        if (position == viewPager?.currentItem && positionOffsetPixels == 0) {
                            delegateView((sectionsPagerAdapter?.getFragmentAt(position) as ChildFragment).getRcChild())
                        }
                    }

                    override fun onPageSelected(position: Int) {
                    }
                })
                // first time delegate view
                viewPager.postDelayed({
                    delegateView((sectionsPagerAdapter?.getFragmentAt(0) as ChildFragment).getRcChild())
                }, 200)
            }
        }
    }
}
