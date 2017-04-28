/*
	generic Utility class/functions
	currently only contais code for writing errors to log files
	
 */

package com.amazonaws.samples;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Utilities {
	
	public static void errorToLogFile(String error){
		try{
			Writer writer;
			writer = new BufferedWriter(new FileWriter("errorLog.log", true));
			writer.append(error + "\n");
			writer.close();
			return;
		} catch (IOException e){
			e.printStackTrace();			
			return;
		}
	}
	public static void asxErrorToLogFile(String file, String error){
		try{
			Writer writer;
			writer = new BufferedWriter(new FileWriter("asxErrorLog.log", true));
			writer.append(file + ": " + error + "\n");
			writer.close();
			return;
		} catch (IOException e){
			e.printStackTrace();			
			return;
		}
	}
}
