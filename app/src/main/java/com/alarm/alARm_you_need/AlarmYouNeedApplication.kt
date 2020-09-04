package com.alarm.alARm_you_need

import android.app.*
import android.util.Log
import io.realm.Realm

class AlarmYouNeedApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Log.d("DEBUGGING LOG", "AlarmYouNeedApplication")
    }

}

/* Todo List
* - 활성화 알람 시간 순 정렬
* - 다음 알람까지 남은 시간 계산
* - BaseRingActivity 구현 ... ArRingActiviy부터 Kt로 바꿔야겠다
*
* */
