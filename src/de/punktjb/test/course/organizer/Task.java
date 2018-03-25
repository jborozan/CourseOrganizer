package de.punktjb.test.course.organizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;


/**
 * Main class
 * @author jurica
 *
 */
public class Task {

	// input 
	Set<Title> titles = new UnifiedSet<>();

	// blocks set
	Set<Block> blocks = new UnifiedSet<>();

	// alignment list
	private IntSet aligments;

	/**
	 * Constructor
	 */
	public Task() {

		// init alignment set 
		aligments = IntSets.immutable.with(60, 30, 45, 15, 20, 40);

	}
	
	/**
	 * It starts here
	 */
	public void process() {

		// create "static" lunch title/block
		// full block with 1 lunch title
		Title lunchTitle = new Title("Lunch", 60);
		Block lunchblock = new Block("Lunch", 60, 60);
		lunchblock.getTitles().add(lunchTitle);
		
		// create "static" myce title/block
		// full block with 1 meet your colleagues event title
		Title myceTitle = new Title("Meet Your Colleagues Event", 60);
		Block myceblock = new Block("Meet Your Colleagues Event", 60, 60);
		myceblock.getTitles().add(myceTitle);
		
		// could be extended to parse and include date with yyyy-MM-dd
		SimpleDateFormat ft = new SimpleDateFormat ("HH:mm");
		
		try {
			// create track 1 and add blocks 
			Session track1 = new Session("Track 1", ft.parse("09:00"));
			track1.getBlocks().add(new Block("Morning block 1", 180, 180));
			track1.getBlocks().add(lunchblock);
			track1.getBlocks().add(new Block("Afternoon block 1", 180, 240));
			track1.getBlocks().add(myceblock);

			// include blocks from track 1 in all blocks variable
			blocks.addAll(track1.getBlocks());
			
			// create track 2 and add blocks
			Session track2 = new Session("Track 2", ft.parse("09:00"));
			track2.getBlocks().add(new Block("Morning block 2", 180, 180));
			track2.getBlocks().add(lunchblock);
			track2.getBlocks().add(new Block("Afternoon block 2", 180, 240));
			track2.getBlocks().add(myceblock);
			
			// include blocks from track 2 in all blocks variable
			blocks.addAll(track2.getBlocks());
			
			// just small info
			int totalTitleTime = titles.stream().mapToInt( title -> title.getLength() ).sum();
			int minimumblockTime = blocks.stream().filter( s -> !s.isBlockValid() ).mapToInt(block -> block.getMinLength()).sum();
			int maximumblockTime = blocks.stream().filter( s -> !s.isBlockValid() ).mapToInt(block -> block.getMaxLength()).sum();
			
			System.out.println();
			System.out.println(" * Info: total available title time         = " + totalTitleTime + "min");
			System.out.println(" *       minimum available time in blocks = " + minimumblockTime + "min" );
			System.out.println(" *       maximum available time in blocks = " + maximumblockTime + "min" );

			//distribute titles to blocks
			doTitlesDistribution();
			
			// and print tracks
			System.out.println();
			System.out.print( track1.toString() );
			System.out.println();
			System.out.print( track2.toString() );	
		}
		catch(Exception e) {
			e.printStackTrace(System.err);
		}

	}

