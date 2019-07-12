package com.company;

public class Artist {
    public int artistID;
    public String artist;
    public String genre;

    Artist(){

    }

    public void getHeader(String header){
        String outHead;
        //if(header.equals("id"))
    }

    @Override
    public String toString() {
        String outStr = "| ArtistID: " + artistID + " | Artist: " + artist + " | " + " | Genre: " + genre + "";
        return outStr;
    }
}