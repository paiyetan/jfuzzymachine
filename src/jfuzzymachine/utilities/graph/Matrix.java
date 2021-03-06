/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.utilities.graph;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author aiyetanpo
 */
public class Matrix {
    
    private String[] rowIds;
    private String[] columnIds;
    private int[][] matrix;

    public Matrix(String[] rowIds, String[] columnIds, int[][] matrix) {
        this.rowIds = rowIds;
        this.columnIds = columnIds;
        this.matrix = matrix;
    }

    public Matrix(Vertex[] rIds, Vertex[] cIds, int[][] matrix) {
        this.rowIds = new String[rIds.length];
        this.columnIds = new String[cIds.length];
        for(int i = 0; i < rIds.length; i++){
            rowIds[i] = rIds[i].getId();
            columnIds[i] = cIds[i].getId();
        }
        this.matrix = matrix;
    }

    public String[] getRowIds() {
        return rowIds;
    }

    public String[] getColumnIds() {
        return columnIds;
    }

    public int[][] getMatrix() {
        return matrix;
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
    
}
