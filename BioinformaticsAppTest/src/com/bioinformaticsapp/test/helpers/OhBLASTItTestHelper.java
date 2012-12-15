package com.bioinformaticsapp.test.helpers;

import com.bioinformaticsapp.data.DatabaseHelper;
import com.bioinformaticsapp.models.BLASTQuery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class OhBLASTItTestHelper {

	private static final String TAG = "OhBLASTItTestHelper";

	public void cleanDatabase(Context context){
		DatabaseHelper helper = new DatabaseHelper(context);
		
		//Create the database if it does not exist already, or open it if it does
		SQLiteDatabase db = helper.getWritableDatabase();
		
		if(db.delete(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, null, null) > 0){
			Log.i(TAG, "Data from "+BLASTQuery.BLAST_SEARCH_PARAMS_TABLE+" deleted");
		}else{
			Log.i(TAG, BLASTQuery.BLAST_SEARCH_PARAMS_TABLE+" already clean");
		}
		
		if(db.delete(BLASTQuery.BLAST_QUERY_TABLE, null, null) > 0){
			Log.i(TAG, "Data from "+BLASTQuery.BLAST_QUERY_TABLE+" deleted");
		}else{

			Log.i(TAG, BLASTQuery.BLAST_QUERY_TABLE+" already clean");
		}
	
		db.close();
	}
	
}
