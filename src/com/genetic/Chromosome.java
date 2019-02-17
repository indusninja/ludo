package com.genetic;

import java.util.Random;

public class Chromosome implements Comparable {

	public static class RandomGenerator {

		public static Random generator = new Random((int) System
				.currentTimeMillis());
	};

	private int fitness = 0;
	protected double[] mData;
	// protected int mLifespan;
	protected int mSize, nCount;

	public float MutateProbability = 0.5f;

	private int[] networkConfig;

	public float UniformSeperationProbability = 0.75f;

	public Chromosome(int[] config) {
		networkConfig = config;

		mSize = 0;
		nCount = 0;

		for (int i = 1; i < config.length; i++) {
			mSize += (config[i - 1] + 1) * (config[i]);
			nCount += config[i];
		}
		mData = new double[mSize];
		/*
		 * mLifespan = 0; while (this.getFitness() == 1) Randomize();
		 */
		// System.out.println("Chromosome being initialized: mSize= " + mSize +
		// "; nCount = "+ nCount);
		Randomize();
	}

	public int compareTo(Object o) {
		Chromosome g = (Chromosome) o;

		int fitnessDifference = g.getFitness() - this.getFitness();

		return fitnessDifference;
	}

	public String getChromosome() {
		String chromo = "";
		for (double d : mData) {
			chromo += Double.toString(d) + "|";
		}
		return chromo.substring(0, chromo.length() - 1);
	}

	public double getChromosomeElement(int index) {
		return mData[index];
	}

	public int getFitness() {
		return fitness;
	}

	public int[] getNetworkConfig() {
		int[] config = new int[networkConfig.length - 1];
		for (int i = 0; i < config.length; i++) {
			config[i] = networkConfig[i + 1];
		}
		return config;
	}

	public int getNetworkInputCount() {
		return networkConfig[0];
	}

	public double[] getNeuronWeight(int index) {
		double[] weights;
		int layer = -1;
		for (int i = 1; i < networkConfig.length; i++) {
			if (index < networkConfig[i]) {
				layer = i;
				break;
			} else
				index -= networkConfig[i];
		}

		// System.out.println("Found in layer # " + layer);
		if (layer == -1)
			return null;

		int startPos = 0, length = 0;
		for (int i = 1; i < layer; i++) {
			startPos += (networkConfig[i - 1] + 1) * (networkConfig[i]);
		}

		startPos += (networkConfig[layer - 1] + 1) * index;
		length = (networkConfig[layer - 1] + 1);

		weights = new double[length];
		// System.arraycopy(array,3,subArray,0,4);
		System.arraycopy(mData, startPos, weights, 0, length);
		return weights;
	}

	public void Mutate() {
		for (int i = 0; i < nCount; i++) {
			if (RandomGenerator.generator.nextFloat() <= MutateProbability) {
				MutateNeuron(i);
			}
		}
		/*
		 * for (int i = 0; i < mData.length; i++) { if
		 * (RandomGenerator.generator.nextFloat() <= MutateProbability) {
		 * mData[i] = 1 - mData[i]; } }
		 */
	}

	private void MutateNeuron(int index) {

		int layer = -1;
		for (int i = 1; i < networkConfig.length; i++) {
			if (index < networkConfig[i]) {
				layer = i;
				break;
			} else
				index -= networkConfig[i];
		}

		if (layer == -1)
			return;

		int startPos = 0, length = 0;
		for (int i = 1; i < layer; i++) {
			startPos += (networkConfig[i - 1] + 1) * (networkConfig[i]);
		}

		startPos += (networkConfig[layer - 1] + 1) * index;
		length = (networkConfig[layer - 1] + 1);

		for (int i = startPos; i < (startPos + length); i++) {
			if (RandomGenerator.generator.nextFloat() <= MutateProbability) {
				mData[i] = RandomGenerator.generator.nextGaussian();
			}
		}
	}

	private void Randomize() {
		for (int i = 0; i < this.mData.length; i++) {
			// this.mData[i] = (RandomGenerator.generator.nextBoolean()) ? 1 :
			// 0;
			this.mData[i] = RandomGenerator.generator.nextDouble();
		}
	}

	public Chromosome[] Reproduce(Chromosome gene)
			throws CloneNotSupportedException {
		return UniformCrossover(gene);
	}

	public void setChromosomeElement(int index, double value) {
		mData[index] = value;
	}

	public void setFitness(int value) {
		fitness = value;
	}

	public void setNeuronWeight(int index, double[] weights) {
		// double[] weights;
		int layer = -1;
		for (int i = 1; i < networkConfig.length; i++) {
			if (index < networkConfig[i]) {
				layer = i;
				break;
			} else
				index -= networkConfig[i];
		}

		if (layer == -1)
			return;

		int startPos = 0, length = 0;
		for (int i = 1; i < layer; i++) {
			startPos += (networkConfig[i - 1] + 1) * (networkConfig[i]);
		}

		startPos += (networkConfig[layer - 1] + 1) * index;
		length = (networkConfig[layer - 1] + 1);

		for (int i = startPos; i < (startPos + length); i++) {
			mData[i] = weights[i - startPos];
		}
	}

	private Chromosome[] UniformCrossover(Chromosome parent2)
			throws CloneNotSupportedException {
		Chromosome[] c = new Chromosome[2];
		c[0] = new Chromosome(this.networkConfig);
		c[1] = new Chromosome(parent2.networkConfig);

		for (int i = 0; i < this.nCount; i++) {
			if (RandomGenerator.generator.nextFloat() <= UniformSeperationProbability) {
				c[0].setNeuronWeight(i, this.getNeuronWeight(i));
				c[1].setNeuronWeight(i, parent2.getNeuronWeight(i));
				// c[0].mData[i] = this.mData[i];
				// c[1].mData[i] = parent2.mData[i];
			} else {
				c[0].setNeuronWeight(i, parent2.getNeuronWeight(i));
				c[1].setNeuronWeight(i, this.getNeuronWeight(i));
				// c[0].mData[i] = parent2.mData[i];
				// c[1].mData[i] = this.mData[i];
			}
		}

		return c;
	}
}
