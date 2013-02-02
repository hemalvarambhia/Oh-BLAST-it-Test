package com.bioinformaticsapp.test.functional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.io.NCBIBLASTHitsParser;
import com.bioinformaticsapp.models.BLASTHit;

public class TestNCBIBLASTHitsParser extends InstrumentationTestCase {

	private AssetManager assetManager;

	private NCBIBLASTHitsParser parser;
	
	private static final String TAG = "TestNCBIBLASTHitsParser";
	
	
	protected void setUp() throws Exception {
		super.setUp();
		assetManager = this.getInstrumentation().getContext().getResources().getAssets();
		parser = new NCBIBLASTHitsParser();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	
	}

	public void testWeCannotParseWithANullReader(){
		
		boolean cannotParse = false;
		
		try {
			parser.parse(null);
		} catch (Exception e) {
			cannotParse = true;
		}
		
		assertTrue(cannotParse);
		
		
	}

	public void testWeCanParseAnNCBIBlastSearchXmlFile(){
		
		InputStream blasthitsXmlFile = null;
		
		try {
			blasthitsXmlFile = assetManager.open("YAMJ8623016.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("Could not find xml file in assets directory");
		}
		
		parser = new NCBIBLASTHitsParser();
			
		List<BLASTHit> hits = new ArrayList<BLASTHit>();
		try {
			hits = parser.parse(blasthitsXmlFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail("Error during parsing: "+e.getMessage());
		}
		
		assertTrue(hits.size() == 100);
	}
	
	public void testWeGetTheCorrectAccessionNumberOfABlastHit(){
InputStream blasthitsXmlFile = null;
		
		try {
			blasthitsXmlFile = assetManager.open("YAMJ8623016.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("Could not find xml file in assets directory");
		}
		
		parser = new NCBIBLASTHitsParser();
			
		List<BLASTHit> hits = new ArrayList<BLASTHit>();
		try {
			hits = parser.parse(blasthitsXmlFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail("Error during parsing: "+e.getMessage());
		}
		
		BLASTHit hit = hits.get(32);
	
		assertEquals("HM542328", hit.getAccessionNumber());
		
	}
	
	public void testWeGetTheCorrectDescriptionOfABlastHit(){
		
		InputStream blasthitsXmlFile = null;
		
		try {
			blasthitsXmlFile = assetManager.open("YAMJ8623016.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("Could not find xml file in assets directory");
		}
		
		parser = new NCBIBLASTHitsParser();
			
		List<BLASTHit> hits = new ArrayList<BLASTHit>();
		try {
			hits = parser.parse(blasthitsXmlFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail("Error during parsing: "+e.getMessage());
		}
		
		BLASTHit hit = hits.get(32);
		
		assertEquals("Pisaster brevispinus voucher BIOUG<CAN>:BAM00170 cytochrome oxidase subunit 1 (COI) gene, partial cds; mitochondrial", hit.getDescription());
		
	}
	
	

}
