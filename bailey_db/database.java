package com.company;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.ECField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;


public class database {

    private Connection conn;
    private Scanner reader;

    database() {
        try {
            reader = new Scanner(System.in);
            System.out.println("=== DB Connection SETUP ===");
            System.out.println("Name of DB");
            String dbName = reader.nextLine();
            System.out.println("DB UserName: ");
            String dbUser = reader.nextLine();
            System.out.println("DB PWD: ");
            String dbPwd = reader.nextLine();
            System.out.println("Attempting to Connect...");
            Class.forName("com.mysql.jdbc.Driver");
            String url = String.format("jdbc:mysql://localhost:3306/%s?useSSL=false&allowPublicKeyRetrieval=true", dbName);
            conn = DriverManager.getConnection(url, dbUser, dbPwd);
            System.out.println("Connected!!!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public void run() {

        mainMenu();
    }

    public void mainMenu() {
        this.reader  = new Scanner(System.in);
        System.out.println("===== Welcome to Song Database =====");
        System.out.println("====================================");
        System.out.println("1. Print the list of current songs");
        System.out.println("2. Add a song");
        System.out.println("3. Delete a song");
        System.out.println("4. Update a song");
        System.out.println("5. Search Song ->");
        System.out.println("6. Print Aggregates");
        System.out.println("7. Save as CSV to Current Folder");
        System.out.println("s. QuickSort");
        System.out.println("q. Exit\n");
        System.out.println("8. (First Time Init ONLY)                     Create Table");
        System.out.println("9. (First Time Init ONLY)(Create Table First) Populate Table");

        System.out.print("Enter the number corresponding to your option:");
        String n = reader.nextLine();

        if (n.equals("1")) {
            // Print all song
            getFullQuery();
            System.out.println("\n");
            mainMenu();
        } else if (n.equals("2")) {
            addSong();
            mainMenu();
        } else if (n.equals("3")) {
            delete();
            // TODO: Delete
            mainMenu();
        } else if (n.equals("4")) {
            // TODO: Update
            update();
            mainMenu();
        } else if (n.equals("5")) {
            songSearch();
            mainMenu();
        }
        else if (n.equals("6")) {
            aggregate();
            mainMenu();
        }
        else if (n.equals("7")) {
            writeData();
            mainMenu();
        }
        else if (n.equals("s")) {
            quickSort();
            mainMenu();
        }
        else if (n.equals("q")) {
            // Exit
            reader.close();
            System.out.println("Exiting System");
            return;
        } else if (n.equals("8")) {
            // Make New Table and initialize
            // createTable();
            createArtists();
            createSongs();
            mainMenu();
            // createTracks();
        } else if (n.equals("9")) {
            // Make New Table and initialize
            // populateTable();
            populateArtists();
            populateSongs();
            mainMenu();
            // populateTracks();
        } else {
            System.out.println("Error reading your input. Please enter a number");
            mainMenu();
        }

    }

    public void createTable() {
        System.out.println("CREATING TABLE...");
        String query =
                "CREATE TABLE Music " +
                        "(Artist VARCHAR(25), " +
                        "Title VARCHAR(25), " +
                        "Genre VARCHAR(25), " +
                        "SubGenre VARCHAR(25), " +
                        "SongKey VARCHAR(25), " +
                        "BPM INT, " +
                        "Duration INT" +
                        ");";
        //TODO: NEEDS ID
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.executeUpdate();
            System.out.println("\"Music\" TABLE CREATED");
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }


    public void createArtists() {
        System.out.println("CREATING TABLE...");
        String query =
                "CREATE TABLE Artists " +
                        "(ArtistID INT NOT NULL AUTO_INCREMENT," +
                        "Artist VARCHAR(25)," +
                        "Genre VARCHAR(25)," +
                        "PRIMARY KEY(ArtistID)" +
                        ");";
        //TODO: NEEDS ID
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.executeUpdate();
            System.out.println("\"Artists\" TABLE CREATED");
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }


    public void createSongs() {
        System.out.println("CREATING TABLE...");
        String query =
                "CREATE TABLE Songs " +
                        "(SongID INT NOT NULL AUTO_INCREMENT, " +
                        "Song VARCHAR(25), " +
                        "SubGenre VARCHAR(25), " +
                        "SongKey VARCHAR(25), " +
                        "BPM INT, " +
                        "Duration INT," +
                        "Comment VARCHAR(100), " +
                        "ArtistID INT, " +
                        "PRIMARY KEY(SongID)" +
                        ");";
        //TODO: NEEDS ID
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.executeUpdate();
            System.out.println("\"Songs\" TABLE CREATED");
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    // DEPRECIATED UNTIL FURTHER NOTICE
    public void createTracks() {
        System.out.println("CREATING TABLE...");
        String query =
                "CREATE TABLE Tracks " +
                        "(SongID INT, " +
                        "ArtistID INT" +
                        ");";
        //TODO: NEEDS ID
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.executeUpdate();
            System.out.println("\"Tracks\" TABLE CREATED");
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void aggregate(){
        try{
            String query = "SELECT AVG(BPM) AS avg_BPM from Songs";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("\nAVG BPM: ");
            while (rs.next()) {
                System.out.println(rs.getString("avg_BPM"));
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        try{
            String query = "SELECT AVG(Duration) AS avg_dur from Songs";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("\nAVG Duration: ");
            while (rs.next()) {
                System.out.println(rs.getString("avg_dur")+"\n");
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void update(){
        System.out.println("===== Update Song =====");
        System.out.println("Please Enter the ID of the song you would like to update:");
        int val;
        String query;
        try{
            val = reader.nextInt();
            if(!songIDExists(val)){
                System.out.println("ERROR: Song ID Does Not Exist");
                System.out.println("Enter a Valid ID");
                return;
            }
            System.out.println("SONG To Update:");
            getSongQuery("SELECT * FROM Songs WHERE SongID="+val+";");

            System.out.println("===== Quick Update =====");
            System.out.println("1. Update Song ArtistID");
            System.out.println("2. Update Song Name");
            System.out.println("3. Update Song Comment");
            System.out.println("q. Back ->");
            this.reader = new Scanner(System.in);
            String in = reader.nextLine();
            PreparedStatement preparedStatement;
            if(in.equals("1")){
                System.out.println("Please Enter the NEW ArtistID");
                try{
                    int artID = reader.nextInt();
                    query = "UPDATE Songs SET Songs.ArtistID="+artID+" WHERE Songs.SongID="+val+";";
                    preparedStatement = conn.prepareStatement(query);
                    preparedStatement.executeUpdate();

                }
                catch (Exception e){
                    System.out.println(e.getLocalizedMessage());
                }
            }
            else if(in.equals("2")){
                System.out.println("Please Enter the NEW Name");
                try{
                    String name = reader.nextLine();
                    query = "UPDATE Songs SET Songs.Song="+name+" WHERE Songs.SongID="+val+";";
                    preparedStatement = conn.prepareStatement(query);
                    preparedStatement.executeUpdate();
                }
                catch (Exception e){
                    System.out.println(e.getLocalizedMessage());
                }
            }
            else if(in.equals("3")){
                System.out.println("Please Enter the NEW Comment");
                try{
                    String comment = reader.nextLine();
                    query = "UPDATE Songs SET Songs.Comment="+comment+" WHERE Songs.SongID="+val+";";
                    preparedStatement = conn.prepareStatement(query);
                    preparedStatement.executeUpdate();
                }
                catch (Exception e){
                    System.out.println(e.getLocalizedMessage());
                }
            }
            else if(in.equals("q")){
                return;
            }
            else{
                System.out.println("=-=-= Invalid Option. Going Back -> =-=-=");
                return;
            }
        }
        catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void quickUpdate(){
        System.out.println("===== Quick ");
    }

    //Works w sterilization
    public void addSong() {
        System.out.println("Enter Song name: ");
        String song = reader.nextLine();
        System.out.println("Enter Subgenre: ");
        String subgenre = reader.nextLine();
        System.out.println("Enter Song key: ");
        String songkey = reader.nextLine();
        System.out.println("Enter Comment: ");
        String comment = reader.nextLine();
        try{
            System.out.println("Enter BPM: ");
            int bpm = reader.nextInt();
            System.out.println("Enter Duration: ");
            int duration = reader.nextInt();
            // SANITY CHECK FOR ARTIST ID
            System.out.println("Enter AristID: ");
            int artistID = reader.nextInt();
            if(!artistIDExists(artistID)){
                System.out.println("=-=-=-= THE ARTIST ID YOU ENTERED DOES NOT EXIST =-=-=-=");
                System.out.println("=-=-=-= PLEASE TRY A VALID ARTIST ID =-=-=-=");
                mainMenu();
                return;
            }

            String query = String.format("INSERT INTO Songs (Song, SubGenre, SongKey, BPM, Duration, comment, artistID) " +
                    "VALUES (\'%s\', \'%s\', \'%s\', \'%d\', \'%d\', \'%s\', \'%d\');", song, subgenre, songkey, bpm, duration, comment, artistID);
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.executeUpdate();
                System.out.println("\"TABLE: SONGS.\" -- ADDED");
            } catch (Exception e) {

                System.out.println("ERROR: With SQL");
                System.out.println(e.getLocalizedMessage());
            }
        }
        catch (Exception e){
            System.out.println("=-=-=-= ERROR: Enter a Valid Number =-=-=-=");
            System.out.println(e.getLocalizedMessage());
        }
    }

    //TODO: DELETE SONG
    public void deleteSong(){

    }

    public boolean artistIDExists(int checkID){
        String query = "SELECT * from Artists";
        ArrayList<Integer> ids = new ArrayList<Integer>();
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ids.add(Integer.parseInt(rs.getString("ArtistID")));
            }
        }
        catch(Exception e){
            System.out.println("VERIFY ARTIST ID EXITST : FAILED");
            System.out.println(e.getLocalizedMessage());
        }
        if(ids.contains(checkID)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean songIDExists(int checkID){
        String query = "SELECT * from Songs";
        ArrayList<Integer> ids = new ArrayList<Integer>();
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ids.add(Integer.parseInt(rs.getString("SongID")));
            }
        }
        catch(Exception e){
            System.out.println("VERIFY SONG ID EXITST : FAILED");
            System.out.println(e.getLocalizedMessage());
        }
        if(ids.contains(checkID)){
            return true;
        }
        else{
            return false;
        }
    }


    public void songSearch() {
        System.out.println("\n===== Song Search =====");
        System.out.println("=======================");
        System.out.println("1. Print ALL Songs & Artists");
        System.out.println("2. Artist ID");
        System.out.println("3. Artist Name");
        System.out.println("4. Song ID");
        System.out.println("5. Song Title");
        System.out.println("6. BPM Range");
        System.out.println("q. Back ->\n");

        System.out.println("Enter the number corresponding to your option:");
        String n = reader.nextLine(); // Scans the next token of the input as an int.

        String query;
        if (n.equals("1")) {  // Print All
            getFullQuery();
        }
        else if (n.equals("2")) {  // ID
            System.out.println("Enter an ARTIST'S ID (int): ");
            int val;
            try {
                val = reader.nextInt();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Please enter a valid number\n");
                songSearch();
                return;
            }
            query = "SELECT * FROM Songs " +
                    "WHERE artistID = "+val+";";
            getSongQuery(query);

        } else if (n.equals("3")) {  // Artist Name
            System.out.println("Enter an Artist's Name\n");
            String val = reader.next();
            query = "SELECT * FROM Songs " +
                    "WHERE artistID = (SELECT Artists.ArtistID FROM Artists WHERE Artists.Artist=\'" + val +"\');";
            getSongQuery(query);
        }
        else if (n.equals("4")) {  // ID
            System.out.println("Enter an SONG'S ID (int): ");
            int val;
            try {
                val = reader.nextInt();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Please enter a valid number\n");
                songSearch();
                return;
            }
            query = "SELECT * FROM Songs " +
                    "WHERE SongID = " + val + ";";
            getSongQuery(query);
        }
        else if (n.equals("5")) {  // Title
            System.out.println("Enter a Song Title\n");
            this.reader = new Scanner(System.in);
            String val = reader.nextLine();
            query = "SELECT * FROM Songs " +
                    "WHERE Song = \'"+val+"\';";
            getSongQuery(query);
        }
        else if (n.equals("6")) {  // Title
            bpmRange();
        }
        else if (n.equals("q")) {  // exit
            return;
        }
        else {
            System.out.println("Please Enter a correct menu option\n");
            songSearch();
        }
    }

    // Works
    public void bpmRange() {
        String query;
        String sMin;
        String sMax;
        int min;
        System.out.println("Enter minimum BPM");
        try {
            sMin = reader.nextLine();
            min = Integer.parseInt(sMin);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Please enter a valid number");
            bpmRange();
            return;
        }
        System.out.println("Enter maximum BPM: \n");
        int max;
        try {
            sMax = reader.nextLine();
            max = Integer.parseInt(sMax);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Please enter a valid number");
            bpmRange();
            return;
        }
        query = "SELECT Songs.SongID, Songs.Song, Songs.SubGenre, Songs.SongKey, Songs.BPM, Songs.duration, Songs.Comment, Songs.ArtistID FROM Songs " +
                "INNER JOIN Artists ON songs.artistID = artists.artistID WHERE BPM <=\'" + max + "\' AND BPM >= \'" + min + "\'";
        getSongQuery(query);
    }

    public void quickSort(){
        String query;
        System.out.println("Enter an SONG'S ID (int): ");
        int id;
        try {
            id = reader.nextInt();
            System.out.println("Deleting Song" + id);
            query = "SELECT BPM FROM Songs WHERE SongID=" + id + ";";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            int bpm=0;
            int bpmLow=0;
            int bpmHigh=0;
            while (rs.next()) {
                bpm = rs.getInt("BPM");
                bpmLow = bpm-10;
                bpmHigh = bpm+10;
            }
            query = "SELECT * FROM Songs WHERE Songs.SongID =" + id + " AND Songs.BPM >="+bpmLow+" AND Songs.BPM<="+bpmHigh+";";
            getSongQuery(query);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Please enter a valid number\n");
            return;
        }


    }

    // TODO: Test
    public void delete() {
        String query;
        String val;

        int id;
        System.out.println("Enter the ID of the song to delete: \n");
        try {
            id = reader.nextInt();
            System.out.println("Deleting Song" + id);
            query = "DELETE FROM Songs WHERE SongID = \'" + id + "\'";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Please enter a valid number\n");
            delete();
            return;
        }

        /*else if (val.equals("2")) {
            int id;
            System.out.println("Enter the ID of the artist to delete: \n");
            try {
                id = reader.nextInt();
                System.out.println("Deleting Song" + id);
                query = "DELETE FROM Artists WHERE artsitId = \'" + id + "\'";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.execute();

            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Please enter a valid number\n");
                delete();
                return;
            }
        }*/
    }
    

    public void getArtistQuery(String query) {

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                //Extract all values and print them
                Artist a = new Artist();
                a.artistID = rs.getInt("ArtistID");
                a.artist = rs.getString("Artist");
                a.genre = rs.getString("Genre");

                System.out.println(a.toString());
            }
        } catch (Exception e) {
            System.out.println("getQuery Failed");
            System.out.println(e.getMessage());
        }
    }


    public void getSongQuery(String query) {

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                //Extract all values and print them
                Song s = new Song();
                s.songID = rs.getInt("SongID");
                s.song = rs.getString("Song");
                s.subgenre = rs.getString("Subgenre");
                s.songKey = rs.getString("SongKey");
                s.bpm = rs.getInt("BPM");
                s.duration = rs.getString("Duration");
                s.comment = rs.getString("Comment");
                s.artistID = rs.getInt("ArtistID");

                System.out.println(s.toString());
            }
        } catch (Exception e) {
            System.out.println("Parsing Query Failed");
            System.out.println(e.getMessage());
        }
    }


    public void getFullQuery() {

        try {
            System.out.println("========= Artists =========");
            String query = "SELECT * from Artists";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Artist a = new Artist();
                a.artistID = rs.getInt("ArtistID");
                a.artist = rs.getString("Artist");
                a.genre = rs.getString("Genre");

                System.out.println(a.toString());
            }
            System.out.println("========= Songs =========");
            query = "SELECT * from Songs";
            preparedStatement = conn.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                //Extract all values and print them
                Song s = new Song();
                s.songID = rs.getInt("SongID");
                s.song = rs.getString("Song");
                s.subgenre = rs.getString("Subgenre");
                s.songKey = rs.getString("SongKey");
                s.bpm = rs.getInt("BPM");
                s.duration = rs.getString("Duration");
                s.comment = rs.getString("Comment");
                s.artistID = rs.getInt("ArtistID");

                System.out.println(s.toString());
            }
        } catch (Exception e) {
            System.out.println("getQuery Failed");
            System.out.println(e.getMessage());
        }
    }



    //TODO: UNTESTED
    public void writeData() {

        try {
            // Write CSV FILE
            String WRITE_PATH_CSV = "./artists.csv"; // PATH TO CSV FILE,   the "./" refers to current folder
            try (
                    BufferedWriter writer = Files.newBufferedWriter(Paths.get(WRITE_PATH_CSV)); // GIVES PATH TO WRITER
                    CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                            .withHeader("Artist", "Genre"));
            ) {
                // TODO: Query for values
                String query = "SELECT * from Artists";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    Artist a = new Artist();
                    String artist = rs.getString("Artist");
                    String genre = rs.getString("Genre");

                    csvPrinter.printRecord(artist, genre);
                }
                csvPrinter.flush(); // required csv command, only use at end
            } catch (Exception e) {
                System.err.println("CSV WRITER: ERROR");
                System.err.println(e.getMessage());
            }

            WRITE_PATH_CSV = "./songs.csv"; // PATH TO CSV FILE,   the "./" refers to current folder
            try (
                    BufferedWriter writer = Files.newBufferedWriter(Paths.get(WRITE_PATH_CSV)); // GIVES PATH TO WRITER
                    CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                            .withHeader("Song", "SubGenre", "SongKey", "BPM", "Duration", "Comment", "ArtistID"));
            ) {
                String query = "SELECT * from Songs";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    //Extract all values and print them
                    Song s = new Song();
                    //int songID = rs.getInt("SongID");
                    String song = rs.getString("Song");
                    String subgenre = rs.getString("Subgenre");
                    String songKey = rs.getString("SongKey");
                    int bpm = rs.getInt("BPM");
                    String duration = rs.getString("Duration");
                    String comment = rs.getString("Comment");
                    int artistID = rs.getInt("ArtistID");

                    csvPrinter.printRecord(song, subgenre, songKey, bpm, duration, comment, artistID);
                }
                csvPrinter.flush(); // required csv command, only use at end
            } catch (Exception e) {
                System.err.println("CSV WRITER: ERROR");
                System.err.println(e.getMessage());
            }
        }
        catch (Exception e) {
            System.err.println("writeData Failed");
            System.err.println(e.getMessage());
        }
    }


    public void populateArtists() {
        //TODO: Dynamic path file

        String csvPath = "./artists.csv";
        readArtistData(csvPath);
    }

    public void populateSongs() {
        //TODO: Dynamic path file

        String csvPath = "./songs.csv";
        readSongData(csvPath);
    }

    public void readData(String csvPath) {
        try {
            // Class.forName("com.mysql.jdbc.Driver");
            // Declare variables to use
            // Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cpsc408?useSSL=false&allowPublicKeyRetrieval=true", "ander412", "Onbay117");
            ResultSet rs;
            PreparedStatement preparedStatement;
            System.out.println("Getting CSV file from path: " + csvPath);

            boolean firstRowFlag = true;
            int count = 60; //Populate only the first 60 rows

            //final String SAMPLE_CSV_FILE_PATH = "./users.csv";  // PATH to file you would like to read from
            try (
                    //Reader reader = Files.newBufferedReader(Paths.get(csvPath)); // Give the path to the reader
                    //CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);;
                    Reader in = new FileReader(csvPath);
                    CSVParser csvParser = CSVFormat.RFC4180.withHeader("Artist", "Title", "Genre", "SubGenre", "SongKey", "BPM", "Duration").parse(in);
            ) {
                for (CSVRecord csvRecord : csvParser) {
                    //Skip first entry in CSV File
                    if(count == 0){
                        return;
                    }
                    if(firstRowFlag == false){
                        // Accessing Values by Column Index
                        //String id = csvRecord.get("ID");
                        String artist = csvRecord.get("Artist"); //
                        String title = csvRecord.get("Title"); //
                        String genre = csvRecord.get("Genre"); //
                        String subGenre = csvRecord.get("SubGenre"); //
                        String songKey = csvRecord.get("SongKey"); //
                        String bpm = csvRecord.get("BPM"); //
                        String duration = csvRecord.get("Duration"); //

                        String query = String.format("INSERT INTO Music (Artist, Title, Genre, SubGenre, SongKey, BPM, Duration) " +
                                "VALUES (\'%s\',\'%s\', \'%s\', \'%s\', \'%s\', %s, %s);", artist, title, genre, subGenre, songKey, bpm, duration);    // TODO: No ID in format8

                        preparedStatement = conn.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        System.out.println("TABLE POPULATED");
                    }
                    firstRowFlag = false;
                    count--;
                }
                csvParser.close();
            } catch (Exception e) {
                System.out.println("readData 1 Failed: ");
                System.out.println(e.getLocalizedMessage());
            }
        } catch (Exception e) {
            System.err.println("readData 2 Failed: ");
            System.err.println(e.getLocalizedMessage());
        }
    }

    // Prob Works
    public void readArtistData(String csvPath) {
        try {
            // Class.forName("com.mysql.jdbc.Driver");
            // Declare variables to use
            // Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cpsc408?useSSL=false&allowPublicKeyRetrieval=true", "ander412", "Onbay117");
            ResultSet rs;
            PreparedStatement preparedStatement;
            System.out.println("Getting CSV file from path: " + csvPath);

            boolean firstRowFlag = false;
            int count = 60; //Populate only the first 3 rows

            //final String SAMPLE_CSV_FILE_PATH = "./users.csv";  // PATH to file you would like to read from
            try (
                    //Reader reader = Files.newBufferedReader(Paths.get(csvPath)); // Give the path to the reader
                    //CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);;
                    Reader in = new FileReader(csvPath);
                    CSVParser csvParser = CSVFormat.RFC4180.withHeader("Artist", "Genre").parse(in);
            ) {
                for (CSVRecord csvRecord : csvParser) {
                    //Skip first entry in CSV File
                    if(count == 0){
                        return;
                    }
                    if(firstRowFlag == false){
                        // Accessing Values by Column Index
                        // String id = csvRecord.get("ID");
                        // String artistID = csvRecord.get("ArtistID"); //
                        String artist = csvRecord.get("Artist"); //
                        String genre = csvRecord.get("Genre"); //

                        String query = String.format("INSERT INTO Artists (Artist, Genre) " +
                                "VALUES (\'%s\', \'%s\');", artist, genre);

                        preparedStatement = conn.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        System.out.println("TABLE POPULATED");
                    }
                    firstRowFlag = false;
                    count--;
                }
                csvParser.close();
            } catch (Exception e) {
                System.out.println("readData 1 Failed: ");
                System.out.println(e.getLocalizedMessage());
            }
        } catch (Exception e) {
            System.err.println("readData 2 Failed: ");
            System.err.println(e.getLocalizedMessage());
        }
    }


    public void readSongData(String csvPath) {
        try {
            // Class.forName("com.mysql.jdbc.Driver");
            // Declare variables to use
            // Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cpsc408?useSSL=false&allowPublicKeyRetrieval=true", "ander412", "Onbay117");
            ResultSet rs;
            PreparedStatement preparedStatement;
            System.out.println("Getting CSV file from path: " + csvPath);

            boolean firstRowFlag = false;
            int count = 60; //Populate only the first 60 rows

            //final String SAMPLE_CSV_FILE_PATH = "./users.csv";  // PATH to file you would like to read from
            try (
                    //Reader reader = Files.newBufferedReader(Paths.get(csvPath)); // Give the path to the reader
                    //CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);;
                    Reader in = new FileReader(csvPath);
                    CSVParser csvParser = CSVFormat.RFC4180.withHeader("Song", "Subgenre", "SongKey", "BPM", "Duration", "Comment", "ArtistID").parse(in);
            ) {
                for (CSVRecord csvRecord : csvParser) {
                    //Skip first entry in CSV File
                    if(count == 0){
                        return;
                    }
                    if(firstRowFlag == false){
                        // Accessing Values by Column Index
                        //String id = csvRecord.get("ID");
                        // String songID = csvRecord.get("SongID");
                        String song = csvRecord.get("Song");
                        String subgenre = csvRecord.get("Subgenre");
                        String songKey = csvRecord.get("SongKey");
                        String bpm = csvRecord.get("BPM");
                        String duration = csvRecord.get("Duration");
                        String comment = csvRecord.get("Comment");
                        String artistID = csvRecord.get("ArtistID");

                        String query = String.format("INSERT INTO Songs (Song, Subgenre, SongKey, BPM, Duration, Comment, ArtistID) " +
                                "VALUES (\'%s\', \'%s\', \'%s\', \'%s\', \'%s\', \'%s\', \'%s\');", song, subgenre, songKey, bpm, duration, comment, artistID);    // TODO: No ID in format8

                        preparedStatement = conn.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        System.out.println("SONGS TABLE POPULATED");
                    }
                    firstRowFlag = false;
                    count--;
                }
                csvParser.close();
            } catch (Exception e) {
                System.out.println("readData 1 Failed: ");
                System.out.println(e.getLocalizedMessage());
            }
        } catch (Exception e) {
            System.err.println("readData 2 Failed: ");
            System.err.println(e.getLocalizedMessage());
        }
    }


    public void readTrackData(String csvPath) {
        try {
            // Class.forName("com.mysql.jdbc.Driver");
            // Declare variables to use
            // Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cpsc408?useSSL=false&allowPublicKeyRetrieval=true", "ander412", "Onbay117");
            ResultSet rs;
            PreparedStatement preparedStatement;
            System.out.println("Getting CSV file from path: " + csvPath);

            boolean firstRowFlag = false;
            int count = 60; //Populate only the first 3 rows

            //final String SAMPLE_CSV_FILE_PATH = "./users.csv";  // PATH to file you would like to read from
            try (
                    //Reader reader = Files.newBufferedReader(Paths.get(csvPath)); // Give the path to the reader
                    //CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);;
                    Reader in = new FileReader(csvPath);
                    CSVParser csvParser = CSVFormat.RFC4180.withHeader("SongID", "ArtistID").parse(in);
            ) {
                for (CSVRecord csvRecord : csvParser) {
                    //Skip first entry in CSV File
                    if(count == 0){
                        return;
                    }
                    if(firstRowFlag == false){
                        // Accessing Values by Column Index
                        //String id = csvRecord.get("ID");
                        String songID = csvRecord.get("SongID"); //
                        String artistID = csvRecord.get("ArtistID"); //

                        String query = String.format("INSERT INTO Tracks (SongID, ArtistID) " +
                                "VALUES (\'%s\',\'%s\');", songID, artistID);    // TODO: No ID in format8

                        preparedStatement = conn.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        System.out.println("TABLE POPULATED");
                    }
                    firstRowFlag = false;
                    count--;
                }
                csvParser.close();
            } catch (Exception e) {
                System.out.println("readData 1 Failed: ");
                System.out.println(e.getLocalizedMessage());
            }
        } catch (Exception e) {
            System.err.println("readData 2 Failed: ");
            System.err.println(e.getLocalizedMessage());
        }
    }


}

