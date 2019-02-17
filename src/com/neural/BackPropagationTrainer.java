package com.neural;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class BackPropagationTrainer {

	protected ActivationNetwork network;
	public double LearningRate = 0.1;
	public double Momentum = 0.5;

	public BackPropagationTrainer(ActivationNetwork net) {
		this.network = net;
	}

	public double RunEpoch(double[][] input, double[][] output) {
		double error = 0;

		for (int i = 0; i < input.length; i++) {
			error += Run(input[i], output[i]);
		}

		return error / input.length;
	}

	public double RunEpoch(ArrayList input, ArrayList output) {
		double error = 0;

		for (int i = 0; i < input.size(); i++) {
			double[] ivalue = (double[]) input.get(i);
			double[] ovalue = (double[]) output.get(i);
			error += Run(ivalue, ovalue);
		}

		return error / input.size();
	}

	public double Run(double[] input, double[] desiredOutput) {
		double error = 0;
		network.Compute(input);

		for (int j = 0; j < network.layers[network.layerCount - 1].neuronCount; j++) {
			error += Math.pow(desiredOutput[j] - network.output[j], 2) / 2;
			network.layers[network.layerCount - 1].neurons[j].dEa = -1
					* (desiredOutput[j] - network.output[j]);
		}

		for (int i = network.layerCount - 1; i >= 0; i--) {
			double[] layerinput = null;
			if (i == 0) {
				layerinput = input;
			} else {
				layerinput = network.layers[i - 1].outputs;
			}

			for (int j = 0; j < network.layers[i].neuronCount; j++) {
				for (int k = 0; k < network.layers[i].neurons[j].inputCount + 1; k++) {
					if (k == network.layers[i].neurons[j].inputCount) {
						network.layers[i].neurons[j].deltaW[k] = -1
								* LearningRate
								* network.layers[i].neurons[j]
										.getCorrectionValue() * -1;
					} else {
						network.layers[i].neurons[j].deltaW[k] = -1
								* LearningRate
								* network.layers[i].neurons[j]
										.getCorrectionValue() * layerinput[k];
					}

					if (k != network.layers[i].neurons[j].inputCount && i != 0) {
						network.layers[i - 1].neurons[k].dEa += network.layers[i].neurons[j]
								.getCorrectionValue()
								* network.layers[i].neurons[j].weights[k];
					}
				}
			}
		}

		CommitDelta();
		return error;
	}

	protected void CommitDelta() {
		for (int i = 0; i < network.layerCount; i++) {
			for (int j = 0; j < network.layers[i].neuronCount; j++) {
				for (int k = 0; k < network.layers[i].neurons[j].weights.length; k++) {
					network.layers[i].neurons[j].weights[k] += network.layers[i].neurons[j].deltaW[k]
							+ (Momentum * network.layers[i].neurons[j].prevDeltaW[k]);
					network.layers[i].neurons[j].prevDeltaW[k] = network.layers[i].neurons[j].deltaW[k];
					network.layers[i].neurons[j].dEa = 0;
				}
			}
		}
	}

	public void Load(String filename) {
		ArrayList inputs = new ArrayList();
		ArrayList outputs = new ArrayList();
		try {
			File file = new File(filename);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			if (doc.getDocumentElement().getNodeName()
					.equals("BackPropagation")) {

				LearningRate = Double.parseDouble(doc.getDocumentElement()
						.getAttribute("LearningRate"));

				Momentum = Double.parseDouble(doc.getDocumentElement()
						.getAttribute("Momentum"));

				NodeList layerlst = doc.getElementsByTagName("Pattern");

				for (int s = 0; s < layerlst.getLength(); s++) {

					Node input = layerlst.item(s);

					if (input.getNodeType() == Node.ELEMENT_NODE) {
						Element fstElem = (Element) input;
						NodeList inputNdlst = fstElem
								.getElementsByTagName("Input");
						double[] inp = new double[7];
						for (int i = 0; i < inputNdlst.getLength(); i++) {
							Node ivalue = inputNdlst.item(i);
							inp[i] = Double
									.parseDouble(ivalue.getTextContent());
							// System.out.print(inp[i] + " ");
						}
						double[] oup = new double[1];
						oup[0] = Double.parseDouble(fstElem
								.getElementsByTagName("Output").item(0)
								.getTextContent());
						// System.out.println("; Output = "+ oup[0]);
						inputs.add(inp);
						outputs.add(oup);
					}
				}
			}

			int iterations = 0, iterationLimit = 10000;
			double error = 50, errorLimit = 0.001;
			while (iterations < iterationLimit && error > errorLimit) {
				error = this.RunEpoch(inputs, outputs);
				iterations++;
			}
			System.out.println("Network trained to " + error + " Error.");
			this.network.Save("OptimizedNet.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
