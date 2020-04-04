package ser321.assign3.ghli1;import java.rmi.*;
import java.util.Hashtable;

import ser321.assign2.lindquis.SeriesSeason;

import java.io.*;


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
 * Purpose: Implementation of Series server using RMI api.
 *
 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Gene H. Li ghli1@asu.edu
 *	   Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2020
 */


class SeriesServerImpl extends UnicastRemoteObject implements SeriesServer{

    protected Hashtable<String, SeriesSeason> seriesSeasons;

    public SeriesServerImpl() throws RemoteException{
        try{
            File inFile = new File("seriesTest.json");

            if (inFile.exists()){
                ObjectInputStream i = new ObjectInputStream(new FileInputStream(inFile));
                seriesSeasons = (Hashtable)i.readObject();
            }
            else{
                seriesSeasons = new Hashtable<>();
                seriesSeasons.put("none", new SeriesSeason());
            }
        }
        catch (Exception e){
            System.out.println("Exception thrown initializing seriesseasons"+e.getMessage());
        }
    }

    public SeriesSeason getSeriesSeason(String title){
        return this.seriesSeasons.get(title);
    }

    public boolean addSeriesSeason(SeriesSeason toAdd){
        boolean ret = false;
        try {
            seriesSeasons.put(toAdd.getTitle()+" - Season "+toAdd.getSeason(), toAdd);
            ret = true;
        }
        catch (Exception e){
            System.out.println("Error placing object");
        }
        return ret;
    }
}