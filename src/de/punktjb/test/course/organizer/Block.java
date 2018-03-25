package de.punktjb.test.course.organizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;

/**
 * There are multiple types of blocks
 *  - full: with minimal time length = max time length (like morning ones)
 *    those have priority when distributing titles
 *  - flexible: with minimal time length < max time length (like afternoon ones)
 * @author jurica
 *
 */
public class Block {

	private String name;
	private int minLength;
	private int maxLength;
	
	private List<Title> titles = new FastList<Title>();
	
	public Block(String name, int minLength, int maxLength) {
		super();
		this.name = name;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public List<Title> getTitles() {
		return titles;
	}

	public void setTitles(List<Title> titles) {
		this.titles = titles;
	}
	
	public void addTitle(Title title) {
		
		this.titles.add(title);
	}
	
	public void removeTitle(Title title) {
		
		this.titles.remove(title);
	}
	
	/**
	 * Calculate actual block length
	 * @return actual block length in minutes
	 */
	public int getActualLength() {
		
		return titles.stream().mapToInt( title -> title.getLength() ).sum();
	}

	/**
	 * Gives back free time in minutes  for this block
	 * @return available free time in minutes
	 */
	public int getAvailableSpace() {
		
		return maxLength - getActualLength();
	}
	
	/**
	 * returns if block used capacity lies (inclusive) between minimal and max required minutes
	 * @return
	 */
	public boolean isBlockValid() {
		
		int actualLength = getActualLength();
		
		return actualLength >= minLength && actualLength <= maxLength;
	}
	
	/**
	 * returns max possible free minutes in this block 
	 * @return
	 */
	public int getFreeMinutes() {
		
		return maxLength - getActualLength();
	}
	
	/**
	 * Output string including titles with start time in hours of the day
	 */
	public String toString(Date startTime) {

		StringBuffer buf = new StringBuffer();
		
		// time management and format output
		SimpleDateFormat ft = new SimpleDateFormat ("hh:mm a ");	
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startTime);
		
		titles.forEach( title -> {
				
				// add to buffer
				buf.append( ft.format(calendar.getTime()) ).append(title).append("\n");
				
				// and update for the next title - add those minutes
				calendar.add(Calendar.MINUTE, title.getLength());				
			}			
		);
		
		return  buf.toString();
	}

}
