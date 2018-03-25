package de.punktjb.test.course.organizer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.collections.impl.list.mutable.FastList;

/**
 * Session class consists of name, start time/date plus list of blocks
 * @author jurica
 *
 */
public class Session {

	// name
	private String name;
	
	// start date and time (but actually only hours are used)
	private Date startDate;
	
	// list of blocks
	private List<Block> blocks = new FastList<Block>();
	
	public Session(String name, Date startDate) {
		super();
		this.name = name;
		this.startDate = startDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}
	
	/**
	 * returns session string including blocks and tracks
	 */
	public String toString() {
		
		StringBuffer buf = new StringBuffer();

		// set name
		buf.append(name).append("\n");
		
		// time management - needed to 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);

		// for all blocks
		blocks.forEach( block -> {

				// add to out buffer (calendar time is needed to printout right time)
				buf.append( block.toString(calendar.getTime()) );
				
				// update time
				calendar.add(Calendar.MINUTE, block.getActualLength());
			} 
		);		
		
		return  buf.toString();
	}
}
