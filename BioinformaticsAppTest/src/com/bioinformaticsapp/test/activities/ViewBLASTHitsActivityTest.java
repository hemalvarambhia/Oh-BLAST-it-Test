package com.bioinformaticsapp.test.activities;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.bioinformaticsapp.ViewBLASTHitsActivity;
import com.bioinformaticsapp.ViewTaxonomyActivity;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.jayway.android.robotium.solo.Solo;

public class ViewBLASTHitsActivityTest extends
		ActivityInstrumentationTestCase2<ViewBLASTHitsActivity> {

	private static final String TAG = "ViewBLASTHitsActivityTest";
	private static final int DIALOG_TIMEOUT = 15000;
	private Solo solo;
	
	private String exampleNCBIJobIdentifier = "YAMJ8623016";
	private String exampleEMBLJobIdentifier = "ncbiblast-R20120418-133731-0240-81389354-pg";
	
	public ViewBLASTHitsActivityTest(){
		super(ViewBLASTHitsActivity.class);
	}

	public void setUp() throws Exception {
		
		super.setUp();
		
		getInstrumentation().getTargetContext().deleteFile(exampleEMBLJobIdentifier+".xml");
		getInstrumentation().getTargetContext().deleteFile(exampleEMBLJobIdentifier+".xml");
		
		
		copyBLASTHitsFileToAppDataDir(exampleNCBIJobIdentifier+".xml");
		
		copyBLASTHitsFileToAppDataDir(exampleEMBLJobIdentifier+".xml");
		
		copyBLASTHitsFileToAppDataDir("NOH86ITs.xml");
	}
	
	public void tearDown() throws Exception {
		
		solo.finishOpenedActivities();
		
	}
	
	private void copyBLASTHitsFileToAppDataDir(String fileName) throws IOException{
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
	
	public void testWeCanViewNCBIBLASTHits() throws IOException{
		
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		query.setJobIdentifier(exampleNCBIJobIdentifier);
		Intent intent = new Intent();
		intent.putExtra("query", query);
		setActivityIntent(intent);
		
		solo = new Solo(getInstrumentation(), getActivity());
		solo.waitForView(ListView.class);
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		
		assertTrue("List View containing any items", !listViews.isEmpty());
		
		for(ListView listView: listViews){
			assertEquals("Number of items in list view", 100, listView.getAdapter().getCount());
		}
		
	} 

	public void testWeCanViewEMBLBLASTHits() throws IOException{
		
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setJobIdentifier(exampleEMBLJobIdentifier);
		Intent intent = new Intent();
		intent.putExtra("query", query);
		setActivityIntent(intent);
		
		solo = new Solo(getInstrumentation(), getActivity());
		solo.waitForView(ListView.class);
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		
		assertTrue("List View containing any items", !listViews.isEmpty());
		
		for(ListView listView: listViews){
			assertEquals("Number of items in list view", 144, listView.getAdapter().getCount());
		}
		
	}
	
	public void testAlertDialogIsShownIsHitsCouldNotBeLoaded(){
		
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.NCBI);
		query.setJobIdentifier("NOH86ITs");
		Intent intent = new Intent();
		intent.putExtra("query", query);
		setActivityIntent(intent);
		
		
		solo = new Solo(getInstrumentation(), getActivity());
		solo.waitForView(TextView.class);
		String noBLASTHitsMessage = getInstrumentation().getTargetContext().getResources().getString(com.bioinformaticsapp.R.string.no_hits_message);
		boolean noBlastHitsMessageRendered = solo.waitForText(noBLASTHitsMessage);
		
		assertTrue(noBlastHitsMessageRendered);
		
	}
	//TODO
	public void testThatTappingBLASTHitShowsUsTheTaxonomyInformation(){
		BLASTQuery query = new BLASTQuery("blastn", BLASTVendor.EMBL_EBI);
		query.setJobIdentifier(exampleEMBLJobIdentifier);
		Intent intent = new Intent();
		intent.putExtra("query", query);
		setActivityIntent(intent);
		
		solo = new Solo(getInstrumentation(), getActivity());
		solo.waitForView(ListView.class);
		
		solo.clickInList(2);
		
		solo.waitForDialogToClose(DIALOG_TIMEOUT);
		
		solo.assertCurrentActivity("Expected to see the BLAST hits taxonomy information", ViewTaxonomyActivity.class);
		
	}
	
}
