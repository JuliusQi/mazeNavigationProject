import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

/*
[Main.java]
A program that simulates a maze that a monster travels through to get food. There are spots in the maze where traps can be set, and the program must determine the ideal location to set traps in order to catch the monster no matter which path it chooses to take and while conserving as many traps
@author Julius Qi
April 4th, 2023
*/

class Main {
  public static void main(String[] args) {
    char[][][] map = fileReader();
    arrayPrint(map, 0);
    int numIdCount = 1;
    boolean[] breaks = new boolean[map[0][0].length];
    //no path has an id of 0 so it is true by default
    breaks[0] = true;

    for (int i = 1; i < breaks.length; i++) {
      breaks[i] = false;
    }

    while (pathMapper(numIdCount, 1, 0, map, breaks) == true) {
      arrayPrint(map, numIdCount);
      numIdCount++;
      for (int i = 1; i < breaks.length; i++) {
        breaks[i] = false;
      }
    }

    trapSet(map, numIdCount);

    arrayPrint(map, 0);
  }

  /**
  fileReader asks for input about the file name of the file being used and converts the contents of that file into a character array
  @return a 2d char array with the map
  */
  public static char[][][] fileReader() {
    Scanner input = new Scanner(System.in);
    boolean retry = true;
    int height = 0;
    int length = 0;
    String line;
    int pCount = 0;
    char[][][] map = new char[1][1][1];
    int count = 0;

    while (retry == true) {
      retry = false;
      try {
        System.out.println("Input file name (include file type): ");
        File file = new File(input.next());
        Scanner fileReader = new Scanner(file);

        //figures out size of map
        while (fileReader.hasNextLine()) {
          height++;
          line = fileReader.nextLine();
          length = line.length();

          //counts number of trap placing locations
          while (line.indexOf("P") != -1) {
            pCount++;
            line = line.substring(line.indexOf("P") + 1);
          }
        }

        fileReader.close();
        Scanner fileReader2 = new Scanner(file);
        map = new char[height][length][pCount*5];

        //fill the array (borders are replaced with 'x')
        while (fileReader2.hasNextLine()) {
          line = fileReader2.nextLine();
          for (int j = 0; j < length; j++) {
            if ((line.charAt(0) == '+') || (line.charAt(0) == '-') || (line.charAt(0) == '|')) {
              map[count][j][0] = 'x';
            } else {
              map[count][j][0] = line.charAt(0);
            }
            if (line.length() > 1) {
              line = line.substring(1);
            }
          }
          count++;
        }

        fileReader2.close();
      } catch (FileNotFoundException e) {
        System.out.println("---File not found, please try again---");
        retry = true;
      }
    }
    input.close();
    return map;
  }

