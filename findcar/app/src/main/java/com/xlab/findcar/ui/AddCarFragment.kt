package com.xlab.findcar.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.xlab.findcar.R
import com.xlab.findcar.database.CarDatabaseHelper
import com.xlab.findcar.model.Car
import com.google.android.material.textfield.TextInputEditText
import android.provider.MediaStore

class AddCarFragment : Fragment() {
    private lateinit var dbHelper: CarDatabaseHelper
    private var selectedImageUri: Uri? = null
    
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            // You might want to show a preview of the selected image
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(context, "이미지 선택 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_car, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = CarDatabaseHelper(requireContext())

        // Initialize input fields
        val nameInput = view.findViewById<TextInputEditText>(R.id.nameInput)
        val departInput = view.findViewById<TextInputEditText>(R.id.departInput)
        val phoneInput = view.findViewById<TextInputEditText>(R.id.phoneInput)
        val plateNumberInput = view.findViewById<TextInputEditText>(R.id.plateNumberInput)
        val modelInput = view.findViewById<TextInputEditText>(R.id.modelInput)

        // Set up image selection
        view.findViewById<View>(R.id.imageButton).setOnClickListener {
            checkAndRequestPermission()
        }

        // Set up save button
        view.findViewById<View>(R.id.saveButton).setOnClickListener {
            val name = nameInput.text.toString()
            val depart = departInput.text.toString()
            val phone = phoneInput.text.toString()
            val plateNumber = plateNumberInput.text.toString()
            val model = modelInput.text.toString()

            if (validateInputs(name, depart, phone, plateNumber, model)) {
                val car = Car(
                    name = name,
                    depart = depart,
                    phone = phone,
                    plateNumber = plateNumber,
                    model = model,
                    image = selectedImageUri?.toString()
                )

                val result = dbHelper.addCar(car)
                if (result != -1L) {
                    Toast.makeText(context, "차량이 성공적으로 등록되었습니다", Toast.LENGTH_SHORT).show()
                    // Navigate back or clear inputs
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(context, "차량 등록에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set up cancel button
        view.findViewById<View>(R.id.cancelButton).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun validateInputs(
        name: String,
        depart: String,
        phone: String,
        plateNumber: String,
        model: String
    ): Boolean {
        if (name.isBlank() || depart.isBlank() || phone.isBlank() || 
            plateNumber.isBlank() || model.isBlank()) {
            Toast.makeText(context, "모든 필드를 입력해주세요", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
} 