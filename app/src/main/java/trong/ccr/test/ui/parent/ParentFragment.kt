package trong.ccr.test.ui.parent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import trong.ccr.test.R
import trong.ccr.test.ui.RecyclerViewTouchChild
import trong.ccr.test.ui.RecyclerViewTouchParent

/**
 * A Parent fragment contains [RecyclerViewTouchParent]
 */
class ParentFragment : Fragment(), RecyclerViewTouchParent.IParent {

    private var rvParent: RecyclerViewTouchParent? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parent, container, false)
        rvParent = view.findViewById(R.id.list)
        rvParent?.setListener(this)
        // Set the adapter
        if (rvParent is RecyclerView) {
            rvParent?.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ParentRvAdapter(
                    context,
                    childFragmentManager,
                    ParentDummyContent.ITEMS,
                    true, ::delegateScrollingView
                )
            }
        }
        return view
    }

    // Delegate scrolling view
    private fun delegateScrollingView(recyclerView: RecyclerViewTouchChild) {
        rvParent?.delegateView = recyclerView
    }

    companion object {
        @JvmStatic
        fun newInstance() = ParentFragment()
    }

    override fun triggerChildTouchTop(touchTop: Boolean) {
        Log.e("OLALA", "Trigger touch top ${touchTop}")
    }
}
