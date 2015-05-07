package com.stpproject.viewutil;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bcrstpproject.DetailsContactActivity;
import com.example.bcrstpproject.R;
import com.stpproject.model.AddressItem;
import com.stpproject.model.DataItem;
import com.stpproject.model.EmailItem;
import com.stpproject.model.PhoneItem;
import com.stpproject.model.WebItem;

public class AsyncGetDataItem extends AsyncTask<Integer, Void, DataItem> {

	private DetailsContactActivity mContext;
	LayoutInflater mInflater;
	private DataBaseHandler mDB;

	public AsyncGetDataItem(Context context) {
		mContext = (DetailsContactActivity) context;
		mInflater = mContext.getLayoutInflater();
		mDB = new DataBaseHandler(context);
	}

	@Override
	protected DataItem doInBackground(Integer... params) {
		Integer iID = (Integer) params[0];
		if (iID == -1)
			return null;
		DataItem dataItem = mDB.getDataItemById(iID);
		return dataItem;
	}

	@Override
	protected void onPostExecute(DataItem result) {
		super.onPostExecute(result);

		if (result == null) {
			Toast.makeText(mContext,
					"Not able to load contact. Please try agian!",
					Toast.LENGTH_LONG).show();
			return;
		}

		Bitmap btm = BitmapFactory.decodeFile(result.getImage());
		if (btm == null) {
			btm = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.maybay);
		}

		mContext.mImgViewContact.setImageBitmap(btm);
		mContext.mTxtCompanyOverlay.setText(result.getNameCompany());
		mContext.mTxtContactNameOverlay.setText(result.getNameCard());

		addDynamicPhone(result.getListPhone());
		addDynamicEmail(result.getListEmail());
		addDynamicAddress(result.getListAddress());
		addDynamicWebsite(result.getListWeb());
		setEvenCallSms(result);
		mContext.mDataItem = result;
	}

	public void addDynamicPhone(ArrayList<PhoneItem> lstPhone) {
		if (lstPhone == null)
			return;
		int iSz = lstPhone.size();
		mContext.mLnrPhone.removeAllViews();
		if (iSz <= 0)
			return;
		for (int i = 0; i < iSz; i++) {
			final View v = mInflater.inflate(R.layout.row_phone_details,
					mContext.mLnrPhone, false);
			final TextView txtPhoneNumber = (TextView) v
					.findViewById(R.id.txtPhoneNumberDetails);
			final TextView txttype = (TextView) v
					.findViewById(R.id.tvTypePhone);
			if (lstPhone.get(i).getType() == 0) {
				txttype.setText("Work phone");

			} else if (lstPhone.get(i).getType() == 1) {
				txttype.setText("Mobile phone");
			} else {
				txttype.setText("Fax");
			}

			ImageButton imgBtnCall = (ImageButton) v
					.findViewById(R.id.btnImgCallDetails);
			txtPhoneNumber.setText(lstPhone.get(i).getPhoneName());

			imgBtnCall.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent smsIntent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:" + txtPhoneNumber.getText()));
					mContext.startActivity(smsIntent);
				}
			});
			PhoneViewFieldHolder holder = new PhoneViewFieldHolder(
					txtPhoneNumber, imgBtnCall);
			mContext.mLnrPhone.addView(v);
			mContext.lstPhone.add(holder);
		}
	}

	public void addDynamicEmail(ArrayList<EmailItem> lstEmail) {
		if (lstEmail == null)
			return;
		int iSz = lstEmail.size();
		mContext.mLnrEmail.removeAllViews();
		if (iSz <= 0)
			return;
		for (int i = 0; i < iSz; i++) {
			final View v = mInflater.inflate(R.layout.row_mail_details,
					mContext.mLnrEmail, false);
			final TextView txtEmail = (TextView) v
					.findViewById(R.id.txtEmailDetails);
			ImageButton imgBtnSendmail = (ImageButton) v
					.findViewById(R.id.btnImgSendMailDetails);
			txtEmail.setText(lstEmail.get(i).getEmailName());

			imgBtnSendmail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri
							.parse("mailto:" + txtEmail.getText()));
					mContext.startActivity(smsIntent);
				}
			});
			EmailViewFieldHolder holder = new EmailViewFieldHolder(txtEmail,
					imgBtnSendmail);
			mContext.mLnrEmail.addView(v);
			mContext.lstEmail.add(holder);
		}
	}

	public void addDynamicAddress(ArrayList<AddressItem> lstAddress) {
		if (lstAddress == null)
			return;
		int iSz = lstAddress.size();
		mContext.mLnrAddress.removeAllViews();
		if (iSz <= 0)
			return;
		for (int i = 0; i < iSz; i++) {
			final View v = mInflater.inflate(R.layout.row_address_details,
					mContext.mLnrAddress, false);
			final TextView txtAddress = (TextView) v
					.findViewById(R.id.txtAddressDetails);
			txtAddress.setText(lstAddress.get(i).getAddressName());
			mContext.mLnrAddress.addView(v);
		}
	}

	public void addDynamicWebsite(ArrayList<WebItem> lstWeb) {
		if (lstWeb == null)
			return;
		int iSz = lstWeb.size();
		mContext.mLnrWebsite.removeAllViews();
		if (iSz <= 0)
			return;
		for (int i = 0; i < iSz; i++) {
			final View v = mInflater.inflate(R.layout.row_web_details,
					mContext.mLnrWebsite, false);
			final TextView txtWebsite = (TextView) v
					.findViewById(R.id.txtWebsiteDetails);
			ImageButton imgBtnGoWebsite = (ImageButton) v
					.findViewById(R.id.btnImgSurfWebDetails);
			txtWebsite.setText(lstWeb.get(i).getWebName());

			imgBtnGoWebsite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent itenWeb = new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://" + txtWebsite.getText()));
					mContext.startActivity(itenWeb);
				}
			});
			WebViewFiledHolder holder = new WebViewFiledHolder(txtWebsite,
					imgBtnGoWebsite);
			mContext.mLnrWebsite.addView(v);
			mContext.lstWeb.add(holder);
		}
	}

	public void setEvenCallSms(final DataItem dataItem) {
		final FeaturesCard featuresCard = new FeaturesCard(mContext);
		final ArrayList<DataItem> listSmsData = new ArrayList<DataItem>();
		listSmsData.add(dataItem);
		mContext.mImgBtnMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				featuresCard.showDialogSms(listSmsData);
			}
		});
		mContext.mImgBtnPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				featuresCard.showDialogCall(dataItem);
			}
		});
	}
}
