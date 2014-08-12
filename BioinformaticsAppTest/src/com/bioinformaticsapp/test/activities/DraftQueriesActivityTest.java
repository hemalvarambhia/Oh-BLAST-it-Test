package com.bioinformaticsapp.test.activities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;

import java.util.List;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.bioinformaticsapp.ViewBLASTQuerySearchParameters;
import com.bioinformaticsapp.ListDraftBLASTQueries;
import com.bioinformaticsapp.SetUpEMBLEBIBLASTQuery;
import com.bioinformaticsapp.SetUpNCBIBLASTQuery;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

public class DraftQueriesActivityTest extends ActivityInstrumentationTestCase2<ListDraftBLASTQueries> {
	private Solo solo;
	private Context ctx;
 	
	public DraftQueriesActivityTest() {
		super(ListDraftBLASTQueries.class);	
	}
	
	public void setUp() throws Exception {
		super.setUp();
		ctx = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(ctx);
		helper.cleanDatabase();
	}

	public void testWeCanViewAllDraftQueries(){
		saveQuery(BLASTQueryBuilder.aBLASTQuery());
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class, 1, 5000);
		
		assertOnlyDisplaysDraftQueries();
	}
	
	public void testWeCanOpenAnNCBIQueryWhenTappingOnTheCorrespondingListItem(){
		saveQuery(BLASTQuery.ncbiBLASTQuery("blastn"));
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		solo.clickInList(1);
		
		solo.assertCurrentActivity("Expected the NCBI Query Setup screen", SetUpNCBIBLASTQuery.class);
	}
	
	public void testWeCanOpenAnEMBLQueryWhenTappingOnTheCorrespondingListItem(){
		saveQuery(BLASTQuery.emblBLASTQuery("blastn"));
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		solo.clickInList(1);
		
		solo.assertCurrentActivity("Expected the EMBL Query Setup screen", SetUpEMBLEBIBLASTQuery.class);
	}
	
	public void testWeCanCreateAnEMBLQuery(){
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnActionBarItem(R.menu.create_query_menu);
		solo.clickOnActionBarItem(R.id.create_embl_query);
		
		solo.assertCurrentActivity("Expected the EMBL Query set up screen", SetUpEMBLEBIBLASTQuery.class);
	}
	
	public void testWeCanCreateAnNCBIQuery(){
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.clickOnActionBarItem(R.menu.create_query_menu);
		solo.clickOnActionBarItem(R.id.create_ncbi_query);
		
		solo.assertCurrentActivity("Expected the NCBI Query set up screen", SetUpNCBIBLASTQuery.class);
	}
	
	public void testWeCanDeleteABLASTQuery(){
		saveQuery(BLASTQueryBuilder.aBLASTQuery());
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		solo.clickLongInList(1);
		solo.clickOnText("Delete");
		solo.clickOnButton("OK");
		solo.waitForView(ListView.class, 1, 5000);
		
		ListView listView = solo.getCurrentListViews().get(0);
		assertThat("Should be able to delete a query", listView.getCount(), is(0));
	}

	public void testWeCanCancelADeleteQueryAction(){
		saveQuery(BLASTQueryBuilder.aBLASTQuery());
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(ListView.class);
		solo.clickLongInList(1);
		solo.clickOnText("Delete");
		solo.clickOnButton("Cancel");
		solo.waitForView(ListView.class);
		
		ListView listView = solo.getCurrentListViews().get(0);
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(ctx);
		List<BLASTQuery> draftQueries = labBook.findBLASTQueriesByStatus(Status.DRAFT);
		assertThat("Should be able to cancel delete a query", listView.getCount(), is(draftQueries.size()));
	}
	
	public void testWeCanViewTheParametersOfAnEMBLQuery(){
		saveQuery(BLASTQuery.emblBLASTQuery("blastn"));
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		solo.clickLongInList(1);
		String viewParametersOption = ctx.getResources().getString(R.string.view_query_parameters);
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("Search parameters activity should show", ViewBLASTQuerySearchParameters.class);
	}

	public void testThatWeCanViewTheParametersOfAnNCBIQuery(){
		saveQuery(BLASTQuery.ncbiBLASTQuery("blastn"));
		solo = new Solo(getInstrumentation(), getActivity());
		
		solo.waitForView(TextView.class);
		solo.clickLongInList(1);
		String viewParametersOption = ctx.getResources().getString(R.string.view_query_parameters);
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("The search parameters activity should show", ViewBLASTQuerySearchParameters.class);
	}

	private void saveQuery(BLASTQuery query){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(ctx);
		BLASTQuery saved = labBook.save(query);
		assertNotNull(saved.getPrimaryKey());
	}
	
	private void assertOnlyDisplaysDraftQueries(){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(ctx);
		List<BLASTQuery> draftQueries = labBook.findBLASTQueriesByStatus(Status.DRAFT);
		ListView listView = solo.getCurrentListViews().get(0);
		assertThat(listView.getCount(), is(draftQueries.size()));
	}
	
	public void tearDown() throws Exception {
		if(solo!=null)
			solo.finishOpenedActivities();
		super.tearDown();
	}
}
