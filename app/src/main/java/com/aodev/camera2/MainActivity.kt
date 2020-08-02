package com.aodev.camera2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


private const val REQUEST_CAMERA_CODE = 98
private const val REQUEST_GALLERY_CODE = 99
private val PERMISSION_CAMERA_CODE = 100;
private val PERMISSION_GALLERY_CODE = 101;

private const val FILE_NAME = "photo.jpg"
private lateinit var photoFile: File

class MainActivity : AppCompatActivity() {
    private val CAMERA = Manifest.permission.CAMERA
    private val GALLERY = Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_Capture.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(CAMERA) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    val permissions = arrayOf(CAMERA)
                    requestPermissions( arrayOf(CAMERA), PERMISSION_CAMERA_CODE)
                } else {
                    dispatchImageFromCamera();
                }
            } else {
                dispatchImageFromCamera()
            }
        }

        btn_Gallery.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(GALLERY) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    val permissions = arrayOf(GALLERY);
                    requestPermissions(permissions, PERMISSION_GALLERY_CODE);
                } else {
                    dispatchImageFromGallery();
                }
            } else {
                dispatchImageFromGallery()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_CODE) {
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchImageFromCamera()
            } else {
                Toast.makeText(this, "Permission denied CAMERA", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == PERMISSION_GALLERY_CODE) {
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchImageFromGallery()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchImageFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(FILE_NAME)
        val fileProvider =
            FileProvider.getUriForFile(this, "edu.stanford.rkpandey.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE)
        } else {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dispatchImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY_CODE)
    }

    private fun getPhotoFile(fileName: String): File {
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
//            val takenImage = data?.extras?.get("data") as Bitmap
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageView.setImageBitmap(takenImage)
        } else if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(data?.data)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}