package edu.maine.graphalgorithms;
/*
 * Started on Apr 25, 2005
 * 
 * Munkres-Kuhn (Hungarian) Algorithm Edu Version: 0.11
 * 
 * Konstantinos A. Nedas                     
 * Department of Spatial Information Science & Engineering
 * University of Maine, Orono, ME 04469-5711, USA
 * kostas@spatial.maine.edu
 * http://www.spatial.maine.edu/~kostas       
 *
 * This Java class implements the Hungarian algorithm [a.k.a Munkres' algorithm,
 * a.k.a. Kuhn algorithm, a.k.a. Assignment problem, a.k.a. Marriage problem,
 * a.k.a. Maximum Weighted Maximum Cardinality Bipartite Matching].
 *
 * This is an educational version of the algorithm, intended to help 
 * those who would like to understand how the algorithm internally works.
 * [I do NOT explain WHY the algorithm does the things it does, just
 * WHAT it does.] If you want to get rid of the comments and tutorial
 * character of this program, get the clean Hungarian algorithm from
 * the same website you got this one (http://www.spatial.maine.edu/~kostas/dev/soft/munkres.htm).
 * The clean version can be used as a method call from within any main
 * or other function.
 * 
 * Any comments, corrections, or additions would be much appreciated. 
 * Credit due to professor Bob Pilgrim for providing an online copy of the
 * pseudocode for this algorithm (http://216.249.163.93/bob.pilgrim/445/munkres.html)
 * 
 * Feel free to redistribute this source code, as long as this header--with
 * the exception of sections in brackets--remains as part of the file.
 * 
 * Requirements: JDK 1.5.0_01 or better.
 * [Created in Eclipse 3.1M6 (www.eclipse.org).]
 * 
 */

 import java.util.*;
 
public class HungarianAlgorithmEdu {
	
	//********************************//
	//METHODS FOR CONSOLE INPUT-OUTPUT//
	//********************************//
	
	public static int readInput(String prompt)	//Reads input,returns double.
	{
		Scanner in = new Scanner(System.in);
		System.out.print(prompt);
		int input = in.nextInt();
		return input;
	}
	public static void insertLines(int blancLines)	//Inserts <blancLine> lines.
	{
		for (int i=0; i<blancLines; i++)
		{System.out.println();}
	}
	public static void printStepHeader(String stepTitle, int blancLinesAfter)	//Format step headers.
	{
		String stars = "";
		for (int i=0; i<(stepTitle.length()+2); i++)	{stars = stars + "*";}
		System.out.print(stars + "\n" + "*" + stepTitle + "*" + "\n" + stars + "\n");
		insertLines(blancLinesAfter);
	}
	public static void print2DArray 	//Prints 2-D double array.
	(double[][] array, String elementFormatter, String title, int blancLinesAfter)
	{
		System.out.println(title);
		for (int i=0; i<array.length; i++)
		{
			for (int j=0; j<array[i].length; j++)
				{System.out.printf(elementFormatter, array[i][j]);}
			System.out.println();
		}
		insertLines(blancLinesAfter);
	}
	public static void print2DArrayInt //Prints a 2-D int array
	(int[][] array, String elementFormatter, String title, int blancLinesAfter)
	{
		System.out.println(title);
		for (int i=0; i<array.length; i++)
		{
			for (int j=0; j<array[i].length; j++)
				{System.out.printf(elementFormatter, array[i][j]);}
			System.out.println();
		}
		insertLines(blancLinesAfter);
	}
	public static void printPath //Special print method for the path array.
	(int[][] array, int numOfRows, String elementFormatter, String title, int blancLinesAfter)
	{
		System.out.println(title);
		for (int i=0; i<numOfRows; i++)
		{
			for (int j=0; j<array[i].length; j++)
				{System.out.printf(elementFormatter, (array[i][j]+1));}
			System.out.println();
		}
		insertLines(blancLinesAfter);
	}
	public static void print1DArrayInt	//Prints a 1-D int array.
	(int[] array, String formatter, String title, int blancLinesAfter)
	{
		System.out.println(title);
		for (int i=0; i<array.length; i++)
			{System.out.printf(formatter, array[i]);}
		insertLines(blancLinesAfter);
	}
	public static double getTime(double start, double end) //Calculates elapsed time in seconds.
	{
		double total = (end - start)/1000000000.0; //Convert time in seconds
		return total;
	}
	public static void printTime(double time)	//Formats time output.
	{
		String timeElapsed = "";
		int days = (int)Math.floor(time)/(24 * 3600);
		int hours = (int)Math.floor(time%(24*3600))/(3600);
		int minutes = (int)Math.floor((time%3600)/60);
		int seconds = (int)Math.round(time%60);
				
		if (days > 0)
			timeElapsed = Integer.toString(days) + "d:";
		if (hours > 0)
			timeElapsed = timeElapsed + Integer.toString(hours) + "h:";
		if (minutes > 0)
			timeElapsed = timeElapsed + Integer.toString(minutes) + "m:";
		
		timeElapsed = timeElapsed + Integer.toString(seconds) + "s";
		System.out.print(timeElapsed);
	}
	
