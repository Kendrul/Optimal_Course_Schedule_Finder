package courseSchedulerCore;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;


public class SearchInstanceRandom {
	
	private final boolean debug = false;
	
	//strictly for testing
	private final boolean testValue = true;
	
	private Schedule child = null;
	private Parser parser;
	
	//private Boolean isValid = true;
	//private int value = -1;
	private Random rng;
	
	private int tuesdayExcludeSlot = -1;
	private boolean night13Added;
	
	public SearchInstanceRandom(int seed, Parser parse)
	{
		seed = (int) (Math.pow(7,5) * seed % (Math.pow(2,31) - 1));
		rng = new Random(seed);
		parser = parse;
		child = new Schedule(parser);
		night13Added = false;
		
		for(int i = 0; i < parser.courseSlotsVector.size(); i++)
		{
			TimeSlot slot = parser.courseSlotsVector.get(i);
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
	
	public Schedule assign()
	{//this method starts the process of assigning time slots to course and labs, then evaluates the result
		if(debug) System.out.println("Assignment started (Lecture Random)");
		boolean valid = assignSlot(true); //assign times to Lectures
		if(debug && valid) System.out.println("Assignment started (Lab Random)");
		if(valid) valid = assignSlot(false); //assign times to Labs
		if(debug && valid) System.out.println("Checking constraints (Random)");
		if(valid) 
		{
			valid = constr(); //check the hard constraints one more time
			//System.out.println(child);
		}
		if (valid) child.setValue(child.eval()); //evaluate the soft constraints
		
		if(valid) 
		{
			System.out.println(child);
			return child; //return the completed valid schedule
		}else
		{
			System.out.println(child);
			return null; //invalid schedule, return nothing
		}
	}//end assign
	
	private boolean assignSlot(boolean isCourse)
	{//this method assigns timeslots to courses
		boolean isValid = true;	
		int length;
		if (isCourse) length = parser.coursesVector.size();
		else length = parser.labsVector.size();
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
		int roll;
		boolean isValid = false;
		boolean isCourse = true;
		
		//HARDCODE VALUE - NO LECTURE 11:00 - 12:30 on Tuesday DONE
		//HARDCODE VALUE - EVENING CLASSES DONE
		Vector<Integer> exclusion = excludeSlot(index, isCourse);
	
		do {//TODO make more efficient
			
			//pick a random slot
			roll = rng.nextInt() % parser.courseSlotsVector.size();
			if (roll < 0) roll *= -1;
				
			if(!exclusion.contains(roll))
			{//if the slot has not been chosen yet
				//assign the slot to the course, check validity
				isValid = child.assign(index, parser.courseSlotsVector.get(roll), isCourse);
				//isValid = checkConstraints();
				//if choice was invalid, add to exclusion list and pick a new random spot
				if(!isValid) exclusion.add(roll);
			}
			//loop continues until a valid assignment is made OR no valid assignments possible
		} while ((isValid != true) && (exclusion.size() < parser.courseSlotsVector.size()));		
		
		return isValid;
	}//end pickRandomCourse
	
	private boolean pickRandomLab(int index)
	{//this method will randomly choose a timeSlot for the course at the index
		int roll;
		boolean isValid = false;
		boolean isCourse = false;
		Vector<Integer> exclusion = excludeSlot(index, isCourse);
			
		do {//TODO make more efficient
			
			//pick a random slot
			Labs target = parser.labsVector.get(index);
			roll = rng.nextInt() % parser.labSlotsVector.size();
			if (roll < 0) roll *= -1;
			
			if(!exclusion.contains(roll))
			{//if the slot has not been chosen yet
				//assign the slot to the course, check validity
				isValid = child.assign(index, parser.labSlotsVector.get(roll), isCourse);
				//isValid = checkConstraints();
				//if choice was invalid, add to exclusion list and pick a new random spot
				if(!isValid) 
					exclusion.add(roll);
			}
		} while ((isValid != true) && (exclusion.size() < parser.labSlotsVector.size()));		
		
		return isValid;
	}
	
	private boolean hardCode(int index, boolean isCourse)
	{//this method checks for certain restrictions such as partAssign, or other hard coded values
		boolean isValid = testValue;

		if(isCourse)
		{
			Courses course = parser.coursesVector.get(index);
			
			//for the CPSC 813/913 courses	
			if((course.getCourseNumber().equals("813") || course.getCourseNumber().equals("913")) && course.getDepartment().equals("CPSC"))
			{
				isValid = child.assign13(index, parser.labSlotsVector.get(course.getPartAssign().get(0))) && course.constr(child, index) && parser.labSlotsVector.get(course.getPartAssign().get(0)).constr(child, index, !isCourse);				
			}

			else if (course.getPartAssign().size() > 0)
			{//do the partial assignment
				isValid = child.assign(index, parser.courseSlotsVector.get(course.getPartAssign().get(0)), isCourse) && course.constr(child, index) && parser.courseSlotsVector.get(course.getPartAssign().get(0)).constr(child, index, isCourse);
			}

			//System.out.println(course);
		} 
		else //its a lab
		{
			Labs lab = parser.labsVector.get(index) ;
		
			if (lab.getPartAssign().size() > 0)
			{//do the partial assignment
				isValid = child.assign(index, parser.labSlotsVector.get(lab.getPartAssign().get(0)), isCourse) && lab.constr(child, index) && parser.labSlotsVector.get(lab.getPartAssign().get(0)).constr(child, index, isCourse);
			}		

			//System.out.println(lab);
		}//end if-else (not a lecture)		
		
		return isValid;
	}
	
	private Vector<Integer> excludeSlot(int index, boolean isCourse)
	{//this method adds any specific excluded slots
		Vector<Integer> exclusion = new Vector<>();
		
		if(isCourse)
		{//Course specific exclusions (Tuesday and Evening)
			//No Courses Allowed on Tuesday at 11:00
			exclusion.add(tuesdayExcludeSlot);			
			
			if(parser.coursesVector.get(index).getLectureNumber().startsWith("9"))
			{//for evening classes
				for(int i = 0; i < parser.courseSlotsVector.size(); i++)
				{
					TimeSlot slot = parser.courseSlotsVector.get(i);
					
					if(!((slot.getStartTime().matches("17:00")) || (slot.getStartTime().matches("18:00")) || (slot.getStartTime().matches("19:00")) || (slot.getStartTime().matches("20:00")) || (slot.getStartTime().matches("18:30"))))
					{//if its not an evening slot, add to the exclusion list
						exclusion.add(i);
					}
				}//end for loop
			}			
		}else //end if-then
		{
			if(parser.labsVector.get(index).getLabNumber().startsWith("9"))
			{//for evening classes
				for(int i = 0; i < parser.labSlotsVector.size(); i++)
				{
					TimeSlot slot = parser.labSlotsVector.get(i);
					
					if(!((slot.getStartTime().matches("17:00")) || (slot.getStartTime().matches("18:00")) || (slot.getStartTime().matches("19:00")) || (slot.getStartTime().matches("20:00")) || (slot.getStartTime().matches("18:30"))))
					{//if its not an evening slot, add to the exclusion list
						exclusion.add(i);
					}
				}//end for loop
			}
		}//end if-else	
		
		return exclusion;
	}

	public boolean constr()
	{//TODO
		boolean valid = child.constr();
		return valid;
	}
}
