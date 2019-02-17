package com.neural;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class ActivationNeuron extends Neuron {

	public ActivationNeuron(int inputs) {
		super(inputs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double Compute(double[] inputs) {
		// TODO Auto-generated method stub
		double weightedSum = 0;

		for (int i = 0; i < inputs.length; i++) {
			weightedSum += inputs[i] * this.weights[i];
		}

		weightedSum -= weights[weights.length - 1];

		this.output = Activate(weightedSum);

		return this.output;
	}

	protected double Activate(double x) {
		return (1 / (1 + Math.exp(-1 * x)));
	}

	public void Save(OutputStreamWriter out) {
		try {
			out.write("\t\t<ActivationNeuron weights=\"" + (this.inputCount + 1)
					+ "\">\r\n");
			for (int i = 0; i < this.inputCount+1; i++) {

				out.write("\t\t\t<Weight>" + this.weights[i] + "</Weight>\r\n");
			}
			out.write("\t\t</ActivationNeuron>\r\n");

			out.flush();
		} catch (UnsupportedEncodingException e) {
			System.out
					.println("This VM does not support the Latin-1 character set.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void Load(Element fstElem) {
		NodeList w = fstElem.getElementsByTagName("Weight");
		for(int i=0;i<w.getLength();i++){
			Node activewe = w.item(i);
			
			this.weights[i] = Double.parseDouble(activewe.getTextContent());
		}
	}
}
