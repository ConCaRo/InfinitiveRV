package trong.ccr.test.ui.child

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import trong.ccr.test.R
import trong.ccr.test.ui.RecyclerViewTouchChild

/**
 * A Child fragment contains [RecyclerViewTouchChild]
 */
class ChildFragment : Fragment() {

    var root: RecyclerViewTouchChild? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_child, container, false)
        root = view as RecyclerViewTouchChild?
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = ChildRcAdapter(ChildDummyContent.ITEMS)
            }
        }
        return view
    }

    fun getRcChild(): RecyclerViewTouchChild = root as RecyclerViewTouchChild

    companion object {
        @JvmStatic
        fun newInstance() = ChildFragment()
    }
}
