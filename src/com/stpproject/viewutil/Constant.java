package com.stpproject.viewutil;

public class Constant {
	//key
		public static final String KEY_IMAGE_PATH_FOR_PROCESSING = "image_path";
		public static final String KEY_FINISH_SCAN               = "done";
		public static final String KEY_PATH_TESS_DATA            = "datapath"; // thu muc chua folder tessdata
		public static final String KEY_LANG_TESS                 = "lang";
		public static final String KEY_IS_DATA_COPIED            = "data_copy";
		public static final String KEY_SEND_FROM         		 = "process";
		public static final String KEY_ITEM_ID_SEND				 = "item_id";
		
		//value
		public static final int VALUE_SEND_FROM_PROCESS          = 1;
		public static final int VALUE_NOT_KNOW_WHERE			 = -1;
		public static final int VALUE_SEND_FROM_DETAILS			 = 2;
		public static final String VALUE_NOT_EXISTED             = "not_existed";
		
		//action
		public static final String ACTION_TESS_RESULT_RECEIVER        = "com.executortask.result";
		public static final String ACTION_RETRIEVE_IMAGE_RECEIVER	  = "com.retrieveimage.receiver";
		public static final String ACTION_WRITEIMAGE_DONE_RECEIVER	  = "com.writeimage.done";
		
		//name
		public static final String PREFERENCE_FILE_NAME          = "mysetting";
		
		//default
		public static final String DEFAULT_TESS_DATA_PATH_PARENT = "/stpdata";
		
		//test
		public static final String LOG_NAME = "LogBCR.txt";
		
		//request
		public static final int REQUEST_IMAGE_GALLERY            = 113;
		public static final int REQUEST_EDIT_FROM_DETAILS		 = 115;		 
}
