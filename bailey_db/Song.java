package com.company;

public class Song {

    public int songID;
    public String song;
    public String subgenre;
    public String songKey;
    public int bpm;
    public String duration;
    public String comment;
    public int artistID;

    Song(){

    }

    public void getHeader(String header){
        String outHead;
        //if(header.equals("id"))
    }

    @Override
    public String toString() {
        String outStr = "| SongID: " + songID + " | Song: " + song + " | " + "Subgenre: "+subgenre+ " | SongKey: "+ songKey + " | BPM: " + bpm+" | Duration: " + duration + " | Comment: " + comment + " | AristID: "+artistID+ " |";
        return outStr;
    }
}
