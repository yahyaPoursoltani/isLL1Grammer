import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Main {
     public static void main(String[] args) throws Exception {
         try {
             boolean ch = false;

             Grammer g = new Grammer("/home/poursoltani/Desktop/final2.txt");
             isLL1 gr = new isLL1(g);
             int count = 0;
             for (Variable v : Variable.list) {

                 Rule[] rules = gr.getRulesArray(v);

                 for (int i = 0; i < rules.length; i++) {
                     if (rules[i].rule.get(i) instanceof Variable && ((Variable) rules[i].rule.get(i)).getLiteral().equals(rules[i].getVariable().getLiteral())) {
                         ch = true;
                     }
                 }
                 //gr.print();
                 if (ch) {
                     System.out.println("it has left recurtion." + rules[count]);
                 } else {
                     System.out.println(gr);
                 }

             }

             if (ch) {
                 System.out.println("it is Not LL1");
             } else {
                 System.out.println(gr);
             }
         }catch (StackOverflowError e){
             System.out.println("it is Not LL1 Left");
         }
     }
}