	//*******************************************//
	//METHODS THAT PERFORM ARRAY-PROCESSING TASKS//
	//*******************************************//
	
	public static void generateRandomArray	//Generates random 2-D array.
	(double[][] array, String randomMethod)	
	{
		Random generator = new Random();
		for (int i=0; i<array.length; i++)
		{
			for (int j=0; j<array[i].length; j++)
			{
				if (randomMethod.equals("random"))
					{array[i][j] = generator.nextDouble();}
				if (randomMethod.equals("gaussian"))
				{
						array[i][j] = generator.nextGaussian()/4;		//range length to 1.
						if (array[i][j] > 0.5) {array[i][j] = 0.5;}		//eliminate outliers.
						if (array[i][j] < -0.5) {array[i][j] = -0.5;}	//eliminate outliers.
						array[i][j] = array[i][j] + 0.5;				//make elements positive.
				}
			}
		}			
	}
	public static double findLargest		//Finds the largest element in a positive array.
	(double[][] array)
	//works for arrays where all values are >= 0.
	{
		double largest = 0;
		for (int i=0; i<array.length; i++)
		{
			for (int j=0; j<array[i].length; j++)
			{
				if (array[i][j] > largest)
				{
					largest = array[i][j];
				}
			}
		}
			
		return largest;
	}
	public static double[][] transpose		//Transposes a double[][] array.
	(double[][] array)	
	{
		double[][] transposedArray = new double[array[0].length][array.length];
		for (int i=0; i<transposedArray.length; i++)
		{
			for (int j=0; j<transposedArray[i].length; j++)
			{transposedArray[i][j] = array[j][i];}
		}
		return transposedArray;
	}
	public static double[][] copyOf			//Copies all elements of an array to a new array.
	(double[][] original)	
	{
		double[][] copy = new double[original.length][original[0].length];
		for (int i=0; i<original.length; i++)
		{
			//Need to do it this way, otherwise it copies only memory location
			System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
		}
		
		return copy;
	}
		
	//**********************************//
	//METHODS OF THE HUNGARIAN ALGORITHM//
	//**********************************//
	
