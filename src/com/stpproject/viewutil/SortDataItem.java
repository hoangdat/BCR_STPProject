package com.stpproject.viewutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import com.stpproject.model.DataItem;

public class SortDataItem {
	public static final int NAME_VIEW_MODE = 1;
	public static final int COMPANY_VIEW_MODE = 2;
	
	
//	public static ArrayList<DataItem> sortNameAscending(
//			ArrayList<DataItem> myArray) {
//		Comparator<DataItem> comperator = new Comparator<DataItem>() {
//
//			@Override
//			public int compare(DataItem item1, DataItem item2) {
//				return item1.getNameCard().compareToIgnoreCase(
//						item2.getNameCard());
//			}
//		};
//		Collections.sort(myArray, comperator);
//		return myArray;
//	}
	
	public static ArrayList<Character> sortIndexAscending(HashMap<Character, ArrayList<Integer>> hashPerIndex) {
		Set<Character> keySet = hashPerIndex.keySet();
		ArrayList<Character> allChars = new ArrayList<Character>();
		for (Character c : keySet) {
			Character cUp = Character.toUpperCase(c);
			allChars.add(cUp);
		}
		
		Comparator<Character> comparator = new Comparator<Character>() {
			
			@Override
			public int compare(Character lhs, Character rhs) {
				// TODO Auto-generated method stub
				return lhs.compareTo(rhs);
			}
		};
		
		Collections.sort(allChars, comparator);
		return allChars;
	}
	
	public static HashMap<Character, ArrayList<Integer>> mapIndexAndSize(ArrayList<DataItem> sortedData, int iMode) {
		Locale locale = new Locale("vi", "VN");
		HashMap<Character, ArrayList<Integer>> hashPerIndex = new HashMap<Character, ArrayList<Integer>>();
		int iSzData = sortedData.size();
		
		if (iMode == NAME_VIEW_MODE) {
			for (int i = 0; i < iSzData; i++) {
				DataItem data = sortedData.get(i);
				if (data == null || data.getNameCard() == null || data.getNameCard().isEmpty()) continue;
				Character charStartName = data.getNameCard().toUpperCase(locale).charAt(0);
				if (charStartName != null) {
					if (Character.isLetter(charStartName)) {
						if (charStartName == 'Ă' || charStartName == 'Â') {
							charStartName = 'A';
						} else if (charStartName == 'Đ') {
							charStartName = 'D';
						} else if (charStartName == 'Ô' || charStartName == 'Ơ') {
							charStartName = 'O';
						}
						ArrayList<Integer> arrTmp = hashPerIndex.get(charStartName);

						if (arrTmp == null) {
							arrTmp = new ArrayList<Integer>();
							arrTmp.add(i);
							hashPerIndex.put(charStartName, arrTmp);
						} else {
							arrTmp.add(i);
							hashPerIndex.put(charStartName, arrTmp);
						}
					} else {
						ArrayList<Integer> arrTmp = hashPerIndex.get('#');
						if (arrTmp == null) {
							arrTmp = new ArrayList<Integer>();
							arrTmp.add(i);
							hashPerIndex.put('#', arrTmp);
						} else {
							arrTmp.add(i);
							hashPerIndex.put('#', arrTmp);
						}
					}
				} 
			}
		} else if (iMode == COMPANY_VIEW_MODE) {
			for (int i = 0; i < iSzData; i++) {
				DataItem data = sortedData.get(i);
				if (data == null) continue;
				if (data.getNameCompany() == null || data.getNameCompany().isEmpty()) {
					ArrayList<Integer> arrTmp = hashPerIndex.get('#');
					if (arrTmp == null) {
						arrTmp = new ArrayList<Integer>();
						arrTmp.add(i);
						hashPerIndex.put('#', arrTmp);
					} else {
						arrTmp.add(i);
						hashPerIndex.put('#', arrTmp);
					}
					continue;
				}
				Character charStartCompany = data.getNameCompany().toUpperCase(locale).charAt(0);// chu y khi cai truong nay null
				if (charStartCompany != null) {
					if (Character.isLetter(charStartCompany)) {
						if (charStartCompany == 'Ă' || charStartCompany == 'Â') {
							charStartCompany = 'A';
						} else if (charStartCompany == 'Đ') {
							charStartCompany = 'D';
						} else if (charStartCompany == 'Ô' || charStartCompany == 'Ơ') {
							charStartCompany = 'O';
						}
						ArrayList<Integer> arrTmp = hashPerIndex.get(charStartCompany);

						if (arrTmp == null) {
							arrTmp = new ArrayList<Integer>();
							arrTmp.add(i);
							hashPerIndex.put(charStartCompany, arrTmp);
						} else {
							arrTmp.add(i);
							hashPerIndex.put(charStartCompany, arrTmp);
						}
					} else {
						ArrayList<Integer> arrTmp = hashPerIndex.get('#');
						if (arrTmp == null) {
							arrTmp = new ArrayList<Integer>();
							arrTmp.add(i);
							hashPerIndex.put('#', arrTmp);
						} else {
							arrTmp.add(i);
							hashPerIndex.put('#', arrTmp);
						}
					}
				} else {
					ArrayList<Integer> arrTmp = hashPerIndex.get('#');
					if (arrTmp == null) {
						arrTmp = new ArrayList<Integer>();
						arrTmp.add(i);
						hashPerIndex.put('#', arrTmp);
					} else {
						arrTmp.add(i);
						hashPerIndex.put('#', arrTmp);
					}
				}
			}
		} 

		return hashPerIndex;
	}

}
