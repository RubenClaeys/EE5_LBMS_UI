 package kuleuven_groept_ee5;

public class Main {
	
	private static SerialCommunicator communicator = null;
	private static Gui ui = null;
	private static Calculator calc = null;

	public static void main(String[] args) {
		ui = new Gui();
		ui.getFrame().setVisible(true);
		
		communicator = new SerialCommunicator();
		calc = new Calculator();

	}
	
	public static SerialCommunicator getCommunicator(){
		return communicator;
	}

	public static Gui getUi() {
		return ui;
	}
	
	public static Calculator getCalc(){
		return calc;
	}

}
