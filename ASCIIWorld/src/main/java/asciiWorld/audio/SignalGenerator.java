package asciiWorld.audio;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Inner signal generator class.
 * 
 * An instance of this class can be used to generate a variety of different synthetic
 * audio signals.  Each time the getSyntheticData method is called on an instance of this class,
 * the method will fill the incoming array with the samples for a synthetic signal.
 * 
 * @author Trey
 *
 */
public class SignalGenerator {
	
	//The following are audio format parameters.
	// They may be modified by the signal generator
	// at runtime.  Values allowed by Java
	// SDK 1.4.1 are shown in comments.
	private float sampleRate = 16000.0f;
	  
	// Allowable 8, 16.
	private int channels = 1;
	
	// A buffer to hold two seconds monaural and one second stereo data at 16000 samp/sec for 16-bit samples
	private byte _synDataBuffer[];

	//Note:  Because this class uses a ByteBuffer asShortBuffer to handle the data, it can only be used to generate signed 16-bit data.
	ByteBuffer byteBuffer;
	ShortBuffer shortBuffer;
	int byteLength;
	
	public int getChannels() {
		return channels;
	}
	
	public float getSampleRate() {
		return sampleRate;
	}
	
	public byte[] getAudioData() {
		return _synDataBuffer;
	}
	
	public void generate(SignalType signalType){
		_synDataBuffer = new byte[16000 * 4];
		
		// Prepare the ByteBuffer and the shortBuffer for use
byteBuffer = ByteBuffer.wrap(_synDataBuffer);
shortBuffer = byteBuffer.asShortBuffer();

byteLength = _synDataBuffer.length;

// Decide which synthetic data generator method to invoke based on which radio
// button the user selected in the Center of the GUI.  If you add more methods for
// other synthetic data types, you need to add corresponding radio buttons to the
// GUI and add statements here to test the new radio buttons.  Make additions here
// if you add new synthetic generator methods.
    switch (signalType) {
    case DecayPulse:
    	decayPulse();
    	break;
    case EchoPulse:
    	echoPulse();
    	break;
    case FMSweep:
    	fmSweep();
    	break;
    case StereoPanning:
    	stereoPanning();
    	break;
    case StereoPingPong:
    	stereoPingpong();
    	break;
    case Tones:
    	tones();
    	break;
    case WaWaPulse:
    	waWaPulse();
    	break;
    }

  }

  //This method generates a monaural tone
  // consisting of the sum of three sinusoids.
  void tones(){
	  TonesSignalGenerator gen = new TonesSignalGenerator(_synDataBuffer);
	  gen.generate();
	  
	  channels = gen.getChannels();
	  sampleRate = gen.getSampleRate();
	  //shortBuffer = gen.getBuffer();
  }
  //-------------------------------------------//

  //This method generates a stereo speaker sweep,
  // starting with a relatively high frequency
  // tone on the left speaker and moving across
  // to a lower frequency tone on the right
  // speaker.
  void stereoPanning(){
    channels = 2;//Java allows 1 or 2
int bytesPerSamp = 4;//Based on channels
sampleRate = 16000.0F;
// Allowable 8000,11025,16000,22050,44100
int sampLength = byteLength/bytesPerSamp;
for(int cnt = 0; cnt < sampLength; cnt++){
  //Calculate time-varying gain for each
  // speaker
  double rightGain = 16000.0*cnt/sampLength;
  double leftGain = 16000.0 - rightGain;

  double time = cnt/sampleRate;
  double freq = 600;//An arbitrary frequency
  //Generate data for left speaker
  double sinValue =
             Math.sin(2*Math.PI*(freq)*time);
  shortBuffer.put(
                 (short)(leftGain*sinValue));
  //Generate data for right speaker
  sinValue =
         Math.sin(2*Math.PI*(freq*0.8)*time);
  shortBuffer.put(
                (short)(rightGain*sinValue));
}//end for loop
  }//end method stereoPanning
  //-------------------------------------------//

  //This method uses stereo to switch a sound
  // back and forth between the left and right
  // speakers at a rate of about eight switches
  // per second.  On my system, this is a much
  // better demonstration of the sound separation
  // between the two speakers than is the
  // demonstration produced by the stereoPanning
  // method.  Note also that because the sounds
  // are at different frequencies, the sound
  // produced is similar to that of U.S.
  // emergency vehicles.

  void stereoPingpong(){
    channels = 2;//Java allows 1 or 2
int bytesPerSamp = 4;//Based on channels
sampleRate = 16000.0F;
// Allowable 8000,11025,16000,22050,44100
int sampLength = byteLength/bytesPerSamp;
double leftGain = 0.0;
double rightGain = 16000.0;
for(int cnt = 0; cnt < sampLength; cnt++){
  //Calculate time-varying gain for each
  // speaker
  if(cnt % (sampLength/8) == 0){
    //swap gain values
    double temp = leftGain;
    leftGain = rightGain;
    rightGain = temp;
  }//end if

  double time = cnt/sampleRate;
  double freq = 600;//An arbitrary frequency
  //Generate data for left speaker
  double sinValue =
             Math.sin(2*Math.PI*(freq)*time);
  shortBuffer.put(
                 (short)(leftGain*sinValue));
  //Generate data for right speaker
  sinValue =
         Math.sin(2*Math.PI*(freq*0.8)*time);
  shortBuffer.put(
                (short)(rightGain*sinValue));
}//end for loop
  }//end stereoPingpong method
  //-------------------------------------------//

