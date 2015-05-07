package com.stpproject.viewutil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bcrstpproject.R;
import com.stpproject.model.AddressItem;
import com.stpproject.model.DataItem;
import com.stpproject.model.EmailItem;
import com.stpproject.model.PhoneItem;
import com.stpproject.model.WebItem;

public class FeaturesCard {
	private Context context;

	public FeaturesCard(Context context) {
		this.context = context;
	}

	public int addContact(DataItem dataItem) {
		if (dataItem.getContact_position() != -1) {
			deleteContact(dataItem.getContact_position());
		}
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
				.build());

		// ------------------------------------------------------ Names
		if (dataItem.getNameCard() != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
							dataItem.getNameCard()).build());
		}
		// ------------------------------------------------------ Photo
		if (dataItem.getImage() != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(dataItem.getImage());
			if (bitmap != null) {
				bitmap = Bitmap.createScaledBitmap(bitmap, 1024, 768, true);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte imageInByte[] = stream.toByteArray();
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(
								ContactsContract.Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
						.withValue(
								ContactsContract.CommonDataKinds.Photo.PHOTO,
								imageInByte).build());
			}

		}
		// ------------------------------------------------------ Company
		if (dataItem.getNameCompany() != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.Organization.COMPANY,
							dataItem.getNameCompany()).build());
		}
		// ------------------------------------------------------ Phone
		for (int index = 0; index < dataItem.getListPhone().size(); index++) {
			PhoneItem phoneItem = dataItem.getListPhone().get(index);
			if (phoneItem.getType() == 0) {
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(
								ContactsContract.Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
						.withValue(
								ContactsContract.CommonDataKinds.Phone.NUMBER,
								phoneItem.getPhoneName())
						.withValue(
								ContactsContract.CommonDataKinds.Phone.TYPE,
								ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
						.build());
			} else if (phoneItem.getType() == 1) {
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(
								ContactsContract.Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
						.withValue(
								ContactsContract.CommonDataKinds.Phone.NUMBER,
								phoneItem.getPhoneName())
						.withValue(
								ContactsContract.CommonDataKinds.Phone.TYPE,
								ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
						.build());
			} else {
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(
								ContactsContract.Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(
								ContactsContract.Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
						.withValue(
								ContactsContract.CommonDataKinds.Phone.NUMBER,
								phoneItem.getPhoneName())
						.withValue(
								ContactsContract.CommonDataKinds.Phone.TYPE,
								ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK)
						.build());
			}
		}
		// ------------------------------------------------------ Email
		for (int index = 0; index < dataItem.getListEmail().size(); index++) {
			EmailItem emailItem = dataItem.getListEmail().get(index);
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA,
							emailItem.getEmailName())
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
							ContactsContract.CommonDataKinds.Email.TYPE_WORK)
					.build());
		}
		// ------------------------------------------------------ Web
		for (int index = 0; index < dataItem.getListWeb().size(); index++) {
			WebItem webItem = dataItem.getListWeb().get(index);
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Website.DATA,
							webItem.getWebName())
					.withValue(ContactsContract.CommonDataKinds.Website.TYPE,
							ContactsContract.CommonDataKinds.Website.TYPE_WORK)
					.build());
		}

		// ------------------------------------------------------ Address
		for (int index = 0; index < dataItem.getListAddress().size(); index++) {
			AddressItem addressItem = dataItem.getListAddress().get(index);
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.STREET,
							addressItem.getAddressName())
					.withValue(
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
							ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
					.build());
		}
		// Asking the Contact provider to create a new contact
		try {
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
					ops);
			int contactId = getContactId();
			return contactId;
		} catch (Exception e) {
			// e.printStackTrace();
			// Toast.makeText(context, "Exception: " + e.getMessage(),
			// Toast.LENGTH_SHORT).show();
			return -1;
		}
	}

	private void deleteContact(int contact_id) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		String[] args = new String[] { contact_id + "" };
		ops.add(ContentProviderOperation.newDelete(RawContacts.CONTENT_URI)
				.withSelection(RawContacts.CONTACT_ID + "=?", args).build());
		try {
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
					ops);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int getContactId() {
		int contact_id = 0;
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));
				contact_id = Integer.parseInt(id);
			}
		}
		return contact_id;
	}

	public void showDialogEmail(ArrayList<DataItem> listData) {
		final ArrayList<StringItem> listString = new ArrayList<StringItem>();
		for (int index = 0; index < listData.size(); index++) {
			DataItem dataItem = listData.get(index);
			if (null == dataItem.getNameCard()) {
				StringItem stringItem = new StringItem("Unknows");
				listString.add(stringItem);
			} else {
				StringItem stringItem = new StringItem(dataItem.getNameCard());
				listString.add(stringItem);
			}
			for (int index2 = 0; index2 < dataItem.getListEmail().size(); index2++) {
				StringItem stringItem = new StringItem(dataItem.getListEmail()
						.get(index2).getEmailName());
				stringItem.setType(1);
				listString.add(stringItem);
			}
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// Get the layout inflater
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfeatures_custom, null);
		CustomArrayAdapter adapter = new CustomArrayAdapter(context, listString);
		ListView listView = (ListView) view.findViewById(R.id.listViewFeatures);
		listView.setAdapter(adapter);
		builder.setTitle("Send email")
				.setView(view)
				// Add action buttons
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						StringBuilder smsBuider = new StringBuilder();
						smsBuider.append("mailto:");
						for (int index3 = 0; index3 < listString.size(); index3++) {
							StringItem item = listString.get(index3);
							if (item.getType() == 1 && item.iCheck == 1) {
								smsBuider.append(item.getTitle() + ";");
							}
						}
						if (smsBuider.length() > 7) {
							smsBuider.deleteCharAt(smsBuider.length() - 1);
							Intent smsIntent = new Intent(Intent.ACTION_SENDTO,
									Uri.parse(smsBuider.toString()));
							context.startActivity(smsIntent);
						}
					}
				})
				.setNegativeButton("CANCEL",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
		Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
		nbutton.setTextColor(Color.rgb(59, 185, 255));
		Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
		pbutton.setTextColor(Color.rgb(59, 185, 255));
	}

	public void showDialogSms(ArrayList<DataItem> listData) {
		final ArrayList<StringItem> listString = new ArrayList<StringItem>();
		for (int index = 0; index < listData.size(); index++) {
			DataItem dataItem = listData.get(index);
			if (null == dataItem.getNameCard()) {
				StringItem stringItem = new StringItem("Unknows");
				listString.add(stringItem);
			} else {
				StringItem stringItem = new StringItem(dataItem.getNameCard());
				listString.add(stringItem);
			}
			for (int index2 = 0; index2 < dataItem.getListPhone().size(); index2++) {
				StringItem stringItem = new StringItem(dataItem.getListPhone()
						.get(index2).getPhoneName());
				stringItem.setType(1);
				listString.add(stringItem);
			}
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// Get the layout inflater
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View view = inflater.inflate(R.layout.dialogfeatures_custom, null);
		CustomArrayAdapter adapter = new CustomArrayAdapter(context, listString);
		ListView listView = (ListView) view.findViewById(R.id.listViewFeatures);
		listView.setAdapter(adapter);
		builder.setTitle("Send sms")
				.setView(view)
				// Add action buttons
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						StringBuilder smsBuider = new StringBuilder();
						smsBuider.append("smsto:");
						for (int index3 = 0; index3 < listString.size(); index3++) {
							StringItem item = listString.get(index3);
							if (item.getType() == 1 && item.iCheck == 1) {
								smsBuider.append(item.getTitle() + ";");
							}
						}
						if (smsBuider.length() > 6) {
							smsBuider.deleteCharAt(smsBuider.length() - 1);
							Intent smsIntent = new Intent(Intent.ACTION_SENDTO,
									Uri.parse(smsBuider.toString()));
							context.startActivity(smsIntent);
						}
					}
				})
				.setNegativeButton("CANCEL",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
		Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
		nbutton.setTextColor(Color.rgb(59, 185, 255));
		Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
		pbutton.setTextColor(Color.rgb(59, 185, 255));
	}

	public void showDialogCall(DataItem dataItem) {
		final String[] stringsCall = new String[dataItem.getListPhone().size()];
		for (int index = 0; index < dataItem.getListPhone().size(); index++) {
			stringsCall[index] = dataItem.getListPhone().get(index)
					.getPhoneName();
		}
		AlertDialog.Builder builderCall = new AlertDialog.Builder(context);
		builderCall.setTitle("Call to:").setItems(stringsCall,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent smsIntent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + stringsCall[which]));
						context.startActivity(smsIntent);
					}
				});
		builderCall.create().show();
	}

	class CustomArrayAdapter extends ArrayAdapter<StringItem> {
		private ArrayList<StringItem> listString;
		private Context contextArr;

		public CustomArrayAdapter(Context context,
				ArrayList<StringItem> listStringIteam) {
			super(context, R.layout.features_rowitem_checkbox, listStringIteam);
			this.listString = listStringIteam;
			this.contextArr = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			final StringItem stringItem = listString.get(position);
			if (stringItem.getType() == 0) {
				LayoutInflater inflater = ((Activity) contextArr)
						.getLayoutInflater();
				view = inflater.inflate(R.layout.features_rowitem_name, parent,
						false);
				TextView textName = (TextView) view
						.findViewById(R.id.features_nameTv);
				if (stringItem.getTitle().length() < 31) {
					textName.setText(stringItem.getTitle());
				} else {
					textName.setText(stringItem.getTitle().substring(0, 30)
							+ "...");
				}

			} else {
				LayoutInflater inflater = ((Activity) contextArr)
						.getLayoutInflater();
				view = inflater.inflate(R.layout.features_rowitem_checkbox,
						parent, false);
				TextView textName = (TextView) view
						.findViewById(R.id.features_emailorSms);
				if (stringItem.getTitle().length() < 21) {
					textName.setText(stringItem.getTitle());
				} else {
					textName.setText(stringItem.getTitle().substring(0, 20)
							+ "...");
				}
				final CheckBox checkBox = (CheckBox) view
						.findViewById(R.id.features_checkBox);
				if (stringItem.iCheck == 1) {
					checkBox.setChecked(true);
				}
				checkBox.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						stringItem.setiCheck(1 - stringItem.getiCheck());
					}
				});
			}
			return view;
		}

	}

	class StringItem {
		private String title;
		private int type = 0;
		private int iCheck = 0;

		public StringItem(String str) {
			this.title = str;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getiCheck() {
			return iCheck;
		}

		public void setiCheck(int iCheck) {
			this.iCheck = iCheck;
		}
	}
}
