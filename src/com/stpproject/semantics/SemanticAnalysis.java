package com.stpproject.semantics;

import java.util.ArrayList;

import org.opencv.core.Rect;

import com.stpproject.model.AddressItem;
import com.stpproject.model.DataItem;
import com.stpproject.model.EmailItem;
import com.stpproject.model.PhoneItem;
import com.stpproject.model.RectItem;
import com.stpproject.model.WebItem;

public class SemanticAnalysis {
	private ArrayList<LineItem> listLineItems;
	private String otherName;
	private String jobName;
	private DataItem dataItem;

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public SemanticAnalysis(ArrayList<LineItem> lisLineItems) {
		this.listLineItems = lisLineItems;
		this.dataItem = new DataItem();
	}

	private void getEmail() {
		for (int index = 0; index < listLineItems.size(); index++) {
			LineItem lineItem = listLineItems.get(index);
			if (lineItem.isProcess()) {
				continue;
			}
			ProcessString processString = new ProcessString(
					lineItem.getStringResult());
			ArrayList<String> resultList = processString.getEmailList();
			if (resultList.size() > 0) {
				lineItem.setProcess(true);
				Rect rect = lineItem.getRectRegion();
				RectItem rectItem = new RectItem();
				rectItem.setX(rect.x);
				rectItem.setY(rect.y);
				rectItem.setHeight(rect.height);
				rectItem.setWidth(rect.width);
				for (int index2 = 0; index2 < resultList.size(); index2++) {
					EmailItem emailItem = new EmailItem();
					emailItem.setEmailName(resultList.get(index2));
					emailItem.setRectItem(rectItem);
					dataItem.getListEmail().add(emailItem);
				}
			}
		}
	}

	private void getWebsite() {
		for (int index = 0; index < listLineItems.size(); index++) {
			LineItem lineItem = listLineItems.get(index);
			if (lineItem.isProcess()) {
				continue;
			}
			ProcessString processString = new ProcessString(
					lineItem.getStringResult());
			ArrayList<String> resultList = processString.getWebsiteList();
			if (resultList.size() > 0) {
				lineItem.setProcess(true);
				Rect rect = lineItem.getRectRegion();
				RectItem rectItem = new RectItem();
				rectItem.setX(rect.x);
				rectItem.setY(rect.y);
				rectItem.setHeight(rect.height);
				rectItem.setWidth(rect.width);
				for (int index2 = 0; index2 < resultList.size(); index2++) {
					WebItem webItem = new WebItem();
					webItem.setWebName(resultList.get(index2));
					webItem.setRectItem(rectItem);
					dataItem.getListWeb().add(webItem);
				}
			}
		}
	}

	private void getNumberPhone() {
		for (int index = 0; index < listLineItems.size(); index++) {
			LineItem lineItem = listLineItems.get(index);
			// Quet lai toan bo de tim sdt
			// if (lineItem.isProcess()) {
			// continue;
			// }
			ProcessString processString = new ProcessString(
					lineItem.getStringResult());
			ArrayList<String> resultList = processString.getNumberPhone();
			if (resultList.size() > 0) {
				lineItem.setProcess(true);
				Rect rect = lineItem.getRectRegion();
				RectItem rectItem = new RectItem();
				rectItem.setX(rect.x);
				rectItem.setY(rect.y);
				rectItem.setHeight(rect.height);
				rectItem.setWidth(rect.width);
				for (int index2 = 0; index2 < resultList.size(); index2++) {
					PhoneItem phoneItem = new PhoneItem();
					phoneItem.setPhoneName(resultList.get(index2));
					phoneItem.setRectItem(rectItem);
					phoneItem.setType(CheckNameTitle.checkTypePhone(resultList
							.get(index2)));
					dataItem.getListPhone().add(phoneItem);
				}
				if (processString.isFax()) {
					dataItem.getListPhone()
							.get(dataItem.getListPhone().size() - 1).setType(2);
				}
			}
		}
	}

