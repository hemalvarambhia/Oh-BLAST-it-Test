package com.bioinformaticsapp.test.helpers;

import junit.framework.TestCase;

import com.bioinformaticsapp.blastservices.SearchStatus;
import com.bioinformaticsapp.helpers.StatusTranslator;
import static com.bioinformaticsapp.models.BLASTQuery.Status;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;

public class TestStatusTranslator extends TestCase {

	private StatusTranslator translator;
	
	public void setUp(){
		translator = new StatusTranslator();
	}
	
	public void testThatSearchStatusRUNNINGYieldsSENT(){
		Status status = translator.translate(SearchStatus.RUNNING);
		
		assertThat("RUNNING means it was SUBMITTED", status, is(Status.SUBMITTED));
	}
	
	public void testThatSearchStatusFINISHEDYieldsFINISHED(){
		Status status = translator.translate(SearchStatus.FINISHED);
		
		assertThat("FINISHED means FINISHED", status, is(Status.FINISHED));
	}
	
	public void testThatSearchStatusERRORYieldsERROR(){
		Status status = translator.translate(SearchStatus.ERROR);
		
		assertThat("ERROR means ERROR", status, is(Status.ERROR));
	}
	
	public void testThatSearchStatusNOT_FOUNDYieldsNOT_FOUND(){
		Status status = translator.translate(SearchStatus.NOT_FOUND);
		
		assertThat("NOT_FOUND means NOT_FOUND", status, is(Status.NOT_FOUND));
	}
	
	public void testThatUNSUREYieldsUNSURE(){
		Status status = translator.translate(SearchStatus.UNSURE);
		
		assertThat("UNSURE means UNSURE", status, is(Status.UNSURE));
	}
	
}
