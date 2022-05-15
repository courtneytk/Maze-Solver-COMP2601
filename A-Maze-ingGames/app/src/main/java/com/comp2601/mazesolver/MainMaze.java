package com.comp2601.mazesolver;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

//public class MainMaze extends

public class MainMaze extends AppCompatActivity {

    private static Button btnPop;

    // Scale of the game (number of rows and columns)

    //Can change the number of cells per row or column in the maze by simply changing NUM_COLS or NUM_ROWS variables
    private  static final int NUM_ROWS = 11;
    private  static final int NUM_COLS = 9;

    // Initial Start and Destination locations
    private  static final int INITIAL_START_ROW = 0;
    private  static final int INITIAL_START_COL = 0;
    private  static final int INITIAL_DESTINATION_ROW = NUM_ROWS-1;
    private  static final int INITIAL_DESTINATION_COL = NUM_COLS-1;

    // To determine the beginning and destination of the maze
    private Button startButton;
    private Button endButton;
    private Button wallButton;
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;
    private int numLoops = 10;
    private static int count = 0 ;

    //For generating random values between a certain number
    private int min1 = 0;
    private int max1 = NUM_ROWS-1; //Assign it to whichever (row or column) is smaller
    private int min2 = 0;
    private int max2 = NUM_COLS;

    //Button that accounts for the solve maze request
    private static Button solveMazeButton;

    //Button that deals with request for generation of obstacles
    private static Button genObstacles;

    //Button that emails the maze results to the user's email
    private static Button emailResults;

    //Button to change theme of the app
    private static Button changeTheme;

    //Button to reset maze
    private static Button resetMaze;


    //Boolean to track if maze was solved or not
    private boolean mazeSolved = false;

    //Two-dimensional array to store rows and columns of the maze
    Button buttons[][] = new Button[NUM_ROWS][NUM_COLS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        //Utils.onActivityCreateSetTheme(MainMaze.this,count++);
        Utils.onActivityCreateSetTheme(MainMaze.this);
        setContentView(R.layout.activity_main);

        //Information to use on theme of the app
        changeTheme = (Button) findViewById(R.id.changeTheme);
        genObstacles = (Button) findViewById(R.id.button_obstacles);
        resetMaze = (Button) findViewById(R.id.button_reset);


        // Adding buttons with UI Threads
        TableLayout gameLayout = findViewById(R.id.gameTable);

        //Using double iteration to create rows and columns to form the layout of the maze
        for (int row = 0; row < NUM_ROWS; row++){

            TableRow tableRow = new TableRow(MainMaze.this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    0,0,0 //This is what allows for no margins between each row
            ));
            gameLayout.addView(tableRow);

            for (int col = 0; col < NUM_COLS; col++){

                final int FINAL_COL = col;
                final int FINAL_ROW = row;
                final Button button = new Button(MainMaze.this);

                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if((button == buttons[INITIAL_START_ROW][INITIAL_START_COL] || button == buttons[INITIAL_DESTINATION_ROW][INITIAL_DESTINATION_COL]) && button.getText() == getResources().getString(R.string.empty)){
                            Button b = (Button) v;
                            setDetails(b,"Wall");
                            Toast t = Toast.makeText(MainMaze.this, "Button Clicked", Toast.LENGTH_SHORT);
                            t.show();
                        }
                        else if(button.getText() == getResources().getString(R.string.empty)) {
                            if(startButton == null){
                                startButtonSetup(button,FINAL_ROW,FINAL_COL);
                            }
                            else if(endButton == null){
                                endButtonSetup(button,FINAL_ROW,FINAL_COL);
                            }
                            else{
                                Button b = (Button) v;
                                setDetails(b,"Wall");
                                Toast t = Toast.makeText(MainMaze.this, "Button Clicked", Toast.LENGTH_SHORT);
                                t.show();
                            }
                        }
                        else if(button.getText() == getResources().getString(R.string.start)){
                            Button b = (Button) v;
                            setDetails(b,"Empty");
                            Toast t = Toast.makeText(MainMaze.this, "Button Clicked", Toast.LENGTH_SHORT);
                            t.show();
                            startButton = null;

                        }
                        else if(button.getText() == getResources().getString(R.string.destination)){
                            Button b = (Button) v;
                            setDetails(b,"Empty");
                            Toast t = Toast.makeText(MainMaze.this, "Button Clicked", Toast.LENGTH_SHORT);
                            t.show();
                            endButton = null;

                        }
                        else if(button.getText() == getResources().getString(R.string.wall)){
                            Button b = (Button) v;
                            setDetails(b,"Empty");
                            Toast t = Toast.makeText(MainMaze.this, "Button Clicked", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    }


                });

                //Row layout for the maze
                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );

