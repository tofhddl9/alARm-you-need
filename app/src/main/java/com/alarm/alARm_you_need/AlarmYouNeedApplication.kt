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
* - ARCore, Sceneform 버전 확인해보자... 사용중인 버전 성능이 안 좋다
*
* */
