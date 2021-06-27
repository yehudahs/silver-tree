/**
 * 
 */
package com.silvertree.ideplugin.common;

/**
 * @author yehuda.s
 *
 */
public class Token implements Comparable<Token> {
	
	private static int tokCount = 0;
	private Integer _tokId;
	private String _text;
	private Integer _fromTextOffset;
	private Integer _toTextOffset;
	private TokenType _type;
	private AttributeType _attrType;

	public enum TokenType{
		NONE,
		TREE,
		ATTRIBUTE,
		INCLUDE,
		COMMENT,
		
	}
	
	public enum AttributeType{
		None,
		SINGLE_LINE_ATTRIBUTE,
		KEY_STRING_ATTRIBUTE,
		KEY_INT_ATTRIBUTE,
	}
	
	public Token(String text, Integer from, Integer to, TokenType type) {
		_text = text;
		_fromTextOffset = from;
		_toTextOffset = to;
		_type = type;
	}
	
	public Token() {
		_tokId = tokCount;
		tokCount++;
		_text = "";
		_fromTextOffset = Integer.MIN_VALUE;
		_toTextOffset = Integer.MAX_VALUE;
		_type = TokenType.NONE;
	}
	
	public boolean isEmpty() {
		return getType() == TokenType.NONE;
	}

	public Integer getFromOffset() {
		return _fromTextOffset;
	}
	
	public void setFromOffset(int fromOffset) {
		_fromTextOffset = fromOffset;
	}
	
	public Integer getToOffset() {
		return _toTextOffset;
	}
	
	public void setToOffset(int toOffset) {
		_toTextOffset = toOffset;
	}
	
	TokenType getType() {
		return _type;
	}
	
	void setType(TokenType type) {
		_type = type;
	}

	/**
	 * @return the _attrType
	 */
	public AttributeType getAttrType() {
		return _attrType;
	}

	/**
	 * @param _attrType the _attrType to set
	 */
	public void setAttrType(AttributeType _attrType) {
		this._attrType = _attrType;
	}
	
	public String toString() {
		if (getType() == TokenType.NONE) {
			return "";
		}
		return _text.substring(_fromTextOffset, _toTextOffset);
	}

	@Override
	public int compareTo(Token o) {
		Integer distanceToObject = getToOffset();
		Integer otherDistanceToObject = o.getToOffset();
		
		// when we are dealing with trees than the distance is when the tree starts
		// otherwise the attribute inside the tree will be catch first.
		if (getType() == TokenType.TREE) {
			distanceToObject = getFromOffset();
		}
		if (o.getType() == TokenType.TREE) {
			otherDistanceToObject = o.getFromOffset();
		}
		return distanceToObject.compareTo(otherDistanceToObject);
	}
	
	
}
