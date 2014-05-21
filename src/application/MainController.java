package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class MainController implements Initializable {
	Calculator calculator;

	private double xStart = -10, xEnd = 10;
	private int granulity = 1000;

	@FXML
	LineChart<Number, Number> mChart;

	@FXML
	TextField mFormulaTextBox;

	@FXML
	Parent mRoot;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		calculator = new Calculator();
	}

	@FXML
	private void reset(ActionEvent event) {
		mFormulaTextBox.clear();
		mChart.getData().clear();
		calculator.clear();
	}

	@FXML
	private void setDomainS(ActionEvent event) {
		xStart = Double.parseDouble(((TextField) event.getSource()).getText());
	}

	@FXML
	private void setDomainE(ActionEvent event) {
		xEnd = Double.parseDouble(((TextField) event.getSource()).getText());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@FXML
	private void plot(ActionEvent event) {
		List<Function> functions = calculator.getFunctions();
		mChart.getData().clear();
		for (int j = 0; j < functions.size(); j++) {
			Function f = functions.get(j);
			XYChart.Series functionSeries = new XYChart.Series();
			functionSeries.setName(f.formula);
			for (int i = 0; i < f.data.length; i++) {
				if (!Double.isNaN(f.data[i][1]) && f.data[i][1] != Double.POSITIVE_INFINITY && f.data[i][1] != Double.NEGATIVE_INFINITY) {
					System.out.print(f.data[i][0] + " " + f.data[i][1] + "\n");
					XYChart.Data data = new XYChart.Data(f.data[i][0],
							f.data[i][1]);
					functionSeries.getData().add(data);
				}
			}
			mChart.getData().add(functionSeries);
		}
	}

	@FXML
	private void setTitle(ActionEvent event) {
		mChart.setTitle(((TextField) event.getSource()).getText());
	}

	@FXML
	private void setXLabel(ActionEvent event) {
		mChart.getXAxis().setLabel(((TextField) event.getSource()).getText());
	}

	@FXML
	private void setYLabel(ActionEvent event) {
		mChart.getYAxis().setLabel(((TextField) event.getSource()).getText());
	}

	@FXML
	private void addFunction(ActionEvent event) {
		if (xStart < xEnd)
			try {
				calculator.addAndCalculate(
						((TextField) event.getSource()).getText(), xStart, xEnd,
						granulity);
				((TextField) event.getSource()).setStyle("-fx-border-color: green;");
			} catch (UnknownFunctionException | UnparsableExpressionException e) {
				((TextField) event.getSource()).setStyle("-fx-border-color: red;");
				//e.printStackTrace();
			}
		else
			System.err.println("Bad domain!");
	}

	@FXML
	private void export(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		File file = fileChooser.showSaveDialog(mRoot.getScene().getWindow());
		if (file != null) {
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(
						mChart.snapshot(new SnapshotParameters(), null), null),
						"png", file);
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

}
