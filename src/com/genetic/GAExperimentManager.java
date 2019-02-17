package com.genetic;

public class GAExperimentManager {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int[] config = { 7, 7, 1 };
		Population p = new Population(100, config);

		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
		int minturn = -1, maxturn = -1;
		for (int i = 0; i < 500; i++) {
			try {
				p.RunEpoch();
				/*
				 * System.out.println("Maximum fitness in population: " +
				 * p.getBestFitness());
				 */
				/*
				 * System.out.print(p.getBestFitness()+"\t"); if(i%10==0&&i!=0)
				 * System.out.println();
				 */
				System.out.println("Epoch # " + i + ": Score = "
						+ p.getBestFitness());
				if (p.getBestFitness() < min) {
					min = p.getBestFitness();
					minturn = i;
				}
				if (p.getBestFitness() > max) {
					max = p.getBestFitness();
					maxturn = i;
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Max value attained = " + max + " @ epoch # "
				+ maxturn);
		System.out.println("Min value attained = " + min + " @ epoch # "
				+ minturn);
	}

}
