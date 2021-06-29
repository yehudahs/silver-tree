package com.silvertree.ideplugin.common;

import java.util.ArrayList;
import java.util.Collections;

public class DeviceTree extends DeviceTreeObject{
	
	public ArrayList<DeviceTreeObject> children;
	
	public DeviceTree(Token tok) throws Exception {
		super();
		setToken(tok);
		getToken().setType(Token.TokenType.TREE);
		children = new ArrayList<DeviceTreeObject>();
		
		try {
			parse();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error parsing:\n" + tok);
		}
	}
	

	@Override
	public void parse() throws Exception {
		if (getToken() == null) return;
		
		int textLength = getToken().toString().length();
		int currPos = 0;
		int iteration = 0;
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
				currPos = tok.getToOffset();
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
			
			iteration++;
		}
	}
	
	private Token getNextToken(int startPos) throws Exception {
		//check if we have comments
		Token singleCommentTok = getNextSingleLineCommentPos(startPos);
		if (!singleCommentTok.isEmpty())
			return singleCommentTok;
		
		Token multiLineTok = getNextMultiLineCommentPos(startPos);
		if (! multiLineTok.isEmpty())
			return multiLineTok;
		
		//check if we have include
		Token includeTok = getNextIncludePos(startPos);
		if (! includeTok.isEmpty())
			return includeTok;
		
		ArrayList<Token> nextTokens = new ArrayList<Token>();
		nextTokens.add(getNextSingleLineAttrPos(startPos));
		nextTokens.add(getNextKeyValuePos(startPos));
		nextTokens.add(getNextDeviceTreePos(startPos));
		Token nextToken = Collections.min(nextTokens);
		if (nextToken.isEmpty()) {
			System.out.println("\nError: can't tokinize:\n" + getToken().toString().substring(startPos));
			throw new Exception("returning empty token...");
		}
		return nextToken;
	}
	
	
	/**
	 * @param currPos
	 */
	private Token getNextMultiLineCommentPos(int currPos) {

		if (! getToken().toString().substring(currPos).startsWith("/*")) {
			return new Token();
		}
		
		int multiCommentEndPos = getToken().toString().indexOf("*/", currPos) + 2;
		Token tok = new Token(getToken().toString(), currPos, multiCommentEndPos, currPos, Token.TokenType.COMMENT);
		return tok;
	}

	/**
	 * @param currPos
	 */
	private Token getNextSingleLineCommentPos(int currPos) {
		if (! getToken().toString().substring(currPos).startsWith("//")) {
			return new Token();
		}

		int endOfLinePos = getToken().toString().indexOf("\n", currPos);
		Token tok = new Token(getToken().toString(), currPos, endOfLinePos, currPos, Token.TokenType.COMMENT);
		return tok;
	}
	
	private Token getNextIncludePos(int currPos) {
		if (getToken().toString().substring(currPos).startsWith("#include")) {
			int includeStartPos = getToken().toString().indexOf("#include", currPos);
			int endOfLinePos = getToken().toString().indexOf("\n", includeStartPos);
			Token tok = new Token(getToken().toString(), includeStartPos, endOfLinePos, currPos, Token.TokenType.INCLUDE);
			return tok;
		}

		if (getToken().toString().substring(currPos).startsWith("/include")) {
			int includeStartPos = getToken().toString().indexOf("/include", currPos);
			int endOfLinePos = getToken().toString().indexOf("\n", includeStartPos);
			Token tok = new Token(getToken().toString(), includeStartPos, endOfLinePos, currPos, Token.TokenType.INCLUDE);
			return tok;
		}

		return new Token();
		
	}


	/**
	 * @param currPos
	 * @return
	 * @throws Exception 
	 */
	private Token getNextDeviceTreePos(int currPos) throws Exception {
		int deviceTreeStartPos = getToken().toString().indexOf("{", currPos);
		if (deviceTreeStartPos == -1)
			return new Token();
		
		// because we are identifing trees when we see {, we can't add the { char into the next tree.
		// it will cause an endless loop...
		deviceTreeStartPos += 1;
		
		//need to find the ending "}"
		int deviceTreeEndPos = findClosingBrackets(getToken().toString(), deviceTreeStartPos);
		Token tok = new Token(getToken().toString(), deviceTreeStartPos, deviceTreeEndPos, deviceTreeStartPos, Token.TokenType.TREE);
		return tok;
	}

	private Token getNextKeyValuePos(int currPos) {
		int KeyValuePos = getToken().toString().indexOf("=", currPos);
		if (KeyValuePos == -1)
			return new Token();
		int attributeEndPos = getToken().toString().indexOf(";", currPos);
		Token tok = new Token(getToken().toString(), currPos, attributeEndPos+1, KeyValuePos, Token.TokenType.ATTRIBUTE);
		return tok;
	}
	/**
	 * @param currPos
	 * @return
	 */
	private Token getNextSingleLineAttrPos(int currPos) {
		int attributeEndPos = getToken().toString().indexOf(";", currPos);
		if (attributeEndPos == -1)
			return new Token();
		Token tok = new Token(getToken().toString(), currPos, attributeEndPos+1, attributeEndPos, Token.TokenType.ATTRIBUTE);
		return tok;
	}
	
	private int findClosingBrackets(String text, int openPos) throws Exception {
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
				throw new Exception("can't find closing brackets");
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
