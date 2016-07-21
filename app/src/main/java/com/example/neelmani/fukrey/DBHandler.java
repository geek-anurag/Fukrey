package com.example.neelmani.fukrey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.neelmani.fukrey.MessageHandlers.MessageDetails;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DB_VERSION = 1;

	// Database Name
	private static final String DB_NAME = "MessageManager";

	// Contacts table name
	private static final String TABLE_MESSAGE_DETAILS = "MessageDetails";

	// Contacts Table Columns names
	private static final String COLUMN_ID = "row_id";
	private static final String COLUMN_USER_NAME = "user_name";
	private static final String COLUMN_AGREE = "agree";
	private static final String COLUMN_DISAGREE = "disagree";
	private static final String COLUMN_COMMENT_TEXT = "comment_text";
	private static final String COLUMN_TIME_STAMP = "time_stamp";
	private static final String COLUMN_IMAGE = "image";
	private static final String COLUMN_LATITUDE = "latitude";
	private static final String COLUMN_LONGITUDE= "longitude";
	private static final String COLUMN_ADDRESS= "address";
	private static final String COLUMN_LSC= "lsc";
	private static final String COLUMN_DISTANCE= "distance";

	public DBHandler(Context context) {
		//TODO: To delete existing rows and insert new thousand rows in one transaction (schedueled)
		super(context, DB_NAME, null, DB_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_MESSAGE_DETAILS_TABLE = "CREATE TABLE " + TABLE_MESSAGE_DETAILS + "("
				+ COLUMN_ID + " TEXT ,"
				+ COLUMN_USER_NAME + " TEXT,"
				+ COLUMN_AGREE + " TEXT,"
				+ COLUMN_DISAGREE + " TEXT,"
				+ COLUMN_COMMENT_TEXT + " TEXT,"
				+ COLUMN_TIME_STAMP + " TEXT,"
				+ COLUMN_IMAGE + " BLOB,"
				+ COLUMN_LATITUDE + " TEXT,"
				+ COLUMN_LONGITUDE + " TEXT,"
	         	+ COLUMN_ADDRESS + " TEXT,"
				+ COLUMN_LSC + " TEXT,"
		        + COLUMN_DISTANCE + " TEXT)";
		db.execSQL(CREATE_MESSAGE_DETAILS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_DETAILS);
		// Create tables again
		onCreate(db);
	}

	public void addMessageDetails(MessageDetails messageDetails) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, messageDetails.getMessageId());
		values.put(COLUMN_USER_NAME, messageDetails.getUserName());
		values.put(COLUMN_AGREE, messageDetails.getAgree());
		values.put(COLUMN_DISAGREE, messageDetails.getDisagree());
		values.put(COLUMN_COMMENT_TEXT, messageDetails.getMessage());
		values.put(COLUMN_TIME_STAMP, messageDetails.getTimeStamp());
		values.put(COLUMN_IMAGE, messageDetails.getImage());
		values.put(COLUMN_ADDRESS, messageDetails.getAddress());
		values.put(COLUMN_LSC, messageDetails.getLSC());
		values.put(COLUMN_LATITUDE, messageDetails.getLatitude());
		values.put(COLUMN_LONGITUDE, messageDetails.getLongitude());
		values.put(COLUMN_DISTANCE, messageDetails.getDistance());
		// Inserting Row
		db.insert(TABLE_MESSAGE_DETAILS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	MessageDetails getMessageDetails (int id) {

		String selectQuery = "SELECT ROWID, * FROM " + TABLE_MESSAGE_DETAILS+" where row_id="+id;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor != null)
			cursor.moveToFirst();

		MessageDetails messageDetails = new MessageDetails(cursor.getString(1),
				cursor.getString(2),
				cursor.getString(3),
				cursor.getString(4),
				cursor.getString(5),
				cursor.getString(6),
				cursor.getBlob(7),
				cursor.getString(8),
				cursor.getString(9),
				cursor.getString(10),
				cursor.getString(11),
				cursor.getString(12));
		// return messageDetails
		return messageDetails;
	}
	
	// Getting All MessageDetails
	public ArrayList<MessageDetails> getAllMessageDetails(int rows) {
		ArrayList<MessageDetails> messageDetailsList = new ArrayList<MessageDetails>();
		// Select All Query
		String selectQuery = "SELECT ROWID, * FROM " + TABLE_MESSAGE_DETAILS+" ORDER BY ROWID DESC LIMIT "+rows;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				MessageDetails messageDetails = new MessageDetails();
				messageDetails.setMessageId(cursor.getString(1));
				messageDetails.setUserName(cursor.getString(2));//cursor.getColumnIndex("ROWID"));
				messageDetails.setAgree(cursor.getString(3));
				messageDetails.setDisagree(cursor.getString(4));
				messageDetails.setMessage(cursor.getString(5));
				messageDetails.setTimeStamp(cursor.getString(6));
				messageDetails.setImage(cursor.getBlob(7));
				messageDetails.setLatitude(cursor.getString(8));
				messageDetails.setLongitde(cursor.getString(9));
				messageDetails.setAddress(cursor.getString(10));
				messageDetails.setLSC(cursor.getString(11));
				messageDetails.setDistance(cursor.getString(12));
				// Adding contact to list
				messageDetailsList.add(messageDetails);
			} while (cursor.moveToNext());
		}

		// return contact list
		return messageDetailsList;
	}

	// Updating single MessageDetails
	public int updateMessageDetails(String columnName, String value, String rowId) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(columnName, value);
		/*values.put(COLUMN_USER_NAME, messageDetails.getUserName());
		values.put(COLUMN_AGREE, messageDetails.getAgree());
		values.put(COLUMN_DISAGREE, messageDetails.getDisagree());
		values.put(COLUMN_COMMENT_TEXT, messageDetails.getMessage());*/

		// updating row
		return db.update(TABLE_MESSAGE_DETAILS, values, COLUMN_ID + " = ?",
				new String[] { rowId });
	}

	// Deleting single MessageDetails
	public void deleteMessageDetails() {
		SQLiteDatabase db = this.getWritableDatabase();
		//db.delete(TABLE_MESSAGE_DETAILS, "row_id = ?",new String[] { rowId });
		db.delete(TABLE_MESSAGE_DETAILS,null,null);
		db.close();
	}


	// Getting MessageDetails Count
	public int getMessageDetailsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_MESSAGE_DETAILS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

}
