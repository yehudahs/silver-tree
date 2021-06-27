package com.silvertree.ideplugin.common;

import java.util.ArrayList;
import java.util.Collections;

public class DeviceTree extends DeviceTreeObject{
	
	public ArrayList<DeviceTreeObject> children;
	
	public DeviceTree(Token tok) throws Exception {
		setToken(tok);
		getToken().setType(Token.TokenType.TREE);
		children = new ArrayList<DeviceTreeObject>();
		parse();
	}
	

	@Override
	public void parse() throws Exception {
		if (getToken() == null) return;
		
		int textLength = getToken().toString().length();
		int currPos = 0;
		while(currPos < textLength) {
			
			// skip white spaces
			if (getToken().toString().charAt(currPos) == ' '
					|| getToken().toString().charAt(currPos) == '\n'
					|| getToken().toString().charAt(currPos) == '\t'
					|| getToken().toString().charAt(currPos) == '\r') {
				currPos++;
				continue;
			}
			
			// get the next token.
			Token tok = getNextToken(currPos);
			switch(tok.getType()) {
			case ATTRIBUTE:
				DeviceTreeAttribute s = new DeviceTreeAttribute(tok);
				addChild(s);
				currPos = tok.getToOffset() + 1;
				break;
			case TREE:
				DeviceTree t = new DeviceTree(tok);
				t.setKey(getToken().toString().substring(currPos, getToken().toString().indexOf("{", currPos)));				
				addChild(t);
				
				// jump over the closing "}"
				currPos = tok.getToOffset() + 1;
				// jump over the closing ";"
				currPos = getToken().toString().indexOf(";", currPos) + 1;
				break;
			default:
				currPos = tok.getToOffset() + 1;
				break;
			
			}
			
		}
	}
	
	private Token getNextToken(int startPos) {
		ArrayList<Token> nextTokens = new ArrayList<Token>();
		nextTokens.add(getNextAttributePos(startPos));
		nextTokens.add(getNextDeviceTreePos(startPos));
		nextTokens.add(getNextSingleLineCommentPos(startPos));
		nextTokens.add(getNextMultiLineCommentPos(startPos));
		nextTokens.add(getNextIncludePos(startPos));
		Token nextToken = Collections.min(nextTokens);
		return nextToken;
	}
	
	
	/**
	 * @param currPos
	 */
	private Token getNextMultiLineCommentPos(int currPos) {
		int multiCommentStartPos = getToken().toString().indexOf("/*", currPos);
		if (multiCommentStartPos == -1)
			return new Token();
		int multiCommentEndPos = getToken().toString().indexOf("*/", multiCommentStartPos) + 2;
		Token tok = new Token(getToken().toString(), multiCommentStartPos, multiCommentEndPos, Token.TokenType.COMMENT);
		return tok;
	}

	/**
	 * @param currPos
	 */
	private Token getNextSingleLineCommentPos(int currPos) {
		int singleCommentStartPos = getToken().toString().indexOf("//", currPos);
		if (singleCommentStartPos == -1)
			return new Token();
		int endOfLinePos = getToken().toString().indexOf("\n", currPos);
		Token tok = new Token(getToken().toString(), singleCommentStartPos, endOfLinePos, Token.TokenType.COMMENT);
		return tok;
	}
	
	private Token getNextIncludePos(int currPos) {
		int includeStartPos = getToken().toString().indexOf("#include", currPos);
		if (includeStartPos == -1)
			return new Token();
		int endOfLinePos = getToken().toString().indexOf("\n", includeStartPos);
		Token tok = new Token(getToken().toString(), includeStartPos, endOfLinePos, Token.TokenType.INCLUDE);
		return tok;
	}


	/**
	 * @param currPos
	 * @return
	 */
	private Token getNextDeviceTreePos(int currPos) {
		int deviceTreeStartPos = getToken().toString().indexOf("{", currPos);
		if (deviceTreeStartPos == -1)
			return new Token();
		
		// because we are identifing trees when we see {, we can't add the { char into the next tree.
		// it will cause an endless loop...
		deviceTreeStartPos += 1;
		
		//need to find the ending "}"
		int deviceTreeEndPos = findClosingBrackets(getToken().toString(), deviceTreeStartPos);
		Token tok = new Token(getToken().toString(), deviceTreeStartPos, deviceTreeEndPos, Token.TokenType.TREE);
		return tok;
	}


	/**
	 * @param currPos
	 * @return
	 */
	private Token getNextAttributePos(int currPos) {
		int attributeEndPos = getToken().toString().indexOf(";", currPos);
		if (attributeEndPos == -1)
			return new Token();
		Token tok = new Token(getToken().toString(), currPos, attributeEndPos+1, Token.TokenType.ATTRIBUTE);
		return tok;
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
	
	@Override
	public int getAddressCells() {
		if (isAddressCellsExists()) {
			return super.getAddressCells();
		}else {
			DeviceTreeObject parent = getParent();
			while (parent != null && !parent.isAddressCellsExists()) {
				parent = parent.getParent();
			}
			if (parent != null)
				return parent.getAddressCells();
			else
				return super.getAddressCells();
			
		}
	}
	
	@Override
	public int getSizeCells() {
		if (isSizeCellsExists()) {
			return super.getSizeCells();	
		}else {
			DeviceTreeObject parent = getParent();
			while (parent != null && !parent.isSizeCellsExists()) {
				parent = parent.getParent();
			}
			if (parent != null)
				return parent.getSizeCells();
			else
				return super.getSizeCells();
		}
	}


	@Override
	public String dump() {
        var dumpString = getKey() + "{\n";
		for (DeviceTreeObject ob : children) {
			var child = ob.dump();
			if (child != null)
				dumpString += child;
		}
		dumpString += "};\n";
		return dumpString;
    }
}
