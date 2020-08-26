/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.utilities.simulation;

import jfuzzymachine.exceptions.TableBindingException;
import java.io.File;
import jfuzzymachine.utilities.graph.Model;
import jfuzzymachine.utilities.graph.Vertex;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jfuzzymachine.utilities.ConfigFileReader;
import jfuzzymachine.utilities.ModelFitFileReader;

/**
 *
 * @author aiyetanpo
 */
public class Simulator {

        
    private enum INIT{FIRST, RANDOM, AVERAGE, ALL}; //initial values
    private enum KOTYPE{ SINGLE, DOUBLE}; // type of knockout to simulate...
    
    @SuppressWarnings("FieldMayBeFinal")
    private LinkedList<Simulation> simulations;
    private HashMap<String, LinkedList<Simulation>> KOToSimulationsMap;
    private HashMap<Vertex, LinkedList<Model>> outputsToModelsMap;
    private double alpha;
    private int maxIteration = 50; //default...
    private double eCutOff;
    private INIT initType;
    private Table exprs;
    
    private boolean tanTransform;
    private boolean logitTransform; 
    private double k;
    private String phenotypeId;
    
    private boolean simulateKnockout;
    private KOTYPE knockoutType; // SINGLE/DOUBLE...
    
    
    public Simulator(String fitFile, // use _GraphOutput.fit - best fit...
                        String exprsMatFile, 
                            double fitCutOff, 
                                double alpha, 
                                    boolean includesPheno, 
                                        String phenotypeId, 
                                            String phenoExprsMatFile, 
                                                int maxIterations, double eCutOff,
                                                    INIT initType,
                                                        boolean tanTransform,
                                                            boolean logitTransform, 
                                                                double k,
                                                                    boolean simulateKnockout,
                                                                        KOTYPE knockoutType) throws IOException, TableBindingException 
    {
        this.maxIteration = maxIterations;
        this.eCutOff = eCutOff;
        this.exprs = new Table(exprsMatFile, Table.TableType.DOUBLE); 
        this.initType = initType;        
        this.outputsToModelsMap = ModelFitFileReader.read(fitFile, fitCutOff);
        this.logitTransform = logitTransform;
        this.tanTransform = tanTransform;
        this.k = k;
        this.phenotypeId = phenotypeId;
        this.simulateKnockout = simulateKnockout;
        this.knockoutType = knockoutType;
        
        
        System.out.println("In Simulator: #outputsToModelsMap: " + outputsToModelsMap.keySet().size());
        if(includesPheno){            
            Table phenoExprs = new Table(phenoExprsMatFile, Table.TableType.DOUBLE); //merge pheno and exprs Table into a single table
            exprs = exprs.bind(phenoExprs, Table.BindType.ROW);
        }
        
        this.alpha = alpha; 
        
        
        if(simulateKnockout){
            
            KOToSimulationsMap = new HashMap();
            //String[] genesToKnockout = exprs.getRowIds();
            String[] genesToKnockout = getGenesToKnockout(); //should be from genes that qualified to be in the network model
                                                                // -- which should have been captured in the outputsToModelsMap
            
            switch (knockoutType){
                
                case DOUBLE:
                    LinkedList<String[]> geneKnockoutsPairs = new LinkedList();//
                    geneKnockoutsPairs = new LinkedList();//get pairs from list....
                    for(int i = 0; i < genesToKnockout.length; i++){
                        String geneA = genesToKnockout[i];
                        if(!geneA.equalsIgnoreCase(phenotypeId)){
                            for(int j = i+1; j < genesToKnockout.length; j++){
                                String geneB = genesToKnockout[j];
                                if(!geneB.equalsIgnoreCase(phenotypeId)){
                                    String[] knockouts = new String[2];
                                    knockouts[0] = geneA;
                                    knockouts[1] = geneB;
                                    geneKnockoutsPairs.add(knockouts);
                                }
                            }
                        }
                    }                    
                    System.out.println("Number of Genes Knockouts Pairs: " + geneKnockoutsPairs.size()); 
                    
                    switch(initType){
                        
                        case ALL: // using all samples' expression values as initial values for the simulation....
                            
                            for(String[] geneKnockoutsPair: geneKnockoutsPairs){                                
                                String mappedSimulationsKey = geneKnockoutsPair[0] + "_" + geneKnockoutsPair[1];
                                LinkedList<Simulation> mappedsimulations = new LinkedList();
                                for(int col = 0; col < exprs.getNumberOfColumns(); col++){
                                    double[] initialValues = exprs.getColumn(col, Table.TableType.DOUBLE);                                    
                                    Simulation sims = new Simulation(outputsToModelsMap,
                                                                        initialValues,
                                                                        exprs,
                                                                        alpha,
                                                                        maxIterations,
                                                                        eCutOff,
                                                                        includesPheno,
                                                                        phenotypeId,
                                                                        tanTransform,
                                                                        logitTransform, 
                                                                        k);
                                    sims.setSimulateKnockout(simulateKnockout); // set the actual value....
                                    sims.setKnockOuts(geneKnockoutsPair);
                                    mappedsimulations.add(sims);                                                                        
                                }
                                KOToSimulationsMap.put(mappedSimulationsKey, mappedsimulations);
                            }
                            break;
                        
                        default: //all other types of initial values -- RANDOM, FIRST, etc...
                            
                            for(String[] geneKnockoutsPair: geneKnockoutsPairs){
                                String mappedSimulationsKey = geneKnockoutsPair[0] + "_" + geneKnockoutsPair[1];
                                LinkedList<Simulation> mappedsimulations = new LinkedList();
                                double[] initialValues = getInitialValues(exprs, initType); //defaults to random initial values...
                                Simulation sims = new Simulation(outputsToModelsMap,
                                                                    initialValues,
                                                                    exprs,
                                                                    alpha,
                                                                    maxIterations,
                                                                    eCutOff,
                                                                    includesPheno,
                                                                    phenotypeId,
                                                                    tanTransform,
                                                                    logitTransform, 
                                                                    k);
                                sims.setSimulateKnockout(simulateKnockout); // set the actual value....
                                sims.setKnockOuts(geneKnockoutsPair);
                                mappedsimulations.add(sims);                                
                                KOToSimulationsMap.put(mappedSimulationsKey, mappedsimulations);
                            }
                            break;
                                                
                    }   
                    break;
                    
                default: //SINGLE                    
                    
                    System.out.println("Number of Gene Knockouts: " + genesToKnockout.length);
                    
                    switch(initType){
                        
                        case ALL:
                            
                            for(String geneToKnockout : genesToKnockout){
                                if(!geneToKnockout.equalsIgnoreCase(phenotypeId)){
                                    LinkedList<Simulation> mappedsimulations = new LinkedList();
                                    for(int col = 0; col < exprs.getNumberOfColumns(); col++){
                                        double[] initialValues = exprs.getColumn(col, Table.TableType.DOUBLE); //                                        
                                        Simulation sims = new Simulation(outputsToModelsMap,
                                                                            initialValues,
                                                                            exprs,
                                                                            alpha,
                                                                            maxIterations,
                                                                            eCutOff,
                                                                            includesPheno,
                                                                            phenotypeId,
                                                                            tanTransform,
                                                                            logitTransform, 
                                                                            k);
                                        sims.setSimulateKnockout(simulateKnockout);// set the actual value....
                                        sims.setGeneToKnockOut(geneToKnockout);
                                        mappedsimulations.add(sims);                                        
                                    }
                                    KOToSimulationsMap.put(geneToKnockout, mappedsimulations);
                                }
                            }
                            break;
                            
                        default:
                            
                            for(String geneToKnockout : genesToKnockout){
                                if(!geneToKnockout.equalsIgnoreCase(phenotypeId)){
                                    double[] initialValues = getInitialValues(exprs, INIT.RANDOM); //defaults to random initial values...
                                    LinkedList<Simulation> mappedsimulations = new LinkedList();
                                    Simulation sims = new Simulation(outputsToModelsMap,
                                                                        initialValues,
                                                                        exprs,
                                                                        alpha,
                                                                        maxIterations,
                                                                        eCutOff,
                                                                        includesPheno,
                                                                        phenotypeId,
                                                                        tanTransform,
                                                                        logitTransform, 
                                                                        k);
                                    sims.setSimulateKnockout(simulateKnockout);// set the actual value....
                                    sims.setGeneToKnockOut(geneToKnockout);
                                    mappedsimulations.add(sims);
                                    KOToSimulationsMap.put(geneToKnockout, mappedsimulations);
                                }
                            }
                            break;
                    }        
                    break;
            }
            
        }else{        
        
            this.simulations = new LinkedList();
            
            switch(initType){
                
                case ALL:
                    for(int col = 0; col < exprs.getNumberOfColumns(); col++){
                        double[] initialValues = exprs.getColumn(col, Table.TableType.DOUBLE); //this assumes a single sample/perturbation provides the initial outputs values              
                        this.simulations.add( new Simulation(outputsToModelsMap,
                                                                initialValues,
                                                                exprs,
                                                                alpha,
                                                                maxIterations,
                                                                eCutOff,
                                                                includesPheno,
                                                                phenotypeId,
                                                                tanTransform,
                                                                logitTransform, 
                                                                k));

                    }                
                    break;

                default: //all others...(RANDOM, FIRST, AVERAGE)
                    //String[] vertexIds = exprs.getRowIds();
                    double[] initialValues = getInitialValues(exprs, initType); //this assumes a single sample/perturbation provides the initial outputs values              
                    this.simulations.add( new Simulation(outputsToModelsMap,
                                                                initialValues,
                                                                exprs,
                                                                alpha,
                                                                maxIterations,
                                                                eCutOff,
                                                                includesPheno,
                                                                phenotypeId,
                                                                tanTransform,
                                                                logitTransform, 
                                                                k));

            }

        }
            
    } 
    
