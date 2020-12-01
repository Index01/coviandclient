package com.covilyfe.v1r0

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.core.database.getBlobOrNull


class DBHandlerAccount(var context: Context?) : SQLiteOpenHelper(context, DB_NAME,null, DB_VERSION){
    companion object {
        var DB_VERSION = 1
        const val DB_NAME = "RonaRiskLocalAcct"
        const val TABLE_NAME = "Account"
        const val COL_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_EMAIL = "email"
        const val COL_JWT = "jwt"
        const val COL_AVATAR = "avatar"
    }

    fun deleteRows(){
        val db = this.writableDatabase
        db.execSQL("delete from Account")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " VARCHAR(256), " +
                COL_EMAIL + " VARCHAR(256), " +
                COL_JWT + " VARCHAR(256), " +
                COL_AVATAR + " BLOB)" ;
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        /** So dumb. I usually uncomment this stuff when upgrading schemas. **/
        //todo: deleteme
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        println("---------------------- Dropping Account table like it is hot ----------------------")
        DB_VERSION = newVersion
        /*
        val dbUpdateStr = "ALTER TABLE PERSON ADD COLUMN img_type INTEGER DEFAULT 0"
        if (newVersion > oldVersion){
            db!!.execSQL(dbUpdateStr)
        }
        */
    }

    fun insertData(account: Account){
        val db = this.writableDatabase
        val cv = ContentValues()
        // db!!.execSQL("ALTER TABLE PERSON ADD COLUMN date INTEGER DEFAULT 0")
        cv.put(COL_ID,account.id)
        cv.put(COL_USERNAME,account.username)
        cv.put(COL_EMAIL,account.email)
        cv.put(COL_JWT,account.jwt)
        cv.put(COL_AVATAR,account.avatar)
        val result = db.insert(TABLE_NAME, null, cv)
        if(result == -1.toLong())
            Toast.makeText(context, "Failed to create account", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "Account ready!", Toast.LENGTH_SHORT).show()
    }

    fun retrieveAccount() : ArrayList<Account>{
        val list : ArrayList<Account> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from " + TABLE_NAME
        val cursor = db.rawQuery(query, null)

        DatabaseUtils.queryNumEntries(db, TABLE_NAME)
        if(cursor.moveToFirst()){
            do {
                val account = Account()
                account.id = cursor.getString(cursor.getColumnIndex(COL_ID)).toInt()
                account.username = cursor.getString(cursor.getColumnIndex(COL_USERNAME))
                account.email = cursor.getString(cursor.getColumnIndex(COL_EMAIL))
                account.jwt = cursor.getString(cursor.getColumnIndex(COL_JWT))
                account.avatar = cursor.getBlobOrNull(cursor.getColumnIndex(COL_AVATAR))
                list.add(account)
            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun updateAvatar(id: Int, avatar: ByteArray){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_AVATAR, avatar)
        db.update(TABLE_NAME, cv, COL_ID+"="+id, null)

    }

//    fun deleteRow(view: RecyclerView.ViewHolder){
//        val ret = retrieveAccount()
//        val db = this.writableDatabase
//        db.delete(TABLE_NAME, COL_ID+"="+ view.itemView.txtID.text, null) >0;
//        db.close()
//        println("---------- deleted rows --------------")
//        println(ret)
//    }

}
