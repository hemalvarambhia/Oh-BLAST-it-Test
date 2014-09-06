package com.bioinformaticsapp.test.activities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bioinformaticsapp.ListFinishedBLASTQueries;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.ViewBLASTHits;
import com.bioinformaticsapp.ViewBLASTQuerySearchParameters;
import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import static com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder.*;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class FinishedQueriesActivityTest extends
		ActivityInstrumentationTestCase2<ListFinishedBLASTQueries> {

	private Solo solo;
	private String exampleEMBLJobId = "ncbiblast-R20120418-133731-0240-81389354-pg";
	private BLASTQuery emblQuery;
	
	public FinishedQueriesActivityTest(){
		super(ListFinishedBLASTQueries.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		emblQuery = aBLASTQueryWithStatus(Status.FINISHED);
		emblQuery.setJobIdentifier(exampleEMBLJobId);
		helper.save(emblQuery);
		copyBLASTHitsFileToAppSandbox(String.format("%s.xml", exampleEMBLJobId));
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
	
	public void testContextMenuContainsOptionToSeeParameters(){
		solo.waitForView(TextView.class);
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		boolean menuItemShown = solo.searchText(viewParametersOption);
		assertTrue("Context menu with item allowing user to see parameters", menuItemShown);
	}

	public void testWeCanSeeTheParametersOfSelectedEMBLQuery(){
		solo.waitForView(TextView.class);
		solo.clickLongInList(1);
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("Should show the search parameters activity", ViewBLASTQuerySearchParameters.class);
		boolean checkJobIdIsShownAsTitle = solo.searchText(emblQuery.getJobIdentifier());
		assertTrue("Title is the job identifier", checkJobIdIsShownAsTitle);
	}
	
	public void testWeCanDeleteABLASTQuery() throws IOException{
		solo.waitForView(TextView.class);
		solo.clickLongInList(1);
		solo.clickOnText("Delete");
		solo.clickOnText("OK");
		solo.waitForView(ListView.class);
		
		ListView listView = solo.getCurrentListViews().get(0);
		ListAdapter adapter = listView.getAdapter();
		assertThat("Should be able to delete a query", adapter.getCount(), is(0));
		assertBLASTHitsFileDeleted();
	}
	
	public void testWeCanCancelADeleteQueryAction() throws IOException{
		solo.waitForView(TextView.class);
		solo.clickLongInList(1);
		solo.clickOnText("Delete");
		solo.clickOnButton("Cancel");
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		BLASTQueryLabBook queryController = new BLASTQueryLabBook(getInstrumentation().getTargetContext());
		List<BLASTQuery> finishedQueries = queryController.findBLASTQueriesByStatus(Status.FINISHED);
		for(ListView listView : listViews){	
			assertEquals("Cancellation should not delete the query", finishedQueries.size(), listView.getAdapter().getCount());
		}
	}
	
	public void testWeCanViewTheBLASTHitsOfAQuery() throws IOException{
		solo.clickInList(1);
		solo.waitForActivity(ViewBLASTHits.class.getName());
		
		solo.assertCurrentActivity("Should show the activity that renders the BLAST hits", ViewBLASTHits.class);
	}
	

	public void copyBLASTHitsFileToAppSandbox(String hitsFileName) throws IOException {
		InputStream input = getInstrumentation().getContext().getAssets().open(hitsFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input)); 
		FileOutputStream dest = getInstrumentation().getTargetContext().openFileOutput(hitsFileName, Context.MODE_PRIVATE);
		PrintWriter writer = new PrintWriter(dest);
		
		String line = null;
		while((line = reader.readLine()) != null){
			writer.println(line);
		}
		writer.close();
	}
	
	private void assertBLASTHitsFileDeleted() {
		boolean blastHitsFileDeleted = false;
		try{
			getInstrumentation().getTargetContext().openFileInput(exampleEMBLJobId+".xml");
		}catch(FileNotFoundException e){
			blastHitsFileDeleted = true;
		}		
		assertTrue("The file containing the BLAST hits should be deleted", blastHitsFileDeleted);
	}
}
