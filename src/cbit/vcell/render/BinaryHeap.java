package cbit.vcell.render;

/**
* Implements a binary heap, Min element at the top
* Implements decreaseKey method
* Duplicates allowed
*/

@SuppressWarnings("unchecked")
public class BinaryHeap
{
	private static final int DEFAULT_SIZE = 200;
	private int currentSize;				// # of elements
	private PointEx[] array;			// the heap array
 
	public BinaryHeap( )
	{
		currentSize = 0;
		array = new PointEx[DEFAULT_SIZE+1];
	}

	public int insert(PointEx x)
	{
		if(currentSize+1 == array.length) {
			doubleArray();
		}
		// Percolate up
		int hole = ++currentSize;
		array[0] = x;
     
		for(; x.compareTo(array[hole/2].getDistance())<0; hole/=2) {
			array[hole] = array[hole/2];
			array[hole].setHole(hole);
		}
		array[hole] = x;
		array[hole].setHole(hole);
		return hole;
	}

	// returns the top of the binary heap (smallest item)
	public PointEx findMin()
	{
		if(isEmpty()) {
			throw new RuntimeException("Binary Heap is empty");
		}
		return array[1];
	}

	public PointEx removeMin()
	{
		PointEx minItem = findMin();
		array[1] = array[currentSize--];
		percolateDown(1);
		minItem.setHole(0);
		return minItem;
	}

	// decrease element key if element is present
	public void decreaseKey(int hole, double newDistance) {
		boolean found = false;
		if(array[hole].compareTo(newDistance) < 0) {		// cannot decrease to a larger key
					System.out.println("New key " + newDistance +" is larger than current key" + array[hole].getDistance() + " at " + hole);
//			throw new RuntimeException("New key is larger than current key");
		} else if(array[hole].compareTo(newDistance) == 0) {
			return;		// they're equal, nothing to do
		}

		PointEx tmp = array[hole];
		tmp.setDistance(newDistance);

		// we shift down 1 position all elements above hole which have a key larger than our element
		for(; hole>1; hole/=2) {
			if(tmp.compareTo(array[hole/2].getDistance()) < 0) {
				array[hole] = array[hole/2];	
				array[hole].setHole(hole);
			} else {
				break;
			}
		}
		array[hole] = tmp;
		array[hole].setHole(hole);
	}

	public boolean isEmpty()
	{
		return currentSize == 0;
	}

	public int size()
	{
		return currentSize;
	}
 
	public void makeEmpty()
	{
		currentSize = 0;
	}
	
	private void buildHeap()
	{
		for(int i=currentSize/2; i>0; i--) {
			percolateDown(i);
		}
	}

	private void percolateDown(int hole)
	{
		int child;
		PointEx tmp = array[hole];

		for(; hole*2 <= currentSize; hole=child)
		{
			child = hole*2;
			if( child != currentSize && array[child+1].compareTo(array[child].getDistance()) < 0) {
				child++;
			}
			if(array[child].compareTo(tmp.getDistance()) < 0) {
				array[hole] = array[child];
				array[hole].setHole(hole);
			} else {
				break;
			}
		}
		array[hole] = tmp;
		array[hole].setHole(hole);
	}
 
	private void doubleArray()
	{
		PointEx[] newArray;
		
		newArray = new PointEx[array.length*2];
		for(int i = 0; i < array.length; i++) {
			newArray[i] = array[i];
		}
		array = newArray;
	}
	
	
	public static void main(String[] args) {
		int numX = 2;
		int numY = 2;
		int numZ = 3;
		int numItems = numX*numY*numZ;
		
		BinaryHeap bh = new BinaryHeap();
		
		for(int z=0; z<numZ; z++) {
			for(int y=0; y<numY; y++) {
				for(int x=0; x<numX; x++) {
					int index = x + y*numX + z*numX*numY;
					bh.insert(new PointEx(5+x+y+z, new Integer(index)));
				}
			}
		}
		
		// get them out one by one starting with the one with smallest key
		for(int i=0; i<numItems; i++) {
			PointEx e = (PointEx)(bh.removeMin());
			System.out.println(e.getPosition() + " : " + e.getDistance());
		}
		
		System.out.println(" ---------------------------- ");

		// another way to work with the data
		PointEx[] items = new PointEx[numItems];
		int index;
		for(int z=0; z<numZ; z++) {
			for(int y=0; y<numY; y++) {
				for(int x=0; x<numX; x++) {
					index = x + y*numX + z*numX*numY;
					items[index] = new PointEx(5+x+y+z, index);
					bh.insert(items[index]);
				}
			}
		}
		
		
		// modify element with index=10 - point at (0,1,2)
		index = 0 + 1*numX + 2*numX*numY;		
		PointEx ch = items[index];
		bh.decreaseKey(ch.getHole(), 5.678);

		for(int i=0; i<numItems; i++) {
			PointEx e = (PointEx)(bh.removeMin());
			System.out.println(e.getPosition() + " : " + e.getDistance());
		}
		
		
	}
	

}
