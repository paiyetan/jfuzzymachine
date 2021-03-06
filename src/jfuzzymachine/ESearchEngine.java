/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import jfuzzymachine.JFuzzyMachine.ExpressionType;
import jfuzzymachine.tables.Table;


/**
 *
 * @author aiyetanpo
 */
public class ESearchEngine { //ExhaustiveSearch....

    /**
     * 
     *
     * @param printer
     * @param config
     */
    public void printESearchResultFileHeader(PrintWriter printer, HashMap<String, String> config){
        printer.println("Output\tNumberOfInput(s)\tInput(s)\tRule(s)\tError(E)");
        if(config.get("outputInRealtime").equalsIgnoreCase("TRUE")){
            // print output file header...
            System.out.println("Output\tNumberOfInput(s)\tInput(s)\tRule(s)\tError(E)");
        }        
    }

    @SuppressWarnings("NonPublicExported")
    public void printESearchResult(ESearchResult result, PrintWriter printer, HashMap<String, String> config) {       
        printer.println(result.getOutputGene()  + "\t" + 
                        result.getNumOfInputs() + "\t" +
                        Arrays.toString(result.getInputGenes()) + "\t" +
                        Arrays.toString(result.getRules()) + "\t" +
                        result.getError()
                        );
        
        if(config.get("outputInRealtime").equalsIgnoreCase("TRUE")){
            // print output file header...
            System.out.println(result.getOutputGene()  + "\t" + 
                        result.getNumOfInputs() + "\t" +
                        Arrays.toString(result.getInputGenes()) + "\t" +
                        Arrays.toString(result.getRules()) + "\t" +
                        result.getError()
            );
        }   
        
    }
    
