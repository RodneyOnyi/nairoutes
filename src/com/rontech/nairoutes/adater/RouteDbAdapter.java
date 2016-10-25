package com.rontech.nairoutes.adater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RouteDbAdapter {

	public static final String KEY_ROWID = "_id";
	 public static final String KEY_ROUTE = "route";
	 public static final String KEY_STAGE = "stage";
	 public static final String KEY_STOP = "stop";
	 public static final String KEY_FARE = "fare";
	 
	 private static final String TAG = "RouteDbAdapter";
	 private DatabaseHelper mDbHelper;
	 private SQLiteDatabase mDb;
	 
	 private static final String DATABASE_NAME = "PSV";
	 private static final String SQLITE_TABLE = "Route";
	 private static final int DATABASE_VERSION = 1;
	 
	 private final Context mCtx;
	 
	 private static final String DATABASE_CREATE =
	  "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
	  KEY_ROWID + " integer PRIMARY KEY autoincrement," +
	  KEY_ROUTE + "," +
	  KEY_STAGE + "," +
	  KEY_STOP + "," +
	  KEY_FARE + "," +
	  " UNIQUE (" + KEY_ROUTE +"));";
	 
	 private static class DatabaseHelper extends SQLiteOpenHelper {
	 
	  DatabaseHelper(Context context) {
	   super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }
	 
	 
	  @Override
	  public void onCreate(SQLiteDatabase db) {
	   Log.w(TAG, DATABASE_CREATE);
	   db.execSQL(DATABASE_CREATE);
	  }
	 
	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	   Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
	     + newVersion + ", which will destroy all old data");
	   db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
	   onCreate(db);
	  }
	 }
	 
	 public RouteDbAdapter(Context ctx) {
	  this.mCtx = ctx;
	 }
	 
	 public RouteDbAdapter open() throws SQLException {
	  mDbHelper = new DatabaseHelper(mCtx);
	  mDb = mDbHelper.getWritableDatabase();
	  return this;
	 }
	 
	 public void close() {
	  if (mDbHelper != null) {
	   mDbHelper.close();
	  }
	 }
	 
	 public long createRoute(String route, String stage,
	   String stop, String fare) {
	 
	  ContentValues initialValues = new ContentValues();
	  initialValues.put(KEY_ROUTE, route);
	  initialValues.put(KEY_STAGE, stage);
	  initialValues.put(KEY_STOP, stop);
	  initialValues.put(KEY_FARE, fare);
	 
	  return mDb.insert(SQLITE_TABLE, null, initialValues);
	 }
	 
	 public boolean deleteAllRoutes() {
	 
	  int doneDelete = 0;
	  doneDelete = mDb.delete(SQLITE_TABLE, null , null);
	  Log.w(TAG, Integer.toString(doneDelete));
	  return doneDelete > 0;
	 
	 }
	 
	 public Cursor fetchRoutesByName(String inputText) throws SQLException {
	  Log.w(TAG, inputText);
	  Cursor mCursor = null;
	  if (inputText == null  ||  inputText.length () == 0)  {
	   mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
	     KEY_ROUTE, KEY_STAGE, KEY_STOP, KEY_FARE},
	     null, null, null, null, null);
	 
	  }
	  else {
	   mCursor = mDb.query(true, SQLITE_TABLE, new String[] {KEY_ROWID,
	     KEY_ROUTE, KEY_STAGE, KEY_STOP, KEY_FARE},
	     KEY_STOP + " like '%" + inputText + "%'" ,null, null, null, null, null);
	  }
	  if (mCursor != null) {
	   mCursor.moveToFirst();
	  }
	  return mCursor;
	 
	 }
	 
	 public Cursor fetchAllRoutes() {
	 
	  Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
	    KEY_ROUTE, KEY_STAGE, KEY_STOP, KEY_FARE},
	    null, null, null, null, null);
	 
	  if (mCursor != null) {
	   mCursor.moveToFirst();
	  }
	  return mCursor;
	 }
	 
	 public void insertSomeRoutes() {
	 
	  createRoute("102","Railways","Kenyatta,Mimosa,Uchumi,Prestige,Adams,Posta,Impala_Club,Junction,Dagoretti Corner,BP Satellite,Kawangware","Off-Peak 20-50Ksh\r\nPeak 50-80Ksh");
	  createRoute("15","Bus Station","Nyayo,Wilson,Barracks,Cemetry,Uchumi_Langata,UhuruGardens,Otiende,St.Mary","Off-Peak 20-50Ksh\r\nPeak 50-80Ksh");
	  createRoute("237","Ronald Ngara Street","GSU,Roosters,Safari Park,TRM,Githurai,Kahawa_Wendani,KU,Northern Bypass,Ruiru,NIBS,Juja,Thika","Off Peak 50-80Ksh \r\nPeak 60-150Ksh");
	  createRoute("111","Railways","Kenyatta,Mimosa,Uchumi,Prestige,Adams,Posta,Impala Club,Junction,Dagoretti Corner,Santak,RaceCourse,Lenana School,Nakumatt Karen,Ngong","Off Peak\r\nPeak");
	  createRoute("105","Koja","Westlands,Safaricom,Deloitee,Kangemi,Kabete,Uthiru,Ndumbuni,Kwa Ngwacii,Kikuyu","Off Peak\r\nPeak");
	  createRoute("110ATH","Railways","Nyayo,Capital Centre,Bellevue,Tile&Carpet,Panari,Business Park,General Motors,Cabanas","Off Peak\r\nPeak");
	  createRoute("115","Railways","This is to check whether the code works as expected since I am not sure what seems to be the problem","Off Peak\r\nPeak");
	 
	 }
	 
}
