package com.linkinpark213.phone.client.speaker;

import javax.sound.sampled.*;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class Recorder {
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private RecordThread recordThread;
    private String dirName;

    public Recorder(String dirName) {
        try {
            audioFormat = getDefaultAudioFormat();
            DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(targetDataLineInfo);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        this.dirName = dirName;
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

    public void startRecording() {
        recordThread = new RecordThread(audioFormat, targetDataLine, dirName);
        recordThread.start();
    }

    public void stopRecording() {
        targetDataLine.stop();
        recordThread.stop();
        targetDataLine.close();
    }
}
