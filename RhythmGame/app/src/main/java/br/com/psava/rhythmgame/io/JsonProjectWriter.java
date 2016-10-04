package br.com.psava.rhythmgame.io;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by patricksava on 04/10/16.
 */

public class JsonProjectWriter {
    private static final String TAG = "JsonProjectWriter";

    public static boolean writeJsonFile(String filename, JSONObject obj, Context c) {
        if(!checkEnvironment(c)){
            Log.e(TAG, "Can't write on SD");
            return false;
        }

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal
        File root = android.os.Environment.getExternalStorageDirectory();

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder
        File dir = new File (root.getAbsolutePath() + "/rhythmgame/projects");
        dir.mkdirs();
        File file = new File(dir, filename + ".json");

        try {
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.print(obj.toString());
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "******* File not found.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean checkEnvironment(Context c){
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            return false;
        } else {
            // Can't read or write
            return false;
        }
    }
}
