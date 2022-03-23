package courseSchedulerCore;

import java.util.Random;
import java.util.Vector;

public abstract class SearchInstanceAbstract {
	protected final boolean debug = true;
	protected final boolean isRandomSearch = false;
	protected final boolean isGeneticSearch = false;
	
	//strictly for testing
	protected final boolean testValue = true;
	
	protected Schedule child;
	protected CourseAndTimeSlotsData courseAndTime;
	protected Penalty penalty;
	
	protected Random rng;
	protected int tuesdayExcludeSlot = -1;
	
	public SearchInstanceAbstract()
	{
		
	}
	
	public SearchInstanceAbstract(int seed, CourseAndTimeSlotsData cat, Penalty pen) {
		courseAndTime = cat;
		penalty = pen;
		child = new Schedule(courseAndTime, pen);
		seed = (int) (Math.pow(7,5) * seed % (Math.pow(2,31) - 1));
		rng = new Random(seed);
	}
	
	public Schedule assign() 
	{//this method starts the process of assigning time slots to course and labs, then evaluates the result

		if(debug && isRandomSearch) System.out.println("Assignment started (Lecture Random)");
		if(debug && isGeneticSearch) System.out.println("Assignment started (Lecture Genetic)");
		boolean valid = assignSlot(true); //assign times to Lectures
		
		if(debug && valid && isRandomSearch) System.out.println("Assignment started (Lab Random)");
		if(debug && valid && isGeneticSearch) System.out.println("Assignment started (Labs Genetic)");
		if(valid) valid = assignSlot(false); //assign times to Labs
		
		if(debug && valid && isRandomSearch) System.out.println("Checking constraints (Random)");
		if(debug && valid&& isGeneticSearch) System.out.println("Checking Constraints (Genetic)");
		
		if(valid) valid = constr(); //check the hard constraints one more time
		if (valid) child.setValue(child.eval()); //evaluate the soft constraints
		
		if(valid) 
		{
			if (isRandomSearch) System.out.println(child);
			return child; //return the completed valid schedule
		}else
		{
			if (isRandomSearch) System.out.println(child);
			return null; //invalid schedule, return nothing
		}
	}
	
	protected abstract boolean assignSlot(boolean isCourse);
	
	protected boolean pickRandomMainLoop(int index, boolean isCourse, Vector<Integer> exclusion)
	{
		int roll;
		boolean isValid = false;
		
		do {//TODO make more efficient
			
			//pick a random slot
			if (isCourse) {
				roll = rng.nextInt() % courseAndTime.getCourseSlotsVector().size();
				if (roll < 0) roll *= -1;
				if(!exclusion.contains(roll))
				{//if the slot has not been chosen yet
					//assign the slot to the course, check validity
					isValid = child.assign(index, courseAndTime.getCourseSlotsVector().get(roll), isCourse);
					//isValid = checkConstraints();
					//if choice was invalid, add to exclusion list and pick a new random spot
					if(!isValid) exclusion.add(roll);
				}
			} else //isLab
			{//pick a random slot
				roll = rng.nextInt() % courseAndTime.getLabSlotsVector().size();
				if (roll < 0) roll *= -1;
				
				if(!exclusion.contains(roll))
				{//if the slot has not been chosen yet
					//assign the slot to the course, check validity
					isValid = child.assign(index, courseAndTime.getLabSlotsVector().get(roll), isCourse);
					//isValid = checkConstraints();
					//if choice was invalid, add to exclusion list and pick a new random spot
					if(!isValid) exclusion.add(roll);
				}
			}//end else
			//loop continues until a valid assignment is made OR no valid assignments possible
		} while ((isValid != true) && (exclusion.size() < courseAndTime.getCourseSlotsVector().size()));		
		
		return isValid;
	}
	
	protected boolean pickRandomLab(int index)
	{//this method will randomly choose a timeSlot for the course at the index

		boolean isCourse = false;
		Vector<Integer> exclusion = excludeSlot(index, isCourse);
		boolean isValid = pickRandomMainLoop(index, isCourse, exclusion);		
		
		return isValid;
	}
	
	protected boolean hardCode(int index, boolean isCourse)
	{//this method checks for certain restrictions such as partAssign, or other hard coded values
		boolean isValid = courseAndTime.hardCode(index, isCourse, child);
		return isValid;
	}
	
	protected Vector<Integer> excludeSlot(int index, boolean isCourse)
	{//this method adds any specific excluded slots
		Vector<Integer> exclusion = new Vector<>();
		
		if(isCourse)
		{//Course specific exclusions (Tuesday and Evening)
			//No Courses Allowed on Tuesday at 11:00
			exclusion.add(tuesdayExcludeSlot);			
			
			if(courseAndTime.getCoursesVector().get(index).getLectureNumber().startsWith("9"))
			{//for evening classes
				for(int i = 0; i < courseAndTime.getCourseSlotsVector().size(); i++)
				{
					TimeSlot slot = courseAndTime.getCourseSlotsVector().get(i);
					
					if(!((slot.getStartTime().matches("17:00")) || (slot.getStartTime().matches("18:00")) || (slot.getStartTime().matches("19:00")) || (slot.getStartTime().matches("20:00")) || (slot.getStartTime().matches("18:30"))))
					{//if its not an evening slot, add to the exclusion list
						exclusion.add(i);
					}
				}//end for loop
			}			
		}else //end if-then
		{
			if(courseAndTime.getLabsVector().get(index).getLabNumber().startsWith("9"))
			{//for evening classes
				for(int i = 0; i < courseAndTime.getLabSlotsVector().size(); i++)
				{
					TimeSlot slot = courseAndTime.getLabSlotsVector().get(i);
					
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
}//end class
