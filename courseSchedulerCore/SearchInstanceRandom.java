package courseSchedulerCore;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;


public class SearchInstanceRandom extends SearchInstanceAbstract{
	
	private final boolean debug = false;

	private boolean night13Added;
	
	public SearchInstanceRandom(int seed, CourseAndTimeSlotsData cat, Penalty pen)
	{
		seed = (int) (Math.pow(7,5) * seed % (Math.pow(2,31) - 1));
		rng = new Random(seed);
		courseAndTime = cat;
		child = new Schedule(courseAndTime, pen);
		night13Added = false;
		
		for(int i = 0; i < courseAndTime.getCourseSlotsVector().size(); i++)
		{
			TimeSlot slot = courseAndTime.getCourseSlotsVector().get(i);
			if ((slot.getStartTime().matches("11:00")) && (slot.getDay().startsWith("T")))
			{//find the 11:00 tuesday course slot, and store the index value
				tuesdayExcludeSlot = i;
			}//end if-then
			
			if (slot.getStartTime().equals("18:00") && slot.getDay().startsWith("T"))
			{
				night13Added = true;
			}
			
			if((tuesdayExcludeSlot != -1) && (night13Added))
			{//found what we are looking for so end the loop
				break;
			}
		}//end for-loop
	}//end constructor
	
	public Schedule assignTest()
	{//creates a single random valid schedule, for testing purposes
		Schedule output;
		int counter = 0;
		//do{
			output = assign();
			counter++;
		//}while ((output == null) && (counter < 10000));
		
		return output;
	}
	
	protected boolean assignSlot(boolean isCourse)
	{//this method assigns timeslots to courses
		boolean isValid = true;	
		int length;
		if (isCourse) length = courseAndTime.getCoursesVector().size();
		else length = courseAndTime.getLabsVector().size();
		int index = 0;
		
		while((index < length) && (isValid == true))
		{//main search loop, goes through the courses/labs to assign timeSlots as long it remains valid	
			
			//check for a specific assignment (hardcoded or partAssign)
			isValid = hardCode(index, isCourse);
			//check to see if the slot is already assigned	
			if (child.getSlot(index, isCourse) != null) 
			{
				index++;
				continue;
			}
						
			//pick a random assignment if possible and check validity
			if(isCourse) isValid = pickRandomCourse(index);
			else isValid = pickRandomLab(index);
			
			index++;
			if(debug) System.out.println("Slot Run " + index + " course? " + isCourse);
		}//end while loop (exit if schedule is invalid or all courses assigned a timeSlot)
		
		return isValid;
	}//end assignCourse
	
	private boolean pickRandomCourse(int index)
	{//this method will randomly choose a timeSlot for the course at the index
		boolean isCourse = true;
		
		//HARDCODE VALUE - NO LECTURE 11:00 - 12:30 on Tuesday DONE
		//HARDCODE VALUE - EVENING CLASSES DONE
		Vector<Integer> exclusion = excludeSlot(index, isCourse);
	
		boolean isValid = pickRandomMainLoop(index, isCourse, exclusion);	
		
		return isValid;
	}//end pickRandomCourse
	
	protected boolean pickRandomLab(int index)
	{//this method will randomly choose a timeSlot for the course at the index
		
		boolean isCourse = false;
		Vector<Integer> exclusion = excludeSlot(index, isCourse);
		boolean isValid = pickRandomMainLoop(index, isCourse, exclusion);
		return isValid;
	}
}