    private String[] getGenesToKnockout() {
        //throw new UnsupportedOperationException("Not supported yet."); 
        //To change body of generated methods, choose Tools | Templates.
        String[] feats = new String[this.outputsToModelsMap.keySet().size()];
        LinkedList<String> nodesInNetwork = new LinkedList();
        Set<Vertex> outputVertices = this.outputsToModelsMap.keySet();
        int index = 0;
        for(Vertex outputVertex : outputVertices){
            Collections.sort(outputsToModelsMap.get(outputVertex));
        }
        for(Vertex outputVertex : outputVertices){
           //feats[index] = outputVertex.getId();
           //index++;           
            String outputNode = outputVertex.getId();
            if(!nodesInNetwork.contains(outputNode))
               nodesInNetwork.add(outputNode);
            Model bestFitModel = null;
            try{
                //LinkedList<Model> inputModels = outputsToModelsMap.get(outputNode);
                //Collections.sort(inputModels); // to get the best model used in the network
                bestFitModel = outputsToModelsMap.get(outputVertex).getLast();
            }catch(NullPointerException e){
               System.out.println("OutputNode : " + outputNode);
               e.printStackTrace();
               System.exit(1);
            }
            LinkedList<Vertex> inputVertices = bestFitModel.getInputNodes();
           
            for(Vertex inputVertex : inputVertices){
                String inputNode = inputVertex.getId();
                if(!nodesInNetwork.contains(inputNode))
                    nodesInNetwork.add(inputNode);
            }           
        }
        
        feats = nodesInNetwork.toArray(new String[nodesInNetwork.size()]);;        
        return(feats);
    }

    
    private double[] getInitialValues(Table exprs, INIT init) {
        double[] initValues = null;
        switch(init){
            case FIRST:
                initValues = exprs.getColumn(0, Table.TableType.DOUBLE);
                break;
            default: //init.RANDOM.....
                int randomIndex = (int) (Math.random() * exprs.getColumnIds().length);
                initValues = exprs.getColumn(randomIndex, Table.TableType.DOUBLE);
        }
        return initValues;
    }
    
    
    
