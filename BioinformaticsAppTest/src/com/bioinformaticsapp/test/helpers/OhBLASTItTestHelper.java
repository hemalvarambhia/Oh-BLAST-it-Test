package com.bioinformaticsapp.test.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.DatabaseHelper;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.SearchParameter;

public class OhBLASTItTestHelper {

	private static final String TAG = "OhBLASTItTestHelper";

	public OhBLASTItTestHelper(Context context){
		mContext = context;
	}
	
	public void cleanDatabase(){
		DatabaseHelper helper = new DatabaseHelper(mContext);
		
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
		BLASTQueryController queryController = new BLASTQueryController(mContext);
		OptionalParameterController parameterController = new OptionalParameterController(mContext);
		long queryPrimaryKey = queryController.save(query);
		for(SearchParameter parameter: query.getAllParameters()){
			parameter.setBlastQueryId(queryPrimaryKey);
			parameterController.save(parameter);
		}
		
		parameterController.close();
		queryController.close();
		
		return queryPrimaryKey;
	}
	
	private Context mContext;
	
}
