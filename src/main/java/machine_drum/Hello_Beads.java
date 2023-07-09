package machine_drum;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.WavePlayer;

public class Hello_Beads {
    public static void macos() {
        JavaSoundAudioIO jsaio = new JavaSoundAudioIO(512);
        JavaSoundAudioIO.printMixerInfo();
        jsaio.selectMixer(3);
        AudioContext ac = new AudioContext(jsaio);

        WavePlayer wp = new WavePlayer(ac,440.0f, Buffer.SINE);
        ac.out.addInput(wp);
        ac.start();
    }
    public static void main(String[] args) {
       macos();
    }
}
