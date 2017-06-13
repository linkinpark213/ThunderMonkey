package com.linkinpark213.phone.client.receiver;

import javax.sound.sampled.*;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class Player {
    private static final AudioFormat AUDIO_FORMAT;
    private SourceDataLine sourceDataLine;

    static {
        AUDIO_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                8000f,
                16,
                1,
                2,
                8000f,
                true);
    }

    public Player() {
        try {
            DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, AUDIO_FORMAT);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceDataLineInfo);
            sourceDataLine.open(AUDIO_FORMAT);
            sourceDataLine.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
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

    public void play(byte[] record) {
        sourceDataLine.write(record, 0, record.length);
    }

    public void close() {
        sourceDataLine.close();
    }
}
