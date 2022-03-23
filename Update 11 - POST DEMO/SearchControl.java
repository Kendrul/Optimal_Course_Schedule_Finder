import java.util.Random;


public class SearchControl {
	
	private final boolean debug = true;
	
	private Parser parser = null;
	private Random rng1; //for good parents
	private Random rng2; //for bad parents
	private Random rng3; //for parent choice and random class choice
	private Random rng4; //for the cull
	
	//SEARCH RUN CONSTANTS
	private final int scheduleMax = 300; //this is the max number of schedules to hold
	private int currentSize = scheduleMax;
	private final int idealValue = 0; //this is the ideal eval result which would suggest an optimal schedule
	private final int cullResult = 30; //this is the number of schedules to keep when the max is reached, must be multiple of 3 and less then scheduleMax
	private final double runTimeRandom = 1000; //this sets the time parameter for how long the search will run
	private final double runTimeGenetic = 1000; //this sets the time parameter for how long the search will run
	private final int seedOne = 3466; //seed for the good-weighted RNG
	private final int seedTwo = 51951; //seed for the bad-weighted RNG
	private int seedThree= 19; //seed for the pickParent RNG
	private final int seedFour = 137; //for the cull
	
	public SearchControl(Parser parse)
	{
		parser = parse;
		//indexer();
		//seedThree = (int) System.currentTimeMillis();
	}
	
	public Schedule test()
	{
		rng3 = new Random(seedThree);
		int seed = rng3.nextInt();
	    for(int j = 0; j < 500; j++) 
	    {
	    		seed = rng3.nextInt();
	    }
		SearchInstanceRandom tester = new SearchInstanceRandom(rng3.nextInt(), parser);
		return tester.assignTest();
	}
	
	public Schedule begin()
	{
        Schedule [] startList = genesis(); //create an initial group 100 randomly created valid schedules
        System.out.println();
        System.out.println("Genesis complete");
        Schedule result = search(startList); //the main body of the search
        return result;
	}
	
	/**
	 * This method creates a scheduleMax randomly generated valid schedules to use in the mutation-algorithm search
	 * @return an array of 100 randomly generated valid schedules
	 */
	    private Schedule [] genesis()
	    {
	    	Schedule [] theList = new Schedule[scheduleMax];
	    	rng1 = new Random(seedOne);
	    	rng2 = new Random(seedTwo);
	    	rng3 = new Random(seedThree);
	    	rng4 = new Random(seedFour);
	    	int counter = 0;
	    	
	    	for(int i = 0; i < 30; i++)
	    	{//create each schedule
	    		Schedule current = null;
	    		
	    		while((current == null) && (counter < runTimeRandom))
	    		{//create schedules until a valid one is made

	    			SearchInstanceRandom creation = new SearchInstanceRandom(rng3.nextInt(), parser);
	    			current = creation.assign();
	    			if(debug) System.out.println("Random Loop Iteration: " + counter);
	    			//System.out.println(current);
	    			counter++;
	    		}
	    		//add the valid schedule to the list
	    		if(debug) System.out.println("Schedules Created: " + i);
	    		theList[i] = current;
	    	}//end for-loop
	    	
	    	return theList;
	    }
	    
	    private Schedule search(Schedule [] startList)
	    {
	    	if(debug) System.out.println("Start Main Search");
	    	Schedule [] theList = startList;
	    	Schedule winner = theList[pickBest(startList)];
	    	if(winner == null) return null;
	    	//System.out.println(winner);
	    	Schedule newGuy = winner;
	    	SearchInstanceGenetic searchParty;
	    	
	    	double timeIndex = 0;
	    	int listIndex = 0;
	    	
	    	while ((winner.getValue() > idealValue) && (timeIndex < runTimeGenetic))
	    	{
	    		if(debug) System.out.println("Primary Loop iteration: " + timeIndex);
	    		if(currentSize >= scheduleMax) {
	    			theList = cullWorst(theList);
	    			listIndex = cullResult;
	    		}
	    		
	    		//pick the parents for genetic input
	    		Schedule badParent = pickBad(theList);
	    		Schedule goodParent = pickGood(theList);
	    		
	    		searchParty = new SearchInstanceGenetic(goodParent, badParent, rng3.nextInt(), parser);
	    		newGuy = searchParty.assign();
	    		
	    		if(newGuy != null) {
	    			//System.out.println(newGuy.getValue());
	    			//System.out.println(newGuy);
	    			//check if the newly created schedule is the best one, if yes change winner to point to it
	    			if (newGuy.getValue() < winner.getValue() ) winner = newGuy;
	    			//add the new schedule to the array
	    			theList[listIndex] = newGuy;
	    			//increment the indexes
	    			listIndex++;
	    		}
	    		timeIndex++;
	    		currentSize++;
	    	}//end while loop
	    	
	    	return winner;
	    }//end search
	    
