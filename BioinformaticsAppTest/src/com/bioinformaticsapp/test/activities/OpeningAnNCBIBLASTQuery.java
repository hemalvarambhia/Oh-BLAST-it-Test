package com.bioinformaticsapp.test.activities;

import com.bioinformaticsapp.ListDraftBLASTQueries;
import com.bioinformaticsapp.R;
import com.bioinformaticsapp.SetUpNCBIBLASTQuery;
import com.bioinformaticsapp.ViewBLASTQuerySearchParameters;
import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.test.testhelpers.BLASTQueryBuilder;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;
import com.jayway.android.robotium.solo.Solo;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

public class OpeningAnNCBIBLASTQuery extends
		ActivityInstrumentationTestCase2<ListDraftBLASTQueries> {

	private Solo solo;
	private Context ctx;

	public OpeningAnNCBIBLASTQuery(){
		super(ListDraftBLASTQueries.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		ctx = getInstrumentation().getTargetContext();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(ctx);
		helper.cleanDatabase();
		saveQuery(BLASTQuery.ncbiBLASTQuery("blastn"));
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testWeCanOpenAnNCBIQueryWhenTappingOnTheCorrespondingListItem(){
		solo.waitForView(ListView.class);
		solo.clickInList(1);
		
		solo.assertCurrentActivity("Expected the NCBI Query Setup screen", SetUpNCBIBLASTQuery.class);
	}
	

	public void testThatWeCanViewTheParametersOfAnNCBIQuery(){
		solo.waitForView(TextView.class);
		solo.clickLongInList(1);
		String viewParametersOption = ctx.getResources().getString(R.string.view_query_parameters);
		solo.clickOnText(viewParametersOption);
		
		solo.assertCurrentActivity("The search parameters activity should show", ViewBLASTQuerySearchParameters.class);
	}

	private void saveQuery(BLASTQuery query) {
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(ctx);
		labBook.save(query);
	}
}
