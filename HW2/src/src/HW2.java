 
/* HW2. Fruits and hash tables
 * This file contains 7 classes:
 * 		- Row represents a row of fruits,
 * 		- CountConfigurationsNaive counts stable configurations naively,
 * 		- Quadruple manipulates quadruplets,
 * 		- HashTable builds a hash table,
 * 		- CountConfigurationsHashTable counts stable configurations using our hash table,
 * 		- Triple manipulates triplets,
 * 		- CountConfigurationsHashMap counts stable configurations using the HashMap of java.
 */


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

class Row { // represent a row of fruits
	private final int[] fruits;

	// empty row constructor
	Row() {
		this.fruits = new int[0];
	}

	// constructor from the field fruits
	Row(int[] fruits) {
		this.fruits = fruits;
	}

	// equals method to compare the row to an object o
	@Override
	public boolean equals(Object o) {
		// we start by transforming the object o into an object of the class Row
		// here we suppose that o will always be of the class Row
		Row that = (Row) o;
		// we check if the two rows have the same length
		if (this.fruits.length != that.fruits.length)
			return false;
		// we check if the i-th fruits of the two rows coincide
		for (int i = 0; i < this.fruits.length; ++i) {
			if (this.fruits[i] != that.fruits[i])
				return false;
		}
		// we have the equality of the two rows
		return true;
	}

	// hash code of the row
	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < fruits.length; ++i) {
			hash = 2 * hash + fruits[i];
		}
		return hash;
	}

	// string representing the row
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < fruits.length; ++i)
			s.append(fruits[i]);
		return s.toString();
	}

	// Question 1

	// returns a new row by adding fruit to the end of the row
	Row extendedWith(int fruit) {
		int[] newFruits = new int[fruits.length + 1];
		for (int i = 0; i < fruits.length; i++)
			newFruits[i] = fruits[i];
		newFruits[fruits.length] = fruit;
		return new Row(newFruits);
	}

	// return the list of all stable rows of width width
	static LinkedList<Row> allStableRows(int width) {
		LinkedList<Row> rows = new LinkedList<Row>();
		if (width == 0) {
			rows.add(new Row());
			return rows;
		}
		rows.add(new Row(new int[] { 0 }));
		rows.add(new Row(new int[] { 1 }));
		while (!rows.isEmpty()) {
			Row currentRow = rows.removeFirst();
			if (currentRow.fruits.length == width) {
				rows.add(currentRow);
				break;
			}

			if (currentRow.fruits.length >= 2 && currentRow.fruits[currentRow.fruits.length -2] == 0 && currentRow.fruits[currentRow.fruits.length - 1] == 0){
				rows.add(currentRow.extendedWith(1));
				continue;
			}
			if (currentRow.fruits.length >= 2 && currentRow.fruits[currentRow.fruits.length -2] == 1 && currentRow.fruits[currentRow.fruits.length - 1] == 1){
				rows.add(currentRow.extendedWith(0));
				continue;
			}

			rows.add(currentRow.extendedWith(0));
			rows.add(currentRow.extendedWith(1));
		}

		return rows;
	}
	

	// check if the row can be stacked with rows r1 and r2
	// without having three fruits of the same type adjacent
	boolean areStackable(Row r1, Row r2) {
		if (r1.fruits.length != r2.fruits.length || r1.fruits.length != fruits.length){
			return false;
		}
		for (int i = 0; i < fruits.length; i++){
			if (fruits[i] == r1.fruits[i] && fruits[i] == r2.fruits[i]){
				return false;
			}
		}
		return true;
	}
}

// Naive counting
class CountConfigurationsNaive {  // counting of stable configurations

	// Question 2

	// returning the number of grids whose first lines are r1 and r2,
	// whose lines are lines of rows and whose height is height
	static long count(Row r1, Row r2, LinkedList<Row> rows, int height) {
		if (height == 2){
			return 1;
		}
		if (height <= 1){
			return 0;
		}

		long total = 0;
		for (Row r3 : rows) {
			if (r1.areStackable(r2, r3)) {
				total += count(r2, r3, rows, height - 1);
			}
		}
		return total;
	}

	// returning the number of grids with n lines and n columns
	static long count(int n) {
		LinkedList<Row> rows = Row.allStableRows(n);
		long total = 0;
		if (n == 0){
			return 1;
		}
		if (n == 1){
			return 2;
		}

		for (Row r1 : rows) {
			for (Row r2 : rows) {
				total += count(r1, r2, rows, n);
			}
		}

		return total;
	}
}

// Construction and use of a hash table

class Quadruple { // quadruplet (r1, r2, height, result)
	Row r1;
	Row r2;
	int height;
	long result;

	Quadruple(Row r1, Row r2, int height, long result) {
		this.r1 = r1;
		this.r2 = r2;
		this.height = height;
		this.result = result;
	}
}

