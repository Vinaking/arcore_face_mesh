package com.tunghoang.arcore_face_mesh

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.AugmentedFaceNode
import java.nio.FloatBuffer

class MainActivity : AppCompatActivity() {
    private val MIN_OPENGL_VERSION = 3.0
    private lateinit var arFragment: ArFragment
    private lateinit var faceMeshView: FaceMeshView
    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!checkIsSupportedDeviceOrFinish())
            return
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment
        faceMeshView = findViewById(R.id.canvasView)

        val sceneView = arFragment.arSceneView

        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST

        val scene = sceneView.scene

        scene.addOnUpdateListener {
            val faceList = sceneView.session!!.getAllTrackables(AugmentedFace::class.java)
            // Make new AugmentedFaceNodes for any new faces.
            for (face in faceList) {
                if (!faceNodeMap.containsKey(face)) {
                    val faceNode = AugmentedFaceNode(face)
                    faceNode.setParent(scene)

                    //add light bulb above head
                    ViewRenderable.builder().setView(this, R.layout.idea_view).build()
                        .thenAccept {
                            val lightBulb = Node()
                            val localPosition = Vector3()
                            //lift the light bulb to be just above your head.
                            localPosition.set(0.0f, 0.1f, 0.0f)
                            lightBulb.localPosition = localPosition
                            lightBulb.setParent(faceNode)
                            lightBulb.renderable = it

                        }

                    //give the face a little blush
                    Texture.builder()
                        .setSource(this, R.drawable.blush_texture)
                        .build()
                        .thenAccept { texture ->
                            faceNode.faceMeshTexture = texture
                        }
                    faceNodeMap[face] = faceNode

                }

                val buffer: FloatBuffer = face.meshVertices
                val points = ArrayList<Vector3>()
                for (i in 0..467) {
                    val vector3 = Vector3(
                        buffer[i * 3],
                        buffer[i * 3 + 1], buffer[i * 3 + 2]
                    )
                    val node = Node()
                    node.localPosition = vector3
                    node.setParent(faceNodeMap[face])
                    val worldPosition = node.worldPosition
                    val point = scene.camera.worldToScreenPoint(worldPosition)

                    points.add(point)
                }

                faceMeshView.setPoints(points)
            }

            // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
            val faceIterator = faceNodeMap.entries.iterator()
            while (faceIterator.hasNext()) {
                val entry = faceIterator.next()
                val face = entry.key
                if (face.trackingState == TrackingState.STOPPED) {
                    val faceNode = entry.value
                    faceNode.setParent(null)
                    faceNode.children.clear()
                    faceIterator.remove()
                }
            }
        }
    }

    /*
      Method that checks if AR Core is available on this device.
   */
    private fun checkIsSupportedDeviceOrFinish(): Boolean {
        if (ArCoreApk.getInstance()
                .checkAvailability(this) === ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE
        ) {
            finish()
            return false
        }
        val openGlVersionString = (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            finish()
            return false
        }
        return true
    }
}