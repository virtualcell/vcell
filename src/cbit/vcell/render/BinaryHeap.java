package cbit.vcell.render;

/**
* Implements a binary heap, Min element at the top
* Implements decreaseKey method
* Duplicates allowed
*/

@SuppressWarnings("rawtypes")
interface ComparableEx extends Comparable {
	int compareContent(Object o);
}

@SuppressWarnings("unchecked")
public class BinaryHeap
{
	private static final int DEFAULT_SIZE = 200;
	private int currentSize;				// # of elements
	private ComparableEx[] array;			// the heap array
 
	public BinaryHeap( )
	{
		currentSize = 0;
		array = new ComparableEx[DEFAULT_SIZE+1];
	}
	public BinaryHeap(ComparableEx[ ] items)
	{
		currentSize = items.length;
		array = new ComparableEx[items.length+1];
     
		for(int i=0; i<items.length; i++) {
			array[i+1] = items[i];
		}
		buildHeap();    
	}
 
	public int insert(ComparableEx x)
	{
		if(currentSize+1 == array.length) {
			doubleArray();
		}
		// Percolate up
		int hole = ++currentSize;
		array[0] = x;
     
		for(; x.compareTo(array[hole/2])<0; hole/=2) {
			array[hole] = array[hole/2];
		}
		array[hole] = x;
		return hole;
	}

	// returns the top of the binary heap (smallest item)
	public ComparableEx findMin()
	{
		if(isEmpty()) {
			throw new RuntimeException("Binary Heap is empty");
		}
		return array[1];
	}

	public ComparableEx removeMin()
	{
		ComparableEx minItem = findMin();
		array[1] = array[currentSize--];
		percolateDown(1);
		return minItem;
	}

	// decrease element key if element is present
	public void decreaseKey(ComparableEx x) {
		boolean found = false;
//		int hole = currentSize;
		int hole = 1;
//		for( ; hole>0; hole--) {							//  54 sec		98 sec
		for( ; hole<=currentSize; hole++) {					//  28 sec		75 sec
			if(x.compareContent(array[hole]) == 0) {
				if(array[hole].compareTo(x) < 0) {		// cannot decrease to a larger key
//					System.out.println("New key is larger than current key");
					throw new RuntimeException("New key is larger than current key");
				} else if(array[hole].compareTo(x) == 0) {
					return;		// they're equal, nothing to do
				}
				found = true;
				break;
			}
		}
		if(found) {
			// we shift down 1 position all elements above hole which have a key larger than our element
			for(; hole>1; hole/=2) {
				if(x.compareTo(array[hole/2]) < 0) {
					array[hole] = array[hole/2];	
				} else {
					break;
				}
			}
			array[hole] = x;
		} else {
//			System.out.println("Element not found!");
			throw new RuntimeException("Element not found!");
		}
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
		ComparableEx tmp = array[hole];

		for(; hole*2 <= currentSize; hole=child)
		{
			child = hole*2;
			if( child != currentSize && array[child+1].compareTo(array[child]) < 0) {
				child++;
			}
			if(array[child].compareTo(tmp) < 0) {
				array[hole] = array[child];
			} else {
				break;
			}
		}
		array[hole] = tmp;
	}
 
	private void doubleArray()
	{
		ComparableEx[] newArray;
		
		newArray = new ComparableEx[array.length*2];
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
		
		// modify element with index=10 - point at (0,1,2)
		int index = 0 + 1*numX + 2*numX*numY;		
		PointEx ch = new PointEx(5.678, index);
		bh.decreaseKey(ch);
		
		for(int i=0; i<numItems; i++) {
			PointEx e = (PointEx)(bh.removeMin());
			System.out.println(e.getPosition() + " : " + e.getDistance());
		}
		
		System.out.println(" ---------------------------- ");

		// another way to work with the data
		PointEx[] items = new PointEx[numItems];
		for(int z=0; z<numZ; z++) {
			for(int y=0; y<numY; y++) {
				for(int x=0; x<numX; x++) {
					index = x + y*numX + z*numX*numY;
					items[index] = new PointEx(5+x+y+z, index);
					bh.insert(items[index]);
				}
			}
		}
		
		
		for(int i=0; i<numItems; i++) {
			PointEx e = (PointEx)(bh.removeMin());
			System.out.println(e.getPosition() + " : " + e.getDistance());
		}
		
		
	}
	

}
