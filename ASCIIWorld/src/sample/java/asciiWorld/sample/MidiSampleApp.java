/*
 * MidiSampleApp.java
 * Purpose: 
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Trey Tomes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package asciiWorld.sample;

import javax.sound.midi.Instrument;

import asciiWorld.audio.MidiSynthesizer;

/**
 * @author Trey Tomes <trey.tomes@gmail.com>
 *
 */
public class MidiSampleApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
			e.printStackTrace();
		}
	}
}
