/**
 * This module acts as the View layer for your application.
 * The 'MediaLibraryGui' class actually builds the Gui with all
 * the components - buttons, text fields, text areas, panels etc.
 * This class should be used to write the logic to add functionality
 * to the Gui components.
 * You are free add more files and further modularize this class's
 * functionality.
 */
 package ser321.assign3.ghli1;
 
import javax.swing.*;
import java.io.*;
import java.nio.file.Paths;
import java.rmi.*;
import java.nio.charset.Charset;
import javax.sound.sampled.*;
import java.beans.*;
import java.net.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.lang.Runtime;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URLConnection;
import java.time.Duration;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;

/**
 * Copyright 2020 Tim Lindquist, Gene H. Li
 *
 * This software is the intellectual property of the author, and can not be 
 * distributed, used, copied, or reproduced, in whole or in part, for any purpose, commercial or otherwise.
 * The author grants the ASU Software Engineering program the right to copy, execute, and evaluate this
 * work for the purpose of determining performance of the author in coursework,
 * and for Software Engineering program evaluation, so long as this copyright and
 * right-to-use statement is kept in-tact in such use.
 * All other uses are prohibited and reserved to the author.
 * 
 * Purpose: Gene H Li assignment 2 solution
 *
 * @author Tim Lindquist (Tim.Linquist@asu.edu), Gene Li (ghli1@asu.edu)
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version January 2020
 */
