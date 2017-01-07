package sign;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class WavPlayer implements Runnable {

  private final int BUFFER_SIZE = 128000;
  private File soundFile;
  private AudioInputStream audioStream;
  private AudioFormat audioFormat;
  private SourceDataLine sourceLine;
  private String filename;
  private int delay;
  private double volume;

  public WavPlayer(String filename, int delay, double volume) {
    this.filename = filename;
    this.delay = delay;
    this.volume = volume;
  }

  public void run() {

    try {
      Thread.sleep(delay);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String strFilename = filename;

    try {
      soundFile = new File(strFilename);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      audioStream = AudioSystem.getAudioInputStream(soundFile);
    } catch (Exception e) {
      e.printStackTrace();
    }

    audioFormat = audioStream.getFormat();

    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
    try {
      sourceLine = (SourceDataLine) AudioSystem.getLine(info);
      sourceLine.open(audioFormat);

      FloatControl gainControl = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
      double gain = (double) volume / 100.0;
      float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
      gainControl.setValue(dB);
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    sourceLine.start();

    int nBytesRead = 0;
    byte[] abData = new byte[BUFFER_SIZE];
    while (nBytesRead != -1) {
      try {
        nBytesRead = audioStream.read(abData, 0, abData.length);
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (nBytesRead >= 0) {
        @SuppressWarnings("unused")
        int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
      }
    }

    sourceLine.drain();
    sourceLine.close();
  }

}
