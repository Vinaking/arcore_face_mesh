package com.tunghoang.arcore_face_mesh

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.ar.sceneform.math.Vector3

class FaceMeshView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var points = ArrayList<Vector3>()
    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    fun setPoints(points: ArrayList<Vector3>) {
        this.points = points
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (point in points) {
            canvas.drawCircle(point.x, point.y, 5f, paint)
        }
    }
}