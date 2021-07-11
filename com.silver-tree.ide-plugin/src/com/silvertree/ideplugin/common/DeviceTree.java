package com.silvertree.ideplugin.common;

import java.util.ArrayList;
import java.util.Collections;

import com.silvertree.ideplugin.common.Token.AttributeType;
import com.silvertree.ideplugin.common.Token.TokenType;
import com.silvertree.ideplugin.common.address_regs.MemoryMap;
import com.silvertree.ideplugin.common.address_regs.Reg;

public class DeviceTree extends DeviceTreeObject{
	
	public ArrayList<DeviceTreeObject> children;
	private DeviceTreeAttribute _reg;
	private ArrayList<MemoryMap> _memoryMap;
	private Reg _nonMemoryMap;
	private DeviceTreeAttribute _size_cells;
	private DeviceTreeAttribute _address_cells;
	
	public static DeviceTree create(Token tok) {
		
		DeviceTree tree = new DeviceTree(tok);
		try {
			
			tree.setChildrenParent();
			tree.setSpecialAttributes();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error parsing:\n" + tok);
		}
		return tree;
	}
	
	protected DeviceTree(Token tok) {
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
	
	protected void setChildrenParent() {
		for (DeviceTreeObject child: getChildren(false)) {
			child.setParent(this);
		}
	}
	
	private void setSpecialAttributes() {
		for (DeviceTreeObject child: getChildren(false)) {
			if (child.getToken().getType() == TokenType.ATTRIBUTE) {
				if (child.getToken().getAttrType() == AttributeType.REG_ATTRIBUTE)
					_reg = (DeviceTreeAttribute) child;
				if (child.getToken().getAttrType() == AttributeType.SIZE_CELLS_ATTRIBUTE)
					_size_cells = (DeviceTreeAttribute) child;
				if (child.getToken().getAttrType() == AttributeType.ADDRESS_CELLS_ATTRIBUTE)
					_address_cells = (DeviceTreeAttribute) child;
			}
		}
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
			case TREE:
				DeviceTree t = DeviceTree.create(tok);
				t.setKey(getToken().toString().substring(currPos, getToken().toString().indexOf("{", currPos)));				
				addChild(t);
				
				// jump over the closing "}"
				currPos = tok.getToOffset() + 1;
				// jump over the closing ";"
				currPos = getToken().toString().indexOf(";", currPos) + 1;
				break;
			default:
				DeviceTreeAttribute s = new DeviceTreeAttribute(tok);
				addChild(s);
				currPos = tok.getToOffset();
				break;
			}
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
		
		Token defineTok = getNextDefinePos(startPos);
		if (! defineTok.isEmpty())
			return defineTok;
		
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
	
	
	private Token getNextDefinePos(int currPos) {
		if (getToken().toString().substring(currPos).startsWith("#define")) {
			int includeStartPos = getToken().toString().indexOf("#define", currPos);
			int endOfLinePos = getToken().toString().indexOf("\n", includeStartPos);
			Token tok = new Token(getToken().toString(), includeStartPos, endOfLinePos, currPos, Token.TokenType.DEFINE);
			return tok;
		}
		
		return new Token();
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
		
		if (getToken().toString().substring(currPos).endsWith(".dts")) {
			int endOfLinePos = getToken().toString().indexOf("\n", currPos);
			// sometimes the only line in the file is just a refernce to another .dts file without
			//ending \n
			if (endOfLinePos == -1)
				endOfLinePos = getToken().toString().length();
			Token tok = new Token(getToken().toString(), currPos, endOfLinePos, currPos, Token.TokenType.INCLUDE);
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
				char c = text.charAt(closePos);
				if (c == '{') {
				    counter++;
				}
				else if (c == '}') {
				    counter--;
				}
				closePos++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception("can't find closing brackets");
			}
	    }
	    //don't return the '}'.
	    return closePos-1;
	}
	
	public void addChild(DeviceTreeObject child) {
		children.add(child);
	}
	public void removeChild(DeviceTreeObject child) {
		children.remove(child);
		child.setParent(null);
	}
	public DeviceTreeObject [] getChildren(boolean visibleOnly) {
		if (visibleOnly) {
			ArrayList<DeviceTreeObject> visibleChilderen = new ArrayList<DeviceTreeObject>();
			for (DeviceTreeObject child : children)
				if (child.isVisible())
					visibleChilderen.add(child);
			return (DeviceTreeObject [])visibleChilderen.toArray(new DeviceTreeObject[visibleChilderen.size()]);
		}
		else
			return (DeviceTreeObject [])children.toArray(new DeviceTreeObject[children.size()]);
		
	}
	public boolean hasChildren() {
		return children.size()>0;
	}
	
