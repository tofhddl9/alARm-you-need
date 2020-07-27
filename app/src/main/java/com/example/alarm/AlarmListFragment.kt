package com.example.alarm
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_alarm_list.*

class AlarmListFragment: Fragment(){
    private lateinit var listAdapter: AlarmListAdapter
    private var viewModel: ListViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = requireActivity().application!!.let {
            ViewModelProvider(
                requireActivity().viewModelStore,
                ViewModelProvider.AndroidViewModelFactory(it))
                .get(ListViewModel::class.java)
        }

        viewModel!!.let {
            it.alarmLiveData.value?.let {
                listAdapter = AlarmListAdapter(it)
                alarmListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                alarmListView.adapter = listAdapter
                listAdapter.itemClickListener = {
                    val intent = Intent(activity, AlarmSettingActivity::class.java)
                    intent.putExtra("ALARM_ID", it)
                    Log.d("StartActivity", "Clicked")
                    startActivity(intent)
                }

                listAdapter.checkBoxClickListner = {
                    /* true : for debug*/
                    viewModel!!.toggleAlarm(it, true)
                    Log.d("alarm toggle","success")
                }

            }
            it.alarmLiveData.observe(viewLifecycleOwner,
                Observer {
                    listAdapter.notifyDataSetChanged()
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        listAdapter.notifyDataSetChanged()
    }
}