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
	private Integer _tokenIdentifierPos;
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
		KEY_VALUE_ATTRIBUTE,
		KEY_STRING_ATTRIBUTE,
		KEY_INT_ATTRIBUTE,
	}
	
	public Token(String text, Integer from, Integer to, Integer identifierPos, TokenType type) {
		_tokId = tokCount;
		tokCount++;
		_text = text;
		_fromTextOffset = from;
		_toTextOffset = to;
		_type = type;
		_tokenIdentifierPos = identifierPos;
	}
	
	public Token() {
		_tokId = tokCount;
		tokCount++;
		_text = "";
		_fromTextOffset = Integer.MIN_VALUE;
		_toTextOffset = Integer.MAX_VALUE;
		_type = TokenType.NONE;
		_tokenIdentifierPos = Integer.MAX_VALUE;
	}
	
	public int getTokenIdentifierPos() {
		return _tokenIdentifierPos;
	}
	
	public boolean isEmpty() {
		return getType() == TokenType.NONE;
	}
	
	public int getID() {
		return _tokId;
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
		Integer distanceToToken = getTokenIdentifierPos();
		Integer otherDistanceToToken = o.getTokenIdentifierPos();
		return distanceToToken.compareTo(otherDistanceToToken);
	}
	
	
}
