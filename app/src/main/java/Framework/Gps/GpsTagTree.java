package Framework.Gps;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

import Framework.FileIO;

/**
 *
 * @author George Hatt/Michal Zak
 */
public class GpsTagTree implements Serializable{
    static final long serialVersionUID = 2L;
    static final String FILE_NAME = "storedLocations.ser";

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
        static final long serialVersionUID = 3L;

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


    public void save(Context context){
        FileIO.saveToFile(this, context, FILE_NAME);
    }

    public static GpsTagTree loadFromFile(Context context){
        return (GpsTagTree) FileIO.readFromFile(context, FILE_NAME);
    }

    public boolean delete(Context context){
        return FileIO.deleteFile(context, FILE_NAME);
    }
}
