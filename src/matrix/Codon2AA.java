/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package matrix;

import java.util.HashMap;

/**
 *
 * @author zzx
 */
public class Codon2AA {
       
       HashMap<String, String> codon2AA=new HashMap<>();
       
       public Codon2AA(String codonType) {
               String line=file.Config.getItem("codon."+codonType);
               String split[]=line.split(";");
               for(int i=0;i<split.length;i++){
                      String codon=split[i].split(",")[0].trim().toString();
                      String AA=split[i].split(",")[1].trim().toString();
                      this.putCodon(codon, AA); 
                }
        }
       private void putCodon(String codon,String aminoAcid){
              this.codon2AA.put(codon, aminoAcid);
       }
       public final String getAminoAcid(String codon){
              if(!this.codon2AA.keySet().contains(codon))
                     return null;
              else return this.codon2AA.get(codon);
       }
       
//     private static final HashMap<String,String> codon2AATable =new HashMap<String,String>(){
//           {
//                  //Isoleucine
//                  put("ATT", "I");put("ATC", "I");put("ATA", "I");
//                  //Leucine
//                  put("CTT", "L");put("CTC", "L");put("CTA", "L");put("CTG", "L");put("TTA", "L");put("TTG", "L");
//                  //Valine
//                  put("GTT", "V");put("GTC", "V");put("GTA", "V");put("GTG", "V");
//                  // PhenylalaninecodonTable
//                  put("TTT", "F");put("TTC", "F");
//                  //Methionine---initiation code
//                  put("ATG", "M");
//                  //Cysteine
//                  put("TGT", "C");put("TGC", "C");
//                  //Alanine
//                  put("GCT", "A");put("GCC", "A");put("GCA", "A");put("GCG", "A");
//                  //Glycine
//                  put("GGT", "G");put("GGC", "G");put("GGA", "G");put("GGG", "G");
//                  //Proline
//                  put("CCT", "P");put("CCC", "P");put("CCA", "P");put("CCG", "P");
//                  //Threonine
//                  put("ACT", "T");put("ACC", "T");put("ACA", "T");put("ACG", "T");
//                  //Serine
//                  put("TCT", "S");put("TCC", "S");put("TCA", "S");put("TCG", "S");put("AGT", "S");put("AGC", "S");
//                  //Tyrosine
//                  put("TAT", "Y");put("TAC", "Y");
//                  //Tryptophan
//                  put("TGG", "W");
//                  //Glutamine
//                  put("CAA", "Q");put("CAG", "Q");
//                  //Asparagine
//                  put("AAT", "N");put("AAC", "N");
//                  //Histidine
//                  put("CAT", "H");put("CAC", "H");
//                  //Glutamic acid
//                  put("GAA", "E");put("GAG", "E");
//                  //Aspartic acid
//                  put("GAT", "D");put("GAC", "D");
//                  //Lysine
//                  put("AAA", "K");put("AAG", "K");
//                  //Arginine
//                  put("CGT", "R");put("CGC", "R");put("CGA", "R");put("CGG", "R");put("AGA", "R");put("AGG", "R");
//                  //Stop codon
//                  put("TAA", "*");put("TAG", "*");put("TGA", "*");   
//           }
//     };
    
//    public static final String getAminoAcid(String codon){  
//           if(!Codon2AA.codon2AATable.keySet().contains(codon))
//                  return null;
//           else return Codon2AA.codon2AATable.get(codon); 
//    }
    
}
