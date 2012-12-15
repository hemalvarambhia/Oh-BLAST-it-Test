package com.bioinformaticsapp.test.activities;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.bioinformaticsapp.BioinformaticsAppHomeActivity;
import com.bioinformaticsapp.EMBLEBISetUpQueryActivity;
import com.bioinformaticsapp.NCBIQuerySetUpActivity;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.test.helpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;
//Test case for the home screen
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
		
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper();
		
		helper.cleanDatabase(ctx);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		
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
