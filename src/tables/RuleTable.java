/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tables;

/**
 *
 * @author paiyetan
 */
public class RuleTable {
    
    private final int[][] ruleTable = {{1, 1, 1}, //0
                            {1, 1, 2}, //1
                            {1, 1, 3}, //2
                            {1, 2, 1}, //3
                            {1, 2, 2}, //4
                            {1, 2, 3}, //5
                            {1, 3, 1}, //6
                            {1, 3, 2}, //7 
                            {1, 3, 3}, //8
                            {2, 1, 1}, //9
                            {2, 1, 2}, //10
                            {2, 1, 3}, //11
                            {2, 2, 1}, //12
                            {2, 2, 2}, //13
                            {2, 2, 3}, //14
                            {2, 3, 1}, //15
                            {2, 3, 2}, //16
                            {2, 3, 3}, //17
                            {3, 1, 1}, //18
                            {3, 1, 2}, //19
                            {3, 1, 3}, //20
                            {3, 2, 1}, //21
                            {3, 2, 2}, //22
                            {3, 2, 3}, //23
                            {3, 3, 1}, //24
                            {3, 3, 2}, //25
                            {3, 3, 3}}; //26
                          
    
   
    public int[][] getRuleTable() {
        return ruleTable;
    }
    
    
    
}