  //This method generates a monaural linear
  // frequency sweep from 100 Hz to 1000Hz.
  void fmSweep(){
    channels = 1;//Java allows 1 or 2
int bytesPerSamp = 2;//Based on channels
sampleRate = 16000.0F;
// Allowable 8000,11025,16000,22050,44100
int sampLength = byteLength/bytesPerSamp;
double lowFreq = 100.0;
double highFreq = 1000.0;

for(int cnt = 0; cnt < sampLength; cnt++){
  double time = cnt/sampleRate;

  double freq = lowFreq +
           cnt*(highFreq-lowFreq)/sampLength;
  double sinValue =
               Math.sin(2*Math.PI*freq*time);
  shortBuffer.put((short)(16000*sinValue));
}//end for loop
  }//end method fmSweep
  //-------------------------------------------//

  //This method generates a monaural triple-
  // frequency pulse that decays in a linear
  // fashion with time.
  void decayPulse(){
    channels = 1;//Java allows 1 or 2
int bytesPerSamp = 2;//Based on channels
sampleRate = 16000.0F;
// Allowable 8000,11025,16000,22050,44100
int sampLength = byteLength/bytesPerSamp;
for(int cnt = 0; cnt < sampLength; cnt++){
  //The value of scale controls the rate of
  // decay - large scale, fast decay.
  double scale = 2*cnt;
  if(scale > sampLength) scale = sampLength;
  double gain = 
         16000*(sampLength-scale)/sampLength;
  double time = cnt/sampleRate;
  double freq = 499.0;//an arbitrary freq
  double sinValue =
    (Math.sin(2*Math.PI*freq*time) +
    Math.sin(2*Math.PI*(freq/1.8)*time) +
    Math.sin(2*Math.PI*(freq/1.5)*time))/3.0;
  shortBuffer.put((short)(gain*sinValue));
}//end for loop
  }//end method decayPulse
  //-------------------------------------------//

  //This method generates a monaural triple-
  // frequency pulse that decays in a linear
  // fashion with time.  However, three echoes
  // can be heard over time with the amplitude
  // of the echoes also decreasing with time.
  void echoPulse(){
    channels = 1;//Java allows 1 or 2
int bytesPerSamp = 2;//Based on channels
sampleRate = 16000.0F;
// Allowable 8000,11025,16000,22050,44100
int sampLength = byteLength/bytesPerSamp;
int cnt2 = -8000;
int cnt3 = -16000;
int cnt4 = -24000;
for(int cnt1 = 0; cnt1 < sampLength;
                cnt1++,cnt2++,cnt3++,cnt4++){
  double val = echoPulseHelper(
                            cnt1,sampLength);
  if(cnt2 > 0){
    val += 0.7 * echoPulseHelper(
                            cnt2,sampLength);
  }//end if
  if(cnt3 > 0){
    val += 0.49 * echoPulseHelper(
                            cnt3,sampLength);
  }//end if
  if(cnt4 > 0){
    val += 0.34 * echoPulseHelper(
                            cnt4,sampLength);
  }//end if

  shortBuffer.put((short)val);
}//end for loop
  }//end method echoPulse
  //-------------------------------------------//

  double echoPulseHelper(int cnt,int sampLength){
    //The value of scale controls the rate of
// decay - large scale, fast decay.
double scale = 2*cnt;
if(scale > sampLength) scale = sampLength;
double gain = 
         16000*(sampLength-scale)/sampLength;
double time = cnt/sampleRate;
double freq = 499.0;//an arbitrary freq
    double sinValue =
      (Math.sin(2*Math.PI*freq*time) +
      Math.sin(2*Math.PI*(freq/1.8)*time) +
      Math.sin(2*Math.PI*(freq/1.5)*time))/3.0;
    return(short)(gain*sinValue);
  }//end echoPulseHelper

  //-------------------------------------------//

  //This method generates a monaural triple-
  // frequency pulse that decays in a linear
  // fashion with time.  However, three echoes
  // can be heard over time with the amplitude
  // of the echoes also decreasing with time.
  //Note that this method is identical to the
  // method named echoPulse, except that the
  // algebraic sign was switched on the amplitude
  // of two of the echoes before adding them to
  // the composite synthetic signal.  This
  // resulted in a difference in the
  // sound.
  void waWaPulse(){
    channels = 1;//Java allows 1 or 2
int bytesPerSamp = 2;//Based on channels
sampleRate = 16000.0F;
// Allowable 8000,11025,16000,22050,44100
int sampLength = byteLength/bytesPerSamp;
int cnt2 = -8000;
int cnt3 = -16000;
int cnt4 = -24000;
for(int cnt1 = 0; cnt1 < sampLength;
                cnt1++,cnt2++,cnt3++,cnt4++){
  double val = waWaPulseHelper(
                            cnt1,sampLength);
  if(cnt2 > 0){
    val += -0.7 * waWaPulseHelper(
                            cnt2,sampLength);
  }//end if
  if(cnt3 > 0){
    val += 0.49 * waWaPulseHelper(
                            cnt3,sampLength);
  }//end if
  if(cnt4 > 0){
    val += -0.34 * waWaPulseHelper(
                            cnt4,sampLength);
  }//end if

  shortBuffer.put((short)val);
}//end for loop
  }//end method waWaPulse
  //-------------------------------------------//

  double waWaPulseHelper(int cnt,int sampLength){
    //The value of scale controls the rate of
// decay - large scale, fast decay.
  double scale = 2*cnt;
  if(scale > sampLength) scale = sampLength;
  double gain = 
         16000*(sampLength-scale)/sampLength;
double time = cnt/sampleRate;
double freq = 499.0;//an arbitrary freq
    double sinValue =
      (Math.sin(2*Math.PI*freq*time) +
      Math.sin(2*Math.PI*(freq/1.8)*time) +
      Math.sin(2*Math.PI*(freq/1.5)*time))/3.0;
    return(short)(gain*sinValue);
  }//end waWaPulseHelper

  //-------------------------------------------//
}//end SynGen class}
