package machine_drum;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;

public class Hello_Beads {
    public static void macos() {
        JavaSoundAudioIO jsaio = new JavaSoundAudioIO(512);
        JavaSoundAudioIO.printMixerInfo();
        jsaio.selectMixer(3);
        AudioContext ac = new AudioContext(jsaio);

        //create a sine generator
        WavePlayer wp = new WavePlayer(ac,440.0f, Buffer.SINE);

        //create a Glide to control the gain 5000ms ramp time
        Glide gainGlide = new Glide(ac, 0.0f, 5000.0f);

        //create a Gain to control the sine wave volume

        Gain sineGain = new Gain(ac, 1, gainGlide);

        //add the wave generator to the gain
        sineGain.addInput(wp);

        //add the Gain as an input to the master output, ac.out
        ac.out.addInput(sineGain);


        ac.start();

        //ramp the gain to 0.9f
        gainGlide.setValue(0.9f);
    }


    public static void main(String[] args) {
       macos();
    }

}
