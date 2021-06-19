package com.silvertree.ideplugin.common;

import java.util.ArrayList;

public class DeviceTree extends DeviceTreeObject{
	
	private String deviceTree;
	public ArrayList<DeviceTreeObject> children;
	
	public DeviceTree(String text) throws Exception {
		deviceTree = text;
		setName(null);
		setType(DeviceTreeType.TREE);
		children = new ArrayList<DeviceTreeObject>();
		parse();
	}
	

	@Override
	public void parse() throws Exception {
		if (deviceTree == null) return;
		
		int textLength = deviceTree.length();
		int currPos = 0;
		while(currPos != textLength) {
			//check if the next token is Sentence or DeviceTree
			int nextSentenceEndPos = deviceTree.indexOf(";", currPos);
			int nextDeviceTreeStartPos = deviceTree.indexOf("{", currPos);
			
			//if there is no more sentences and no more DeviceTree - we have finished the parsing and return.
			if (nextSentenceEndPos == -1 && nextDeviceTreeStartPos == -1) {
				return;
			}else if(nextSentenceEndPos != -1 &&  nextDeviceTreeStartPos == -1) {
				// we have another sentence
				currPos = addAttribute(currPos, nextSentenceEndPos);
			}else if (nextSentenceEndPos == -1 && nextDeviceTreeStartPos != -1) {
				// we have found a device tree. find matching brackets
				currPos = addDeviceTree(currPos);
			} else if (nextSentenceEndPos != -1 && nextDeviceTreeStartPos != -1) {
				// if we find that we have more sentences and device trees - find who is closer.
				if (nextSentenceEndPos < nextDeviceTreeStartPos) {
					currPos = addAttribute(currPos, nextSentenceEndPos);
				}else {
					currPos = addDeviceTree(currPos);
				}
			} else {
				throw new Exception("Error parsing tree");
			}
		}
	}


	private int addDeviceTree(int from) throws Exception {
		int nextDeviceTreeStartPos = deviceTree.indexOf("{", from);
		int nextDeviceTreeEndPos = findClosingBrackets(deviceTree, nextDeviceTreeStartPos);
		DeviceTree t = new DeviceTree(deviceTree.substring(nextDeviceTreeStartPos+1, nextDeviceTreeEndPos));
		String treeName = deviceTree.substring(from, nextDeviceTreeStartPos);
		treeName = treeName.replaceAll("\\s+", ""); // remove all white spaces
		t.setName(treeName);
		addChild(t);
		from = nextDeviceTreeEndPos;
		from += 2;
		return from;
	}


	private int addAttribute(int currPos, int nextSentenceEndPos) {
		Attribute s = new Attribute(deviceTree.substring(currPos, nextSentenceEndPos));
		addChild(s);
		currPos = nextSentenceEndPos + 1;
		return currPos;
	}
	
	private int findClosingBrackets(String text, int openPos) {
	    int closePos = openPos;
	    int counter = 1;
	    while (counter > 0) {
	        try {
				char c = text.charAt(++closePos);
				if (c == '{') {
				    counter++;
				}
				else if (c == '}') {
				    counter--;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return closePos;
	}
	
	public void addChild(DeviceTreeObject child) {
		children.add(child);
		child.setParent(this);
	}
	public void removeChild(DeviceTreeObject child) {
		children.remove(child);
		child.setParent(null);
	}
	public DeviceTreeObject [] getChildren() {
		return (DeviceTreeObject [])children.toArray(new DeviceTreeObject[children.size()]);
	}
	public boolean hasChildren() {
		return children.size()>0;
	}

}
