package cl.ucn.disc.hpc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Soduku class
 */
@Slf4j
public class Sudoku {

    public static int[][] GRID_TO_SOLVE = {
            {0, 0, 0, 6, 0, 0, 9, 0, 0},
            {0, 8, 0, 0, 0, 7, 0, 4, 0},
            {0, 0, 3, 4, 8, 0, 7, 2, 0},
            {0, 6, 5, 0, 7, 0, 8, 0, 0},
            {2, 7, 8, 9, 0, 0, 0, 0, 0},
            {0, 4, 0, 0, 3, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 4, 0, 8, 0},
            {0, 0, 4, 0, 0, 2, 6, 0, 3},
            {6, 3, 0, 0, 0, 0, 4, 5, 2},
    };

    public static final int N = 9;

    public static boolean finished = false;

    /**
     * The main
     *
     * @param args
     */
    public static void main(String[] args) {

        log.debug("Program initialized...");

        int[][] empty = createEmptyMatrix(9);
        showMatrix(empty);
        solutionSudoku(empty);

        log.debug("Program finished");


    }

    public static void solutionSudoku(int [][] matrix){

        final int cores = 2;

        final StopWatch sw = StopWatch.createStarted();

        //The Executor of Threads
        final ExecutorService executor = Executors.newFixedThreadPool(cores);

        if(solve(0,-1,matrix,0,0)){
            showMatrix(matrix);
            log.debug("Eureka!!");
        }else{
            log.debug("No solution for this sudoku ");
        }
    }

    /**
     * Given a size create a matrix full of zeros
     * @param N size
     * @return The matrix
     */
    public static int[][] createEmptyMatrix(int N){

        int[][] out = new int[N][N];

        for(int row = 0; row < N; row++) {

            for (int col = 0; col < N; col++) {

                out[row][col] = 0;

            }
        }
        return out;
    }

    /**
     * Show the matrix
     * @param matrix
     */
    public static void showMatrix(int[][] matrix){

        String out = "Matrix\n";
        int row = matrix.length;
        int col = matrix[0].length;

        for(int i = 0; i < row; i++) {

            if(i % Math.sqrt(row) == 0 && i >0){
                String spaces = new String(new char[row]).replace("\0", "- ");
                out += "\n- "+spaces+"-\n";
            }else{
                out += "\n";
            }

            for (int j = 0; j < col; j++) {

                if(j % Math.sqrt(col) == 0 && j > 0){
                    out += "| ";
                }
                out += String.valueOf(matrix[i][j]) + " ";
            }
        }

        log.debug(out);
    }

    /**
     * Check if a value is in a box
     * @param row
     * @param col
     * @param value
     * @param matrix
     * @return true if it is
     */
    private static boolean checkValueBox(int row, int col, int value, int[][] matrix) {
        int r = row - row % ((int) Math.sqrt(matrix.length));
        int c = col - col % ((int) Math.sqrt(matrix.length));

        for (int i = r; i < r + ((int) Math.sqrt(matrix.length)); i++)
            for (int j = c; j < c + ((int) Math.sqrt(matrix.length)); j++)
                if (matrix[i][j] == value)
                    return true;

        return false;
    }

    /**
     * Check if a value if can be placed in a specific cell
     * @param row
     * @param col
     * @param value
     * @param matrix
     * @return
     */
    public static boolean checkCell(int row, int col, int value, int[][] matrix){

        for(int i = 0; i < matrix.length; i++){

            //Check rows
            if(matrix[i][col] == value){
                return false;
            }
            //Check col
            if(matrix[row][i] == value){
                return false;
            }
        }
        //Check box
        if(checkValueBox(row,col,value,matrix)){
            return false;
        };

        return true;
    }

    /**
     * Recursive method that checks cell by cell
     * @param row
     * @param col
     * @param matrix
     * @param numberCell
     * @param startValue
     * @return
     */
    public static boolean solve(int row,int col, int[][] matrix, int numberCell, int startValue){


        //showMatrix(matrix);

        //Out: reach the last cell
        if(numberCell == N * N){
            return true;
        }

        //if finished is true another thread finished the sudoku
        if(finished) return true;

        //The col is in the beyond the last col
        if(++col == N){
            col = 0;
            if(++row == N) row = 0;
        }

        //Value in the sudoku
        if(matrix[row][col] != 0){
            return solve(row,col,matrix,numberCell + 1,startValue);   //Pass to next cell
        }else{ //An empty cell

            //Loop for each valid value 1 to N
            for(int val=1 ; val <= N; val++){

                if(++startValue == N+1) startValue = 1; //reset the startValue

                if(checkCell(row,col,val,matrix)){  //Valid value for the cell
                    matrix[row][col] = val;

                    if(solve(row,col,matrix,numberCell + 1,startValue)); //Pass to next cell
                }

            }

            matrix[row][col] = 0; //Reset on backtrack
            return false;

        }

    }

}
