package com.example.paint

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import com.divyanshu.colorseekbar.ColorSeekBar
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.divyanshu.draw.activity.DrawingActivity

import com.github.tbouron.shakedetector.library.ShakeDetector
import java.io.File
import java.io.FileOutputStream
import java.util.*

private const val REQUEST_CODE_DRAW = 101

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clearAll.setOnClickListener {
            draw_view.clearCanvas()
        }

        undo.setOnClickListener{
            draw_view.undo()
        }

        redo.setOnClickListener{
            draw_view.redo()
        }

        save_image.setOnClickListener{
            showSaveDialog()
        }

        color_seek_bar.setOnColorChangeListener(object: ColorSeekBar.OnColorChangeListener{
            override fun onColorChangeListener(color: Int) {
                draw_view.setColor(color)
            }
        })

        seekBar.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {}

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int,
                    fromUser: Boolean
                ) {
                    val min = 1
                    val step = 1
                    val value = min + progress * step

                    draw_view.setStrokeWidth(value.toFloat())

                }
            }
        )

        ShakeDetector.create(this) {
            draw_view.clearCanvas()
            Toast.makeText(applicationContext, "Removed", Toast.LENGTH_SHORT).show()
        }
    }


    fun showSaveDialog() {

            val alertDialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_save, null)
            alertDialog.setView(dialogView)
            val fileNameEditText: EditText = dialogView.findViewById(R.id.editText_file_name)
            val filename = UUID.randomUUID().toString()
            fileNameEditText.setSelectAllOnFocus(true)
            fileNameEditText.setText(filename)
            alertDialog.setTitle("Save Drawing")
                .setPositiveButton("ok") { _, _ -> saveImage(fileNameEditText.text.toString()) }
                .setNegativeButton("Cancel") { _, _ -> }

            val dialog = alertDialog.create()
            dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.show()

    }

    fun saveImage(name: String){

        hasStoragePermission()

        val file = File(path, "$name.png")
        path.mkdirs()
        file.createNewFile()
        val outputStream = FileOutputStream(file)
        val bitmap = draw_view.getBitmap()
            .apply {
                compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        outputStream.flush()
        outputStream.close()
        MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null, null)

        Toast.makeText(applicationContext, "Image saved", Toast.LENGTH_SHORT).show()
    }

    private fun hasStoragePermission() {

        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        } else {
        }

    }

    companion object {
        private val fileDir = "${Environment.DIRECTORY_PICTURES}/AndroidPaint/"
        private val path = Environment.getExternalStoragePublicDirectory(fileDir)
    }






}
