package machine_drum;

import machine_drum.midi.MidiKeyboard;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;

import javax.sound.midi.ShortMessage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Hello_Midi {
    WavePlayer sine;
    Gain sineGain;
    Glide gainGlide;

    public static void main(String[] args) {
        Hello_Midi synth = new Hello_Midi();
        synth.setup();
    }

    public void setup() {
        //set up audio context
        JavaSoundAudioIO jsaio = new JavaSoundAudioIO(512);
        JavaSoundAudioIO.printMixerInfo();
        jsaio.selectMixer(3);
        AudioContext ac = new AudioContext(jsaio);

        //create a sine generator
        sine = new WavePlayer(ac,440.0f, Buffer.SINE);

        //create a Glide to control the gain 5000ms ramp time
        gainGlide = new Glide(ac, 0.0f, 50.0f);

        //create a Gain to control the sine wave volume
        sineGain = new Gain(ac, 1, gainGlide);

        //add the wave generator to the gain
        sineGain.addInput(sine);

        //add the Gain as an input to the master output, ac.out
        ac.out.addInput(sineGain);

        // set up the keyboard input
        MidiKeyboard keys = new MidiKeyboard();
        keys.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // if the event is not null
                if( e != null )
                {
                    // if the event is a MIDI event
                    if( e.getSource() instanceof ShortMessage)
                    {
                        // get the MIDI event
                        ShortMessage sm = (ShortMessage)e.getSource();

                        // if the event is a key down
                        if( sm.getCommand() == MidiKeyboard.NOTE_ON && sm.getData2() > 1 )
                        {
                            keyDown(sm.getData1());
                        }
                        // if the event is a key up
                        else if( sm.getCommand() == MidiKeyboard.NOTE_OFF )
                        {
                            keyUp(sm.getData1());
                        }
                    }
                }
            }
        });

        ac.start();
    }

    private float pitchToFrequency(int midiPitch)
    {
        /*
         *  MIDI pitch number to frequency conversion equation from
         *  http://newt.phys.unsw.edu.au/jw/notes.html
         */
        double exponent = (midiPitch - 69.0) / 12.0;
        return (float)(Math.pow(2, exponent) * 440.0f);
    }

    public void keyDown(int midiPitch)
    {
        if( sine != null && gainGlide != null )
        {
            sine.setFrequency(pitchToFrequency(midiPitch));
            gainGlide.setValue(0.9f);
        }
    }

    public void keyUp(int midiPitch)
    {
        if( gainGlide != null )
        {
            gainGlide.setValue(0.0f);
        }
    }
}
