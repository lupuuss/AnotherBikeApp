package ga.lupuss.anotherbikeapp.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginActivity
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager



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

    override fun finishActivity() {
        finish()
    }

    override fun isOnline(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}