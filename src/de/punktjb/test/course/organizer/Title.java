package de.punktjb.test.course.organizer;

/**
 * Title class - contains title name and time length
 * @author jurica
 *
 */
public class Title {

	// title name 
	private String name;
	
	// title time length
	private int length;
	
	public Title(String name, int length) {
		super();
		this.name = name;
		this.length = length;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Output string of title with its length
	 */
	public String toString() {

		StringBuffer buf = new StringBuffer();
		
		buf.append(name).append(" ");
		
		// lightning shall be displayed instead of 5min
		if( length == 5 )
			buf.append("lightning");
		else
			buf.append(length).append("min");
		
		return  buf.toString();
	}
}