	public static int[][] hgAlgorithm (double[][] array, String sumType)
	{
		double[][] cost = copyOf(array);	//Create the cost matrix.
				
		if (sumType.equalsIgnoreCase("min"))
		{
			System.out.println("Since you want to find the assignment\n" +
								"that minimizes the sum, the matrix above\n" +
								"is the cost matrix.\n");
		}
		if (sumType.equalsIgnoreCase("max"))	//Then array is weight array. Must change to cost.
		{
			double maxWeight = findLargest(cost);
			
			System.out.println("Since you want to find the assignment\n" +
								"that maximizes the sum, the matrix above\n" +
								"is a weight matrix. However, the Hungarian\n" +
								"algorithm always minimises the sum. Hence,\n" +
								"we need to convert the matrix to a cost\n" +
								"matrix. This is easy. Any weight can be\n" +
								"converted to a cost by subtracting from a\n" +
								"quantity larger than the larger weight.\n" +
								"This is precisely what we do.\n");
			
			System.out.printf("The largest weight is: %.1f\n\n", maxWeight);
								
			for (int i=0; i<cost.length; i++)		//Generate cost by subtracting.
			{
				for (int j=0; j<cost[i].length; j++)
				{
					cost [i][j] = (maxWeight - cost [i][j]);
				}
			}
			print2DArray(cost, "%.1f\t", "The cost matrix (generated from weight matrix) is:" , 1);
		}
				
		double maxCost = findLargest(cost);		//Find largest cost matrix element (needed for step 6).
		
		//Now we need to declare and initialize several other arrays that help in the calculations.
		//These are:
		//The mask array (dimensions same as cost).
		int[][] mask = new int[cost.length][cost[0].length];	
		
		//The row covering vector (dimension rowsOfCostx1).
		int[] rowCover = new int[cost.length];
		
		//The column covering vector (dimension colsOfCostx1).
		int[] colCover = new int[cost[0].length];
		
		//This next one is used to remember the position of a zero retrieved from step 4 that
		//needs to be passed to step 5. It has to do with the path array defined in step 5 and is
		//basically the hardest part of the algorithm to understand. To get an idea you need to read
		//what is happening inside steps 4 and 5.
		//This one should be defined here as an array and not as 2 independent variables
		//because in Java, simple variables that get sent to methods and change in the methods
		//DO NOT change in the main program whereas arrays DO.
		int[] zero_RC = new int[2];
		
		//Step number: its value guides the algorithm through the various steps.
		int step = 1;
		
		//When this changes to "true" the assignment would be completed.
		boolean done = false;	
				
		//We have defined all we need and it is time to start the main program.
		printStepHeader("**** HUNGARIAN ALGORITHM STARTING *****", 1);
		
		//Create the main execution loop
		while (done == false)
		{ 
			switch (step)
		    {
				case 1:
					step = hg_step1(step, cost);     
		    	    break;
		    	        	    
		    	case 2:
		    	    step = hg_step2(step, cost, mask, rowCover, colCover);
					break;
		    	    
		    	case 3:
		    	    step = hg_step3(step, mask, colCover);
					break;
		    	    
		    	case 4:
		    	    step = hg_step4(step, cost, mask, rowCover, colCover, zero_RC);
					break;
		    	    
		    	case 5:
					step = hg_step5(step, mask, rowCover, colCover, zero_RC);
					break;
		    	    
		    	case 6:
		    	   	step = hg_step6(step, cost, rowCover, colCover, maxCost);
					
					break;
		    	    
		    	case 7:
		    	    System.out.println("*********************************************");
					System.out.println ("\nAll calculations completed!\nGetting out of main loop...\n");
		    	    done=true;
		    	    break;
		    }
		}//end while
		
		System.out.println ("Algorithm out of main loop.\nProcess exited with code " + step + ".\n");
		printStepHeader("**** HUNGARIAN ALGORITHM FINISHED! *****", 1);
				
		int[][] assignment = new int[array.length][2];	//Create the returned array.
		for (int i=0; i<mask.length; i++)
		{
			for (int j=0; j<mask[i].length; j++)
			{
				if (mask[i][j] == 1)
				{
					assignment[i][0] = i;
					assignment[i][1] = j;
				}
			}
		}
		
		return assignment;
		
	}
	public static int hg_step1(int step, double[][] cost)
	{
		//What STEP 1 does:
		//For each row of the cost matrix, find the smallest element
		//and subtract it from from every other element in its row. 
	    
	   	printStepHeader("Step 1 (executed only once)", 1);
		System.out.println( "Step 1 finds the smallest value in each\n" +
							"row of the cost matrix and subtracts it\n" +
							"from every other value in the same row.\n");
		print2DArray(cost, "%.1f\t", "The cost matrix before step 1 is:", 1);
		
		double minval;
		
	   	for (int i=0; i<cost.length; i++)	
	   	{									
	   	    minval=cost[i][0];
	   	    //First loop on columns finds the minimum value in a row
	   	    for (int j=0; j<cost[i].length; j++)
	   	    {
	   	        if (minval>cost[i][j])
	   	        {
	   	            minval=cost[i][j];
	   	        }
			}
			System.out.printf("The minimum value in row %d is %.1f.\n", (i+1), minval);
						
			//Second loop on columns subtracts the minimum element on a row from
	   	    //all other elements of the row.
			for (int j=0; j<cost[i].length; j++)
	   	    {
	   	        cost[i][j]=cost[i][j]-minval;
	   	    }
		}
	   	insertLines(1);
		print2DArray(cost, "%.1f\t", "After subtracting, the cost matrix becomes:", 1);
			    
		step=2;
		System.out.println ("Step 1 completed. Going to step " + step + ".\n\n");
	   			
		return step;
	}
	public static int hg_step2(int step, double[][] cost, int[][] mask, int[]rowCover, int[] colCover)
	{
		//What STEP 2 does:
		//As the calcs proceed, the cost matrix will start having many 0s. Some of these
		//we need to characterize as starred zeros and others as prime. This is exactly
		//the information that the mask matrix maintains. This matrix has the same dimensions
		//as the cost matrix. A 0 element in cost will have the same position in mask and its
		//value in mask will be 1 if the zero is starred, or 2 if the zero is primed.
		//Step 2 is concerned with finding and marking starred zeros only.
		//It checks every element of cost to see if it is a zero and if its row or column
		//are not covered (rowCover and colCover arrays). If these conditions are met,
		//the zero is starred so mask[i][j] becomes 1 for this zero element of cost.
		//In addition, the row i and the column j are covered (this covering is needed
		//internally in this step to work correctly). Before leaving step 2, the covers need
		//to be uncovered so as to help us count the amount of starred zeros in step 3.
	     
		printStepHeader("Step 2 (executed only once)", 1);
		System.out.println( "Step 2 examines the cost matrix and sets\n" +
							"as starred the 1st uncovered zero it finds\n" +
							"(i.e., its value becomes 1 in the mask).\n" +
							"It also covers the row and col of this zero.\n" +
							"Then it repeats itself until no uncovered\n" +
							"zeros are found.\n");
		print2DArray(cost, "%.1f\t", "Cost matrix passed to step 2:", 0);		
		print1DArrayInt(rowCover, "%d   ", "Row cover matrix passed to step 2 is:", 1);
		print1DArrayInt(colCover, "%d   ", "Col cover matrix passed to step 2 is:", 2);
		
		//Find starred zeros
	    for (int i=0; i<cost.length; i++)
	    {
	        for (int j=0; j<cost[i].length; j++)
	        {
	            if ((cost[i][j]==0) && (colCover[j]==0) && (rowCover[i]==0))
	            {
	                System.out.println("The element cost(" + (i+1) + "," + (j+1) + ") is 0...also\n" +
										"row " + (i+1) + " is uncovered and so is col " + (j+1) +".");
					mask[i][j]=1;
					System.out.println("So we set mask(" + (i+1) + "," + (j+1) + ") to 1 (starred).");
	                colCover[j]=1;
	                rowCover[i]=1;
					print2DArrayInt(mask, "%d\t", "Now the mask matrix becomes:", 0);
					print1DArrayInt(rowCover, "%d   ", "the rowCover matrix becomes:", 1);
					print1DArrayInt(colCover, "%d   ", "and the colCover matrix becomes:", 2);
				}
	        }
	    }
		System.out.println("\nNo other uncovered zeros were found\n" +
							"in the cost matrix.\n" +
							"Resetting Row cover and Col cover...\n");
							
		//Reset the row and column vectors
	    clearCovers(rowCover, colCover);
		System.out.println("Did it.");
		print1DArrayInt(rowCover, "%d   ", "Row cover matrix resetted:", 1);
		print1DArrayInt(colCover, "%d   ", "Col cover matrix resetted:", 2);
			    
		step=3;
		System.out.println ("Step 2 completed. Going to step " + step + ".\n\n");
	   		
		return step;
	}
	public static int hg_step3(int step, int[][] mask, int[] colCover)
	{
		//What STEP 3 does:
		//Step 3 works on the mask matrix only. It examines each column and if
		//the column has a starred zero, then it covers it. If k columns are
		//covered, where k is min(rowsOfInputtedArray, colsOfInputtedArray),then
		//we got a complete set of unique assignments and we are done. It means
		//we have found and starred k independent zeros. However we can set
		//k = rowsOfInputtedArray because we have transposed the matrix already
		//so that the rows are less than the columns.
		//If k columns are not covered, then we go to Step 4.
		
		printStepHeader("Step 3", 1);
		System.out.println( "Step 3 covers columns of starred zeros.\n" +
							"If " + mask.length + " columns are covered we are done.\n" +
							"(" + mask.length + " because rows of cost matrix are " + mask.length + ".)\n" +
							"If not, we need to proceed to step 4.\n");
					
		print2DArrayInt(mask, "%d\t", "Mask matrix passed to step 3:", 1);
		
		//Cover columns containing starred zeros
	    for (int i=0; i<mask.length; i++)
	    {
	        for (int j=0; j<mask[i].length; j++)
	        {
	            if (mask[i][j] == 1)
	            {
	                colCover[j]=1;
				}
	        }
	    }
	    
		//This block used only to print what columns are covered in increasing sequence
		for (int j=0; j<colCover.length; j++)
		{
			if (colCover[j] == 1)
			{
				System.out.println("Column " + (j+1) + " had a starred zero and was covered.");
			}
		}
		
		print1DArrayInt(colCover, "%d   ", "\nThe Col cover matrix after step 3 becomes:", 2);
		
		//Count number of covered columns
		int count=0;
		for (int j=0; j<colCover.length; j++)
	    {
	        count=count+colCover[j];
	    }
	    //Branch execution to end or to Step 4
		if (count>=mask.length)	//Should be cost.length but ok, because mask has same dimensions.
		{
	        step=7;
	        System.out.println("There are " + count + " covered columns,\n" +
	        					"equal to the required " +mask.length + 
	        					".\nThe problem has been solved.\n");
	    }
	    else
	    {
	        step=4;
	        if (count == 1)
			{
				System.out.println("There is " + count + " covered column,\n"+
									"less than the required " + mask.length + 
									".\nWe need to go to step 4.\n");
			}
	        else
			{
				System.out.println("There are " + count + " covered columns,\n"+
									"less than the required " + mask.length + 
									".\nWe need to go to step 4.\n");
			}
			System.out.println ("Step 3 completed. Going to step " + step + ".\n\n");
		}
	    
		return step;
	}
	public static int hg_step4(int step, double[][] cost, int[][] mask, int[] rowCover, int[] colCover, int[] zero_RC)
	{
		//What STEP 4 does:
		//Step 4 begins by checking for uncovered zeros in the cost matrix (i.e., finds a zero in
		//the cost matrix, then checks if its row and col are not covered in the row Cover and 
		//col Cover arrays respectively). If all these conditions are met then the uncovered zero
		//is primed (i.e., the corresponding element in the mask matrix is set to 2).
		//Then, step 4 checks to see if there is a starred zero in the row that contains the primed
		//zero (the one that was just primed). There are 2 alternatives:
		//a. If there is a starred zero, then cover this row and uncover the column containing
		//	 the starred zero. Repeat step 4 from the beginning until there are no uncovered
		//   zeros left in the cost matrix in which case we need to go to step 6.
		//b. If there is no starred zero in the row containing the primed zero then save the
		//   location of this primed zero (so that we can tell step 5 where the last discovered
		//   prime zero is) and go to step 5.
		
		printStepHeader("Step 4", 1);
		System.out.println( "Step 4 finds uncovered zeros in the cost\n" +
							"matrix and primes them in the mask matrix\n");
																
		print2DArrayInt(mask, "%d\t", "Mask matrix passed to step 4:", 0);
		print1DArrayInt(rowCover, "%d   ", "Row cover matrix passed to step 4:", 1);
		print1DArrayInt(colCover, "%d   ", "Col cover matrix passed to step 4:", 1);
		System.out.println();	
		int[] row_col = new int[2];	//Holds row and col of uncovered zero.
		boolean done = false;
		while (done == false)
		{
			row_col = findUncoveredZero(row_col, cost, rowCover, colCover);
			if (row_col[0] == -1)
			{
				System.out.println("All zeros are covered. Going to step 6.\n");
				done = true;
				step = 6;
			}
			else
			{
				System.out.println("There is an uncovered zero at: cost(" +
									(row_col[0]+1) + "," + (row_col[1]+1) + ").");
				
				mask[row_col[0]][row_col[1]] = 2;	//Prime the found uncovered zero.
				
				System.out.println("Therefore, element cost(" + 
									(row_col[0]+1) + "," + (row_col[1]+1) + ") is primed.");
				print2DArrayInt(mask, "%d\t", "Mask matrix has now become:", 1);
							
				//Now we try to find if there is a starred zero in the same row as 
				//the zero that we just primed.
				boolean starInRow = false;
				for (int j=0; j<mask[row_col[0]].length; j++)
				{
					if (mask[row_col[0]][j]==1)		//If there is a star in the same row...
					{
						starInRow = true;
						row_col[1] = j;		//remember its column.
					}
				}
											
				if (starInRow==true)
				{
					System.out.println("There is also a starred zero in the cost matrix\n" +
										"in the same row with the zero we just primed.");
														
					System.out.println("It is element cost(" + (row_col[0]+1) + "," + (row_col[1]+1) + ")\n" +
										"We cover its row and uncover its column.");
					
					rowCover[row_col[0]] = 1;	//Cover the star's row.
					colCover[row_col[1]] = 0;	//Uncover its column.
					
					print1DArrayInt(rowCover, "%d   ", "Row cover matrix now is:", 1);
					print1DArrayInt(colCover, "%d   ", "Col cover matrix now is:", 2);
					System.out.println("Now we search again for uncovered zeros.");
				}
				else
				{
					System.out.println("There is no starred zero in the cost matrix\n" +
										"in the same row with the zero we just primed.");
					done = true;
					step = 5;
					System.out.println("Therefore, we need to go to step " + step + ".\n" +
										"However, before doing so, we need to store\n" +
										"the position of the zero we primed in the\n" +
										"Zero_RC matrix so that step 5 knows how to\n" +
										"construct Paths.\n");
					
					zero_RC[0] = row_col[0];	//Save row of primed zero.
					zero_RC[1] = row_col[1];	//Save column of primed zero.
					
					System.out.println("The Zero_RC matrix now is:" +
										" [" + (zero_RC[0]+1) +"][" + (zero_RC[1]+1) +  "]\n");
				}
			}
		}
		
		System.out.println ("Step 4 completed. Going to step " + step + ".\n\n");
		return step;
	}
	public static int[] findUncoveredZero	//Aux 1 for hg_step4.
	(int[] row_col, double[][] cost, int[] rowCover, int[] colCover)
	{
		System.out.println( "Finding an uncovered zero in cost matrix...");
		print2DArray(cost, "%.1f\t", "Cost matrix is (for easy reference):", 1);
		
		//The first row value that we define must not be one that refers to an index
		//of the cost matrix. It is only a check. If a meaningful row needs to be
		//returned it will be returned from the loop that follows. If there is no
		//such value we return -1 to tell step 4 that there exists no uncovered zero
		//and, hence, it needs to go to Step 6.
		row_col[0] = -1;	//Just a check value. Not a real index.
		row_col[1] = 0;
			
		int i = 0;
		boolean done = false;
		while (done == false)
		{
			int j = 0;
			while (j < cost[i].length)
			{
				if (cost[i][j]==0 && rowCover[i]==0 && colCover[j]==0)
				{
					row_col[0] = i;
					row_col[1] = j;
					done = true;
				}
				j = j+1;
			}//end inner while
			i=i+1;
			if (i >= cost.length)
			{
				done = true;
			}
		}//end outer while
		
		return row_col;
	}
	public static int hg_step5(int step, int[][] mask, int[] rowCover, int[] colCover, int[] zero_RC)
	{
		//What STEP 5 does:	
		//Step 5 basically is a version of an augmenting path algorith (for solving the
		//maximal matching problem). It discovers and sets augmenting paths, that is, paths
		//that increase the flow of a network (I think). Understanding augmenting paths etc
		//is a bit difficult and you need to only if you are a computer science major. Here,
		//I will just try to superficially explain how it works inside so that you understand
		//what is basically happening.
		//We want to construct a series of alternating primed and starred zeros as follows:
		//	a. Let path(i,j) represent the last uncovered primed zero found in Step 4. If you
		//     remember, we stored its coordinates in the zero_RC array.
		//	b. Let path(i+1,j) denote the starred zero in the column of path(i,j)(if any).
		//	   (When we say in the column, we mean in the column of the mask matrix).
		//	c. Let path(i+2,j) denote the primed zero in the row of path(i+1,j)
		//	   (there will always be one) (Again with row, we mean row of mask matrix).
		//	d. Continue until the series terminates at a primed zero that has no starred
		//	   zero in its column.
		//	e. Unstar each starred zero of the series and star each primed zero of the series.
		//	f. Erase all primes (if any other primes exist in the mask matrix).
		//	g. Uncover every row and column
		//	h. Return to step 3.
		//Clearly, some of the above steps, need to be decomposed into sub-methods. 
		
		printStepHeader("Step 5", 1);
		System.out.println( "A summary of what this step does,\n" +
							"exists (commented)in the source code.\n");
		
		int count = 0;	//Counts rows of the path matrix.
		
		//First we declare the path matrix. Since it holds positions of elements in the
		//mask matrix, its first column holds the row number and the second column holds
		//the column number. So this matrix is somethingx2. We can't be sure what exactly this
		//something will be equal to. It changes everytime. Therefore, we can either have the
		//path array dynamically grow when needed, or set it to something it cannot exceed.
		//We do the latter.
		
		//Personal Note: We set its rows to be mask.length * mask[0].length. It should never
		//be anything larger than that cause we would be going in circles forever then.
		//If this does not work, then Houston we have a problem. It does seem to work ok.
		//So far so good, keep an eye on this.
				
		int[][] path = new int[(mask[0].length*mask.length)][2];
		
		//Set the first row of path to the coordinates of the last uncovered primed zero.
		path[count][0] = zero_RC[0];	//Row of last prime.
		path[count][1] = zero_RC[1];	//Column of last prime.
		
		System.out.println("We create the path matrix, and set its\n" +
							"first row to represent the coordinates\n" +
							"(in the mask) of the last uncovered\n" +
							"primed zero that was spotted in step 4.\n" +
							"This last primed zero was found to be\n" +
							"in position: mask(" + (zero_RC[0]+1) + "," + (zero_RC[1]+1) + ").\n" +
							"Indeed, this is confirmed to be the case\n" +
							"if we check the mask and Cover matrices.\n");
		print1DArrayInt(rowCover, "%d   ", "Row cover matrix passed to step 5:", 1);
		print1DArrayInt(colCover, "%d   ", "Col cover matrix passed to step 5:", 1);					
		print2DArrayInt(mask, "%d\t", "Mask matrix passed to step 5:", 0);
		printPath(path, (count+1), "%d\t", "The path matrix, therefore, becomes: ", 2);
		
		boolean done = false;
		while (done == false)
		{ 
			int r = findStarInCol(mask, path[count][1]);
			if (r>=0)
			{
				count = count+1;
				path[count][0] = r;					//Row of starred zero.
				path[count][1] = path[count-1][1];	//Column of starred zero.
				printPath(path, (count+1), "%d\t", "The path matrix, therefore, becomes: ", 2);
			}
			else
			{
				System.out.println("No starred zero exists in column " + (path[count][1]+1) + ".\n\n" +
									"Therefore, since the last primed zero\n" +
									"has no starred zero in its column, we\n" +
									"terminate the alternating series of\n" +
									"primed and starred zeros here.\n");
				done = true;
			}
			
			if (done == false)
			{
				int c = findPrimeInRow(mask, path[count][0]);
				count = count+1;
				path[count][0] = path [count-1][0];	//Row of primed zero.
				path[count][1] = c;					//Column of primed zero.
				printPath(path, (count+1), "%d\t", "The path matrix, therefore, becomes: ", 2);
			}
		}//end while
		
		convertPath(mask, path, count);
		
		System.out.println("The Cover arrays are being cleared...");
		clearCovers(rowCover, colCover);
		System.out.println("Did it.");
		print1DArrayInt(rowCover, "%d   ", "Row cover matrix cleared:", 1);
		print1DArrayInt(colCover, "%d   ", "Col cover matrix cleared:", 2);
		
		erasePrimes(mask);
		print2DArrayInt(mask, "%d\t", "\nMask matrix at the end of step 5:", 1);
		
		step = 3;
		System.out.println ("Step 5 completed. Going to step " + step + ".\n\n");
		
		return step;
		
	}
	public static int findStarInCol			//Aux 1 for hg_step5.
	(int[][] mask, int col)
	{
		System.out.println("Now we try to find whether a starred zero\n" +
							"exists in the column of the last primed\n" +
							"zero, that is, in column " + (col+1) + ".");
		print2DArrayInt(mask, "%d\t", "Mask matrix (for easy reference):", 0);
		int r=-1;	//Again this is a check value.
		for (int i=0; i<mask.length; i++)
		{
			if (mask[i][col]==1)
			{
				r = i;
			}
		}
		if (r != -1)
		{
			System.out.println("A starred zero does exist in column " + (col+1) + ".\n" +
								"It is in row " + (r+1) + ".");
		}
		
		return r;
	}
	public static int findPrimeInRow		//Aux 2 for hg_step5.
	(int[][] mask, int row)
	{
		System.out.println("Now we try to find whether a primed zero\n" +
							"exists in the row of the last starred\n" +
							"zero, that is, in row " + (row+1) + ".");
		print2DArrayInt(mask, "%d\t", "Mask matrix (for easy reference):", 0);
		int c = -1;
		for (int j=0; j<mask[row].length; j++)
		{
			if (mask[row][j]==2)
			{
				c = j;
			}
		}
		System.out.println("A primed zero does exist in row " + (row+1) + ".\n" +
							"It is in column " + (c+1) + ".");
		return c;
	}
	public static void convertPath			//Aux 3 for hg_step5.
	(int[][] mask, int[][] path, int count)
	{
		System.out.println("convertPath() method now starts...\n" +
							"It stars all primed zeros of the series,\n" +
							"and resets all starred zeros of the series.");
		print2DArrayInt(mask, "%d\t", "Mask matrix before convertPath():", 0);
		for (int i=0; i<=count; i++)
		{
			if (mask[(path[i][0])][(path[i][1])]==1)
			{
				mask[(path[i][0])][(path[i][1])] = 0;
			}
			else
			{
				mask[(path[i][0])][(path[i][1])] = 1;
			}
		}
		print2DArrayInt(mask, "%d\t", "Mask matrix after convertPath()", 1);
	}
	public static void erasePrimes			//Aux 4 for hg_step5.
	(int[][] mask)
	{
		System.out.println("erasePrimes() method erases any other\n" +
							"primes that were possibly left.");
		int tempCount = 0;
		for (int i=0; i<mask.length; i++)
		{
			for (int j=0; j<mask[i].length; j++)
			{
				if (mask[i][j]==2)
				{
					tempCount = tempCount + 1;
					mask[i][j] = 0;
				}
			}
		}
		if (tempCount == 0)
		{
			System.out.println("No other primes were found.");
		}
		else
		{
			System.out.println(tempCount + " other primes were found and erased.\n");
		}
	}
	public static void clearCovers			//Aux 5 for hg_step5 (also used from other parts).
	(int[] rowCover, int[] colCover)
	{
		for (int i=0; i<rowCover.length; i++)
		{
			rowCover[i] = 0;
		}
		for (int j=0; j<colCover.length; j++)
		{
			colCover[j] = 0;
		}
	}
	public static int hg_step6(int step, double[][] cost, int[] rowCover, int[] colCover, double maxCost)
	{
		//What STEP 6 does:
		//Step 6 does something similar to what step 1 did but not quite...
		//It finds the smallest uncovered value in the cost matrix. Then, it:
		//	a. Subtracts this value from every element that is on an uncovered column,
		//  b. Adds this value to every element that is on a covered row.
		//In essence it modifies the cost matrix so that step 4 can continue again.
			
		printStepHeader("Step 6", 1);
		System.out.println( "Step 6 finds the smallest uncovered value\n" +
							"in the cost matrix.\n" +
							"It subtracts this value from every element\n" +
							"on an uncovered column, and it adds it to\n" +
							"every element on a covered row.\n");
		print2DArray(cost, "%.1f\t", "Cost matrix passed to step 6:", 0);		
		print1DArrayInt(rowCover, "%d   ", "Row cover matrix passed to step 6:", 1);
		print1DArrayInt(colCover, "%d   ", "Col cover matrix passed to step 6:", 2);
		
		double minval = 0;
		minval = findSmallest(cost, rowCover, colCover, maxCost);
		System.out.printf("After scanning the cost matrix, the\n" +
				"minimun value was determined to be: %.1f.\n", minval);

		System.out.printf("Uncovered columns to subtract %.1f from: ", minval);
		int count1 = 0;
		for (int j=0; j<colCover.length; j++)
		{
			if (colCover[j]==0)
			{
				count1 = count1 + j;
				System.out.print((j+1) + ", ");
			}
		}
		if (count1==0)
		{
			System.out.print("none");
		}
		System.out.println();
		
		System.out.printf("Covered rows to add %.1f to: ", minval);
		int count2 = 0;
		for (int i=0; i<rowCover.length; i++)
		{
			if (rowCover[i]==1)
			{
				count2 = count2 + i;
				System.out.print((i+1) + " ");
			}
		}
		if (count2==0)
		{
			System.out.print("none");
		}
		System.out.println();					 
		
		for (int i=0; i<rowCover.length; i++)
		{
			for (int j=0; j<colCover.length; j++)
			{
				if (rowCover[i]==1)
				{
					cost[i][j] = cost[i][j] + minval;
				}
				if (colCover[j]==0)
				{
					cost[i][j] = cost[i][j] - minval;
				}
			}
		}
			
		print2DArray(cost, "%.1f\t", "\nNow the cost matrix becomes:", 0);	
		
		step = 4;
		System.out.println ("\nStep 6 completed. Going to step " + step + ".\n\n");
		
		return step;
	}
	public static double findSmallest		//Aux 1 for hg_step6.
	(double[][] cost, int[] rowCover, int[] colCover, double maxCost)
	{
		double minval = maxCost;	//There cannot be a larger cost than this.
		
		//Now find the smallest uncovered value.
		for (int i=0; i<cost.length; i++)
		{
			for (int j=0; j<cost[i].length; j++)
			{
				if (rowCover[i]==0 && colCover[j]==0 && (minval > cost[i][j]))
				{
					minval = cost[i][j];
				}
			}
		}
		
		return minval;
	}
	
