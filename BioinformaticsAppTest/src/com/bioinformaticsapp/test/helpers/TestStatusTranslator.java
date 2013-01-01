package com.bioinformaticsapp.test.helpers;

import junit.framework.TestCase;

import com.bioinformaticsapp.helpers.StatusTranslator;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.web.SearchStatus;

public class TestStatusTranslator extends TestCase {

	private StatusTranslator translator;
	
	public void setUp(){
		translator = new StatusTranslator();
	}
	
	public void tearDown(){
		translator = null;
	}
	
	public void testThatSearchStatusRUNNINGYieldsSENT(){
		Status status = translator.translate(SearchStatus.RUNNING);
		
		assertEquals("RUNNING means it was SUBMITTED", Status.SUBMITTED, status);
	}
	
	public void testThatSearchStatusFINISHEDYieldsFINISHED(){
		Status status = translator.translate(SearchStatus.FINISHED);
		
		assertEquals("FINISHED means FINISHED", Status.FINISHED, status);
	}
	
	public void testThatSearchStatusERRORYieldsERROR(){
		Status status = translator.translate(SearchStatus.ERROR);
		
		assertEquals("ERROR means ERROR", Status.ERROR, status);
	}
	
	public void testThatSearchStatusNOT_FOUNDYieldsNOT_FOUND(){
		Status status = translator.translate(SearchStatus.NOT_FOUND);
		
		assertEquals("NOT_FOUND means NOT_FOUND", Status.NOT_FOUND, status);
	}
	
	public void testThatUNSUREYieldsUNSURE(){
		Status status = translator.translate(SearchStatus.UNSURE);
		
		assertEquals("UNSURE means UNSURE", Status.UNSURE, status);
	}
	
}
