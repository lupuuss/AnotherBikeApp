package ga.lupuss.anotherbikeapp.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast

abstract class BaseActivity : AppCompatActivity(), BaseView {

    private lateinit var toast: Toast


    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toast = Toast.makeText(this, "empty", Toast.LENGTH_LONG)
    }

    fun activateToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {

            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun makeToast(str: String) {
        toast.setText(str)
        toast.show()
    }

    override fun makeToast(stringId: Int) {
        toast.setText(stringId)
        toast.show()
    }
}