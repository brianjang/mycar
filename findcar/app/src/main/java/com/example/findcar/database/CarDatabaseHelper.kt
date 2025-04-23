package com.example.findcar.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.findcar.model.Car

class CarDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "findcar.db"
        private const val DATABASE_VERSION = 1

        // Table name
        const val TABLE_CARS = "cars"

        // Column names
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DEPART = "depart"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_PLATE_NUMBER = "plate_number"
        const val COLUMN_MODEL = "model"
        const val COLUMN_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_CARS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DEPART TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_PLATE_NUMBER TEXT NOT NULL UNIQUE,
                $COLUMN_MODEL TEXT NOT NULL,
                $COLUMN_IMAGE TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CARS")
        onCreate(db)
    }

    // Add a new car
    fun addCar(car: Car): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, car.name)
            put(COLUMN_DEPART, car.depart)
            put(COLUMN_PHONE, car.phone)
            put(COLUMN_PLATE_NUMBER, car.plateNumber)
            put(COLUMN_MODEL, car.model)
            put(COLUMN_IMAGE, car.image)
        }
        return db.insert(TABLE_CARS, null, values)
    }

    // Get all cars
    fun getAllCars(): List<Car> {
        val cars = mutableListOf<Car>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CARS,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val car = Car(
                    id = getLong(getColumnIndexOrThrow(COLUMN_ID)),
                    name = getString(getColumnIndexOrThrow(COLUMN_NAME)),
                    depart = getString(getColumnIndexOrThrow(COLUMN_DEPART)),
                    phone = getString(getColumnIndexOrThrow(COLUMN_PHONE)),
                    plateNumber = getString(getColumnIndexOrThrow(COLUMN_PLATE_NUMBER)),
                    model = getString(getColumnIndexOrThrow(COLUMN_MODEL)),
                    image = getString(getColumnIndexOrThrow(COLUMN_IMAGE))
                )
                cars.add(car)
            }
        }
        cursor.close()
        return cars
    }

    // Update a car
    fun updateCar(car: Car): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, car.name)
            put(COLUMN_DEPART, car.depart)
            put(COLUMN_PHONE, car.phone)
            put(COLUMN_PLATE_NUMBER, car.plateNumber)
            put(COLUMN_MODEL, car.model)
            put(COLUMN_IMAGE, car.image)
        }
        return db.update(
            TABLE_CARS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(car.id.toString())
        )
    }

    // Delete a car
    fun deleteCar(carId: Long): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_CARS,
            "$COLUMN_ID = ?",
            arrayOf(carId.toString())
        )
    }

    // Get car by plate number
    fun getCarByPlateNumber(plateNumber: String): Car? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CARS,
            null,
            "$COLUMN_PLATE_NUMBER = ?",
            arrayOf(plateNumber),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val car = Car(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                depart = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEPART)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                plateNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLATE_NUMBER)),
                model = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL)),
                image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
            )
            cursor.close()
            car
        } else {
            cursor.close()
            null
        }
    }
}