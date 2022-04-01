package com.sketchboard.android.minipaint

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.sketchboard.mycanvas.R
import java.util.*

//import com.sketchboard.minipaint.R

// Stroke width for the the paint.
private const val STROKE_WIDTH = 12f

/**
 * Custom view that follows touch events to draw on a canvas.
 */

class MyCanvasView(context: Context) : View(context) {

    // Holds the path you are currently drawing.


    var drawColor = ResourcesCompat.getColor(resources, R.color.blueColor, null)
    // Holds the path you are currently drawing.
    private var path:CustomPath? =null;
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.whitecolor, null)
//  To undo canvas drawing
    private val paths = ArrayList<CustomPath>()
    private val undonePaths = ArrayList<CustomPath>()
//
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private lateinit var frame: Rect

    // Set up the paint with which to draw.
     lateinit var paint:Paint;
    lateinit var paint2:Paint;

    //
    private var mBrushSize: Float =
            0.toFloat() // A variable for stroke/brush size to draw on the canvas.

    // A variable to hold a color of the stroke.
    private var color = R.color.blackColor
    //
    init{
        paint= Paint()
        paint2= Paint()
        paint.color=drawColor
        paint.isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        paint.isDither = true
        paint.style = Paint.Style.STROKE // default: FILL
        paint.strokeJoin = Paint.Join.ROUND // default: MITER
        paint.strokeCap = Paint.Cap.ROUND // default: BUTT
        paint2.style = Paint.Style.STROKE // default: FILL
        paint2.strokeJoin = Paint.Join.ROUND // default: MITER
        paint2.strokeCap = Paint.Cap.ROUND // default: BUTT
        paint.strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
        path= CustomPath(color, mBrushSize)
    }
    /**
     * Don't draw every single pixel.
     * If the finger has has moved less than this distance, don't draw. scaledTouchSlop, returns
     * the distance in pixels a touch can wander before we think the user is scrolling.
     */
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
    private var currentX = 0f
    private var currentY = 0f
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    /**
     * Called whenever the view changes size.
     * Since the view starts out with no size, this is also called after
     * the view has been inflated and has a valid size.
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

//        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

        // Calculate a rectangular frame around the picture.
        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (Path in paths) {
            paint2.strokeWidth=Path.strokeWidth;
            paint2.color=Path.color
            canvas?.drawPath(Path, paint2)
        }
        canvas?.drawPath(path!!, paint)
    }
    fun undoCanvasDrawing() {
        if (paths.size > 0) {
            undonePaths.add(paths.removeAt(paths.size - 1))
            invalidate()
        } else {

        }
    }

    fun redoCanvasDrawing() {
        if (undonePaths.size > 0) {
            paths.add(undonePaths.removeAt(undonePaths.size - 1))
            invalidate()
        } else {

        }
    }
    /**
     * No need to call and implement MyCanvasView#performClick, because MyCanvasView custom view
     * does not handle click actions.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }
    /**
     * The following methods factor out what happens for different touch events,
     * as determined by the onTouchEvent() when statement.
     * This keeps the when conditional block
     * concise and makes it easier to change what happens for each event.
     * No need to call invalidate because we are not drawing anything.
     */
    private fun touchStart() {
        path!!.reset()
        
        path!!.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            path!!.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to save it.
            extraCanvas.drawPath(path!!, paint)
        }

        invalidate()
    }

    private fun touchUp() {
//        path.reset()
    path!!.lineTo(currentX, currentY)
    extraCanvas?.drawPath(path!!, paint)
    path!!.color=paint.color
    path!!.strokeWidth=paint.strokeWidth
    paths.add(path!!)

    path = CustomPath(paint.color,paint.strokeWidth)
}
    //create a custom class which inherits Path class so we can add two required params
    internal inner class CustomPath(var color: Int,var strokeWidth:Float) : Path()
}



