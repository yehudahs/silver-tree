package com.silver_tree.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.silvertree.ideplugin.common.DeviceTree;
import com.silvertree.ideplugin.common.DeviceTreeRoot;
import com.silvertree.ideplugin.common.Token;

public class parser {

	public static void main(String[] args) {
		ArrayList<Integer> test_list = parse_args(args);
		File sampleFolder = new File("./samples");
		ArrayList<File> dtsFiles = listFilesForFolder(sampleFolder);
		int numOfSucc = 0, testNum = 0;
		for (File dtsFile : dtsFiles) {
			// run test if:
			// 1. the test_list is empty (just run all tests)
			// 2. the test_list contains this test (the user want to run this specific test).
			if (test_list.isEmpty() || test_list.contains(testNum)) {
				System.out.print("test: " + testNum + ": dtsFile: " + dtsFile + ": ");
				CmdProc proc = runTest(dtsFile);
				String result = "Fail\n";
				if (proc.returnCode == 0) {
					result = "Succ\n";
					numOfSucc++;
				}
				System.out.println(result);
			}
			testNum++;
		}

		System.out.println("succ: " + numOfSucc + " / " + (testNum + 1) + " (" + numOfSucc / dtsFiles.size() + "%)");
	}
	
	private static ArrayList<Integer> parse_args(String[] args) {
		ArrayList<Integer> test_list = new ArrayList<Integer>();
		for(int args_idx = 0 ; args_idx < args.length; args_idx++) {
			test_list.add(Integer.parseInt(args[args_idx]));
		}
		return test_list;
	}

	private static CmdProc runTest(File dtsFile) {
		try {
			String content = Files.readString(Paths.get(dtsFile.getPath()), StandardCharsets.UTF_8);
			Token tok = new Token(content, 0, content.length(), 0, Token.TokenType.TREE);
			DeviceTreeRoot root = new DeviceTreeRoot(tok);
			String parserDump = root.dump(0);
			Path inputFile = Files.createTempFile(null, null);
			Files.write(inputFile, parserDump.getBytes(StandardCharsets.UTF_8));
			Path outputFile = Files.createTempFile(null, null);
			String[] cmd = {"dtc", "-I", "dts", "-O", "dtb", "-o", outputFile.toString(), inputFile.toString()};
			CmdProc proc = new CmdProc(cmd);
			return proc;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<File> listFilesForFolder(File folder) {
		ArrayList<File> dtsFiles = new ArrayList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				dtsFiles.addAll(listFilesForFolder(fileEntry));
			} else {
				if (fileEntry.getName().endsWith(".dts")) {
					dtsFiles.add(fileEntry);
				}
			}
		}
		return dtsFiles;
	}

}
