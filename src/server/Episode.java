
 package ser321.assign3.ghli1;
/**
 * Copyright 2020 Gene Li,
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
 * Purpose: Episode is the Java Object that stores the episode specific info from JSON fetched from omdb .

 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Gene H. Li ghli1@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2020
 */

public class Episode implements java.io.Serializable{

	private String Title;
	private int Episode;
	private double ImdbRating;

	//Empty constructor for populating fields using instantiated object w setters
	public Episode(){
	}

	//Constructor for creating Episode object with properties outright.
	public Episode(String Title, int Episode, double ImdbRating){
		this.Title = Title;
		this.Episode = Episode;
		this.ImdbRating = ImdbRating;
	}

	//Getters
	public String getTitle(){
		return this.Title;
	}
	public int getEpisode(){
		return this.Episode;
	}
	public double getImdbRating(){
		return this.ImdbRating;
	}

	//Setters
	public void setTitle(String Title){
		this.Title = Title;
	}
	public void setEpisode(int Episode){
		this.Episode = Episode;
	}
	public void setImdbRating(double ImdbRating){
		this.ImdbRating = ImdbRating;
	}

}

