package com.silver_tree.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.silvertree.ideplugin.common.DeviceTreeRoot;
import com.silvertree.ideplugin.common.Token;

public class parser {

	public static void main(String[] args) throws IOException {
		ArrayList<Integer> test_list = parse_args(args);
		File sampleFolder = new File("./samples");
		ArrayList<File> dtsFiles = listFilesForFolder(sampleFolder);
		String statistics = "";
		int numOfSucc = 0, testNum = 0;
		for (File dtsFile : dtsFiles) {
			// run test if:
			// 1. the test_list is empty (just run all tests)
			// 2. the test_list contains this test (the user want to run this specific test).
			if (test_list.isEmpty() || test_list.contains(testNum)) {
				String test = "test: " + testNum + ": dtsFile: " + dtsFile + ": ";
				System.out.print(test);
				statistics += test;
				CmdProc proc = runTest(dtsFile);
				String result;
				if (proc.returnCode == 0) {
					result = "Succ\n";
					numOfSucc++;
				}else {
					result = "Fail (" + proc.errOut +")\n";
				}
				System.out.print(result);
				statistics += result;
			}
			testNum++;
		}
		String bottomLine = "succ: " + numOfSucc + " / " + (testNum + 1) + " (" + ((float) numOfSucc / dtsFiles.size()) + "%)";
		System.out.println(bottomLine);
		statistics += bottomLine;
		Path statisticsFile = Path.of("tests_results.txt");
		Files.writeString(statisticsFile, statistics);
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
			File inputFile = new File(dtsFile.getParent() + "/input-" + dtsFile.getName());
			File outputFile = new File(dtsFile.getParent() + "/output" + dtsFile.getName());
			File preProcOutFile = new File(dtsFile.getPath().replace(".dts", ".tmp"));
			FileWriter writer = new FileWriter(inputFile);
			writer.write(parserDump);
			writer.close();
			
			
			// run the preprocessor
			// example of cmd run
			// gcc -E -Wp,-MD,hifive-unmatched-a00.pre.tmp -nostdinc -Iarch/riscv/boot/dts -Iinclude -undef -D__DTS__  -x assembler-with-cpp -o ./arch/riscv/boot/dts/sifive/hifive-unleashed-a00.tmp  ./arch/riscv/boot/dts/sifive/hifive-unleashed-a00.dts
			String[] PreProcCmd = {"gcc", 
					"-E", 
					"-Wp,-MD," + dtsFile.getPath().replace(".dts", ".pre.tmp"),
					"-nostdinc",
					"-I./samples/include",
					"-undef", "-D__DTS__",
					"-x", "assembler-with-cpp",
					"-o", preProcOutFile.getPath(),
					dtsFile.getPath(),
			};
			CmdProc preProc = new CmdProc(PreProcCmd);
			if (preProc.returnCode != 0) {
				inputFile.delete();
				outputFile.delete();
				preProcOutFile.delete();
				return preProc;
			}

			//run the dtc
			//example of running the dtc
			// dtc -I dts -O dts ./arch/riscv/boot/dts/sifive/hifive-unleashed-a00.tmp -o ./arch/riscv/boot/dts/sifive/hifive-unleashed-a00.final
			
			String[] cmd = {"dtc", 
					"-I", "dts", 
					"-O", "dtb", 
					"-o", outputFile.toString(), preProcOutFile.getPath()};
			CmdProc proc = new CmdProc(cmd);
			
			inputFile.delete();
			outputFile.delete();
			preProcOutFile.delete();
			
			return proc;
		} catch (Exception e) {
			System.out.println("got exception working on: " + dtsFile.getPath());
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
