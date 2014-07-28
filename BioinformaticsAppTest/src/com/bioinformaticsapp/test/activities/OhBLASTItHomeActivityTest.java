package com.bioinformaticsapp.test.activities;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.bioinformaticsapp.AppPreferences;
import com.bioinformaticsapp.OhBLASTItAppHomeActivity;
import com.bioinformaticsapp.SetUpEMBLEBIBLASTQuery;
import com.bioinformaticsapp.SetUpNCBIBLASTQuery;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;
//Test case for the home screen
public class OhBLASTItHomeActivityTest extends ActivityInstrumentationTestCase2<OhBLASTItAppHomeActivity> {

	private final static String TAG = "BioInformaticsAppHomeActivityTest";
	private Solo solo;
	private Context ctx;
	public OhBLASTItHomeActivityTest() {
		super(OhBLASTItAppHomeActivity.class);
		
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		ctx = getInstrumentation().getTargetContext();
		
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(ctx);
		
		helper.cleanDatabase();
		
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
		
		solo.assertCurrentActivity("Expected the EMBL set up activity to be launched", SetUpEMBLEBIBLASTQuery.class);
	}
	
	public void testWeCanStartAnNCBIQuery(){
		solo.clickOnActionBarItem(R.menu.main_menu);
		solo.clickOnActionBarItem(R.id.create_ncbi_query);
		
		solo.assertCurrentActivity("Expected the NCBI set up activity to be launched", SetUpNCBIBLASTQuery.class);
	}
	
	public void testWeCanGoToThePreferencesActivity(){
		solo.clickOnActionBarItem(R.menu.main_menu);
		solo.clickOnActionBarItem(R.id.settings_item);
		
		solo.assertCurrentActivity("Expected the preferences activity to be launched", AppPreferences.class);
	}
	
}
