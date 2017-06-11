package com.linkinpark213.phone.client.receiver;

import javax.sound.sampled.*;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class Player {
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;

    public Player() {
        try {
            audioFormat = getDefaultAudioFormat();
            DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceDataLineInfo);
            sourceDataLine.open(audioFormat);
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

}
