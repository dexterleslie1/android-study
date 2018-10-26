package com.future.android.study.media;

import android.media.MediaCodecInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dexterleslie.Chan
 */
public class AACAdtsUtils {
    private final static Map<Integer,Integer> SampleRateToAdtsSamplingFrequencyIndex=new HashMap<Integer,Integer>();

    static{
        // 参考https://wiki.multimedia.cx/index.php/MPEG-4_Audio#Sampling_Frequencies
        SampleRateToAdtsSamplingFrequencyIndex.put(44100,0x4);
        SampleRateToAdtsSamplingFrequencyIndex.put(8000,0xb);
    }

    /**
     * 添加AAC adts头到数据包
     * @param packet
     * @param packetLen
     */
    public static void addADTStoPacket(int sampleRate,byte[] packet, int packetLen) {
        int profile = MediaCodecInfo.CodecProfileLevel.AACObjectLC; // AAC LC
        int freqIdx = SampleRateToAdtsSamplingFrequencyIndex.get(sampleRate);
        int chanCfg = 1; // CPE
        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }
}
