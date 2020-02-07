import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Variable {
    public static List<Variable> list = new ArrayList<Variable>();
    public static List<String> correct = new ArrayList<String>();
    public Variable loopVariable;
    public Set<Terminal> follow = new HashSet<Terminal>();
    private String literal;
    public Variable(String literal){
        this.literal = literal;

        boolean available = false;
        for (Variable var:Variable.list) {
            if(var.getLiteral().equals(literal))
                available = true;
        }
        if(!available)
            this.list.add(this);
    }

    public String getLiteral() {
        return literal;

    }

    public static int getMaxLength(){
        int maxLength=0;
        for (Variable term:Variable.list) {
            if (maxLength < term.getLiteral().length())
                maxLength = term.getLiteral().length();
        }
        return maxLength;
    }
    public static int getMinLength(){
        int minLength=Integer.MAX_VALUE;
        for (Variable term:Variable.list) {
            if (minLength > term.getLiteral().length())
                minLength = term.getLiteral().length();
        }
        return minLength;
    }


    public static boolean contains(String literal){
        for (String term:Variable.correct) {
            if(term.equals(literal))
                return true;
        }
        return false;
    }


}
