package com.stpproject.viewutil;


import java.util.ArrayList;
import java.util.HashMap;

import com.example.bcrstpproject.MainActivity;
import com.example.bcrstpproject.R;
import com.stpproject.model.DataItem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter implements SectionIndexer {

	public static final int VIEW_TYPE_DATA = 1;
	public static final int VIEW_TYPE_INDEX = 0;
	
	private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private SparseBooleanArray mSelectedItem;
	
	ArrayList<DataItem> mDataListLocal = new ArrayList<DataItem>();

	DataItem mDataItemForGetView;
	Character mCharForGetView;

	MainActivity mContext;
	int mResource;
	
	public void setDataListLocal(ArrayList<DataItem> lstData) {
		this.mDataListLocal = lstData;
	}

	public ContactAdapter(MainActivity context, int iResource) {
		this.mContext = context;
		this.mResource = iResource;
		mDataItemForGetView = new DataItem();
		mSelectedItem = new SparseBooleanArray();
	}
	
	public SparseBooleanArray getSelectedItemsPosition() {
		return mSelectedItem;
	}
	
	public void createSelectedItemsPosition() {
		mSelectedItem = new SparseBooleanArray();
	}
	
	public void toggleSelection(int position) {
		selectView(position, !mSelectedItem.get(position));
	}
	
	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItem.put(position, value);
		else
			mSelectedItem.delete(position);
		notifyDataSetChanged();
	}
	
	public int getCountSelectedItems() {
		return mSelectedItem.size();
	}
	
	public ArrayList<DataItem> getSelectedItems() {
		ArrayList<DataItem> lstSelected = new ArrayList<DataItem>();
		
		int iSz = mSelectedItem.size();
		for (int i = 0; i < iSz; i++) {
			boolean b = mSelectedItem.valueAt(i);
			if (b) {
				int key = mSelectedItem.keyAt(i);
				DataItem data = (DataItem)getItem(key);
				lstSelected.add(data);
			}			
		}
		
		return lstSelected;
	}
	
	
	

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		//int iSzData = mContext.mDataList.size();
		int iSzData = mDataListLocal.size();
		int iSzIndex = mContext.alphaList.size();
		return iSzData + iSzIndex;
	}

	@Override
	public Object getItem(int position) {
		
		if ((position > getCount()) || (position < 0)) {
			return null;
		}
		
		int iGlo = 0;
		int iPreSz = 0;
		int iIndex = 0;
		int iSz = mContext.indexSorted.size();
		for (int i = 0; i < iSz; i++) {
			int iSizePer = mContext.alphaList.get(mContext.indexSorted.get(i))
					.size();
			iIndex = iPreSz;
			if (position == iIndex) {

				return null;
			}
			iPreSz = iPreSz + iSizePer + 1;
			if ((position - iPreSz) < 0) {
				iGlo = i;
				break;
			}
		}
			
		int iData = position - iIndex - 1;
//		mDataItemForGetView = mContext.mDataList.get(mContext.alphaList.get(
//				mContext.indexSorted.get(iGlo)).get(iData));
		DataItem dataItem = mDataListLocal.get(mContext.alphaList.get(mContext.indexSorted.get(iGlo)).get(iData));
		return dataItem;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemViewType(int position) {

		mDataItemForGetView = new DataItem();
		mCharForGetView = null;

		int iGlo = 0;
		int iPreSz = 0;
		int iIndex = 0;
		int iSz = mContext.indexSorted.size();
		for (int i = 0; i < iSz; i++) {
			int iSizePer = mContext.alphaList.get(mContext.indexSorted.get(i))
					.size();
			iIndex = iPreSz;
			if (position == iIndex) {

				// return for getView
				mCharForGetView = mContext.indexSorted.get(i);
				return VIEW_TYPE_INDEX;
			}
			iPreSz = iPreSz + iSizePer + 1;
			if ((position - iPreSz) < 0) {
				iGlo = i;
				break;
			}
		}

		// return for getView()
		int iData = position - iIndex - 1;
//		mDataItemForGetView = mContext.mDataList.get(mContext.alphaList.get(
//				mContext.indexSorted.get(iGlo)).get(iData));
		mDataItemForGetView = mDataListLocal.get(mContext.alphaList.get(mContext.indexSorted.get(iGlo)).get(iData));
		return VIEW_TYPE_DATA;
	}
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		int iTypeView = getItemViewType(position);
		if (iTypeView == VIEW_TYPE_DATA) {
			ViewDataHolder holder = new ViewDataHolder();

			if (v == null
					|| ((Integer) v.getTag(R.id.ID_STORE_TYPE)) == VIEW_TYPE_INDEX) {
				LayoutInflater inflater = mContext.getLayoutInflater();
				v = inflater.inflate(R.layout.row_contact_item, parent, false);
				holder.imgViewDataRow = (ImageView) v
						.findViewById(R.id.imgViewItemProfile);
				holder.txtNameRow = (TextView) v.findViewById(R.id.txtItemName);
				holder.txtCompanyRow = (TextView) v
						.findViewById(R.id.txtItemCompany);
				v.setTag(R.id.ID_STORE_VIEW, holder);
				v.setTag(R.id.ID_STORE_TYPE, VIEW_TYPE_DATA);
			} else {
				holder = (ViewDataHolder) v.getTag(R.id.ID_STORE_VIEW);
			}

			final DataItem dataItem = mDataItemForGetView;
			holder.position = position;
			holder.txtNameRow.setText(dataItem.getNameCard());
			holder.txtCompanyRow.setText(dataItem.getNameCompany());
			
			if (dataItem.getImage() != null) {
				if (dataItem.getBitmapIcon() == null) {
					holder.datapath = dataItem.getImage();
					new AsyncTask<ViewDataHolder, Void, Bitmap>() {
						private ViewDataHolder v;
						
						@Override
						protected Bitmap doInBackground(
								ViewDataHolder... params) {
							v = params[0];
							Bitmap bitmap = BitmapFactory.decodeFile(v.datapath);
							if (bitmap != null) {
								bitmap = Bitmap.createScaledBitmap(bitmap, 128, 75, true);
							}
							return bitmap;
						}
						
						@Override
						protected void onPostExecute(Bitmap result) {
							super.onPostExecute(result);
							if (v.position == position) {
								v.imgViewDataRow.setImageBitmap(result);
								dataItem.setBitmapIcon(result);
							}
						}
					}.execute(holder);
				} else {
					holder.imgViewDataRow.setImageBitmap(dataItem.getBitmapIcon());
				}
			} else {
				holder.imgViewDataRow.setImageResource(R.drawable.oto);
			}
			
		} else {
			ViewIndexHolder holder = new ViewIndexHolder();
			v = convertView;
			if (v == null
					|| ((Integer) v.getTag(R.id.ID_STORE_TYPE)) == VIEW_TYPE_DATA) {
				LayoutInflater inflater = mContext.getLayoutInflater();
				v = inflater.inflate(R.layout.row_index_item_list, parent,
						false);
				holder.txtIndexRow = (TextView) v
						.findViewById(R.id.txtIndexInList);
				v.setTag(R.id.ID_STORE_VIEW, holder);
				v.setTag(R.id.ID_STORE_TYPE, VIEW_TYPE_INDEX);
				v.setOnClickListener(null);
			} else {
				holder = (ViewIndexHolder) v.getTag(R.id.ID_STORE_VIEW);
			}

			Character c = mCharForGetView;
			holder.txtIndexRow.setText(c + " ");
		}

		return v;
	}
	
	public HashMap<Character, Integer> mapIndexToPosition() {
		HashMap<Character, Integer> hashPosition = new HashMap<Character, Integer>();
		int iPreSz = 0;
		int iSz = mContext.indexSorted.size();
		for (int i = 0; i < iSz; i++) {
			Character c = mContext.indexSorted.get(i);
			int iSzPerIndex = mContext.alphaList.get(c).size();
			
			hashPosition.put(c, iPreSz);
			
			iPreSz = iPreSz + iSzPerIndex + 1;
		}
		
		return hashPosition;
	}

	class ViewDataHolder {
		ImageView imgViewDataRow;
		TextView txtNameRow;
		TextView txtCompanyRow;
		int position;
		String datapath;
	}

	class ViewIndexHolder {
		TextView txtIndexRow;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		Character ch = mSections.charAt(sectionIndex);
		if (ch == null) {
			return -1;
		}
		
		Object pos = mContext.mMapIndexPos.get(ch);
		if (pos == null) return -1;
		else 
			return (Integer)pos;
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[mSections.length()];
		for (int i = 0; i < mSections.length(); i++)
			sections[i] = String.valueOf(mSections.charAt(i));
		return sections;
	}



}
