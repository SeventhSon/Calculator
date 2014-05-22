package application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import org.apache.commons.math3.analysis.function.Acosh;
import org.apache.commons.math3.analysis.function.Asinh;
import org.apache.commons.math3.analysis.function.Atanh;
import org.apache.commons.math3.util.MathUtils;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.CustomFunction;
import de.congrace.exp4j.CustomOperator;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.InvalidCustomFunctionException;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class Calculator {
	private ObservableList<Function> mFunctions;
	private Collection<CustomFunction> customs;

	public Calculator() {
		mFunctions = FXCollections.observableArrayList();
		customs = new ArrayList<CustomFunction>();
		CustomOperator mod = new CustomOperator("%", true, 3) {
			@Override
			protected double applyOperation(double[] values) {
				if (values[1] == 0d) {
					throw new ArithmeticException("Division by zero!");
				}
				return values[0] % values[1];
			}
		};
		CustomOperator factorial = new CustomOperator("!", true, 6, 1) {
			@Override
			protected double applyOperation(double[] values) {
				double tmp = 1d;
				int steps = 1;
				while (steps < values[0]) {
					tmp = tmp * (++steps);
				}
				return tmp;
			}
		};
		try {
			CustomFunction max = new CustomFunction("max", 2) {
				@Override
				public double applyFunction(double[] values) {
					double max = values[0];
					for (int i = 1; i < this.getArgumentCount(); i++) {
						if (values[i] > max) {
							max = values[i];
						}
					}
					return max;
				}
			};
			CustomFunction min = new CustomFunction("min", 2) {
				@Override
				public double applyFunction(double[] values) {
					double min = values[0];
					for (int i = 1; i < this.getArgumentCount(); i++) {
						if (values[i] < min) {
							min = values[i];
						}
					}
					return min;
				}
			};
			CustomFunction round = new CustomFunction("round") {
				public double applyFunction(double[] values) {
					return Math.round(values[0]);
				}
			};
			CustomFunction truncate = new CustomFunction("trunc", 2) {
				public double applyFunction(double[] values) {
					return Math.floor(values[0]);
				}
			};
			CustomFunction acosh = new CustomFunction("acosh") {
				public double applyFunction(double[] values) {
					return new Acosh().value(values[0]);
				}
			};
			CustomFunction asinh = new CustomFunction("asinh") {
				public double applyFunction(double[] values) {
					return new Asinh().value(values[0]);
				}
			};
			CustomFunction atanh = new CustomFunction("atanh") {
				public double applyFunction(double[] values) {
					return new Atanh().value(values[0]);
				}
			};
			customs.add(atanh);
			customs.add(asinh);
			customs.add(acosh);
			customs.add(truncate);
			customs.add(round);
			customs.add(min);
			customs.add(max);
		} catch (InvalidCustomFunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double[][] Calculate(String formula, double xStart, double xEnd,
			int granulity) throws UnknownFunctionException, UnparsableExpressionException {
		Calculable expression;
		formula = formula.replaceAll("PI", "3.14159");
		formula = formula.replaceAll("E", "2.718281");
		expression = new ExpressionBuilder(formula).withVariableNames("x")
				.withCustomFunctions(customs).build();
		double step = (xEnd - xStart) / granulity;

		double[][] function = new double[granulity][2];
		for (int i = 0; i < granulity; i++) {
			function[i][0] = xStart;
			function[i][1] = expression.calculate(xStart);
			xStart += step;
			System.out.print(function[i][0] + " " + function[i][1] + "\n");
		}
		System.out.println();
		return function;
	}

	public void addAndCalculate(String formula, double xStart, double xEnd,
			int granulity, String type, Color color, int size)
			throws UnknownFunctionException, UnparsableExpressionException {
		Calculable expression;
		formula = formula.replaceAll("PI", "3.14159");
		formula = formula.replaceAll("E", "2.718281");
		expression = new ExpressionBuilder(formula).withVariableNames("x")
				.withCustomFunctions(customs).build();
		double step = (xEnd - xStart) / granulity;

		double[][] function = new double[granulity][2];
		for (int i = 0; i < granulity; i++) {
			function[i][0] = xStart;
			function[i][1] = expression.calculate(xStart);
			xStart += step;
			System.out.print(function[i][0] + " " + function[i][1] + "\n");
		}
		System.out.println();
		mFunctions.add(new Function(formula, function, size, color, type));
	}

	public ObservableList<Function> getFunctions() {
		return mFunctions;

	}

	public void clear() {
		mFunctions.clear();
	}
}