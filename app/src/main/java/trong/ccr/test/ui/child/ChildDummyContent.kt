package trong.ccr.test.ui.child

import java.util.ArrayList
import java.util.HashMap

object ChildDummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<DummyChildItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, DummyChildItem> = HashMap()

    private val COUNT = 40

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(
                createDummyItem(i)
            )
        }
    }

    private fun addItem(item: DummyChildItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createDummyItem(position: Int): DummyChildItem {
        return DummyChildItem(
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
    data class DummyChildItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}
