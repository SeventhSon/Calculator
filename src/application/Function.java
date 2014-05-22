package application;

import javafx.scene.paint.Color;

public class Function {

	double[][] data;
	String formula;
	int size;
	String type;
	Color color;

	@Override
	public String toString() {

		return this.formula;
	}

	public Function(String formula, double[][] data, int size, Color color,
			String type) {
		this.formula = formula;
		this.data = data;
		this.size = size;
		this.color = color;
		this.type = type;
	}
}
