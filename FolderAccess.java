/*
Given child-parent folder relationships, and user has access to folders in the access set. Find if user has access to particular folder.
/A
|__ _ /B
        |___ /C <-- access
        |___ /D
|___ /E <-- access
        |___ /F


folders = [("B", "A"),
("C", "B"),
("D", "B"),
("E", "A"),
("F", "E")]

access = set("C", "E")

has_access(String folder_name) -> boolean
has_access("B") -> false
has_access("C") -> true
has_access("F") -> true

Things to consider:
The child-parent relationships are given in HashMap?
can a folder has more than one parent? If so, how is it represented?

Linux command to create symbolic link to folder:
ln -s path-to-actual-folder name-of-link
ls -ld name-of-link

Would it be possible a circular child-parent relationship? if so, mark visited.
Need to return false if you are checking a folder non-exiting.
*/

public class FolderAccess {
    Map<String, String> map;
    Set<String> set;
    public FolderAccess(String[][] folders, Set<String> access) {
        this.map = new HashMap();
        for(String[] f : folders) {
            if(!map.containsKey(f[1])) map.put(f[1], null);
            map.put(f[0], f[1]);
        }
        this.set = access;
    }
    public boolean hasAccess(String folderName) {
        String curr = folderName;
        while(curr != null) {
            if(set.contains(curr)) return true;
            curr = map.get(curr);
        }
        return false;
    }
    
    public Set simplfyAccess() {
        Set<String> simple = new HashSet();
        
        for(String folder : set) {
            String parent = map.get(folder);
            boolean delete = false;
            while(parent != null && !delete) {
                if(set.contains(parent)) {
                    delete = true;
                } else {
                    parent = map.get(parent);
                }
            }
            if(!delete) simple.add(folder);
        }
        
        return simple;
    }
    //mlgn
    
    public static void main(String[] args) {
        Map<String, String> foldersParent = new HashMap<>();
        foldersParent.put("B", "A");
        foldersParent.put("C", "B");
        foldersParent.put("D", "B");
        foldersParent.put("E", "A");
        foldersParent.put("F", "E");
        String[][] folders = new String[][]{{"B", "A"},
                                      {"C", "B"},
                                      {"D", "B"},
                                      {"E", "A"},
                                      {"F", "E"}};

        Set<String> access = new HashSet<>();
        access.add("C");
        //access.add("E");
        //access.add("A");
        access.add("B");
        access.add("F");

        FolderAccess solver = new FolderAccess(folders, access);
        Set<String> simple = solver.simplfyAccess();
        System.out.println(solver.hasAccess("B"));
        System.out.println(solver.hasAccess("C"));
        System.out.println(solver.hasAccess("F"));
        System.out.println(solver.hasAccess("G"));
        for(String f : simple) {
            System.out.println(f);
        }
        assert solver.hasAccess("B") == false;
        assert solver.hasAccess("C") == true;
        assert solver.hasAccess("F") == true;
        assert solver.hasAccess("G") == false; //G is not in the folders structure
    }  
}
