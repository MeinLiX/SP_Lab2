import java.util.*;
import java.io.*;
import java.lang.*;
import java.io.File;

class Instruction {
    public int from;
    public int to;
    public char letter;

    Instruction(int _from, char _letter, int _to) {
        from = _from;
        to = _to;
        letter = _letter;
    }

    @Override
    public String toString() {
        return "{" + from + "," + to + ":" + letter + "}";
    }
}

class Automate {
    protected int countOfStates;
    protected int countOfLetters;
    protected int firstState;

    public ArrayList<Instruction>[] edgesOfTheGraph;
    public HashSet<Integer> lastStates;

    public Automate() {
        this(100, 100, 0);
    }

    public Automate(int _states, int _letters, int _firstState) {

        countOfStates = ++_states; // adding first state
        countOfLetters = _letters;
        firstState = _firstState;

        edgesOfTheGraph = new ArrayList[countOfStates];
        for (int i = 0; i < countOfStates; i++)
            edgesOfTheGraph[i] = new ArrayList<Instruction>();
        lastStates = new HashSet<Integer>();
    }

    public void AddInstruction(Instruction instr) {
        if (instr != null &&
                instr.from < countOfStates &&
                instr.to < countOfStates &&
                !edgesOfTheGraph[instr.from].contains(instr)) {
            edgesOfTheGraph[instr.from].add(instr);
        } else if (instr.from >= countOfStates || instr.to >= countOfStates)
            throw new Error("Out of the Limit States.");

    }

    public boolean Validation() {
        HashSet<Character> foundedLetters = new HashSet<Character>();
        HashSet<Integer> foundedStates = new HashSet<Integer>();
        for (var eotg : edgesOfTheGraph) {
            for (var i : eotg) {
                foundedLetters.add(i.letter);
                for (int j : new int[]{i.from, i.to}) {
                    foundedStates.add(j);
                }
            }
        }
        if (foundedLetters.size() != countOfLetters && foundedStates.size() != countOfStates)
            return false;

        return true;
    }

    @Override
    public String toString() {
        String str = "";
        for (var eotg : edgesOfTheGraph)
            for (var i : eotg)
                str += i.toString() + "\n";

        return str;
    }
}

class DFS {
    private HashSet<Integer> currentStates;
    private String path;
    private Automate automate;
    public boolean conclusion;

    public DFS(Automate _automate, String _path) {
        if (_automate != null)
            automate = _automate;
        else throw new Error("Automate must have a point");
        if (_path != null && _path.matches("^[A-Z]*$"))
            path = _path;
        else throw new Error("Incorrect DFS path");

        conclusion = false;
        currentStates = new HashSet<Integer>();
    }

    public boolean search(int _currentStates, String _currentPath) {

        if (_currentPath.length() > path.length()) {
           // System.out.println("Collected string is incorrect.");
            return false;
        }
        if (_currentPath.compareTo(path) == 0) {
           // System.out.println("Collected string is equal to " + path);
            return automate.lastStates.contains(_currentStates);
        }

        boolean res = false;
        for (var i : automate.edgesOfTheGraph[_currentStates]) {
            if (i.letter == path.charAt(_currentPath.length())) {
                res |= search(i.to, _currentPath + i.letter);                    //Nondeterministic
            }
        }
       // System.out.println("Solution ("+path.charAt(_currentPath.length())+") is " + (res ? "" : "not") + " found..." + "| STATE = " + _currentStates);

        conclusion=res;
        return res;
    }

    @Override
    public String toString() {
        return "Solution ("+path+") is " + (conclusion ? "" : "not") + " found...";
    }
}


public class Main {
    public static void main(String[] args) {
        new Main().startReadData("D:\\MeinLiX\\project\\source\\repos UNIVER\\3ks\\SysProga\\SP_Lab2_Hrohul\\src\\file.txt");
    }

    public void startReadData(String path) {
        try {
            readDataAndSolve(path);
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void readDataAndSolve(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        Automate MyA = new Automate(sc.nextInt(), sc.nextInt(), sc.nextInt());

        int countOfTerminate = sc.nextInt();
        while (countOfTerminate-- > 0) {
            MyA.lastStates.add(sc.nextInt());
        }
        int countOfOperations = sc.nextInt();
        while (countOfOperations-- > 0) {
            MyA.AddInstruction(new Instruction(sc.nextInt(), sc.next().charAt(0), sc.nextInt()));
        }

        //if (!MyA.Validation()) {
        //    throw new Error("Don't validation automate.");
        //}

        Scanner in = new Scanner(System.in);
        while(true) {
            DFS adfs = new DFS(MyA, in.nextLine());
            adfs.search(MyA.firstState, "");
            System.out.println(adfs.toString());
        }
    }
}
/* FILE EXAMPLE
3
3
0
2
1 2
4
0 B 1
0 C 2
1 B 1
1 A 2
*/
