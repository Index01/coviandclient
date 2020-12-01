package com.covilyfe.v1r0

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_person.view.*


class DBHandler(var context: Context?) : SQLiteOpenHelper(context, DB_NAME,null, DB_VERSION){
    val risk_sum = mutableListOf<Int>()
    val exposure_sum = mutableListOf<Int>()
    companion object {
        var DB_VERSION = 7
        const val DB_NAME = "RonaRiskLocal"
        const val TABLE_NAME = "Person"
        const val COL_ID = "id"
        const val COL_DATE = "date"
        const val COL_NAME = "name"
        const val COL_DESCRIPTION = "description"
        const val COL_RISK_PERCEIVED = "risk_perceived"
        const val COL_EXPOSURE_PERCEIVED = "exposure_perceived"
        const val COL_IMGTYPE = "img_type"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " INTEGER, " +
                COL_NAME + " VARCHAR(256), " +
                COL_DESCRIPTION + " VARCHAR(256), " +
                COL_RISK_PERCEIVED + " INTEGER, " +
                COL_EXPOSURE_PERCEIVED + " INTEGER, " +
                COL_IMGTYPE + " VARCHAR(256)) " ;
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        /** So dumb. I usually uncomment this stuff when upgrading schemas. **/
        //todo: deleteme
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        println("---------------------- Dropping table like it is hot ----------------------")
        DB_VERSION = newVersion
        /*
        val dbUpdateStr = "ALTER TABLE PERSON ADD COLUMN img_type INTEGER DEFAULT 0"
        if (newVersion > oldVersion){
            db!!.execSQL(dbUpdateStr)
        }
        */
    }

    fun insertData(person: Person){
        val db = this.writableDatabase
        val cv = ContentValues()
       // db!!.execSQL("ALTER TABLE PERSON ADD COLUMN date INTEGER DEFAULT 0")
        cv.put(COL_DATE,person.date)
        cv.put(COL_NAME,person.name)
        cv.put(COL_DESCRIPTION,person.description)
        cv.put(COL_RISK_PERCEIVED,person.risk_perceived)
        cv.put(COL_EXPOSURE_PERCEIVED,person.exposure_perceived)
        cv.put(COL_IMGTYPE, person.img_type)
        val result = db.insert(TABLE_NAME, null, cv)
        if(result == -1.toLong())
            Toast.makeText(context, "Failed to create record", Toast.LENGTH_SHORT).show()
    }

    fun retrievePersons() : ArrayList<Person>{
        val list : ArrayList<Person> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from " + TABLE_NAME
        val cursor = db.rawQuery(query, null)
        risk_sum.clear()
        exposure_sum.clear()

        DatabaseUtils.queryNumEntries(db, TABLE_NAME)
        if(cursor.moveToFirst()){
            do {
                val person = Person()
                person.id = cursor.getString(cursor.getColumnIndex(COL_ID)).toInt()
                person.date = cursor.getString(cursor.getColumnIndex(COL_DATE))
                person.name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                person.description = cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION))
                person.risk_perceived =
                    cursor.getString(cursor.getColumnIndex(COL_RISK_PERCEIVED)).toInt()
                person.exposure_perceived =
                    cursor.getString(cursor.getColumnIndex(COL_EXPOSURE_PERCEIVED)).toInt()
                person.img_type = cursor.getString(cursor.getColumnIndex(COL_IMGTYPE))
                list.add(person)
                risk_sum.add(person.risk_perceived)
                exposure_sum.add(person.exposure_perceived)
            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun deleteRow(view: RecyclerView.ViewHolder){
        val ret = retrievePersons()
        val db = this.writableDatabase
        db.delete(TABLE_NAME, COL_ID+"="+ view.itemView.txtID.text, null) >0;
        db.close()
        println("---------- deleted rows --------------")
        println(ret)
    }

    fun getRiskSum(): Int {
        if(!risk_sum.isNullOrEmpty()){
            return risk_sum.sum()
        }else{
            return 0
        }
    }

    fun getExposureSum(): Int {
        if(!exposure_sum.isNullOrEmpty()){
            return exposure_sum.sum()
        }else{
            return 0
        }
    }

    fun getRiskMax(): Int {
        if(!risk_sum.isNullOrEmpty()){
            return risk_sum.max()!!
        }else{
            return 0
        }
    }

    fun getExposureMax(): Int {
        if(!exposure_sum.isNullOrEmpty()){
            return exposure_sum.max()!!
        }else{
            return 0
        }
    }
}