	@Override
	public String dump(int ident) {
		var identString = identString(ident);
		var dumpString = identString;
        dumpString += getKey() + "{\n";
		for (DeviceTreeObject ob : children) {
			var child = ob.dump(ident + 1);
			if (child != null)
				dumpString += child;
		}
		dumpString += identString + "};\n";
		return dumpString;
    }


	@Override
	public boolean isVisible() {
		return true;
	}
	
	
	public DeviceTree [] getAllSubTreesRec(boolean visibleOnly){
		ArrayList<DeviceTree> children = new ArrayList<DeviceTree>();
		for (DeviceTreeObject dto: getChildren(visibleOnly)) {
			if (dto instanceof DeviceTree) {
				children.add((DeviceTree) dto);
				DeviceTree [] subTree = ((DeviceTree) dto).getAllSubTreesRec(visibleOnly);
				Collections.addAll(children, subTree);
			}
		}
		return (DeviceTree [])children.toArray(new DeviceTree[children.size()]);
	}
	
	public DeviceTree [] getTreesSortedByAddress() {
		DeviceTree[] treeArr = getAllSubTreesRec(false);
		ArrayList<DeviceTree> treesWithAddress = new ArrayList<DeviceTree>();
		for (DeviceTree tree: treeArr) {
			if (tree.hasReg()) {
				treesWithAddress.add(tree);
			}
		}
		return (DeviceTree [])treesWithAddress.toArray(new DeviceTree[treesWithAddress.size()]);
	}


	private boolean hasReg() {
		return getReg() != null;
	}


	public DeviceTreeAttribute getReg() {
		return _reg;
	}

	public void setReg(DeviceTreeAttribute attr) {
		this._reg = attr;
	}


	public boolean hasSizeCells() {
		return getSizeCells() != null;
	}
	
	public DeviceTreeAttribute getSizeCells() {
		if (_size_cells == null && hasParent()) 
			_size_cells = getParent().getSizeCells();
		return _size_cells;
	}
	
	public void setSizeCells(DeviceTreeAttribute attr) {
		_size_cells = attr;
	}
	
	public boolean hasAddressCells() {
		return getAddressCells() != null;
	}

	public DeviceTreeAttribute getAddressCells() {
		if (_address_cells == null && hasParent())
			_address_cells = getParent().getAddressCells();
		return _address_cells;
	}
	
	public void setAddressCells(DeviceTreeAttribute attr) {
		_address_cells = attr;
	}
	
	
	protected void processMemMapsRec() {
		if (hasReg() && hasAddressCells() && hasSizeCells()) {
			int	address_cells_length = Integer.decode(getAddressCells().getValue());
			int	size_cells_length = Integer.decode(getSizeCells().getValue());
			if (address_cells_length < 1 || size_cells_length < 1)
				return;
			
			String[] regsArr = getReg().getValue().split(" ");
			// check that we have memory mapped tree and
			// not Non Memory Mapped or CPU (which has only 1 cell)
			if (regsArr.length <= 1)
				return;

			int single_register_size = address_cells_length + size_cells_length;
			int regs_count = regsArr.length / single_register_size;
			_memoryMap = new ArrayList<MemoryMap>();
			for (int reg_idx = 0; reg_idx < regs_count; reg_idx++) {
				int from_addr_cells = reg_idx * single_register_size;
				int to_addr_cells = from_addr_cells + address_cells_length - 1;
				int size_cells_from = to_addr_cells + 1;
				int size_cells_to = size_cells_from + size_cells_length - 1;
				MemoryMap reg = new MemoryMap(regsArr, from_addr_cells, to_addr_cells, size_cells_from, size_cells_to);
				_memoryMap.add(reg);
			}
			
		}
		
		for (DeviceTreeObject tree: getChildren(false)) {
			if (tree instanceof DeviceTree)
				((DeviceTree) tree).processMemMapsRec();
		}
	}
	
	
	protected void processNonMemMapsRec() {
		if (hasReg()) {
			String[] regsArr = getReg().getValue().split(" ");
			if (regsArr.length == 1)
				return;
			_nonMemoryMap = new Reg(regsArr[0]);
		}
		
		for (DeviceTreeObject tree: getChildren(false)) {
			if (tree instanceof DeviceTree)
				((DeviceTree) tree).processNonMemMapsRec();
		}
	}
}