    public void searchWithFiveInputs(int[] inputsCombination, 
                                     double eCutOff, 
                                     double[] outputGeneExpValues, 
                                     double deviationSquaredSum, 
                                     Table exprs, 
                                     FuzzySet[][] fMat,
                                     String[] otherGenes,
                                     String outputGene,
                                     //ESearch esearch,
                                     PrintWriter printer,
                                     HashMap<String, String> config,
                                     Table phenoExprs, 
                                     FuzzySet[][] phenoFMat,
                                     boolean modelPhenotype
                                    ){
        
        String[] inputGenes = new String[inputsCombination.length];            
        String inputGene1 = otherGenes[inputsCombination[0]];
        String inputGene2 = otherGenes[inputsCombination[1]];
        String inputGene3 = otherGenes[inputsCombination[2]];
        String inputGene4 = otherGenes[inputsCombination[3]];
        String inputGene5 = otherGenes[inputsCombination[4]];
        inputGenes[0] = inputGene1;
        inputGenes[1] = inputGene2;
        inputGenes[2] = inputGene3;
        inputGenes[3] = inputGene4;
        inputGenes[4] = inputGene5;
        
        Fuzzifier fuzzifier = new Fuzzifier();

        if(modelPhenotype){
            boolean tanTransform = Boolean.parseBoolean(config.get("tanTransform"));
            boolean logitTransform = Boolean.parseBoolean(config.get("logitTransform"));
            double kValue = Double.parseDouble(config.get("kValue"));
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        for(int l = 1; l <= 3; l++){
                            for(int m = 1; m <= 3; m++){
                                for(int n = 1; n <= 3; n++){

                                    for(int o = 1; o <= 3; o++){
                                        for(int p = 1; p <= 3; p++){
                                            for(int q = 1; q <= 3; q++){

                                                for(int r = 1; r <= 3; r++){
                                                    for(int s = 1; s <= 3; s++){
                                                        for(int t = 1; t <= 3; t++){

                                                            for(int u = 1; u <= 3; u++){
                                                                for(int v = 1; v <= 3; v++){
                                                                    for(int w = 1; w <= 3; w++){

                                                                        double residualSquaredSum = 0;                                                                        
                                                                        for(int index = 0; index < outputGeneExpValues.length; index++){                                                                                                                                                        
                                                                            //get fuzzyValues of input genes, 
                                                                            FuzzySet fz1 = fMat[exprs.getRowIndex(inputGene1)][index];
                                                                            FuzzySet fz2 = fMat[exprs.getRowIndex(inputGene2)][index];
                                                                            FuzzySet fz3 = fMat[exprs.getRowIndex(inputGene3)][index];
                                                                            FuzzySet fz4 = fMat[exprs.getRowIndex(inputGene4)][index];
                                                                            FuzzySet fz5 = fMat[exprs.getRowIndex(inputGene5)][index];
                                                                            //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                                                                            double zx = fz1.get(i) + fz2.get(l) + fz3.get(o) + fz4.get(r) + fz5.get(u);
                                                                            double zy = fz1.get(j) + fz2.get(m) + fz3.get(p) + fz4.get(s) + fz5.get(v);
                                                                            double zz = fz1.get(k) + fz2.get(n) + fz3.get(q) + fz4.get(t) + fz5.get(w);

                                                                            //get defuzzified (xCaretValue) value of Z @ position index
                                                                            //double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.PHENOTYPE);
                                                                            double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), tanTransform, logitTransform, kValue);
                                                                            // compute residual and cummulative residual squared sum...
                                                                            residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);                                        
                                                                        }
                                                                        // compute error..
                                                                        double err = 1 - (residualSquaredSum/deviationSquaredSum);
                                                                        // filter
                                                                        if(err >= eCutOff){                                                                           
                                                                            String[] rules = new String[inputsCombination.length];
                                                                            rules[0] = new Rule(i,j,k).toString();
                                                                            rules[1] = new Rule(l,m,n).toString();
                                                                            rules[2] = new Rule(o,p,q).toString();
                                                                            rules[3] = new Rule(r,s,t).toString();
                                                                            rules[4] = new Rule(u,v,w).toString();
                                                                            ESearchResult sResult = new ESearchResult(outputGene, 
                                                                                                                        inputsCombination.length,
                                                                                                                            inputGenes,
                                                                                                                                rules,
                                                                                                                                    err
                                                                                                                                );
                                                                            printESearchResult(sResult, printer, config);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }   
        }else{
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        for(int l = 1; l <= 3; l++){
                            for(int m = 1; m <= 3; m++){
                                for(int n = 1; n <= 3; n++){

                                    for(int o = 1; o <= 3; o++){
                                        for(int p = 1; p <= 3; p++){
                                            for(int q = 1; q <= 3; q++){

                                                for(int r = 1; r <= 3; r++){
                                                    for(int s = 1; s <= 3; s++){
                                                        for(int t = 1; t <= 3; t++){

                                                            for(int u = 1; u <= 3; u++){
                                                                for(int v = 1; v <= 3; v++){
                                                                    for(int w = 1; w <= 3; w++){

                                                                        double residualSquaredSum = 0;                                                                        
                                                                        for(int index = 0; index < outputGeneExpValues.length; index++){                                                                                                                                                        
                                                                            //get fuzzyValues of input genes, 
                                                                            FuzzySet fz1 = fMat[exprs.getRowIndex(inputGene1)][index];
                                                                            FuzzySet fz2 = fMat[exprs.getRowIndex(inputGene2)][index];
                                                                            FuzzySet fz3 = fMat[exprs.getRowIndex(inputGene3)][index];
                                                                            FuzzySet fz4 = fMat[exprs.getRowIndex(inputGene4)][index];
                                                                            FuzzySet fz5 = fMat[exprs.getRowIndex(inputGene5)][index];
                                                                            //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                                                                            double zx = fz1.get(i) + fz2.get(l) + fz3.get(o) + fz4.get(r) + fz5.get(u);
                                                                            double zy = fz1.get(j) + fz2.get(m) + fz3.get(p) + fz4.get(s) + fz5.get(v);
                                                                            double zz = fz1.get(k) + fz2.get(n) + fz3.get(q) + fz4.get(t) + fz5.get(w);

                                                                            //get defuzzified (xCaretValue) value of Z @ position index
                                                                            double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.GENOTYPE);                                                                            

                                                                            // compute residual and cummulative residual squared sum...
                                                                            residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);                                        
                                                                        }
                                                                        // compute error..
                                                                        double err = 1 - (residualSquaredSum/deviationSquaredSum);
                                                                        // filter
                                                                        if(err >= eCutOff){                                                                           
                                                                            String[] rules = new String[inputsCombination.length];
                                                                            rules[0] = new Rule(i,j,k).toString();
                                                                            rules[1] = new Rule(l,m,n).toString();
                                                                            rules[2] = new Rule(o,p,q).toString();
                                                                            rules[3] = new Rule(r,s,t).toString();
                                                                            rules[4] = new Rule(u,v,w).toString();
                                                                            ESearchResult sResult = new ESearchResult(outputGene, 
                                                                                                                        inputsCombination.length,
                                                                                                                            inputGenes,
                                                                                                                                rules,
                                                                                                                                    err
                                                                                                                                );
                                                                            printESearchResult(sResult, printer, config);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } 
        }    
            
    }
    
    public void searchWithFourInputs(int[] inputsCombination, 
                                     double eCutOff, 
                                     double[] outputGeneExpValues, 
                                     double deviationSquaredSum, 
                                     Table exprs, 
                                     FuzzySet[][] fMat,
                                     String[] otherGenes,
                                     String outputGene,
                                     //ESearch esearch,
                                     PrintWriter printer,
                                     HashMap<String, String> config,
                                     Table phenoExprs, 
                                     FuzzySet[][] phenoFMat,
                                     boolean modelPhenotype
                                    ){
        String[] inputGenes = new String[inputsCombination.length];            
        String inputGene1 = otherGenes[inputsCombination[0]];
        String inputGene2 = otherGenes[inputsCombination[1]];
        String inputGene3 = otherGenes[inputsCombination[2]];
        String inputGene4 = otherGenes[inputsCombination[3]];
        inputGenes[0] = inputGene1;
        inputGenes[1] = inputGene2;
        inputGenes[2] = inputGene3;
        inputGenes[3] = inputGene4;
        
        Fuzzifier fuzzifier = new Fuzzifier();

        if(modelPhenotype){
            boolean tanTransform = Boolean.parseBoolean(config.get("tanTransform"));
            boolean logitTransform = Boolean.parseBoolean(config.get("logitTransform"));
            double kValue = Double.parseDouble(config.get("kValue"));
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        for(int l = 1; l <= 3; l++){
                            for(int m = 1; m <= 3; m++){
                                for(int n = 1; n <= 3; n++){

                                    for(int o = 1; o <= 3; o++){
                                        for(int p = 1; p <= 3; p++){
                                            for(int q = 1; q <= 3; q++){

                                                for(int r = 1; r <= 3; r++){
                                                    for(int s = 1; s <= 3; s++){
                                                        for(int t = 1; t <= 3; t++){

                                                            double residualSquaredSum = 0;                                                                        
                                                            for(int index = 0; index < outputGeneExpValues.length; index++){                                                                                                                                                        
                                                                //get fuzzyValues of input genes, 
                                                                FuzzySet fz1 = fMat[exprs.getRowIndex(inputGene1)][index];
                                                                FuzzySet fz2 = fMat[exprs.getRowIndex(inputGene2)][index];
                                                                FuzzySet fz3 = fMat[exprs.getRowIndex(inputGene3)][index];
                                                                FuzzySet fz4 = fMat[exprs.getRowIndex(inputGene4)][index];
                                                                //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                                                                double zx = fz1.get(i) + fz2.get(l) + fz3.get(o) + fz4.get(r);
                                                                double zy = fz1.get(j) + fz2.get(m) + fz3.get(p) + fz4.get(s);
                                                                double zz = fz1.get(k) + fz2.get(n) + fz3.get(q) + fz4.get(t);

                                                                //get defuzzified (xCaretValue) value of Z @ position index
                                                                //double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.PHENOTYPE);
                                                                double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), tanTransform, logitTransform, kValue);
                                                                            
                                                                // compute residual and cummulative residual squared sum...
                                                                residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);                                        
                                                            }
                                                            // compute error..
                                                            double err = 1 - (residualSquaredSum/deviationSquaredSum);
                                                            // filter
                                                            if(err >= eCutOff){                                                                           
                                                                String[] rules = new String[inputsCombination.length];
                                                                rules[0] = new Rule(i,j,k).toString();
                                                                rules[1] = new Rule(l,m,n).toString();
                                                                rules[2] = new Rule(o,p,q).toString();
                                                                rules[3] = new Rule(r,s,t).toString();
                                                                ESearchResult sResult = new ESearchResult(outputGene, 
                                                                                                            inputsCombination.length,
                                                                                                                inputGenes,
                                                                                                                    rules,
                                                                                                                        err
                                                                                                                    );
                                                                printESearchResult(sResult, printer, config);
                                                            }

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else{
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        for(int l = 1; l <= 3; l++){
                            for(int m = 1; m <= 3; m++){
                                for(int n = 1; n <= 3; n++){

                                    for(int o = 1; o <= 3; o++){
                                        for(int p = 1; p <= 3; p++){
                                            for(int q = 1; q <= 3; q++){

                                                for(int r = 1; r <= 3; r++){
                                                    for(int s = 1; s <= 3; s++){
                                                        for(int t = 1; t <= 3; t++){

                                                            double residualSquaredSum = 0;                                                                        
                                                            for(int index = 0; index < outputGeneExpValues.length; index++){                                                                                                                                                        
                                                                //get fuzzyValues of input genes, 
                                                                FuzzySet fz1 = fMat[exprs.getRowIndex(inputGene1)][index];
                                                                FuzzySet fz2 = fMat[exprs.getRowIndex(inputGene2)][index];
                                                                FuzzySet fz3 = fMat[exprs.getRowIndex(inputGene3)][index];
                                                                FuzzySet fz4 = fMat[exprs.getRowIndex(inputGene4)][index];
                                                                //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                                                                double zx = fz1.get(i) + fz2.get(l) + fz3.get(o) + fz4.get(r);
                                                                double zy = fz1.get(j) + fz2.get(m) + fz3.get(p) + fz4.get(s);
                                                                double zz = fz1.get(k) + fz2.get(n) + fz3.get(q) + fz4.get(t);

                                                                //get defuzzified (xCaretValue) value of Z @ position index
                                                                double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.GENOTYPE);
                                                                
                                                                // compute residual and cummulative residual squared sum...
                                                                residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);                                        
                                                            }
                                                            // compute error..
                                                            double err = 1 - (residualSquaredSum/deviationSquaredSum);
                                                            // filter
                                                            if(err >= eCutOff){                                                                           
                                                                String[] rules = new String[inputsCombination.length];
                                                                rules[0] = new Rule(i,j,k).toString();
                                                                rules[1] = new Rule(l,m,n).toString();
                                                                rules[2] = new Rule(o,p,q).toString();
                                                                rules[3] = new Rule(r,s,t).toString();
                                                                ESearchResult sResult = new ESearchResult(outputGene, 
                                                                                                            inputsCombination.length,
                                                                                                                inputGenes,
                                                                                                                    rules,
                                                                                                                        err
                                                                                                                    );
                                                                printESearchResult(sResult, printer, config);
                                                            }

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void searchWithThreeInputs(int[] inputsCombination, 
                                     double eCutOff, 
                                     double[] outputGeneExpValues, 
                                     double deviationSquaredSum, 
                                     Table exprs, 
                                     FuzzySet[][] fMat,
                                     String[] otherGenes,
                                     String outputGene,
                                     //ESearch esearch,
                                     PrintWriter printer,
                                     HashMap<String, String> config,
                                     Table phenoExprs, 
                                     FuzzySet[][] phenoFMat,
                                     boolean modelPhenotype
                                    ){
        String[] inputGenes = new String[inputsCombination.length];            
        String inputGene1 = otherGenes[inputsCombination[0]];
        String inputGene2 = otherGenes[inputsCombination[1]];
        String inputGene3 = otherGenes[inputsCombination[2]];
        inputGenes[0] = inputGene1;
        inputGenes[1] = inputGene2;
        inputGenes[2] = inputGene3;
        
        Fuzzifier fuzzifier = new Fuzzifier();

        if(modelPhenotype){
            boolean tanTransform = Boolean.parseBoolean(config.get("tanTransform"));
            boolean logitTransform = Boolean.parseBoolean(config.get("logitTransform"));
            double kValue = Double.parseDouble(config.get("kValue"));
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        for(int l = 1; l <= 3; l++){
                            for(int m = 1; m <= 3; m++){
                                for(int n = 1; n <= 3; n++){

                                    for(int o = 1; o <= 3; o++){
                                        for(int p = 1; p <= 3; p++){
                                            for(int q = 1; q <= 3; q++){

                                                double residualSquaredSum = 0;                                                                        
                                                for(int index = 0; index < outputGeneExpValues.length; index++){                                                                                                                                                        
                                                    //get fuzzyValues of input genes, 
                                                    FuzzySet fz1 = fMat[exprs.getRowIndex(inputGene1)][index];
                                                    FuzzySet fz2 = fMat[exprs.getRowIndex(inputGene2)][index];
                                                    FuzzySet fz3 = fMat[exprs.getRowIndex(inputGene3)][index];
                                                    //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                                                    double zx = fz1.get(i) + fz2.get(l) + fz3.get(o);
                                                    double zy = fz1.get(j) + fz2.get(m) + fz3.get(p);
                                                    double zz = fz1.get(k) + fz2.get(n) + fz3.get(q);

                                                    //get defuzzified (xCaretValue) value of Z @ position index
                                                    //double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.PHENOTYPE);
                                                    double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), tanTransform, logitTransform, kValue);
                                                                            
                                                    // compute residual and cummulative residual squared sum...
                                                    residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);                                        
                                                }
                                                // compute error..
                                                double err = 1 - (residualSquaredSum/deviationSquaredSum);
                                                // filter
                                                if(err >= eCutOff){                                                                           
                                                    String[] rules = new String[inputsCombination.length];
                                                    rules[0] = new Rule(i,j,k).toString();
                                                    rules[1] = new Rule(l,m,n).toString();
                                                    rules[2] = new Rule(o,p,q).toString();
                                                    ESearchResult sResult = new ESearchResult(outputGene, 
                                                                                                inputsCombination.length,
                                                                                                    inputGenes,
                                                                                                        rules,
                                                                                                            err
                                                                                                        );
                                                    printESearchResult(sResult, printer, config);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else{
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        for(int l = 1; l <= 3; l++){
                            for(int m = 1; m <= 3; m++){
                                for(int n = 1; n <= 3; n++){

                                    for(int o = 1; o <= 3; o++){
                                        for(int p = 1; p <= 3; p++){
                                            for(int q = 1; q <= 3; q++){

                                                double residualSquaredSum = 0;                                                                        
                                                for(int index = 0; index < outputGeneExpValues.length; index++){                                                                                                                                                        
                                                    //get fuzzyValues of input genes, 
                                                    FuzzySet fz1 = fMat[exprs.getRowIndex(inputGene1)][index];
                                                    FuzzySet fz2 = fMat[exprs.getRowIndex(inputGene2)][index];
                                                    FuzzySet fz3 = fMat[exprs.getRowIndex(inputGene3)][index];
                                                    //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                                                    double zx = fz1.get(i) + fz2.get(l) + fz3.get(o);
                                                    double zy = fz1.get(j) + fz2.get(m) + fz3.get(p);
                                                    double zz = fz1.get(k) + fz2.get(n) + fz3.get(q);

                                                    //get defuzzified (xCaretValue) value of Z @ position index
                                                    double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.GENOTYPE);
                                                    
                                                    // compute residual and cummulative residual squared sum...
                                                    residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);                                        
                                                }
                                                // compute error..
                                                double err = 1 - (residualSquaredSum/deviationSquaredSum);
                                                // filter
                                                if(err >= eCutOff){                                                                           
                                                    String[] rules = new String[inputsCombination.length];
                                                    rules[0] = new Rule(i,j,k).toString();
                                                    rules[1] = new Rule(l,m,n).toString();
                                                    rules[2] = new Rule(o,p,q).toString();
                                                    ESearchResult sResult = new ESearchResult(outputGene, 
                                                                                                inputsCombination.length,
                                                                                                    inputGenes,
                                                                                                        rules,
                                                                                                            err
                                                                                                        );
                                                    printESearchResult(sResult, printer, config);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void searchWithTwoInputs(int[] inputsCombination, 
                                     double eCutOff, 
                                     double[] outputGeneExpValues, 
                                     double deviationSquaredSum, 
                                     Table exprs, 
                                     FuzzySet[][] fMat,
                                     String[] otherGenes,
                                     String outputGene,
                                     //ESearch esearch,
                                     PrintWriter printer,
                                     HashMap<String, String> config,
                                     Table phenoExprs, 
                                     FuzzySet[][] phenoFMat,
                                     boolean modelPhenotype
                                    ){
        String[] inputGenes = new String[inputsCombination.length];            
        String inputGene1 = otherGenes[inputsCombination[0]];
        String inputGene2 = otherGenes[inputsCombination[1]];
        inputGenes[0] = inputGene1;
        inputGenes[1] = inputGene2;
        
        Fuzzifier fuzzifier = new Fuzzifier();

        if(modelPhenotype){
            boolean tanTransform = Boolean.parseBoolean(config.get("tanTransform"));
            boolean logitTransform = Boolean.parseBoolean(config.get("logitTransform"));
            double kValue = Double.parseDouble(config.get("kValue"));
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        for(int l = 1; l <= 3; l++){
                            for(int m = 1; m <= 3; m++){
                                for(int n = 1; n <= 3; n++){

                                    double residualSquaredSum = 0;                                                                        
                                    for(int index = 0; index < outputGeneExpValues.length; index++){                                                                                                                                                        
                                        //get fuzzyValues of input genes, 
                                        FuzzySet fz1 = fMat[exprs.getRowIndex(inputGene1)][index];
                                        FuzzySet fz2 = fMat[exprs.getRowIndex(inputGene2)][index];
                                        //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                                        double zx = fz1.get(i) + fz2.get(l);
                                        double zy = fz1.get(j) + fz2.get(m);
                                        double zz = fz1.get(k) + fz2.get(n);

                                        //get defuzzified (xCaretValue) value of Z @ position index
                                        //double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.PHENOTYPE);
                                        double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), tanTransform, logitTransform, kValue);
                                                                            
                                        // compute residual and cummulative residual squared sum...
                                        residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);                                        
                                    }
                                    // compute error..
                                    double err = 1 - (residualSquaredSum/deviationSquaredSum);
                                    // filter
                                    if(err >= eCutOff){                                                                           
                                        String[] rules = new String[inputsCombination.length];
                                        rules[0] = new Rule(i,j,k).toString();
                                        rules[1] = new Rule(l,m,n).toString();
                                        ESearchResult sResult = new ESearchResult(outputGene, 
                                                                                    inputsCombination.length,
                                                                                        inputGenes,
                                                                                            rules,
                                                                                                err
                                                                                            );
                                        printESearchResult(sResult, printer, config);
                                    }                                
                                }
                            }
                        }
                    }
                }
            }
        }else{
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        for(int l = 1; l <= 3; l++){
                            for(int m = 1; m <= 3; m++){
                                for(int n = 1; n <= 3; n++){

                                    double residualSquaredSum = 0;                                                                        
                                    for(int index = 0; index < outputGeneExpValues.length; index++){                                                                                                                                                        
                                        //get fuzzyValues of input genes, 
                                        FuzzySet fz1 = fMat[exprs.getRowIndex(inputGene1)][index];
                                        FuzzySet fz2 = fMat[exprs.getRowIndex(inputGene2)][index];
                                        //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                                        double zx = fz1.get(i) + fz2.get(l);
                                        double zy = fz1.get(j) + fz2.get(m);
                                        double zz = fz1.get(k) + fz2.get(n);

                                        //get defuzzified (xCaretValue) value of Z @ position index
                                        double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.GENOTYPE);
                                        
                                        // compute residual and cummulative residual squared sum...
                                        residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);                                        
                                    }
                                    // compute error..
                                    double err = 1 - (residualSquaredSum/deviationSquaredSum);
                                    // filter
                                    if(err >= eCutOff){                                                                           
                                        String[] rules = new String[inputsCombination.length];
                                        rules[0] = new Rule(i,j,k).toString();
                                        rules[1] = new Rule(l,m,n).toString();
                                        ESearchResult sResult = new ESearchResult(outputGene, 
                                                                                    inputsCombination.length,
                                                                                        inputGenes,
                                                                                            rules,
                                                                                                err
                                                                                            );
                                        printESearchResult(sResult, printer, config);
                                    }                                
                                }
                            }
                        }
                    }
                }
            }            
        }
    }
    
    public void searchWithOneInput(int[] inputsCombination, 
                                     double eCutOff, 
                                     double[] outputGeneExpValues, 
                                     double deviationSquaredSum, 
                                     Table exprs, 
                                     FuzzySet[][] fMat,
                                     String[] otherGenes,
                                     String outputGene,
                                     PrintWriter printer,
                                     HashMap<String, String> config,
                                     Table phenoExprs, 
                                     FuzzySet[][] phenoFMat,
                                     boolean modelPhenotype
                                     ){
        String[] inputGenes = new String[inputsCombination.length];            
        String inputGene1 = otherGenes[inputsCombination[0]];
        inputGenes[0] = inputGene1;
        
        Fuzzifier fuzzifier = new Fuzzifier();

        if(modelPhenotype){ 
            boolean tanTransform = Boolean.parseBoolean(config.get("tanTransform"));
            boolean logitTransform = Boolean.parseBoolean(config.get("logitTransform"));
            double kValue = Double.parseDouble(config.get("kValue"));
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        double residualSquaredSum = 0;                                                                        
                        for(int index = 0; index < outputGeneExpValues.length; index++){                                                                                                                                                        
                            //get fuzzyValues of input genes, 
                            FuzzySet fz1 = fMat[exprs.getRowIndex(inputGene1)][index];
                            //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                            double zx = fz1.get(i);
                            double zy = fz1.get(j);
                            double zz = fz1.get(k);

                            //get defuzzified (xCaretValue) value of Z @ position index
                            //double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.PHENOTYPE);
                            double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), tanTransform, logitTransform, kValue);
                                                                            
                            // compute residual and cummulative residual squared sum...
                            residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);                                        
                        }
                        // compute error..
                        double err = 1 - (residualSquaredSum/deviationSquaredSum);
                        // filter
                        if(err >= eCutOff){                                                                           
                            String[] rules = new String[inputsCombination.length];
                            rules[0] = new Rule(i,j,k).toString();
                            ESearchResult sResult = new ESearchResult(outputGene, 
                                                                        inputsCombination.length,
                                                                            inputGenes,
                                                                                rules,
                                                                                    err
                                                                                );
                            printESearchResult(sResult, printer, config);
                        }                                       
                    }
                }
            }
        }else{
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        double residualSquaredSum = 0;                                                                        
                        for(int index = 0; index < outputGeneExpValues.length; index++){                                                                                                                                                        
                            //get fuzzyValues of input genes, 
                            FuzzySet fz1 = fMat[exprs.getRowIndex(inputGene1)][index];
                            //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                            double zx = fz1.get(i);
                            double zy = fz1.get(j);
                            double zz = fz1.get(k);

                            //get defuzzified (xCaretValue) value of Z @ position index
                            double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.GENOTYPE);
                            
                            // compute residual and cummulative residual squared sum...
                            residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);                                        
                        }
                        // compute error..
                        double err = 1 - (residualSquaredSum/deviationSquaredSum);
                        // filter
                        if(err >= eCutOff){                                                                           
                            String[] rules = new String[inputsCombination.length];
                            rules[0] = new Rule(i,j,k).toString();
                            ESearchResult sResult = new ESearchResult(outputGene, 
                                                                        inputsCombination.length,
                                                                            inputGenes,
                                                                                rules,
                                                                                    err
                                                                                );
                            printESearchResult(sResult, printer, config);
                        }                                       
                    }
                }
            }
        }
    }
    

}
