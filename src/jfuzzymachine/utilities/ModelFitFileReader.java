/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.utilities;

import jfuzzymachine.utilities.graph.Model;
import jfuzzymachine.utilities.graph.Vertex;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author aiyetanpo
 */
public class ModelFitFileReader {
    
    public static HashMap<Vertex, LinkedList<Model>> read(String fitFilePath, double fitCutOff) throws FileNotFoundException, IOException{
        HashMap<Vertex, LinkedList<Model>> outputsToModelsMap = new HashMap();
        BufferedReader reader = new BufferedReader(new FileReader(fitFilePath));
        String line;
        int lineIndex = 0;
        while((line = reader.readLine())!=null){
            if(lineIndex != 0){
                
                Vertex outputNode;
                LinkedList<Vertex> inputNodes = new LinkedList();
                LinkedList<String> rulesList = new LinkedList();
                double fit;
              
                //Output    NumberOfFittedModels	InputNodes(BestFit)	Rules	Fit
                String[] lineArr = line.split("\t");
                
                fit = Double.parseDouble(lineArr[4]);
                if(fit >= fitCutOff){
                    outputNode = new Vertex(lineArr[0].trim());
                    String[] inputNodesIds = lineArr[2].replace("[", "").replace("]", "").split(", ");
                    for(String inputNodeId : inputNodesIds)
                        inputNodes.add(new Vertex(inputNodeId));
                    String[] rules = lineArr[3].replace("[[", "").replace("]]", "").split("], \\[");
                    rulesList.addAll(Arrays.asList(rules));


                    Model fittedModel = new Model(outputNode, inputNodes, rulesList, fit);
                    if(outputsToModelsMap.containsKey(outputNode)){
                        LinkedList<Model> mappedModels = outputsToModelsMap.remove(outputNode);
                        mappedModels.add(fittedModel);
                        outputsToModelsMap.put(outputNode, mappedModels);
                    }else{
                        LinkedList<Model> mappedModels = new LinkedList();
                        mappedModels.add(fittedModel);
                        outputsToModelsMap.put(outputNode, mappedModels);
                    }
                }                
            }
            lineIndex++;
        }
        
        reader.close();
        //System.out.println("ModelFitFileReader: #outputsToModelsMap: " + outputsToModelsMap.keySet().size());
        return outputsToModelsMap;
    }
    
}
