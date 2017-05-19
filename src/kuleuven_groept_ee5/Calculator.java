package kuleuven_groept_ee5;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Calculator {

	private Main main = null;
	private Range range;
	private Mode mode;
	private Boolean stop=false;
	private ArrayList<Integer> samples = new ArrayList<Integer>();  
	private boolean freqEnable = false;
	private int nrOfSamples = 0;
	private int measCount=0;
	private double count = 0;
	
	
	public Calculator(Main main){
		this.main = main;
		mode = Mode.CONTINU;
		range = Range.EIGHT;
	}


	public void freqConfig(boolean b,int n){
		freqEnable = b;
		nrOfSamples = n;
		count = 0;
	}
	




	
	public void addSample(int sample){
		
		switch(mode){
		case SINGLE:

			calcSingle(sample);
			break;
			
		case CONTINU:
			samples.add(sample);
			if(samples.size() == 50){
				calcBlock();
				samples.clear();
			}
			break;
		}
		
	}

	
	public void calcSingle(int sample){

		double voltage = 0;
		
		switch(range){
		case EIGHT:
			voltage = (((double)sample)/255)*8;


			if(voltage>=4.016){
				
				voltage = ((voltage-4.016)/3.2)*8;
			}
			else{
				voltage = - (1-((voltage-0.314)/3.7))*8;
			}
			break;
		
		case FIVE:
			voltage = (((double)sample)/255) * 5;
			if(voltage >= 2.509){
				voltage = ((voltage-2.509)/2)*5;
			} else{
				voltage = - (1-((voltage-1.431)/1.078))*5;
			}
			break;
			
		case THIRTY:
			voltage = (((double)sample)/255) * 25;
			if(voltage >= 12.549){
				voltage = ((voltage-12.549)/9.706)*25;
			}
			else{
				voltage = - (1-((voltage-0.588)/11.961))*25;
			}
			break;
			
		case SEVENFIFTY:
			voltage = (((double)sample)/255) * 750;
			if(voltage>=350){
				voltage = ((voltage-350)/158.8)*750;
			}
			else{
				voltage = - (1-((voltage-82.4)/267.6))*750;
			}
			break;
			
		default:
			System.out.println("No range selected --> in default of calculate() " );
			break;
		
		}
		
		double voltageRounded = round(voltage, 3);
		main.getUi().getOutputArea().setForeground(Color.black);
		switch(range){
			case FIVE:
			case EIGHT:
			case THIRTY:
				main.getUi().getOutputArea().append(voltageRounded + "V" + "\n");
				break;
			case SEVENFIFTY:
				voltageRounded = round(voltage, 1);
				main.getUi().getOutputArea().append(voltageRounded + "mV" + "\n");
				break;
			default: 
				main.getUi().getOutputArea().append(voltageRounded + "V" + "\n");
				System.out.println("Voltage range not selected (default: append V)" + "\n");
				break;
		}
		
	}
	
	
	public void calcBlock(){
		double[] doubleSamples = new double[samples.size()]; 
		double voltage = 0;
		
		for(int i = 0; i < samples.size(); i++){
			
			int sample = samples.get(i);
			
			switch(range){
			case EIGHT:
				
				voltage = (((double)sample)/255) * 8;
				if(voltage>=4.016){
					voltage = ((voltage-4.016)/3.2)*8;
				}
				else{
					voltage = - (1-((voltage-0.314)/3.7))*8;
				}
				break;
			
			case FIVE:
				voltage = (((double)sample)/255) * 5;
				if(voltage >= 2.509){
					voltage = ((voltage-2.509)/2)*5;
				} else{
					voltage = - (1-((voltage-1.431)/1.078))*5;
				}
				break;
				
			case THIRTY:
				voltage = (((double)sample)/255) * 25;
				if(voltage >= 12.549){
					voltage = ((voltage-12.549)/9.706)*25;
				}
				else{
					voltage = - (1-((voltage-0.588)/11.961))*25;
				}
				break;
				
			case SEVENFIFTY:
				voltage = (((double)sample)/255) * 750;
				if(voltage>=350){
					voltage = ((voltage-350)/158.8)*750;
				}
				else{
					voltage = - (1-((voltage-82.4)/267.6))*750;
				}
				break;
				
			default:
				System.out.println("No range selected --> in default of calculate() " );
				break;
			
			}
			doubleSamples[i] = voltage;	
		}
		toUserInterface(doubleSamples);
		if(freqEnable){
			if(count < nrOfSamples){
				main.getFreqCalc().addSamples(doubleSamples);
				count += doubleSamples.length;
			}else{
				main.getFreqChart().getBtnCalc().setEnabled(true);
				count = 0;
				freqEnable = false;
			}
			
		}
	}
	
	
	public void toUserInterface(double[] doubleSamples){
		for(int i = 0; i < doubleSamples.length; i++){
			switch(range){
			case FIVE:
			case EIGHT:
				if(!stop && i==0){
					main.getUi().getOscilloscoop().changeDataset(doubleSamples);
					measCount++;
					if(main.getUi().getOscilloscoop().getMeasure() != Measurements.DEFAULT && measCount==10){
						main.getUi().getOscilloscoop().Calculate(doubleSamples);
						measCount=0;
					}
				}
				break;
			case THIRTY:
				main.getUi().getOutputArea().append(round(doubleSamples[i],3) + "V"  + "\n");
				break;
			case SEVENFIFTY:
				main.getUi().getOutputArea().append(round(doubleSamples[i],1) + "mV"  + "\n");
				break;
			default: 
				System.out.println("No range selected --> in default of toUserInteface() " );
				break;		
			}
			main.getUi().getOutputArea().setCaretPosition(main.getUi().getOutputArea().getDocument().getLength());
		}
		
	}
	
	public double round(double value, int places) {
		if (places < 0){
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	public void setRange(Range range) {
		this.range = range;
	}
	
	public void setMode(Mode mode){
		this.mode = mode;
	}

	public void setStop(boolean temp){
		stop=temp;
	}
	
	public boolean getStop(){
		return stop;
	}
	
}
