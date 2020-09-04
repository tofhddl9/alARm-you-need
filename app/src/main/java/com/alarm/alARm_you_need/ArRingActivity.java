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

package com.alarm.alARm_you_need;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper;
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper;
import com.google.ar.sceneform.FrameTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

/**
 * This application demonstrates using augmented images to place anchor nodes. app to include image
 * tracking functionality.
 */
public class ArRingActivity extends AppCompatActivity {

    private AugmentedImageFragment arFragment;
    private ImageView fitToScanView;
    private Button initButton;
    private String alarmId;

    private PhysicsController physicsController;
    private Boolean init;
    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("DEBUGGING LOG", "ArRingActivity::onCreate is called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augmented_image);

        alarmId = getIntent().getStringExtra("ALARM_ID");
        Realm realm = Realm.getDefaultInstance();
        assert alarmId != null;
        Log.d("DEBUGGING LOG", "ArRingActivity get alarmId : " +  alarmId);
        String targetImageUri = new AlarmDao(realm).selectAlarm(alarmId).getUriImage();

        arFragment = (AugmentedImageFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arFragment.setAugmentedTargetImage(Uri.parse(targetImageUri));

        fitToScanView = findViewById(R.id.image_view_fit_to_scan);
        initButton = findViewById(R.id.init_btn);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        SetListener();
        init = false;

        if (getSupportActionBar() != null) {
            Log.d("DEBUGGING LOG", "hide back button");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

    }

    public void SetListener() {
        initButton.setOnClickListener(view -> init = true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DEBUGGING LOG", "ArRingActivity::onResume is called");
        // ARCore requires camera permission to operate.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
        }

        if (augmentedImageMap.isEmpty()) {
            fitToScanView.setVisibility(View.VISIBLE);
            initButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("DEBUGGING LOG", "ArRingActivity::onKeyDown() is called");
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d("DEBUGGING LOG", "ArRingActivity::onBackPressed()");
        //nop
    }

    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.d("DEBUGGING LOG", "ArRingActivity::onUserLeaveHint()");

        if (AlarmService.service != null) {
            Intent intent = new Intent(this, ArRingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("ALARM_ID", alarmId);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("DEBUGGING LOG", "ArRingActivity::onDestroy is called");

        if (!AlarmService.normalExit) {
            Log.d("DEBUGGING LOG", "ArRingActivity ...  abnormal exit");
            if (AlarmService.service != null) {
                stopService(AlarmService.service);
            }
        }
        else {
            Log.d("DEBUGGING LOG", "ArRingActivity ... normal exit");
            if (AlarmService.service != null) {
                stopService(AlarmService.service);
                AlarmService.service = null;
            }
        }
    }

    /**
     * Registered with the Sceneform Scene object, this method is called at the start of each frame.
     *
     * @param frameTime - time since last frame.
     */
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame or ARCore is not tracking yet, just return.
        if (frame == null || frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    String text = "Detected Image " + augmentedImage.getIndex();
                    SnackbarHelper.getInstance().showMessage(this, text);
                    break;

                case TRACKING:
                    // Have to switch to UI Thread to update View.
                    fitToScanView.setVisibility(View.GONE);
                    initButton.setVisibility(View.VISIBLE);

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        AugmentedImageNode node = new AugmentedImageNode(this);
                        node.setImage(augmentedImage);
                        augmentedImageMap.put(augmentedImage, node);
                        arFragment.getArSceneView().getScene().addChild(node);

                        physicsController = new PhysicsController(this);


                    } else {
                        // If the image anchor is already created
                        AugmentedImageNode node = augmentedImageMap.get(augmentedImage);

                        Pose ball_pose;
                        if (init) {
                            physicsController.DeleteBallRigidBody();

                            node.AddBall();
                            ball_pose = physicsController.getBallPose(true);
                            physicsController.AddBallRigidBody();
                            init = false;
                        }
                        else {
                            ball_pose = physicsController.getBallPose(false);
                        }

                        node.updateBallPose(ball_pose);
                        if (physicsController.isEscape(ball_pose)) {
                            AlarmRingOff();
                        }

                        Pose worldGravityPose = Pose.makeTranslation(0, -9.8f, 0);
                        Pose mazeGravityPose = augmentedImage.getCenterPose().inverse().compose(worldGravityPose);
                        float[] mazeGravity = mazeGravityPose.getTranslation();
                        physicsController.applyGravityToBall(mazeGravity);

                        physicsController.updatePhysics();
                    }
                    break;

                case STOPPED:
                    AugmentedImageNode node = augmentedImageMap.get(augmentedImage);
                    augmentedImageMap.remove(augmentedImage);
                    arFragment.getArSceneView().getScene().removeChild(node);
                    break;
            }
        }
    }

    private void AlarmRingOff() {
        stopService(AlarmService.service);
        AlarmService.service = null;
        AlarmService.normalExit = true;

        Intent intent = new Intent(this, GoodMorningActivity.class);
        intent.putExtra("ALARM_ID", alarmId);
        startActivity(intent);
    }

}