  /**
  arrayPrint prints the array in the form of a 2d array
  @param 3d char array
  @param int with the layer that should be printed
  */
  public static void arrayPrint(char[][][] map, int layer) {
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[0].length; j++) {
        if (layer > 0) {
          if (map[i][j][layer] == 't') {
            System.out.print("t ");
          } else {
            System.out.print("  ");
          }
        } else {
          System.out.print(map[i][j][layer]+" ");
        }
      }
      System.out.println();
    }
  }

  /**
  pathMapper is a recursive method that lays out every possible path to take when traveling through the map and leaves behind a numerical id on the squares to indicate which paths have traveled there
  @param an int with the numerical id of the current path
  @param two ints representing the coordinates of the square on the map where the method will start
  @param the 3d array with the map and information slots for path imprints
  @param boolean with true if its a unique path and false if its no
  @param a boolean array with each slot representing whether a break has been found in that path
  @returns boolean value of true if the square at the coordinate given is connected to the food through a unique path
  */
  public static boolean pathMapper(int numId, int y, int x, char[][][] map, boolean[] breaks) {
    boolean validPath = false;
    boolean uniqueStatus = true;
    boolean[] breaksChanged = new boolean[breaks.length];

    //if value in breaks was already true than no change can occur
    for (int i = 0; i < numId; i++) {
      breaksChanged[i] = false;
    }
      
    //leaves behind imprints 
    map[y][x][numId] = 't';

    //updates the breaks array
    for (int i = 1; i < numId; i++) {
      if (map[y][x][i] != 't') {
        //notes what has been changed in this iteration
        if (breaks[i] == false) {
          breaksChanged[i] = true;
        }
        breaks[i] = true;
      }
    }

    //checks whether the path is unique
    for (int i = 0; i < numId; i++) {
      if (breaks[i] != true) {
        uniqueStatus = false;
      }
    }

    //base case
    if (map[y][x][0] == 'F') {
      if (uniqueStatus == true) {
        validPath = true;
      }
    } else {
      //Checks spots above and below
      //h represents change in vertical
      for (int h = -1; h < 2; h = h + 2) {
        //checks for out of bounds
        if (((y + h) < map.length) && ((y + h) > -1)) {
          //looking for possible path
          if ((map[y+h][x][numId] != 't') && (map[y+h][x][0] != 'x')) {
            if (pathMapper(numId, (y+h), (x), map, breaks) == true) {
              validPath = true;
            }
          }
        }
      }
      //checks spots to the left and right
      //u represents change in horizontal
      for (int u = -1; u < 2; u = u + 2) {
        //checks for out of bounds
        if (((x + u) < map[0].length) && ((x + u) > -1)) {
          //looking for possible path
          if ((map[y][x+u][numId] != 't') && (map[y][x+u][0] != 'x')) {
            if (pathMapper(numId, (y), (x+u), map, breaks) == true) {
              validPath = true;
            }
          }
        }
      }
    }
    //removes imprints on the way back and returns breaks array to previous configuration if the branch of the recursion failed to reach the end through a unique path
    if (validPath == false) {
      map[y][x][numId] = ' ';
      for (int i = 1; i < numId; i++) {
        if (breaksChanged[i] == true) {
          breaks[i] = false;
        }
      }
    }

    //returns breaks array to previous configuration if the branch of the recursion failed to reach the end through a unique path
    
    return validPath;
  }

  /**
  trapSet places the least amount of traps required to block all paths for the monster
  @param 3d char array with map and paths
  @param int with the amount of paths (+1)
  */
  public static void trapSet(char[][][] map, int numIdCount) {
    //setting up a 2d int array with all positions of P and the amount of paths that traverse their squares
    //height is equivalent to the amount of P on the map
    int[][] trapPos = new int[map[0][0].length/5][3];
    
    int pFoundCount = 0;
    int lineCount = 0;
    int sum = 0;
    int highestP = 0;

    //finds all p positions
    while (pFoundCount != (map[0][0].length/5)) {
      for (int i = 0; i < map[0].length; i++) {
        if (map[lineCount][i][0] == 'P') {
          //Data stored in y(0), x(1) format
          trapPos[pFoundCount][0] = lineCount;
          trapPos[pFoundCount][1] = i;
          pFoundCount++;
        }
      }
      lineCount++;
    }

    //goes through each P square and tallies the amount of paths that cross there and puts the value in the third slot of trapPos
    //loops for each P position
    for (int i = 0; i < trapPos.length; i++) {
      sum = 0;
      //loops once for each path
      for (int j = 1; j < numIdCount; j++) {
        if (map[trapPos[i][0]][trapPos[i][1]][j] == 't') {
          sum++;
        }
      }
      trapPos[i][2] = sum;
      if (i == 0) {
        highestP = 0;
      } else {
        if (sum > trapPos[highestP][2]) {
          highestP = i;
        }
      }
    }

    //loops until all paths are blocked
    while (trapPos[highestP][2] != 0) {
      //replaces the P with a T
      map[trapPos[highestP][0]][trapPos[highestP][1]][0] = 'T';

      //removes all paths that cross that P from all other P's
      for (int i = 1; i < numIdCount; i++) {
        if (map[trapPos[highestP][0]][trapPos[highestP][1]][i] == 't') {
          //loops for each P location
          for (int j = 0; j < trapPos.length; j++) {
            if (map[trapPos[j][0]][trapPos[j][1]][i] == 't') {
              map[trapPos[j][0]][trapPos[j][1]][i] = ' ';
              trapPos[j][2]--;
            }
          }
        }
      }

      //figures out the new highestP
      for (int i = 0; i < trapPos.length; i++) {
        if (trapPos[i][2] > trapPos[highestP][2]) {
          highestP = i;
        }
      }
    }
  }
}