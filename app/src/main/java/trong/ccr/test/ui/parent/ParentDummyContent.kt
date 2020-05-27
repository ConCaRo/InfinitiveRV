package trong.ccr.test.ui.parent

import java.util.ArrayList
import java.util.HashMap

object ParentDummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<DummyParentItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, DummyParentItem> = HashMap()

    private val COUNT = 20

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(
                createDummyItem(i)
            )
        }
    }

    private fun addItem(item: DummyParentItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createDummyItem(position: Int): DummyParentItem {
        return DummyParentItem(
            position.toString(),
            "Item " + position,
            makeDetails(position)
        )
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A dummy item representing a piece of content.
     */
    data class DummyParentItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}
