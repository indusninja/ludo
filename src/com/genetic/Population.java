package com.genetic;

import com.ludo.LUDOSimulator.LUDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Population {
	List<Chromosome> mGenes = new ArrayList<Chromosome>();
	int mSize = 250;
	float mutateRatio = 0.25f;
	// int mChromeSize = 10;
	int[] networkChromeConfig;

	public Population(int size, int[] chromosomeSize) {
		mSize = size;
		networkChromeConfig = chromosomeSize;
		ResetPopulation();
	}

	public Chromosome getGene(int index) {
		return mGenes.get(index);
	}

	public void Mutate(int g) {
		mGenes.get(g).Mutate();
	}

	public void Reproduce(int gp1, int gp2) throws CloneNotSupportedException {
		Chromosome[] children = null;

		children = mGenes.get(gp1).Reproduce(mGenes.get(gp2));

		mGenes.add(children[0]);
		mGenes.add(children[1]);
	}

	public void ResetPopulation() {
		// System.out.println("Creating Population");
		mGenes.clear();
		for (int i = 0; i < mSize; i++) {
			mGenes.add(new Chromosome(networkChromeConfig));
		}
		SortPopulation();
	}

	public float RunEpoch() throws CloneNotSupportedException {

		// Mutate lower % of population
		for (int i = (mSize - (int) (mSize * mutateRatio)); i < mSize; i++) {
			Mutate(i);
		}

		// Select Parents from top
		int parent1 = -1, parent2 = -1;
		if (mGenes.size() >= 2) {
			parent1 = 0;
			parent2 = 1;
		}

		// Reproduce
		if (parent1 != -1 && parent2 != -1) {
			Reproduce(parent1, parent2);
		}

		// Evaluate population
		SortPopulation();

		// Discard extra and lowest populace
		while (mGenes.size() > mSize) {
			mGenes.remove(mGenes.size() - 1);
		}

		float populationFitness = 0;
		for (int i = 0; i < mGenes.size(); i++) {
			populationFitness += mGenes.get(i).getFitness();
		}

		/*
		 * System.out.println("Average Population Fitness = " +
		 * Float.toString(populationFitness / mGenes.size())); this.Debug();
		 */
		return populationFitness / mGenes.size();
	}

	public void Save(String filename) {
		// Write code here to save the chromosome with best fitness as a neural
		// network xml file that can be loaded
	}

	private void SortPopulation() {
		// Play and set fitness value before sorting, otherwise it will go
		// nowhere
		// System.out.println("Finding Population Fitness");
		for (int i = 0; i < mGenes.size(); i++) {
			LUDO l = new LUDO();
			mGenes.get(i).setFitness(l.playGenetic(mGenes.get(i), 100));
			// System.out.print(".");
		}
		// System.out.println("Sorting Population");
		Collections.sort(mGenes);

		/*for (int i = 0; i < mGenes.size(); i++)
			System.out.print(mGenes.get(i).getFitness() + ",");
		System.out.println();*/
	}

	public int getBestFitness() {
		return mGenes.get(0).getFitness();
	}
}
