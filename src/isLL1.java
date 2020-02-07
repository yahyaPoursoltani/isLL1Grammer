import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class isLL1 {

    public String discription="";
    public Grammer grammer;
    public isLL1(Grammer grammer){
        this.grammer = grammer;
    }

    public void setFirst(Variable variable){
        ArrayList<Rule> varRule = new ArrayList<Rule>();

        for (Rule rule : grammer.rules){
            if(rule.getVariable().equals(variable))
                varRule.add(rule);
        }

    }

    public boolean hasNullRule(Variable variable){
        ArrayList<Rule> varRuls = new ArrayList<Rule>();
        for (Rule rule : grammer.rules)
        {
            if(rule.getVariable().getLiteral().equals(variable.getLiteral()))
                varRuls.add(rule);
        }

        for (Rule rule:varRuls) {
            if(rule.rule.get(0) instanceof Terminal && ((Terminal) rule.rule.get(0)).getLiteral().equals("#"))
                return true;
        }
        return false;
    }

    public boolean isNullable(Variable variable){
        if(hasNullRule(variable))
            return true;
        else{
            ArrayList<Rule> varRuls = new ArrayList<Rule>();
            for (Rule rule : grammer.rules)
            {
                if(rule.getVariable().getLiteral().equals(variable.getLiteral()))
                    varRuls.add(rule);
            }

            for(Rule rule: varRuls){
                for(int count =0 ; count<rule.rule.size() ; count++){
                    if(rule.rule.get(count) instanceof Terminal && !((Terminal) rule.rule.get(count)).getLiteral().equals("#") && !((Terminal) rule.rule.get(count)).getLiteral().equals("$"))
                        break;
                    else if(rule.rule.get(count) instanceof Variable && !isNullable((Variable) rule.rule.get(count)))
                        break;
                    else if(rule.rule.get(count) instanceof Variable && isNullable((Variable) rule.rule.get(count)))
                        continue;
                    else if(rule.rule.get(count) instanceof Terminal && ((Terminal) rule.rule.get(count)).getLiteral().equals("$"))
                        return true;

                }
            }
        }

        return false;
    }

    public Terminal[] first(Variable variable){
        Set<Terminal> first = new HashSet<Terminal>();
        ArrayList<Rule> varRuls = new ArrayList<Rule>();
        Terminal[] firstArray;
        for (Rule rule : grammer.rules)
        {
            if(rule.getVariable().getLiteral().equals(variable.getLiteral()))
                varRuls.add(rule);
        }

        for(Rule rule: varRuls){
            for(int count =0 ; count<rule.rule.size() ; count++){
                if(count==0){
                    if(rule.rule.get(0) instanceof Variable && ((Variable) rule.rule.get(0)).getLiteral().equals(rule.getVariable().getLiteral()))
                        continue;
                }
                if(rule.rule.get(count) instanceof Terminal && !((Terminal) rule.rule.get(count)).getLiteral().equals("#") && !((Terminal) rule.rule.get(count)).getLiteral().equals("$"))
                {
                    first.add((Terminal) rule.rule.get(count));
                    break;
                }
                else if(rule.rule.get(count) instanceof Variable && isNullable((Variable) rule.rule.get(count))) {
                    Variable thisVar =(Variable) rule.rule.get(count);
                    Terminal[] newFirst = first(thisVar);
                    for(Terminal term : newFirst){
                        if(!term.getLiteral().equals("#"))
                         first.add(term);
                    }
                    continue;
                }else if(rule.rule.get(count) instanceof Variable && !isNullable((Variable) rule.rule.get(count))) {
                    Variable thisVar =(Variable) rule.rule.get(count);
                    Terminal[] newFirst = first(thisVar);
                    for(Terminal term : newFirst){
                        if(!term.getLiteral().equals("#"))
                            first.add(term);
                    }
                    break;
                }else if(rule.rule.get(count) instanceof Terminal && ((Terminal) rule.rule.get(count)).getLiteral().equals("$"))
                    first.add(Terminal.getNullTerm());

            }
        }

        if(hasNullRule(variable))
            first.add(Terminal.getNullTerm());
        int count =0;
        firstArray = new Terminal[first.size()];
        for (Terminal term : first)
            firstArray[count++] = term;
        return firstArray;
    }

    public Terminal[] fallow(Variable variable){
        Set<Terminal> fallow = new HashSet<Terminal>();
        Rule[] candidates = getRul(variable);
        if(variable.getLiteral().equals(grammer.rules.get(0).getVariable().getLiteral()))
            fallow.add(Terminal.getEndTerm());
        for (Rule rule : candidates){
            int[] canIndexes = getVarIndex(variable , rule);
            for (Integer index : canIndexes){
                if(rule.rule.get(index+1) instanceof Terminal && !(((Terminal) rule.rule.get(index+1)).getLiteral().equals("$")))
                    fallow.add((Terminal) rule.rule.get(index+1));
                else if(rule.rule.get(index+1) instanceof Variable && !isLastVariable(index , rule)){
                    Terminal[] thisFirst = first((Variable) rule.rule.get(index+1));
                    for (Terminal term : thisFirst) {
                        if(!term.getLiteral().equals(Terminal.getNullTerm().getLiteral()))
                            fallow.add(term);
                    }
                }else if((((Variable) rule.rule.get(index)).getLiteral().equals(rule.getVariable().getLiteral())) && isLastVariable(index , rule)){
                    continue;
                }else if(!(((Variable) rule.rule.get(index)).getLiteral().equals(rule.getVariable().getLiteral())) && isLastVariable(index , rule) && !canCreateLoop(rule.getVariable() ,(Variable) rule.rule.get(index))){
                    Terminal[] thisFallow = fallow(rule.getVariable());
                    for (Terminal term : thisFallow) {
                        fallow.add(term);
                    }
                }else if(!(((Variable) rule.rule.get(index)).getLiteral().equals(rule.getVariable().getLiteral())) && isLastVariable(index , rule) && canCreateLoop(rule.getVariable() ,(Variable) rule.rule.get(index))){
                    Terminal[] thisFallow = otherFallow(rule.getVariable() ,(Variable) rule.rule.get(index));
                    for (Terminal term : thisFallow) {
                        fallow.add(term);
                    }

                }
                if(rule.rule.get(index+1) instanceof Variable && isNullable((Variable) rule.rule.get(index+1))){
                    for(int i=index+1 ; i<rule.rule.size() ; i++) {
                        if(rule.rule.get(i) instanceof Terminal) {
                            fallow.add((Terminal) rule.rule.get(i));
                            break;
                        }else if(rule.rule.get(i) instanceof Variable){
                            Terminal[] thisFirst = first((Variable) rule.rule.get(i));
                            for (Terminal term : thisFirst) {
                                if(!term.getLiteral().equals(Terminal.getNullTerm().getLiteral()))
                                    fallow.add(term);
                            }
                            if(!isNullable((Variable) rule.rule.get(i)))
                                break;
                        }
                    }
                }

            }
        }


        Terminal[] fallowArr = new Terminal[fallow.size()];
        int i=0;
        for (Terminal term : fallow){
            fallowArr[i++] = term;
        }

        return fallowArr;

    }







    public Terminal[] otherFallow(Variable variable , Variable dangerous){
        Set<Terminal> fallow = new HashSet<Terminal>();
        Rule[] candidates = getRul(variable);
        if(variable.getLiteral().equals(grammer.rules.get(0).getVariable().getLiteral()))
            fallow.add(Terminal.getEndTerm());
        for (Rule rule : candidates){
            int[] canIndexes = getVarIndex(variable , rule);
            for (Integer index : canIndexes){
                if(rule.rule.get(index+1) instanceof Terminal && !(((Terminal) rule.rule.get(index+1)).getLiteral().equals("$")))
                    fallow.add((Terminal) rule.rule.get(index+1));
                else if(rule.rule.get(index+1) instanceof Variable && !isLastVariable(index , rule)){
                    Terminal[] thisFirst = first((Variable) rule.rule.get(index+1));
                    for (Terminal term : thisFirst) {
                        if(!term.getLiteral().equals(Terminal.getNullTerm().getLiteral()))
                            fallow.add(term);
                    }
                }else if((((Variable) rule.rule.get(index)).getLiteral().equals(rule.getVariable().getLiteral())) && isLastVariable(index , rule)){
                    continue;
                }
                if(rule.rule.get(index+1) instanceof Variable && isNullable((Variable) rule.rule.get(index+1))){
                    for(int i=index+1 ; i<rule.rule.size() ; i++) {
                        if(rule.rule.get(i) instanceof Terminal) {
                            fallow.add((Terminal) rule.rule.get(i));
                            break;
                        }else if(rule.rule.get(i) instanceof Variable){
                            Terminal[] thisFirst = first((Variable) rule.rule.get(i));
                            for (Terminal term : thisFirst) {
                                if(!term.getLiteral().equals(Terminal.getNullTerm().getLiteral()))
                                    fallow.add(term);
                            }
                            if(!isNullable((Variable) rule.rule.get(i)))
                                break;
                        }else if(!(((Variable) rule.rule.get(index)).getLiteral().equals(rule.getVariable().getLiteral())) && isLastVariable(index , rule) && !canCreateLoop(rule.getVariable() ,(Variable) rule.rule.get(index))){
                            Terminal[] thisFallow = fallow(rule.getVariable());
                            for (Terminal term : thisFallow) {
                                if(!term.getLiteral().equals(dangerous.getLiteral()))
                                    fallow.add(term);
                            }
                        }
                    }
                }

            }
        }


        Terminal[] fallowArr = new Terminal[fallow.size()];
        int i=0;
        for (Terminal term : fallow){
            fallowArr[i++] = term;
        }

        return fallowArr;

    }




    public Rule[] getRul(Variable variable){
        ArrayList<Rule> rules = new ArrayList<Rule>();
        for(Rule rule : grammer.rules){
            for(int i=0 ; i<rule.rule.size() ; i++){
                if(rule.rule.get(i) instanceof Variable && ((Variable) rule.rule.get(i)).getLiteral().equals(variable.getLiteral()))
                {
                    rules.add(rule);
                    break;
                }
            }
        }

        Rule[] rulesArr = new Rule[rules.size()];
        for (int i=0 ; i<rules.size() ; i++)
            rulesArr[i] = rules.get(i);

        return rulesArr;
    }

    public int[] getVarIndex (Variable variable , Rule rule){
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for(int i=0 ; i<rule.rule.size() ; i++){
            if(rule.rule.get(i) instanceof Variable && ((Variable) rule.rule.get(i)).getLiteral().equals(variable.getLiteral()))
                indexes.add(i);
        }

        int[] indexArr = new int[indexes.size()];
        for (int i=0 ; i<indexes.size() ; i++)
            indexArr[i] = indexes.get(i);
        return indexArr;
    }

    public boolean isLastVariable(int index , Rule rule){
        int count = index+1;
        while (!(rule.rule.get(count) instanceof Terminal && ((Terminal) rule.rule.get(count)).getLiteral().equals("$")))
        {
            if(rule.rule.get(count) instanceof Variable && isNullable((Variable) rule.rule.get(count)))
                count++;
            else
                return false;
        }
        return true;
    }

    public Variable[] getLastVar(Variable variable){
        Set<Variable> lastVar = new HashSet<Variable>();
        ArrayList<Rule> varRuls = new ArrayList<Rule>();
        for (Rule rule : grammer.rules)
        {
            if(rule.getVariable().getLiteral().equals(variable.getLiteral()))
                varRuls.add(rule);
        }
        for (Rule rule : varRuls){
            int breakPoint=0;
            for (int count = 0 ; count<rule.rule.size() ; count++){
                if(rule.rule.get(count) instanceof Variable && isLastVariable(count , rule)) {
                    lastVar.add((Variable) rule.rule.get(count));
                    breakPoint = count;
                    break;
                }
            }

            for (int count = breakPoint ; count<rule.rule.size() ; count++){
                if(rule.rule.get(count) instanceof Variable && isNullable((Variable) rule.rule.get(count))) {
                    lastVar.add((Variable) rule.rule.get(count));

                }
            }
        }
        Variable[] varArr = new Variable[lastVar.size()];
        int count=0;
        for (Variable v : lastVar){
            varArr[count++] = v;
        }

        return varArr;

    }

    public boolean canCreateLoop(Variable startVariable , Variable currentVariable){
        Variable[] last = getLastVar(currentVariable);//آیا متغیر فعلی ، در این آرایه قرار دارد؟
        for (Variable v : last){
            if(v.getLiteral().equals(startVariable.getLiteral()))
                return true;
        }
        return false;
    }
    //is LL1????

    public Terminal[] getFirstOfRule(Rule rule){
        Set<Terminal> first = new HashSet<>();
        for(int i=0 ; i<rule.rule.size() ; i++){
            if(rule.rule.get(i) instanceof Terminal && !((Terminal) rule.rule.get(i)).getLiteral().equals("#") && !((Terminal) rule.rule.get(i)).getLiteral().equals("$")) {
                first.add((Terminal) rule.rule.get(i));
                break;
            }else if(rule.rule.get(i) instanceof Variable && isNullable((Variable)rule.rule.get(i))){
                Terminal[] newFirst = first((Variable)rule.rule.get(i));
                for(Terminal trm : newFirst){
                    if(!trm.getLiteral().equals("#"))
                        first.add(trm);
                }
            }else if(rule.rule.get(i) instanceof Variable && !isNullable((Variable)rule.rule.get(i))){
                Terminal[] newFirst = first((Variable)rule.rule.get(i));
                for(Terminal trm : newFirst){
                    if(!trm.getLiteral().equals("#"))
                        first.add(trm);
                }
                break;
            }if(rule.rule.get(i) instanceof Terminal && !((Terminal) rule.rule.get(i)).getLiteral().equals("$")) {
                first.add(Terminal.getNullTerm());
                break;
            }
        }

        Terminal[] trmArr = new Terminal[first.size()];
        int count=0;
        for (Terminal trm : first){
            trmArr[count++] = trm;
        }
        return trmArr;
    }

    public boolean isNullableRule(Rule rule){
        int count =0;
        boolean exit=false;
        while (!exit && !(rule.rule.get(count) instanceof Terminal && ((Terminal) rule.rule.get(count)).getLiteral().equals("$")))
        {
            if(rule.rule.get(count) instanceof Terminal && !((Terminal) rule.rule.get(count)).getLiteral().equals("#"))
                return false;
            else if(rule.rule.get(count) instanceof Variable && !isNullable((Variable) rule.rule.get(count)))
                return false;
            else if(rule.rule.get(count) instanceof Terminal && ((Terminal) rule.rule.get(count)).getLiteral().equals("$"))
                return true;
            count++;
        }

        if(rule.rule.get(count) instanceof Terminal && ((Terminal) rule.rule.get(count)).getLiteral().equals("$"))
            return true;
        else return false;

    }

    //Methodes for LL1 conditions.
    public boolean hasNullSubscribeFirst(Rule rule1 , Rule rule2) {
        Terminal[] first1Arr = getFirstOfRule(rule1);
        Terminal[] first2Arr = getFirstOfRule(rule2);
        //insert into a set
        Set<String> first1 = new HashSet<String>();
        Set<String> first2 = new HashSet<String>();
        for (Terminal term : first1Arr)
            first1.add(term.getLiteral());
        for (Terminal term : first2Arr)
            first2.add(term.getLiteral());
        //
        if (first1.size() > first2.size()) {
            for (String term : first2)
                if (first1.contains(term))
                    return false;
        } else if (first2.size() >= first1.size()) {
            for (String term : first1)
                if (first2.contains(term))
                    return false;
        }

        return true;
    }

    public boolean hasOneNullOrLess(Variable variable){
        int nulls = 0;
        Rule[] rules = getRulesArray(variable);
        for (Rule rule : rules){
            if(isNullableRule(rule)){
                discription += " The rule " + rule + " construct null value.\n";
                nulls++;
            }
        }

        if(nulls<=1) {
            discription += "We have " + nulls + " null rules.\n";
            return true;
        }
        else{
            discription += "We have " + nulls + " null rules.so , This is not a LL1 Grammer.\n";
            return false;
        }
    }

    public boolean hasNullFallowSubscribe(Variable variable){
        Rule[] rules = getRulesArray(variable);
        Terminal[] fallowArr = fallow(variable);
        for (Rule rule : rules){
            if(isNullableRule(rule))
                continue;
            else{
                Terminal firstArr[] = getFirstOfRule(rule);
                //insert into a set
                Set<String> first = new HashSet<String>();
                Set<String> fallow = new HashSet<String>();
                for (Terminal term : fallowArr)
                    fallow.add(term.getLiteral());
                for (Terminal term : firstArr)
                    first.add(term.getLiteral());
                //
                if (first.size() >= fallow.size()) {
                    for (String term : fallow)
                        if (first.contains(term)) {
                            discription += term + " is in fallow of the " + variable.getLiteral() + " and The first of The rule " + rule +" . so This is not a LL1 Grammer.\n";
                            return false;
                        }
                } else if (fallow.size() > first.size()) {
                    for (String term : first)
                        if (fallow.contains(term)) {
                            discription += term + " is in fallow of the " + variable.getLiteral() + " and The first of The rule " + rule +" . so This is not a LL1 Grammer.\n";
                            return false;
                        }
                }
                discription +=" The fallow of The variable " + variable.getLiteral() + " and The first of The rule " + rule +" . so This is null.\n";
            }


        }
        return true;
    }

    Rule[] getRulesArray(Variable variable){
        ArrayList<Rule> rules = new ArrayList<Rule>();
        for (Rule rule : grammer.rules){
            if(rule.getVariable().getLiteral().equals(variable.getLiteral()))
                rules.add(rule);
        }

        Rule[] ruleArr = new Rule[rules.size()];
        for (int i=0 ; i<rules.size() ; i++)
            ruleArr[i] = rules.get(i);

        return ruleArr;
    }

    public boolean checkLL1(){
        this.discription="";
        for (Variable variable : Variable.list){
            discription += "\n\n\nVariable " + variable.getLiteral() + " :  \n";
            Rule[] rules = getRulesArray(variable);

            if(rules.length==1){
                discription += "This Variable has only one rule.so it passed\n";
                continue;
            }
            for (int i=0 ; i<rules.length ; i++) {
                if(rules[i].rule.get(i) instanceof Variable && ((Variable) rules[i].rule.get(i)).getLiteral().equals(rules[i].getVariable().getLiteral())) {
                    discription += "chapgardi ... not LL1";
                    return false;
                }
                    for (int j = 0; j < rules.length; j++) {
                    if (i != j) {
                        discription += "The subscribtion of first of rules " + rules[i] + " and  " + rules[j] + " ";
                        if (!hasNullSubscribeFirst(rules[i], rules[j])) {
                            discription += " is not null.so this is not a LL1 grammer. \n";
                            return false;
                        }
                        discription += " is null. \n";
                    }

                }
            }

                discription+="\n\n\n\n\n condition2: \n";

            if(!hasOneNullOrLess(variable))
                return false;

            discription+="\n\n\n\n\n condition3: \n";
            if(!hasNullFallowSubscribe(variable))
                return false;
        }
        discription += "all of The conditions Passed.So This is a LL1 grammer";
        return true;
    }

    @Override
    public String toString() {
        String total ="In The Name Of Allah \n\n\n";
        total += "Your Grammer rules are : \n" + grammer + "\n";
        //print first and fallow
        for(int count=0 ; count<Variable.list.size() ; count++)
        {
            Terminal[] first = this.first(Variable.list.get(count));
            total += "first(" + Variable.list.get(count).getLiteral() + ") = { ";
            for (Terminal term : first){
                total += term.getLiteral() + "   ";
            }
            total +="}\n";
        }



        for(int count=0 ; count<Variable.list.size() ; count++)
        {
            Set<Terminal> fallow = new HashSet<>(Arrays.asList(this.fallow(Variable.list.get(count))));
            total += "fallow(" + Variable.list.get(count).getLiteral() + ") = { ";
            for (Terminal term : fallow){
                total += term.getLiteral() + "   ";
            }
            total +="}\n";
        }

        total+= " ********************* is This a LL1 grammer? \n";
        this.checkLL1();
        total+=discription + "\n *************************************************************\n";
        total+= "Prepared By Yahya Poursoltani , CS student at shahed Univ";
        return total;
    }

    public void print() throws Exception{
        String path = this.grammer.path;
        String newPath ="";
        String[] pathParts = path.split("/");
        for (int i=0 ; i<pathParts.length-1 ; i++){
            newPath +="/" + pathParts[i];
        }
        newPath += "/" +"out_" +pathParts[pathParts.length-1];

        PrintWriter writer = new PrintWriter(newPath, "UTF-8");
        writer.println(this);
        writer.close();
    }
}
