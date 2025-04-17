#set page(columns: 2, margin: 1cm)

#show raw: it => block(
  fill: rgb("#EEEEEE"),
  inset: 4pt,
  radius: 4pt,
  width: 100%,
  text(fill: rgb("#000000"), size: 8pt, it)
)

=== 20. toString() of a Stack
```java
public String toString() {
    StringBuffer sb=new StringBuffer("[");
    for (Singly s = this.head; s != null; s=s.next){
        sb.append(s.element);
        if (s.next != null) sb.append(", ");
    }
    return sb.append("]").toString();
}
// stack: top->bottom: 1, 2, 3
// return "[1, 2, 3]"
```

=== 24. Josephus' problem
Players are placed in a circle. Starting with player 1, he count up to p and eliminate the p-th player. Then, starting with the next
player, count p and he dies. Keep doing so until one is left.
```java
static Doubly circle(int n) {
    Doubly l1 = new Doubly(1);
    for (int i = n; i >= 2; i--) {
        l1.insertAfter(i);
        if (i == n) {
            l1.prev = l1.next; 
            l1.next.next = l1;
        }
    }
    return l1;
}
static int josephus(int n, int p) {
    Doubly c = circle(n);
    while (c != c.next) {
        for (int i = 1; i < p; i++) c = c.next;
        c.remove();
        c = c.next;
    }
    return c.element;
}
```
```java
static Singly circle(int n) {
    Singly last = new Singly(n, null);
    Singly first = last;
    for (int i = n - 1; i >= 1; i--)
        first = new Singly(i, first);
        // first.next = create a new node, 
        // update first to the new node
    last.next = first;
    return last;
}
static int josephus(int n, int p) {
    Singly pred = circle(n);
    Singly c = pred.next;
    while (c != pred) {
        for (int i = 1; i < p; i++) {
            pred = c;
            c = c.next;
        }
        pred.next = c.next; 
        c = c.next;
    }
    return c.element;
}
```
\
=== 28. Remove element in hash table
```java
static Bucket remove(Bucket b, String s) {
    if (b == null) return null;
    if (b.element.equals(s)) return b.next;
    b.next = remove(b.next, s);
    return b;
}
void remove(String s) {
    int i = hash(s);
    if (!Bucket.contains(this.buckets[i], s))
        return;
    this.buckets[i] = Bucket.remove(this.buckets[i], s);
    this.size--;
}
```

=== 37. Find element $<=$ x in Binary Search Tree
```java
static int floor(BST b, int x) {
    Integer c = null;
    while (b != null) { // invariant : c <= x
        if (b.value == x) return x;
        if (b.value > x) b = b.left;
        else { c = b.value; b = b.right; }
    }
    if (c == null) 
        throw new NoSuchElementException();
    return c;
}
```

=== 40. Add size to AVLTree
Add size field and size() method. Modify the add and remove methods.
```java
void add(Integer x) {
    if (!AVL.contains(this.root, x)) this.size++;
    this.root = AVL.add(this.root, x);
}
```