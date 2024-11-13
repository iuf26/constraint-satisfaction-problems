package csp.robot;

import csp.model.TrackAndAdjustStrategy;
import csp.model.Variable;
import csp.robot.models.RobotGridCsp;
import csp.robot.models.RobotGridCspFactory;
import csp.model.LookaheadStrategy;
import csp.robot.models.strategies.RobotNavigationLookaheadStrategy;
import csp.robot.models.strategies.RobotNavigationTrackAndAdjust;

import java.util.Map;
import java.util.Objects;

public class RunnerCspRobot {


    private static Map<Variable, Object> runWithForwardCheckStrategy(RobotGridCsp robotGridCSP) {
        LookaheadStrategy robotLookahead = new RobotNavigationLookaheadStrategy(robotGridCSP.getEnergyLimit());
        return robotGridCSP.solveBacktracking(robotLookahead, null);

    }

    private static Map<Variable, Object> runWithTrackAndAdjust(RobotGridCsp robotGridCSP) {
        TrackAndAdjustStrategy trackAndAdjustStrategy = new RobotNavigationTrackAndAdjust(robotGridCSP.getEnergyLimit(), robotGridCSP.getRestrictedCells());
        return robotGridCSP.solveBacktracking(null, trackAndAdjustStrategy);

    }

    private static Map<Variable, Object> runWithBaseBacktracking(RobotGridCsp robotGridCsp) {
        return robotGridCsp.solveBacktracking(null, null);
    }



    public static void run() {
        RobotGridCsp robotGridCsp = RobotGridCspFactory.getComplexRobotGrid();
//        Map<Variable, Object> assignment = runWithBaseBacktracking(robotGridCsp);
        Map<Variable, Object> assignment = runWithTrackAndAdjust(robotGridCsp);
//        Map<Variable, Object> assignment = runWithForwardCheckStrategy(robotGridCsp);
        robotGridCsp.displaySolution(assignment);
        robotGridCsp.displayConstraintGraphSize();
        if (Objects.nonNull(assignment)){
            System.out.println("Solution size: " + assignment.size());
        }
//        RobotGridCsp robotGridCSP = RobotGridCspFactory.getArc4RobotGridConfiguration();
//        robotGridCSP.displayConstraintGraphSize();
//        //BACKTRACKING
//        Map<Variable, Object> assignment = robotGridCSP.solveBacktracking();
//        robotGridCSP.displaySolution(assignment);
//
//        //NODE CONSISTENCY
//        Map<Variable, Object> assignmentNodeConsistency = robotGridCSP.solveWithNodeConsistency();
//        robotGridCSP.displaySolution(assignmentNodeConsistency);


        //ARC CONSISTENCY ARC4
//        Map<Variable, Object> assignmentArc4 = robotGridCSP.solveWithArc4Algorithm();
//        robotGridCSP.displaySolution(assignmentArc4);

        //TRACK AND ADJUST
//        runWithTrackAndAdjust();


        //FORWARD CHECK STRATEGY
        //runWithForwardCheckStrategy();


    }

}
