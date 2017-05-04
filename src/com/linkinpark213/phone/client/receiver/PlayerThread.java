package com.linkinpark213.phone.client.receiver;

import javax.sound.sampled.*;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class PlayerThread extends Thread {
    private SourceDataLine sourceDataLine;
    private String fileName;

    public PlayerThread(String fileName) {
        this.fileName = fileName;
    }

    public AudioFormat getDefaultAudioFormat() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 7619f;
        int sampleSize = 16;
        String signedString = "signed";
        boolean bigEndian = true;
        int channels = 1;
        return new AudioFormat(encoding, rate, sampleSize, channels,
                (sampleSize / 8) * channels, rate, bigEndian);
    }

    public void play() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName));
            DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, audioInputStream.getFormat());
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceDataLineInfo);
            sourceDataLine.open(audioInputStream.getFormat());
            sourceDataLine.start();

            byte[] byteArray = new byte[1024];
            int len = 0;
            while ((len = audioInputStream.read(byteArray)) > 0) {
                sourceDataLine.write(byteArray, 0, len);
            }

            audioInputStream.close();
            sourceDataLine.drain();
            sourceDataLine.close();

            File file = new File(fileName);
            file.delete();

        } catch (EOFException e) {

        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.play();
    }
}
