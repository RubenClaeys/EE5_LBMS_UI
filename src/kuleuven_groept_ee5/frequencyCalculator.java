package kuleuven_groept_ee5;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FrequencyCalculator {
	
	private Main main = null;
	private WindowType windowType;
	private double[] windowWeights;
	private double windowWeightsSqueredSum = 0;
	private double[] samples;
	private ArrayList<Double> tempSamples; 
	private SampleFrequency sampleFreq;
	double Fs = 0;
	


	
	public FrequencyCalculator(Main main){
		this.main = main;
		tempSamples = new ArrayList<Double>();
		windowType = WindowType.RECTANGULAR;
	}
	
	
	public void startReading(int nrOfSamples){
		main.getCalc().freqConfig(true, nrOfSamples);
	}

	public void addSamples(double[] block){
		for(int i=0;i<block.length;i++){
			tempSamples.add(block[i]);
		}
		System.out.println("Block added");

	}
	

	
	public void calculate(){
		samples = new double[tempSamples.size()];
		for(int i = 0; i < tempSamples.size();i++ ){
			samples[i] = tempSamples.get(i);
		}
		
		double[] powerSpectrum = new double[samples.length/2];
		powerSpectrum = calculatePowerSpectrum(samples);
				
		setFs();
				
		double[] frequencies = new double[samples.length/2];
		frequencies = getFrequencyArray(samples,Fs);
		
		System.out.println("Powerspectrum: ");
		
		for(int i =0; i< powerSpectrum.length; i++){
			System.out.println(frequencies[i] + "-->" + powerSpectrum[i]);
		}
		
	}
	
	
	public double[] getSamples(){
		samples = new double[tempSamples.size()];
		for(int i = 0; i < tempSamples.size();i++ ){
			samples[i] = tempSamples.get(i);
		}
		return samples;
	}

	public double[] getFrequencyArray(double[] samples, double sampleFrequency){
		int size = getPowerOfTwo(samples.length);

		// the other half of frequencies are the same (mirrored)
		double[] frequencies = new double[size/2];

		for(int i=0; i<frequencies.length ; i++){
			frequencies[i] = i*sampleFrequency/size;
		}

		return frequencies;
	}
	
	
	public double[] calculatePowerSpectrum(double[] samples) { 

		Complex[] fftTransformedData = fft(samples); 
		int dataLength = samples.length; 
		int size = dataLength/2; 

		double[] powerSpectrum = new double[size]; 
		powerSpectrum[0] = square(fftTransformedData[0].getReal()) + square(fftTransformedData[0].getImaginary()); 
		for (int i = 1; i < size ; i++) { 
			powerSpectrum[i] = square(fftTransformedData[i].getReal()) 
								+ square(fftTransformedData[i].getImaginary()) 
								+ square(fftTransformedData[dataLength - i].getReal()) 
								+ square(fftTransformedData[dataLength-i].getImaginary()); 
		} 
		for (int i = 1; i < size ; i++) { 
			powerSpectrum[i] = 2.0D * powerSpectrum[i] / (windowWeightsSqueredSum*dataLength); 
		} 
		return powerSpectrum; 
	}




	public Complex[] fft(double[] data){
		FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		double[] windowedSignal = applyWindow(data);
		double[] paddedSignal = zeroPad(windowedSignal);
		Complex[] complexData = transformer.transform(paddedSignal, TransformType.FORWARD);
		return complexData;
	}

	
	
	// applies a window on the real data and returns an array of the windowed data
	public double[] applyWindow(double[] data){
		calculateWeights(data.length);
		double[] windowedData = new double[data.length];
		
		for(int i =0; i< data.length; i++){
			windowedData[i] = data[i]*windowWeights[i];
		}
		return windowedData;
	}
	
	
	// calculates the weights for a particular window of the specified length
	public void calculateWeights(int length) { 
		if (windowWeights != null && windowWeights.length == length) { 
			   return; 
			  } 
		windowWeights = new double[length]; 
		double n = length; 
		switch (windowType) { 
		case BARTLETT: 
			for (int i = 0; i < length; i++) { 
				windowWeights[i] = 1.0D - Math.abs((2.0D * i - n + 1.0D) / (n - 1.0D)); 
			} 
			break; 

		case HAMMING: 
			for (int i = 0; i < length; i++) { 
				windowWeights[i] = 0.54D - 0.46D * Math.cos(2.0D * i * Math.PI / (n - 1.0D)); 
			} 
			break; 
		case HANN: 
			for (int i = 0; i < length; i++) { 
				windowWeights[i] = 0.5D * (1.0D - Math.cos(2.0D * i * Math.PI / (n - 1.0D))); 
			} 
			break; 

		case RECTANGULAR: 
			for (int i = 0; i < length; i++) { 
				windowWeights[i] = 1.0D; 
			} 
			break; 
		case WELCH: 
			for (int i = 0; i < length; i++) { 
				windowWeights[i] = 1.0D - square((2.0D * i - n + 1.0D) / (n - 1.0D)); 
			} 
			break; 
		} 
		windowWeightsSqueredSum  = 0; 
		for (int i = 0; i < windowWeights.length; i++) 
			windowWeightsSqueredSum += square(windowWeights[i]); 
	} 
	
	
	public double[] zeroPad(double[] data){
		int initialLength = data.length;
		int newLength = getPowerOfTwo(initialLength);

		double[] paddedArray = Arrays.copyOf(data, newLength);

		return paddedArray;
	}

	public int getPowerOfTwo(int initialLength){
		double logBase2 = Math.log(initialLength)/ Math.log(2); //returns log2(initialSize)
		int newSize =(int) Math.pow(2, Math.ceil(logBase2));
		return newSize;
	}

	public double square(double x){
		return x*x;
	}
	
	public void setFs(){
		switch(sampleFreq){
		case ONE:
			Fs = 13.24;
			break;
			
		case TEN:
			Fs = 13.24;
			break;
			
		case HUNDRED:
			Fs = 101.4;
			break;
			
		case TWO_H:
			Fs = 198.7;
			break;
			
		case FIVE_H:
			Fs = 487.8;
			break;
			
		case ONE_K:
			Fs = 957.8;
			break;
			
		case TWO_K:
			Fs = 1847.5;
			break;
			
		case FIVE_K:
			Fs = 4;
			break;
			
		case TEN_K:
			Fs = 7.08;
			break;
			
		case TWENTYFIVE_K:
			Fs = 13.62;
			break;
			
		case FIFTY_K:
			Fs = 20.08;
			break;
			
		case SEVENTYFIVE_K:
			Fs = 20.08;
			break;
			
		default:
			Fs = 1;
			break;
		}
		
	}
	
	public double getFs(){
		return Fs;
	}
	
	
	
	public void setSampleFreq(SampleFrequency sampleFreq) {
		this.sampleFreq = sampleFreq;
	}
	
	

}
