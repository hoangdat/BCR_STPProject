package com.stpproject.viewutil;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.bcrstpproject.EditContactsActivity;
import com.example.bcrstpproject.R;
import com.stpproject.imgprocessing.MyUtilsImage;
import com.stpproject.model.AddressItem;
import com.stpproject.model.DataItem;
import com.stpproject.model.EmailItem;
import com.stpproject.model.PhoneItem;
import com.stpproject.model.RectItem;
import com.stpproject.model.WebItem;

public class AsyncGetDataForEdit extends AsyncTask<Integer, Void, DataItem> {

	EditContactsActivity mContext;
	LayoutInflater mInflater;
	private int mDataWhere;
	private ProgressDialog mProgressDialog;
	private String mImagePath;
	DataBaseHandler mDB;

	public AsyncGetDataForEdit(Context context, LayoutInflater inflater,
			int dataWhere, String imagePath) {
		mContext = (EditContactsActivity) context;
		mInflater = inflater;
		this.mDataWhere = dataWhere;
		if (imagePath != null) {
			this.mImagePath = imagePath;
		}
		mDB = new DataBaseHandler(context);
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage("Getting data ...");
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		super.onPreExecute();
	}

	@Override
	protected DataItem doInBackground(Integer... params) {
		if (mDataWhere == Constant.VALUE_SEND_FROM_DETAILS) {
			// test tam;
			int id = params[0];
			if (id < 1)
				return null;
			DataItem dataItem = mDB.getDataItemById(id);
			mImagePath = dataItem.getImage();
			mContext.mDataItemReceive = dataItem;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inMutable = true;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			mContext.mBitmap = BitmapFactory.decodeFile(mImagePath, options);
			return dataItem;
		} else if (mDataWhere == Constant.VALUE_SEND_FROM_PROCESS) {
			if (ManagerSystemGlobal.gloSendData != null) {
				mContext.mDataItemReceive = ManagerSystemGlobal.gloSendData;
				if (mImagePath != null) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inMutable = true;
					mContext.mBitmap = BitmapFactory.decodeFile(mImagePath, options);
				}
				return ManagerSystemGlobal.gloSendData;
			} else {
				return null;
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(DataItem result) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			try {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (result == null) {
			Log.d("NUll", "result nulllllllllll");
			return;
		}
		loadImageEdit(mImagePath);
		loadNameEdit(result.getNameCard(), result.getRectName());
		loadDynamicPhoneEdit(result.getListPhone());
		loadDynamicCompanyEdit(result.getNameCompany(), result.getRectCompany());
		loadDynamicAddressEdit(result.getListAddress());
		loadDynamicEmailEdit(result.getListEmail());
		loadDynamicWebsiteEdit(result.getListWeb());
		super.onPostExecute(result);
	}


	public void loadImageEdit(String imagePath) {
		Bitmap btm = BitmapFactory.decodeFile(imagePath);
		if (btm != null) {
			mContext.imgViewContactEdit.setImageBitmap(btm);
		} else {
			// truong hop anh null thi sao
		}
	}

	public void loadNameEdit(String strName, final RectItem rect) {
		if (strName == null)
			return;
		mContext.mEdtNameEditOri.setText(strName);
		NameEditHolder nameHolder = new NameEditHolder(mContext.mEdtNameEditOri);
		if (rect != null) {
			nameHolder.setRectItem(rect);
			mContext.mEdtNameEditOri.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						mContext.mBtmDraw = MyUtilsImage.drawRectInEdit(mContext.mBitmap, rect);
						mContext.imgViewContactEdit.setImageBitmap(mContext.mBtmDraw);	
					}
				}
			});
		} else {
			mContext.mEdtNameEditOri.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						mContext.imgViewContactEdit.setImageBitmap(mContext.mBitmap);	
					}
				}
			});
		}
		mContext.mNameEditHolder = nameHolder;
	}

	public void loadDynamicPhoneEdit(ArrayList<PhoneItem> lstPhone) {
		String[] fider = { "Work", "Mobile", "Fax" };
		if (lstPhone == null)
			return;
		int iSz = lstPhone.size();
		if (iSz <= 0)
			return;
		for (int i = 0; i < iSz; i++) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
					android.R.layout.simple_spinner_item, fider);
			adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
			final View v = mInflater.inflate(R.layout.row_phone_edit,
					mContext.mLnrPhoneEdit, false);
			final EditText edtPhone = (EditText) v
					.findViewById(R.id.edtPhoneNumberEDIT);
			final Spinner spinner = (Spinner) v
					.findViewById(R.id.spinTypePhone);
			spinner.setAdapter(adapter);
			ImageButton imgBtnMinus = (ImageButton) v
					.findViewById(R.id.btnImgSubstractPhoneEDIT);
			final PhoneEditHolder phoneHolder = new PhoneEditHolder(edtPhone,
					imgBtnMinus, spinner);
			PhoneItem phone = lstPhone.get(i);
			spinner.setSelection(phone.getType());
			edtPhone.setText(phone.getPhoneName());
			if (phone.getRectItem() != null) {
				final RectItem rect = phone.getRectItem();
				phoneHolder.setRectItem(rect);
				edtPhone.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							mContext.mBtmDraw = MyUtilsImage.drawRectInEdit(mContext.mBitmap, rect);
							mContext.imgViewContactEdit.setImageBitmap(mContext.mBtmDraw);
						}
					}
				});
			} else {
				edtPhone.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						mContext.imgViewContactEdit.setImageBitmap(mContext.mBitmap);	
					}
				});
			}
			imgBtnMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mContext.mLnrPhoneEdit.removeView(v);
					mContext.mLstPhoneEditHolder.remove(phoneHolder);
				}
			});
			mContext.mLnrPhoneEdit.addView(v);
			mContext.mLstPhoneEditHolder.add(phoneHolder);
		}
	}

	public void loadDynamicEmailEdit(ArrayList<EmailItem> lstEmail) {
		if (lstEmail == null)
			return;
		int iSz = lstEmail.size();
		if (iSz <= 0)
			return;
		for (int i = 0; i < iSz; i++) {
			final View v = mInflater.inflate(
					R.layout.row_email_web_add_company_edit,
					mContext.mLnrEmailEdit, false);
			final EditText edtEmail = (EditText) v
					.findViewById(R.id.edtInfoEDIT);
			ImageButton imgBtnMinus = (ImageButton) v
					.findViewById(R.id.btnImgSubstractInfoEDIT);
			final MailEditHolder mailHolder = new MailEditHolder(edtEmail,
					imgBtnMinus);
			EmailItem email = lstEmail.get(i);
			edtEmail.setText(email.getEmailName());
			if (email.getRectItem() != null) {
				final RectItem rect = email.getRectItem();
				mailHolder.setRectItem(rect);
				edtEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						mContext.mBtmDraw = MyUtilsImage.drawRectInEdit(mContext.mBitmap, rect);
						mContext.imgViewContactEdit.setImageBitmap(mContext.mBtmDraw);
					}
				});
			} else {
				edtEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						mContext.imgViewContactEdit.setImageBitmap(mContext.mBitmap);	
					}
				});
			}
			imgBtnMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mContext.mLnrEmailEdit.removeView(v);
					mContext.mLstMailEditHolder.remove(mailHolder);
				}
			});
			mContext.mLnrEmailEdit.addView(v);
			mContext.mLstMailEditHolder.add(mailHolder);
		}
	}

	public void loadDynamicCompanyEdit(String strCompName, final RectItem rectComp) {
		if (strCompName == null)
			return;
		mContext.isHaveCompany = true;
		mContext.imgBtnAddCompanyField.setVisibility(View.INVISIBLE);
		final View v = mInflater.inflate(
				R.layout.row_email_web_add_company_edit,
				mContext.mLnrCompanyEdit, false);
		EditText edtComp = (EditText) v.findViewById(R.id.edtInfoEDIT);
		ImageButton imgBtnMinus = (ImageButton) v
				.findViewById(R.id.btnImgSubstractInfoEDIT);
		final CompanyEditHolder compHolder = new CompanyEditHolder(edtComp,
				imgBtnMinus);
		edtComp.setText(strCompName);
		if (rectComp != null) {
			compHolder.setRectItem(rectComp);
			edtComp.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					mContext.mBtmDraw = MyUtilsImage.drawRectInEdit(mContext.mBitmap, rectComp);
					mContext.imgViewContactEdit.setImageBitmap(mContext.mBtmDraw);
				}
			});
		} else {
			edtComp.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					mContext.imgViewContactEdit.setImageBitmap(mContext.mBitmap);		
				}
			});
		}
		imgBtnMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mContext.isHaveCompany = false;
				mContext.imgBtnAddCompanyField.setVisibility(View.VISIBLE);
				mContext.mLnrCompanyEdit.removeView(v);
				mContext.mLstCompanyEditHolder.remove(compHolder);
			}
		});
		mContext.mLnrCompanyEdit.addView(v);
		mContext.mLstCompanyEditHolder.add(compHolder);
	}

	public void loadDynamicAddressEdit(ArrayList<AddressItem> lstAdd) {
		if (lstAdd == null)
			return;
		int iSz = lstAdd.size();
		if (iSz <= 0)
			return;
		for (int i = 0; i < iSz; i++) {
			final View v = mInflater.inflate(
					R.layout.row_email_web_add_company_edit,
					mContext.mLnrAddressEdit, false);
			final EditText edtAddress = (EditText) v
					.findViewById(R.id.edtInfoEDIT);
			ImageButton imgBtnMinus = (ImageButton) v
					.findViewById(R.id.btnImgSubstractInfoEDIT);
			final AddressEditHolder addHolder = new AddressEditHolder(
					edtAddress, imgBtnMinus);
			AddressItem address = lstAdd.get(i);
			edtAddress.setText(address.getAddressName()); // co can check chuoi
															// nay null hay ko
			if (address.getRectItem() != null) {
				final RectItem rect = address.getRectItem();
				addHolder.setRectItem(rect);
				edtAddress.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						mContext.mBtmDraw = MyUtilsImage.drawRectInEdit(mContext.mBitmap, rect);
						mContext.imgViewContactEdit.setImageBitmap(mContext.mBtmDraw);
					}
				});
			} else {
				edtAddress.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						mContext.imgViewContactEdit.setImageBitmap(mContext.mBitmap);	
					}
				});
			}
			imgBtnMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mContext.mLnrAddressEdit.removeView(v);
					mContext.mLstAddEditHolder.remove(addHolder);
				}
			});
			mContext.mLnrAddressEdit.addView(v);
			mContext.mLstAddEditHolder.add(addHolder);
		}
	}

	public void loadDynamicWebsiteEdit(ArrayList<WebItem> lstWeb) {
		if (lstWeb == null)
			return;
		int iSz = lstWeb.size();
		if (iSz <= 0)
			return;
		for (int i = 0; i < iSz; i++) {
			final View v = mInflater.inflate(
					R.layout.row_email_web_add_company_edit,
					mContext.mLnrWebsiteEdit, false);
			final EditText edtWeb = (EditText) v.findViewById(R.id.edtInfoEDIT);
			ImageButton imgBtnMinus = (ImageButton) v
					.findViewById(R.id.btnImgSubstractInfoEDIT);
			final WebEditHolder webHolder = new WebEditHolder(edtWeb,
					imgBtnMinus);
			WebItem web = lstWeb.get(i);
			edtWeb.setText(web.getWebName());
			if (web.getRectItem() != null) {
				final RectItem rect = web.getRectItem();
				webHolder.setRectItem(rect);
				edtWeb.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						mContext.mBtmDraw = MyUtilsImage.drawRectInEdit(mContext.mBitmap, rect);
						mContext.imgViewContactEdit.setImageBitmap(mContext.mBtmDraw);
					}
				});
			} else {
				edtWeb.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						mContext.imgViewContactEdit.setImageBitmap(mContext.mBitmap);	
					}
				});
			}

			imgBtnMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mContext.mLnrWebsiteEdit.removeView(v);
					mContext.mLstWebEditHolder.remove(webHolder);
				}
			});
			mContext.mLnrWebsiteEdit.addView(v);
			mContext.mLstWebEditHolder.add(webHolder);
		}
	}

}
