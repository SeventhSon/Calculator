package application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import org.apache.commons.math3.analysis.function.Acosh;
import org.apache.commons.math3.analysis.function.Asinh;
import org.apache.commons.math3.analysis.function.Atanh;

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
	private Collection<CustomOperator> customops;

	public Calculator() {
		mFunctions = FXCollections.observableArrayList();
		customs = new ArrayList<CustomFunction>();
		customops = new ArrayList<CustomOperator>();

		CustomOperator equal = new CustomOperator("==", true, 1, 2) {
			public double applyOperation(double[] values) {
				if (values[0] == values[1]) {
					return 1d;
				} else {
					return 0d;
				}
			}
		};

		CustomOperator notEqual = new CustomOperator("!=", true, 1, 2) {
			public double applyOperation(double[] values) {
				if (values[0] != values[1]) {
					return 1d;
				} else {
					return 0d;
				}
			}
		};

		CustomOperator greaterEqual = new CustomOperator(">=", true, 1, 2) {
			public double applyOperation(double[] values) {
				if (values[0] >= values[1]) {
					return 1d;
				} else {
					return 0d;
				}
			}
		};

		CustomOperator lessEqual = new CustomOperator("<=", true, 1, 2) {
			public double applyOperation(double[] values) {
				if (values[0] <= values[1]) {
					return 1d;
				} else {
					return 0d;
				}
			}
		};

		CustomOperator greaterThen = new CustomOperator(">", true, 1, 2) {
			public double applyOperation(double[] values) {
				if (values[0] > values[1]) {
					return 1d;
				} else {
					return 0d;
				}
			}
		};

		CustomOperator lessThen = new CustomOperator("<", true, 1, 2) {
			public double applyOperation(double[] values) {
				if (values[0] < values[1]) {
					return 1d;
				} else {
					return 0d;
				}
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
			customops.add(lessThen);
			customops.add(greaterThen);
			customops.add(lessEqual);
			customops.add(greaterEqual);
			customops.add(equal);
			customops.add(notEqual);
		} catch (InvalidCustomFunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double[][] Calculate(String formula, double xStart, double xEnd,
			int granulity) throws UnknownFunctionException,
			UnparsableExpressionException {
		Calculable expression, ifexp = null;
		boolean ifop=false;
		formula = formula.replaceAll("PI", "3.14159");
		formula = formula.replaceAll("E", "2.718281");
		Pattern p1 = Pattern.compile("^(.+?)\\?(.+):(.+)$");
		Matcher m = p1.matcher(formula);

		double[][] function = new double[granulity][2];
		double step = (xEnd - xStart) / granulity;
		
		if (m.find()) {
			System.out.println("If "+m.group(1)+" then "+m.group(2)+" else "+m.group(3));
			ifexp = new ExpressionBuilder(m.group(1))
					.withVariableNames("x").withCustomFunctions(customs).withOperations(customops)
					.build();
			ifop = true;
		}
		

		for (int i = 0; i < granulity; i++) {
			if(ifop){
				if (ifexp.calculate(xStart) == 1) {
					expression = new ExpressionBuilder(m.group(2))
							.withVariableNames("x")
							.withCustomFunctions(customs).build();
				} else {
					expression = new ExpressionBuilder(m.group(3))
							.withVariableNames("x")
							.withCustomFunctions(customs).build();
				}

			} else {
				expression = new ExpressionBuilder(formula)
						.withVariableNames("x").withCustomFunctions(customs)
						.build();
			}
			function[i][0] = xStart;
			function[i][1] = expression.calculate(xStart);
			System.out.print(function[i][0] + " " + function[i][1] + "\n");
			xStart += step;
		}
		System.out.println();
		return function;
	}

	public void addAndCalculate(String formula, double xStart, double xEnd,
			int granulity, String type, Color color, int size)
			throws UnknownFunctionException, UnparsableExpressionException {
		mFunctions.add(new Function(formula, Calculate(formula, xStart, xEnd,
				granulity), size, color, type));
	}

	public ObservableList<Function> getFunctions() {
		return mFunctions;

	}

	public void clear() {
		mFunctions.clear();
	}
}