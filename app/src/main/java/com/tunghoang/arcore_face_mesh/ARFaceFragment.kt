package com.tunghoang.arcore_face_mesh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import java.util.*

class ARFaceFragment : ArFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentView = super.onCreateView(inflater, container, savedInstanceState)
        //hide some of the plane finders and discovery since its not needed for augmented images.
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        return fragmentView!!
    }

    /**
     * Enable Augmented Face Mode for the ARCore Config.
     */
    override fun getSessionConfiguration(session: Session): Config {
        val config = Config(session)
        config.augmentedFaceMode = Config.AugmentedFaceMode.MESH3D
        return config
    }

    /**
     * Tell ARCore that we need to use the front camera.
     */
    override fun getSessionFeatures(): Set<Session.Feature> {
        return EnumSet.of<Session.Feature>(Session.Feature.FRONT_CAMERA)
    }
}