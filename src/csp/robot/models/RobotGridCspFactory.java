package csp.robot.models;

public class RobotGridCspFactory {


    public static RobotGridCsp getDefaultRobotGrid() {
        int rows = 5;
        int cols = 5;
        int energyLimit = 5;
        int[][] targets = {{0, 4}};
        boolean[][] restrictedCells = new boolean[rows][cols];
        restrictedCells[1][2] = true;
        restrictedCells[3][1] = true;
        return new RobotGridCsp(rows, cols, energyLimit, restrictedCells, targets);
    }

    public static RobotGridCsp getComplexRobotGrid() {
        int rows = 50;
        int cols = 50;
        int energyLimit = 408;
        int[][] targets = {{0, 4}, {5, 7}, {8, 9}};
        boolean[][] restrictedCells = new boolean[rows][cols];
        restrictedCells[1][2] = true;
        restrictedCells[3][1] = true;
        restrictedCells[0][1] = true;
        restrictedCells[0][49] = true;
        return new RobotGridCsp(rows, cols, energyLimit, restrictedCells, targets);
    }

    public static RobotGridCsp createRobotGridWithNoPathSolution() {
        int rows = 5;
        int cols = 5;
        int energyLimit = 2;
        int[][] targets = {{0, 4}};
        boolean[][] restrictedCells = new boolean[rows][cols];
        restrictedCells[1][2] = true;
        restrictedCells[3][1] = true;
        return new RobotGridCsp(rows, cols, energyLimit, restrictedCells, targets);
    }

    public static RobotGridCsp getArc4RobotGridConfiguration() {
        int rows = 7;
        int cols = 7;
        int energyLimit = 7;
        int[][] targets = {{0, 6}};
        boolean[][] restrictedCells = new boolean[rows][cols];
        restrictedCells[2][3] = true;
        restrictedCells[3][2] = true;
        restrictedCells[3][4] = true;
        restrictedCells[4][3] = true;
        return new RobotGridCsp(rows, cols, energyLimit, restrictedCells, targets);
    }
}
