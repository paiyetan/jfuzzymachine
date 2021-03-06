/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.utilities.simulation;

import jfuzzymachine.tables.Table;
/**
 *
 * @author aiyetanpo
 */
public class Fuzzifier {
    
    public FuzzySet[][] getFuzzyMatrix(Table exprs) {
        //Table fuzzyTable;
        double[][] mat = exprs.getDoubleMatrix();
        FuzzySet[][] xfMat = new FuzzySet[exprs.getRowIds().length][exprs.getColumnIds().length];
        
        for(int i = 0; i < exprs.getRowIds().length; i++){
            for(int j = 0; j < exprs.getColumnIds().length; j++){
                xfMat[i][j] = this.fuzzify(mat[i][j]);
            }
        }
        return xfMat;
    }
    
    public FuzzySet[] getFuzzyArray(double exprs[]) {
        FuzzySet[] xfArr = new FuzzySet[exprs.length];       
        for(int i = 0; i < exprs.length; i++){           
            xfArr[i] = this.fuzzify(exprs[i]);            
        }
        return xfArr;
    }
    
    public FuzzySet fuzzify(double value){
        FuzzySet fz;
        
        double y1 = 0, y2 = 0, y3 = 0;
        value = Math.atan(value); //apply the arctan normalization...
        value = value / (Math.PI/2); // 
        
        
        y1 = (value < 0) ? -value : 0 ; 
        y2 = 1 - Math.abs(value);
        y3 = (value <= 0) ? 0: value ;
        
        fz = new FuzzySet(y1, y2, y3);
        return fz;
    }     
    
    
    public FuzzySet fuzzify(double value, Simulation.ExpressionType expressionType){
        
        FuzzySet fz;
        double y1 = 0, y2 = 0, y3 = 0;
        value = Math.atan(value); //apply the atan normalization...
        value = value / (Math.PI/2); // 
        
        switch(expressionType){
            
            case PHENOTYPE:
                y1 = (value < 0.5) ? (1 - (2*value)) : 0 ;
                y2 = 1 - Math.abs((2*value) - 1);
                y3 = (value < 0.5) ? 0 : ((2*value) - 1) ;
                break;
                
            default: // GENOTYPE
                y1 = (value < 0) ? -value : 0 ; 
                y2 = 1 - Math.abs(value);
                y3 = (value <= 0) ? 0: value ;
                break;
        
        }
        
        fz = new FuzzySet(y1, y2, y3);        
        return fz;
    }  
    
    public double deFuzzify(FuzzySet fz){
        double dfz;
        dfz = (fz.getY3() - fz.getY1())/(fz.getY1() + fz.getY2() + fz.getY3());      
        
        dfz = dfz * (Math.PI/2);
        dfz = Math.tan(dfz);  
        
        return dfz;
    } 
    
    
    public double deFuzzify(FuzzySet fz, 
                               boolean tanTransform,
                                   boolean logitTransform, 
                                       double k){
        double dfz;
        dfz = (fz.getY3() - fz.getY1())/(fz.getY1() + fz.getY2() + fz.getY3()); 
        
        if(tanTransform){
            dfz = dfz * (Math.PI/2);
            dfz = Math.tan(dfz); 
        }
        
        if(logitTransform){
            dfz = 1/( 1 + Math.pow(Math.E, ( -k * dfz)) );
        } 
        
        return dfz;
    }
    
    
    
    public FuzzySet applyRule(FuzzySet fzSet, String rule) {
        FuzzySet fz_i;
        double[] fzValues = fzSet.getSetAsArray();
        String[] ruleValues = rule.split(", ");
        double[] fzValues_i = new double[fzValues.length];
        for(int i = 0; i < fzValues.length; i++){
            fzValues_i[i] = fzValues[(Integer.parseInt(ruleValues[i])) - 1];
        }
        fz_i = new FuzzySet(fzValues_i);
        return fz_i;
    }

 
}
