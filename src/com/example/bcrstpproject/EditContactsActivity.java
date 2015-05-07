package com.example.bcrstpproject;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.stpproject.model.AddressItem;
import com.stpproject.model.DataItem;
import com.stpproject.model.EmailItem;
import com.stpproject.model.PhoneItem;
import com.stpproject.model.WebItem;
import com.stpproject.viewutil.AddressEditHolder;
import com.stpproject.viewutil.AsyncGetDataForEdit;
import com.stpproject.viewutil.AsyncSaveToDB;
import com.stpproject.viewutil.CompanyEditHolder;
import com.stpproject.viewutil.Constant;
import com.stpproject.viewutil.MailEditHolder;
import com.stpproject.viewutil.NameEditHolder;
import com.stpproject.viewutil.PhoneEditHolder;
import com.stpproject.viewutil.WebEditHolder;

public class EditContactsActivity extends Activity implements OnClickListener {

	public ImageView imgViewContactEdit;
	LayoutInflater mInflater;
	private int mDataFromWhere;
	private String mImagePath;
	private ActionBar mActionBar;
	public LinearLayout mLnrPhoneEdit, mLnrCompanyEdit, mLnrAddressEdit,
			mLnrWebsiteEdit, mLnrEmailEdit;
	public ImageButton imgBtnAddPhoneField, imgBtnAddEmailField,
			imgBtnAddAddressField, imgBtnAddWebsiteField,
			imgBtnAddCompanyField;
	public EditText mEdtNameEditOri;
	public boolean isHaveCompany = false;
	public NameEditHolder mNameEditHolder = null;
	public ArrayList<WebEditHolder> mLstWebEditHolder = new ArrayList<WebEditHolder>();
	public ArrayList<AddressEditHolder> mLstAddEditHolder = new ArrayList<AddressEditHolder>();
	public ArrayList<PhoneEditHolder> mLstPhoneEditHolder = new ArrayList<PhoneEditHolder>();
	public ArrayList<CompanyEditHolder> mLstCompanyEditHolder = new ArrayList<CompanyEditHolder>();
	public ArrayList<MailEditHolder> mLstMailEditHolder = new ArrayList<MailEditHolder>();
	public DataItem mDataItemReceive;
	