	public static void main(String[] args) 
	{
	
		//If the intent is to find the assignment, that is, the combination of 
		//unique row-col pairs that minimizes the sum then leave "min" below for
		//sumType. If, instead you want to find the assignment that maximizes the
		//sum then change "min" to "max".
		String sumType = "max";
		//Hard-coded example. Change to enter your own matrix.
		double[][] array =
		{
				{14, 81, 78},
				{54, 95, 28},
				{67, 33, 51}
		};
		
		//Another example matrix
		/*double[][] array = 
		{
				{23, 27, 42, 55, 97},
				{14, 94, 5, 77, 22},
				{53, 3, 88, 89, 7},
				{99, 52, 14, 1, 63},
				{50, 100, 34, 41, 92}
		};
		*/
		
		/*//<UNCOMMENT> BELOW AND COMMENT BLOCK ABOVE TO USE A RANDOMLY GENERATED MATRIX
		//Do NOT enter any dimension larger than 5 or so...it will take long to finish
		//plus...you won't understand much because of the crowded output.
		int numOfRows = readInput("How many rows for the matrix? ");
		int numOfCols = readInput("How many columns for the matrix? ");
		double[][] array = new double[numOfRows][numOfCols];
		generateRandomArray(array, "random");	//All elements within [0,1].
		*///</UNCOMMENT>
		
		System.out.println("INITIAL DATA MATRICES");
		System.out.println("(In all calcs to follow, printing\nonly 1 decimal digit for doubles)\n");
		if (array.length > array[0].length)
		{
			//Cols must be >= Rows.
			System.out.println("You entered a rectangular array where\n" +
								"the rows are more than the columns.\n" +
								"This array needs to be transposed\n" +
								"because the algorithm works correctly\n" +
								"only if rows <= columns. (This does\n" +
								"not affect the assignment).\n");
								
			array = transpose(array);
		}
				
		print2DArray(array, "%.1f\t", "The inputted matrix is:", 1);
		
		double startTime = System.nanoTime();	
		
		int[][] assignment = new int[array.length][2];
		assignment = hgAlgorithm(array, sumType);	//Call Hungarian algorithm.
		 
		double endTime = System.nanoTime();
				
		String arrayType;						//Define this for printing purposes only.
		if (sumType.equalsIgnoreCase("min"))
		{
			arrayType = "cost";
		}
		else if (sumType.equalsIgnoreCase("max"))
		{
			arrayType = "weight";
		}
		else
		{
			arrayType = "";
		}
		
		print2DArray(array, "%.1f\t", ("The initial " + arrayType + " array was:"), 1);
		System.out.println("The winning assignment (" + sumType + " sum) is:\n");	
		double sum = 0;
		for (int i=0; i<assignment.length; i++)
		{
			//<COMMENT> to avoid printing the elements that make up the assignment
			System.out.printf("array(%d,%d) = %.1f\n", (assignment[i][0]+1), (assignment[i][1]+1),
					array[assignment[i][0]][assignment[i][1]]);
			sum = sum + array[assignment[i][0]][assignment[i][1]];
			//</COMMENT>
		}
		
		System.out.printf("\nThe %s is: %.1f\n", sumType, sum);
		
		System.out.println("\n\nIf you do not trust this algorithm you\n" +
							"can always try doing this manually.\n" +
							"It is a lot of fun, especially when\n" +
							"your arrays start getting larger than\n" +
							"[3]x[3] :-).\n\n" +
							"Basically, for nxn arrays you need to\n" +
							"consider n! (n factorial) combinations\n" +
							"if you adopt a brute force approach.\n" +
							"Good luck!\n\n" +
							"This algorithm, however, has a complexity\n" +
							"of about O(n^3) (At least this is what I\n" +
							"read).Quite an improvement.\n\n" +
							"There are also a few more algorithms for\n" +
							"the assignment problem that are a bit more\n" +
							"efficient, but this is the only one for\n" +
							"which I found info on how to implement.\n\n");
		
		double totalTime = getTime(startTime, endTime);
		System.out.print("Total time required: ");
		printTime(totalTime);
		insertLines(1);
		
	}

/**
 * HungarianAlgorithmEdu constructor comment.
 */
public HungarianAlgorithmEdu() {
	super();
}
}