package asciiWorld.audio;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.Instrument;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class AudioTestApp extends JFrame {
	
	private static final long serialVersionUID = -2679979295324647669L;

	public static void main(String args[]){
		//AudioTestApp app = new AudioTestApp();
		midiTest();
	}
	
	private static void midiTest() {
		MidiSynthesizer synth = new MidiSynthesizer();
		
		for (Instrument instr : synth.getInstruments()) {
			System.out.println(instr.getName());
		}
		
		synth.loadInstrument("Marimba");
		
		for (int n = 0; n <= 127; n++) {
			synth.play(n, 100);
		}
		for (int n = 127; n >= 0; n--) {
			synth.play(n, 100);
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	  // Following components appear in the North position of the GUI.
	  final JButton generateBtn = new JButton("Generate");
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

	  public AudioTestApp() {
	    //A panel for the North position.  Note the etched border.
	    final JPanel controlButtonPanel = new JPanel();
	    controlButtonPanel.setBorder(BorderFactory.createEtchedBorder());

	    //A panel and button group for the radio buttons in the Center position.
	    final JPanel synButtonPanel = new JPanel();
	    final ButtonGroup synButtonGroup = new ButtonGroup();
	    //This panel is used for cosmetic purposes only, to cause the radio buttons to be centered horizontally in the Center position.
	    final JPanel centerPanel = new JPanel();

	    //A panel for the South position.  Note the etched border.
	    final JPanel outputButtonPanel = new JPanel();
	    outputButtonPanel.setBorder(BorderFactory.createEtchedBorder());

	    //Register anonymous listeners on the Generate button and the Play/File button.
	    generateBtn.addActionListener(
	      new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	        	SignalGenerator gen = new SignalGenerator();
	        	gen.generate(getSignalType());
				new AudioPlaybackThread(gen).start();
	        }
	      }
	    );
	    
	    //Add two buttons and a text field to a physical group in the North of the GUI.
	    controlButtonPanel.add(generateBtn);
	    controlButtonPanel.add(elapsedTimeMeter);

	    //Add radio buttons to a mutually exclusive group in the Center of the GUI.  Make additions here if you add new synthetic generator methods.
	    synButtonGroup.add(tones);
	    synButtonGroup.add(stereoPanning);
	    synButtonGroup.add(stereoPingpong);
	    synButtonGroup.add(fmSweep);
	    synButtonGroup.add(decayPulse);
	    synButtonGroup.add(echoPulse);
	    synButtonGroup.add(waWaPulse);

	    //Add radio buttons to a physical group and center it in the Center of the GUI. Make additions here if you add new synthetic generator methods.
	    synButtonPanel.setLayout(new GridLayout(0,1));
	    synButtonPanel.add(tones);
	    synButtonPanel.add(stereoPanning);
	    synButtonPanel.add(stereoPingpong);
	    synButtonPanel.add(fmSweep);
	    synButtonPanel.add(decayPulse);
	    synButtonPanel.add(echoPulse);
	    synButtonPanel.add(waWaPulse);

	    //Note that the centerPanel has center alignment by default.
	    centerPanel.add(synButtonPanel);

	    //Add the panels containing components to the content pane of the GUI in the appropriate positions.
	    getContentPane().add(controlButtonPanel,BorderLayout.NORTH);
	    getContentPane().add(centerPanel, BorderLayout.CENTER);
	    getContentPane().add(outputButtonPanel, BorderLayout.SOUTH);

	    //Finish the GUI.  If you add more radio buttons in the center, you may need to modify the call to setSize to increase the vertical component of the GUI size.
	    setTitle("Copyright 2003, R.G.Baldwin");
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setSize(320, 480);
	    setVisible(true);
    }
}