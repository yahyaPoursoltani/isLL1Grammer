import java.util.ArrayList;
import java.util.List;

public class Terminal {
    private String literal;
    public static List<Terminal> list = new ArrayList<Terminal>();
    public static List<String> correct = new ArrayList<String>();

    public Terminal(String literal){
        this.literal = literal;
        boolean available = false;
        for (Terminal term:Terminal.list) {
            if(term.getLiteral().equals(literal))
                available = true;
        }
        if(!available)
            this.list.add(this);
    }

    public String getLiteral() {
        return literal;
    }

    public static void addTerminal(String terminal){
        Terminal.list.add(new Terminal(terminal));
    }

    public static int getMaxLength(){
        int maxLength=0;
        for (Terminal term:Terminal.list) {
            if (maxLength < term.getLiteral().length())
                maxLength = term.getLiteral().length();
        }
        return maxLength;
    }
    public static int getMinLength(){
        int minLength=Integer.MAX_VALUE;
        for (Terminal term:Terminal.list) {
            if (minLength > term.getLiteral().length())
                minLength = term.getLiteral().length();
        }
        return minLength;
    }

    public static boolean contains(String literal){
        for (String term:Terminal.correct) {
            if(term.equals(literal))
                return true;
        }
        return false;
    }

    public static Terminal getNullTerm(){
        for(Terminal term : Terminal.list)
        {
            if (term.getLiteral().equals("#"))
                return term;
        }
        return new Terminal("#");
    }

    public static Terminal getEndTerm(){
        for(Terminal term : Terminal.list)
        {
            if (term.getLiteral().equals("$"))
                return term;
        }
        return new Terminal("$");
    }
}
