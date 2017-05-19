package kuleuven_groept_ee5;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class Gui {

	private Main main = null;
	private JFrame frame;
	private JTextField input;
	private JTextArea output;
	private JComboBox<String> portSelect;
	private JComboBox<Mode> modeSelect;
	private JComboBox<Range> rangeSelect;
	private JComboBox<SampleFrequency> sampleFrequency;
	private Oscilloscope chart;
	private FrequencyChart freq;
	

	public Gui(Main main) {
		this.main = main;
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(0, 0, 660, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblSelect = new JLabel("Select a serial port:");
		lblSelect.setBounds(10, 10, 200, 20);
		frame.getContentPane().add(lblSelect);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				main.getCommunicator().searchForPorts();
				
			}
		});
		btnSearch.setBounds(20, 40, 100, 20);
		frame.getContentPane().add(btnSearch);

		portSelect = new JComboBox<String>();
		portSelect.setBounds(130, 40, 100, 20);
		frame.getContentPane().add(portSelect);

		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				main.getCommunicator().connect();
			}
		});
		btnConnect.setBounds(240, 40, 100, 20);
		frame.getContentPane().add(btnConnect);

		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				main.getCommunicator().disconnect();

			}
		});
		btnDisconnect.setBounds(350, 40, 100, 20);
		frame.getContentPane().add(btnDisconnect);

		JLabel lblSelectMode = new JLabel("Select operation mode:");
		lblSelectMode.setBounds(10, 80, 200, 20);
		frame.getContentPane().add(lblSelectMode);

		modeSelect = new JComboBox<Mode>(Mode.values());
		modeSelect.setBounds(20, 110, 100, 20);
		frame.getContentPane().add(modeSelect);

		rangeSelect = new JComboBox<Range>(Range.values());
		rangeSelect.setBounds(140, 110, 100, 20);
		frame.getContentPane().add(rangeSelect);
		
		sampleFrequency = new JComboBox<>(SampleFrequency.values());
		sampleFrequency.setSelectedItem(SampleFrequency.ONE_K);
		sampleFrequency.setBounds(260,110,120,20);
		frame.getContentPane().add(sampleFrequency);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateMode();
				main.getCommunicator().write("H");	
			}
		});
		btnSave.setBounds(400, 110, 100, 20);
		frame.getContentPane().add(btnSave);

		JLabel lblInput = new JLabel("Input:");
		lblInput.setBounds(10, 150, 100, 20);
		frame.getContentPane().add(lblInput);

		input = new JTextField();
		input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				main.getCommunicator().write(input.getText());
			}
		});
		input.setBounds(20, 180, 100, 20);
		input.setColumns(10);
		frame.getContentPane().add(input);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				main.getCommunicator().write(input.getText());
			}
		});
		btnSend.setBounds(130, 180, 100, 20);
		frame.getContentPane().add(btnSend);

		JLabel lblOutput = new JLabel("Output: ");
		lblOutput.setBounds(10, 210, 100, 20);
		frame.getContentPane().add(lblOutput);

		output = new JTextArea();
		output.setEditable(false);
		output.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(output);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVisible(true);
		scrollPane.setBounds(20, 240, 400, 150);
		frame.getContentPane().add(scrollPane);
		
		JButton btnChart = new JButton("Oscilloscope");
		btnChart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try {
					chart = new Oscilloscope(main);
					chart.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnChart.setVisible(true);
		btnChart.setBounds(450, 250, 120, 20);
		frame.getContentPane().add(btnChart);
		
		JButton btnFrequency = new JButton("Frequency chart");
		btnFrequency.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				FrequencyChart freq = new FrequencyChart(main);
				main.setFreqChart(freq);
				freq.getFrame().setVisible(true);
			}
		});
		btnFrequency.setVisible(true);
		btnFrequency.setBounds(450, 280, 120, 20);
		frame.getContentPane().add(btnFrequency);
		
		
	}
	


	public void updateMode(){
		Range range = (Range) getSelectedRange().getSelectedItem();
		Mode mode = (Mode) getSelectedMode().getSelectedItem();
		SampleFrequency sampleFr = (SampleFrequency) getSampleFrequency().getSelectedItem();

		output.append("Selected mode: " + range.toString() + "  " + mode.toString() + " " + sampleFr.toString() + "\n");
		main.getUi().getOutputArea().setCaretPosition(main.getUi().getOutputArea().getDocument().getLength());
		main.getCommunicator().setMode(mode);
		main.getCommunicator().setRange(range);
		main.getCommunicator().setSampleFr(sampleFr);
		main.getCalc().setRange(range);
		main.getCalc().setMode(mode);
		main.getFreqCalc().setSampleFreq(sampleFr);
	}

	public void setMain(Main main) {
		this.main = main;
	}
	
	public JTextArea getOutputArea() {
		return output;
	}

	public JComboBox<String> getSelectedPort() {
		return portSelect;
	}

	public JComboBox<Mode> getSelectedMode() {
		return modeSelect;
	}

	public JComboBox<Range> getSelectedRange() {
		return rangeSelect;
	}
	
	public JComboBox<SampleFrequency> getSampleFrequency(){
		return sampleFrequency;
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	public Oscilloscope getOscilloscoop(){
		return chart;
	}
	
}
