package com.arifian.training.liburansemarang.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.arifian.training.liburansemarang.models.Wisata

/**
 * Created by faqih on 31/10/17.
 */
class DBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {
        private val DATABASE_NAME = "dbwisata"
        private val DATABASE_TABLE = "table_wisata"
        private val WISATA_ID = "_id"
        private val NAMA_WISATA = "nama_wisata"
        private val GAMBAR_WISATA = "gambar_wisata"
        private val ALAMAT_WISATA = "alamat_wisata"
        private val DESKRIPSI_WISATA = "deskripsi_wisata"
        private val LATITUDE_WISATA = "latitude_wisata"
        private val LONGITUDE_WISATA = "longitude_wisata"
        private val PENGUNJUNG_WISATA = "pengunjung_wisata"
        private val FAVORITE_WISATA = "favorite_wisata"
        private val DATABASE_VERSION = 4

        private val CREATE_TABLE = ("CREATE TABLE " + DATABASE_TABLE
                + " (" + WISATA_ID + " INTEGER  PRIMARY KEY, "
                + NAMA_WISATA + " VARCHAR(200), "
                + GAMBAR_WISATA + " VARCHAR(200), "
                + ALAMAT_WISATA + " TEXT, "
                + DESKRIPSI_WISATA + " TEXT, "
                + LATITUDE_WISATA + " VARCHAR(20), "
                + LONGITUDE_WISATA + " VARCHAR(20), "
                + PENGUNJUNG_WISATA + " INTEGER, "
                + FAVORITE_WISATA + " INTEGER);")

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE)
        onCreate(db)
    }

    fun insert(wisata: Wisata): Long{
        val cv = ContentValues()
        cv.put(WISATA_ID, wisata.idWisata)
        cv.put(NAMA_WISATA, wisata.namaWisata)
        cv.put(GAMBAR_WISATA, wisata.gambarWisata)
        cv.put(ALAMAT_WISATA, wisata.alamatWisata)
        cv.put(DESKRIPSI_WISATA, wisata.deksripsiWisata)
        cv.put(LATITUDE_WISATA, wisata.latitudeWisata)
        cv.put(LONGITUDE_WISATA, wisata.longitudeWisata)
        cv.put(PENGUNJUNG_WISATA, wisata.pengunjung)
        cv.put(FAVORITE_WISATA, wisata.favorite)

        val db = writableDatabase
        val result = db.insert(DATABASE_TABLE, null, cv)
        db.close()
        return result
    }

    fun delete(wisata: Wisata): Int{
        val clause = WISATA_ID+" = ?"
        val args = Array(1, {wisata.idWisata})

        val db = this.writableDatabase
        val count = db.delete(DATABASE_TABLE, clause, args)
        db.close()
        return count
    }

    fun query(): ArrayList<Wisata>{
        val db = readableDatabase

        val wisatas = ArrayList<Wisata>()
        val cursor = db.query(DATABASE_TABLE, null, null, null, null, null,null)
        if(cursor != null){
            while (cursor.moveToNext()){
                val wisata = Wisata(
                        cursor.getInt(cursor.getColumnIndex(WISATA_ID)).toString(),
                        cursor.getString(cursor.getColumnIndex(NAMA_WISATA)),
                        cursor.getString(cursor.getColumnIndex(GAMBAR_WISATA)),
                        cursor.getString(cursor.getColumnIndex(ALAMAT_WISATA)),
                        cursor.getString(cursor.getColumnIndex(DESKRIPSI_WISATA)),
                        cursor.getString(cursor.getColumnIndex(LATITUDE_WISATA)),
                        cursor.getString(cursor.getColumnIndex(LONGITUDE_WISATA)),
                        cursor.getString(cursor.getColumnIndex(PENGUNJUNG_WISATA)),
                        cursor.getString(cursor.getColumnIndex(FAVORITE_WISATA))
                )
                wisatas.add(wisata)
            }
        }
        cursor.close()
        return wisatas
    }
}