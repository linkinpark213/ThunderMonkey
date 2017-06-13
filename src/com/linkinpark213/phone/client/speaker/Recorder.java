package com.linkinpark213.phone.client.speaker;

import javax.sound.sampled.*;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class Recorder {
    private static final AudioFormat AUDIO_FORMAT;
    private TargetDataLine targetDataLine;

    static {
        AUDIO_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                8000f,
                16,
                1,
                2,
                8000f,
                true);
    }

    public Recorder() {
        try {
            DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(targetDataLineInfo);
            targetDataLine.open(AUDIO_FORMAT);
            targetDataLine.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
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