	private void getAddress() {
		for (int index = 0; index < listLineItems.size(); index++) {
			LineItem lineItem = listLineItems.get(index);
			if (lineItem.isProcess()) {
				continue;
			}
			if (isHaveNumber(lineItem.getStringResult())) {
				ArrayList<Integer> listNumberChoose = new ArrayList<Integer>();
				listNumberChoose.add(0, index);
				// lineItem.setProcess(true);
				// Duyet tu duoi len
				LineItem startItem = lineItem;
				for (int index2 = index - 1; index2 > -1; index2--) {
					LineItem checkItem = listLineItems.get(index2);
					if (checkItem.isProcess()) {
						continue;
					}
					if (startItem.isSequential(checkItem)
							&& !CheckNameTitle.isNameCompany(checkItem
									.getStringResult())) {
						// checkItem.setProcess(true);
						startItem = checkItem;
						listNumberChoose.add(0, index2);

					}

				}
				// Duyet tu tren xuong
				startItem = lineItem;
				for (int index2 = index + 1; index2 < listLineItems.size(); index2++) {
					LineItem checkItem = listLineItems.get(index2);
					if (checkItem.isProcess()) {
						continue;
					}
					if (startItem.isSequential(checkItem)
							&& !CheckNameTitle.isNameCompany(checkItem
									.getStringResult())) {
						// checkItem.setProcess(true);
						startItem = checkItem;
						listNumberChoose.add(listNumberChoose.size(), index2);

					}

				}
				// Check kq
				StringBuilder addressCheck = new StringBuilder();
				for (int index3 = 0; index3 < listNumberChoose.size(); index3++) {
					addressCheck.append(listLineItems.get(
							listNumberChoose.get(index3)).getStringResult());
					addressCheck.append(" ");
				}
				if (addressCheck.length() > 0) {
					addressCheck.deleteCharAt(addressCheck.length() - 1);
				}
				if (CheckNameTitle.isNameAddress(addressCheck.toString())) {
					AddressItem addressItem = new AddressItem();
					addressItem.setAddressName(CheckNameTitle
							.deleteTitleAddress(addressCheck.toString()));
					ArrayList<RectItem> listRect = new ArrayList<RectItem>();
					for (int index3 = 0; index3 < listNumberChoose.size(); index3++) {
						listLineItems.get(listNumberChoose.get(index3))
								.setProcess(true);
						Rect rect = listLineItems.get(
								listNumberChoose.get(index3)).getRectRegion();
						RectItem rectItem = new RectItem();
						rectItem.setX(rect.x);
						rectItem.setY(rect.y);
						rectItem.setHeight(rect.height);
						rectItem.setWidth(rect.width);
						listRect.add(rectItem);
					}
					addressItem.setRectItem(this.getRect(listRect));
					dataItem.getListAddress().add(addressItem);
				}
			}

		}
	}

	private boolean isHaveNumber(String str) {
		int countP = 0;
		for (int index = 0; index < str.length(); index++) {
			if (isNumber(str.charAt(index))) {
				return true;
			}
			if (str.charAt(index) == ',') {
				countP++;
			}
		}
		if (countP > 1) {
			return true;
		}
		return false;
	}

	private boolean isNumber(char c) {
		return (c >= '0' && c <= '9');
	}

	private void findNameCompany() {
		for (int index = 0; index < listLineItems.size(); index++) {
			LineItem lineItem = listLineItems.get(index);
			if (lineItem.isProcess()) {
				continue;
			}
			if (CheckNameTitle.isNameCompany(lineItem.getStringResult())) {
				ArrayList<Integer> listNumberChoose = new ArrayList<Integer>();
				listNumberChoose.add(0, index);
				lineItem.setProcess(true);
				// Duyet tu duoi len
				LineItem startItem = lineItem;
				for (int index2 = index; index2 > -1; index2--) {
					LineItem checkItem = listLineItems.get(index2);
					if (checkItem.isProcess()) {
						continue;
					}
					if (startItem.isSequentialHeight(checkItem)) {
						checkItem.setProcess(true);
						startItem = checkItem;
						listNumberChoose.add(0, index2);

					}

				}
				// Duyet tu tren xuong
				startItem = lineItem;
				for (int index2 = index; index2 < listLineItems.size(); index2++) {
					LineItem checkItem = listLineItems.get(index2);
					if (checkItem.isProcess()) {
						continue;
					}
					if (startItem.isSequentialHeight(checkItem)) {
						checkItem.setProcess(true);
						startItem = checkItem;
						listNumberChoose.add(listNumberChoose.size(), index2);

					}

				}
				// In kq
				if (listNumberChoose.size() > 0) {
					StringBuilder nameCompany = new StringBuilder();
					ArrayList<RectItem> listRect = new ArrayList<RectItem>();
					for (int index3 = 0; index3 < listNumberChoose.size(); index3++) {
						nameCompany
								.append(listLineItems.get(
										listNumberChoose.get(index3))
										.getStringResult());
						nameCompany.append(" ");
						Rect rect = listLineItems.get(
								listNumberChoose.get(index3)).getRectRegion();
						RectItem rectItem = new RectItem();
						rectItem.setX(rect.x);
						rectItem.setY(rect.y);
						rectItem.setHeight(rect.height);
						rectItem.setWidth(rect.width);
						listRect.add(rectItem);
					}
					nameCompany.deleteCharAt(nameCompany.length() - 1);
					dataItem.setNameCompany(nameCompany.toString());
					dataItem.setRectCompany(this.getRect(listRect));
					break;
				}
			}

		}

	}

