package com.bioinformaticsapp.test.activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.bioinformaticsapp.BioinformaticsAppHomeActivity;
import com.bioinformaticsapp.EMBLEBISetUpQueryActivity;
import com.bioinformaticsapp.NCBIQuerySetUpActivity;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.data.DatabaseHelper;
import com.bioinformaticsapp.models.BLASTQuery;
import com.jayway.android.robotium.solo.Solo;

public class BioInformaticsAppHomeActivityTest extends ActivityInstrumentationTestCase2<BioinformaticsAppHomeActivity> {

	private final static String TAG = "BioInformaticsAppHomeActivityTest";
	private Solo solo;
	private Context ctx;
	public BioInformaticsAppHomeActivityTest() {
		super(BioinformaticsAppHomeActivity.class);
		
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		ctx = getInstrumentation().getTargetContext();
		
		solo = new Solo(getInstrumentation(), getActivity());
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
	}
	
	protected void tearDown() throws Exception {
		if(solo != null){
			solo.finishOpenedActivities();
		}
		
		super.tearDown();
	}
	
	public void testWeCanStartAnEMBLQuery(){
		solo.clickOnActionBarItem(R.menu.main_menu);
		solo.clickOnActionBarItem(R.id.create_embl_query);
		
		solo.assertCurrentActivity("Expected the EMBL set up activity to be launched", EMBLEBISetUpQueryActivity.class);
	}
	
	public void testWeCanStartAnNCBIQuery(){
		solo.clickOnActionBarItem(R.menu.main_menu);
		solo.clickOnActionBarItem(R.id.create_ncbi_query);
		
		solo.assertCurrentActivity("Expected the NCBI set up activity to be launched", NCBIQuerySetUpActivity.class);
	}
	
}
