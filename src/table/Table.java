/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author aiyetanpo
 */
public class Table {
            
    private String[] rowIds;
    private String[] columnIds;
    private String[][] matrix;
    
    private double[][] dMatrix;
    
    public enum TableType {INTEGER, DOUBLE};
        
    public Table(String inFilePath) throws IOException{
        read(inFilePath);
    }

    public Table(String inFilePath, Table.TableType type) throws IOException{
        read(inFilePath);
        switch(type){
            case DOUBLE:
               dMatrix = new double[rowIds.length][columnIds.length];
               for(int i = 0; i < rowIds.length; i++){
                   for(int j = 0; j < columnIds.length; j++){
                       dMatrix[i][j] = Double.parseDouble(matrix[i][j]);
                   }
               }
            case INTEGER:
                //yet to be implemented...
            default:
                break;
        }
    }

    
    public Table(String[] rowIds, 
            String[] columnIds, String[][] matrix) {
        this.rowIds = rowIds;
        this.columnIds = columnIds;
        this.matrix = matrix;
    }
  
    public String[] getColumnIds() {
        return columnIds;
    }

    public String[] getRowIds() {
        return rowIds;
    }

    public String[][] getMatrix() {
        return matrix;
    }
    
    public double[][] getMatrix(Table.TableType type) {
        return dMatrix;
    }
    
    public String[] getRow(int index){
        String[] indexedRow = new String[columnIds.length];
        for(int i=0; i < indexedRow.length; i++){
            indexedRow[i] = matrix[index][i];
        }
        return indexedRow;
    }
    
    public String[] getColumn(int index){
        String[] indexedColumn = new String[rowIds.length];
        for(int i=0; i < indexedColumn.length; i++){
            indexedColumn[i] = matrix[i][index];
        }
        return indexedColumn;
    }
    
    public double[] getRow(int index, Table.TableType type){
        double[] indexedRow = new double[columnIds.length];
        for(int i=0; i < indexedRow.length; i++){
            indexedRow[i] = dMatrix[index][i];
        }
        return indexedRow;
    }
    
    public double[] getColumn(int index, Table.TableType type){
        double[] indexedColumn = new double[rowIds.length];
        for(int i=0; i < indexedColumn.length; i++){
            indexedColumn[i] = dMatrix[i][index];
        }
        return indexedColumn;
    }
    
    
    public void print(String outFile) throws FileNotFoundException{
        PrintWriter printer = new PrintWriter(outFile);
        // print header...
        printer.print("Features");
        for(String columnID : columnIds){
           printer.print("\t" + columnID);
        }
        printer.print("\n");
        
        // print the body
        for(int i = 0; i < rowIds.length; i++){
            printer.print(rowIds[i]);
            for(int j = 0; j < columnIds.length; j++){
                printer.print("\t" + matrix[i][j]);
            }
            printer.print("\n");
        }
        
        printer.close();
    }

    private void read(String inFilePath) throws FileNotFoundException, IOException {
        
        BufferedReader reader = new BufferedReader(new FileReader(inFilePath));
        String line;
        int lineIndex = 0;
        ArrayList<String> rows = new ArrayList();
        
        while((line = reader.readLine())!=null){
            lineIndex++;
            String[] lineArr;
            if(lineIndex==1){// this is the header line...
                lineArr = line.split("\t");
                columnIds = new String[lineArr.length-1];
                for(int i = 1; i < lineArr.length; i++){
                    columnIds[i-1] = lineArr[i];
                }
            }else{
                rows.add(line);
            }
        }
        
        reader.close();
        
        rowIds = new String[rows.size()];
        matrix = new String[rowIds.length][columnIds.length];
        //populate matrix...
        for(int i = 0; i < rows.size(); i++){
            String rowInfo = rows.get(i);
            String[] infoArr = rowInfo.split("\t");
            for(int j = 0; j < infoArr.length; j ++){
                if(j == 0)
                    rowIds[i] = infoArr[j];
                else
                    matrix[i][j-1] = infoArr[j];
            }
            
        }        
    }
    
}