                //Setting up the current button
                generalButtonSetup(button,params);

                //Adding the current row of buttons to the general view
                tableRow.addView(button);
                buttons[row][col] = button;
            }
        }


        //Setting the initial start and end buttons when program starts
        startButtonSetup(buttons[INITIAL_START_ROW][INITIAL_START_COL],INITIAL_START_ROW,INITIAL_START_COL);
        endButtonSetup(buttons[INITIAL_DESTINATION_ROW][INITIAL_DESTINATION_COL],INITIAL_DESTINATION_ROW,INITIAL_DESTINATION_COL);


        //Solve maze button on the UI
        solveMazeButton = (Button) findViewById(R.id.button_solve_maze);
        genObstacles = (Button) findViewById(R.id.button_obstacles);
        emailResults = (Button) findViewById(R.id.button_email);


        //Solve maze click listener
        solveMazeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Checking if maze was solved before
                if(mazeSolved){
                    //Clear the previous path of the maze
                    for (int row = 0; row < NUM_ROWS; row++) {
                        for (int col = 0; col < NUM_COLS; col++) {
                            Button b = buttons[row][col];
                            if (b.getText() == getResources().getString(R.string.path))
                            {
                                setDetails(b,"Empty");
                            }
                        }
                    }
                    mazeSolved = false;
                }

                //Disabling all the buttons on the grid
                for(int row=0; row<NUM_ROWS; row++){
                    for(int col=0; col<NUM_COLS; col++){
                        buttons[row][col].setEnabled(false);

                    }
                }

                //Disable the solve button
                solveMazeButton.setEnabled(false);
                genObstacles.setEnabled(false);
                emailResults.setEnabled(false);
                changeTheme.setEnabled(false);
                resetMaze.setEnabled(false);




                //Solving the maze

                //Creates an instance of the asynchronous task and executes
                if(!mazeSolved){
                    MazeSolve executeTask = new MazeSolve();
                    executeTask.execute();
                }

            }

        });

        emailResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PopupActivity.class);
                startActivity(intent);
            }
        });

        changeTheme.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(count == 2)
                {
                    Utils.changeToTheme(MainMaze.this,count);
                    count = 1;
                }
                else if(count == 1)
                {
                    Utils.changeToTheme(MainMaze.this,count);
                    count = 2;
                }
                else if(count == 0)
                {
                    Utils.changeToTheme(MainMaze.this,count);
                    count = 2;
                }
            }
        });

        genObstacles.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Clearing the table before adding obstacles
                for (int row = 0; row < NUM_ROWS; row++) {
                    for (int col = 0; col < NUM_COLS; col++) {
                        //Button b = buttons[row][col];
                        setDetails(buttons[row][col],"Empty");
                    }
                }
                for(int i=0; i<numLoops; i++)
                {
                    Random random = new Random();
                    int numberRow = random.nextInt(NUM_ROWS);
                    int numberCol = random.nextInt(NUM_COLS);

                    wallSetup(buttons[numberRow][numberCol]);

                }


                startButtonSetup(buttons[startRow][startCol],startRow,startCol);
                endButtonSetup(buttons[endRow][endCol],endRow,endCol);

            }
        });

        resetMaze.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //Clearing all the buttons to empty
                for (int row = 0; row < NUM_ROWS; row++)
                {
                    for (int col = 0; col < NUM_COLS; col++)
                    {
                        Button b = buttons[row][col];
                        setDetails(b,"Empty");

                    }
                }
                startButtonSetup(buttons[INITIAL_START_ROW][INITIAL_START_COL],INITIAL_START_ROW,INITIAL_START_COL);
                endButtonSetup(buttons[INITIAL_DESTINATION_ROW][INITIAL_DESTINATION_COL],INITIAL_DESTINATION_ROW,INITIAL_DESTINATION_COL);

            }
        });



    }

    private class MazeSolve extends AsyncTask<Void, Button, String> {

        //Want to do a LILO queue
        private final Stack<Button> pathProgress = new Stack<Button>();

        //Stores the path for the duration of the maze solve task
        private final ArrayList<Button> path = new ArrayList<Button>();

        //Function checks current cell characteristics to ensure a path can be created using it
        private String checkCells(int currentButtonRow, int currentButtonCol){

            if (currentButtonRow < 0 || currentButtonRow > NUM_ROWS-1 || currentButtonCol < 0 || currentButtonCol > NUM_COLS-1){
                return "not found";
            }

            //Check if current path value is already in array list (edge case)
            else if(path.contains(buttons[currentButtonRow][currentButtonCol])){
                return "not found";
            }

            //Determining if we reached the destination
            else if (currentButtonRow == endRow && currentButtonCol == endCol) {
                return "found";
            }

            //If we are at a wall
            else if(buttons[currentButtonRow][currentButtonCol].getText() == getResources().getString(R.string.wall)){
                return "not found";
            }

            //Adding the button to the solution
            path.add(buttons[currentButtonRow][currentButtonCol]);

            //Publish progress so far on actual app (since adding to ArrayList will not be visible)
            publishProgress(buttons[currentButtonRow][currentButtonCol]);

            //Delaying output so it's easier to see
            delay();

            //Checking adjacent cells using recursive steps

            //Above current button
            if(checkCells(currentButtonRow,currentButtonCol-1).equals("found")){
                return "found";
            }

            //Below current button
            else if(checkCells(currentButtonRow,currentButtonCol+1).equals("found")) {
                return "found";
            }

            //To the left of current button
            else if(checkCells(currentButtonRow-1,currentButtonCol).equals("found")) {
                return "found";
            }


            //To the right of current button
            else if(checkCells(currentButtonRow+1,currentButtonCol).equals("found")) {
                return "found";
            }

            //Not sending any arguments here because if we have come this far, it means that there isn't a viable button to move to
            publishProgress();

            //Delaying output so that we can see the process
            delay();

            //If we reach this part of the code without returning it means that a path value wasn't found so that's what is returned
            return "not found";
        }

        @Override
        protected String doInBackground(Void... arg0) {

            return checkCells(startRow,startCol);

        }

        @Override
        protected void onProgressUpdate(Button... args) {
            // Adding to the current path
            if (args.length != 0) {
                pathProgress.push(args[0]);

                if (args[0].getText() != getResources().getString(R.string.start)){
                    setDetails(args[0],"Path");
                }

            }
            // Removing from the current path
            else
            {
                Button removeValuePath = pathProgress.pop();

                if (removeValuePath.getText() != getResources().getString(R.string.start)){
                    setDetails(removeValuePath,"Empty");
                }
            }

        }

        @Override
        protected void onPostExecute(String pathValue) {
            //Setting the solved maze variable to a true boolean value
            mazeSolved = true;

            //Outputting short statement to user to indicate if path was found or not
            if (pathValue.equals("found"))
            {
                Toast t = Toast.makeText(MainMaze.this, "Great! A path was found!", Toast.LENGTH_LONG);
                t.show();

            }
            else if(pathValue.equals("not found"))
            {
                Toast t = Toast.makeText(MainMaze.this, "A path was not found!", Toast.LENGTH_LONG);
                t.show();
            }

            //Enabling the maze buttons
            for(int row=0; row<NUM_ROWS; row++){
                for(int col=0; col<NUM_COLS; col++){
                    buttons[row][col].setEnabled(true);

                }
            }

            // Enabling the solve button
            solveMazeButton.setEnabled(true);
            genObstacles.setEnabled(true);
            emailResults.setEnabled(true);
            changeTheme.setEnabled(true);
            resetMaze.setEnabled(true);


        }
    }


    //Functions to reduce repetition

    //Details set for start button
    private void startButtonSetup(Button newStartButton, int row, int col){
        startButton = newStartButton;
        setDetails(startButton,"Start");

        //To retrieve updated information of the position of the start row and column
        startRow = row;
        startCol = col;
    }

    //Details for creating obstacles
    private void wallSetup(Button newWall){
        wallButton = newWall;
        setDetails(wallButton,"Wall");
    }


    //Details set for end button
    private void endButtonSetup(Button newEndButton, int row, int col){
        endButton = newEndButton;
        setDetails(endButton,"End");

        //To retrieve updated information of the position of the end row and column
        endRow = row;
        endCol = col;
    }

    //Setting up the default state of every button in the maze
    private void generalButtonSetup(Button currentButton, TableRow.LayoutParams params){
        currentButton.setLayoutParams(params);

        //Setting the button width and heights to become squared
        currentButton.getLayoutParams().width = 120;
        currentButton.getLayoutParams().height = 120;

        currentButton.setPadding(1,1,1,1);
        setDetails(currentButton,"Empty");
    }

    //Setting the color and text to each button
    private void setDetails(Button b,String value){
        if(value.equals("Empty")){
            b.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.empty));
            b.setText(R.string.empty);
        }
        else if(value.equals("Wall")){
            b.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.wall));
            b.setText(R.string.wall);
        }
        else if(value.equals("Start")){
            b.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.start));
            b.setText(R.string.start);
        }
        else if(value.equals("End")){
            b.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.destination));
            b.setText(R.string.destination);
        }
        else if(value.equals("Path")){
            b.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.path));
            b.setText(R.string.path);
        }
    }

    //Puts thread to sleep for a short amount of time so we can see the path finding process
    private void delay(){
        try
        {
            Thread.sleep(50);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
