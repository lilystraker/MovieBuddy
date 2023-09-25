package com.example.moviebuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class MovieDatabase {
// Define 3 tables:
//        Movies
//        Cinema
//        MoviesShowing
    public static final String DB_NAME = "movieBuddy";
    public static final String DB_TABLE = "movies";
    public static final int DB_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (MovieID STRING PRIMARY KEY, Title STRING, Directors STRING, CastMembers STRING, ReleaseDate STRING, Poster STRING)";
    private SQLHelper helper;

    public static final String DB_TABLE2 = "cinema";
    private static final String CREATE_TABLE2 = "CREATE TABLE " + DB_TABLE2 + " (CinemaID STRING PRIMARY KEY, Name STRING, Location STRING)";

    public static final String DB_TABLE3 = "showing";

    private static final String CREATE_TABLE3 = "CREATE TABLE " + DB_TABLE3 + " (MovieShowingID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "CinemaID String," +
            "MovieID String," +
            "FOREIGN KEY (CinemaID) REFERENCES cinema(CinemaID)," +
            "FOREIGN KEY (MovieID) REFERENCES movies(MovieID))";


    private SQLiteDatabase db;
    private Context context;

    public MovieDatabase(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
//        Open database in write mode
        this.db = helper.getWritableDatabase();
    }

    public void open() {
        this.db = helper.getWritableDatabase();
    }

//    Open database only for read operations
    public MovieDatabase openReadable() throws android.database.SQLException {
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }

//    Close database
    public void close() {
        db.close();
        helper.close();
    }

//    Add movie row
    public boolean addRow(String i, String t, String di, String c, String date, String p) {
        synchronized(this.db) {
            ContentValues newMovie = new ContentValues();
            newMovie.put("MovieID", i);
            newMovie.put("Title", t);
            newMovie.put("Directors", di);
            newMovie.put("CastMembers", c);
            newMovie.put("ReleaseDate", date);
            newMovie.put("Poster", p);

            try {
                open(); // Open the database before performing operations
//                Insert new values into database
                db.insertOrThrow(DB_TABLE, null, newMovie);
                return true;
            } catch (Exception e) {
//                Check for error
                Log.e("Error in inserting rows", e.toString());
                e.printStackTrace();
                return false;
            } finally {
//                Close database
                close();
            }
        }
    }

//    Get movie rows to display
    public ArrayList<String> retrieveRows() {
//        Open DB
        open();
        ArrayList<String> movieRows = new ArrayList<>();
        String[] columns = new String[] {"MovieID", "Title", "Directors", "CastMembers", "ReleaseDate", "Poster"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
//        Move to first row
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
//           Get each column
            movieRows.add(cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2)+ ", " + cursor.getString(3) + ", " + cursor.getString(4));
//              Move to next row
            cursor.moveToNext();
        }
//       When reaching end of database
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
//      Close DB
        db.close();
        return movieRows;
    }