    public void printOutputNodes(String outFile) throws FileNotFoundException{
        PrintWriter printer = new PrintWriter(outFile + ".out");
        //Set<Vertex> os = outputToInputNodes.keySet();
        Set<Vertex> os = outputsToModelsMap.keySet();
        for(Vertex o : os){
            printer.println(o.getId()); 
            System.out.println(o.getId());
        }
        printer.close();
    }
    
    
    public void runSimulations(){
        // implement a parallel running of simulations here...
        if(simulateKnockout){
            Set<String> KOGenes = KOToSimulationsMap.keySet();
            System.out.println("[RUNSIMS] Number of knockedout keys: " + KOGenes.size());            
            
            for(String KO : KOGenes){
                KOToSimulationsMap.get(KO).parallelStream().forEach((simulation) ->{
                    simulation.run();
                });
            }
            
        }else{
            
            simulations.parallelStream().forEach((simulation) -> {
                simulation.run();
            });
            
        }
    } 
    
    
    public void printSimulations(String simulationsDirPath, String outFileBasename) throws FileNotFoundException{
        printDeltas(simulationsDirPath, outFileBasename); //deltas
        printSimulatedValues(simulationsDirPath, outFileBasename); //simulated values...
    }
    
    public void printDeltas(String simulationsDirPath, final String outFile) throws FileNotFoundException{
        
        if(this.simulateKnockout){
            
            Set<String> KOGenes = KOToSimulationsMap.keySet();
            /*
            for(String KOGene : KOGenes){
                //create a directory to hold knockedout gene's simulation....
                String kOGeneSimulationsDirPath = simulationsDirPath + File.separator + KOGene;
                if(!new File(kOGeneSimulationsDirPath).exists()){
                    new File(kOGeneSimulationsDirPath).mkdir();
                }
                String KOGeneOutFile = kOGeneSimulationsDirPath + File.separator + outFile;
                LinkedList<Simulation> mappedsimulations = KOToSimulationsMap.get(KOGene);
                for(int i = 0; i < mappedsimulations.size(); i++){
                    PrintWriter printer = new PrintWriter(KOGeneOutFile + "." + i + ".dta");
                    Simulation sim = mappedsimulations.get(i);
                    LinkedList<double[]> deltaValuesList = sim.getDeltaValuesList();
                    // print table_header...
                    for(int itr = 0; itr < deltaValuesList.size(); itr++){
                        printer.print("\t" + "itr_" + itr);
                    }
                    printer.print("\n");
                    // print table_body...
                    for(int j = 0; j < exprs.getRowIds().length; j++){
                        printer.print(exprs.getRowIds()[j]); //print rowId
                        for(int k = 0; k < deltaValuesList.size(); k++){

                            printer.print("\t" + deltaValuesList.get(k)[j]);

                        }
                        printer.print("\n");
                    }// end table
                    printer.close();
                }                
            }
            */
            
            KOGenes.parallelStream().forEach((KOGene) ->{
                //create a directory to hold knockedout gene's simulation....
                String kOGeneSimulationsDirPath = simulationsDirPath + File.separator + KOGene;
                if(!new File(kOGeneSimulationsDirPath).exists()){
                    new File(kOGeneSimulationsDirPath).mkdir();
                }
                String KOGeneOutFile = kOGeneSimulationsDirPath + File.separator + outFile;
                LinkedList<Simulation> mappedsimulations = KOToSimulationsMap.get(KOGene);
                for(int i = 0; i < mappedsimulations.size(); i++){
                    PrintWriter printer = null;
                    try {
                        printer = new PrintWriter(KOGeneOutFile + "." + i + ".dta");
                        Simulation sim = mappedsimulations.get(i);
                        LinkedList<double[]> deltaValuesList = sim.getDeltaValuesList();
                        // print table_header...
                        for(int itr = 0; itr < deltaValuesList.size(); itr++){
                            printer.print("\t" + "itr_" + itr);
                        }   printer.print("\n");
                        // print table_body...
                        for(int j = 0; j < exprs.getRowIds().length; j++){
                            printer.print(exprs.getRowIds()[j]); //print rowId
                            for(int k = 0; k < deltaValuesList.size(); k++){
                                
                                printer.print("\t" + deltaValuesList.get(k)[j]);
                                
                            }
                            printer.print("\n");
                        }// end table
                        printer.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        printer.close();
                    }
                } 
            });
            
        }else{
            String outputFile = simulationsDirPath + File.separator + outFile;
            for(int i = 0; i < simulations.size(); i++){
                PrintWriter printer = new PrintWriter(outputFile + "." + i + ".dta");
                Simulation sim = simulations.get(i);
                LinkedList<double[]> deltaValuesList = sim.getDeltaValuesList();
                // print table_header...
                for(int itr = 0; itr < deltaValuesList.size(); itr++){
                    printer.print("\t" + "itr_" + itr);
                }
                printer.print("\n");
                // print table_body...
                for(int j = 0; j < exprs.getRowIds().length; j++){
                    printer.print(exprs.getRowIds()[j]); //print rowId
                    for(int k = 0; k < deltaValuesList.size(); k++){

                        printer.print("\t" + deltaValuesList.get(k)[j]);

                    }
                    printer.print("\n");
                }// end table
                printer.close();
            }
        }
    }
    
