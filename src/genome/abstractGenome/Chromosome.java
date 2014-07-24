/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genome.abstractGenome;

/**
 *
 * @author sss
 */
public abstract class Chromosome {
        private String ID;

        public Chromosome(Chromosome chromosome) {
                this.setID(chromosome.getID());
        }

        public Chromosome(String chrID) {
                this.setID(chrID);
        }
        
        private void setID(String id){
                this.ID=id;
        }
        public String getID(){
                return this.ID;
        }   
}
