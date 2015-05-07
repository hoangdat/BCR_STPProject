package com.example.bcrstpproject;

//loi chua xoa view cu. viet them vao view cu => sai

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stpproject.model.DataItem;
import com.stpproject.viewutil.AddressViewFieldHolder;
import com.stpproject.viewutil.AsyncGetDataItem;
import com.stpproject.viewutil.Constant;
import com.stpproject.viewutil.DataBaseHandler;
import com.stpproject.viewutil.EmailViewFieldHolder;
import com.stpproject.viewutil.FeaturesCard;
import com.stpproject.viewutil.PhoneViewFieldHolder;
import com.stpproject.viewutil.WebViewFiledHolder;

public class DetailsContactActivity extends Activity implements OnClickListener {

	public ImageView mImgViewContact;
	public ImageButton mImgBtnPhone, mImgBtnMessage;

	public TextView mTxtContactNameOverlay, mTxtCompanyOverlay;
	public LinearLayout mLnrPhone, mLnrEmail, mLnrAddress, mLnrWebsite;

	public DataItem mDataItem;
	private ActionBar mActionBar;
	private int mIDItem;
	private FeaturesCard featuresCard;
	private DataBaseHandler dataBaseHandler;
	private Context context;

	public ArrayList<PhoneViewFieldHolder> lstPhone = new ArrayList<PhoneViewFieldHolder>();
	public ArrayList<EmailViewFieldHolder> lstEmail = new ArrayList<EmailViewFieldHolder>();
	public ArrayList<AddressViewFieldHolder> lstAddress = new ArrayList<AddressViewFieldHolder>();
	public ArrayList<WebViewFiledHolder> lstWeb = new ArrayList<WebViewFiledHolder>();
    
	private ImageButton imgBtnEdit,imgBtnBack;
	private Animation click;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_details_contact);
		click = AnimationUtils.loadAnimation(this, R.anim.click);
		initUI();
		customActionBar();
		featuresCard = new FeaturesCard(this);
		dataBaseHandler = new DataBaseHandler(this);
		context = this;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mIDItem = bundle.getInt(Constant.KEY_ITEM_ID_SEND, -1);

		AsyncGetDataItem async = new AsyncGetDataItem(
				DetailsContactActivity.this);
		async.execute(mIDItem);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_contact_detail:
			if (null == mDataItem) {
				break;
			}
			final ProgressDialog dialog = new ProgressDialog(context);
			new AsyncTask<String, Void, String>() {
				protected void onPreExecute() {
					dialog.setMessage("Saving ...");
					dialog.setCanceledOnTouchOutside(false);
					dialog.setCancelable(false);
					dialog.show();
					super.onPreExecute();
				};

				@Override
				protected String doInBackground(String... params) {
					int positionContact = featuresCard.addContact(mDataItem);
					mDataItem.setContact_position(positionContact);
					dataBaseHandler.updateContactPosition(mDataItem);
					return "Done";
				}

				protected void onPostExecute(String result) {
					if (dialog != null && dialog.isShowing()) {
						try {
							dialog.dismiss();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					Toast.makeText(context, result, Toast.LENGTH_LONG).show();
				};

			}.execute();
			break;
		case R.id.delete_detail:
			if (null == mDataItem) {
				break;
			}
			makeDialogDelete();
			break;
		case R.id.share_detail:
			if (null == mDataItem) {
				break;
			}
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, mDataItem.converseStr());
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void makeDialogDelete() {
		AlertDialog.Builder builderDelete = new AlertDialog.Builder(context);
		builderDelete
				.setMessage("Deleting ? ")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						final ProgressDialog dialogDelete = new ProgressDialog(
								context);
						new AsyncTask<String, Void, String>() {
							protected void onPreExecute() {
								dialogDelete.setMessage("Deleting ...");
								dialogDelete.setCanceledOnTouchOutside(false);
								dialogDelete.setCancelable(false);
								dialogDelete.show();
								super.onPreExecute();
							};

							@Override
							protected String doInBackground(String... params) {
								dataBaseHandler.deleteCard(mDataItem);
								return "Done";
							}

							protected void onPostExecute(String result) {
								if (dialogDelete != null
										&& dialogDelete.isShowing()) {
									try {
										dialogDelete.dismiss();
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
								Toast.makeText(context, result,
										Toast.LENGTH_LONG).show();
								finish();
							};

						}.execute();
					}
				})
				.setNegativeButton("CANCEL",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
		builderDelete.create().show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_detail, menu); // thay doi
		return true;
	}

	public void initUI() {
		mImgViewContact = (ImageView) findViewById(R.id.imgContact);
		mImgBtnPhone = (ImageButton) findViewById(R.id.btnImgCall);
		mImgBtnMessage = (ImageButton) findViewById(R.id.btnImgMessage);

		mTxtContactNameOverlay = (TextView) findViewById(R.id.txtContactName);
		mTxtCompanyOverlay = (TextView) findViewById(R.id.txtCompanyName);

		mLnrPhone = (LinearLayout) findViewById(R.id.lnrPhone);
		mLnrEmail = (LinearLayout) findViewById(R.id.lnrEmail);
		mLnrAddress = (LinearLayout) findViewById(R.id.lnrAddress);
		mLnrWebsite = (LinearLayout) findViewById(R.id.lnrWeb);
	}

	// public void coOperateDataUI() {
	// Intent intent = getIntent();
	//
	// //doan nay thuc hien async
	// }

	public void customActionBar() {
		mActionBar = getActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setBackgroundDrawable(null);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View customView = inflater.inflate(R.layout.custom_actionbar, null);
		imgBtnEdit = (ImageButton) customView
				.findViewById(R.id.btnImgEditActionBar);
		imgBtnBack = (ImageButton) customView
				.findViewById(R.id.btnImgBackActionBar);

		imgBtnEdit.setOnClickListener(this);
		imgBtnBack.setOnClickListener(this);

		mActionBar.setCustomView(customView);
		mActionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onClick(View v) {
		int iID = v.getId();
		switch (iID) {
		case R.id.btnImgEditActionBar:
			imgBtnEdit.startAnimation(click);
			Intent intent = new Intent(DetailsContactActivity.this,
					EditContactsActivity.class);
			intent.putExtra(Constant.KEY_SEND_FROM,
					Constant.VALUE_SEND_FROM_DETAILS);
			Bundle bundle = new Bundle();
			bundle.putInt(Constant.KEY_ITEM_ID_SEND, mIDItem);
			intent.putExtras(bundle);
			startActivityForResult(intent, Constant.REQUEST_EDIT_FROM_DETAILS);
			break;
		case R.id.btnImgBackActionBar:
			imgBtnBack.startAnimation(click);
			onBackPressed();
			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.REQUEST_EDIT_FROM_DETAILS
				&& resultCode == RESULT_OK) {
			// phai co them ham remove cai view cu di
			lstPhone = new ArrayList<PhoneViewFieldHolder>();
			lstEmail = new ArrayList<EmailViewFieldHolder>();
			lstAddress = new ArrayList<AddressViewFieldHolder>();
			lstWeb = new ArrayList<WebViewFiledHolder>();

			Bundle bundle = data.getExtras();
			mIDItem = bundle.getInt(Constant.KEY_ITEM_ID_SEND, -1);

			AsyncGetDataItem async = new AsyncGetDataItem(
					DetailsContactActivity.this);
			async.execute(mIDItem);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}
