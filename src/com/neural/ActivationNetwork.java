package com.neural;

import com.genetic.Chromosome;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class ActivationNetwork {

	protected int inputCount = 0;
	protected int layerCount = 0;
	protected ActivationLayer[] layers = null;
	protected double[] output;
	public double error = 0;

	public ActivationNetwork(int inputs, int[] layerData) {
		init(inputs, layerData);
	}

	private void init(int inputs, int[] layerData) {
		inputCount = inputs;
		layerCount = layerData.length;
		layers = new ActivationLayer[layerCount];

		for (int i = 0; i < layerCount; i++) {
			if (i == 0) {
				layers[i] = new ActivationLayer(inputs, layerData[i]);
			} else {
				layers[i] = new ActivationLayer(layerData[i - 1], layerData[i]);
			}
		}
		int outputLayerCount = layerData[layerData.length - 1];
		output = new double[outputLayerCount];
	}

	public double[] Compute(double[] input) {
		double[] tempoutput = input;

		for (int i = 0; i < layerCount; i++) {
			tempoutput = layers[i].Compute(tempoutput);
		}

		this.output = tempoutput;

		return this.output;
	}

	public void Debug() {
		for (int i = 0; i < layers.length; i++) {
			System.out.println("LAYER # " + Integer.toString(i + 1));
			String[] st = layers[i].Debug();
			for (String s : st) {
				System.out.println(s);
			}
		}
	}

	public ActivationLayer getLayer(int index) {
		return layers[index];
	}

	private String getNetworkConfig() {
		String o = "";

		for (int i = 0; i < layerCount; i++) {
			o += this.layers[i].neuronCount + ",";
		}

		o = o.substring(0, o.length() - 1);
		return o;
	}

	public void Save(String filename) {
		try {
			OutputStream fout = new FileOutputStream(filename);
			OutputStream bout = new BufferedOutputStream(fout);
			OutputStreamWriter out = new OutputStreamWriter(bout, "8859_1");
			out.write("<?xml version=\"1.0\" ");
			out.write("encoding=\"ISO-8859-1\"?>\r\n");
			out.write("<ActivationNetwork inputs=\"" + this.inputCount
					+ "\" layers=\"" + getNetworkConfig() + "\">\r\n");
			for (int i = 0; i < layers.length; i++) {
				layers[i].Save(out);
			}
			out.write("</ActivationNetwork>\r\n");

			out.flush(); // Don't forget to flush!
			out.close();
		} catch (UnsupportedEncodingException e) {
			System.out
					.println("This VM does not support the Latin-1 character set.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void Load(String filename) {
		try {
			File file = new File(filename);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			if (doc.getDocumentElement().getNodeName().equals(
					"ActivationNetwork")) {

				int inp = Integer.parseInt(doc.getDocumentElement()
						.getAttribute("inputs"));

				String[] laystr = doc.getDocumentElement().getAttribute(
						"layers").split(",");

				int[] lay = new int[laystr.length];

				for (int i = 0; i < laystr.length; i++) {
					lay[i] = Integer.parseInt(laystr[i]);
				}

				init(inp, lay);

				NodeList layerlst = doc.getElementsByTagName("ActivationLayer");

				for (int s = 0; s < layerlst.getLength(); s++) {

					Node activela = layerlst.item(s);

					if (activela.getNodeType() == Node.ELEMENT_NODE) {
						Element fstElem = (Element) activela;
						layers[s].Load(fstElem);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Load(Chromosome c) {
		int index = 0;
		for(int i=0;i<layers.length;i++)
			for(int j=0;j<layers[i].neurons.length;j++)
				for(int w=0;w<layers[i].neurons[j].weights.length;w++){
					layers[i].neurons[j].weights[w] = c.getChromosomeElement(index);
					index++;
				}
	}
}
