package com.example.alarm
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_list.*

class MainActivity : AppCompatActivity() {

    private var viewModel: ListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        Thread.sleep(1500)
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val fragmentTransation = supportFragmentManager.beginTransaction()
        fragmentTransation.replace(R.id.contentLayout, AlarmListFragment())
        fragmentTransation.commit()

        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(ListViewModel::class.java)
        }

        fab.setOnClickListener { view ->
            val intent = Intent(applicationContext, AlarmSettingActivity::class.java)
            startActivity(intent)
        }
        /* todo : alarm onoff update*/
    }

}