package com.stpproject.viewutil;

import com.example.bcrstpproject.EditContactsActivity;
import com.example.bcrstpproject.MainActivity;
import com.stpproject.model.DataItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class AsyncSaveToDB extends AsyncTask<DataItem, Void, Integer>{

	EditContactsActivity mContext;
	private ProgressDialog mProgressDialog;
	private int mDataWhere;
	DataBaseHandler mDB;
	
	
	public AsyncSaveToDB(Context context, int dataWhere) {
		mContext = (EditContactsActivity)context;
		this.mDataWhere = dataWhere;
		mDB = new DataBaseHandler(context);
	}
	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage("Saving ...");
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected Integer doInBackground(DataItem... params) {
		DataItem data = (DataItem) params[0];
		if (data == null) return -1;
		if (mDataWhere == Constant.VALUE_SEND_FROM_PROCESS) {
			int i = mDB.addDataCard(data);
			return i; 
		} else if (mDataWhere == Constant.VALUE_SEND_FROM_DETAILS) {
			int i = mDB.editCard(data);
			return i;
		}
		return -1;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			try {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (result == -1) {
			Toast.makeText(mContext, "Error occur while saving.\nPlease save again!", Toast.LENGTH_SHORT).show();
		} else {
			if (mDataWhere == Constant.VALUE_SEND_FROM_DETAILS) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt(Constant.KEY_ITEM_ID_SEND, result);
				intent.putExtras(bundle);
				mContext.setResult(Activity.RESULT_OK, intent);
				mContext.finish();
			} else if (mDataWhere == Constant.VALUE_SEND_FROM_PROCESS) {
				mContext.finish();
			}
		}
		super.onPostExecute(result);
	}

}
