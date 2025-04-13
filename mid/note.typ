= Midterm Exercises in notes

= 20. page 61 , Ans page 196
Program 6 Stack structure(made using singly linked list):
```java
class Stack {
  private Singly head;
  Stack() {
    this.head = null;
  }
  boolean isEmpty() {
    return this.head == null;
  }
  void push(int x) {
    this.head = new Singly(x, this.head);
  }
  int top() {
    if (this.head == null)
      throw new NoSuchElementException();
    return this.head.element;
  }
  int pop() {
    if (this.head == null)
      throw new NoSuchElementException();
    int e = this.head.element;
    this.head = this.head.next;
    return e;
  }
}
```
```java
static String listToString(Singly s) {
  StringBuffer sb = new StringBuffer("[");
  while (s != null) {
    sb.append(s.element);
    if (s.next != null) sb.append(" -> ");
    s = s.next;
  }
  return sb.append("]").toString();
}
```
Write a public dynamic method String toString() for the Stack class, which returns the contents of a stack as a string such as "[1, 2, 3]" where 1 is the top of the stack.  We can draw inspiration from the listToString method given above.\
幫Stack class寫一個method toString(), "[1,2,3]" 1 是stack的top\
可以從listToString獲得啟發
- Singly是單向linked list
```java
String toString(){
  StringBuffer sb = new StringBuffer("[");
  while (s != null) {
    sb.append(s.element);
    if (s.next != null) sb.append(",");
    s = s.next;
  }
  return sb.append("]").toString();
}
```

= 24. page 70 , Ans page 196
Program 10 The Josephus Problem:
```java
// constructs the circular list 1,2,...,n and returns element 1
static Doubly circle(int n) {
  Doubly l1 = new Doubly(1);
  for (int i = n; i >= 2; i--) {
    l1.insertAfter(i);
    if (i == n) { l1.prev = l1.next; l1.next.next = l1; }
  }
  return l1;
}
static int josephus(int n, int p) {
  Doubly c = circle(n);
  while (c != c.next) { // as long as there are at least two players left
    for (int i = 1; i < p; i++)
      c = c.next;
    c.remove(); // we eliminate the p-th
    c = c.next;
  }
  return c.element;
}
```
Rewrite the josephus method using a singly linked cyclic list. Hint: in the inner loop, keep a pointer to the previous element, so that you can easily delete the p-th element at the end of the loop. 
重寫josephus method 使用singly linked list, Hint: 在內部迴圈中，保持一個指向前一個元素的指針，以便在迴圈結束時可以輕鬆刪除第 p 個元素。\

- doubly 是雙向linked list\

- Josephus problem:\
  - 有 n 個人（編號從 1 到 n）圍成一圈，從第 1 個人開始報數，每報到第 k 個人就將其淘汰，接著從下一個人繼續報數。如此重複進行，直到剩下一人為止。求最後剩下的那個人的編號。

```java
static Singly circle(int n) {
  Singly first = new Singly(1, null);
  Singly last = first;
  for (int i = 2; i <= n; i++) {
    last.next = new Singly(i, first);
  }
  last.next = first;
  return first;
}
static int josephus(int n, int p) {
  Singly c = circle(n);
  while (c != c.next) { // as long as there are at least two players left
    for (int i = 1; i < p; i++)
      c = c.next;
    c.remove(); // we eliminate the p-th
    c = c.next;
  }
  return c.element;
}
```


= 28. page 75 , Ans page 198
Add a void remove(String s) method to remove an element s from the hash table.  What is the impact on the add method? 

= 37. page 84 , Ans page 198
Write a method static int floor(BST b, int x) that returns the largest element of b less than or equal to x, if it exists, and throws an exception otherwise. 
= 40. page 95, Ans page 199
Add to the AVLSet class a private field size containing the number of elements in the set and an int size() method that returns its value. Modify the add and remove methods to update the value of this field. It will be necessary to correctly handle the case where the element added by add is already in the set and the case where the element removed by remove is not in the set. 
= 59. page 127 , Ans page 204
Add to the union-find structure a method int numClasses() giving the number of distinct classes. We will try to provide this value in constant time, maintaining the value as an additional field.
= 61. page 127 , Ans 205
Another solution to realize the union-find structure is not to use arrays, but to represent each element directly as an object containing two fields rank and link. If E denotes the type of the elements, we can define the following generic class:
```java
class Elt<E> {
  private E value;
  private Elt<E> link;
  private int rank;
  ...
}
```
It is no longer necessary to maintain global information about the union-find structure, because each element contains all the necessary information. (Be careful, however, not to share a value of type Elt<\E> between multiple partitions.) The interface of the Elt class is as follows:
```java
    Elt(E x)
  Elt<E> find()
    void union(Elt<E> e
```
The Elt(E x) constructor constructs a class containing a single element, of value x. We can choose the convention that a link pointer is null when it is a representative. Write this constructor as well as the find and union methods. 

= 78. page 143 , Ans page 207
Program 26 The total is good:
```java
// computes all integers that can be constructed with
// any subset of s
// a subset of s is a mask of {0,...,|s|-1}
// represented by an integer between 0 and 2^|s|-1
static Map<Integer, Map<Integer, String>> computeAll(int[] s) {
  Map<Integer, Map<Integer, String>> res = new HashMap<>();
  // we start with the singletons
  for (int i = 0; i < s.length; i++) {
    Map<Integer, String> m = new HashMap<>();
    m.put(s[i], "" + s[i]);
    res.put(1 << i, m);
  }
  int n = 1 << s.length;
  // then for any subset u
  for (int u = 3; u < n; u++) {
    if (res.containsKey(u)) // already done (u is a singleton)
    continue;
    Map<Integer, String> m = new HashMap<>();
    res.put(u, m);

    for (int left = 1; left < u; left++)
    if ((left & u) == left) { // left is a subset of u
      int right = u & ~left;
      m.putAll(res.get(left));
      for (int x: res.get(left).keySet()) {
        String ex = res.get(left).get(x);
        for (int y: res.get(right).keySet()) {
          String ey = res.get(right).get(y);
          m.put(x + y, "("+ex+"+"+ey+")");
          m.put(x * y, "("+ex+"*"+ey+")");
          if (x >= y) m.put(x - y, "("+ex+"-"+ey+")");
          if (y != 0 && x % y == 0) m.put(x / y, "("+ex+"/"+ey+")");
        }
      }
    }
  }
  return res;
}

```

Is Program 26 still correct if the array s contains duplicates, i.e., if it is a multiset and not a set? 

= 44. page 100, Ans page 201
Add to the Trie class a method void remove(String s) which removes the occurrence of the string s, if it exists.

= 51. page 108 , Ans page ??
Add a String toString() method that returns the Java string defined by a rope.
We will take care to do this efficiently using a StringBuffer.
