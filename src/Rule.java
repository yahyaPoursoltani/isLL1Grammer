import java.util.ArrayList;
import java.util.List;

public class Rule {
    private Variable variable;
    public ArrayList rule = new ArrayList();
    private int index=0;
    private String buffer="";
    private String ruleStr;

    public Rule(String rule) throws Exception{
        createRule(rule);
    }

    public void createRule(String rule) throws Exception{
        String[] ruleParts = rule.split("::=");
        if(ruleParts.length != 2)
            throw new Exception("invalid Terminal used!");
        this.variable = new Variable(ruleParts[0].substring(1,ruleParts[0].length()-1));
        this.ruleStr = ruleParts[1];
        checkTerminal();
    }

    public void checkTerminal() throws Exception{
        if(index < ruleStr.length()) {
            if (currentChar() == '<') {
                buffer = "";
                index++;
                checkVariable();
            }else {
                while (!acceptTerminal(buffer)) {
                    buffer += currentChar();
                    index++;
                }
                if (!acceptTerminal(buffer))
                    throw new Exception("Invalid Terminal.");
                else {
                    if(false/*matchToOther()*/){
                      //  System.out.println("oh my GOD!");
                    }else {
                        rule.add(new Terminal(buffer));
                        buffer = "";
                    }
                    checkTerminal();
                }
            }

        }
    }

    public void checkVariable() throws Exception{
        if(index < ruleStr.length()) {
            int varCount = 0;
            while (currentChar() != '>') {
                if (currentChar() == '<') {
                    if (acceptTerminal("<")) {
                        this.rule.add(new Terminal("<"));
                    } else throw new Exception("invalid Terminal");

                    index -= varCount;
                    buffer="";
                    checkTerminal();
                    break;
                } else {
                    buffer += currentChar();
                    index++;
                }
                varCount++;
            }
            if (acceptVariable(buffer)) {
                rule.add(new Variable(buffer));
                index++;
                buffer = "";
                checkTerminal();
            }
        }
    }

    private char currentChar(){
        char cur = ruleStr.charAt(index);
        return cur;
    }

    public boolean acceptTerminal(String terminal){
        if(Terminal.contains(terminal))
            return true;
        else
            return false;
    }

    private boolean acceptVariable(String variable){
        if(Variable.contains(variable))
            return true;
        else
            return false;
    }

    private boolean matchToOther(){
        int count =0;
        List<Terminal> matched = new ArrayList<Terminal>();
        for (int listCount=0 ; listCount<Terminal.list.size() ; listCount++) {
            String c = Terminal.list.get(listCount).getLiteral();
            if(Terminal.list.get(listCount).getLiteral().startsWith(buffer))
                count++;
        }
        if (count ==1){
            return false;
        }else
            return true;
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public String toString(){
        String rule ="<" + this.variable.getLiteral()+">" + "::=";
        for(int count = 0 ; count<this.rule.size() ; count++){
            if(this.rule.get(count) instanceof Variable)
            {
                rule+= "<" + ((Variable) this.rule.get(count)).getLiteral() + ">";
            }else if(this.rule.get(count) instanceof Terminal){
                rule+= ((Terminal) this.rule.get(count)).getLiteral();
            }
        }
        return rule;
    }
}