	    private Schedule [] cullWorst(Schedule [] theList)
	    {//this method removes (scheduleMax - cullResult) number of schedules from the list
	    	//keeping the cullResult (30) best schedules
	    	
	    	Schedule [] newList = new Schedule[scheduleMax];
	    	int winner;
	    	
	    	for(int i = 0; i < cullResult; i++)
	    	{//grab the best (30?) schedules, remove from old list and put into the new list
	    		winner = pickBest(theList);
	    		newList[i] = theList[winner];
	    		theList[winner] =  null;	    		
	    	}
	    	
	    	currentSize = cullResult;
	    	return newList;
	    }
	    
	    private Schedule [] cull(Schedule [] theList)
	    {//this method removes (scheduleMax - cullResult) number of schedules from the list
	    	//it grabs the best 10, worst 10, and 10 random schedules for the newList
	    	//TODO make more efficient
	    	Schedule [] newList = new Schedule[scheduleMax];
	    	Schedule winner = null;
	    	Schedule loser = null;
	    	Schedule randomed;
	    	
	    	int windex = 0;
	    	int lossdex = 0;
	    	int randex = 0;

	    	int runs = cullResult / 3;
	    	
	    	for(int i = 0; i < runs; i++)
	    	{//runs cullSize / 3 times
	    		
	    		for(int j = 0; j < scheduleMax; j++)
	    		{
		    		if(theList[j] == null) continue;
		    		else if(winner == null)
		    			{//no current "winner", set the first found schedule
		    				winner = theList[j];
		    				windex = j;
		    			}
		    		else if(winner.getValue() < theList[j].getValue())
		    		{//found a new best schedule, set it to be the current winner
		    			winner = theList[j];
		    			windex = j;
		    		}	
	    			
	    			if(theList[j] == null) continue;
		    		else if(loser == null) 
		    			{//no current "loser", set the first found schedule
		    				loser = theList[j];
		    				lossdex = j;
		    			}
		    		else if(loser.getValue() > theList[j].getValue())
		    		{//found a new worst schedule, set it to be the current winner
		    			loser = theList[i];
		    			lossdex = j;
		    		}//end if-else-else	
	    		}//end for-j-loop
	    		
	    		do {//pick a random schedule from the list
	    			randex = rng4.nextInt() % scheduleMax;
	    			if(randex < 0) randex = randex * -1;
	    			randomed = theList[randex];	    			
	    		} while ((randomed != null) && (randomed != winner) && (randomed != loser));
	    		
	    		//add to new list
	    		newList[i] = winner;
	    		newList[i + 1] = loser;
	    		newList[i + 2] = randomed;
	    		//remove from list
	    		theList[windex] = null;
	    		theList[lossdex] = null;
	    		theList[randex] = null;	    		
	    	}//end for-i-loop
	    	
	    	currentSize = cullResult;
	    	return newList;
	    }//end cull
	    
	    private int pickBest(Schedule [] theList)
	    {//This method returns the schedule with the lowest penalties from the input list
	    	//set best to the first slot to start
	    	int winner = 0;
	    	
	    	for(int i = 0; i < theList.length; i++)
	    	{//compare the current best schedule to the one at the index, 
	    		if(theList[i] == null) continue;
	    		else if(theList[winner] == null) winner = i;
	    		else if(theList[winner].getValue() > theList[i].getValue())
	    		{//found a new best schedule, set it to be the current winner
	    			winner = i;
	    		}	    		
	    	}	    		    	
	    	return winner;
	    }//end pickBest
	    
