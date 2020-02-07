import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Grammer {
    private List<String> ruleStr = new ArrayList<String>();
    public List<Rule> rules = new ArrayList<Rule>();
    public String path;
    public Grammer(String path) throws Exception{
        readGrammer(path);
        this.path = path;
        for (String rule:this.ruleStr) {
            rules.add(new Rule(rule));
        }
    }

    private void readGrammer(String path) throws Exception {
        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String allTerminals = bufferedReader.readLine();
        String[] terminals = allTerminals.split(",");
        for (String terminal:terminals) {
            if(terminal != "")
                Terminal.correct.add(terminal);
        }

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] rulePart = line.split("::=");
            Variable.correct.add(rulePart[0].substring(1 , rulePart[0].length()-1));
            String[] rules = rulePart[1].split("/");
            for (String rule:rules) {
                   this.ruleStr.add(rulePart[0] + "::=" + rule);
            }

        }
        fileReader.close();
    }
    @Override
    public String toString(){
        String grammer="";
        for (Rule r:this.rules) {
            grammer+=r.toString() + "\n";
        }
        return grammer;
    }

    
}

