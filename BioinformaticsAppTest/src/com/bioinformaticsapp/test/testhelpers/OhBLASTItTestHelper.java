package com.bioinformaticsapp.test.testhelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.data.DatabaseHelper;
import com.bioinformaticsapp.models.BLASTQuery;

public class OhBLASTItTestHelper {

	private static final String TAG = "OhBLASTItTestHelper";

	public OhBLASTItTestHelper(Context context){
		this.context = context;
	}
	
	public void cleanDatabase(){
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
	
	public long save(BLASTQuery query){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(context);
		BLASTQuery saved = labBook.save(query);
		return saved.getPrimaryKey();
	}
	
	private Context context;
	
}