public class MediaLibraryApp extends MediaLibraryGui implements
TreeWillExpandListener,
ActionListener,
TreeSelectionListener {

	private static final boolean debugOn = true;
    private static final String pre = "https://www.omdbapi.com/?apikey=";
	private static String urlOMBD;
	private String url;
	//private MediaLibrary library;re
	private SeriesLibrary slibrary, searchlibrary;
	private String omdbKey;
	private boolean searchFlag = false;

	public MediaLibraryApp(String author, String authorKey, String hostId, String regPort) {
		super(author);
		
		try {
			this.slibrary = (SeriesLibrary) Naming.lookup(
					"rmi://"+hostId+":"+regPort+"/SeriesLibrary");
	
					
			// sets the value of 'author' on the title window of the GUI.
			this.omdbKey = authorKey;
			urlOMBD = pre + authorKey + "&t=";
				
			
			// register this object as an action listener for menu item clicks. This will cause
			// my actionPerformed method to be called every time the user selects a menuitem.
			for(int i=0; i<userMenuItems.length; i++){
				for(int j=0; j<userMenuItems[i].length; j++){
					userMenuItems[i][j].addActionListener(this);
				}
			}
			// register this object as an action listener for the Search button. This will cause
			// my actionPerformed method to be called every time the user clicks the Search button
			searchJButt.addActionListener(this);
			try{
				//tree.addTreeWillExpandListener(this);  // add if you want to get called with expansion/contract
				tree.addTreeSelectionListener(this);
				rebuildTree();
			}catch (Exception ex){
				JOptionPane.showMessageDialog(this,"Handling "+
				" constructor exception: " + ex.getMessage());
			}
			try{
				/*
				* display an image just to show how the album or artist image can be displayed in the
				* app's window. setAlbumImage is implemented by MediaLibraryGui class. Call it with a
				* string url to a png file as obtained from an album search.
				*/	
				// TODO: set album image here
				setAlbumImage("");
				
			}catch(Exception ex){
				System.out.println("unable to open image");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		setVisible(true);
	}

	/**
	 * A method to facilitate printing debugging messages during development, but which can be
	 * turned off as desired.
     * @param message Is the message that should be printed.
     * @return void
	 */
	private void debug(String message) {
		if (debugOn)
			System.out.println("debug: "+message);
	}

	/**
	 * Create and initialize nodes in the JTree of the left pane.
	 * buildInitialTree is called by MediaLibraryGui to initialize the JTree.
	 * Classes that extend MediaLibraryGui should override this method to 
	 * perform initialization actions specific to the extended class.
	 * The default functionality is to set base as the label of root.
	 * In your solution, you will probably want to initialize by deserializing
	 * your library and displaying the categories and subcategories in the
	 * tree.
	 * @param root Is the root node of the tree to be initialized.
	 * @param base Is the string that is the root node of the tree.
	 */
	public void buildInitialTree(DefaultMutableTreeNode root, String base){
		//set up the context and base name
		try{
			root.setUserObject(base);
		}catch (Exception ex){
			JOptionPane.showMessageDialog(this,"exception initial tree:"+ex);
			ex.printStackTrace();
		}
	}

	/**
	 * TODO
	 * method to build the JTree of media shown in the left panel of the UI. The
	 * field tree is a JTree as defined and initialized by MediaLibraryGui class.
	 * It is defined to be protected so it can be accessed by extending classes.
	 * This version of the method uses the music library to get the names of
	 * tracks. Your solutions will need to replace this structure with one that
	 * you need for the series/season and Episode. These two classes should store your information. 
	 * Your library (so a changes - or newly implemented MediaLibraryImpl) will store 
	 * and provide access to Series/Seasons and Episodes.
	 * This method is provided to demonstrate one way to add nodes to a JTree based
	 * on an underlying storage structure.
	 * See also the methods clearTree, valueChanged defined in this class, and
	 * getSubLabelled which is defined in the GUI/view class.
	 **/
	public void rebuildTree(){ //rebuilds the main library
		rebuildTree(slibrary);		
	}
	public void rebuildTree(SeriesLibrary pLib){ //builds library only used for search
		tree.removeTreeSelectionListener(this);
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
			clearTree(root, model);
			try {
			ArrayList<String> titles = pLib.getSeriesSeason();
			
			for (int i = 0; i<titles.size(); i++){
				SeriesSeason sss = pLib.getSeriesSeason(titles.get(i));
				String sTitle = sss.getTitle();
				String sSTitle = sss.getSeriesAndSeason();
				debug(sTitle+" - - - - "+sSTitle);
				debug("Adding episode with title:"+sTitle);
				//create toAdd for every episode
				ArrayList<Episode> episodes = sss.getEpisodes();
				for (int j = 0; j < episodes.size(); j++) {
					String epT = episodes.get(j).getTitle();
					int epN	= episodes.get(j).getEpisode();
					DefaultMutableTreeNode toAdd = 
						new DefaultMutableTreeNode("Ep"+epN+" - "+epT);
					DefaultMutableTreeNode subNode = getSubLabelled(root, sSTitle);

					if(subNode!=null){ // if seriesSeason subnode already exists
						debug("seriesSeason exists: "+sSTitle);
						model.insertNodeInto(toAdd, subNode,
						model.getChildCount(subNode));
					}else{ // album node does not exist
						DefaultMutableTreeNode anAlbumNode =
							new DefaultMutableTreeNode(sSTitle);
						debug("no album, so add one with name: "+sSTitle);
						model.insertNodeInto(anAlbumNode, root,
						model.getChildCount(root));
						DefaultMutableTreeNode aSubCatNode = 
							new DefaultMutableTreeNode("aSubCat");
						debug("adding subcat labelled: "+"aSubCat");
						model.insertNodeInto(toAdd,anAlbumNode,
						model.getChildCount(anAlbumNode));
					}
				}					
		}
	}catch (Exception E) {E.printStackTrace();}
		// expand all the nodes in the JTree
		for(int r =0; r < tree.getRowCount(); r++){
			tree.expandRow(r);
		}
		tree.addTreeSelectionListener(this);


			
	}

    /**
     * Remove all nodes in the left pane tree view.
     *
     * @param root Is the root node of the tree.
     * @param model Is a model that uses TreeNodes.
     * @return void
     */
	private void clearTree(DefaultMutableTreeNode root, DefaultTreeModel model){
		try{
			DefaultMutableTreeNode next = null;
			int subs = model.getChildCount(root);
			for(int k=subs-1; k>=0; k--){
				next = (DefaultMutableTreeNode)model.getChild(root,k);
				debug("removing node labelled:"+(String)next.getUserObject());
				model.removeNodeFromParent(next);
			}
		}catch (Exception ex) {
			System.out.println("Exception while trying to clear tree:");
			ex.printStackTrace();
		}
	}

	public void treeWillCollapse(TreeExpansionEvent tee) {
		debug("In treeWillCollapse with path: "+tee.getPath());
		tree.setSelectionPath(tee.getPath());
	}

	public void treeWillExpand(TreeExpansionEvent tee) {
		debug("In treeWillExpand with path: "+tee.getPath());
	}

	// TODO: 
	// this will be called when you click on a node. 
	// It will update the node based on the information stored in the library
	// this will need to change since your library will be of course totally different
	// extremely simplified! E.g. make sure that you display sensible content when the root,
	// the My Series, the Series/Season, and Episode nodes are selected
	public void valueChanged(TreeSelectionEvent e) {
		try{
			tree.removeTreeSelectionListener(this);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tree.getLastSelectedPathComponent();
			if(node!=null){
				String nodeLabel = (String)node.getUserObject();
				debug("In valueChanged. Selected node labelled: "+ nodeLabel);
				// is this a terminal node?

				// All fields empty to start with
				seriesSeasonJTF.setText(""); 
				genreJTF.setText(""); 
				ratingJTF.setText(""); 
				episodeJTF.setText("");
				summaryJTA.setText("");


				DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot(); // get the root
                // First (and only) child of the root (username) node is 'My Series' node.
				DefaultMutableTreeNode mySeries = (DefaultMutableTreeNode)root.getChildAt(0); // mySeries node
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();

				// TODO when it is an episode change the episode to something and set the rating to the episode rating
				
				
				if(node.getChildCount()==0 &&
						(node != (DefaultMutableTreeNode)tree.getModel().getRoot())){
					String parentLabel = (String)parent.getUserObject();
					SeriesSeason sd = slibrary.getSeriesSeason(parentLabel);
						if (sd == null) { sd = 	
							searchlibrary.getSeriesSeason(parentLabel);
						} //Check if user is on Search view.
					
					//get episode number
					String epi = nodeLabel.split("-")[0].split("p")[1].split(" ")[0];
					int epNo = new Integer(epi);
					if (epNo < 1) { epNo = 1; } //Some episodes are listed as 0 
					Episode episo = sd.getEpisodes().get(epNo-1 >= parent.getChildCount() ? parent.getChildCount()-1 : epNo-1); //Some fetches don't have all episodes

					// TODO just setting some values so you see how it can be done, they do not fit the fields!
					episodeJTF.setText(episo.getTitle()); // name of the episode
					//ratingJTF.setText(md.rating); // change to rating of the episode 
					ratingJTF.setText(""+episo.getImdbRating()); 
					//genreJTF.setText("genre"); // change to genre of the series from library
					genreJTF.setText(sd.getGenre());
					//summaryJTA.setText("Just writing some summary text in here, hard coded should later on be from the library and the series of course."); // change to Plot of library for season
					summaryJTA.setText(sd.getPlotSummary());
					//seriesSeasonJTF.setText(parentLabel); // Change to season name
					seriesSeasonJTF.setText(sd.getSeriesAndSeason());
					
					setAlbumImage(sd.getImgURL());
					
				}
				else if (parent == root){ // should be the series/season
					SeriesSeason sd = slibrary.getSeriesSeason(nodeLabel);
					if (sd == null) { sd = 
						searchlibrary.getSeriesSeason(nodeLabel); 
					} //check if user is in search view
					seriesSeasonJTF.setText(nodeLabel); // season name
					genreJTF.setText(sd.getGenre()); // genre of the series from library
					ratingJTF.setText(""+sd.getRating()); // rating of the season get from library
					episodeJTF.setText("-"); // nothing in here since not an episode
					summaryJTA.setText(sd.getPlotSummary());
					setAlbumImage(sd.getImgURL());
				}
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
		tree.addTreeSelectionListener(this);
	}

	// TODO: this is where you will need to implement a bunch. So when some action is called the correct thing happens
	public void actionPerformed(ActionEvent e) {
		tree.removeTreeSelectionListener(this);
		if(e.getActionCommand().equals("Exit")) {
			System.exit(0);
		}else if(e.getActionCommand().equals("Save")) {
			boolean savRes = false;
			try {
				slibrary.saveLibraryToFile();
				savRes = true;
			}
			catch(Exception ess) {}
			System.out.println("Save "+((savRes)?"successful":"not implemented")); //TODO implement that current library is saved to JSON file
		}else if(e.getActionCommand().equals("Restore")) {
			//Couldn't get the same code to work inside SeriesLibraryImpl instance's restoreLibrary for some reason
			boolean resRes = false;
			/*try {
				 slibrary = new SeriesLibraryImpl();
				InputStream i = new FileInputStream(new File("seriesTest.json"));
				JSONObject series = new JSONObject(new JSONTokener(i));
				Iterator<String> keys = series.keys();
				while (keys.hasNext()){
					String nodeTitle = keys.next();
				//debug("KEY"+nodeTitle);
					JSONObject actual = series.optJSONObject(nodeTitle);
				//debug("OBJ?"+actual.toString());
						SeriesSeason sseason = new SeriesSeason();
//==================================
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
						//debug("VALssss episodes?");
					epNum = new Integer(jEp.get("Episode").toString());
					epRating = new Double(jEp.get("imdbRating").toString());
				
					Episode ep = new Episode(epTitle, epNum, epRating);
					eps.add(ep);
						//debug("VALssss episodesafter?");
					
				}			
			}
			slibrary.restoreLibraryFromFile(
			sseason.setTitle((String)actual.get("Title"));
			sseason.setGenre((String)actual.get("Genre"));
			sseason.setImgURL((String)actual.get("Poster"));
			sseason.setPlotSummary((String)actual.get("Plot"));
						//debug("VALssssSS?");
			sseason.setRating(new Double(actual.get("imdbRating").toString()));			
			
			sseason.setSeason(new Integer(actual.get("Season").toString()));
			sseason.setEpisodes(eps);
//===============
						//debug("VAL?"+sseason.getTitle());
						try {
							slibrary.addSeriesSeason(sseason);
						}
						catch (Exception er) {er.printStackTrace();}
					
				}
				//debug("00000000000000000000000"+slibrary.getSeriesSeason());
				resRes = true; 
			}
			catch (Exception dl) {
				debug("hello, something went wrong withrestore"); 
				dl.printStackTrace();
			} */
			try { slibrary.restoreLibraryFromFile(); }
			catch(Exception er) { er.printStackTrace(); }
			rebuildTree();
			System.out.println("Restore "+((resRes)?"successful":"not implemented")); // TODO: implement that tree is restored to library
		}else if(e.getActionCommand().equals("Series-SeasonAdd")) {
		 // TODO: implement that the whole season with all episodes currently in tree will be added to library 
			//All episodes from search Query is added		
			try{
			slibrary.addSeriesSeason(
			searchlibrary.getSeriesSeason(searchlibrary.getSeriesSeason().get(0))			
			);
			rebuildTree();
			}
			catch(Exception er) {er.printStackTrace();}
		}else if(e.getActionCommand().equals("Search")) { 
			// TODO: implement that the search result is used to create new series/season object
            /*
             * In the below API(s) the error response should be appropriately handled
             */

			// with all episodes only display this new series/season with the episodes in tree

			// Doing a fetch two times so that we only get the full series info (with poster, summary, rating) once
			
			// fetch series info
			String searchReqURL = urlOMBD+seriesSearchJTF.getText().replace(" ", "%20");
			String json = fetchURL(searchReqURL);
			
			
			
			// fetch season info
			String searchReqURL2 = urlOMBD+seriesSearchJTF.getText().replace(" ", "%20")+"&season="+seasonSearchJTF.getText();
			String jsonEpisodes = fetchURL(searchReqURL2);
			
			
			//Tries to construct JSON object from search result. If search fails rebuilds tree from saved library
			try{
				JSONObject seriesObj = new JSONObject(json);
				JSONObject seasonObj = new JSONObject(jsonEpisodes);
				JSONArray epObjs = seasonObj.getJSONArray("Episodes");
				
				
				String title, genre, imgURL, plotSummary;
				int season;
				double rating;
				ArrayList<Episode> eps = new ArrayList<>();
				if (epObjs != null) {
					for (int i = 0 ; i < epObjs.length(); i++){
						String epTitle;
						int epNum;
						double epRating;
						JSONObject jEp = epObjs.getJSONObject(i);
						epTitle = (String)jEp.get("Title");
						epNum = new Integer((String)jEp.get("Episode"));
						try{
							epRating = new Double((String)jEp.get("imdbRating"));
						}
						catch(Exception edd){epRating = 0.0;}		
					
						Episode ep = new Episode(epTitle, epNum, epRating);
						eps.add(ep);
						
					}			
				}
				
				title = (String)seriesObj.get("Title");
				genre = (String)seriesObj.get("Genre");
				imgURL = (String)seriesObj.get("Poster");
				plotSummary = (String)seriesObj.get("Plot");
				rating = new Double((String)seriesObj.get("imdbRating"));			
				
				season = new Integer((String)seasonObj.get("Season"));
				
				SeriesSeason ss = new SeriesSeason(title, season, rating, genre, imgURL, plotSummary, eps); //JSON based constructor wouldn't work for some reason, used this one instead
				
				//Builds temporary tree from search results. If failed, rebuilds tree from saved library
				try {
					searchlibrary = new SeriesLibraryImpl();
					searchlibrary.addSeriesSeason(ss);
					rebuildTree(searchlibrary);
				}
				catch (Exception er) { 
					er.printStackTrace();
					rebuildTree(); 
				}
			}
			catch(Exception E) {
				E.printStackTrace();
				rebuildTree();
			}
			


			/* TODO: implement here that this json will be used to create a Season object with the episodes included
			 * This should also then build the tree and display the info in the left side bar (so the new tree with its episodes)
			 * right hand should display the Series information
			 */

		}else if(e.getActionCommand().equals("Tree Refresh")) {
			rebuildTree();
		}else if(e.getActionCommand().equals("Series-SeasonRemove")) {
			//TODO: remove the season from library
			
			//Removes from main library the library that is currently the search result
			try{
				slibrary.removeSeriesSeason(
					searchlibrary.getSeriesSeason().get(0)
				);
			}
			catch(Exception removeE) { 
				System.out.println("This season of this series either does not exist in your main library or has already been removed. \nEnsure you are trying to remove the series that is the current search result"); 
			}
			rebuildTree();
		}
		tree.addTreeSelectionListener(this);
	}

	/**
	 *
	 * A method to do asynchronous url request printing the result to System.out
	 * @param aUrl the String indicating the query url for the OMDb api search
	 *
	 **/
	public void fetchAsyncURL(String aUrl){
		try{
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(aUrl))
					.timeout(Duration.ofMinutes(1))
					.build();
			client.sendAsync(request, BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenAccept(System.out::println)
			.join();
		}catch(Exception ex){
			System.out.println("Exception in fetchAsyncUrl request: "+ex.getMessage());
		}
	}

	/**
	 *
	 * a method to make a web request. Note that this method will block execution
	 * for up to 20 seconds while the request is being satisfied. Better to use a
	 * non-blocking request.
	 * @param aUrl the String indicating the query url for the OMDb api search
	 * @return the String result of the http request.
	 *
	 **/
	public String fetchURL(String aUrl) {
		StringBuilder sb = new StringBuilder();
		URLConnection conn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(aUrl);
			conn = url.openConnection();
			if (conn != null)
				conn.setReadTimeout(20 * 1000); // timeout in 20 seconds
			if (conn != null && conn.getInputStream() != null) {
				in = new InputStreamReader(conn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader br = new BufferedReader(in);
				if (br != null) {
					int ch;
					// read the next character until end of reader
					while ((ch = br.read()) != -1) {
						sb.append((char)ch);
					}
					br.close();
				}
			}
			in.close();
		} catch (Exception ex) {
			System.out.println("Exception in url request:"+ ex.getMessage());
		} 
		return sb.toString();
	}

	public static void main(String args[]) {
		String name = "first.last";
		String key = "use-your-last.ombd-key";
		String host = "localhost";
		String port = "1099";
		if (args.length >= 4){
			//System.out.println("java -cp classes:lib/json.lib ser321.assign2.lindquist."+
			//                   "MediaLibraryApp \"Lindquist Music Library\" lastFM-Key");
			host = args[2];
			port = args[3];
			name = args[0];
			key = args[1];
			
		}
		try{
			//System.out.println("calling constructor name "+name);
			MediaLibraryApp mla = new MediaLibraryApp(name,key, host, port);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
