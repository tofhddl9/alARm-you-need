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

package com.example.alarm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This application demonstrates using augmented images to place anchor nodes. app to include image
 * tracking functionality.
 */
public class AugmentedImageActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ImageView fitToScanView;
    private Button initButton;

    private PhysicsController physicsController;
    private Boolean init;
    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augmented_image);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        fitToScanView = findViewById(R.id.image_view_fit_to_scan);
        initButton = findViewById(R.id.init_btn);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        SetListener();
        init = false;
    }

    public void SetListener() {
        initButton.setOnClickListener(view -> init = true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (augmentedImageMap.isEmpty()) {
            fitToScanView.setVisibility(View.VISIBLE);
            initButton.setVisibility(View.GONE);
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
                            // @TODO : Escape Logic
                            init = true;
                        }
                        // Use real world gravity, (0, -10, 0) as gravity
                        // Convert to Physics world coordinate (because Maze mesh has to be static)
                        // Use it as a force to move the ball

                        // Replace this line to the code below
                        // Pose worldGravityPose = Pose.makeTranslation(0, -20f, 0);
                        Pose worldGravityPose = Pose.makeTranslation(0, -9.8f, 0);
                        Pose mazeGravityPose = augmentedImage.getCenterPose().inverse().compose(worldGravityPose);
                        float mazeGravity[] = mazeGravityPose.getTranslation();
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
}