	private void makeTitle() {
		int size = listLineItems.size();
		int[] listHeight = new int[size];
		int[] listPosition = new int[size];
		// make data
		for (int index = 0; index < size; index++) {
			listPosition[index] = index;
			listHeight[index] = listLineItems.get(index).getRectRegion().height;
		}
		// sort
		for (int i = 0; i < size; i++)
			for (int j = i + 1; j < size; j++) {
				if (listHeight[i] < listHeight[j]) {
					int temp = listHeight[i];
					listHeight[i] = listHeight[j];
					listHeight[j] = temp;
					//
					temp = listPosition[i];
					listPosition[i] = listPosition[j];
					listPosition[j] = temp;
				}
			}
		// Find nameCard
		int positionName = -1;
		for (int index = 0; index < size; index++) {
			LineItem lineItem = listLineItems.get(listPosition[index]);
			if (lineItem.isProcess()) {
				continue;
			}
			if (CheckNameTitle.isNameCard(listLineItems, listPosition[index])) {
				positionName = listPosition[index];
				break;
			}
		}
		// Make nameCard
		if (positionName != -1) {
			dataItem.setNameCard(listLineItems.get(positionName)
					.getStringResult());
			listLineItems.get(positionName).setProcess(true);
			Rect rect = listLineItems.get(positionName).getRectRegion();
			RectItem rectItem = new RectItem();
			rectItem.setX(rect.x);
			rectItem.setY(rect.y);
			rectItem.setHeight(rect.height);
			rectItem.setWidth(rect.width);
			dataItem.setRectName(rectItem);
			makeJobName(positionName);
			// Find Company
			if (null == dataItem.getNameCompany()) {
				StringBuilder nameCompanyBuider = new StringBuilder();
				ArrayList<RectItem> listRect = new ArrayList<RectItem>();
				for (int index = 0; index < positionName; index++) {
					LineItem lineItem = listLineItems.get(index);
					if (lineItem.isProcess()) {
						continue;
					}
					lineItem.setProcess(true);
					Rect rect1 = lineItem.getRectRegion();
					RectItem rectItem1 = new RectItem();
					rectItem1.setX(rect1.x);
					rectItem1.setY(rect1.y);
					rectItem1.setHeight(rect1.height);
					rectItem1.setWidth(rect1.width);
					listRect.add(rectItem1);
					nameCompanyBuider.append(lineItem.getStringResult());
					nameCompanyBuider.append(' ');
				}
				if (nameCompanyBuider.length() > 0) {
					nameCompanyBuider
							.deleteCharAt(nameCompanyBuider.length() - 1);
					dataItem.setNameCompany((nameCompanyBuider.toString()));
					dataItem.setRectCompany(this.getRect(listRect));
				}
			}
		}
		// Find Other
		makeOther();
	}

	private void makeOther() {
		StringBuilder nameOtherBuider = new StringBuilder();
		for (int index = 0; index < listLineItems.size(); index++) {
			LineItem lineItem = listLineItems.get(index);
			if (lineItem.isProcess()) {
				continue;
			}
			lineItem.setProcess(true);
			nameOtherBuider.append(lineItem.getStringResult());
			nameOtherBuider.append('\n');
		}
		if (nameOtherBuider.length() > 0) {
			this.setOtherName(nameOtherBuider.toString());
		}
	}

