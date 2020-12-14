package org.jlibsedml;

/*
 * This is used as an implementation class for holding model content when reading a .sedx file.
 */
class BaseModelContent implements IModelContent {
	private String content, name;
    
    BaseModelContent(String content, String name) {
		super();
		this.content = content;
		this.name = name;
	}

	
	public String getContents() {
		return content;
	}

	public String getName() {
		return name;
	}

}