class HashTable { // hash table
	final static int M = 50000;
	Vector<LinkedList<Quadruple>> buckets;

	// Question 3.1

	// constructor
	HashTable() {
		buckets = new Vector<LinkedList<Quadruple>>(M);
		for (int i = 0; i < M; i++) {
			buckets.add(new LinkedList<Quadruple>());
		}
	}

	// Question 3.2

	// return the hash code of the triplet (r1, r2, height)
	static int hashCode(Row r1, Row r2, int height) {
		final int prime = 69;
		int result = 1;
		result = prime * result + r1.hashCode();
		result = prime * result + r2.hashCode();
		result = prime * result + height;
		return result;
	}

	// return the bucket of the triplet (r1, r2, height)
	int bucket(Row r1, Row r2, int height) {
		return hashCode(r1, r2, height) % M;
	}

	// Question 3.3

	// add the quadruplet (r1, r2, height, result) in the bucket indicated by the
	// method bucket
	void add(Row r1, Row r2, int height, long result) {
		this.buckets.get(bucket(r1, r2, height)).add(new Quadruple(r1, r2, height, result));
	}

	// Question 3.4

	// search in the table an entry for the triplet (r1, r2, height)
	Long find(Row r1, Row r2, int height) {
		 LinkedList<Quadruple> result  = this.buckets.get(bucket(r1, r2, height));
		 for (Quadruple q : result){
			 if (q.r1.equals(r1) && q.r2.equals(r2) && q.height == height){
				 return q.result;
			 }
		 }
		 return null;
	}

}

class CountConfigurationsHashTable { // counting of stable configurations using our hash table
	static HashTable memo = new HashTable();

	// Question 4

	// return the number of grids whose first lines are r1 and r2,
	// whose lines are lines of rows and whose height is height
	// using our hash table
	static long count(Row r1, Row r2, LinkedList<Row> rows, int height) {
		if (height == 2){
			return 1;
		}
		if (height <= 1){
			return 0;
		}

		Long result = memo.find(r1, r2, height);
		if (result != null){
			return result;
		}

		long total = 0;
		for (Row r3 : rows) {
			if (r1.areStackable(r2, r3)) {
				Long countResult = count(r2, r3, rows, height - 1);
				memo.add(r2, r3, height - 1, countResult);
				total += countResult;
			}
		}
		return total;
	}

	// return the number of grids with n lines and n columns
	static long count(int n) {
		LinkedList<Row> rows = Row.allStableRows(n);
		long total = 0;
		if (n == 0){
			return 1;
		}
		if (n == 1){
			return 2;
		}

		for (Row r1 : rows) {
			for (Row r2 : rows) {
				total += count(r1, r2, rows, n);
			}
		}

		return total;
	}
}

//Use of HashMap

class Triple { // triplet (r1, r2, height)
	Row r1;
	Row r2;
	int height;

	Triple(Row r1, Row r2, int height) {
		this.r1 = r1;
		this.r2 = r2;
		this.height = height;
	}

	// equals method to compare the triplet to an object o
	@Override
	public boolean equals(Object o) {
		Triple that = (Triple) o;
		if (!this.r1.equals(that.r1) || !this.r2.equals(that.r2) || this.height != that.height)
			return false;
		return true;
	}

	// hash code of the triplet
	@Override
	public int hashCode() {
		return HashTable.hashCode(r1, r2, height);
	}
}

class CountConfigurationsHashMap { // counting of stable configurations using the HashMap of java
	static HashMap<Triple, Long> memo = new HashMap<Triple, Long>();

	// Question 5

	// returning the number of grids whose first lines are r1 and r2,
	// whose lines are lines of rows and whose height is height
	// using the HashMap of java
	static long count(Row r1, Row r2, LinkedList<Row> rows, int height) {
		if (height == 2){
			return 1;
		}
		if (height <= 1){
			return 0;
		}

		Long result = memo.containsKey(new Triple(r1, r2, height)) ? memo.get(new Triple(r1, r2, height)) : null;
		if (result != null){
			return result;
		}

		long total = 0;
		for (Row r3 : rows) {
			if (r1.areStackable(r2, r3)) {
				Long countResult = count(r2, r3, rows, height - 1);
				memo.put(new Triple(r2, r3, height - 1), countResult);
				total += countResult;
			}
		}
		return total;
	}

	// return the number of grids with n lines and n columns
	static long count(int n) {
		LinkedList<Row> rows = Row.allStableRows(n);
		long total = 0;
		if (n == 0){
			return 1;
		}
		if (n == 1){
			return 2;
		}

		for (Row r1 : rows) {
			for (Row r2 : rows) {
				total += count(r1, r2, rows, n);
			}
		}

		return total;
	}
}
