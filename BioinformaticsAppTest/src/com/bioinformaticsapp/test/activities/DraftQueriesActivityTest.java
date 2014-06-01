package com.bioinformaticsapp.test.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.bioinformaticsapp.BLASTQuerySearchParametersActivity;
import com.bioinformaticsapp.DraftBLASTQueriesActivity;
import com.bioinformaticsapp.EMBLEBISetUpQueryActivity;
import com.bioinformaticsapp.NCBIQuerySetUpActivity;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.persistence.BLASTQueryController;
import com.bioinformaticsapp.persistence.BLASTQueryLabBook;
import com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class DraftQueriesActivityTest extends ActivityInstrumentationTestCase2<DraftBLASTQueriesActivity> {
	private Solo solo;
	private Context ctx;
 	
	public DraftQueriesActivityTest() {
		super(DraftBLASTQueriesActivity.class);	
	}
	
	public void setUp() throws Exception {
		super.setUp();
		ctx = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(ctx);
		helper.cleanDatabase();
	}

	public void testWeCanViewAllDraftQueries(){
		//FIXME - test is fragile. It failed and passed
		saveQuery(BLASTQueryBuilder.aBLASTQuery());
		solo = new Solo(getInstrumentation(), getActivity());
		
		List<ListView> listViews = solo.getCurrentListViews();
		solo.waitForView(ListView.class);
		
		BLASTQueryController queryController = new BLASTQueryController(ctx);
		List<BLASTQuery> draftQueries = queryController.findBLASTQueriesByStatus(Status.DRAFT);
		for(ListView v: listViews){
			assertEquals("Expected "+draftQueries.size()+" draft queries but got "+v.getAdapter().getCount(), draftQueries.size(), v.getAdapter().getCount());
		}
		
		queryController.close();
		
	}
	
	public void testWeCanOpenAnNCBIQueryWhenTappingOnTheCorrespondingListItem(){
		saveQuery(BLASTQuery.ncbiBLASTQuery("blastn"));
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		solo.clickInList(1);
		
		solo.assertCurrentActivity("Expected the NCBI Query Setup screen", NCBIQuerySetUpActivity.class);
	}
	
	public void testWeCanOpenAnEMBLQueryWhenTappingOnTheCorrespondingListItem(){
		saveQuery(BLASTQuery.emblBLASTQuery("blastn"));
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		solo.clickInList(1);
		
		solo.assertCurrentActivity("Expected the EMBL Query Setup screen", EMBLEBISetUpQueryActivity.class);
	}
	
	public void testWeCanCreateAnEMBLQuery(){
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnActionBarItem(R.menu.create_query_menu);
		solo.clickOnActionBarItem(R.id.create_embl_query);
		
		solo.assertCurrentActivity("Expected the EMBL Query set up screen", EMBLEBISetUpQueryActivity.class);
	}
	
	public void testWeCanCreateAnNCBIQuery(){
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnActionBarItem(R.menu.create_query_menu);
		solo.clickOnActionBarItem(R.id.create_ncbi_query);
		
		solo.assertCurrentActivity("Expected the NCBI Query set up screen", NCBIQuerySetUpActivity.class);
	}
	
	public void testWeCanDeleteABLASTQuery(){
		saveQuery(BLASTQueryBuilder.aBLASTQuery());
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		solo.clickLongInList(1);
		solo.clickOnText("Delete");
		solo.clickOnButton("OK");
		solo.waitForView(ListView.class);
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		for(ListView listView : listViews){
			assertEquals("Should be able to delete a query", 0, listView.getCount());
		}
	}

	public void testWeCanCancelADeleteQueryAction(){
		saveQuery(BLASTQueryBuilder.aBLASTQuery());
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		solo.clickLongInList(1);
		solo.clickOnText("Delete");
		solo.clickOnButton("Cancel");
		solo.waitForView(ListView.class);
		
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		BLASTQueryController queryController = new BLASTQueryController(ctx);
		List<BLASTQuery> draftQueries = queryController.findBLASTQueriesByStatus(Status.DRAFT);
		queryController.close();
		for(ListView listView : listViews){
			assertEquals("Should be able to cancel delete a query", draftQueries.size(), listView.getCount());
		}
	}
	
	public void testWeCanViewTheParametersOfAnEMBLQuery(){
		
		BLASTQuery emblQuery = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		saveQuery(emblQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		
		solo.clickLongInList(1);
		
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("Search parameters activity should show", BLASTQuerySearchParametersActivity.class);
		
		boolean hasATitle = solo.searchText("BLAST Query Parameters");

		assertTrue("There should be an appropriate title", hasATitle);
		
	}

	public void testThatWeCanViewTheParametersOfAnNCBIQuery(){
		BLASTQuery ncbiQuery = new BLASTQuery("blastn", BLASTVendor.NCBI);
		saveQuery(ncbiQuery);
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		solo.clickLongInList(1);
		String viewParametersOption = getInstrumentation().getTargetContext().getResources().getString(R.string.view_query_parameters);
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("The search parameters activity should show", BLASTQuerySearchParametersActivity.class);
		boolean hasATitle = solo.searchText("BLAST Query Parameters");
		assertTrue("Title is the job identifier", hasATitle);		
	}

	private long saveQuery(BLASTQuery query){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(ctx);
		BLASTQuery saved = labBook.save(query);
		assertNotNull(saved.getPrimaryKey());
		return saved.getPrimaryKey();
	}
	
	public void tearDown() throws Exception {
		if(solo!=null)
			solo.finishOpenedActivities();

		super.tearDown();
	}
}
