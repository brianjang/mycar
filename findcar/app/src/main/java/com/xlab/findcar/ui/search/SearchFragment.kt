package com.xlab.findcar.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.xlab.findcar.R
import com.xlab.findcar.databinding.FragmentSearchBinding
import com.xlab.findcar.ui.base.BaseFragment
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import com.xlab.findcar.database.CarDatabaseHelper
import com.xlab.findcar.model.Car
import android.widget.TextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText

class SearchFragment : BaseFragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var dbHelper: CarDatabaseHelper
    private val allCars = mutableListOf<Car>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViews() {
        dbHelper = CarDatabaseHelper(requireContext())
        setupRecyclerView()
        setupSearchBar()
        loadAllCars()
    }

    private fun loadAllCars() {
        allCars.clear()
        allCars.addAll(dbHelper.getAllCars())
        searchAdapter.updateData(allCars)
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter(
            onDeleteClick = { car ->
                showDeleteConfirmationDialog(car)
            },
            onModifyClick = { car ->
                showModifyDialog(car)
            }
        )
        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    private fun showDeleteConfirmationDialog(car: Car) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Car")
            .setMessage("Are you sure you want to delete ${car.name}'s car (${car.plateNumber})?")
            .setPositiveButton("Delete") { _, _ ->
                deleteCar(car)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCar(car: Car) {
        val result = dbHelper.deleteCar(car.id)
        if (result > 0) {
            allCars.remove(car)
            searchAdapter.updateData(allCars)
            Toast.makeText(context, "Car deleted successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to delete car", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showModifyDialog(car: Car) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_modify_car, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Set current values
        dialogView.findViewById<TextInputEditText>(R.id.editName).setText(car.name)
        dialogView.findViewById<TextInputEditText>(R.id.editPlateNumber).setText(car.plateNumber)
        dialogView.findViewById<TextInputEditText>(R.id.editPhone).setText(car.phone)
        dialogView.findViewById<TextInputEditText>(R.id.editDepart).setText(car.depart)
        dialogView.findViewById<TextInputEditText>(R.id.editModel).setText(car.model)

        // Set up buttons
        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val updatedCar = Car(
                id = car.id,
                name = dialogView.findViewById<TextInputEditText>(R.id.editName).text.toString(),
                plateNumber = dialogView.findViewById<TextInputEditText>(R.id.editPlateNumber).text.toString(),
                phone = dialogView.findViewById<TextInputEditText>(R.id.editPhone).text.toString(),
                depart = dialogView.findViewById<TextInputEditText>(R.id.editDepart).text.toString(),
                model = dialogView.findViewById<TextInputEditText>(R.id.editModel).text.toString()
            )

            val result = dbHelper.updateCar(updatedCar)
            if (result > 0) {
                val index = allCars.indexOfFirst { it.id == car.id }
                if (index != -1) {
                    allCars[index] = updatedCar
                    searchAdapter.updateData(allCars)
                    Toast.makeText(context, "Car updated successfully", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Failed to update car", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupSearchBar() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                performSearch(s.toString())
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredResults = if (query.isEmpty()) {
            allCars
        } else {
            val lowercaseQuery = query.lowercase()
            allCars.filter { car ->
                car.name.lowercase().contains(lowercaseQuery) ||
                car.plateNumber.lowercase().contains(lowercaseQuery) ||
                car.model.lowercase().contains(lowercaseQuery)
            }
        }
        searchAdapter.updateData(filteredResults)
    }

    override fun setupObservers() {
        // Setup any observers here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dbHelper.close()
        _binding = null
    }
}

class SearchAdapter(
    private val onDeleteClick: (Car) -> Unit,
    private val onModifyClick: (Car) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    
    private var cars: List<Car> = emptyList()

    fun updateData(newCars: List<Car>) {
        cars = newCars
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val car = cars[position]
        holder.bind(car)
    }

    override fun getItemCount() = cars.size

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val plateNumberTextView: TextView = itemView.findViewById(R.id.plateNumberTextView)
        private val departTextView: TextView = itemView.findViewById(R.id.departTextView)
        private val modelTextView: TextView = itemView.findViewById(R.id.modelTextView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        private val modifyButton: ImageButton = itemView.findViewById(R.id.modifyButton)

        fun bind(car: Car) {
            nameTextView.text = car.name
            plateNumberTextView.text = car.plateNumber
            departTextView.text = car.depart
            modelTextView.text = car.model
            
            deleteButton.setOnClickListener {
                onDeleteClick(car)
            }
            
            modifyButton.setOnClickListener {
                onModifyClick(car)
            }
        }
    }
} 