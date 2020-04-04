package ser321.assign2.lindquis;

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
 * @version March 2020
 */

public class Episode{

	private String Title;
	private int Episode;
	private double ImdbRating;

	public Episode(){
	}

	public Episode(String Title, int Episode, double ImdbRating){
		this.Title = Title;
		this.Episode = Episode;
		this.ImdbRating = ImdbRating;
	}

	public String getTitle(){
		return this.Title;
	}
	public int getEpisode(){
		return this.Episode;
	}
	public double getImdbRating(){
		return this.ImdbRating;
	}
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

