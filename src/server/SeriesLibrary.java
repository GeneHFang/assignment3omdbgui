
 package ser321.assign3.ghli1;
import java.rmi.*;
import java.util.ArrayList;
import org.json.JSONObject;

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
 * Purpose: SeriesLibrary defines the interface for series library operations.
 *
 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Gene H. Li ghli1@asu.edu
 *	   Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2020
 */

public interface SeriesLibrary extends Remote{
	//Returns a string arraylist in format of "SHOW TITLE - SHOW SEASON", which acts as the keys for the aLib hash
	public ArrayList<String> getSeriesSeason() throws RemoteException;

	//returns the SeriesSeason object that corresponds to the key provided
	public SeriesSeason getSeriesSeason(String title) throws RemoteException;

	//Adds a SeriesSeason object to the aLib hash, using the "SHOW TITLE - SHOW SEASON" as key
	public boolean addSeriesSeason(SeriesSeason seriesSeason) throws RemoteException;

	//Removes a SeriesSeason object that corresponds to the key provided	
	public boolean removeSeriesSeason(String title) throws RemoteException;
	
	//Save and restore library from JSON file in server directory
	public boolean saveLibraryToFile() throws RemoteException;
	public boolean restoreLibraryFromFile() throws RemoteException;
	
}
