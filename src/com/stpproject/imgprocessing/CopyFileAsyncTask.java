package com.stpproject.imgprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.bcrstpproject.MainActivity;
import com.example.bcrstpproject.R;
import com.stpproject.viewutil.Constant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

public class CopyFileAsyncTask extends AsyncTask<InputStream, Integer, File> {

	static MainActivity mContext;
	
	
	public CopyFileAsyncTask(Context context) {
		mContext = (MainActivity)context;
	}
	
	@Override
	protected File doInBackground(InputStream... params) {
		OutputStream out = null;
		InputStream in = params[0];
		File folder = null;
		folder = createTessDataFolder();
		if (folder == null) {
			return null;
		}
		
		File fileCP = new File(folder, "/vie.traineddata");
		try {
			out = new FileOutputStream(fileCP);
			byte[] buffer = new byte[1024];
			int countRead = 0;
			while ((countRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, countRead);
			}
		} catch (FileNotFoundException fnfE) {
			fnfE.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File tessdataFolder = fileCP.getParentFile();
		return tessdataFolder;
	}
	
	
	public static File createTessDataFolder() {
		String folderParentName = "stpdata";
		String folderTessDataName = "tessdata";
		File storageDir = mContext.getFilesDir();
		Log.i("createTessDataFolder", storageDir.getAbsolutePath());
		//File folderTess = new File(mContext.getFilesDir() + "/" + folderName);
		File folderTessPar = new File(mContext.getFilesDir(), folderParentName);
		if (!folderTessPar.exists()) {
			folderTessPar.mkdirs();
		}
		File folderTess = new File(folderTessPar, folderTessDataName);
		if (!folderTess.exists()) {
			folderTess.mkdirs();
		}
		return folderTess;
	}
	
	@Override
	protected void onPostExecute(File result) {
		super.onPostExecute(result);
		
		SharedPreferences sharePref = mContext.getSharedPreferences(Constant.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		Editor edit = sharePref.edit();
		edit.putBoolean(Constant.KEY_IS_DATA_COPIED, true);
		edit.putString(Constant.KEY_PATH_TESS_DATA, result.getParent());
		edit.putString(Constant.KEY_LANG_TESS, "vie");
		edit.commit();
	}
	
	public static void createTessDataNotAsync() {
		InputStream in = mContext.getResources().openRawResource(R.raw.vie);
		OutputStream out = null;
		File folder = null;
		folder = createTessDataFolder();
		if (folder == null) {
			throw new NullPointerException("Not able to create tessData");
		}
		
		File fileCP = new File(folder, "/vie.traineddata");
		try {
			out = new FileOutputStream(fileCP);
			byte[] buffer = new byte[1024];
			int countRead = 0;
			while ((countRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, countRead);
			}
		} catch (FileNotFoundException fnfE) {
			fnfE.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File tessdataFolder = fileCP.getParentFile();
		SharedPreferences sharePref = mContext.getSharedPreferences(Constant.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
		Editor edit = sharePref.edit();
		edit.putBoolean(Constant.KEY_IS_DATA_COPIED, true);
		edit.putString(Constant.KEY_PATH_TESS_DATA, tessdataFolder.getParent());
		edit.putString(Constant.KEY_LANG_TESS, "vie");
		edit.commit();
	}

}
