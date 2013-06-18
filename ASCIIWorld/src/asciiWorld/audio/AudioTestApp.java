package asciiWorld.audio;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Date;

import javax.sound.midi.Instrument;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class AudioTestApp extends JFrame {

	public static void main(String args[]){
		AudioTestApp app = new AudioTestApp();
		
	}
	
	private static void midiTest() {
		MidiSynthesizer synth = new MidiSynthesizer();
		
		for (Instrument instr : synth.getInstruments()) {
			System.out.println(instr.getName());
		}
		
		synth.loadInstrument("Marimba");
		
		for (int n = 40; n <= 80; n++) {
			synth.play(n, 100);
		}
		for (int n = 80; n >= 40; n--) {
			synth.play(n, 100);
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	  // The following are general instance variables used to create a SourceDataLine object..
	  AudioFormat audioFormat;
	  AudioInputStream audioInputStream;
	  SourceDataLine sourceDataLine;
	
	  //The following are audio format parameters.
	  // They may be modified by the signal generator
	  // at runtime.  Values allowed by Java
	  // SDK 1.4.1 are shown in comments.
	  private float sampleRate = 16000.0f;
	  
	  /**
	   * Allowable 8000, 11025, 16000, 22050, 44100.
	   */
	  private static final int SAMPLE_SIZE_INT_BITS = 16;
	  
	  /**
	   * Allowable 8, 16.
	   */
	  private int channels = 1;
	  
	  /*
	   * Allowable 1, 2.
	   */
	  private static final boolean SIGNED = true;
	  
	  /**
	   * Allowable true, false.
	   */
	  private static final boolean BIG_ENDIAN = true;

	  //A buffer to hold two seconds monaural and one second stereo data at 16000 samp/sec for 16-bit samples
	  byte audioData[] = new byte[16000 * 4];

	  // Following components appear in the North position of the GUI.
	  final JButton generateBtn = new JButton("Generate");
	  final JButton playOrFileBtn = new JButton("Play/File");
	  final JLabel elapsedTimeMeter = new JLabel("0000");

	  //Following radio buttons select a synthetic
	  // data type.  Add more buttons if you add
	  // more synthetic data types.  They appear in
	  // the center position of the GUI.
	  final JRadioButton tones = new JRadioButton("Tones",true);
	  final JRadioButton stereoPanning = new JRadioButton("Stereo Panning");
	  final JRadioButton stereoPingpong = new JRadioButton("Stereo Pingpong");
	  final JRadioButton fmSweep = new JRadioButton("FM Sweep");
	  final JRadioButton decayPulse = new JRadioButton("Decay Pulse");
	  final JRadioButton echoPulse = new JRadioButton("Echo Pulse");
	  final JRadioButton waWaPulse = new JRadioButton("WaWa Pulse");
	  
	  public SignalType getSignalType() {
		  if (tones.isSelected()) {
			  return SignalType.Tones;
		  } else if (stereoPanning.isSelected()) {
			  return SignalType.StereoPanning;
		  } else if (stereoPingpong.isSelected()) {
			  return SignalType.StereoPingPong;
		  } else if (fmSweep.isSelected()) {
			  return SignalType.FMSweep;
		  } else if (decayPulse.isSelected()) {
			  return SignalType.DecayPulse;
		  } else if (echoPulse.isSelected()) {
			  return SignalType.EchoPulse;
		  } else if (waWaPulse.isSelected()) {
			  return SignalType.WaWaPulse;
		  } else {
			  return null;
		  }
	  }

	  //Following components appear in the South
	  // position of the GUI.
	  final JRadioButton listen = new JRadioButton("Listen",true);
	  final JRadioButton file = new JRadioButton("File");
	  final JTextField fileName = new JTextField("junk",10);

	  public AudioTestApp(){
	    //A panel for the North position.  Note the
	    // etched border.
	    final JPanel controlButtonPanel =
	                                    new JPanel();
	    controlButtonPanel.setBorder(
	             BorderFactory.createEtchedBorder());

	    //A panel and button group for the radio
	    // buttons in the Center position.
	    final JPanel synButtonPanel = new JPanel();
	    final ButtonGroup synButtonGroup =
	                               new ButtonGroup();
	    //This panel is used for cosmetic purposes
	    // only, to cause the radio buttons to be
	    // centered horizontally in the Center
	    // position.
	    final JPanel centerPanel = new JPanel();

	    //A panel for the South position.  Note the
	    // etched border.
	    final JPanel outputButtonPanel =
	                                    new JPanel();
	    outputButtonPanel.setBorder(
	             BorderFactory.createEtchedBorder());
	    final ButtonGroup outputButtonGroup =
	                               new ButtonGroup();

	    //Disable the Play button initially to force
	    // the user to generate some data before
	    // trying to listen to it or write it to a
	    // file.
	    playOrFileBtn.setEnabled(false);

	    //Register anonymous listeners on the
	    // Generate button and the Play/File button.
	    generateBtn.addActionListener(
	      new ActionListener(){
	        public void actionPerformed(
	                                  ActionEvent e){
	          //Don't allow Play during generation
	          playOrFileBtn.setEnabled(false);
	          //Generate synthetic data
	          new SignalGenerator().getSyntheticData(getSignalType(), audioData);
	          //Now it is OK for the user to listen
	          // to or file the synthetic audio data.
	          playOrFileBtn.setEnabled(true);
	        }//end actionPerformed
	      }//end ActionListener
	    );//end addActionListener()

	    playOrFileBtn.addActionListener(
	      new ActionListener(){
	        public void actionPerformed(
	                                  ActionEvent e){
	          //Play or file the data synthetic data
	          playOrFileData();
	        }//end actionPerformed
	      }//end ActionListener
	    );//end addActionListener()

	    //Add two buttons and a text field to a
	    // physical group in the North of the GUI.
	    controlButtonPanel.add(generateBtn);
	    controlButtonPanel.add(playOrFileBtn);
	    controlButtonPanel.add(elapsedTimeMeter);

	    //Add radio buttons to a mutually exclusive
	    // group in the Center of the GUI.  Make
	    // additions here if you add new synthetic
	    // generator methods.
	    synButtonGroup.add(tones);
	    synButtonGroup.add(stereoPanning);
	    synButtonGroup.add(stereoPingpong);
	    synButtonGroup.add(fmSweep);
	    synButtonGroup.add(decayPulse);
	    synButtonGroup.add(echoPulse);
	    synButtonGroup.add(waWaPulse);

	    //Add radio buttons to a physical group and
	    // center it in the Center of the GUI. Make
	    // additions here if you add new synthetic
	    // generator methods.
	    synButtonPanel.setLayout(
	                            new GridLayout(0,1));
	    synButtonPanel.add(tones);
	    synButtonPanel.add(stereoPanning);
	    synButtonPanel.add(stereoPingpong);
	    synButtonPanel.add(fmSweep);
	    synButtonPanel.add(decayPulse);
	    synButtonPanel.add(echoPulse);
	    synButtonPanel.add(waWaPulse);

	    //Note that the centerPanel has center
	    // alignment by default.
	    centerPanel.add(synButtonPanel);

	    //Add radio buttons to a mutually exclusive
	    // group in the South of the GUI.
	    outputButtonGroup.add(listen);
	    outputButtonGroup.add(file);

	    //Add radio buttons to a physical group in
	    // the South of the GUI.
	    outputButtonPanel.add(listen);
	    outputButtonPanel.add(file);
	    outputButtonPanel.add(fileName);

	    //Add the panels containing components to the
	    // content pane of the GUI in the appropriate
	    // positions.
	    getContentPane().add(
	          controlButtonPanel,BorderLayout.NORTH);
	    getContentPane().add(centerPanel,
	                            BorderLayout.CENTER);
	    getContentPane().add(outputButtonPanel,
	                             BorderLayout.SOUTH);

	    //Finish the GUI.  If you add more radio
	    // buttons in the center, you may need to
	    // modify the call to setSize to increase
	    // the vertical component of the GUI size.
	    setTitle("Copyright 2003, R.G.Baldwin");
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setSize(320, 480);
	    setVisible(true);
	  }//end constructor
	  //-------------------------------------------//

	  /**
	   * This method plays or files the synthetic audio data that has been generated and saved in an array in memory. 
	   */
	  private void playOrFileData() {
	    try{
	      // Get an input stream on the byte array containing the data.
	      InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);

	      // Get the required audio format.
	      audioFormat = new AudioFormat(sampleRate, SAMPLE_SIZE_INT_BITS, channels, SIGNED, BIG_ENDIAN);

	      // Get an audio input stream from the ByteArrayInputStream.
	      audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());

	      // Get info on the required data line.
	      DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

	      // Get a SourceDataLine object.
	      sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
	      
	      // Decide whether to play the synthetic data immediately, or to write it into an audio file, based on the user
	      // selection of the radio buttons in the South of the GUI..
	      if (listen.isSelected()) {
				//Create a thread to play back the data and
				// start it running.  It will run until all
				// the data has been played back
				new ListenThread().start();
	      } else {
	        //Disable buttons until existing data is written to the file.
	        generateBtn.setEnabled(false);
	        playOrFileBtn.setEnabled(false);

	        // Write the data to an output file with the name provided by the text field in the South of the GUI.
	        try {
	        	AudioSystem.write(audioInputStream, AudioFileFormat.Type.AU, new File(fileName.getText() + ".au"));
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	System.exit(0);
	        }
	        
	        // Enable buttons for another operation.
	        generateBtn.setEnabled(true);
	        playOrFileBtn.setEnabled(true);
	      }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.exit(0);
	    }
	}

	  /**
	   * Inner class to play back the data that was saved.
	   * @author Trey
	   *
	   */
	class ListenThread extends Thread{
	  //This is a working buffer used to transfer
	  // the data between the AudioInputStream and
	  // the SourceDataLine.  The size is rather
	  // arbitrary.
	  byte playBuffer[] = new byte[16384];

	  public void run(){
	    try{
	      //Disable buttons while data is being
	      // played.
	      generateBtn.setEnabled(false);
	      playOrFileBtn.setEnabled(false);

	      //Open and start the SourceDataLine
	      sourceDataLine.open(audioFormat);
	      sourceDataLine.start();

	      int cnt;
	      //Get beginning of elapsed time for
	      // playback
	      long startTime = new Date().getTime();

	      //Transfer the audio data to the speakers
	      while((cnt = audioInputStream.read(
	                              playBuffer, 0,
	                              playBuffer.length))
	                                          != -1){
	        //Keep looping until the input read
	        // method returns -1 for empty stream.
	        if(cnt > 0){
	          //Write data to the internal buffer of
	          // the data line where it will be
	          // delivered to the speakers in real
	          // time
	          sourceDataLine.write(
	                             playBuffer, 0, cnt);
	        }//end if
	      }//end while

	      //Block and wait for internal buffer of the
	      // SourceDataLine to become empty.
	      sourceDataLine.drain();


	      //Get and display the elapsed time for
	      // the previous playback.
	      int elapsedTime =
	         (int)(new Date().getTime() - startTime);
	      elapsedTimeMeter.setText("" + elapsedTime);

	      //Finish with the SourceDataLine
	      sourceDataLine.stop();
	      sourceDataLine.close();

	      //Re-enable buttons for another operation
	      generateBtn.setEnabled(true);
	      playOrFileBtn.setEnabled(true);
	    }catch (Exception e) {
	      e.printStackTrace();
	      System.exit(0);
	    }//end catch

	  }//end run
	}//end inner class ListenThread
	
	public enum SignalType {
		Tones,
		StereoPanning,
		StereoPingPong,
		FMSweep,
		DecayPulse,
		EchoPulse,
		WaWaPulse
	}
	
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
	class SignalGenerator {
	  //Note:  Because this class uses a ByteBuffer
	  // asShortBuffer to handle the data, it can
	  // only be used to generate signed 16-bit
	  // data.
	  ByteBuffer byteBuffer;
	  ShortBuffer shortBuffer;
	  int byteLength;
	  byte[] _synDataBuffer;
	  
	  void getSyntheticData(SignalType signalType, byte[] synDataBuffer){
		  _synDataBuffer = synDataBuffer;
		  
	    //Prepare the ByteBuffer and the shortBuffer
	    // for use
	    byteBuffer = ByteBuffer.wrap(synDataBuffer);
	    shortBuffer = byteBuffer.asShortBuffer();

	    byteLength = synDataBuffer.length;

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
}