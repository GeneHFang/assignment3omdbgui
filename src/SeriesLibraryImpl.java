package ser321.assign2.lindquis;

import java.io.*;
import java.util.*;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;


/**
 * Copyright 2020 Gene Li and Tim Lindquist,
 *
 * This software is the intellectual property of the author, and can not be 
 * distributed, used, copied, or reproduced, in whole or in part, for any purpose, commercial or otherwise.
 * The author grants the ASU Software Engineering program the right to copy, execute, and evaluate this
 * work for the purpose of determining performance of the author in coursework,
 * and for Software Engineering program evaluation, so long as this copyright and
 * right-to-use statement is kept in-tact in such use.
 * All other uses are prohibited and reserved to the author.
 * 
 * 
 * Purpose: SeriesLibraryImpl is the implementing class for library interface.
 *
 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Gene H. Li ghli1@asu.edu
 *	   Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version March 2020
 */



public class SeriesLibraryImpl extends Object implements SeriesLibrary{

	private Hashtable<String,SeriesSeason> aLib;
	private static final String fileName="seriesTest.json";

	public SeriesLibraryImpl() { //blank constructor, used to manually populate fields
		this.aLib = new Hashtable<String,SeriesSeason>();
	}
	public SeriesLibraryImpl(boolean init){ //constructor that is called first, populates user's current library seriesTest.json. If user has no JSON file saved, it will log to console informing user.
		this.aLib= new Hashtable<String,SeriesSeason>();
		try { InputStream i = new FileInputStream(new File(fileName));
				JSONObject series = new JSONObject(new JSONTokener(i));
				Iterator<String> keys = series.keys();
				while (keys.hasNext()){
					String nodeTitle = keys.next();
					System.out.println("KEY"+nodeTitle);
					JSONObject actual = series.optJSONObject(nodeTitle);
					
					System.out.println("SON"+actual.toString());
						SeriesSeason sseason = new SeriesSeason();
						JSONArray epObjs = actual.getJSONArray("Episodes");
			//Iterator<String> keys = obj.keys();
			

			ArrayList<Episode> eps = new ArrayList<>();
			if (epObjs != null) {
				for (int ik = 0 ; ik < epObjs.length(); ik++){
					String epTitle;
					int epNum;
					double epRating;
					JSONObject jEp = epObjs.getJSONObject(ik);
					epTitle = (String)jEp.get("Title");
					epNum = new Integer(jEp.get("Episode").toString());
					epRating = new Double(jEp.get("imdbRating").toString());
					System.out.println("attrs: "+epTitle+epNum+epRating);
					Episode ep = new Episode(epTitle, epNum, epRating);
					eps.add(ep);
					
				}			
			}
			sseason.setTitle((String)actual.get("Title"));
			sseason.setGenre((String)actual.get("Genre"));
			sseason.setImgURL((String)actual.get("Poster"));
			sseason.setPlotSummary((String)actual.get("Plot"));
			sseason.setRating(new Double(actual.get("imdbRating").toString()));			
			
			sseason.setSeason(new Integer(actual.get("Season").toString()));
			sseason.setEpisodes(eps);
			
			System.out.println("sseason: "+sseason.getTitle());
					this.aLib.put(nodeTitle, sseason);
					
			System.out.println(saveLibraryToFile().toString());
					
				}
			}
		catch(Exception e) { System.out.println("No library exists"); }
	}

	//Returns a string arraylist in format of "SHOW TITLE - SHOW SEASON", which acts as the keys for the aLib hash
	public ArrayList<String> getSeriesSeason(){
		ArrayList<String> retVal = new ArrayList<>();		
		Enumeration keys = this.aLib.keys();

		while (keys.hasMoreElements()){
			retVal.add((String)keys.nextElement());
		}
	
		return retVal;
	}
	//returns the SeriesSeason object that corresponds to the key provided
	public SeriesSeason getSeriesSeason(String title){
		return this.aLib.get(title);
	}
	
