package kuleuven_groept_ee5;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.Plot;
import javax.swing.JFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class Oscilloscope {
	private JFrame frmOscilloscope;
	private ChartPanel chartPanel;
	private JTextField valueZoomX;
	private DefaultCategoryDataset dataset;
	private double zoomX;
	
	public JFrame getFrame(){
		return frmOscilloscope;
	}

	

	/**
	 * Create the application.
	 */
	public Oscilloscope() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		zoomX=1;
		
		frmOscilloscope = new JFrame();
		frmOscilloscope.setTitle("Oscilloscope");
		frmOscilloscope.setBounds(100, 100, 800, 700);
		frmOscilloscope.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOscilloscope.getContentPane().setLayout(null);
		
		
		
		JButton btnStartReadingOf = new JButton("Start reading of the data");
		btnStartReadingOf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setBounds(65, 25, 560, 450);
				frmOscilloscope.getContentPane().add(panel);
				
				JFreeChart lineChart = ChartFactory.createLineChart("GAFA", "Temps", "Volt", createDataset());	
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
				double[] data={-5,-4,-3,-2,-1,01,2,3,4,5};
				changeDataset(data,1);
			}
		});
		btnStart.setBounds(315, 625, 159, 23);
		frmOscilloscope.getContentPane().add(btnStart);
		
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
		
		valueZoomX=new JTextField(String.valueOf(zoomX));
		valueZoomX.setEditable(false);;
		valueZoomX.setBounds(650, 250,70, 23);
		frmOscilloscope.getContentPane().add(valueZoomX);
	}
	
	protected void setContentPane(ChartPanel chartPanel2) {
		// TODO Auto-generated method stub
		
	}

	private DefaultCategoryDataset createDataset( ) {
	      dataset = new DefaultCategoryDataset( );
	      dataset.addValue(1, "Volt" , "0" );
	      dataset.addValue( -1, "Volt" , "1" );
//	      dataset.addValue( 0.866 , "Volt" , "0.707" );
//	      dataset.addValue( 1, "Volt" ,  "1" );
//	      dataset.addValue(0, "Volt" , "1.3" );
//	      dataset.addValue( 1.77, "Volt" , "1.5" );
//	      dataset.addValue( 1.866 , "Volt" , "1.707" );
//	      dataset.addValue( 2, "Volt" ,  "2" );
	      return dataset;
	   }
	
	public void changeDataset(double[] data, int frequency){
		dataset = new DefaultCategoryDataset();
		String axis="s";
		for(int i=0;i<= data.length-1;i++){
			double key=i/frequency;
			if(key < 1){
				key=key*100;
				axis="ms";
			}
			dataset.addValue(data[i], "Channel1", Integer.toString((int) key));
		}
		JFreeChart lineChart = ChartFactory.createLineChart("GAFA", axis, "Volt", dataset);	
		chartPanel.setChart(lineChart);
	}
	
	private DefaultCategoryDataset createDataset2( ) {
	      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
	      dataset.addValue(1, "Volt" , "0" );
	      dataset.addValue( -1, "Volt" , "1" );
	      dataset.addValue( 2 , "Volt" , "2" );
	      dataset.addValue( -2, "Volt" ,  "3" );
//	      dataset.addValue(0, "Volt" , "1.3" );
//	      dataset.addValue( 1.77, "Volt" , "1.5" );
//	      dataset.addValue( 1.866 , "Volt" , "1.707" );
//	      dataset.addValue( 2, "Volt" ,  "2" );
	      return dataset;
	   }
}
