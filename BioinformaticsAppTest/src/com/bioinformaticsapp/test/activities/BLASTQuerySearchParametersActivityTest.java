package com.bioinformaticsapp.test.activities;

import static org.hamcrest.MatcherAssert.assertThat;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.bioinformaticsapp.BLASTQuerySearchParametersActivity;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder;
import com.jayway.android.robotium.solo.Solo;

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
	
	private void setupWith(BLASTQuery query){
		Intent intent = new Intent();
		intent.putExtra("query", query);
		setActivityIntent(intent);
	}
	
	public void testWeCanSeeTheDatabaseParameterDetails(){
		BLASTQuery anyQuery = BLASTQueryBuilder.aBLASTQuery();
		setupWith(anyQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		
		assertDisplays("Database");
		assertDisplays(anyQuery.getSearchParameter("database"));
	}
	
	public void testWeCanSeeTheExpThresholdDetails(){
		BLASTQuery anyQuery = BLASTQueryBuilder.aBLASTQuery();
		setupWith(anyQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		
		assertDisplays("Exponential Threshold");
		assertDisplays(anyQuery.getSearchParameter("exp_threshold"));
	}
	
	public void testWeCanSeeTheMatchMismatchScoreDetails(){
		BLASTQuery anyQuery = BLASTQueryBuilder.aBLASTQuery();
		setupWith(anyQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		
		assertDisplays("Match/Mismatch Score");
		assertDisplays(anyQuery.getSearchParameter("match_mismatch_score"));
	}
	
	public void testWeCanSeeTheEmailParameterOfAnEBIQuery(){
		BLASTQuery ebiemblQuery = BLASTQuery.emblBLASTQuery("blastn");
		ebiemblQuery.setSearchParameter("email",  "test.user@ohblastit.com");
		setupWith(ebiemblQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		
		assertDisplays("E-mail");
		assertDisplays(ebiemblQuery.getSearchParameter("email"));
	}
	
	public void testWeCanSeeTheScoreParameterOfAnEBIQuery(){
		BLASTQuery ebiemblQuery = BLASTQuery.emblBLASTQuery("blastn");
		setupWith(ebiemblQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		
		assertDisplays("Maximum number of scores");
		assertDisplays(ebiemblQuery.getSearchParameter("score"));	
	}
	
	public void testWeCanSeeTheWordSizeParameterOfAnNCBIQuery(){
		BLASTQuery ncbiQuery = BLASTQuery.ncbiBLASTQuery("blastn");
		setupWith(ncbiQuery);
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		
		assertDisplays("Word size");
		assertDisplays(ncbiQuery.getSearchParameter("word_size"));
	}
	
	private void assertDisplays(String label){
		boolean displaysLabel = solo.searchText(label);
		String failureMessage = String.format("Should display %s", label);
		assertThat(failureMessage, displaysLabel);
	}
	
	private void assertDisplays(SearchParameter parameter){
		boolean displaysParameterValue = solo.searchText(parameter.getValue());
		String failureMessage = String.format("Should display the value of %s", parameter.getName());
		assertThat(failureMessage, displaysParameterValue);
	}
}
