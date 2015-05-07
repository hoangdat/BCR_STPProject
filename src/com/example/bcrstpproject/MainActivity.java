package com.example.bcrstpproject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.stpproject.imgprocessing.CopyFileAsyncTask;
import com.stpproject.logutil.TestUtilities;
import com.stpproject.model.DataItem;
import com.stpproject.viewutil.AsyncGetImageFromGallery;
import com.stpproject.viewutil.Constant;
import com.stpproject.viewutil.ContactAdapter;
import com.stpproject.viewutil.DataBaseHandler;
import com.stpproject.viewutil.FeaturesCard;
import com.stpproject.viewutil.IndexSTPListView;
import com.stpproject.viewutil.ManagerSystemGlobal;
import com.stpproject.viewutil.SortDataItem;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnItemClickListener,
		OnClickListener, OnQueryTextListener {

	public static final String TAG = "MainActivity";

	public LinearLayout mLinearActionMain;
	private SearchView mSearchView;
	private IndexSTPListView mListView;
	private ImageButton mImgBtnGallery, mImgBtnCamera;
	private ContactAdapter mAdapter;

	private RetrieveImageReceiver mReceiverImage;

	public ArrayList<DataItem> mDataList = new ArrayList<DataItem>();
	public HashMap<Character, ArrayList<Integer>> alphaList = new HashMap<Character, ArrayList<Integer>>();
	public HashMap<Character, Integer> mMapIndexPos = new HashMap<Character, Integer>();
	public ArrayList<Character> indexSorted = new ArrayList<Character>();

	private String NOW_SEARCH_TEXT = null;
	private boolean mSearchType = SEARCH_BY_NAME;
	private final static boolean SEARCH_BY_NAME = false;
	private final static boolean SEARCH_BY_COMP = true;
	private Locale mVietLocale = null;

	private DataBaseHandler mDB;
	private FeaturesCard featuresCard;
	private Context mcontext;
	private Animation click;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		click = AnimationUtils.loadAnimation(this, R.anim.click);
		
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#5474BD"));
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(colorDrawable);
	 
		
		mVietLocale = new Locale("vi", "VN");
		mReceiverImage = new RetrieveImageReceiver();
		createDataBase();

		initUI();
		initData();

		SharedPreferences pref = getSharedPreferences(
				Constant.PREFERENCE_FILE_NAME, MODE_PRIVATE);
		boolean b = pref.getBoolean(Constant.KEY_IS_DATA_COPIED, false);
		if (!b) {
			copyRawFileAsync();
		}

		prepareTest();
	}

	public void createDataBase() {
		mcontext = this;
		mDB = new DataBaseHandler(this);
		featuresCard = new FeaturesCard(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.searchview_in_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setOnQueryTextListener(this);
		return true;
	}

	public void prepareTest() {

		File folderLog = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File fLog = new File(folderLog, Constant.LOG_NAME);
		ManagerSystemGlobal.gloPathFileLog = fLog.getAbsolutePath();
		if (fLog.exists()) {

			if (fLog.delete()) {
				Log.d(TAG, "Da delete thanh cong file");
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(getApplicationContext(), "setting",
					Toast.LENGTH_LONG).show();
			break;
		case R.id.action_seachbyname:
			mSearchType = SEARCH_BY_NAME;
			alphaList = SortDataItem.mapIndexAndSize(mDataList,
					SortDataItem.NAME_VIEW_MODE);
			mAdapter.setDataListLocal(mDataList);
			indexSorted = SortDataItem.sortIndexAscending(alphaList);
			mMapIndexPos = mapIndexToPosition();
			mAdapter.notifyDataSetChanged();

			break;
		case R.id.action_seachbycompany:
			mSearchType = SEARCH_BY_COMP;
			alphaList = SortDataItem.mapIndexAndSize(mDataList,
					SortDataItem.COMPANY_VIEW_MODE);
			mAdapter.setDataListLocal(mDataList);
			indexSorted = SortDataItem.sortIndexAscending(alphaList);
			mMapIndexPos = mapIndexToPosition();
			mAdapter.notifyDataSetChanged();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	public void initUI() {
		mLinearActionMain = (LinearLayout) findViewById(R.id.actionMain);

		mListView = (IndexSTPListView) findViewById(R.id.lstViewContact);
		mImgBtnGallery = (ImageButton) findViewById(R.id.imgbtnGetGalleryImg);
		mImgBtnCamera = (ImageButton) findViewById(R.id.imgbtnCamera);

		mListView.setOnItemClickListener(this);
		mImgBtnGallery.setOnClickListener(this);
		mImgBtnCamera.setOnClickListener(this);

		initListViewFeature();
	}

	public void initData() {
		mListView.setFastScrollEnabled(true);
		mAdapter = new ContactAdapter(this, R.layout.row_contact_item);
		mListView.setAdapter(mAdapter);
	}

	public void initListViewFeature() {
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode arg0) {
				// mLinearActionMain.setVisibility(View.VISIBLE);
				// mLinearActionMulti.setVisibility(View.GONE);
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.main_listview, menu);
				mAdapter.createSelectedItemsPosition();
				// mLinearActionMain.setVisibility(View.GONE);
				// mLinearActionMulti.setVisibility(View.VISIBLE);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode,
					MenuItem menuItem) {
				int iID = menuItem.getItemId();
				switch (iID) {
				case R.id.delete_actionmode:
					final ArrayList<DataItem> lstSelected = mAdapter
							.getSelectedItems();
					makeDialogDelete(lstSelected);
					mode.finish();
					break;

				case R.id.checkall_actionmode:
					mAdapter.createSelectedItemsPosition();
					int iSzLstview = mAdapter.getCount();
					for (int i = 0; i < iSzLstview; i++) {
						if (ContactAdapter.VIEW_TYPE_DATA == mAdapter
								.getItemViewType(i)) {
							mListView.setItemChecked(i, true);
							mAdapter.selectView(i, true);
						}
					}
					int checkedCount = mAdapter.getCountSelectedItems();
					mode.setTitle(checkedCount + " Selected");
					break;
				case R.id.sms_actionmode:
					featuresCard.showDialogSms(mAdapter.getSelectedItems());
					mode.finish();
					break;
				case R.id.email_actionmode:
					featuresCard.showDialogEmail(mAdapter.getSelectedItems());
					mode.finish();
					break;

				default:
					break;
				}
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				mAdapter.toggleSelection(position);
				int checkedCount = mAdapter.getCountSelectedItems();
				mode.setTitle(checkedCount + " Selected");
			}
		});
	}

	private void makeDialogDelete(final ArrayList<DataItem> lstSelected) {
		AlertDialog.Builder builderDelete = new AlertDialog.Builder(mcontext);
		builderDelete
				.setMessage("Deleting ? ")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						final ProgressDialog dialogDelete = new ProgressDialog(
								mcontext);
						new AsyncTask<String, Void, String>() {
							protected void onPreExecute() {
								dialogDelete.setMessage("Deleting ...");
								dialogDelete.setCanceledOnTouchOutside(false);
								dialogDelete.setCancelable(false);
								dialogDelete.show();
								super.onPreExecute();
							};

							protected String doInBackground(String... params) {
								for (int index = lstSelected.size() - 1; index >= 0; index--) {
									mDB.deleteCard(lstSelected.get(index));
								}
								mDataList = mDB.getAllDataCard();
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
								Toast.makeText(mcontext, result,
										Toast.LENGTH_LONG).show();
								alphaList = SortDataItem.mapIndexAndSize(
										mDataList, SortDataItem.NAME_VIEW_MODE);
								indexSorted = SortDataItem
										.sortIndexAscending(alphaList);
								mMapIndexPos = mapIndexToPosition();
								mAdapter.setDataListLocal(mDataList);
								mAdapter.notifyDataSetChanged();
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
	public void onClick(View v) {
		int iID = v.getId();
		switch (iID) {
		case R.id.imgbtnGetGalleryImg:
			
			mImgBtnGallery.startAnimation(click);
			getImageGalleryToProcess();
			break;

		case R.id.imgbtnCamera:
			mImgBtnCamera.startAnimation(click);
			Intent intent = new Intent(this, AndroidCamera.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DataItem dataItem = (DataItem) mAdapter.getItem(position);
		if (dataItem != null) {
			Intent intent = new Intent(this, DetailsContactActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(Constant.KEY_ITEM_ID_SEND, dataItem.getId_card());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDataList = mDB.getAllDataCard();
		alphaList = SortDataItem.mapIndexAndSize(mDataList,
				SortDataItem.NAME_VIEW_MODE);
		indexSorted = SortDataItem.sortIndexAscending(alphaList);
		mMapIndexPos = mapIndexToPosition();
		mAdapter.setDataListLocal(mDataList);
		mAdapter.notifyDataSetChanged();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.ACTION_RETRIEVE_IMAGE_RECEIVER);
		registerReceiver(mReceiverImage, intentFilter);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		String text = newText.toLowerCase(mVietLocale);
		NOW_SEARCH_TEXT = text;
		filter(NOW_SEARCH_TEXT);
		return true;
	}

	private void filter(String textFilter) {
		ArrayList<DataItem> lstFilter = new ArrayList<DataItem>();
		if (mSearchType == SEARCH_BY_NAME) {

			if (textFilter == null || textFilter.isEmpty()) {
				alphaList = SortDataItem.mapIndexAndSize(mDataList,
						SortDataItem.NAME_VIEW_MODE);
				mAdapter.setDataListLocal(mDataList);
				indexSorted = SortDataItem.sortIndexAscending(alphaList);
				mMapIndexPos = mapIndexToPosition();
				mAdapter.notifyDataSetChanged();
				return;
			}

			int iSz = mDataList.size();
			for (int i = 0; i < iSz; i++) {
				DataItem data = mDataList.get(i);
				if (data.getNameCard().toLowerCase(mVietLocale)
						.contains(textFilter)) {
					lstFilter.add(data);
				}
			}

			alphaList = SortDataItem.mapIndexAndSize(lstFilter,
					SortDataItem.NAME_VIEW_MODE);
			mAdapter.setDataListLocal(lstFilter);
			indexSorted = SortDataItem.sortIndexAscending(alphaList);
			mMapIndexPos = mapIndexToPosition();
			mAdapter.notifyDataSetChanged();

			return;

		} else {

			if (textFilter == null || textFilter.isEmpty()) {
				alphaList = SortDataItem.mapIndexAndSize(mDataList,
						SortDataItem.COMPANY_VIEW_MODE);
				mAdapter.setDataListLocal(mDataList);
				indexSorted = SortDataItem.sortIndexAscending(alphaList);
				mMapIndexPos = mapIndexToPosition();
				mAdapter.notifyDataSetChanged();
				return;
			}

			int iSz = mDataList.size();
			for (int i = 0; i < iSz; i++) {
				DataItem data = mDataList.get(i);
				if (data == null || data.getNameCompany() == null) continue;
				if (data.getNameCompany().toLowerCase(mVietLocale)
						.contains(textFilter)) {
					lstFilter.add(data);
				}
			}

			alphaList = SortDataItem.mapIndexAndSize(lstFilter,
					SortDataItem.COMPANY_VIEW_MODE);
			mAdapter.setDataListLocal(lstFilter);
			indexSorted = SortDataItem.sortIndexAscending(alphaList);
			mMapIndexPos = mapIndexToPosition();
			mAdapter.notifyDataSetChanged();

			return;

		}
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		return false;
	}

	public HashMap<Character, Integer> mapIndexToPosition() {
		HashMap<Character, Integer> hashPosition = new HashMap<Character, Integer>();
		int iPreSz = 0;
		int iSz = indexSorted.size();
		for (int i = 0; i < iSz; i++) {
			Character c = indexSorted.get(i);
			int iSzPerIndex = alphaList.get(c).size();

			hashPosition.put(c, iPreSz);

			iPreSz = iPreSz + iSzPerIndex + 1;
		}

		return hashPosition;
	}

	private void copyRawFileAsync() {
		InputStream in = getResources().openRawResource(R.raw.vie);
		CopyFileAsyncTask cpFile = new CopyFileAsyncTask(this);
		cpFile.execute(in);
	}

	public void getImageGalleryToProcess() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, Constant.REQUEST_IMAGE_GALLERY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.REQUEST_IMAGE_GALLERY
				&& resultCode == RESULT_OK) {
			retrieveImageFromGallery(data);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiverImage);
	}

	private void retrieveImageFromGallery(Intent intent) {
		Uri uriImage = intent.getData();
		String[] projection = { MediaStore.Images.ImageColumns.DATA };
		Cursor cursor = getContentResolver().query(uriImage, projection, null,
				null, null);
		if (!cursor.moveToFirst()) {
			TestUtilities.writeLog(ManagerSystemGlobal.gloPathFileLog,
					"Cursor null-retreiveImageGallery");
			return;
		}

		String imgString = cursor.getString(cursor
				.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
		if (imgString.contains("http") || imgString == null) {
			Toast.makeText(getApplicationContext(),
					"Please choose another image to scan!", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		AsyncGetImageFromGallery asyncGetImage = new AsyncGetImageFromGallery(
				this);
		asyncGetImage.execute(imgString);

	}

	public class RetrieveImageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String imgPath = bundle.getString(
					Constant.KEY_IMAGE_PATH_FOR_PROCESSING,
					Constant.VALUE_NOT_EXISTED);
			if (imgPath.equals(Constant.VALUE_NOT_EXISTED)) {
				Toast.makeText(getApplicationContext(),
						"Have some trouble in loading image!",
						Toast.LENGTH_SHORT).show();
			} else {
				Intent intent_ = new Intent(getApplicationContext(),
						ProcessingActivity.class);
				Bundle bundle_ = new Bundle();
				bundle_.putCharSequence(Constant.KEY_IMAGE_PATH_FOR_PROCESSING,
						imgPath);
				intent_.putExtras(bundle_);
				startActivity(intent_);
			}
		}

	}
}
