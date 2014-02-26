package asciiWorld.audio;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class MidiSynthesizer {
	
	private static final int DEFAULT_CHANNEL = 0;
	private static final int DEFAULT_DURATION = 500;
	private static final int DEFAULT_VELOCITY = 600;
	
	private Synthesizer _synth;
	private MidiChannel[] _channels;
	private Instrument[] _instruments;
	
	public MidiSynthesizer() {
		try {
			_synth = MidiSystem.getSynthesizer();
			_synth.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}

		_channels = _synth.getChannels();
		_instruments = _synth.getDefaultSoundbank().getInstruments();
	}
	
	public Instrument[] getInstruments() {
		return _instruments;
	}
	
	public int findInstrument(String name) {
		name = name.trim().toLowerCase();
		for (int n = 0; n < getInstruments().length; n++) {
			Instrument instr = getInstruments()[n];
			//System.err.println(String.format("%s=%s [%b]", instr.getName(), name, instr.getName().equals(name)));
			if (instr.getName().trim().toLowerCase().equals(name)) {
				return n;
			}
		}
		return -1;
	}
	
	public void loadInstrument(int channelNumber, int instrumentNumber) {
		_synth.loadInstrument(_instruments[instrumentNumber]);
		_channels[channelNumber].programChange(instrumentNumber);
	}
	
	public void loadInstrument(int instrumentNumber) {
		loadInstrument(DEFAULT_CHANNEL, instrumentNumber);
	}
	
	public void loadInstrument(int channelNumber, String instrumentName) {
		int instrumentNumber = findInstrument(instrumentName);
		loadInstrument(channelNumber, instrumentNumber);
	}
	
	public void loadInstrument(String instrumentName) {
		loadInstrument(DEFAULT_CHANNEL, instrumentName);
	}
	
	public void play(int note, int duration, int velocity) {
		_channels[DEFAULT_CHANNEL].noteOn(note, 600);
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_channels[DEFAULT_CHANNEL].noteOff(note);
	}
	
	public void play(int note, int duration) {
		play(note, duration, DEFAULT_VELOCITY);
	}
	
	public void play(int note) {
		play(note, DEFAULT_DURATION);
	}
	
	@Override
	protected void finalize() throws Throwable {
		_synth.close();
		super.finalize();
	}

}