	/**
	 * Does the titles distribution
	 * easy way distribution: order titles (by 60, 30, 45, 15, rest length) and 
	 * distribute them first to blocks that need to be full (min==max length)
	 * and then flexible ones
	 * 
	 * @param titles
	 * @param blocks
	 */
	private void doTitlesDistribution() {
		
		// for sorting/aligning titles
		List<Title> sortedTitles = new FastList<>();

		// sort/align titles using alignment values
		aligments.forEach( i -> sortedTitles.addAll( titles.stream().filter( t -> t.getLength() == i ).collect(Collectors.toList()) ) );

		// and then add rest ones
		sortedTitles.addAll( titles.stream().filter( t -> !aligments.contains(t.getLength()) ).collect(Collectors.toList()) );
		
		// go through sorted titles
		sortedTitles.forEach(new Consumer<Title>() {

			@Override
			public void accept(Title t) {
				
				// get/find block
				Block block = findblockToInsert(t.getLength());	
				
				// and insert
				if( block != null )
					block.addTitle(t);
			}

			// logic here: first try to find full block with available space which equals title length
			//             then try to find full block with larger available space that title length
			//             than try to find flexible block with equal space and title length
			//             than try to find flexible block with larger space than title length
			// * valid block is one that has actual length between min and max length for flexible blocks or equal if min=max for full blocks
			private Block findblockToInsert(int length) {

				// find best and full fit in non valid full block
				Optional<Block> oblock = blocks.stream().filter( s -> !s.isBlockValid() && s.getAvailableSpace() == length && s.getMaxLength() == s.getMinLength() ).findFirst();
				
				if( oblock.isPresent() )
					return oblock.get();

				// or find next possible full fit in non valid full block
				oblock = blocks.stream().filter( s -> !s.isBlockValid() && s.getAvailableSpace() > length && s.getMaxLength() == s.getMinLength() ).findFirst();

				if( oblock.isPresent() )
					return oblock.get();

				// or find best fit in non valid flexible block
				oblock = blocks.stream().filter( s -> !s.isBlockValid() && s.getAvailableSpace() == length ).findFirst();
				
				if( oblock.isPresent() )
					return oblock.get();

				// or find possible fit in non valid flexible block
				oblock = blocks.stream().filter( s -> !s.isBlockValid() && s.getAvailableSpace() > length ).findFirst();

				if( oblock.isPresent() )
					return oblock.get();

				// or find "there is some more space in valid flexible block" fit
				oblock = blocks.stream().filter( s -> s.getAvailableSpace() > length ).findFirst();

				if( oblock.isPresent() )
					return oblock.get();
				else
					return null;				
			}			
		});
		
		return;
	}
	
	/**
	 * Reads lines from console
	 */
	private void inputTitlesFromConsole() {

        System.out.println("Enter Title, empty line to end:");
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        String inputLine;
        
        try {        	
            // until empty line actually
			while ((inputLine = br.readLine()) != null && inputLine.length() != 0) {
							
				// parse line or exit
				if( inputLine.isEmpty() )
					break;
				else {
					// first trim it
					parseStringToTitle( inputLine.trim() );
				}
			}
			
		} catch (IOException ignore) {
			System.err.println("** Error reading from input!");;
		}
        finally {
        		try {
					br.close();
				} catch (IOException ignore) {}
        }
		
        return;
	}

	/**
	 * Parse input and extracts title from string
	 * @param inputLine input string
	 */
	public void parseStringToTitle(String inputLine) {
		
		// find last space
		int lastSpace = inputLine.lastIndexOf(' ');
		
		// if there is one
		if( lastSpace != -1 ) {
			String titleName = inputLine.substring(0, lastSpace);
			
			// check string ending
			if( inputLine.endsWith("lightning") )
			{
				// it is a 5 minute one
				titles.add(new Title( titleName, 5));
			}
			else if( inputLine.endsWith("min") )
			{
				// number shall be somewhere there
				int lastMinString = inputLine.length() - 3;
				
				try {
					// parse number in substring and add title
					int length = Integer.valueOf(inputLine.substring(lastSpace + 1, lastMinString));
					titles.add( new Title(titleName, length) );
				}
				catch(Exception ignore) {
					System.err.println("** Unable to parse number of minutes in this entry!");
				}
			}
			else
				System.err.println("** Unable to parse this entry, lightning or min is missing!");
		}
		else
			System.err.println("** Unable to parse this entry!");
		
		return;
	}
	
	/**
	 * Reading titles form file
	 * @param fileName file name
	 */
	public void readTitlesFromFile(String fileName) {
		
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			stream.forEach( t -> parseStringToTitle( t.trim() ) );

		} catch (IOException e) {
			System.err.println("** Unable to parse this file: " + fileName);
		}
	}

	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		
		Task task = new Task();
		
		if(args.length > 0 && args[0].equals("-f"))
		{
			// input titles
			task.readTitlesFromFile(args[1]);
			
			// and do the processing
			task.process();
		}
		else
		{
			// input titles
			task.inputTitlesFromConsole();
			
			// and do the processing
			task.process();
		}

		return;
	}
	
}
