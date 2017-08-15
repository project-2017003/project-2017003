package com.optimustechproject2017.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class SqliteDB extends SQLiteOpenHelper {

    public SqliteDB(Context applicationcontext) {
        super(applicationcontext, "App.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query, query2;
        query = "CREATE TABLE Cart ( ID TEXT ,Name TEXT,ItemName TEXT ,price Text, URL TEXT ,Quant text)";
        database.execSQL(query);


//		query2 = "CREATE TABLE ClusterEvents ( EVENTNO INTEGER PRIMARY KEY, EVENTNAME TEXT, udpateStatus TEXT,Gflag Text)";
//		database.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS cart";
        database.execSQL(query);
        onCreate(database);
    }

    public void onchange() {
        SQLiteDatabase database = this.getWritableDatabase();
        String query;
        query = "DELETE  FROM participants ";
        database.execSQL(query);
        query = "DELETE  FROM  ClusterEvents";
        database.execSQL(query);
        //onCreate(database);
    }

    public void insertcart(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("ID", queryValues.get("ID"));

        values.put("price", queryValues.get("price"));
        values.put("Quant", queryValues.get("Quant"));


        database.insert("Cart", null, values);
        database.close();
    }


    public void updateUser(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();
        System.out.println(queryValues);
        String updateQuery = "Update Cart set  price = '" + queryValues.get("price") + "'  ,Quant = '" + queryValues.get("Quant") + "' where ID = '" + queryValues.get("ID") + "'";
        Log.d("Update Query", updateQuery);
        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public int quantcount() {
        int count = 0;
        String selectQuery = "SELECT  * FROM Cart where Quant <> '0'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        Log.d("Countquant", count + "");
        return count;
    }

    public int totalprice() {
        int count = 0;
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  ID,Price,Quant FROM Cart where Quant != '0' ";
        //String selectQuery = "SELECT  * FROM participants  ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ID", cursor.getString(0));
                map.put("Price", cursor.getString(1));
                map.put("Quant", cursor.getString(2));


                count += Integer.parseInt(cursor.getString(1)) * Integer.parseInt(cursor.getString(2));


                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Log.d("totalprice", count + "");

        return count;
    }


    public ArrayList<HashMap<String, String>> getAllcartItems() {


        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  ID,price,Quant FROM Cart";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemID", cursor.getString(0));
                map.put("price", cursor.getString(1));
                map.put(("Quant"), cursor.getString(2));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


    public void InsertEvents(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("EVENTNAME", queryValues.get("EVENTNAME"));
        values.put("udpateStatus", "no");
        values.put("EVENTNO", queryValues.get("EVENTNO"));
        values.put("Gflag", queryValues.get("Gflag"));

        database.insert("ClusterEvents", null, values);
        database.close();
    }

    public String getEventNO(String e) {
        String selectQuery = "SELECT  EVENTNO FROM ClusterEvents where EVENTNAME = '" + e + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        String EventNo = new String();
        if (cursor.moveToFirst()) {
            EventNo = cursor.getString(0);
        }
        return EventNo;
    }


    public void dropDB() {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        //DELETE FROM COMPANY WHERE ID = 7;
        SQLiteDatabase database = this.getWritableDatabase();
        System.out.println("i droped it");
//        String query;
//        query = "DELETE  FROM studs";
//        database.execSQL(query);
//        onCreate(database);

        database.delete("participants", null, null);
        database.close();
    }

    public ArrayList<HashMap<String, String>> getAllUsers(String EventNo) {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  KSID,NAME,GROUPTITLE,TITLE,PLACE FROM participants WHERE EventNo=" + "'" + EventNo + "'";
        //String selectQuery = "SELECT  * FROM participants  ";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("KSID", cursor.getString(0));
                map.put("NAME", cursor.getString(1));
                map.put("GROUPTITLE", cursor.getString(2));
                map.put("TITLE", cursor.getString(3));
                map.put("PLACE", cursor.getString(4));


                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        System.out.println(wordList);
        System.out.println("wordlist");
        return wordList;
    }


    public ArrayList<HashMap<String, String>> getAllGroupUsers(String EventNo, String GroupName) {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  NAME,KSID,GROUPTITLE,TITLE,PLACE FROM participants WHERE EventNo=" + "'" + EventNo + "' and GroupName =  " + "'" + GroupName + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //map.put("updateStatus", cursor.getString(7));
                map.put("KSID", cursor.getString(1));
                map.put("NAME", cursor.getString(0));
                map.put("GROUPTITLE", cursor.getString(2));
                map.put("TITLE", cursor.getString(3));
                map.put("PLACE", cursor.getString(4));


                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    public String composeJSONfromSQLite(String EventNo) {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        //String selectQuery = "SELECT  * FROM participants where udpateStatus = '"+"no"+"'   ";

        String selectQuery = "SELECT  * FROM participants where udpateStatus = '" + "no" + "'  and EVENTNO = '" + EventNo + "' ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("KSID", cursor.getString(0));

                map.put("EVENTNO", cursor.getString(8));
                map.put("ORGANIZER", cursor.getString(12));


                //map.put("PLACE", cursor.getString(0));
                map.put("GROUPNAME", cursor.getString(9));
                map.put("GROUPTITLE", cursor.getString(13));
                map.put("TITLE", cursor.getString(9));
                map.put("PLACE", cursor.getString(10));


                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();

        return gson.toJson(wordList);
    }

    public void updateRank(String EventNo, String ksid, String Rank) {
        SQLiteDatabase database = this.getWritableDatabase();

        String updateQuery = "Update participants set PLACE = '" + Rank + "' where KSID=" + "'" + ksid + "' and EVENTNO = " + " '" + EventNo + "'    ";
        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public void GroupRank(String EventNo, String GN, String Rank) {
        SQLiteDatabase database = this.getWritableDatabase();

        String updateQuery = "Update participants set GROUPRANK = '" + Rank + "' where GROUPNAME=" + "'" + GN + "' and EVENTNO = " + " '" + EventNo + "'    ";
        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public void updatetitle(String EventNo, String ksid, String Rank) {
        SQLiteDatabase database = this.getWritableDatabase();

        String updateQuery = "Update participants set TITLE = '" + Rank + "' where KSID=" + "'" + ksid + "' and EVENTNO = " + " '" + EventNo + "'    ";
        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public void updateGroupalltitle(String EventNo, String Rank, String Groupname) {
        SQLiteDatabase database = this.getWritableDatabase();

        String updateQuery = "Update participants set GROUPTITLE = '" + Rank + "' where GROUPNAME=" + "'" + Groupname + "' and EVENTNO = " + " '" + EventNo + "'    ";
        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public String getGender(String EventNo, String KSID) {

        String gen = "";
        //String selectQuery = "SELECT  * FROM participants where udpateStatus = '"+"no"+"'   ";

        String selectQuery = "SELECT  * FROM participants where KSID = '" + KSID + "'  and EVENTNO = '" + EventNo + "' ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                gen = cursor.getString(2);
                if (cursor.isNull(2)) {
                    gen = "";
                }
            } while (cursor.moveToNext());
        }
        database.close();
        System.out.println("KKKKSSSSS  GEn   " + gen);
        return gen;
    }


    public String getCollege(String EventNo, String KSID) {

        String gen = "";
        //String selectQuery = "SELECT  * FROM participants where udpateStatus = '"+"no"+"'   ";

        String selectQuery = "SELECT  * FROM participants where KSID = '" + KSID + "'  and EVENTNO = '" + EventNo + "' ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                gen = cursor.getString(3);
                if (cursor.isNull(3)) {
                    gen = "";
                }
            } while (cursor.moveToNext());
        }
        database.close();
        System.out.println("KKKKSSSSS  CLG   " + gen);
        return gen;
    }

    public String getMobile(String EventNo, String KSID) {

        String gen = "";
        //String selectQuery = "SELECT  * FROM participants where udpateStatus = '"+"no"+"'   ";

        String selectQuery = "SELECT  * FROM participants where KSID = '" + KSID + "'  and EVENTNO = '" + EventNo + "' ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                gen = cursor.getString(4);
                if (cursor.isNull(4)) {
                    gen = "";
                }
            } while (cursor.moveToNext());
        }
        database.close();
        System.out.println("KKKKSSSSS  MOB   " + gen);
        return gen;
    }


    public String getGroupCollege(String EventNo, String GroupName) {

        String gen = "";
        //String selectQuery = "SELECT  * FROM participants where udpateStatus = '"+"no"+"'   ";

        String selectQuery = "SELECT  CLG FROM participants where GROUPNAME = '" + GroupName + "'  and EVENTNO = '" + EventNo + "' ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                if (!cursor.isNull(0)) {
                    gen = cursor.getString(0);
                } else gen = "";

            } while (cursor.moveToNext());
        }
        database.close();
        System.out.println("KKKKSSSSS  CLG   " + gen);
        return gen;
    }

    public String getEmail(String EventNo, String KSID) {

        String gen = "";
        //String selectQuery = "SELECT  * FROM participants where udpateStatus = '"+"no"+"'   ";

        String selectQuery = "SELECT  EMAIL FROM participants where KSID = '" + KSID + "'  and EVENTNO = '" + EventNo + "' ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                if (!cursor.isNull(0)) {
                    gen = cursor.getString(0);
                } else gen = "";

            } while (cursor.moveToNext());
        }
        database.close();
        System.out.println("KKKKSSSSS  GEn   " + gen);
        return gen;
    }

    public String getAccom(String EventNo, String KSID) {

        String gen = "";
        //String selectQuery = "SELECT  * FROM participants where udpateStatus = '"+"no"+"'   ";

        String selectQuery = "SELECT  ACCOM FROM participants where KSID = '" + KSID + "'  and EVENTNO = '" + EventNo + "' ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                if (!cursor.isNull(0)) {
                    if (cursor.getString(0).equals("1")) {
                        gen = "Yes";
                    } else {
                        gen = "No";
                    }
                }
            } while (cursor.moveToNext());
        }
        database.close();
        System.out.println("KKKKSSSSS  GEn   " + gen);
        return gen;
    }


    public ArrayList<HashMap<String, String>> getAllGroups(String EventNo) {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT DISTINCT  GROUPNAME,PLACE,GROUPTITLE  FROM participants WHERE EVENTNO=" + "'" + EventNo + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                //map.put("userId", cursor.getString(0));
                map.put("GROUPNAME", cursor.getString(0));
                map.put("GROUPTITLE", cursor.getString(2));
                map.put("PLACE", cursor.getString(1));


                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        System.out.println(wordList);
        return wordList;
    }


    public String getSyncStatus() {
        String msg = null;
        if (this.dbSyncCount() == 0) {
            msg = "in Sync!";
        } else {
            msg = "Sync needed\n";
        }
        return msg;
    }


    public int dbSyncCount() {
        int count = 0;
        String selectQuery = "SELECT  * FROM participants where udpateStatus = '" + "no" + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }

    public ArrayList getksid(String EventNo) {
        int count = 0;
        String selectQuery = "SELECT  KSID FROM participants WHERE EVENTNO=" + "'" + EventNo + "'";
        //String selectQuery = "SELECT  KSID FROM participants where udpateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        ArrayList<String> values = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                values.add(cursor.getString(0));

                cursor.moveToNext();
            }
        }

        return values;
    }


    public ArrayList getitems() {
        int count = 0;
        String selectQuery = "SELECT  ID FROM Cart WHERE 1";
        System.out.println(selectQuery);
        //String selectQuery = "SELECT  KSID FROM participants where udpateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        ArrayList<String> values = new ArrayList<>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                values.add(cursor.getString(0));

                cursor.moveToNext();
            }
        }

        return values;
    }

    public void updateSyncStatus(String id, String status, String EventNo) {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update participants set  udpateStatus = '" + status + "' where KSID=" + "'" + id + "' AND EVENTNO=" + "'" + EventNo + "'  ";
        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public void SetRank(String RegiNo, String Rank) {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update users set Rank = '" + Rank + "' where userName=" + "'" + RegiNo + "'";
        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();

    }

    public void SetGroupRank(String GroupName, String Rank) {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update users set Rank = '" + Rank + "' where GroupName=" + "'" + GroupName + "'";
        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


}
