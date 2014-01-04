package com.bioinformaticsapp.test.functional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.ArrayList;
import java.util.List;

import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.test.testhelpers.OhBLASTItTestHelper;

public class SearchParameterControllerTest extends InstrumentationTestCase {

	private SearchParameterController searchParameterController;
	
	protected void setUp() throws Exception {
		super.setUp();
		OhBLASTItTestHelper helper = new OhBLASTItTestHelper(getInstrumentation().getTargetContext());
		helper.cleanDatabase();
		searchParameterController = new SearchParameterController(getInstrumentation().getTargetContext());
	}
	
	protected void tearDown() throws Exception {
		searchParameterController.close();
		super.tearDown();	
	}
	
	public void testWeCanSaveTheSearchParametersOfAQuery(){
		long blastQueryId = 1l;
		SearchParameter parameter = new SearchParameter("email", "h.n.varambhia@gmail.com");
		
		searchParameterController.saveFor(blastQueryId, parameter);
		
		SearchParameter parameterFromDatastore = searchParameterController.getParametersForQuery(blastQueryId).get(0);
		assertThat("Should be able store a SearchParameter in datastore", parameterFromDatastore, is(parameter));
	}

	public void testWeCanRetrieveTheOptionalParametersOfAQuery(){
		long blastQueryId = 1l;
		SearchParameter parameter = new SearchParameter("email", "h.n.varambhia@gmail.com");
		searchParameterController.saveFor(blastQueryId, parameter);
		
		List<SearchParameter> parameters = searchParameterController.getParametersForQuery(blastQueryId);
		
		List<SearchParameter> expected = new ArrayList<SearchParameter>();
		expected.add(parameter);
		assertThat("Should be able to retrieve the search parameters of a query", parameters, is(expected));
	}
	
}
