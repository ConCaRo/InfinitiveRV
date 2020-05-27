package trong.ccr.test.ui.parent

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import trong.ccr.test.R
import trong.ccr.test.ui.child.ChildFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
    R.string.tab_text_4,
    R.string.tab_text_5
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    val listFragment = ArrayList<Fragment>()

    init {
        listFragment.add(ChildFragment.newInstance())
        listFragment.add(ChildFragment.newInstance())
        listFragment.add(ChildFragment.newInstance())
        listFragment.add(ChildFragment.newInstance())
        listFragment.add(ChildFragment.newInstance())
    }

    fun getFragmentAt(position: Int) = listFragment[position]

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return listFragment.size
    }
}