	private void makeJobName(int position) {
		LineItem startItem = listLineItems.get(position);
		for (int index = position + 1; index < listLineItems.size(); index++) {
			LineItem checkItem = listLineItems.get(index);
			if (checkItem.isProcess()) {
				continue;
			}
			if (startItem.isSequential(checkItem)) {
				this.setJobName(checkItem.getStringResult());
				checkItem.setProcess(true);
				break;
			}
		}

	}

	private RectItem getRect(ArrayList<RectItem> listRect) {
		RectItem resultRect = new RectItem();
		resultRect.setY(listRect.get(0).getY());
		resultRect.setHeight(listRect.get(listRect.size() - 1).getY()
				+ listRect.get(listRect.size() - 1).getHeight()
				- resultRect.getY());
		int minX = listRect.get(0).getX();
		int maxX = listRect.get(0).getX() + listRect.get(0).getWidth();
		for (int index = 0; index < listRect.size(); index++) {
			RectItem rectItem = listRect.get(index);
			if (minX > rectItem.getX()) {
				minX = rectItem.getX();
			}
			if (maxX < rectItem.getX() + rectItem.getWidth()) {
				maxX = rectItem.getX() + rectItem.getWidth();
			}
		}
		resultRect.setX(minX);
		resultRect.setWidth(maxX - minX);
		return resultRect;
	}

	private String normalizedString(String str) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(str);
		// Chuan hoa dau \n
		for (int index = 0; index < stringBuilder.length(); index++) {
			if (stringBuilder.charAt(index) == '\n') {
				stringBuilder.replace(index, index + 1, " ");
			}
		}
		while (stringBuilder.charAt(0) == ' ') {
			stringBuilder.deleteCharAt(0);
		}
		while (stringBuilder.charAt(stringBuilder.length() - 1) == ' ') {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		int begin = 0;
		while (begin < stringBuilder.length() - 1) {
			if (stringBuilder.charAt(begin) == ' '
					&& stringBuilder.charAt(begin + 1) == ' ') {
				stringBuilder.deleteCharAt(begin);
			} else {
				begin++;
			}
		}

		return stringBuilder.toString();
	}

	private void normalizedAll() {
		for (int index = 0; index < listLineItems.size(); index++) {
			LineItem item = listLineItems.get(index);
			item.setStringResult(normalizedString(item.getStringResult()));
		}
	}

	private void standardizedAll() {
		// company
		if (null != dataItem.getNameCompany()) {
			dataItem.setNameCompany(Standardized.normalCompany(dataItem
					.getNameCompany()));
		}
		// address
		ArrayList<AddressItem> listAddressItems = dataItem.getListAddress();
		for (int index = 0; index < listAddressItems.size(); index++) {
			AddressItem addressItem = new AddressItem();
			AddressItem addressItemOld = listAddressItems.get(index);
			addressItem.setRectItem(addressItemOld.getRectItem());
			addressItem.setAddressName(Standardized
					.normalAddress(addressItemOld.getAddressName()));
			listAddressItems.add(index, addressItem);
			listAddressItems.remove(index + 1);
		}
		// web
		ArrayList<WebItem> listWebItems = dataItem.getListWeb();
		for (int index = 0; index < listWebItems.size(); index++) {
			WebItem webItem = new WebItem();
			WebItem webItemOld = listWebItems.get(index);
			webItem.setRectItem(webItemOld.getRectItem());
			webItem.setWebName(Standardized.normalUrl(webItemOld.getWebName()));
			listWebItems.add(index, webItem);
			listWebItems.remove(index + 1);
		}
		// email
		ArrayList<EmailItem> listEmailItems = dataItem.getListEmail();
		for (int index = 0; index < listEmailItems.size(); index++) {
			EmailItem emailItem = new EmailItem();
			EmailItem emailItemOld = listEmailItems.get(index);
			emailItem.setRectItem(emailItemOld.getRectItem());
			emailItem.setEmailName(Standardized.normalUrl(emailItemOld
					.getEmailName()));
			listEmailItems.add(index, emailItem);
			listEmailItems.remove(index + 1);
		}
	}

	public DataItem processSemantic() {
		normalizedAll();
		this.getEmail();
		this.getWebsite();
		this.getNumberPhone();
		this.getAddress();
		this.findNameCompany();
		this.makeTitle();
		standardizedAll();
		return dataItem;
	}

}