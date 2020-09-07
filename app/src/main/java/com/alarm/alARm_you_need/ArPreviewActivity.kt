package com.alarm.alARm_you_need

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper
import com.google.ar.sceneform.FrameTime
import kotlinx.android.synthetic.main.activity_augmented_image.*
import java.util.HashMap

class ArPreviewActivity : AppCompatActivity() {
    private var arFragment: AugmentedImageFragment? = null
    private val augmentedImageMap: MutableMap<AugmentedImage, AugmentedImageNode> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DEBUGGING LOG", "ArPreview::onCreate is called")
        setContentView(R.layout.activity_augmented_image)

        val targetImageUri = intent.getStringExtra("IMAGE_URI")

        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as AugmentedImageFragment
        arFragment!!.setAugmentedTargetImage(Uri.parse(targetImageUri))

        arFragment!!.arSceneView.scene.addOnUpdateListener { frameTime: FrameTime ->
            onUpdateFrame(frameTime)
        }

        init_btn.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        SnackbarHelper.getInstance().hide(this)
    }

    override fun onResume() {
        super.onResume()
        Log.d("DEBUGGING LOG", "ArPreviewActivity::onResume is called")
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
        }
        if (augmentedImageMap.isEmpty()) {
            image_view_fit_to_scan?.visibility = View.VISIBLE
        }
    }

    private fun onUpdateFrame(frameTime: FrameTime) {
        val frame = arFragment!!.arSceneView.arFrame

        if (frame == null || frame.camera.trackingState != TrackingState.TRACKING) {
            return
        }
        val updatedAugmentedImages =
            frame.getUpdatedTrackables(AugmentedImage::class.java)

        for (augmentedImage in updatedAugmentedImages) {
            when (augmentedImage.trackingState) {
                TrackingState.PAUSED -> {
                    val text = "곧 미로가 생성됩니다. 이 이미지는 사용할 수 있습니다."
                    SnackbarHelper.getInstance().showMessage(this, text)
                }
                TrackingState.TRACKING -> {
                    image_view_fit_to_scan.visibility = View.GONE

                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        val node = AugmentedImageNode(this)
                        node.setImage(augmentedImage, true)
                        augmentedImageMap[augmentedImage] = node
                        arFragment!!.arSceneView.scene.addChild(node)
                    }
                }
            }
        }
    }

}