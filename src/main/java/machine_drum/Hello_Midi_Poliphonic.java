package machine_drum;

import machine_drum.midi.MidiKeyboard;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;

public class Hello_Midi_Poliphonic {
    WavePlayer osc1;
    WavePlayer osc2;
    WavePlayer osc3;
    Gain sineGain1;
    Gain sineGain2;
    Gain sineGain3;
    Glide gainGlide1;
    Glide gainGlide2;
    Glide gainGlide3;


    public static void main(String[] args) {
        Hello_Midi_Poliphonic synth = new Hello_Midi_Poliphonic();
        synth.setup();
    }

    public void setup() {

        //set up audio context
        JavaSoundAudioIO jsaio = new JavaSoundAudioIO(512);
        JavaSoundAudioIO.printMixerInfo();
        jsaio.selectMixer(3);
        AudioContext ac = new AudioContext(jsaio);

        List<WavePlayer> oscillators = new ArrayList<>();

        osc1 = new WavePlayer(ac, 440.0f, Buffer.SINE);
        osc2 = new WavePlayer(ac, 440.0f, Buffer.SAW);
        osc3 = new WavePlayer(ac, 440.0f, Buffer.TRIANGLE);

        oscillators.add(osc1);
        oscillators.add(osc2);
        oscillators.add(osc3);

        gainGlide1 = new Glide(ac, 0.0f, 50.0f);
        gainGlide2 = new Glide(ac, 0.0f, 50.0f);
        gainGlide3 = new Glide(ac, 0.0f, 50.0f);
        sineGain1 = new Gain(ac, 1, gainGlide1);
        sineGain2 = new Gain(ac, 1, gainGlide2);
        sineGain3 = new Gain(ac, 1, gainGlide3);

        sineGain1.addInput(osc1);
        sineGain2.addInput(osc2);
        sineGain3.addInput(osc3);


        ac.out.addInput(sineGain1);
        ac.out.addInput(sineGain2);
        ac.out.addInput(sineGain3);

        // set up the keyboard input
        MidiKeyboard keys = new MidiKeyboard();
        keys.addActionListener(midiEvent -> {
            // if the event is not null
            if (midiEvent != null) {
                // if the event is a MIDI event
                if (midiEvent.getSource() instanceof ShortMessage) {
                    // get the MIDI event
                    ShortMessage sm = (ShortMessage) midiEvent.getSource();

                    // if the event is a key down
                    if (sm.getCommand() == MidiKeyboard.NOTE_ON && sm.getData2() > 1) {
                        keyDown(sm.getData1(), ac, oscillators);
                        System.out.println(keys.getMidiInputEvent());
                    }
                    // if the event is a key up
                    else if (sm.getCommand() == MidiKeyboard.NOTE_OFF) {
                        keyUp(sm.getData1());
                    }
                }
            }
        });

        ac.start();
    }

    private float pitchToFrequency(int midiPitch) {
        /*
         *  MIDI pitch number to frequency conversion equation from
         *  http://newt.phys.unsw.edu.au/jw/notes.html
         */
        double exponent = (midiPitch - 69.0) / 12.0;
        System.out.println("frequency " + (float) (Math.pow(2, exponent) * 440.0f));
        return (float) (Math.pow(2, exponent) * 440.0f);
    }

    public void keyDown(int midiPitch, AudioContext ac, List<WavePlayer> oscillators) {
        for(WavePlayer osc : oscillators) {
            if(osc != null && gainGlide1 != null && gainGlide2 != null && gainGlide3 != null) {
                osc1.setFrequency(pitchToFrequency(midiPitch));
                osc2.setFrequency(pitchToFrequency(midiPitch));
                osc3.setFrequency(pitchToFrequency(midiPitch));
                gainGlide1.setValue(0.3f);
                gainGlide2.setValue(0.3f);
                gainGlide3.setValue(0.3f);
            }
        }
    }

    public void keyUp(int midiPitch) {
        if (gainGlide1 != null  && gainGlide2 != null && gainGlide3 != null) {
            gainGlide1.setValue(0.0f);
            gainGlide2.setValue(0.0f);
            gainGlide3.setValue(0.0f);
        }
    }
}
