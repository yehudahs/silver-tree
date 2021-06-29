package com.silver_tree.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CmdProc {
	public String stdOut = "";
	public String errOut = "";
	public int returnCode;
	
	public CmdProc(String[] cmd) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder(cmd);
		Process process = builder.start();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String s = null;
	    while ((s = stdInput.readLine()) != null)
	        stdOut += s;
	    
	    s = null;
	    while ((s = stdError.readLine()) != null)
	    	errOut += s;
	    
		returnCode = process.waitFor();
	}
}
