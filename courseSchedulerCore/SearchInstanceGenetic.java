package courseSchedulerCore;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;


public class SearchInstanceGenetic {
		
	//strictly for testing
	private final boolean debug = false;
	private final boolean testValue = true;
	
	private Schedule parentA = null;
	private Schedule parentB = null;
	private Schedule child = null;
	private Parser parser;
	
	//private Boolean isValid = true;
	//private int value = -1;
	private Random rng;
	private int tuesdayExcludeSlot = -1;
	//private boolean night13Added;
	
	/**
	 * 
	 * @param pA good parent schedule
	 * @param pB nad parent schedule
	 * @param inputRNG randomNumberGenerator for picking parents/courses
	 * @param parse link to the parser with all the data
	 */
	public SearchInstanceGenetic(Schedule pA, Schedule pB, int seed, Parser parse)
	{
		parentA = pA;
		parentB = pB;
		seed = (int) (Math.pow(7,5) * seed % (Math.pow(2,31) - 1));
		rng = new Random(seed);
		parser = parse;
		child = new Schedule(parser);
		
		for(int i = 0; i < parser.courseSlotsVector.size(); i++)
		{
			TimeSlot slot = parser.courseSlotsVector.get(i);
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
	
	public Schedule assign()
	{//this method starts the process of assigning time slots to course and labs, then evaluates the result
		if(debug) System.out.println("Assignment started (Lecture Genetic)");
		boolean valid = assignSlot(true); //assign times to Lectures
		if(debug && valid) System.out.println("Assignment started (Labs Genetic)");
		if(valid) valid = assignSlot(false); //assign times to Labs
		if(debug && valid) System.out.println("Checking Constraints (Genetic)");
		if(valid) valid = constr(); //check the hard constraints one more time
		if (valid) child.setValue(child.eval()); //evaluate the soft constraints
		
		if(valid) 
		{
			return child; //return the completed valid schedule
		}else
		{
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
		int roll;
		boolean isValid = false;
		boolean isCourse = true;
		Vector<Integer> exclusion = excludeSlot(index, isCourse);
		
		//add the parents to the exclusion list
		exclusion.add(parser.courseSlotsVector.indexOf(parentA));
		if(choice != 2) exclusion.add(parser.courseSlotsVector.indexOf(parentB));
				
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
		} 

		else //its a lab
		{
			Labs lab = parser.labsVector.get(index);
		
			if (lab.getPartAssign().size() > 0)
			{//do the partial assignment
				isValid = child.assign(index, parser.labSlotsVector.get(lab.getPartAssign().get(0)), isCourse) && lab.constr(child, index) && parser.labSlotsVector.get(lab.getPartAssign().get(0)).constr(child, index, isCourse);
			}		
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
	
	private boolean pickRandomLab(int index, int choice)
	{//this method will randomly choose a timeSlot for the course at the index
		int roll;
		boolean isValid = false;
		boolean isCourse = false;
		Vector<Integer> exclusion = excludeSlot(index, isCourse);
		
		//add the parents to the exclusion list
		exclusion.add(parser.labSlotsVector.indexOf(parentA));
		if(choice != 2) exclusion.add(parser.labSlotsVector.indexOf(parentB));
		
		do {//TODO make more efficient
			
			//pick a random slot
			roll = rng.nextInt() % parser.labSlotsVector.size();
			if (roll < 0) roll *= -1;
			if(!exclusion.contains(roll))
			{//if the slot has not been chosen yet
				//assign the slot to the course, check validity
				isValid = child.assign(index, parser.labSlotsVector.get(roll), isCourse);
				//isValid = checkConstraints();
				//if choice was invalid, add to exclusion list and pick a new random spot
				if(!isValid) exclusion.add(roll);
			}
		} while ((isValid != true) && (exclusion.size() < parser.labSlotsVector.size()));		
		
		return isValid;
	}
	
	public boolean constr()
	{//TODO
		boolean valid = child.constr();
		return valid;
	}
}
