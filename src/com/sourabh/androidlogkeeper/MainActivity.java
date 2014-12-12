package com.sourabh.androidlogkeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        File mFile = new File(Environment.getExternalStorageDirectory()+"/log.txt");
        if(!mFile.exists()){
        	try {
				mFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        if(checkFileSize(mFile)){
        	mFile.delete();
        	try {
				mFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        System.out.println("The log is printed");
        
        extractLogToFile(mFile);
        
    }

    
    public File extractLogToFile(File file){
        //set a file
        Date datum = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        //write log to file
        int pid = android.os.Process.myPid();
        try {
            String command = String.format("logcat -d -v threadtime *:*");        
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String currentLine = null;

            while ((currentLine = reader.readLine()) != null) {
                   if (currentLine != null && currentLine.contains(String.valueOf(pid))) {
                       result.append(currentLine);
                       result.append("\n");
                    }
            }

            /*FileWriter out = new FileWriter(file);
            out.write(result.toString());
            out.close();*/
            FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter myOutWriter = 
									new OutputStreamWriter(fOut);
			myOutWriter.append(result.toString());
			myOutWriter.close();
			fOut.close();

            //Runtime.getRuntime().exec("logcat -d -v time -f "+file.getAbsolutePath());
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }


        //clear the log
        try {
            Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        return file;
    }
    
    /**
     * Checks the file size to write the log
     * @param file
     * @return false if file size is greater than 2MB
     */
    private static boolean checkFileSize(File file)
    {
    	boolean value=false;
    	//Checking if the file size is greater than 2MB
    	if(file.length()> 2e+6)
    	{
    		value=true;
    	}
		return value;
    	
    }
    
}