	public Bitmap mBitmap, mBtmDraw;
	private Animation click;
	public ImageButton imageButtonBack,imageButtonSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_edit_contacts);
		click = AnimationUtils.loadAnimation(this, R.anim.click);
		mInflater = getLayoutInflater();
		customActionBar();
		intiUI();
		getData();
	}

	private void getData() {
		Intent intent = getIntent();
		int where = intent.getIntExtra(Constant.KEY_SEND_FROM,
				Constant.VALUE_NOT_KNOW_WHERE);
		Bundle bundle = intent.getExtras();
		mImagePath = bundle.getString(Constant.KEY_IMAGE_PATH_FOR_PROCESSING);
		
//		Toast.makeText(getApplicationContext(), "ImagP: " + mImagePath,
//				Toast.LENGTH_LONG).show();

		switch (where) {
		case Constant.VALUE_NOT_KNOW_WHERE:
			mDataFromWhere = Constant.VALUE_NOT_KNOW_WHERE;
			Toast.makeText(getApplicationContext(),
					getString(R.string.message_not_know_where),
					Toast.LENGTH_SHORT).show();
			break;
		case Constant.VALUE_SEND_FROM_PROCESS:
			mDataFromWhere = Constant.VALUE_SEND_FROM_PROCESS;
			AsyncGetDataForEdit asyncGetItem = new AsyncGetDataForEdit(EditContactsActivity.this, mInflater,
					Constant.VALUE_SEND_FROM_PROCESS, mImagePath);
			asyncGetItem.execute(0);
			break;
		case Constant.VALUE_SEND_FROM_DETAILS:
			mDataFromWhere = Constant.VALUE_SEND_FROM_DETAILS;
			int id = bundle.getInt(Constant.KEY_ITEM_ID_SEND);
			AsyncGetDataForEdit asyncGet = new AsyncGetDataForEdit(EditContactsActivity.this, mInflater,
					Constant.VALUE_SEND_FROM_DETAILS, null);
			asyncGet.execute(id); // test int chinh la id trong CSDL
			break;
		default:
			break;
		}
	}

	public void customActionBar() {
		mActionBar = getActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setBackgroundDrawable(null);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View custom_actionbar_edit = inflater.inflate(
				R.layout.custom_actionbar_edit, null);

		imageButtonBack = (ImageButton) custom_actionbar_edit
				.findViewById(R.id.btnImgBackActionBarEdit);
		imageButtonSave = (ImageButton) custom_actionbar_edit
				.findViewById(R.id.btnImgSaveActionBarEdit);

		imageButtonBack.setOnClickListener(this);
		imageButtonSave.setOnClickListener(this);

		mActionBar.setCustomView(custom_actionbar_edit);
		mActionBar.setDisplayShowCustomEnabled(true);
	}

	public void intiUI() {
		imgViewContactEdit = (ImageView) findViewById(R.id.imgContactEdit);

		mLnrAddressEdit = (LinearLayout) findViewById(R.id.lnrAddressEdit);
		mLnrCompanyEdit = (LinearLayout) findViewById(R.id.lnrCompanyEdit);
		mLnrPhoneEdit = (LinearLayout) findViewById(R.id.lnrPhoneEdit);
		mLnrWebsiteEdit = (LinearLayout) findViewById(R.id.lnrWebsiteEdit);
		mLnrEmailEdit = (LinearLayout) findViewById(R.id.lnrEmailEdit);

		mEdtNameEditOri = (EditText) findViewById(R.id.edtNameEdit);

		imgBtnAddPhoneField = (ImageButton) findViewById(R.id.btnImgPlusPhone);
		imgBtnAddEmailField = (ImageButton) findViewById(R.id.btnImgPlusEmail);
		imgBtnAddAddressField = (ImageButton) findViewById(R.id.btnImgPlusAdd);
		imgBtnAddWebsiteField = (ImageButton) findViewById(R.id.btnImgPlusWeb);
		imgBtnAddCompanyField = (ImageButton) findViewById(R.id.btnImgPlusCom);

		imgBtnAddPhoneField.setOnClickListener(this);
		imgBtnAddEmailField.setOnClickListener(this);
		imgBtnAddCompanyField.setOnClickListener(this);
		imgBtnAddAddressField.setOnClickListener(this);
		imgBtnAddCompanyField.setOnClickListener(this);
		imgBtnAddWebsiteField.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar_edit, menu);// thay doi cho
		return true;
	}

	@Override
	public void onClick(View v) {
		int iID = v.getId();
		switch (iID) {
		case R.id.btnImgPlusPhone:
			imgBtnAddPhoneField.startAnimation(click);
			addDynamicPhoneEdit();
			break;

		case R.id.btnImgPlusAdd:
			imgBtnAddAddressField.startAnimation(click);
			addDynamicAddressEdit();
			break;

		case R.id.btnImgPlusCom:
			imgBtnAddCompanyField.startAnimation(click);
			if (isHaveCompany) {
				return;
			}
			
			addDynamicCompanyEdit();
			break;

		case R.id.btnImgPlusEmail:
			imgBtnAddEmailField.startAnimation(click);
			addDynamicEmailEdit();
			break;

		case R.id.btnImgPlusWeb:
			imgBtnAddWebsiteField.startAnimation(click);
			addDynamicWebsiteEdit();
			break;

		case R.id.btnImgBackActionBarEdit:
			imageButtonBack.startAnimation(click);
			onBackPressed();
			break;
			
		case R.id.btnImgSaveActionBarEdit:
			imageButtonSave.startAnimation(click);
			if (mDataFromWhere == Constant.VALUE_SEND_FROM_DETAILS) {
				addContact();
			} else if (mDataFromWhere == Constant.VALUE_SEND_FROM_PROCESS) {
				addContact();
			}
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onPause() {
		
		if (mBitmap != null) {
			mBitmap.recycle();
			System.gc();
		}
		
		if (mBtmDraw != null) {
			mBtmDraw.recycle();
			System.gc();
		}
		super.onPause();
	}
	
	
	
	@Override
	public void onBackPressed() {
		if (mDataFromWhere == Constant.VALUE_SEND_FROM_PROCESS) {
			if (mImagePath != null) {
				File f = new File(mImagePath);
				if (f.exists()) {
					f.delete();
				}
			}
			finish();
		}
		super.onBackPressed();
	}

	public void addDynamicPhoneEdit() {
		String[] fider = { "Work", "Mobile", "Fax" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, fider);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		final View v = mInflater.inflate(R.layout.row_phone_edit,
				mLnrPhoneEdit, false);
		EditText edtPhone = (EditText) v.findViewById(R.id.edtPhoneNumberEDIT);
		ImageButton imgBtnMinusPhone = (ImageButton) v
				.findViewById(R.id.btnImgSubstractPhoneEDIT);
		Spinner spinner = (Spinner) v.findViewById(R.id.spinTypePhone);
		spinner.setAdapter(adapter);
		final PhoneEditHolder phoneEditHolder = new PhoneEditHolder(edtPhone,
				imgBtnMinusPhone, spinner);
		edtPhone.setHint("Phone number");
		edtPhone.requestFocus();
		
		edtPhone.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				imgViewContactEdit.setImageBitmap(mBitmap);
			}
		});
		
		mLnrPhoneEdit.addView(v);
		mLstPhoneEditHolder.add(phoneEditHolder);

		imgBtnMinusPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mLnrPhoneEdit.removeView(v);
				mLstPhoneEditHolder.remove(phoneEditHolder);

				if (mLstPhoneEditHolder.size() > 0) {
					EditText edt = mLstPhoneEditHolder.get(
							mLstPhoneEditHolder.size() - 1).getEdtPhoneNumber();
					edt.requestFocus();
				}
			}
		});
	}

	public void addDynamicEmailEdit() {
		final View v = mInflater.inflate(
				R.layout.row_email_web_add_company_edit, mLnrEmailEdit, false);
		EditText edtInfo = (EditText) v.findViewById(R.id.edtInfoEDIT);
		ImageButton imgMinus = (ImageButton) v
				.findViewById(R.id.btnImgSubstractInfoEDIT);

		final MailEditHolder mailHolder = new MailEditHolder(edtInfo, imgMinus);
		edtInfo.setHint("Email");
		edtInfo.requestFocus();
		edtInfo.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				imgViewContactEdit.setImageBitmap(mBitmap);
			}
		});
		mLnrEmailEdit.addView(v);
		mLstMailEditHolder.add(mailHolder);

		imgMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mLnrEmailEdit.removeView(v);
				mLstMailEditHolder.remove(mailHolder);

				if (mLstMailEditHolder.size() > 0) {
					EditText edt = mLstMailEditHolder.get(
							mLstMailEditHolder.size() - 1).getEdtEmail();
					edt.requestFocus();
				}
			}
		});
	}

	public void addDynamicAddressEdit() {
		final View v = mInflater
				.inflate(R.layout.row_email_web_add_company_edit,
						mLnrAddressEdit, false);
		EditText edtInfo = (EditText) v.findViewById(R.id.edtInfoEDIT);
		ImageButton imgMinus = (ImageButton) v
				.findViewById(R.id.btnImgSubstractInfoEDIT);

		final AddressEditHolder addHolder = new AddressEditHolder(edtInfo,
				imgMinus);
		edtInfo.setHint("Address");
		edtInfo.requestFocus();
		edtInfo.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				imgViewContactEdit.setImageBitmap(mBitmap);
			}
		});
		mLnrAddressEdit.addView(v);
		mLstAddEditHolder.add(addHolder);

		imgMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mLnrAddressEdit.removeView(v);
				mLstAddEditHolder.remove(addHolder);

				if (mLstAddEditHolder.size() > 0) {
					EditText edt = mLstAddEditHolder.get(
							mLstAddEditHolder.size() - 1).getEdtAddress();
					edt.requestFocus();
				}
			}
		});
	}

	public void addDynamicCompanyEdit() {
		final View v = mInflater.inflate(
				R.layout.row_email_web_add_company_edit, mLnrEmailEdit, false);
		EditText edtInfo = (EditText) v.findViewById(R.id.edtInfoEDIT);
		ImageButton imgMinus = (ImageButton) v
				.findViewById(R.id.btnImgSubstractInfoEDIT);

		final CompanyEditHolder compHolder = new CompanyEditHolder(edtInfo,
				imgMinus);
		edtInfo.setHint("Company");
		edtInfo.requestFocus();
		edtInfo.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				imgViewContactEdit.setImageBitmap(mBitmap);
			}
		});
		isHaveCompany = true;
		imgBtnAddCompanyField.setVisibility(View.INVISIBLE);
		mLnrCompanyEdit.addView(v);
		mLstCompanyEditHolder.add(compHolder);

		imgMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mLnrCompanyEdit.removeView(v);
				mLstCompanyEditHolder.add(compHolder);
				isHaveCompany = false;
				imgBtnAddCompanyField.setVisibility(View.VISIBLE);
			}
		});
	}

	public void addDynamicWebsiteEdit() {
		final View view = mInflater
				.inflate(R.layout.row_email_web_add_company_edit,
						mLnrWebsiteEdit, false);
		EditText edtInfo = (EditText) view.findViewById(R.id.edtInfoEDIT);
		ImageButton imgMinus = (ImageButton) view
				.findViewById(R.id.btnImgSubstractInfoEDIT);

		final WebEditHolder webHolder = new WebEditHolder(edtInfo, imgMinus);
		edtInfo.setHint("Web");
		edtInfo.requestFocus();
		edtInfo.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				imgViewContactEdit.setImageBitmap(mBitmap);
			}
		});
		mLnrWebsiteEdit.addView(view);
		mLstWebEditHolder.add(webHolder);

		imgMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLnrWebsiteEdit.removeView(view);
				mLstWebEditHolder.remove(webHolder);

				if (mLstWebEditHolder.size() > 0) {
					EditText edt = mLstWebEditHolder.get(
							mLstWebEditHolder.size() - 1).getEdtWebsite();
					edt.requestFocus();
				}
			}
		});
	}

	public void addContact() { // thieu rect cua name va company
		DataItem dataToAdd = new DataItem();
		if (mDataFromWhere == Constant.VALUE_SEND_FROM_DETAILS) {
			if (mDataItemReceive != null) {
				dataToAdd.setId_card(mDataItemReceive.getId_card());
				dataToAdd.setImage(mDataItemReceive.getImage());
				dataToAdd.setContact_position(mDataItemReceive
						.getContact_position());
			}
		} else {
			dataToAdd.setImage(mImagePath);
		}
		String name = new String();
		if ((!mEdtNameEditOri.getText().toString().isEmpty())
				|| (mEdtNameEditOri.getText().toString() == null)) {
			name = mEdtNameEditOri.getText().toString();
		} else {
			Toast.makeText(this, "Need A Name to Save Contact!",
					Toast.LENGTH_SHORT).show();// thay bang dialog
			return;
		}

		dataToAdd.setNameCard(name);
		dataToAdd.setRectName(mDataItemReceive.getRectName());

		ArrayList<PhoneItem> lstPhone = createListPhoneToSave();
		if (lstPhone == null || lstPhone.size() <= 0) {
			Toast.makeText(this, "Need A Phone to Save Contact!",
					Toast.LENGTH_SHORT).show();// sau nay se thay bang dialog
			return;
		}

		dataToAdd.setListPhone(lstPhone);

		ArrayList<EmailItem> lstEmail = createListEmailToSave();
		if (lstEmail != null && lstEmail.size() > 0) {
			dataToAdd.setListEmail(lstEmail);
		}

		String company = createCompanyToSave();
		if (company != null) {
			// nho check cai dieu kien voi chuoi rong nay
			dataToAdd.setNameCompany(company);
			dataToAdd.setRectCompany(mDataItemReceive.getRectCompany());
		}

		ArrayList<AddressItem> lstAddress = createListAddressToSave();
		if (lstAddress != null && lstAddress.size() > 0) {
			dataToAdd.setListAddress(lstAddress);
		}

		ArrayList<WebItem> lstWeb = createListWebToSave();
		if (lstWeb != null && lstWeb.size() > 0) {
			dataToAdd.setListWeb(lstWeb);
		}

		// dataToAdd.setImage(mImagePath);
		AsyncSaveToDB asyncSave = new AsyncSaveToDB(this, mDataFromWhere);
		asyncSave.execute(dataToAdd);
	}

	private ArrayList<PhoneItem> createListPhoneToSave() {
		ArrayList<PhoneItem> lstPhone = new ArrayList<PhoneItem>();
		int iSz = mLstPhoneEditHolder.size();
		if (iSz <= 0) {
			return null;
		}
		for (int i = 0; i < iSz; i++) {
			PhoneEditHolder phoneHolder = mLstPhoneEditHolder.get(i);
			PhoneItem phone = new PhoneItem();

			if (!phoneHolder.getEdtPhoneNumber().getText().toString().isEmpty()) {
				phone.setPhoneName(phoneHolder.getEdtPhoneNumber().getText()
						.toString());
			}

			if (phoneHolder.getRectItem() != null) {
				phone.setRectItem(phoneHolder.getRectItem());
			}
			Spinner spinner = phoneHolder.getSpiner();
			phone.setType(spinner.getSelectedItemPosition());

			lstPhone.add(phone);
		}
		return lstPhone;
	}

	private ArrayList<EmailItem> createListEmailToSave() {
		ArrayList<EmailItem> lstEmail = new ArrayList<EmailItem>();
		int iSz = mLstMailEditHolder.size();
		for (int i = 0; i < iSz; i++) {
			MailEditHolder mailHolder = mLstMailEditHolder.get(i);
			EmailItem email = new EmailItem();

			if (!mailHolder.getEdtEmail().getText().toString().isEmpty()) {
				email.setEmailName(mailHolder.getEdtEmail().getText()
						.toString());
			}

			if (mailHolder.getRectItem() != null) {
				email.setRectItem(mailHolder.getRectItem());
			}
			lstEmail.add(email);
		}

		return lstEmail;
	}

	private ArrayList<AddressItem> createListAddressToSave() {
		ArrayList<AddressItem> lstAddress = new ArrayList<AddressItem>();
		int iSz = mLstAddEditHolder.size();
		for (int i = 0; i < iSz; i++) {
			AddressEditHolder addHolder = mLstAddEditHolder.get(i);
			AddressItem address = new AddressItem();

			if (!addHolder.getEdtAddress().getText().toString().isEmpty()) {
				address.setAddressName(addHolder.getEdtAddress().getText()
						.toString());
			}

			if (addHolder.getRectItem() != null) {
				address.setRectItem(addHolder.getRectItem());
			}
			lstAddress.add(address);
		}
		return lstAddress;
	}

	private ArrayList<WebItem> createListWebToSave() {
		ArrayList<WebItem> lstWeb = new ArrayList<WebItem>();
		int iSz = mLstWebEditHolder.size();
		for (int i = 0; i < iSz; i++) {
			WebEditHolder webHolder = mLstWebEditHolder.get(i);
			WebItem web = new WebItem();

			if (!webHolder.getEdtWebsite().getText().toString().isEmpty()) {
				web.setWebName(webHolder.getEdtWebsite().getText().toString());
			}

			if (webHolder.getRectItem() != null) {
				web.setRectItem(webHolder.getRectItem());
			}
			lstWeb.add(web);
		}
		return lstWeb;
	}

	private String createCompanyToSave() {
		String company = new String();
		int iSz = mLstCompanyEditHolder.size();
		if (iSz <= 0) {
			return null;
		}

		CompanyEditHolder compHolder = mLstCompanyEditHolder.get(0);
		if (!compHolder.getEdtCompanyNameEdit().getText().toString().isEmpty()) {
			company = compHolder.getEdtCompanyNameEdit().getText().toString();
		}

		return company;
	}

	public void SaveEditContact() {

	}
}
