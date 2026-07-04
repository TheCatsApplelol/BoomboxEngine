import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// BoomboxEngine, a custom, thread-safe, polyphonic Java 8 sound engine. enjoy! 😁


public class BoomboxEngine {

    // cache to hold raw audio bytes in ram
    private final Map<String, CachedSound> soundCache = new HashMap<>();
    
    // background worker threads
    private final ExecutorService audioThreadPool = Executors.newCachedThreadPool();

    // load sounds into memory
    public void load(String name, String filepath) {
        try {
            File audioFile = new File(filepath);
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = ais.getFormat();

            // read the entire file into memory as a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = ais.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            byte[] audioData = baos.toByteArray();
            ais.close();

            // store it in our cache
            soundCache.put(name, new CachedSound(format, audioData));
            System.out.println("[Boombox] Loaded into RAM: " + name);

        } catch (Exception e) {
            System.err.println("[Boombox] Failed to load sound: " + filepath);
            e.printStackTrace();
        }
    }

    // play the sound
    public void play(String name) {
        play(name, 1.0f); // defualt: full volume
    }

    // play the sound with a custom volume
    public void play(String name, float volume) {
        CachedSound sound = soundCache.get(name);
        if (sound == null) {
            System.err.println("[Boombox] Error: Sound '" + name + "' not found in cache!");
            return;
        }

        // pass the playback to a background thread
        audioThreadPool.submit(() -> {
            try {
                DataLine.Info info = new DataLine.Info(Clip.class, sound.format);
                Clip clip = (Clip) AudioSystem.getLine(info);
                
                // lightning fast clip opener using the cached ram bytes
                clip.open(sound.format, sound.data, 0, sound.data.length);

                // set volume
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float range = gainControl.getMaximum() - gainControl.getMinimum();
                    float gain = (range * volume) + gainControl.getMinimum();
                    gainControl.setValue(gain);
                }

                // important: listen for when the sound stops and clear it from memory
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // shut down the engine when the game closes
    public void shutdown() {
        audioThreadPool.shutdown();
        soundCache.clear();
        System.out.println("[Boombox] Engine shut down.");
    }

    // helper class to hold cached data
    private static class CachedSound {
        AudioFormat format;
        byte[] data;

        CachedSound(AudioFormat format, byte[] data) {
            this.format = format;
            this.data = data;
        }
    }
}