    public void printSimulatedValues(String simulationsDirPath, String outFile) throws FileNotFoundException{
        
        if(this.simulateKnockout){
            Set<String> KOGenes = KOToSimulationsMap.keySet();
            for(String KOGene : KOGenes){
                //create a directory to hold knockedout gene's simulation....
                String kOGeneSimulationsDirPath = simulationsDirPath + File.separator + KOGene;
                if(!new File(kOGeneSimulationsDirPath).exists()){
                    new File(kOGeneSimulationsDirPath).mkdir();
                }
                String KOGeneOutFile  = kOGeneSimulationsDirPath + File.separator + outFile;
                LinkedList<Simulation> mappedsimulations = KOToSimulationsMap.get(KOGene);
                
                for(int i = 0; i < mappedsimulations.size(); i++){
                    PrintWriter printer = new PrintWriter(KOGeneOutFile + "." + i + ".sim");
                    Simulation sim = mappedsimulations.get(i);
                    LinkedList<double[]> simValues = sim.getSimulatedValues();
                    // print table_header...
                    for(int itr = 0; itr < simValues.size(); itr++){
                        printer.print("\t" + "itr_" + itr);
                    }
                    printer.print("\n");
                    // print table_body...
                    for(int j = 0; j < exprs.getRowIds().length; j++){
                        printer.print(exprs.getRowIds()[j]); //print rowId
                        for(int k = 0; k < simValues.size(); k++){

                            printer.print("\t" + simValues.get(k)[j]);

                        }
                        printer.print("\n");
                    }// end table
                    printer.close();
                }
            }
                
        }else{
            
            outFile = simulationsDirPath + File.separator + outFile;
            for(int i = 0; i < simulations.size(); i++){
                PrintWriter printer = new PrintWriter(outFile + "." + i + ".sim");
                Simulation sim = simulations.get(i);
                LinkedList<double[]> simValues = sim.getSimulatedValues();
                // print table_header...
                for(int itr = 0; itr < simValues.size(); itr++){
                    printer.print("\t" + "itr_" + itr);
                }
                printer.print("\n");
                // print table_body...
                for(int j = 0; j < exprs.getRowIds().length; j++){
                    printer.print(exprs.getRowIds()[j]); //print rowId
                    for(int k = 0; k < simValues.size(); k++){

                        printer.print("\t" + simValues.get(k)[j]);

                    }
                    printer.print("\n");
                }// end table
                printer.close();
            }
        }
    }
   
    
    
