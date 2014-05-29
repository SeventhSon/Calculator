package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class MainController implements Initializable {
	Calculator calculator;

	private double xStart = -10, xEnd = 10, yStart = -10, yEnd = 10;
	private int granulity = 1000;

	@FXML
	LineChart<Number, Number> mChart;

	@FXML
	TextField mFormulaTextBox;

	@FXML
	Parent mRoot;

	@FXML
	ListView<Function> mList;

	@FXML
	ColorPicker mColorPicker;

	@FXML
	ChoiceBox<String> mTypeBox;

	@FXML
	TextField mSizeBox;

	@FXML
	TextField dStart;

	@FXML
	TextField cStart;

	@FXML
	TextField dEnd;

	@FXML
	TextField cEnd;

	ObservableList<String> type;

	Function dummy;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		calculator = new Calculator();
		dummy = new Function("", new double[][] { { 0 } }, 1, new Color(0, 0,
				0, 0), mTypeBox.getValue());
		type = FXCollections.observableArrayList("Solid", "Dotted", "Dashed");
		mTypeBox.setItems(type);
		mTypeBox.setValue(type.get(0));
		mList.setItems(calculator.getFunctions());
		mList.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Function>() {
					public void changed(ObservableValue<? extends Function> ov,
							Function old_val, Function new_val) {
						Function temp = (Function) mList.getSelectionModel()
								.getSelectedItem();
						mList.getItems().remove(dummy);
						if (temp != null) {
							mFormulaTextBox.setText(temp.formula);
							mSizeBox.setText("" + temp.size);
							mTypeBox.setValue(temp.type);
						}
					}
				});
	}

	@FXML
	private void edit(ActionEvent event) {
		Function temp = (Function) mList.getSelectionModel().getSelectedItem();
		temp.color = mColorPicker.getValue();
		temp.formula = mFormulaTextBox.getText();
		try {
			temp.size = Integer.parseInt(mSizeBox.getText());
		} catch (NumberFormatException e) {

		}
		temp.type = mTypeBox.getValue();
		try {
			temp.data = calculator.Calculate(temp.formula, xStart, xEnd,
					granulity);
			mFormulaTextBox.setStyle("-fx-border-color: green;");
		} catch (UnknownFunctionException | UnparsableExpressionException e) {
			mFormulaTextBox.setStyle("-fx-border-color: red;");
		}
		mList.getItems().add(dummy);
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
		((NumberAxis) (mChart.getXAxis())).setAutoRanging(false);
		((NumberAxis) (mChart.getXAxis())).setLowerBound(xStart);
	}

	@FXML
	private void setDomainE(ActionEvent event) {
		xEnd = Double.parseDouble(((TextField) event.getSource()).getText());
		((NumberAxis) (mChart.getXAxis())).setAutoRanging(false);
		((NumberAxis) (mChart.getXAxis())).setUpperBound(xEnd);
	}

	@FXML
	private void setCodomainS(ActionEvent event) {
		yStart = Double.parseDouble(((TextField) event.getSource()).getText());
		((NumberAxis) (mChart.getYAxis())).setAutoRanging(false);
		((NumberAxis) (mChart.getYAxis())).setLowerBound(yStart);
	}

	@FXML
	private void setCodomainE(ActionEvent event) {
		yEnd = Double.parseDouble(((TextField) event.getSource()).getText());
		((NumberAxis) (mChart.getYAxis())).setAutoRanging(false);
		((NumberAxis) (mChart.getYAxis())).setUpperBound(yEnd);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@FXML
	private void plot(ActionEvent event) {
		ObservableList<Function> functions = calculator.getFunctions();
		mChart.getData().clear();
		for (int j = 0; j < functions.size(); j++) {
			Function f = functions.get(j);
			if (f.formula.equals(""))
				continue;
			XYChart.Series<Number, Number> functionSeries = new XYChart.Series();
			functionSeries.setName(f.formula);
			for (int i = 0; i < f.data.length; i++) {
				if (!Double.isNaN(f.data[i][1])
						&& f.data[i][1] != Double.POSITIVE_INFINITY
						&& f.data[i][1] != Double.NEGATIVE_INFINITY) {
					System.out.print(f.data[i][0] + " " + f.data[i][1] + "\n");
					XYChart.Data data = new XYChart.Data(f.data[i][0],
							f.data[i][1]);
					functionSeries.getData().add(data);
				}
			}
			mChart.getData().add(functionSeries);
			f.series = functionSeries;
			String style = "";
			if (f.type.equals("Dotted")) {
				style += "-fx-stroke-dash-array: 0.1 9.0;";
			} else if (f.type.equals("Dashed")) {
				style += "-fx-stroke-dash-array: 8 8;-fx-stroke-dash-offset: 6;";
			}
			style += "-fx-stroke-width: " + f.size + "px;-fx-stroke: #"
					+ Integer.toHexString(f.color.hashCode()) + ";";
			((Node) f.series.nodeProperty().get()).setStyle(style);
		}
	}

	@FXML
	public void select(MouseEvent arg0) {
		if (mList.getSelectionModel().getSelectedItem() != null) {
			Function temp = (Function) mList.getSelectionModel()
					.getSelectedItem();
			mColorPicker.setValue(temp.color);
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
		int size;
		if (xStart < xEnd) {
			dEnd.setStyle("-fx-border-color: green;");
			dStart.setStyle("-fx-border-color: green;");
			try {
				size = Integer.parseInt(mSizeBox.getText());
			} catch (NumberFormatException e) {
				size = 1;
			}
			try {
				calculator.addAndCalculate(
						((TextField) event.getSource()).getText(), xStart,
						xEnd, granulity, (String) mTypeBox.getValue(),
						(Color) mColorPicker.getValue(), size);
				((TextField) event.getSource())
						.setStyle("-fx-border-color: green;");
			} catch (UnknownFunctionException | UnparsableExpressionException e) {
				((TextField) event.getSource())
						.setStyle("-fx-border-color: red;");
				e.printStackTrace();
			}
			mList.getSelectionModel().select(
					mList.getItems().get(mList.getItems().size() - 1));
		} else {
			System.err.println("Bad domain!");
			dEnd.setStyle("-fx-border-color: red;");
			dStart.setStyle("-fx-border-color: red;");
		}
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
