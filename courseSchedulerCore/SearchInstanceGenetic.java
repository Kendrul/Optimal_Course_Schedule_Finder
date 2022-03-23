package courseSchedulerCore;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;


public class SearchInstanceGenetic extends SearchInstanceAbstract{
		
	//strictly for testing
	private final boolean testValue = true;
	
	private Schedule parentA = null;
	private Schedule parentB = null;
	
	protected final boolean isGeneticSearch = true;

	/**
	 * 
	 * @param pA good parent schedule
	 * @param pB bad parent schedule
	 * @param inputRNG randomNumberGenerator for picking parents/courses
	 * @param parse link to the courseAndTime with all the data
	 */
	public SearchInstanceGenetic(Schedule pA, Schedule pB, int seed, CourseAndTimeSlotsData cat, Penalty pen)
	{
		parentA = pA;
		parentB = pB;
		seed = (int) (Math.pow(7,5) * seed % (Math.pow(2,31) - 1));
		rng = new Random(seed);
		courseAndTime = cat;
		penalty = pen;
		child = new Schedule(courseAndTime, penalty);
		
		for(int i = 0; i < courseAndTime.getCourseSlotsVector().size(); i++)
		{
			TimeSlot slot = courseAndTime.getCourseSlotsVector().get(i);
			if ((slot.getStartTime().matches("11:00")) && (slot.getDay().startsWith("T")))
			{//find the 11:00 tuesday course slot, and store the index value
				tuesdayExcludeSlot = i;
			}//end if-then
			
			/*if ((slot.getStartTime() == "18:00") && (slot.getDay() == "TR"))
			{
				night13Added = true;
			}*/
			
			//if((tuesdayExcludeSlot != -1) && (night13Added))
			if(tuesdayExcludeSlot != -1)
			{//found what we are looking for so end the loop
				break;
			}
		}//end for-loop
	}//end constructor
	
	protected boolean assignSlot(boolean isCourse)
	{//this method assigns timeslots to courses
		boolean isValid = true;	
		int length;
		if (isCourse) length = courseAndTime.getCoursesVector().size();
		else length = courseAndTime.getLabsVector().size();
		int index = 0;
		int choice;
		boolean valid; 
		
		while((index < length) && (isValid != false))
		{
			//check for partAssign or 813/913
			isValid = hardCode(index, isCourse);
			//check to see if the slot is already assigned
			if (child.getSlot(index, isCourse) != null) 
			{
				index++;
				continue;
			}
			
			choice = pickParent(isCourse, index);
			
			//assign the time slot from the chosen parent (A = 0, B = 1, parents same = 2 -> choose parent A)
			if (choice == 1) valid = child.assign(index, parentB.getSlot(index, isCourse), isCourse);
			else valid = child.assign(index, parentA.getSlot(index, isCourse), isCourse);
			
			//valid = checkConstraints();
			
			if ((!valid) && (choice != 2))
			{//first parent choice was not valid, check other
				if (choice == 1) valid = child.assign(index, parentA.getSlot(index, isCourse), isCourse);
				else valid = child.assign(index, parentB.getSlot(index, isCourse), isCourse);
				
				//check validity, if still valid then next assignment
				//valid = checkConstraints();
				if(valid){
					index++;
					continue;
				}
			} else if (valid) {
				index++;
				continue;
			}
			
			//parents were invalid, pick a random if possible and check validity
			if(isCourse) isValid = pickRandomCourse(index, choice);
			else isValid = pickRandomLab(index, choice);
			
			index++;
		}//end while loop (exit if schedule is invalid or all courses assigned a timeSlot)
		
		return isValid;
	}//end assignCourse
	
	
	private int pickParent(boolean isCourse, int index)
	{//this method chooses a parent to take a value from, or a random spot
		
		//if parents are the same, just go with parent A as the default
		int roll = 2;
		
		if (parentA.getSlot(index, isCourse) != parentB.getSlot(index, isCourse))
		{//parents are not the same, randomly pick one
			
			roll = rng.nextInt() % 2;
			if (roll < 0) roll *= -1;
		} 
		
		return roll; //parent A = 0, parent B = 1, parents are the same = 2
	}//end picks a parent, or a random slot
	
	private boolean pickRandomCourse(int index, int choice)
	{//this method will randomly choose a timeSlot for the course at the index
		boolean isCourse = true;
		Vector<Integer> exclusion = excludeSlot(index, isCourse);
		
		//add the parents to the exclusion list
		exclusion.add(courseAndTime.getCourseSlotsVector().indexOf(parentA));
		if(choice != 2) exclusion.add(courseAndTime.getCourseSlotsVector().indexOf(parentB));
				
		boolean isValid = pickRandomMainLoop(index, isCourse, exclusion);	
		return isValid;
	}//end pickRandomCourse
	
	private boolean pickRandomLab(int index, int choice)
	{//this method will randomly choose a timeSlot for the course at the index
		
		boolean isCourse = false;
		Vector<Integer> exclusion = excludeSlot(index, isCourse);
		
		//add the parents to the exclusion list
		exclusion.add(courseAndTime.getLabSlotsVector().indexOf(parentA));
		if(choice != 2) exclusion.add(courseAndTime.getLabSlotsVector().indexOf(parentB));
		boolean isValid = pickRandomMainLoop(index, isCourse, exclusion);	
		return isValid;
	}
	
}
