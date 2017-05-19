 package kuleuven_groept_ee5;

public class Main {
	
	private SerialCommunicator communicator = null;
	private Gui ui = null;
	private Calculator calc = null;
	private FrequencyCalculator freqCalc= null;
	private FrequencyChart freqChart = null;
	

	public Main(){
		ui = new Gui(this);
		ui.getFrame().setVisible(true);
		
		communicator = new SerialCommunicator(this);
		
		calc = new Calculator(this);
		
		freqCalc = new FrequencyCalculator(this);
		
	}
	
	public static void main(String[] args) {
		Main main = new Main();
	}



	public SerialCommunicator getCommunicator() {
		return communicator;
	}



	public void setCommunicator(SerialCommunicator communicator) {
		this.communicator = communicator;
	}



	public Gui getUi() {
		return ui;
	}



	public void setUi(Gui ui) {
		this.ui = ui;
	}



	public Calculator getCalc() {
		return calc;
	}



	public void setCalc(Calculator calc) {
		this.calc = calc;
	}



	public FrequencyCalculator getFreqCalc() {
		return freqCalc;
	}



	public void setFreqCalc(FrequencyCalculator freqCalc) {
		this.freqCalc = freqCalc;
	}
	
	public FrequencyChart getFreqChart() {
		return freqChart;
	}

	public void setFreqChart(FrequencyChart freqChart) {
		this.freqChart = freqChart;
	}

	

}
