package com.bioinformaticsapp.test.functional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import com.bioinformaticsapp.io.EMBLEBIBLASTHitsParser;
import com.bioinformaticsapp.models.BLASTHit;

public class TestEMBLEBIBLASTHitsParser extends InstrumentationTestCase {

	private EMBLEBIBLASTHitsParser parser;
	
	private static final String TAG = "TestBLASTHitsParser";
	private AssetManager assetManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		assetManager = this.getInstrumentation().getContext().getResources().getAssets();
		parser = new EMBLEBIBLASTHitsParser();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		parser = null;
		
	}

	public void testWeCannotParseXmlFileAWithNullReader(){
		boolean cannotParse = false;
		try{
			parser.parse(null);
			
		}catch(Exception e){
			cannotParse = true;
		}
		
		assertTrue(cannotParse);
	}
	
	public void testWeCanParseAnEMBLEBIBlastSearchXmlFile() {
		InputStream blastHitsXmlFile = null;
		try {
			blastHitsXmlFile = assetManager.open("ncbiblast-R20120418-133731-0240-81389354-pg.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("Could not find xml file in assets directory");
		}
		List<BLASTHit> hits = parser.parse(blastHitsXmlFile);
		
		assertTrue(hits.size() == 144);
	}
	
	public void testWeGetTheCorrectHitDescription(){
		InputStream blastHitsXmlFile = null;
		try {
			blastHitsXmlFile = assetManager.open("ncbiblast-R20120418-133731-0240-81389354-pg.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("Could not find xml file in assets directory");
		}
		
		List<BLASTHit> hits = parser.parse(blastHitsXmlFile);
		
		BLASTHit aHit = hits.get(8);
		
		assertEquals("Tringa solitaria voucher TLBS 195149287 cytochrome oxidase subunit 1 (COI) gene, partial cds; mitochondrial.", aHit.getDescription());
		
	}
	
	public void testWeGetTheCorrectHitAccessionNumber(){
		InputStream blastHitsXmlFile = null;
		try {
			blastHitsXmlFile = assetManager.open("ncbiblast-R20120418-133731-0240-81389354-pg.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("Could not find xml file in assets directory");
		}
		
		List<BLASTHit> hits = parser.parse(blastHitsXmlFile);
		
		BLASTHit aHit = hits.get(8);
		
		assertEquals("HM033842.1", aHit.getAccessionNumber());
		
		
		
	}
}
