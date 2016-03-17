package simrat.cube26.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

import simrat.cube26.PaymentGatewayList;
import simrat.cube26.R;
import simrat.cube26.data.Cube26Contract.*;
import simrat.cube26.model.PaymentGateway;

/**
 * Created by simrat on 12/3/16.
 */
public class Cube26DbHelper extends SQLiteOpenHelper {

    public static final int DATABSE_VERSION = 1;
    public static final String DATABASE_NAME = "cube26_db";
    private SQLiteDatabase db;
    private String DEBUG_TAG = this.getClass().getName().toString();

    public Cube26DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABSE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PAYMENT_GATEWAY_TABLE = "CREATE TABLE " + PaymentGatewayEntry.TABLE + " (" +
                PaymentGatewayEntry._ID + " INTEGER PRIMARY KEY, " +
                PaymentGatewayEntry.NAME + " TEXT, " +
                PaymentGatewayEntry.DESCRIPTION + " TEXT, " +
                PaymentGatewayEntry.BRANDING + " TEXT, " +
                PaymentGatewayEntry.CURRENCIES + " TEXT, " +
                PaymentGatewayEntry.HOW_TO_URL + " TEXT, " +
                PaymentGatewayEntry.RATING + " TEXT, " +
                PaymentGatewayEntry.SETUP_FEE + " TEXT, " +
                PaymentGatewayEntry.TRANS_FEE + " TEXT, " +
                PaymentGatewayEntry.LIKES + " INTEGER)";
        sqLiteDatabase.execSQL(SQL_CREATE_PAYMENT_GATEWAY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int getCount(){
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PaymentGatewayEntry.TABLE, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public void addGateway(PaymentGateway paymentGateway){
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.d(DEBUG_TAG, paymentGateway.getName());
        contentValues.put(PaymentGatewayEntry.NAME, paymentGateway.getName());
        contentValues.put(PaymentGatewayEntry.DESCRIPTION, paymentGateway.getDescription());
        contentValues.put(PaymentGatewayEntry.BRANDING, paymentGateway.getBranding());
        contentValues.put(PaymentGatewayEntry.RATING, paymentGateway.getRating());
        contentValues.put(PaymentGatewayEntry.CURRENCIES, paymentGateway.getCurrencies());
        contentValues.put(PaymentGatewayEntry.SETUP_FEE, paymentGateway.getSetup_fee());
        contentValues.put(PaymentGatewayEntry.HOW_TO_URL, paymentGateway.getHow_to_url());
        contentValues.put(PaymentGatewayEntry.TRANS_FEE, paymentGateway.getTrans_fee());

        long rowId = db.insert(PaymentGatewayEntry.TABLE, null, contentValues);
        Log.d(DEBUG_TAG, Long.toString(rowId));
    }

    public void deleteRows(){
        db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + PaymentGatewayEntry.TABLE);
    }

    public String[] getNames(){
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PaymentGatewayEntry.TABLE, null);
        String[] names = new String[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            names[i] = cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.NAME));
            i++;
        }
        cursor.close();
        return names;
    }
    public PaymentGateway getPaymentGateway(String name){
        db = this.getReadableDatabase();
        Cursor cursor = db.query(PaymentGatewayEntry.TABLE, null, " NAME = ? ", new String[]{name}, null, null, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0 && cursor != null){
            PaymentGateway paymentGateway = new PaymentGateway();
            paymentGateway.setDescription(cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.DESCRIPTION)));
            paymentGateway.setRating(cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.RATING)));
            paymentGateway.setBranding(cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.BRANDING)));
            paymentGateway.setTrans_fee(cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.TRANS_FEE)));
            paymentGateway.setCurrencies(cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.CURRENCIES)));
            paymentGateway.setSetup_fee(cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.SETUP_FEE)));
            paymentGateway.setHow_to_url(cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.HOW_TO_URL)));
            cursor.close();
            return paymentGateway;
        }
        return null;
    }

    public HashMap<String, Double> getNameRating(){
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PaymentGatewayEntry.TABLE, null);
        HashMap<String, Double> hashMap = new HashMap<String, Double>();
        while(cursor.moveToNext()){
            //Log.d(DEBUG_TAG, cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.NAME)) + cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.RATING)));
            hashMap.put(cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.NAME)), Double.parseDouble(cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.RATING))));
        }
        cursor.close();
        return hashMap;
    }

    public String[] searchQuery(String query){
        db = this.getReadableDatabase();
        Cursor cursor = db.query(true, PaymentGatewayEntry.TABLE, null, PaymentGatewayEntry.NAME + " LIKE ?",
                new String[]{"%" + query + "%"}, null, null, null,
                null);
        String[] values;
        Cursor cursor1 = db.query(true, PaymentGatewayEntry.TABLE, null, PaymentGatewayEntry.CURRENCIES + " LIKE ?",
                new String[] {"%"+ query + "%" }, null, null, null,
                null);
        if((cursor.getCount() > 0 && cursor != null) || (cursor1.getCount() > 0 && cursor1 != null)){
            values = new String[cursor.getCount() + cursor1.getCount()];
            int i = 0;
            if(cursor.getCount() > 0 && cursor != null){
                while(cursor.moveToNext()){
                    values[i] = cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.NAME));
                    i++;
                }
                cursor.close();
            }
            if(cursor1.getCount() > 0 && cursor1 != null){
                while(cursor1.moveToNext()){
                    values[i] = cursor1.getString(cursor1.getColumnIndex(PaymentGatewayEntry.NAME));
                    i++;
                }
                cursor1.close();
            }
            return values;
        }

        return null;
    }
    public String[] getNamesOnSetupFee(String setup_fee){
        db = this.getReadableDatabase();
        Cursor cursor = db.query(PaymentGatewayEntry.TABLE, null, " SETUP_FEE = ? ", new String[]{setup_fee}, null, null, null);
        if(cursor != null){
            String[] values = new String[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()){
                values[i] = cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.NAME));
                i++;
            }
            cursor.close();
            return values;
        }

        return null;
    }
    public boolean updateLikes(String name){
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PaymentGatewayEntry.LIKES, 1);
        long rowId = db.update(PaymentGatewayEntry.TABLE, contentValues, " NAME = ? ", new String[]{name});
        Log.d(DEBUG_TAG, Long.toString(rowId));
        if(rowId > 0)
            return true;
        else return false;
    }
    public String[] likes(){
        db = this.getReadableDatabase();
        Cursor cursor = db.query(PaymentGatewayEntry.TABLE, null, " LIKES = ? ", new String[]{"1"}, null, null, null);
        if(cursor != null){
            String[] values = new String[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()){
                values[i] = cursor.getString(cursor.getColumnIndex(PaymentGatewayEntry.NAME));
                i++;
            }
            cursor.close();
            return values;
        }

        return null;
    }
}
