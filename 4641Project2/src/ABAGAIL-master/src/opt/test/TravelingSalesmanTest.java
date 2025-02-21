package opt.test;

import java.util.Arrays;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.SwapNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.SwapMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class TravelingSalesmanTest {
    /** The n value */
    private static int N = 50;
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) {
        int iters = 1000;
        for(int trial = 0; trial < 5; trial++) {
        for (N = 100; N < 200; N += 10) {
        Random random = new Random();
        // create the random points
        double[][] points = new double[N][2];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = random.nextDouble();
            points[i][1] = random.nextDouble();   
        }
        // for rhc, sa, and ga we use a permutation based encoding
        TravelingSalesmanEvaluationFunction ef = new TravelingSalesmanRouteEvaluationFunction(points);
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new TravelingSalesmanCrossOver(ef);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, iters);
        long starttime = System.currentTimeMillis();
        fit.train();
        System.out.print(N + ",rhc," + ef.value(rhc.getOptimal()));
        System.out.print(",time, " + (System.currentTimeMillis() - starttime));
        
        SimulatedAnnealing sa = new SimulatedAnnealing(1E12, .95, hcp);
        fit = new FixedIterationTrainer(sa, iters);
        fit.train();
        System.out.print(",sa," + ef.value(sa.getOptimal()));
        System.out.print(",time, " + (System.currentTimeMillis() - starttime));
        
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 150, 20, gap);
        fit = new FixedIterationTrainer(ga, 1000);
        fit.train();
        System.out.print(",ga," + ef.value(ga.getOptimal()));
        System.out.println(",time," + (System.currentTimeMillis() - starttime));
        // for mimic we use a sort encoding
        //ef = new TravelingSalesmanSortEvaluationFunction(points);
        //int[] ranges = new int[N];
        //Arrays.fill(ranges, N);
        //odd = new  DiscreteUniformDistribution(ranges);
        //Distribution df = new DiscreteDependencyTree(.1, ranges); 
        //ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        /*
        MIMIC mimic = new MIMIC(200, 100, pop);
        fit = new FixedIterationTrainer(mimic, iters);
        fit.train();
        System.out.println("," + ef.value(mimic.getOptimal()));
        System.out.print(", " + (System.currentTimeMillis() - starttime));
        */
        
    }
    }
}
}
