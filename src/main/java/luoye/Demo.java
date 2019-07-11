package luoye;


import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws IOException {
        String file = "audio/demo.wav";
        WavReader wavReader = new WavReader(file);
        WavReader.RIFFChunk riffChunk = wavReader.readRIFFChunk();
        System.out.println(">>[RIFFChunk]<<");
        System.out.println("ChunkID: " + new String(riffChunk.getChunkID()));
        System.out.println("ChunkSize: " + riffChunk.getChunkSize());
        System.out.println("Format: " + new String(riffChunk.getFormat()));
        WavReader.FormatChunk formatChunk = wavReader.readFormatChunk();
        System.out.println(">>[FormatChunk]<<");
        System.out.println("FormatChunkID: " + new String(formatChunk.getFormatChunkID()));
        System.out.println("FormatChunkSize: " + formatChunk.getFormatChunkSize());
        System.out.println("AudioFormat: " + formatChunk.getAudioFormat());
        System.out.println("NumChannels: " + formatChunk.getNumChannels());
        System.out.println("SampleRate: " + formatChunk.getSampleRate());
        System.out.println("ByteRate: " + formatChunk.getByteRate());
        System.out.println("BlockAlign: " + formatChunk.getBlockAlign());
        System.out.println("BitsPerSample: " + formatChunk.getBitsPerSample());
        WavReader.DataChunk dataChunk = wavReader.readDataChunk();
        System.out.println(">>[DataChunk]<<");
        System.out.println("DataChunkID: " + new String(dataChunk.getDataChunkID()));
        System.out.println("DataChunkSize: " + dataChunk.getDataChunkSize());
        System.out.println("data length: " + dataChunk.getData().length);
        wavReader.close();
    }
}
