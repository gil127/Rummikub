package rummikubLogic;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import dataObject.Cube;

public class CubeBank {
 
    private static final int NUMBER_OF_COLOR_IN_GAME = 4;
    private static final int NUMBER_OF_SAME_CUBES_IN_GAME = 2;
    private static final int LENGTH_OF_NUMBERS_IN_GAME = 13;
    
    private final List<Cube> cubeBank;
    
    public CubeBank() {
        cubeBank = new ArrayList<Cube>();
    }

    public void initNewRound() {
        cubeBank.clear();
        for (int k = 0; k < NUMBER_OF_COLOR_IN_GAME; k++) {
            Cube.Color color = getColorFromIndex(k);

            for (int i = 0; i < NUMBER_OF_SAME_CUBES_IN_GAME; i++) {
                for (int j = 1; j <= LENGTH_OF_NUMBERS_IN_GAME; j++) {
                    Cube cube = new Cube(color, j, false);
                    cubeBank.add(cube);
                }
            }
        }
        
        cubeBank.add (new Cube(Cube.Color.BLACK, 0, true)); // Joker
        cubeBank.add (new Cube(Cube.Color.BLACK, 0, true)); // Joker
    }
    
    private Cube.Color getColorFromIndex(int index) {
       return Cube.Color.values()[index];
    }
    
    public int getCubeSize() {
        return cubeBank.size();
    }
    
    public Cube getRandomCube() {
        Cube res;
        Random rand = new Random();
        int randomValue;
        
        randomValue = rand.nextInt(cubeBank.size());
        res = cubeBank.get(randomValue);
        cubeBank.remove(randomValue);
        
        return res;
    }

    public void initCubesFromLoadedGame(ArrayList<Cube> allCubesInGame) {
        int[] blackCubes = new int [13];
        int[] blueCubes = new int [13];
        int[] yellowCubes = new int [13];
        int[] redCubes = new int [13];
        int numberOfJoker = 0;
        
        resetBucket(blackCubes);
        resetBucket(blueCubes);
        resetBucket(yellowCubes);
        resetBucket(redCubes);
        
        for (Cube cube : allCubesInGame) {
            if (!cube.isJoker()) {
                switch (cube.getColor()) {
                    case BLUE:
                        blueCubes[cube.getValue() - 1]++;
                        break;
                    case BLACK:
                        blackCubes[cube.getValue() - 1]++;
                        break;
                    case YELLOW:
                        yellowCubes[cube.getValue() - 1]++;
                        break;
                    case RED:
                        redCubes[cube.getValue() - 1]++;
                        break;
                }
            }
            else {
                numberOfJoker++;
            }
        }
        
        initCubesAfterCheck(blueCubes, blackCubes, redCubes, yellowCubes, numberOfJoker);
    }
    
    private void resetBucket(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = 0;
        }
    }

    private void initCubesAfterCheck(int[] blueCubes, int[] blackCubes, int[] redCubes, int[] yellowCubes, int numberOfJoker) {
        initBlackCubes(blackCubes);
        initBlueCubes(blueCubes);
        initYellowCubes(yellowCubes);
        initRedCubes(redCubes);
        initJokers(numberOfJoker);
    }

    private void initBlackCubes(int[] blackCubes) {
        for(int i = 0; i < blackCubes.length; i++) {
            for (int j = NUMBER_OF_SAME_CUBES_IN_GAME ; j > blackCubes[i] ; j--) {
                cubeBank.add(new Cube(Cube.Color.BLACK, i + 1, false));
            }
        }
    }
    
    private void initBlueCubes(int[] blueCubes) {
        for(int i = 0; i < blueCubes.length; i++) {
            for (int j = NUMBER_OF_SAME_CUBES_IN_GAME ; j > blueCubes[i] ; j--) {
                cubeBank.add(new Cube(Cube.Color.BLUE, i + 1, false));
            }
        }
    }
    
    private void initRedCubes(int[] redCubes) {
        for(int i = 0; i < redCubes.length; i++) {
            for (int j = NUMBER_OF_SAME_CUBES_IN_GAME ; j > redCubes[i] ; j--) {
                cubeBank.add(new Cube(Cube.Color.RED, i + 1, false));
            }
        }
    }
    
    private void initYellowCubes(int[] yellowCubes) {
        for(int i = 0; i < yellowCubes.length; i++) {
            for (int j = NUMBER_OF_SAME_CUBES_IN_GAME ; j > yellowCubes[i] ; j--) {
                cubeBank.add(new Cube(Cube.Color.YELLOW, i + 1, false));
            }
        }
    }

    private void initJokers(int numberOfJoker) {
        for (int i = NUMBER_OF_SAME_CUBES_IN_GAME; i > numberOfJoker; i--) {
            cubeBank.add(new Cube(Cube.Color.BLACK, 0, true));
        }
    }
}