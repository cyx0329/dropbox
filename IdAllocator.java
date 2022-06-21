/*  Id Allocator / Number / Phone Number
    Write a class for an id allocator that can allocate and release ids
    
    Id allocator. Capacity 0-N.
    int allocate() : returns avilable ID within 0-N range.
    int release(int id) : releases given ID and that ID becomes available for allocation.
    
    Things to consider:
    what happens if all IDs used up? throws Exception or return -1
    release invalid ID or release id that is not allocated, how to handle? just return or throw Exception?
*/

public class Allocator {
    private Queue<Integer> avail;
    private Set<Integer> used;
    private int MAXID;
    
    public Allocator(int maxID) {
        this.MAXID = maxID;
        this.avail = new LinkedList();
        this.used = new HashSet();
        
        for(int i = 0; i < maxID; i++) {
            avail.add(i);
        }
    }
    
    public int allocate() {
        if(avail.isEmpty()) return -1;
        int id = avail.poll();
        used.add(id);
        return id;
    }
    
    public void release(int id) {
        if(id < 0 || id >= MAXID) return;
        if(used.contains(id)) {
            used.remove(id);
            avail.add(id);
        }
    }
    
    public boolean check(int id) {
        if(id < 0 || id >= MAXID) return false;
        return !used.contains(id);
    }
    
    //O(1) time in allocate and release
    //Space is O(n), heavy, because of the data structure of queue and set too
      
    //*If no need to maintain a ordering allocation, can use just one set called available to achieve the above. Allocate is get and remove the first element in the set. (available.iterator().first())
}


/*
FOLLOW UP:
We want to save space we dont care about runtime time complexisty how would you save space?
You might be asked to estimate the amount of memory you need for the above implementation.
You will be asked to make a more space-efficient implementation in which allocate and release might take longer than O(1).
For this, you can use a boolean array (a.k.a. a BitSet in Java, a bit-vector in other languages)
This uses max_id // (8 * 1024 * 1024) MB
*/

//however each boolean member in a boolean[] usually consumes one byte instead of just one bit.
//BitSet approach
public class Allocator {
    private BitSet bitSet;
    private int MAXID;
    private int next_avail;
    
    public Allocator(int maxId) {
        MAXID = maxId;
        bitSet = new BitSet(maxId);
        this.next_avail = 0;
    }
    
    public int allocate() {
        if(next_avail == MAXID) return -1;
        int id = next_avail;
        bitSet.set(id);
        next_avail = bitSet.nextClearBit(id);
        return id;
    }
    
    public int release(int id) {
        if(id < 0 || id >= MAXID) return -1;
        if(bitSet.get(id)) {
            bitSet.clear(id);
            next_avail = Math.min(next_avail, id);
        }
    }
    
    public boolean check(int id) {
        if(id < 0 || id >= MAXID) return false;
        return !bitSet.get(id);
    }
    
    //Time: worst case O(n) in searching for next available id
    //Space is much efficient: O(c), wouldn’t say it is O(1) because we still need max_size number of bits
}

/*
FOLLOW UP:
This is the part where most people flunk. Come up with a way that is a little faster than O(n) for both allocate and release.
You can use a bit more space.
The hard way is to use an interval tree or a segment tree.
The easy way is to use a binary heap.

An array representation of tree is used to represent Segment Trees. For each node at index i, the left child is at index 2*i+1, right child at 2*i+2 and the parent is at  ⌊(i – 1) / 2⌋.

A Binary Heap is a Complete Binary Tree. A binary heap is typically represented as array. The representation is done as:

The root element will be at Arr[0].
Below table shows indexes of other nodes for the ith node, i.e., Arr[i]:
Arr[(i-1)/2]	Returns the parent node
Arr[(2*i)+1]	Returns the left child node
Arr[(2*i)+2]	Returns the right child node
The traversal method use to achieve Array representation is Level Order
*/

public class Allocator {
    private BitSet bitSet;
    private int MAXID;
    
    public Allocator(int maxId) {
        this.MAXID = maxId;
        this.bitSet = new BitSet(maxId*2 -1);
    }
    
    public int allocate() {
        int index = 0;
        while(index < MAXID-1) {
            if(!bitSet.get(index*2+1)) {
                index = index*2+1;  //left child
            } else if(!bitSet.get(index*2+2)) {
                index = index*2+2;
            } else {
                return -1;
            }
        }
        
        bitSet.set(index);
        updateTree(index);
        return index-MAXID+1;
    }
    
    public void release(int id) {
        if(id < 0 || id >= MAXID) return;
        if(bitSet.get(id+MAXID-1)) {
            bitSet.clear(id+MAXID-1);
            updateTree(id+MAXID-1);
        }
    }
    
    public boolean check(int id) {
        if(id < 0 || id >= MAXID) return false;
        return !bitSet.get(id+MAXID-1);
    }
    
    public void updateTree(int index) {
        while(index > 0) {
            int parent = (index-1)/2;
            if(index%2 == 1) { //left child
                if(bitSet.get(index) && bitSet.get(index+1)) {
                    bitSet.set(parent);
                } else {
                    bitSet.clear(parent); // this is required for release id
                }
            } else { //right child
                if(bitSet.get(index) && bitSet.get(index-1)) {
                    bitSet.set(parent);
                } else {
                    bitSet.clear(parent); // this is required for release id
                }
            }
            index = parent;
        }
    }
    
    //Using the idea of binary tree (in heap array representation in bitset)
    //Time: O(log n) time in allocate and release
    //Space: O(n), 2 * space used in bitset
}
