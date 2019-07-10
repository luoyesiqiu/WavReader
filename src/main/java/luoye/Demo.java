package luoye;

import java.io.IOException;

public class Demo {
    public static void main(String[] args) {
        String file = "audio/demo.wav";
        try {
            WavReader wavReader = new WavReader(file);
            WavReader.RIFFChunk riffChunk = wavReader.readRIFFChunk();
            System.out.println(">>[RIFFChunk]<<");
            System.out.println("ChunkID: " + new String(riffChunk.getChunkID()));
            System.out.println("ChunkSize: " + riffChunk.getChunkSize());
            System.out.println("Format: " + new String(riffChunk.getFormat()));
            WavReader.FormatChunk formatChunk = wavReader.readFormatChunk();
            System.out.println(">>[FormatChunk]<<");
            System.out.println("SubChunk1ID: " +new String(formatChunk.getSubChunk1ID()));
            System.out.println("SubChunk1Size: " +formatChunk.getSubChunk1Size());
            System.out.println("AudioFormat: " +formatChunk.getAudioFormat());
            System.out.println("NumChannels: " +formatChunk.getNumChannels());
            System.out.println("SampleRate: " +formatChunk.getSampleRate());
            System.out.println("ByteRate: " +formatChunk.getByteRate());
            System.out.println("BlockAlign: " +formatChunk.getBlockAlign());
            System.out.println("BitsPerSample: " +formatChunk.getBitsPerSample());
            WavReader.DataChunk dataChunk = wavReader.readDataChunk();
            System.out.println(">>[DataChunk]<<");
            System.out.println("SubChunk2ID: " +new String(dataChunk.getSubChunk2ID()));
            System.out.println("SubChunk2Size: " +dataChunk.getSubChunk2Size());
            System.out.println("data length: " +dataChunk.getData().length);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
