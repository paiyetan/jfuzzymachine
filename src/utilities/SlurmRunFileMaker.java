/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author aiyetanpo
 * Slurm...Maker makes srun/sbatch input files for each output Node...
 */
public class SlurmRunFileMaker {
    
    @SuppressWarnings("ConvertToTryWithResources")
    public void makeFile(String outputFileName, // slurm batch file...
                         int start, // start gene or feature...
                         int end, // end gene or feature...
                         int numberOfInputs,
                         double fitCutOff
                        ) throws FileNotFoundException{
        
        PrintWriter printer = new PrintWriter(outputFileName);
        printer.println("#!/bin/bash");
        printer.println();
        //printer.println("#SBATCH --partition=unlimited");
        printer.println("#SBATCH --job-name=runJFuzzyMachine."+start+"."+end+"."+numberOfInputs+"                        # Job name");
        printer.println("#SBATCH --mail-type=ALL                                           # Mail events (NONE, BEGIN, END, FAIL, ALL)");
        printer.println("#SBATCH --mail-user=paul.aiyetan@nih.gov                          # Where to send mail	");
        printer.println("#SBATCH --output=/home/aiyetanpo/Slurm/Logs/runJFuzz."+start+"."+end+"."+numberOfInputs+".log   # Standard output and error log");
        printer.println("#SBATCH --mem=16000						   # memory per compute node in MB");
        printer.println("#SBATCH --nodes=16-32						   # nodes per compute node in MB");
        printer.println();        
        printer.println("pwd; hostname; date");
        printer.println();
        printer.println("echo \"Running JFuzzyMachine program on $SLURM_JOB_NUM_NODES nodes with $SLURM_NTASKS tasks, each with $SLURM_CPUS_PER_TASK cores.\"");
        printer.println();    
        printer.println("# ======================== #");
        printer.println("# Start  Executables...");
        printer.println("# ======================== #");
        printer.println("export WORKDIR=\"/scratch/cluster_tmp/aiyetanpo/Applications/Personal/JFuzzyMachine/20191031/\"");
        printer.println("echo \"Program Output begins: \"");
        printer.println("cd $WORKDIR ## cd into working directory...");
        printer.println();
        printer.println("startdate=$(date '+%m/%d/%Y %H:%M:%S')");
        printer.println("out=\"startdate = \"$startdate");
        printer.println("echo $out"); 
        printer.println("echo \"..in $WORKDIR\"");
        printer.println();
        printer.println("# ======================== #");
        printer.println("# run program script(s) here");
        printer.println("# ======================== #");
        printer.println("# define needed program(s) locations");
        printer.println();
        printer.println("java -Xmx8G -cp ./JFuzzyMachine.jar jfuzzymachine.JFuzzyMachine ./JFuzzyMachine.config "+start+" "+end+" "+numberOfInputs+" "+fitCutOff);
        printer.println();
        printer.println("# ======================== #");
        printer.println("# tidy up when done here...");
        printer.println("# =======================+ #");
        printer.println("enddate=$(date '+%m/%d/%Y %H:%M:%S');");
        printer.println("newout=\"enddate = \"$enddate");
        printer.println("echo $newout"); 

        printer.close();
        
    }
    
    public static void main(String[] args) throws FileNotFoundException{
        
        String outputDirectory = args[0]; //args[0], output directory
        int start = Integer.parseInt(args[1]);// start gene or feature...
        int end = Integer.parseInt(args[2]); // end gene or feature...
        int numberOfInputs = Integer.parseInt(args[3]);
        double fitCutOff = Double.parseDouble(args[4]);
        
        SlurmRunFileMaker fileMaker = new SlurmRunFileMaker();
        for(int i = start; i <= end; i++){
            String outputFile = outputDirectory + File.pathSeparator + 
                    "runFuzzyMachine." + i + "." + i + "." + numberOfInputs + ".sh";
            fileMaker.makeFile(outputFile, i, i, 
                               numberOfInputs, fitCutOff);
        }
    }
    
}