	    private Schedule pickBad(Schedule [] theList)
	    {//this function picks a schedule weighted towards the lower value schedules
	    	int [] choice = pickBatch(theList);
	    	int batch = 10;
	    	Schedule [] smallList = smallerList(theList, choice);
	    	Schedule [] tempList = new Schedule[smallList.length - batch];
	    	
	    	for(int i = 0; i < batch; i++)
	    	{//grabs the best 10 values from this small list and removes them
	    		int temp = pickBest(smallList);
	    		smallList[temp] = null;
	    	}
	    	
	    	int j = 0;
	    	
	    	for(int i=0; i < smallList.length; i++)
	    	{//go through the small list
	    		if(smallList[i] != null)
	    		{//grab a non-null value and put it in our new list
	    			//these values would be the lowest 10 values from the original smallList
	    			tempList[j] = smallList[i];
	    			j++;
	    		}
	    	}
	    	//picks one of the 5 values from the small list to be the bad parent
	    	int roll;
	    	
	    	
	    	roll = rng4.nextInt() % tempList.length;
	    	if(roll < 0) roll *= -1;
	    	
	    	Schedule chosen = tempList[roll];
	    	
	    	return chosen;
	    }
	    
	    private Schedule pickGood(Schedule [] theList)
	    {//this function picks a schedule weighted towards the better value schedules
	    	int [] choice = pickBatch(theList);
	    	int batch = 5;
	    	Schedule [] smallList = smallerList(theList, choice);
	    	Schedule [] tempList = new Schedule[batch];
	    	
	    	for(int i = 0; i < batch; i++)
	    	{//grabs the best 5 values from this small list
	    		int temp = pickBest(smallList);
	    		tempList[i] = smallList[temp];
	    		smallList[temp] = null;
	    	}
	    	//picks one of the 5 values from the small list to be the good parent
	    	
	    	int roll;
	    	
	    	roll = rng4.nextInt() % tempList.length;
	    	if(roll < 0) roll *= -1;
	    	
	    	Schedule chosen = tempList[roll];
	    	if(chosen == null) chosen = pickGood(theList);
	    	
	    	return chosen;
	    }//end pickGood
	    
	    private Schedule [] smallerList(Schedule [] bigList, int [] choice)
	    {
	    	Schedule [] smallList = new Schedule[choice.length];
	    	
	    	for(int i = 0; i < choice.length; i++)
	    	{
	    		smallList[i] = bigList[choice[i]];
	    	}
	    	
	    	return smallList;
	    }
	    
	    private int [] pickBatch(Schedule [] theList)
	    {//this function picks batch (15) random numbers between 0 and (scheduleMax -1)
	    	int batch = 15;
	    	int [] choice =  new int[batch];
	    	if(debug) System.out.println("Current Size: " + currentSize);
	    	
	    	for(int i = 0; i < batch; i++)
	    	{//grabs batch number of schedules, randomly picked, from the list
	    		int counter = 0;
	    		do{
	    		choice[i] = rng4.nextInt() % currentSize;	    		
	    		if(choice[i] < 0) choice[i] = choice[i] * -1;
	    		counter++;
	    		//if (choice[i] >= 100) choice[i]= choice[i] % scheduleMax;
	    		}while ((theList[choice[i]] == null) && (counter < runTimeRandom));	    		
	    		//if(debug) System.out.println("Batch Choice: " + choice[i]);
	    	}
	    	
	    	return choice;
	    }
	    
	    private void indexer()
	    {//adds the index to the parent course for each lab
	    	
	    	for(int i = 0; i < parser.coursesVector.size(); i++ ) 
	    	{
	    		Courses course = parser.coursesVector.get(i);
	    		
	    		for(int j = 0; j < course.getLabIndex().size(); j++)
	    		{
	    			parser.labsVector.get(course.getLabIndex().get(j)).setCIndex(i);
	    		}
	    	}
	    }
}//end SearchControl
