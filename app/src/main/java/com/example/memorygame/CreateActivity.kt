package com.example.memorygame

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import com.example.memorygame.utils.EXTRA_BOARD_SIZE
import com.example.memorygame.utils.isPermissionGranted
import com.example.memorygame.utils.requestPermission
import java.net.URI

class CreateActivity : AppCompatActivity() {

    private lateinit var boardSize: BoardSize
    private lateinit var imagePickerRV: RecyclerView
    private lateinit var buttonSave: Button
    private lateinit var editTextName: EditText
    private val chosenImageURIs = mutableListOf<Uri>()

    private lateinit var startForResult: ActivityResultLauncher<Intent>


    companion object
    {
        private const val PICK_PHOTO_CODE = 123
        private const val READ_PHOTOS_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // activity for result
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            // some data handling
        }

        // make a back button
        boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        val numPics = boardSize.getNumPairs()
        supportActionBar?.title = "Choose pics(0/$numPics)"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // init all the components
        imagePickerRV = findViewById(R.id.activity_create_recyclerViewImagePicker)
        buttonSave = findViewById(R.id.activity_create_buttonSave)
        editTextName = findViewById(R.id.activity_create_editTextGameSave)

        imagePickerRV.setHasFixedSize(true)
        imagePickerRV.layoutManager = GridLayoutManager(this, boardSize.getWidth())

        imagePickerRV.adapter = ImagePickerAdapter(this, chosenImageURIs, boardSize, object: ImagePickerAdapter.ImageClickListener{
            override fun onPlaceholderClicked() {
                if(isPermissionGranted(this@CreateActivity, READ_PHOTOS_PERMISSION))
                {
                    launchIntentForPhotos()
                }
                else
                {
                    requestPermission(this@CreateActivity, READ_PHOTOS_PERMISSION, PICK_PHOTO_CODE)
                }
            }

        })


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PICK_PHOTO_CODE)
        {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                launchIntentForPhotos()
            }
            else
            {
                Toast.makeText(this, "In order to choose custom images, please allow", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home)
        {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchIntentForPhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startForResult.launch(Intent.createChooser(intent, "Choose pics"))
    }
}