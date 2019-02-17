package com.neural;

public abstract class Neuron {

	protected int inputCount = 0;
	protected double output = 0;
	public double dEa = 0;
	protected double[] range = { 0, 1 };
	protected double[] weights = null;
	protected double[] deltaW = null;
	protected double[] prevDeltaW = null;

	public Neuron(int inputs) {
		if (inputs <= 0) {
			inputs = 1;
		}
		inputCount = inputs;
		weights = new double[inputCount + 1];
		deltaW = new double[inputCount + 1];
		prevDeltaW = new double[inputCount + 1];

		this.Randomize();
	}

	public abstract double Compute(double[] inputs);

	public int getInputCount() {
		return inputCount;
	}

	public double getOutput() {
		return output;
	}

	public double getRangeMax() {
		return range[1];
	}

	public double getRangeMin() {
		return range[0];
	}

	public double getWeight(int index) {
		return weights[index];
	}

	public double getCorrectionValue() {
		return output * (1 - output) * dEa;
	}

	public void Randomize() {
		double rangeLength = range[1] - range[0];
		double rangeMin = range[0];
		dEa = 0;

		for (int i = 0; i <= inputCount; i++) {
			weights[i] = RandomGenerator.generator.nextDouble() * rangeLength
					- rangeMin;
			deltaW[i] = 0;
			prevDeltaW[i] = 0;
		}
	}

	public void setInputCount(int count) {
		this.inputCount = count;
	}

	public void setRangeMax(double max) {
		this.range[1] = max;
	}

	public void setRangeMin(double min) {
		this.range[0] = min;
	}

	public void setWeight(int index, double value) {
		this.weights[index] = value;
	}
}
