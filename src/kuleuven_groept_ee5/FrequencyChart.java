package kuleuven_groept_ee5;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;

public class FrequencyChart {
	
	private Main main = null;
	private JFrame frame;
	private JTextField nrOfSamples;
	private JButton btnCalc;
	
	public FrequencyChart(Main main){
		this.main = main;
		initialize();
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public JButton getBtnCalc(){
		return btnCalc;
	}

	public void initialize(){
		frame = new JFrame();
		frame.setBounds(0, 500, 650, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		
		nrOfSamples = new JTextField("#samples");
		nrOfSamples.setBounds(10, 10, 100, 20);
		nrOfSamples.setColumns(10);
		nrOfSamples.requestFocusInWindow();
		nrOfSamples.selectAll();
		nrOfSamples.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int samples = Integer.parseInt(nrOfSamples.getText());	
				main.getFreqCalc().startReading(samples);
			}
		});
		frame.getContentPane().add(nrOfSamples);
		
		JButton btnStart = new JButton("Start Reading samples");
		btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int samples = Integer.parseInt(nrOfSamples.getText());	
				main.getFreqCalc().startReading(samples);			
			}
		});
		btnStart.setBounds(120, 10, 200, 20);
		frame.getContentPane().add(btnStart);
		
		
		btnCalc = new JButton("Calculate");
		btnCalc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				main.getFreqCalc().calculate();
				
			//	createFrequencyChart();
				
				
				
			}
		});
		btnCalc.setEnabled(false);
		btnCalc.setBounds(10, 40, 100, 20);
		frame.getContentPane().add(btnCalc);
		
		
		
	}
	
//	public void createFrequencyChart(){
//		main.getFreqCalc().calculate();
//		
//		double[] samples = main.getFreqCalc().getSamples();
//		
//		double[] powerSpectrum = main.getFreqCalc().calculatePowerSpectrum(samples);
//		double[] frequencyArray = main.getFreqCalc().getFrequencyArray(samples, main.getFreqCalc().getFs());
//		
//		JPanel panel = new JPanel();
//		panel.setBackground(Color.WHITE);
//		panel.setBounds(65, 25, 560, 450);
//		frame.getContentPane().add(panel);
//		
//		JFreeChart lineChart = ChartFactory.createLineChart("Powerspectrum", "Frequency", "Amplitude", dataset);
//	}


}
