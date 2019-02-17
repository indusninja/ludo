package com.neural;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class ActivationLayer {

	protected int inputCount = 0;
	protected int neuronCount = 0;

	protected double[] outputs;

	protected ActivationNeuron[] neurons = null;

	public ActivationLayer(int inputNum, int neuronNum) {
		// Add one more input for BIAS (input for bias is always -1)
		inputCount = inputNum;
		neuronCount = neuronNum;
		neurons = new ActivationNeuron[neuronNum];
		for (int i = 0; i < neuronNum; i++) {
			neurons[i] = new ActivationNeuron(inputNum);
		}
	}

	public double[] Compute(double[] inputs) {

		this.outputs = new double[neuronCount];

		for (int i = 0; i < neurons.length; i++) {
			this.outputs[i] = neurons[i].Compute(inputs);
		}

		return outputs;
	}

	public void Randomize() {
		for (int i = 0; i < neuronCount; i++) {
			neurons[i].Randomize();
		}
	}

	public String[] Debug() {
		String[] debugOut = new String[neuronCount];

		for (int i = 0; i < neurons.length; i++) {
			debugOut[i] = "Neuron # " + Integer.toString(i + 1)
					+ "; Weights : ";
			for (int j = 0; j <= neurons[i].getInputCount(); j++) {
				debugOut[i] += Double.toString(neurons[i].getWeight(j)) + ", ";
			}
			debugOut[i] += "Delta Weights : ";
			for (int j = 0; j <= neurons[i].getInputCount(); j++) {
				debugOut[i] += Double.toString(neurons[i].deltaW[j]) + ", ";
			}
		}

		return debugOut;
	}

	public ActivationNeuron getNeuron(int index) {
		return neurons[index];
	}

	public void Save(OutputStreamWriter out) {
		try {
			out.write("\t<ActivationLayer neurons=\"" + this.neuronCount
					+ "\">\r\n");
			for (int i = 0; i < this.neuronCount; i++) {
				neurons[i].Save(out);
			}
			out.write("\t</ActivationLayer>\r\n");

			out.flush();
		} catch (UnsupportedEncodingException e) {
			System.out
					.println("This VM does not support the Latin-1 character set.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void Load(Element la) {
		NodeList n = la.getElementsByTagName("ActivationNeuron");
		for(int i=0;i<n.getLength();i++){
			Node activene = n.item(i);
			
			if(activene.getNodeType()==Node.ELEMENT_NODE){
				Element fstElem = (Element)activene;
				neurons[i].Load(fstElem);
			}
		}
	}
}
