package Framework.Gps;

import java.util.ArrayList;

/**
 *
 * @author George Hatt
 */
public class GpsTagTree {
    private Node n = new Node('a',0);
    
    /**
     * add a GpsTag to the tree under the name given
     * if a GpsTag with the same name exists it will be overridden
     * @param s
     * @param data
     */
    public void addGpsTag(String s, GpsTag data){
       n.addGpsTag(s, data);
    }
    
    /**
     * extract the GpsTag from the tree with the name given
     * @param s
     * @return
     */
    public GpsTag getGpsTag(String s){
       return n.getGpsTag(s);
    }
    
    class Node{
        private int level;
        private char letter;
        private GpsTag data;
        private ArrayList<Node> nodes;
         
        public Node(char c, int level){
            this.level = level;
            this.letter = c;
            nodes = new ArrayList();
            data = null;
        }
        
        public void addGpsTag(String s, GpsTag data){
            // if at end of string
            if(s.length() == (level)){
                this.data = data;
            }
            else{ // keep searching
                boolean letterExist = false;

                // see if node exists
                for(Node n : nodes){
                    if(n.getLetter() == s.charAt(level)){
                        n.addGpsTag(s, data);
                        letterExist = true;
                    }
                } 

                // add new node
                if(!letterExist){
                    nodes.add(0,new Node(s.charAt(level),level+1));
                    nodes.get(0).addGpsTag(s, data);
                }
            }
        }

        public GpsTag getGpsTag(String s){
            // if at end of string
            if(s.length() == level){
                // does data exist for this name
                return data;
            }
            else{
                // see if node exists
                for(Node n : nodes){
                    if(n.getLetter() == s.charAt(level)){
                        return n.getGpsTag(s);
                    }
                } 
                
                return null;
            }
        }
        
        public char getLetter(){
            return letter;
        }
    }
}
