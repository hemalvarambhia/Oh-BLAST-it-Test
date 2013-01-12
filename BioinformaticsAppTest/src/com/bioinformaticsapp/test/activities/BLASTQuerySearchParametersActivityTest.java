package com.bioinformaticsapp.test.activities;

import com.bioinformaticsapp.BLASTQuerySearchParametersActivity;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.jayway.android.robotium.solo.Solo;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

public class BLASTQuerySearchParametersActivityTest extends
		ActivityInstrumentationTestCase2<BLASTQuerySearchParametersActivity> {

	private Solo solo;
	public BLASTQuerySearchParametersActivityTest(){
		super(BLASTQuerySearchParametersActivity.class);
	}
	
	public void tearDown() throws Exception {
		if(solo != null)	
			solo.finishOpenedActivities();
		super.tearDown();
	}
	
	public void testWeCanSeeTheDatabaseParameterDetails(){
		BLASTQuery anyQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		Intent intent = new Intent();
		intent.putExtra("query", anyQuery);
		setActivityIntent(intent);
		
		solo = new Solo(getInstrumentation(), getActivity());
		solo.waitForView(ListView.class);
		
		boolean hasDatabaseLabel = solo.searchText("Database");
		assertTrue("should display the 'Database' label", hasDatabaseLabel);
		
		boolean displaysDatabaseParameter = solo.searchText(anyQuery.getSearchParameter("database").getValue());
		assertTrue("should display the value of the database parameter", displaysDatabaseParameter);
		
	}
	
	public void testWeCanSeeTheExpThresholdDetails(){
		BLASTQuery anyQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		Intent intent = new Intent();
		intent.putExtra("query", anyQuery);
		setActivityIntent(intent);
		
		solo = new Solo(getInstrumentation(), getActivity());
		solo.waitForView(ListView.class);
		
		boolean hasExpThresholdLabel = solo.searchText("Exponential Threshold");
		assertTrue("should display the 'Exponential Threshold' label", hasExpThresholdLabel);
		
		boolean displaysExpThresholdParameter = solo.searchText(anyQuery.getSearchParameter("exp_threshold").getValue());
		assertTrue("should display the value of the database parameter", displaysExpThresholdParameter);
		
	}
	
	public void testWeCanSeeTheMatchMismatchScoreDetails(){
		BLASTQuery anyQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		Intent intent = new Intent();
		intent.putExtra("query", anyQuery);
		setActivityIntent(intent);
		
		solo = new Solo(getInstrumentation(), getActivity());
		solo.waitForView(ListView.class);
		
		boolean hasMatchMismatchScoreLabel = solo.searchText("Match/Mismatch Score");
		assertTrue("should display the 'Match/Mismatch Score' label", hasMatchMismatchScoreLabel);
		
		boolean displaysMatchMismatchScoreParameter = solo.searchText(anyQuery.getSearchParameter("match_mismatch_score").getValue());
		assertTrue("should display the value of the match/mismatch score parameter", displaysMatchMismatchScoreParameter);
		
	}
	
	public void testWeCanSeeTheEmailParameterOfAnEBIQuery(){
		BLASTQuery ebiemblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		String email = "test.user@ohblastit.com";
		ebiemblQuery.setSearchParameter("email", email);
		Intent intent = new Intent();
		intent.putExtra("query", ebiemblQuery);
		setActivityIntent(intent);
		
		solo = new Solo(getInstrumentation(), getActivity());
		solo.waitForView(ListView.class);
		
		boolean hasEmailLabel = solo.searchText("E-mail");
		assertTrue("should display the 'E-mail' label", hasEmailLabel);
		
		boolean displaysEmailParameter = solo.searchText(ebiemblQuery.getSearchParameter("email").getValue());
		assertTrue("should display the email address parameter", displaysEmailParameter);
		
	}
	
}
