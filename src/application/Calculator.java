package application;

import java.util.ArrayList;
import java.util.List;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class Calculator {
	private List<Function> mFunctions;
	
	public Calculator(){
		mFunctions = new ArrayList<Function>();
	}

	public void addAndCalculate(String formula, double xStart, double xEnd,
			int granulity) {
		Calculable expression;
		try {
			expression = new ExpressionBuilder(formula).withVariableNames("x")
					.build();
			double step = (xEnd - xStart) / granulity;

			double[][] function = new double[granulity][2];
			for (int i = 0; i < granulity; i++) {
				function[i][0] = xStart;
				function[i][1] = expression.calculate(xStart);
				xStart += step;
				System.out.print(function[i][0] + " " + function[i][1] + "\n");
			}
			System.out.println();
			mFunctions.add(new Function(formula, function));
		} catch (UnknownFunctionException | UnparsableExpressionException e) {
			System.err.println("Bad expression!");
			e.printStackTrace();
		}
	}

	public List<Function> getFunctions() {
		return mFunctions;

	}

	public void clear() {
		mFunctions.clear();
	}
}