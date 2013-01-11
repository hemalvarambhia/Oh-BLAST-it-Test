package com.bioinformaticsapp.test.activities;

import com.bioinformaticsapp.BLASTQuerySearchParametersActivity;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

public class BLASTQuerySearchParametersActivityTest extends
		ActivityInstrumentationTestCase2<BLASTQuerySearchParametersActivity> {

	private BLASTQuery anyQuery;
	private Solo solo;
	public BLASTQuerySearchParametersActivityTest(){
		super(BLASTQuerySearchParametersActivity.class);
	}
	
	public void setUp() throws Exception {
		anyQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void tearDown(){
		anyQuery = null;
		solo.finishOpenedActivities();
	}
	
	
}
