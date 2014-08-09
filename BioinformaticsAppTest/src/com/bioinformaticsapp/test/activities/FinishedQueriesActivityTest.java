package com.bioinformaticsapp.test.activities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.bioinformaticsapp.ViewBLASTQuerySearchParameters;
import com.bioinformaticsapp.ListFinishedBLASTQueries;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.ViewBLASTHitsActivity;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.persistence.BLASTQueryController;
import com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class FinishedQueriesActivityTest extends
		ActivityInstrumentationTestCase2<ListFinishedBLASTQueries> {

	private OhBLASTItTestHelper helper;
	private Solo solo;
	
	private String exampleEMBLJobId = "ncbiblast-R20120418-133731-0240-81389354-pg";
	private BLASTQuery emblQuery;
	
	private String exampleNCBIJobId = "YAMJ8623016";
	private BLASTQuery ncbiQuery;
	
	public FinishedQueriesActivityTest(
			Class<ListFinishedBLASTQueries> activityClass) {
		super(activityClass);
		
	}
	
	public FinishedQueriesActivityTest(){
		super(ListFinishedBLASTQueries.class);
	}
	
	public void setUp() throws Exception {
		
		super.setUp();
		helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		
	}
	
	public void tearDown() throws Exception {
		
		solo.finishOpenedActivities();
		
		super.tearDown();
	}
	
	
	public void testContextMenuContainsOptionToSeeParameters(){
		
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setStatus(BLASTQuery.Status.FINISHED);
		emblQuery.setJobIdentifier(exampleEMBLJobId);		
		emblQuery.setSearchParameter("email", "example@email.com");
		helper.save(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		boolean menuItemShown = solo.searchText(viewParametersOption);
		
		assertTrue("Context menu with item allowing user to see parameters", menuItemShown);
		
	}

	public void testWeCanSeeTheParametersOfSelectedEMBLQuery(){
		
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setStatus(BLASTQuery.Status.FINISHED);
		emblQuery.setJobIdentifier(exampleEMBLJobId);		
		emblQuery.setSearchParameter("email", "example@email.com");
		helper.save(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("Should show the search parameters activity", ViewBLASTQuerySearchParameters.class);
		
		boolean checkJobIdIsShownAsTitle = solo.searchText(emblQuery.getJobIdentifier());
		
		assertTrue("Title is the job identifier", checkJobIdIsShownAsTitle);
		
			
	}
	
	public void testWeCanSeeTheParametersOfSelectedNCBIQuery(){
		
		ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		ncbiQuery.setStatus(BLASTQuery.Status.FINISHED);
		ncbiQuery.setJobIdentifier(exampleNCBIJobId);		
		helper.save(ncbiQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("Should show the search parameters activity", ViewBLASTQuerySearchParameters.class);
		
		boolean checkJobIdIsShownAsTitle = solo.searchText(ncbiQuery.getJobIdentifier());
		
		assertTrue("Title is the job identifier", checkJobIdIsShownAsTitle);
			
	}
	
	public void testWeCanDeleteABLASTQuery() throws IOException{
		
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setStatus(BLASTQuery.Status.FINISHED);
		emblQuery.setJobIdentifier(exampleEMBLJobId);		
		emblQuery.setSearchParameter("email", "example@email.com");
		copyBLASTHitsFileToAppDataDir(exampleEMBLJobId+".xml");
		helper.save(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String delete = "Delete";
		
		solo.clickOnText(delete);
		
		solo.clickOnText("OK");
		
		solo.waitForView(ListView.class);
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		
		for(ListView listView : listViews){
			
			assertEquals("Should be able to delete a query", 0, listView.getAdapter().getCount());
			
		}
		
		boolean blastHitsFileDeleted = false;
		
		try{
			getInstrumentation().getTargetContext().openFileInput(exampleEMBLJobId+".xml");
		}catch(FileNotFoundException e){
			blastHitsFileDeleted = true;
		}
		
		assertTrue("The file containing the BLAST hits should be deleted", blastHitsFileDeleted);
	}
	
	public void testWeCanCancelADeleteQueryAction() throws IOException{
		emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		emblQuery.setStatus(BLASTQuery.Status.FINISHED);
		emblQuery.setJobIdentifier(exampleEMBLJobId);		
		emblQuery.setSearchParameter("email", "example@email.com");
		copyBLASTHitsFileToAppDataDir(exampleEMBLJobId+".xml");
		helper.save(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String delete = "Delete";
		
		solo.clickOnText(delete);
		
		//Cancel deletion
		solo.clickOnButton("Cancel");
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		BLASTQueryController queryController = new BLASTQueryController(getInstrumentation().getTargetContext());
		List<BLASTQuery> finishedQueries = queryController.findBLASTQueriesByStatus(Status.FINISHED);
		queryController.close();
		for(ListView listView : listViews){
			
			assertEquals("Cancellation should not delete the query", finishedQueries.size(), listView.getAdapter().getCount());
			
		}
	}
	
	public void testWeCanViewTheBLASTHitsOfAQuery() throws IOException{
		emblQuery = BLASTQueryBuilder.aBLASTQueryWithStatus(Status.FINISHED);
		emblQuery.setJobIdentifier(exampleEMBLJobId);
		copyBLASTHitsFileToAppDataDir(String.format("%s.xml", exampleEMBLJobId));
		helper.save(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		int firstLine = 1;
		solo.clickInList(firstLine);
		solo.waitForActivity(ViewBLASTHitsActivity.class.getName());
		
		solo.assertCurrentActivity("Should show the activity that renders the BLAST hits", ViewBLASTHitsActivity.class);
		
	}
	
	private void copyBLASTHitsFileToAppDataDir(String fileName) throws IOException {
		InputStream input = getInstrumentation().getContext().getAssets().open(fileName);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input)); 
		FileOutputStream dest = getInstrumentation().getTargetContext().openFileOutput(fileName, Context.MODE_PRIVATE);
		PrintWriter writer = new PrintWriter(dest);
		
		String line = null;
		while((line = reader.readLine()) != null){
			
			writer.println(line);
		
		}
		
		writer.close();
	}
}