//    Retrieve select data from movies table
//    Used to select movies showing at each cinema
    public ArrayList<String> retrieveMovieDate() { //query the database and return records as a text
        open();
        ArrayList<String> movieRows = new ArrayList<>();
        String[] columns = new String[] {"MovieID", "Title", "ReleaseDate"};
//        Select title and date from movie table
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            movieRows.add(cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return movieRows;
    }

//    Clear all records
//    This is unnecessary as the same action can be done by selecting all records and clicking delete
    public void clearRecords()
    {
        db = helper.getWritableDatabase();
        db.delete(DB_TABLE, null, null);
        db.close();
    }

//    Deleting selected movies
    public void deleteMovies(String mid) {
        open();
        String deleteQuery = "DELETE FROM " + DB_TABLE + " WHERE MovieID = ?";

        String[] whereArgs = new String[]{mid};

        db.execSQL(deleteQuery, whereArgs);
        db.close();
    }

//    Get movie data as an array of strings
//    Mostly used to retrieve movieID
    public String[] getMovieDetails(String[] movieDetails) {
        openReadable();
        String movieID = "";
        String title = "";
        String directors = "";
        String castMembers = "";
        String releaseDate = "";
        String poster = "";
        String query = "SELECT * FROM " + DB_TABLE + " WHERE MovieID = ?";

        String[] whereArgs = new String[]{movieDetails[0]};

        Cursor cursor = db.rawQuery(query, whereArgs);

//      ensures there are results available
        if (cursor.moveToFirst()) {
            movieID = cursor.getString(cursor.getColumnIndexOrThrow("MovieID"));
            title = cursor.getString(cursor.getColumnIndexOrThrow("Title"));
            directors = cursor.getString(cursor.getColumnIndexOrThrow("Directors"));
            castMembers = cursor.getString(cursor.getColumnIndexOrThrow("CastMembers"));
            releaseDate = cursor.getString(cursor.getColumnIndexOrThrow("ReleaseDate"));
            poster = cursor.getString(cursor.getColumnIndexOrThrow("Poster"));
        }
        cursor.close();
        String[] movieResult = {movieID, title, directors, castMembers, releaseDate, poster};

        db.close();
        return movieResult;

    }

//    Get cinema details
//    Mostly used to get cinemaID
    public String[] getCinemaDetails(String[] cinemaDetails) {
//        open DB in read mode
        openReadable();
        String cinemaID = "";
        String name = "";
        String location = "";
//        select cinema from DB
        String query = "SELECT * FROM " + DB_TABLE2 + " WHERE CinemaID = ?";

        String[] whereArgs = new String[]{cinemaID};

        Cursor cursor = db.rawQuery(query, whereArgs);

//       ensures there are results available
        if (cursor.moveToFirst()) {
            cinemaID = cursor.getString(cursor.getColumnIndexOrThrow("CinemaID"));
            name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            location = cursor.getString(cursor.getColumnIndexOrThrow("Location"));

        }
        cursor.close();
        String[] cinemaResult = {cinemaID, name, location};
        db.close();
        return cinemaResult;

    }

//    Update movies in database
    public void updateMovie(String id, String t, String di, String c, String da, String p){
        open();
        String[] values = {t, di, c, da, p, id};

        String updateSQL = "UPDATE " + DB_TABLE +
                " SET Title = ?, Directors = ?, CastMembers = ?, ReleaseDate = ?, Poster = ?" +
                "WHERE MovieID = ?";
        db.execSQL(updateSQL, values);
        db.close();
    }

//    Update cinema
    public void updateCinema(String cid, String n, String l, ArrayList<String> mid){
        open();
        String[] values = {n, l, cid};

        String updateSQL = "UPDATE " + DB_TABLE2 +
                " SET Name = ?, Location = ? " +
                "WHERE CinemaID = ?";
        db.execSQL(updateSQL, values);

        String[] deleteArgs = {cid};
        String deleteShowingSQL = "DELETE FROM " + DB_TABLE3 + " WHERE CinemaID = ?";
        db.execSQL(deleteShowingSQL, deleteArgs);

//        Update each movie showing at this cinema
        for (int i = 0; i < mid.size(); i++) {
            addShowing(mid.get(i), cid);
        }
        db.close();
    }

// Add a new cinema to database
    public boolean addCinemaRow(String i, String n, String l) {
        synchronized(this.db) {
            open();
            ContentValues newCinema = new ContentValues();

            newCinema.put("CinemaID", i);
            newCinema.put("Name", n);
            newCinema.put("Location", l);

            try {
//                Add values
                db.insertOrThrow(DB_TABLE2, null, newCinema);
            } catch (Exception e) {
                Log.e("Error in inserting cinema to database", e.toString());
                e.printStackTrace();
                return false;
            }
            db.close();
            return true;
        }
    }

//    Get cinema
    public ArrayList<String> retrieveCinema() {
        openReadable();
        ArrayList<String> cinemaRows = new ArrayList<>();
        String[] columns = new String[] {"CinemaID", "Name", "Location"};
        Cursor cursor = db.query(DB_TABLE2, columns, null, null, null, null, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            cinemaRows.add(cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
            String cinemaID = cursor.getString(0);

//            Create a second query to join all movies showing at a select cinema
            String selectQuery = "SELECT m.MovieID, m.Title, m.Directors, m.CastMembers, m.ReleaseDate, m.Poster " +
                    "FROM movies m " +
                    "INNER JOIN showing mc ON m.MovieID = mc.MovieID " +
                    "WHERE mc.CinemaID = ?";

            Cursor cursor2 = db.rawQuery(selectQuery, new String[] {cinemaID});

            cursor2.moveToFirst();
            while (cursor2.isAfterLast() == false) {
//                get the most recently added row
                String currentRow = cinemaRows.get(cinemaRows.size() - 1);
//                a string made of the movie title and release date
                String movieString = cursor2.getString(1) + ", " + cursor2.getString(4);
                String updatedRow = currentRow + "\n" + movieString;
//                replace the current row to include the movie as well
                cinemaRows.set(cinemaRows.size() - 1, updatedRow);
                cursor2.moveToNext();
            }
            if (cursor2 != null && !cursor2.isClosed()) {
                cursor2.close();
            }
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return cinemaRows;
    }
//    Only retrieve cinema rows themself (no movie showing data)
    public ArrayList<String> onlyCinema() {
        open();
        ArrayList<String> cinemaRows = new ArrayList<>();
        String[] columns = new String[] {"CinemaID", "Name", "Location"};
        Cursor cursor = db.query(DB_TABLE2, columns, null, null, null, null, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            cinemaRows.add(cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
            String cinemaID = cursor.getString(0);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return cinemaRows;
    }

// Delete cinema row from database table
    public void deleteCinema(String cid) {
        String deleteQuery = "DELETE FROM " + DB_TABLE2 + " WHERE CinemaID = ?";
        open();
        String[] whereArgs = new String[]{cid};

        db.execSQL(deleteQuery, whereArgs);

        String deleteQuery2 = "DELETE FROM " + DB_TABLE3 + " WHERE CinemaID = ?";
        db.execSQL(deleteQuery2, whereArgs);
        db.close();

    }

//    Add a new row to movieShowing table
    public boolean addShowing(String mid, String cid) {
        synchronized (this.db) {
            open();
            ContentValues newCinema = new ContentValues();

//          A movieCinemaID will be automatically generated
            newCinema.put("MovieID", mid);
            newCinema.put("CinemaID", cid);

            try {
                db.insertOrThrow(DB_TABLE3, null, newCinema);
            } catch (Exception e) {
                Log.e("Error in inserting showing movies showing at cinema to database", e.toString());
                e.printStackTrace();
                return false;
            }
            db.close();
            return true;
        }
    }

    public class SQLHelper extends SQLiteOpenHelper {
//        Create database
        public SQLHelper (Context c) {
            super(c, DB_NAME, null, DB_VERSION);
        }

//        Create tables
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_TABLE2);
            db.execSQL(CREATE_TABLE3);
        }

//        Check for any upgrades
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Movie table", "Upgrading database");
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

            Log.w("Cinema table", "Upgrading database");
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE2);

            Log.w("MovieShowing table", "Upgrading database");
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE3);
            onCreate(db);
        }
    }
}

