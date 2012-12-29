package com.bioinformaticsapp.test.services;

import java.util.List;

import com.bioinformaticsapp.SubmitQueryService;
import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.DatabaseHelper;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.test.ServiceTestCase;
import android.util.Log;

public class SubmitQueryServiceTest extends ServiceTestCase<SubmitQueryService> {

	public SubmitQueryServiceTest(Class<SubmitQueryService> serviceClass) {
		super(serviceClass);
		
	}
	
	public SubmitQueryServiceTest(){
		super(SubmitQueryService.class);
	}
	

	private BLASTQueryController queryController;
	private SearchParameterController parameterController;
	private static final String TAG = "SubmitQueryServiceTest";
	private Context ctx;
	protected void setUp() throws Exception {
		super.setUp();
		ctx = getContext();
		DatabaseHelper helper = new DatabaseHelper(ctx);
		
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

		
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		query.setSequence("CCTTTATCTAATCTTTGGAGCATGAGCTGG");
		query.setStatus(BLASTQuery.Status.PENDING);
		
		
		queryController = new BLASTQueryController(ctx);
		parameterController = new SearchParameterController(ctx);
		long queryId = queryController.save(query);
		List<SearchParameter> parameters = query.getAllParameters();
		
		for(SearchParameter parameter: parameters){
			parameter.setBlastQueryId(queryId);
			parameterController.save(parameter);
		}
		
		queryController.close();
		parameterController.close();
	}
	
	public void testWeCanStart(){
		Intent intent = new Intent(ctx, SubmitQueryService.class);
		startService(intent);
	}
	
}
