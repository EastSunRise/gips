/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package matrix;

/**
 *
 * @author sss
 */
public class CodonChange {

       private String originCodon;
       private String mutationCodon;
       
       public void setOriginCodon(String codon){
              this.originCodon=codon;
       }
       public void setMutationCodon(String codon){
              this.mutationCodon=codon;
       }

       public String getOriginCodon(){
              return this.originCodon;
       }
       public String getMutationCodon(){
              return this.mutationCodon;
       }
    
}