    public static void main(String[] args) throws IOException, TableBindingException{
                
        System.out.println("Starting...");       
        Date start = new Date();
        long start_time = start.getTime();
        
        
        //String edgesFile; 
        String fitFile; 
        String exprsMatFile;
        double fitCutOff;
        double alpha;        
        boolean includesPheno;//FALSE (default), a logical to indicate phenotype simulation be done as well
        String phenotypeId; // "Pheno" (default), identifier used for phenotype in input files...
        String phenoExprsMatFile; // a corresponding "Pheno" expression matrix file        
        int maxIterations = -1; // if greater than -1 
        double eCutOff;
        
        INIT initType = null;
                
        boolean tanTransform;
        boolean logitTransform;
        double k;
        
        boolean simulateKnockout;
        
        KOTYPE knockoutType = null;
        
        
        String outputFile; // a common-name to use for output file(s)
        
        /***
         * edgesFile=./path-to-edges-file/   ### output generated by JFuzzyMachineUtils.graph.Graph, a 'filename.edg' file
         * exprsMatFile=./path-to-expression-matrix-file/
         * fitCutOff=0.6
         * alpha=0.01
         * includesPheno=FALSE
         * phenotypeId=Pheno
         * phenoExprsMatFile=./path-to-phenotype-expression-matrix-file/
         * maxIterations=50                  ### maximum iteration for simulation
         * eCutOff=0.0001                    ### error estimate cut-off
         * initialOutputsValues=ALL                 ### a flag {FIRST, RANDOM, AVERAGE, ALL} to determine initial values for simulation run...
         * 
         */      
        ConfigFileReader cReader = new ConfigFileReader();
        HashMap<String, String> config = cReader.read(args[0]); // configuration file path
        
        //initialize run-time variables....       
        //edgesFile = config.get("edgesFile"); 
        fitFile = config.get("fitFile");
        exprsMatFile = config.get("exprsMatFile"); 
        fitCutOff = Double.parseDouble(config.get("fitCutOff")); 
        alpha = Double.parseDouble(config.get("alpha"));
        
        includesPheno = Boolean.parseBoolean(config.get("includesPheno"));
        phenotypeId = config.get("phenotypeId"); 
        phenoExprsMatFile = config.get("phenoExprsMatFile");
        
        maxIterations = Integer.parseInt(config.get("maxIterations"));
        eCutOff = Double.parseDouble(config.get("eCutOff"));
        String initValues = config.get("initialOutputsValues");        
        
        //determine initial outputs values enum type...
        if(initValues.equalsIgnoreCase("FIRST"))
            initType = INIT.FIRST;
        else if(initValues.equalsIgnoreCase("RANDOM"))
            initType = INIT.RANDOM;
        else if(initValues.equalsIgnoreCase("AVERAGE"))
            initType = INIT.AVERAGE;
        else if(initValues.equalsIgnoreCase("ALL"))
            initType = INIT.ALL;
        
        tanTransform = Boolean.parseBoolean(config.get("tanTransform"));
        logitTransform = Boolean.parseBoolean(config.get("logitTransform"));
        k = Double.parseDouble(config.get("kValue"));
        
        simulateKnockout = Boolean.parseBoolean(config.get("simulateKnockout"));  
        if(simulateKnockout)
            config.replace("simulationType", "ksimulations");
                
        //get knockoutType...
        String koType = config.get("knockoutType");
        if(koType.equalsIgnoreCase("SINGLE")){
            knockoutType = KOTYPE.SINGLE;
        }else{
            knockoutType = KOTYPE.DOUBLE;
        }        
        
        //create a simulation directory...
        File fitFileJFile = new File(fitFile);
        String dirPath = fitFileJFile.getParent();
        //String simulationsDirPath = dirPath + File.separator + "simulations";
        String simulationsDirPath = dirPath + File.separator + config.get("simulationType");
        
        if(simulateKnockout)
            simulationsDirPath = simulationsDirPath + File.separator + config.get("knockoutType");
        
        //make simulations directory...
        if(!new File(simulationsDirPath).exists()){
            new File(simulationsDirPath).mkdirs();
        }
                       
        //outputFile = edgesFile.replace(".txt", "").replace(".tsv", "").replace(".edg", ""); 
        outputFile = fitFileJFile.getName();
        outputFile = outputFile.replace(".fit", "").replace(".fit2", "").replace(".txt", "");        
        //final String outputFileBaseName = outputFile.replace(".fit", "").replace(".fit2", "").replace(".txt", "");        
        
         Simulator sim = new Simulator(fitFile, exprsMatFile, fitCutOff, alpha,
                                        includesPheno, phenotypeId, 
                                            phenoExprsMatFile, 
                                                maxIterations, eCutOff, initType,
                                                tanTransform, logitTransform,  k,
                                                    simulateKnockout,
                                                        knockoutType);
        
        
        System.out.println("Running simulations...");
        sim.runSimulations();
        
        System.out.println("Printing simulations results...");
        sim.printSimulations(simulationsDirPath, outputFile);
                
        System.out.println("\n...Done!");        
        Date end = new Date();
        long end_time = end.getTime();
        
        System.out.println("\n     Started: " + start.toString());
        System.out.println("     Ended: " + end.toString());
        System.out.println("Total time: " + (end_time - start_time) + " milliseconds; " + 
                        TimeUnit.MILLISECONDS.toMinutes(end_time - start_time) + " min(s), "
                        + (TimeUnit.MILLISECONDS.toSeconds(end_time - start_time) - 
                           TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(end_time - start_time))) + 
                                                      " seconds.");        
               
    }
    
}