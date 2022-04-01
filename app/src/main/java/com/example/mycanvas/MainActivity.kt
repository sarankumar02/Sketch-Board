package com.example.mycanvas

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Bitmap.*
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.*
import androidx.core.content.res.ResourcesCompat
import com.example.android.minipaint.MyCanvasView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        var mycanvas = MyCanvasView(this);
        var paintColor = ResourcesCompat.getColor(resources, R.color.redColor, null)
        mycanvas.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        mycanvas.contentDescription = getString(R.string.canvasContentDescription)
        setContentView(R.layout.activity_main)

        var frameView = findViewById<FrameLayout>(R.id.frame);
        frameView.addView(mycanvas)
        mycanvas.paint.setColor(paintColor)
        var redButton = findViewById<Button>(R.id.red);
        var blueButton = findViewById<Button>(R.id.blue);
        var greenButton = findViewById<Button>(R.id.green);
        var purpleButton = findViewById<Button>(R.id.purple);
        var pinkButton = findViewById<Button>(R.id.pink);
        var blackButton = findViewById<Button>(R.id.black);
        var eraser = findViewById<ImageButton>(R.id.eraser);
        var strokeWidth = findViewById<ImageButton>(R.id.stroke);
        var undoButton = findViewById<ImageButton>(R.id.undo);
        var saveButton = findViewById<ImageButton>(R.id.saveButton);


//
//        var stroke2 = findViewById<Button>(R.id.stroke2);
//        var stroke3 = findViewById<Button>(R.id.stroke3);
//
        redButton.setOnClickListener {
            mycanvas.paint.setColor(ResourcesCompat.getColor(resources, R.color.redColor, null))
        }
        blueButton.setOnClickListener {
            mycanvas.paint.setColor(ResourcesCompat.getColor(resources, R.color.blueColor, null))
        }
        pinkButton.setOnClickListener {
            mycanvas.paint.setColor(ResourcesCompat.getColor(resources, R.color.pinkColor, null))
        }
        greenButton.setOnClickListener {
            mycanvas.paint.setColor(ResourcesCompat.getColor(resources, R.color.greenColor, null))
        }
        purpleButton.setOnClickListener {
            mycanvas.paint.setColor(ResourcesCompat.getColor(resources, R.color.purpleColor, null))
        }
        blackButton.setOnClickListener {
            mycanvas.paint.setColor(ResourcesCompat.getColor(resources, R.color.blackColor, null))
        }
        eraser.setOnClickListener {
            mycanvas.paint.setColor(ResourcesCompat.getColor(resources, R.color.whitecolor, null))
        }
        undoButton.setOnClickListener {
            mycanvas.undoCanvasDrawing()
        }
        saveButton.setOnClickListener {
            //
            // write permission to access the storage
            requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            //
            val bitmap = getScreenShotFromView(findViewById<FrameLayout>(R.id.frame));
            var result=saveImageToDownloadFolder(bitmap!!)
            Log.i("BITMAp", "THIS ---" + result.toString())
        }
        strokeWidth.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .create()
            val view = layoutInflater.inflate(R.layout.custom_alert_dialog, null)
            builder.setView(view)
            var strokeButton1 = view.findViewById<Button>(R.id.stroke1);
            var strokeButton2 = view.findViewById<Button>(R.id.stroke2);
            var strokeButton3 = view.findViewById<Button>(R.id.stroke3);

            strokeButton1.setOnClickListener{
                mycanvas.paint.strokeWidth=8.0F;
                builder.dismiss()
            }
            strokeButton2.setOnClickListener{
                mycanvas.paint.strokeWidth=16.0F;
                builder.dismiss()
            }
            strokeButton3.setOnClickListener{
                mycanvas.paint.strokeWidth=26.0F;
                builder.dismiss()
            }
            builder.setCanceledOnTouchOutside(true)
            builder.show()
        }
    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot = createBitmap(v.measuredWidth, v.measuredHeight, Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot
    }
    fun saveImageToDownloadFolder(ibitmap: Bitmap) {
        try {
            var imageFile = "${System.currentTimeMillis()}.jpg";
            val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), imageFile)
            val outputStream: OutputStream = FileOutputStream(filePath)
            ibitmap.compress(CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(this@MainActivity, imageFile + "Sucessfully saved in Download Folder", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT).show()

        }
    }

}