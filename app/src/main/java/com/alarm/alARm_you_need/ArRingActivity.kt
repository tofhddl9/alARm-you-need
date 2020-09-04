/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alarm.alARm_you_need

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper
import com.google.ar.sceneform.FrameTime
import io.realm.Realm
import java.util.*

/**
 * This application demonstrates using augmented images to place anchor nodes. app to include image
 * tracking functionality.
 */
class ArRingActivity : AppCompatActivity() {
    private var arFragment: AugmentedImageFragment? = null
    private var fitToScanView: ImageView? = null
    private var initButton: Button? = null
    private var alarmId: String? = null
    private var physicsController: PhysicsController? = null
    private var init: Boolean? = null

    // Augmented image and its associated center pose anchor, keyed by the augmented image in the database.
    private val augmentedImageMap: MutableMap<AugmentedImage, AugmentedImageNode> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUGGING LOG", "ArRingActivity::onCreate is called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_augmented_image)

        alarmId = intent.getStringExtra("ALARM_ID")
        val realm = Realm.getDefaultInstance()
        Log.d("DEBUGGING LOG", "ArRingActivity get alarmId : $alarmId")
        val targetImageUri = AlarmDao(realm).selectAlarm(alarmId!!).uriImage

        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as AugmentedImageFragment
        arFragment!!.setAugmentedTargetImage(Uri.parse(targetImageUri))

        fitToScanView = findViewById(R.id.image_view_fit_to_scan)
        initButton = findViewById(R.id.init_btn)

        arFragment!!.arSceneView.scene.addOnUpdateListener { frameTime: FrameTime ->
            onUpdateFrame(frameTime)
        }
        SetListener()
        init = false

        if (supportActionBar != null) {
            Log.d("DEBUGGING LOG", "hide back button")
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.setHomeButtonEnabled(false)
        }
    }

    fun SetListener() { initButton!!.setOnClickListener { init = true }}

    override fun onResume() {
        super.onResume()
        Log.d("DEBUGGING LOG", "ArRingActivity::onResume is called")
        // ARCore requires camera permission to operate.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
        }
        if (augmentedImageMap.isEmpty()) {
            fitToScanView!!.visibility = View.VISIBLE
            initButton!!.visibility = View.GONE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.d("DEBUGGING LOG", "ArRingActivity::onKeyDown() is called")
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
            )
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        Log.d("DEBUGGING LOG", "ArRingActivity::onBackPressed()")
        //nop
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d("DEBUGGING LOG", "ArRingActivity::onUserLeaveHint()")
        if (AlarmService.service != null) {
            val intent = Intent(this, ArRingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("ALARM_ID", alarmId)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DEBUGGING LOG", "ArRingActivity::onDestroy is called")
        if (!AlarmService.normalExit) {
            Log.d("DEBUGGING LOG", "ArRingActivity ...  abnormal exit")
            if (AlarmService.service != null) {
                stopService(AlarmService.service)
            }
        } else {
            Log.d("DEBUGGING LOG", "ArRingActivity ... normal exit")
            if (AlarmService.service != null) {
                stopService(AlarmService.service)
                AlarmService.service = null
            }
        }
    }

    /**
     * Registered with the Sceneform Scene object, this method is called at the start of each frame.
     * @param frameTime - time since last frame.
     */
    private fun onUpdateFrame(frameTime: FrameTime) {
        val frame = arFragment!!.arSceneView.arFrame

        // If there is no frame or ARCore is not tracking yet, just return.
        if (frame == null || frame.camera.trackingState != TrackingState.TRACKING) {
            return
        }
        val updatedAugmentedImages =
            frame.getUpdatedTrackables(AugmentedImage::class.java)

        for (augmentedImage in updatedAugmentedImages) {
            when (augmentedImage.trackingState) {
                TrackingState.PAUSED -> {
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    val text = "Detected Image " + augmentedImage.index
                    SnackbarHelper.getInstance().showMessage(this, text)
                }
                TrackingState.TRACKING -> {
                    // Have to switch to UI Thread to update View.
                    fitToScanView!!.visibility = View.GONE
                    initButton!!.visibility = View.VISIBLE

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        val node = AugmentedImageNode(this)
                        node.image = augmentedImage
                        augmentedImageMap[augmentedImage] = node
                        arFragment!!.arSceneView.scene.addChild(node)
                        physicsController = PhysicsController(this)
                    } else {
                        // If the image anchor is already created
                        val node = augmentedImageMap[augmentedImage]
                        var ball_pose: Pose?

                        if (init!!) {
                            physicsController!!.DeleteBallRigidBody()
                            node!!.AddBall()
                            ball_pose = physicsController!!.getBallPose(true)
                            physicsController!!.AddBallRigidBody()
                            init = false
                        } else {
                            ball_pose = physicsController!!.getBallPose(false)
                        }

                        node!!.updateBallPose(ball_pose)
                        if (physicsController!!.isEscape(ball_pose)) {
                            AlarmRingOff()
                        }
                        val worldGravityPose = Pose.makeTranslation(0f, -9.8f, 0f)
                        val mazeGravityPose = augmentedImage.centerPose.inverse().compose(worldGravityPose)
                        val mazeGravity = mazeGravityPose.translation
                        physicsController!!.applyGravityToBall(mazeGravity)
                        physicsController!!.updatePhysics()
                    }
                }
                TrackingState.STOPPED -> {
                    val node = augmentedImageMap[augmentedImage]
                    augmentedImageMap.remove(augmentedImage)
                    arFragment!!.arSceneView.scene.removeChild(node)
                }
            }
        }
    }

    private fun AlarmRingOff() {
        stopService(AlarmService.service)
        AlarmService.service = null
        AlarmService.normalExit = true
        finish()

        val intent = Intent(this, GoodMorningActivity::class.java)
        intent.putExtra("ALARM_ID", alarmId)
        startActivity(intent)
    }
}