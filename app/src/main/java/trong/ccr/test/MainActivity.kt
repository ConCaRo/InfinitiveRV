package trong.ccr.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import trong.ccr.test.ui.parent.ParentFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ParentFragment.newInstance())
                .commit()
        }
    }
}