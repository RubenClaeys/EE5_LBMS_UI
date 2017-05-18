package kuleuven_groept_ee5;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import javax.swing.JFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JRadioButton;

public class Oscilloscope {
	private JFrame frmOscilloscope;
	private ChartPanel chartPanel;
	private JTextField valueZoomX;
	private JTextField valueMeas;
	private DefaultCategoryDataset dataset;
	private double zoomX;
	private int frequency;
	private int[] freqData;
	private String axis;
	private boolean enTrigger;
	private int trigger;
	private int triggerSize;
	private Measurements measure;
	
	
	public JFrame getFrame(){
		return frmOscilloscope;
	}

	/**
	 * Create the application.
	 */
	public Oscilloscope() {
		initialize();
	}
	
	public Measurements getMeasure(){
		return measure;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		zoomX=1;
		frequency=1;
		enTrigger=false;
		trigger=0;
		axis="s";
		measure= Measurements.DEFAULT;
		
		frmOscilloscope = new JFrame();
		frmOscilloscope.setTitle("Oscilloscope");
		frmOscilloscope.setBounds(770, 100, 800, 700);
		frmOscilloscope.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOscilloscope.getContentPane().setLayout(null);
		
		
		
		JButton btnStartReadingOf = new JButton("Start reading of the data");
		btnStartReadingOf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setBounds(65, 25, 560, 450);
				frmOscilloscope.getContentPane().add(panel);
				
				JFreeChart lineChart = ChartFactory.createLineChart(" ", "T ", "Volt", createDataset());	
				chartPanel = new ChartPanel( lineChart );
			    chartPanel.setPreferredSize( new java.awt.Dimension( 560 ,400) );
			    setContentPane( chartPanel ); 
			 panel.removeAll();
			 panel.add (chartPanel);
			 panel.validate();
			 
			 
			}
		});
		btnStartReadingOf.setBounds(315, 525, 159, 23);
		frmOscilloscope.getContentPane().add(btnStartReadingOf);
		
		JButton btnStart = new JButton(" data");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.getCalc().setRange(Range.EIGHT);
				double[] data={-5,-4,-3,-2,-1,0,1,2,3,4,5};
				Main.getCalc().toUserInterface(data);;
			}
		});
		btnStart.setBounds(315, 625, 159, 23);
		frmOscilloscope.getContentPane().add(btnStart);
		
		JTextField valueStop=new JTextField("Running");
		valueStop.setEditable(false);;
		valueStop.setBounds(450,650,70, 23);
		frmOscilloscope.getContentPane().add(valueStop);
		
		JButton btnStop = new JButton("STOP");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.getCalc().setStop(!Main.getCalc().getStop());
			}
		});
		btnStop.setBounds(500, 625, 159, 23);
		frmOscilloscope.getContentPane().add(btnStop);
		
		JSlider slXScale= new JSlider(JSlider.VERTICAL,1,10,2);
		slXScale.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source= (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()){
					int scale=(int)source.getValue();
					System.out.println(scale);
					double zoom=scale/zoomX;
					zoomX=(double) scale;
					chartPanel.setZoomInFactor(zoom);
					chartPanel.zoomInRange( 0,0 );
					valueZoomX.setText("Scale: "+String.valueOf(zoomX));
				}
			}
		});
		slXScale.setVisible(true);
		slXScale.setBounds(650, 100, 25, 140);
		frmOscilloscope.getContentPane().add(slXScale);
		
		
		
		JSlider slXScaleZ= new JSlider(JSlider.VERTICAL,1,10,1);
		
		slXScaleZ.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source= (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()){
					int scale=(int)source.getValue();
					double zoom=scale/zoomX;
					zoomX=(double) scale;
					chartPanel.setZoomInFactor(1/zoom);
					chartPanel.zoomInRange( 0,0 );
					valueZoomX.setText("Scale: 1/"+String.valueOf(zoomX));
				}
			}
		});
		slXScaleZ.setVisible(true);
		slXScaleZ.setBounds(680, 100, 25, 140);
		frmOscilloscope.getContentPane().add(slXScaleZ);
		
		JTextField valueFreq=new JTextField(String.valueOf(frequency));
		valueFreq.setEditable(false);;
		valueFreq.setBounds(650,450,70, 23);
		frmOscilloscope.getContentPane().add(valueFreq);
		
		JSlider slFScale= new JSlider(JSlider.HORIZONTAL,1,10,1);
		slFScale.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source= (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()){
					int scale=(int) source.getValue();
					switch(scale){
					case 1:
						frequency=1;break;
					case 2:
						frequency=10;break;
					case 3:
						frequency=50;break;
					case 4:
						frequency=100;break;
					case 5:
						frequency=200;break;
					case 6:
						frequency=500;break;
					case 7:
						frequency=1000;break;
					case 8:
						frequency=2000;break;
					case 9:
						frequency=5000;break;
					case 10:
						frequency=10000;break;
					}
					valueFreq.setText(String.valueOf(frequency));
				}
			}

			
		});
		slFScale.setVisible(true);
		slFScale.setBounds(650, 400, 140, 25);
		frmOscilloscope.getContentPane().add(slFScale);
		
		valueZoomX=new JTextField(String.valueOf(zoomX));
		valueZoomX.setEditable(false);;
		valueZoomX.setBounds(650, 250,70, 23);
		frmOscilloscope.getContentPane().add(valueZoomX);
		
		
		
		JSlider slTrigger= new JSlider(JSlider.HORIZONTAL,1,10,1);
		
		slTrigger.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source= (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()){
					if(enTrigger){
						trigger=source.getValue();
					}
				}
			}
		});
		slTrigger.setVisible(true);
		slTrigger.setBounds(650, 540, 140, 25);
		slTrigger.setVisible(false);
		frmOscilloscope.getContentPane().add(slTrigger);
		
		JRadioButton trigBut=new JRadioButton("Enable tigger",enTrigger);
		trigBut.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(!enTrigger){
					enTrigger=true;
					slTrigger.setVisible(true);
				}
				else{
					enTrigger=false;
					slTrigger.setVisible(false);
				}
			}
		});
		trigBut.setBounds(650,500,100,25);
		frmOscilloscope.getContentPane().add(trigBut);
		
		valueMeas=new JTextField("nothing measuring");
		valueMeas.setEditable(false);;
		valueMeas.setBounds(750,150,100, 25);
		valueMeas.setVisible(false);
		frmOscilloscope.getContentPane().add(valueMeas);
		
		JComboBox measureBox=new JComboBox(Measurements.values());
		measureBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				measure=(Measurements) measureBox.getSelectedItem();
				if(measure==Measurements.DEFAULT){
					valueMeas.setVisible(false);
				}
				else{
					valueMeas.setVisible(true);
				}
			}
		
		});
		measureBox.setBounds(750,100,100,25);
		frmOscilloscope.getContentPane().add(measureBox);
		
		
	}
	
	private void changeFreqData(int size) {
		freqData=new int[size];
		for(int i=0; i<size ;i++){
			if(frequency==1){
				freqData[i]=i;
			}
			else if(frequency<=100){
				freqData[i]=i*(1000/frequency);
				axis="ms";
			}
			else if(frequency<=1000){
				freqData[i]=i*(1000/frequency);
				axis="ms";
			}
			else if(frequency<=10000){
				freqData[i]=(i*1000000)/frequency;
				axis="µs";
			}
		}
	}
	
	protected void setContentPane(ChartPanel chartPanel2) {
		// TODO Auto-generated method stub
		
	}

	private DefaultCategoryDataset createDataset( ) {
	      dataset = new DefaultCategoryDataset( );
	      dataset.addValue(1, "Volt" , "0" );
	      dataset.addValue( -1, "Volt" , "1" );
	      return dataset;
	   }
	
	public void changeDataset(double[] data){
		boolean trigFound=false;
		int j=0;
		triggerSize=data.length;
		changeFreqData(data.length);
		dataset = new DefaultCategoryDataset();
		for(int i=0;i<= triggerSize-1;i++){
			if(enTrigger){
				if(trigFound || data[i] >=trigger){
					if(trigFound=false){
						if(data.length-i <= triggerSize){
							triggerSize=data.length-i;
						}
					}
					trigFound=true;
					dataset.addValue(data[i], "Channel1", Integer.toString(freqData[j]));
					j++;
				}
			}
			else{
				dataset.addValue(data[i], "Channel1", Integer.toString(freqData[i]));
			}	
		}
		
		JFreeChart lineChart = ChartFactory.createLineChart(" ", axis, "Volt", dataset);	
		chartPanel.setZoomInFactor(zoomX);
		chartPanel.setChart(lineChart);
		chartPanel.zoomInRange( 0,0 );
	}
	
	public void Calculate(double[] data){
		
		switch(measure){
		
		case MEAN:
			double mean=0;
			for(double d:data){
				mean+=d;
			}
			mean=mean/data.length;
			valueMeas.setText(Double.toString(mean));
			break;
			
		case MAX:
			double max=-8;
			for(double d:data){
				if(d>max){
					max=d;
				}
			}
			valueMeas.setText(Double.toString(max));
			break;
			
		case MIN:
			double min=8;
			for(double d:data){
				if(d<min){
					min=d;
				}
			}
			valueMeas.setText(Double.toString(min));
			break;
			
		case PK_TO_PK:
			double maxpk=-8;
			double minpk=8;
			for(double d:data){
				if(d>maxpk){
					maxpk=d;
				}
				else if(d<minpk){
					minpk=d;
				}
			}
			double pk_to_pk=maxpk-minpk;
			valueMeas.setText(Double.toString(pk_to_pk));
			
		}
		
	}
}
