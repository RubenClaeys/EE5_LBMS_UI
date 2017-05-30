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
	
	private Main main = null;
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
	private double[] setdata;
	
	
	/**
	 * Returns the frame of the scope
	 */
	public JFrame getFrame(){
		return frmOscilloscope;
	}


	/**
	 * Create the application.
	 */
	public Oscilloscope(Main main) {
		this.main = main;
		initialize();
	}
	
	/**
	 * returns the selected measurement
	 * @return
	 */
	public Measurements getMeasure(){
		return measure;
	}

	/**
	 * collect the input data and update the graph
	 * @param data
	 */
	public void changeDataset(double[] data){
		//init for triggering
		boolean trigFound=false;
		int j=0;
		if(setdata.length==0){
			setdata=data;
		}
		if(triggerSize==0){
			triggerSize=data.length;
		}
		//makes the y-scale
		changeFreqData(data.length);
		//make a dataset
		dataset = new DefaultCategoryDataset();
		//loop for putting the data in the dataset, if trigger is on the data has to be risen higher then a triggervalue
		for(int i=0;i<= triggerSize-1;i++){
			if(enTrigger){
				if(trigFound || (data[i] >=trigger && data[i-1] < trigger)){
					if(trigFound=false){
						if(data.length-i <= triggerSize && data.length-i> data.length/2){
							triggerSize=data.length-i;
						}
					}
					if(data[i]>=(setdata[j]-0.25) && data[i]<=(setdata[i]+0.25)){
						setdata[j]=data[i];
					}
					else{
						setdata[j]=(setdata[j]+data[i])/2;
					}
					trigFound=true;
					dataset.addValue(setdata[j], "Channel1", Integer.toString(freqData[j]));
					j++;
				}
			}
			else{
				dataset.addValue(data[i], "Channel1", Integer.toString(freqData[i]));
			}	
		}
		//Updating the chart with the new data
		JFreeChart lineChart = ChartFactory.createLineChart(" ", axis, "Volt", dataset);	
		chartPanel.setZoomInFactor(zoomX);
		chartPanel.setChart(lineChart);
		chartPanel.zoomInRange( 0,0 );
	}
	
	/**
	 * Calculating the measurment that is selected
	 * @param data
	 */
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
			break;
		case DEFAULT:
			valueMeas.setText("Nothing to measure");
			break;
			
		default:
			valueMeas.setText("Nothing to measure");
			break;
		}
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//initialize parameters for the frame
		zoomX=1;
		frequency=1;
		enTrigger=false;
		trigger=0;
		triggerSize=0;
		axis="s";
		measure= Measurements.DEFAULT;
		setdata[0]=0;
		
		//making a new frame for the osciloscoop
		frmOscilloscope = new JFrame();
		frmOscilloscope.setTitle("Oscilloscope");
		frmOscilloscope.setBounds(650, 0, 800, 700);
		frmOscilloscope.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOscilloscope.getContentPane().setLayout(null);
		
		
		/*
		 * The start button makes a new chart and show it on the frame
		 */
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
		
		/**
		 * This button shows some dummy data on the screen
		 */
		JButton btnStart = new JButton(" data");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				main.getCalc().setRange(Range.EIGHT);
				double[] data={-5,-4,-3,-2,-1,0,1,2,3,4,5};
				main.getCalc().toUserInterface(data);;
			}
		});
		btnStart.setBounds(315, 625, 159, 23);
		frmOscilloscope.getContentPane().add(btnStart);
		
		
		JTextField valueStop=new JTextField("Running");
		valueStop.setEditable(false);;
		valueStop.setBounds(450,650,70, 23);
		frmOscilloscope.getContentPane().add(valueStop);
		
		/**
		 * The stop button toggles between a stop and run state.
		 * When in the stop state the graph is frozen on the last update
		 */
		JButton btnStop = new JButton("STOP");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				main.getCalc().setStop(!main.getCalc().getStop());
				
			}
		});
		btnStop.setBounds(500, 625, 159, 23);
		frmOscilloscope.getContentPane().add(btnStop);
		
		/**
		 * The scale slider scales the y-axis on the screen
		 * this one is for scaling lower then it is now
		 */
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
		
		/**
		 * The scale slider scales the y-axis on the screen
		 * this one is for scaling higher then it is now
		 */
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
		
		/**
		 * This textfield shows the slected frequency
		 */
		JTextField valueFreq=new JTextField(String.valueOf(frequency));
		valueFreq.setEditable(false);;
		valueFreq.setBounds(650,450,70, 23);
		frmOscilloscope.getContentPane().add(valueFreq);
		
		/**
		 * To select the frequency on the slider
		 */
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
		
		/**
		 * This textfield show the value of the scaling
		 */
		valueZoomX=new JTextField(String.valueOf(zoomX));
		valueZoomX.setEditable(false);;
		valueZoomX.setBounds(650, 250,70, 23);
		frmOscilloscope.getContentPane().add(valueZoomX);
		
		/**
		 * This slider is for indicating how high the triggering of the signal would be
		 */
		JSlider slTrigger= new JSlider(JSlider.HORIZONTAL,1,10,1);
		slTrigger.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source= (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()){
					if(enTrigger){
						trigger=source.getValue();
						System.out.println(trigger);
					}
				}
			}
		});
		slTrigger.setVisible(true);
		slTrigger.setBounds(650, 540, 140, 25);
		slTrigger.setVisible(false);
		frmOscilloscope.getContentPane().add(slTrigger);
		
		/**
		 * The button is for enabling or disabling the trigering on a wave
		 */
		JRadioButton trigBut=new JRadioButton("Enable tigger",enTrigger);
		trigBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
		
		/**
		 * This textfield show the measured value, if it's neccesairy
		 */
		valueMeas=new JTextField("nothing measuring");
		valueMeas.setEditable(false);;
		valueMeas.setBounds(750,150,100, 25);
		valueMeas.setVisible(false);
		frmOscilloscope.getContentPane().add(valueMeas);
		
		/**
		 * The combo box is for selecting what kind of measurment you want to see
		 */
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
	
	/**
	 * This makes the x-axis scale, based on a selected frequency
	 * @param size
	 */
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
	

}
