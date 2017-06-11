package com.linkinpark213.phone.client.speaker;

import javax.sound.sampled.*;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class Recorder {
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;

    public Recorder() {
        try {
            audioFormat = getDefaultAudioFormat();
            DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(targetDataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public AudioFormat getDefaultAudioFormat() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 8000f;
        int sampleSize = 16;
        String signedString = "signed";
        boolean bigEndian = true;
        int channels = 1;
        return new AudioFormat(encoding, rate, sampleSize, channels,
                (sampleSize / 8) * channels, rate, bigEndian);
    }

    public byte[] record() {
        byte[] buffer = new byte[1024];
        targetDataLine.read(buffer, 0, buffer.length);
        return buffer;
    }

    public void close() {
        targetDataLine.close();
    }
}
