package com.stpproject.viewutil;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stpproject.model.AddressItem;
import com.stpproject.model.DataItem;
import com.stpproject.model.EmailItem;
import com.stpproject.model.PhoneItem;
import com.stpproject.model.RectItem;
import com.stpproject.model.WebItem;



public class DataBaseHandler extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "datadb";
	// Database Table
	private static final String TABLE_CARD = "tblCard";
	private static final String TABLE_DETAIL = "tblDetail";
	private SQLiteDatabase dbMain;

	public DataBaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// tblCard
		String CREATE_TABLE_CARD = "CREATE TABLE " + TABLE_CARD + "("
				+ "id_card INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " contact_pos integer not null," + " name_card text,"
				+ " rect_name text," + " name_company text,"
				+ " rect_company text," + " image text" + ")";
		db.execSQL(CREATE_TABLE_CARD);
		// tblDetail
		String CREATE_TABLE_DETAIL = "CREATE TABLE " + TABLE_DETAIL + "("
				+ "id_detail INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " id_card integer not null," + " type integer not null,"
				+ " name_detail text not null," + " rect_detail text" + ")";
		db.execSQL(CREATE_TABLE_DETAIL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAIL);
		// Create tables again
		onCreate(db);
	}

	// Getting all contact
	public ArrayList<DataItem> getAllDataCard() {
		ArrayList<DataItem> listData = new ArrayList<DataItem>();
		// Select All Query
		String selectQuery = "SELECT * FROM tblCard";
		dbMain = this.getWritableDatabase();
		Cursor cursor = dbMain.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				// make dataItem
				DataItem dataItem = new DataItem();
				int id_card = Integer.parseInt(cursor.getString(0));
				dataItem.setId_card(id_card);
				int contact_post = cursor.getInt(1);
				String name_card = cursor.getString(2);
				String rect_name = cursor.getString(3);
				String name_company = cursor.getString(4);
				String rect_company = cursor.getString(5);
				// byte[] image = cursor.getBlob(6);
				String image = cursor.getString(6);
				dataItem.setContact_position(contact_post);
				if (null != name_card) {
					dataItem.setNameCard(name_card);
				}
				if (null != rect_name) {
					RectItem rectItem = new RectItem();
					rectItem.makeDataRect(rect_name);
					dataItem.setRectName(rectItem);
				}
				if (null != name_company) {
					dataItem.setNameCompany(name_company);
				}
				if (null != rect_company) {
					RectItem rectItem = new RectItem();
					rectItem.makeDataRect(rect_company);
					dataItem.setRectCompany(rectItem);
				}
				if (null != image) {
					dataItem.setImage(image);
				}
				// makeDetail
				this.makeDataDetail(dataItem);
				listData.add(dataItem);
			} while (cursor.moveToNext());
		}
		dbMain.close();
		return listData;
	}

	public ArrayList<DataItem> getAllDataSort() {
		ArrayList<DataItem> listData = new ArrayList<DataItem>();
		// Select All Query
		String selectQuery = "SELECT * FROM tblCard";
		dbMain = this.getWritableDatabase();
		Cursor cursor = dbMain.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				// make dataItem
				DataItem dataItem = new DataItem();
				int id_card = Integer.parseInt(cursor.getString(0));
				dataItem.setId_card(id_card);
				int contact_post = cursor.getInt(1);
				String name_card = cursor.getString(2);
				String rect_name = cursor.getString(3);
				String name_company = cursor.getString(4);
				String rect_company = cursor.getString(5);
				// byte[] image = cursor.getBlob(6);
				String image = cursor.getString(6);
				dataItem.setContact_position(contact_post);
				if (null != name_card) {
					dataItem.setNameCard(name_card);
				}
				if (null != rect_name) {
					RectItem rectItem = new RectItem();
					rectItem.makeDataRect(rect_name);
					dataItem.setRectName(rectItem);
				}
				if (null != name_company) {
					dataItem.setNameCompany(name_company);
				}
				if (null != rect_company) {
					RectItem rectItem = new RectItem();
					rectItem.makeDataRect(rect_company);
					dataItem.setRectCompany(rectItem);
				}
				if (null != image) {
					dataItem.setImage(image);
				}
				listData.add(dataItem);
			} while (cursor.moveToNext());
		}
		dbMain.close();
		return listData;
	}

	public DataItem getDataItemById(int id_card) {
		String selectQuery = "SELECT * FROM tblCard WHERE id_card = " + id_card;
		dbMain = this.getWritableDatabase();
		Cursor cursor = dbMain.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			DataItem dataItem = new DataItem();
			dataItem.setId_card(id_card);
			int contact_post = cursor.getInt(1);
			String name_card = cursor.getString(2);
			String rect_name = cursor.getString(3);
			String name_company = cursor.getString(4);
			String rect_company = cursor.getString(5);
			// byte[] image = cursor.getBlob(6);
			String image = cursor.getString(6);
			dataItem.setContact_position(contact_post);
			if (null != name_card) {
				dataItem.setNameCard(name_card);
			}
			if (null != rect_name) {
				RectItem rectItem = new RectItem();
				rectItem.makeDataRect(rect_name);
				dataItem.setRectName(rectItem);
			}
			if (null != name_company) {
				dataItem.setNameCompany(name_company);
			}
			if (null != rect_company) {
				RectItem rectItem = new RectItem();
				rectItem.makeDataRect(rect_company);
				dataItem.setRectCompany(rectItem);
			}
			if (null != image) {
				dataItem.setImage(image);
			}
			// makeDetail
			this.makeDataDetail(dataItem);
			dbMain.close();
			return dataItem;
		}
		dbMain.close();
		return null;
	}

	public ArrayList<DataItem> getListDataDetail(ArrayList<DataItem> listData) {
		dbMain = this.getWritableDatabase();
		for (int index = 0; index < listData.size(); index++) {
			this.makeDataDetail(listData.get(index));
		}
		dbMain.close();
		return listData;
	}

	private void makeDataDetail(DataItem dataItem) {
		int id_card = dataItem.getId_card();
		String selectQuery = "SELECT * FROM tblDetail WHERE id_card = "
				+ id_card;
		Cursor cursor = dbMain.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				int id_detail = Integer.parseInt(cursor.getString(0));
				int type = cursor.getInt(2);
				String name_detail = cursor.getString(3);
				String rect_detail = cursor.getString(4);
				if (type == 1) {
					AddressItem addressItem = new AddressItem();
					addressItem.setId_detail(id_detail);
					addressItem.setId_card(id_card);
					addressItem.setAddressName(name_detail);
					if (null != rect_detail) {
						RectItem rectItem = new RectItem();
						rectItem.makeDataRect(rect_detail);
						addressItem.setRectItem(rectItem);
					}
					dataItem.getListAddress().add(addressItem);

				} else if (type == 2) {
					PhoneItem phoneItem = new PhoneItem();
					phoneItem.setId_detail(id_detail);
					phoneItem.setId_card(id_card);
					phoneItem.makePhoneData(name_detail);
					if (null != rect_detail) {
						RectItem rectItem = new RectItem();
						rectItem.makeDataRect(rect_detail);
						phoneItem.setRectItem(rectItem);
					}
					dataItem.getListPhone().add(phoneItem);

				} else if (type == 3) {
					EmailItem emailItem = new EmailItem();
					emailItem.setId_detail(id_detail);
					emailItem.setId_card(id_card);
					emailItem.setEmailName(name_detail);
					if (null != rect_detail) {
						RectItem rectItem = new RectItem();
						rectItem.makeDataRect(rect_detail);
						emailItem.setRectItem(rectItem);
					}
					dataItem.getListEmail().add(emailItem);

				} else if (type == 4) {
					WebItem webItem = new WebItem();
					webItem.setId_detail(id_detail);
					webItem.setId_card(id_card);
					webItem.setWebName(name_detail);
					if (null != rect_detail) {
						RectItem rectItem = new RectItem();
						rectItem.makeDataRect(rect_detail);
						webItem.setRectItem(rectItem);
					}
					dataItem.getListWeb().add(webItem);
				}
			} while (cursor.moveToNext());
		}

	}
	

	public int addDataCard(DataItem dataItem) {
		dbMain = getWritableDatabase();
		// Add tableCard
		ContentValues values = new ContentValues();
		values.put("contact_pos", dataItem.getContact_position());
		values.put("name_card", dataItem.getNameCard());
		if (null != dataItem.getRectName()) {
			values.put("rect_name", dataItem.getRectName().getStringSql());
		}
		values.put("name_company", dataItem.getNameCompany());
		if (null != dataItem.getRectCompany()) {
			values.put("rect_company", dataItem.getRectCompany().getStringSql());
		}
		if (null != dataItem.getImage()) {
			values.put("image", dataItem.getImage());
		}
		// Inserting Row
		int id_card = (int) dbMain.insert("tblCard", null, values);
		dataItem.setId_card(id_card);
			// Add tableDeatail
		this.addDetail(dataItem);
	
		dbMain.close();
		return id_card;
	}

	private void addDetail(DataItem dataItem) {
		int id_card = dataItem.getId_card();
		// Address
		for (int index = 0; index < dataItem.getListAddress().size(); index++) {
			AddressItem addressItem = dataItem.getListAddress().get(index);
			addressItem.setId_card(id_card);
			ContentValues values2 = new ContentValues();
			values2.put("id_card", id_card);
			values2.put("type", 1);
			values2.put("name_detail", addressItem.getAddressName());
			if (null != addressItem.getRectItem()) {
				values2.put("rect_detail", addressItem.getRectItem()
						.getStringSql());
			}
			int id_detail = (int) dbMain.insert("tblDetail", null, values2);
			addressItem.setId_detail(id_detail);
		}
		// Phone
		for (int index = 0; index < dataItem.getListPhone().size(); index++) {
			PhoneItem phoneItem = dataItem.getListPhone().get(index);
			phoneItem.setId_card(id_card);
			ContentValues values2 = new ContentValues();
			values2.put("id_card", id_card);
			values2.put("type", 2);
			values2.put("name_detail", phoneItem.getStringSql());
			if (null != phoneItem.getRectItem()) {
				values2.put("rect_detail", phoneItem.getRectItem()
						.getStringSql());
			}
			int id_detail = (int) dbMain.insert("tblDetail", null, values2);
			phoneItem.setId_detail(id_detail);
		}
		for (int index = 0; index < dataItem.getListEmail().size(); index++) {
			EmailItem emailItem = dataItem.getListEmail().get(index);
			emailItem.setId_card(id_card);
			ContentValues values2 = new ContentValues();
			values2.put("id_card", id_card);
			values2.put("type", 3);
			values2.put("name_detail", emailItem.getEmailName());
			if (null != emailItem.getRectItem()) {
				values2.put("rect_detail", emailItem.getRectItem()
						.getStringSql());
			}
			int id_detail = (int) dbMain.insert("tblDetail", null, values2);
			emailItem.setId_detail(id_detail);
		}
		for (int index = 0; index < dataItem.getListWeb().size(); index++) {
			WebItem webItem = dataItem.getListWeb().get(index);
			webItem.setId_card(id_card);
			ContentValues values2 = new ContentValues();
			values2.put("id_card", id_card);
			values2.put("type", 4);
			values2.put("name_detail", webItem.getWebName());
			if (null != webItem.getRectItem()) {
				values2.put("rect_detail", webItem.getRectItem().getStringSql());
			}
			int id_detail = (int) dbMain.insert("tblDetail", null, values2);
			webItem.setId_detail(id_detail);
		}
	}

	public void deleteCard(DataItem dataItem) {
		dbMain = getWritableDatabase();
		String deleteCard = "DELETE FROM tblCard WHERE id_card = "
				+ dataItem.getId_card();
		dbMain.execSQL(deleteCard);
		String deleteDetail = "DELETE FROM tblDetail WHERE id_card = "
				+ dataItem.getId_card();
		dbMain.execSQL(deleteDetail);
		dbMain.close();
	}

	public int editCard(DataItem dataItem) {
		deleteCard(dataItem);
		return addDataCard(dataItem);
	}

	public void updateContactPosition(DataItem dataItem) {
		int id_card = dataItem.getId_card();
		int pos = dataItem.getContact_position();
		dbMain = getWritableDatabase();
		String update = "update tblCard set contact_pos = " + pos
				+ " where id_card = " + id_card;
		dbMain.execSQL(update);
		dbMain.close();
	}
}