	//Adds a SeriesSeason object to the aLib hash, using the "SHOW TITLE - SHOW SEASON" as key
	public boolean addSeriesSeason(SeriesSeason seriesSeason){
		try{
			this.aLib.put(seriesSeason.getTitle()+" - Season "+seriesSeason.getSeason(), seriesSeason);
			
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	//Removes a SeriesSeason object that corresponds to the key provided
	public boolean removeSeriesSeason(String title){
		try{
			this.aLib.remove(title);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	//Helper method for getting Iterator of keyvalues
	private Iterator<String> getKeys(){
		return this.aLib.keySet().iterator();	
	}
	
	//unused method for resetting aLib object
	public void clears(){
		this.aLib = new Hashtable<String, SeriesSeason>();
	}
	
	//create JSON of Library
	public JSONObject saveLibraryToFile(){
		JSONObject obj = new JSONObject();
		Iterator<String> keys = getKeys();
		
		//iterate through titles
		while (keys.hasNext()){
			JSONObject subObj = new JSONObject();
			//title
			String key = keys.next();
			
			//for each key of the SeriesSeason object serialize properties
			subObj.put("Title", this.aLib.get(key).getTitle());
			subObj.put("Season", this.aLib.get(key).getSeason());
			subObj.put("imdbRating", this.aLib.get(key).getRating());
			subObj.put("Genre", this.aLib.get(key).getGenre());
			subObj.put("Poster", this.aLib.get(key).getImgURL());
			subObj.put("Plot", this.aLib.get(key).getPlotSummary());
			//JSONArray eps = new JSONArray(this.aLib.get(key).getEpisodes().toArray());
			ArrayList<Episode> epis = this.aLib.get(key).getEpisodes();
			JSONObject[] jEps = new JSONObject[epis.size()];
			for (int i = 0; i < epis.size(); i++){
				JSONObject s = new JSONObject();
				s.put("Title",epis.get(i).getTitle());
				s.put("imdbRating",epis.get(i).getImdbRating());
				s.put("Episode",epis.get(i).getEpisode());
				jEps[i] = s;
			}
			subObj.put("Episodes", jEps);
			obj.put(key, subObj);
		}

		return obj;

		
	}

	//Supposed to create a library from a JSON argument, but doesn't work for some reason. Implemented instead directly inside MediaLibraryApp.java
	public boolean restoreLibraryFromFile(JSONObject file){
		try{
		Iterator<String> keys = file.keys();
		String titleKey = "";
		int seasonKey = -1; 

		while (keys.hasNext()){
			SeriesSeason ss = new SeriesSeason();
			
			String key = keys.next();
		System.out.println("keys???" + key);
			
			Object obj = file.get(key);

			if (obj instanceof String) {
				if (key == "Title") { 
					titleKey = (String)obj;
					ss.setTitle((String)obj);	
				}	
				if (key == "Genre") { ss.setGenre((String)obj); }	
				if (key == "Poster") { ss.setImgURL((String)obj); }	
				if (key == "Plot") { ss.setPlotSummary((String)obj); }			
			}
			else if (obj instanceof Integer){ 
				int val = new Integer((String)obj);
				seasonKey = val;				
				ss.setSeason(val);
			}
			else if (obj instanceof Double){ 
				double val = new Double((String) obj);				
				ss.setRating(val);
			}
			else if (obj instanceof JSONArray){
				ArrayList<Episode> episodes = new ArrayList<>();
				JSONArray jArr = (JSONArray) obj;
				if (jArr != null) {
					for(int i = 0 ; i < jArr.length(); i++){
						Episode ep = new Episode();					
						JSONObject jEp = jArr.getJSONObject(i); 
						Iterator<String> epKeys = jEp.keys();
							while(epKeys.hasNext()){
								String epKey = epKeys.next();
								Object epObj = jEp.get(epKey);
								if(epObj instanceof String) { ep.setTitle((String)epObj); }
								else if (epObj instanceof Integer) {
									int val = (int) epObj;
									ep.setEpisode(val);
								}
								else if (epObj instanceof Double) {
									double val = (double) epObj;
									ep.setImdbRating(val);
								}
							}
						episodes.add(ep);
					}				
				}
				ss.setEpisodes(episodes);
			}
		System.out.println("Putting in "+titleKey+" - Season "+seasonKey);

		this.aLib.put(titleKey+" - Season "+seasonKey,ss);
		}
		return true;}
		catch (Exception e){ return false; }
	}




}

