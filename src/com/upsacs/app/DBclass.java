package com.upsacs.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DBclass extends SQLiteOpenHelper {
	private static final String DB_NAME = "upsacs";
	private static String DB_PATH = "";
	private static DBclass mDBConnection;
	private SQLiteDatabase db;
	private final Context myContext;

	public DBclass(Context paramContext) {
		super(paramContext, "mSLAM_HSBC", null, 1);
		this.myContext = paramContext;
		DB_PATH = "/data/data/"
				+ paramContext.getApplicationContext().getPackageName()
				+ "/databases/";
	}

	public static boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public static synchronized DBclass getDBAdapterInstance(Context context) {
		if (mDBConnection == null) {
			mDBConnection = new DBclass(context);
		}
		return mDBConnection;
	}

	public void close() {
		if (this.db != null)
			this.db.close();
		super.close();

	}

	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
			copyDataBaseOut();
		} else {
			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (Exception e) {
				Log.i("DBClass", e.getMessage());
			}
		}
	}

	private void copyDataBaseOut() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = new FileInputStream(DB_PATH + DB_NAME);
		// Path to the just created empty db
		File sd = Environment.getExternalStorageDirectory();

		// if the path doesn't exist first, create it
		File backupDB = new File(sd.getAbsolutePath() + "/back");
		if (!backupDB.exists()) {
			// backupDB.mkdir();
			backupDB.createNewFile();
		}

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(backupDB);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {
		try {
			String myPath = DB_PATH + DB_NAME;
			db = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<CDOLoc> getLocationId_Name() {
		ArrayList<CDOLoc> arr = new ArrayList<CDOLoc>();
		openDataBase();
		try {

			Cursor localCursor = db.rawQuery("Select id,loc_name from loc_new",
					null);

			CDOLoc cdo1 = new CDOLoc();
			cdo1.setId("0");
			cdo1.setName("--Select--");
			arr.add(cdo1);
			if (localCursor.moveToFirst()) {

				do {
					CDOLoc cdo = new CDOLoc();
					cdo.setId(localCursor.getString(0));
					cdo.setName(localCursor.getString(1));
					arr.add(cdo);
				} while (localCursor.moveToNext());
			}
			localCursor.close();

		} catch (Exception e) {
			close();
			e.printStackTrace();
		}

		close();
		return arr;
	}

	public CDOLoc getLatLongFromID(String id) {
		openDataBase();
		CDOLoc cdo = new CDOLoc();
		try {

			Cursor localCursor = db.rawQuery(
					"Select latitude,longitude from loc_new where id = ?",
					new String[] { id });
			if (localCursor.moveToFirst()) {

				do {

					cdo.setLatitude(localCursor.getString(0));
					cdo.setLongitude(localCursor.getString(1));
				} while (localCursor.moveToNext());
			}
			localCursor.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
		return cdo;
	}

	public List<String> getDistricts() {
		openDataBase();
		List<String> districts = new ArrayList<String>();
		try {

			Cursor localCursor = db.rawQuery(
					"Select distinct District from blood_banks ", null);
			if (localCursor.moveToFirst()) {
				do {

					districts.add(localCursor.getString(0));
				} while (localCursor.moveToNext());
			}
			localCursor.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
		return districts;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public List<CDOLoc> getLocationId_Name_ForDistrict(String district) {
		ArrayList<CDOLoc> arr = new ArrayList<CDOLoc>();
		openDataBase();
		try {

			Cursor localCursor = db
					.rawQuery(
							"Select No,Name,Latitude,Longitude from blood_banks where District = ?",
							new String[] { district });

			if (localCursor.moveToFirst()) {

				do {
					CDOLoc cdo = new CDOLoc();
					cdo.setId(localCursor.getString(0));
					cdo.setName(localCursor.getString(1));
					cdo.setLatitude(localCursor.getString(2).trim().equals("") ? "0.0"
							: localCursor.getString(2));
					cdo.setLongitude(localCursor.getString(3).trim().equals("") ? "0.0"
							: localCursor.getString(3));
					arr.add(cdo);
				} while (localCursor.moveToNext());
			}
			localCursor.close();

		} catch (Exception e) {
			close();
			e.printStackTrace();
		}

		close();
		return arr;
	}

}
