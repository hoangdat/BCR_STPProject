package com.stpproject.test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;





import com.example.bcrstpproject.R;
import com.stpproject.model.AddressItem;
import com.stpproject.model.DataItem;
import com.stpproject.model.EmailItem;
import com.stpproject.model.PhoneItem;
import com.stpproject.model.WebItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AddItem {

	public static ArrayList<DataItem> add5name(Context context) {
		ArrayList<DataItem> arr = new ArrayList<DataItem>();
//		Bitmap bMap = BitmapFactory.decodeResource(context.getResources(),
//				R.drawable.ste);
		Bitmap bMap2 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ste);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bMap2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] image2 = stream.toByteArray();

		String[] mStrings = { "1Abbaye de Bellochuhuhuhu nudd nuhuu dhuahudha dndd", "2Mbbaye du Mont des Cats",
				"%Abertam", "^Fbondance", "&Ockawi", "Ecorn", "Odelost",
				"affidelice au Chablis", "afuega'l Pitu", "airag", "airedale",
				"aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert",
				"American Cheese", "Ami du Chambertin", "Anejo Enchilado",
				"Anneau du Vic-Bilh", "Anthoriro", "Appenzell", "Aragon",
				"Ardi Gasna", "Ardrahan", "Armenian String",
				"Aromes au Gene de Marc", "Asadero", "Asiago",
				"Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss",
				"Wabybel", "Qaguette Laonnaise", "Sakers", "Daladi", "Falaton",
				"Gandal", "Hanon", "Jarry's Bay Cheddar", "Kasing",
				"Casket Cheese", "Vath Cheese", "Zavarian Bergkase",
				"Maylough", "Meaufort", "Neauvoorde", "Beenleigh Blue",
				"Beer Cheese", "Bel Paese", "Bergader", "Bergere Bleue",
				"Berkswell", "Beyaz Peynir", "Bierkase", "Bishop Kennedy",
				"Blarney", "Bleu d'Auvergne", "Bleu de Gex",
				"Bleu de Laqueuille", "Bleu de Septmoncel", "Bleu Des Causses",
				"Blue", "Blue Castello", "Blue Rathgore",
				"Blue Vein (Australian)", "Blue Vein Cheeses", "Bocconcini",
				"Bocconcini (Australian)", "Boeren Leidenkaas", "Bonchester",
				"Bosworth", "Bougon", "Boule Du Roves", "Boulette d'Avesnes",
				"Boursault", "Boursin", "Bouyssou", "Bra", "Braudostur",
				"Breakfast Cheese", "Brebis du Lavort", "Brebis du Lochois",
				"Brebis du Puyfaucon", "Bresse Bleu", "Brick", "Brie",
				"Brie de Meaux", "Brie de Melun", "Brillat-Savarin", "Brin",
				"Brin d' Amour", "Brin d'Amour", "Brinza (Burduf Brinza)",
				"Briquette de Brebis", "Briquette du Forez", "Broccio",
				"Broccio Demi-Affine", "Brousse du Rove", "Bruder Basil",
				"Brusselae Kaas (Fromage de Bruxelles)", "Bryndza" };
		for (int i = 0; i < 50; i++) {
			DataItem di = new DataItem();
			di.setNameCard(mStrings[i]);
//			di.setImage(image2);
			di.setNameCompany(mStrings[mStrings.length - i - 1]);
			arr.add(di);
		}
		return arr;
	}
	
	public static DataItem createADataItem(Context context) {
		DataItem data = new DataItem();
		
		data.setNameCard("Nguyen Van Ngu");
//		Bitmap bMap2 = BitmapFactory.decodeResource(context.getResources(),
//				R.drawable.ste);
//		
//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		bMap2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//		byte[] image2 = stream.toByteArray();
//		data.setImage(image2);
		data.setNameCompany("Samsung Viet Nam ABCDEF");
		ArrayList<PhoneItem> lstPhone = new ArrayList<PhoneItem>();
		for (int i = 0; i < 4; i++) {
			Random rnd = new Random(1000);
			long a = System.currentTimeMillis();
			PhoneItem phone = new PhoneItem();
			String strPh = String.format("09%d%d%d", a, rnd.nextInt(), i);
			phone.setPhoneName(strPh);
			lstPhone.add(phone);
		}
		data.setListPhone(lstPhone);
		
		ArrayList<EmailItem> lstMail = new ArrayList<EmailItem>();
		for (int i = 0; i < 3; i++) {
			Random rnd = new Random(1000);
			long a = System.currentTimeMillis();
			EmailItem email = new EmailItem();
			String strPh = String.format("hoang%d@%dgmail.com%d", a, rnd.nextInt(), i);
			email.setEmailName(strPh);
			lstMail.add(email);
		}
		data.setListEmail(lstMail);
		
		ArrayList<AddressItem> lstAddress = new ArrayList<AddressItem>();
		for (int i = 0; i < 2; i++) {
			long a = System.currentTimeMillis();
			AddressItem add = new AddressItem();
			String strAdd = String.format("%d Hoang Hoa THam Q.%d ", a, i);
			add.setAddressName(strAdd);
			lstAddress.add(add);
		}
		data.setListAddress(lstAddress);
		
		ArrayList<WebItem> lstWeb = new ArrayList<WebItem>();
		for (int i = 0; i < 4; i++) {
			long a = System.currentTimeMillis();
			WebItem add = new WebItem();
			String strAdd = String.format("%dsamsung%d.com ", a, i);
			add.setWebName(strAdd);
			lstWeb.add(add);
		}
		data.setListWeb(lstWeb);
		
		return data;
	}

}
