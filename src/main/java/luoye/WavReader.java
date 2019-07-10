package luoye;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author luoyesiqiu
 */
public class WavReader {
    private String mFile;
    private DataInputStream dataInputStream;

    public DataChunk readDataChunk() {
        char[] dataChunkID = new char[4]; //4
        int dataChunkSize = 0; //4
        byte[] data = null; //dataChunkSize
        try {
            //big endian
            dataChunkID[0] = (char) dataInputStream.readByte();
            dataChunkID[1] = (char) dataInputStream.readByte();
            dataChunkID[2] = (char) dataInputStream.readByte();
            dataChunkID[3] = (char) dataInputStream.readByte();
            if(!new String(dataChunkID).equals("data")){
                throw new IOException("read error: It not a data chunk.");
            }
            //little endian
            dataChunkSize |= (dataInputStream.readByte() & 0xff);
            dataChunkSize |= ((dataInputStream.readByte() << 8) & 0xff00);
            dataChunkSize |= ((dataInputStream.readByte() << 16) & 0xff0000);
            dataChunkSize |= ((dataInputStream.readByte() << 24) & 0xff000000);
            data = new byte[dataChunkSize];
            for (int i = 0; i < dataChunkSize; i++) {
                data[i] = dataInputStream.readByte();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new DataChunk(dataChunkID, dataChunkSize, data);
    }

    public FormatChunk readFormatChunk() {
        char[] formatChunkID = new char[4]; //4
        int formatChunkSize = 0; //4
        int audioFormat = 0; //2
        int numChannels = 0; //2
        int sampleRate = 0; //4
        int byteRate = 0; //4
        int blockAlign = 0; //2
        int bitsPerSample = 0; //2
        int formatChunkSizeTotal = 0;
        try {
            //big endian
            formatChunkID[0] = (char) dataInputStream.readByte();
            formatChunkID[1] = (char) dataInputStream.readByte();
            formatChunkID[2] = (char) dataInputStream.readByte();
            formatChunkID[3] = (char) dataInputStream.readByte();

            if(!new String(formatChunkID).startsWith("fmt")){
                throw new IOException("read error: It not a format chunk.");
            }
            //little endian
            formatChunkSize |= (dataInputStream.readByte() & 0xff);
            formatChunkSize |= ((dataInputStream.readByte() << 8) & 0xff00);
            formatChunkSize |= ((dataInputStream.readByte() << 16) & 0xff0000);
            formatChunkSize |= ((dataInputStream.readByte() << 24) & 0xff000000);

            formatChunkSizeTotal = formatChunkSize;

            //little endian
            audioFormat |= (dataInputStream.readByte() & 0xff);
            audioFormat |= ((dataInputStream.readByte() << 8) & 0xff00);

            //little endian
            numChannels |= (dataInputStream.readByte() & 0xff);
            numChannels |= ((dataInputStream.readByte() << 8) & 0xff00);


            //little endian
            sampleRate |= (dataInputStream.readByte() & 0xff);
            sampleRate |= ((dataInputStream.readByte() << 8) & 0xff00);
            sampleRate |= ((dataInputStream.readByte() << 16) & 0xff0000);
            sampleRate |= ((dataInputStream.readByte() << 24) & 0xff000000);

            //little endian
            byteRate |= (dataInputStream.readByte() & 0xff);
            byteRate |= ((dataInputStream.readByte() << 8) & 0xff00);
            byteRate |= ((dataInputStream.readByte() << 16) & 0xff0000);
            byteRate |= ((dataInputStream.readByte() << 24) & 0xff000000);

            //little endian
            blockAlign |= (dataInputStream.readByte() & 0xff);
            blockAlign |= ((dataInputStream.readByte() << 8) & 0xff00);

            //little endian
            bitsPerSample |= (dataInputStream.readByte() & 0xff);
            bitsPerSample |= ((dataInputStream.readByte() << 8) & 0xff00);

            formatChunkSizeTotal -= 16;
            if (formatChunkSizeTotal > 0) {
                dataInputStream.skipBytes(formatChunkSizeTotal);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FormatChunk(formatChunkID, formatChunkSize, audioFormat, numChannels, sampleRate, byteRate, blockAlign, bitsPerSample);
    }

    public RIFFChunk readRIFFChunk() {
        char[] chunkId = new char[4]; //4
        int chunkSize = 0; //4
        char[] chunkFormat = new char[4]; //4
        try {
            //big endian
            chunkId[0] = (char) dataInputStream.readByte();
            chunkId[1] = (char) dataInputStream.readByte();
            chunkId[2] = (char) dataInputStream.readByte();
            chunkId[3] = (char) dataInputStream.readByte();

            if(!new String(chunkId).startsWith("RIFF")){
                throw new IOException("read error: It not a riff chunk.");
            }
            //little endian
            chunkSize |= (dataInputStream.readByte() & 0xff);
            chunkSize |= ((dataInputStream.readByte() << 8) & 0xff00);
            chunkSize |= ((dataInputStream.readByte() << 16) & 0xff0000);
            chunkSize |= ((dataInputStream.readByte() << 24) & 0xff000000);
            //big endian
            chunkFormat[0] = (char) dataInputStream.readByte();
            chunkFormat[1] = (char) dataInputStream.readByte();
            chunkFormat[2] = (char) dataInputStream.readByte();
            chunkFormat[3] = (char) dataInputStream.readByte();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new RIFFChunk(chunkId, chunkSize, chunkFormat);
    }

    public static class RIFFChunk {
        private char[] chunkID; //4

        public char[] getChunkID() {
            return chunkID;
        }


        public int getChunkSize() {
            return chunkSize;
        }


        public char[] getFormat() {
            return format;
        }

        public RIFFChunk(char[] chunkID, int chunkSize, char[] format) {
            this.chunkID = chunkID;
            this.chunkSize = chunkSize;
            this.format = format;
        }

        private int chunkSize; //4
        private char[] format; //4

    }

    public static class FormatChunk {

        private char[] formatChunkID; //4
        private int formatChunkSize; //4
        private int audioFormat; //2
        private int numChannels; //2
        private int sampleRate; //4
        private int byteRate; //4
        private int blockAlign; //2
        private int bitsPerSample; //2

        public FormatChunk(char[] formatChunkID, int formatChunkSize, int audioFormat, int numChannels, int sampleRate, int byteRate, int blockAlign, int bitsPerSample) {
            this.formatChunkID = formatChunkID;
            this.formatChunkSize = formatChunkSize;
            this.audioFormat = audioFormat;
            this.numChannels = numChannels;
            this.sampleRate = sampleRate;
            this.byteRate = byteRate;
            this.blockAlign = blockAlign;
            this.bitsPerSample = bitsPerSample;
        }

        public char[] getFormatChunkID() {
            return formatChunkID;
        }

        public int getFormatChunkSize() {
            return formatChunkSize;
        }


        public int getAudioFormat() {
            return audioFormat;
        }

        public int getNumChannels() {
            return numChannels;
        }


        public int getSampleRate() {
            return sampleRate;
        }


        public int getByteRate() {
            return byteRate;
        }

        public int getBlockAlign() {
            return blockAlign;
        }


        public int getBitsPerSample() {
            return bitsPerSample;
        }

    }


    public static class DataChunk {

        public DataChunk(char[] dataChunkID, int dataChunkSize, byte[] data) {
            this.dataChunkID = dataChunkID;
            this.dataChunkSize = dataChunkSize;
            this.data = data;
        }

        public char[] getDataChunkID() {
            return dataChunkID;
        }

        public int getDataChunkSize() {
            return dataChunkSize;
        }

        public byte[] getData() {
            return data;
        }

        private char[] dataChunkID; //4
        private int dataChunkSize; //4
        private byte[] data; //dataChunkSize
    }

    public void close() {
        if (dataInputStream != null) {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public WavReader(String file) throws IOException {
        this.mFile = file;
        dataInputStream = new DataInputStream(new FileInputStream(mFile));
    }

    private WavReader() {
    }
}
