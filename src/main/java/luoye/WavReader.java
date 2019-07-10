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
        char[] subChunk2ID = new char[4]; //4
        int subChunk2Size = 0; //4
        byte[] data = null; //subChunk2Size
        try {
            //big endian
            subChunk2ID[0] = (char) dataInputStream.readByte();
            subChunk2ID[1] = (char) dataInputStream.readByte();
            subChunk2ID[2] = (char) dataInputStream.readByte();
            subChunk2ID[3] = (char) dataInputStream.readByte();
            //little endian
            subChunk2Size |= (dataInputStream.readByte() & 0xff);
            subChunk2Size |= ((dataInputStream.readByte() << 8) & 0xff00);
            subChunk2Size |= ((dataInputStream.readByte() << 16) & 0xff0000);
            subChunk2Size |= ((dataInputStream.readByte() << 24) & 0xff000000);
            data = new byte[subChunk2Size];
            for (int i = 0; i < subChunk2Size; i++) {
                data[i] = dataInputStream.readByte();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new DataChunk(subChunk2ID, subChunk2Size, data);
    }

    public FormatChunk readFormatChunk() {
        char[] subChunk1ID = new char[4]; //4
        int subChunk1Size = 0; //4
        int audioFormat = 0; //2
        int numChannels = 0; //2
        int sampleRate = 0; //4
        int byteRate = 0; //4
        int blockAlign = 0; //2
        int bitsPerSample = 0; //2
        int subChunk1SizeTotal = 0;
        try {
            //big endian
            subChunk1ID[0] = (char) dataInputStream.readByte();
            subChunk1ID[1] = (char) dataInputStream.readByte();
            subChunk1ID[2] = (char) dataInputStream.readByte();
            subChunk1ID[3] = (char) dataInputStream.readByte();
            //little endian
            subChunk1Size |= (dataInputStream.readByte() & 0xff);
            subChunk1Size |= ((dataInputStream.readByte() << 8) & 0xff00);
            subChunk1Size |= ((dataInputStream.readByte() << 16) & 0xff0000);
            subChunk1Size |= ((dataInputStream.readByte() << 24) & 0xff000000);

            subChunk1SizeTotal = subChunk1Size;

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

            subChunk1SizeTotal -= 16;
            if (subChunk1SizeTotal > 0) {
                dataInputStream.skipBytes(subChunk1SizeTotal);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FormatChunk(subChunk1ID, subChunk1Size, audioFormat, numChannels, sampleRate, byteRate, blockAlign, bitsPerSample);
    }
    public RIFFChunk readRIFFChunk() {
        char[] chunkId = new char[4]; //4
        int chunkSize = 0; //4
        char[] chunkFormat = new char[4]; //4
        try {
            //big
            chunkId[0] = (char) dataInputStream.readByte();
            chunkId[1] = (char) dataInputStream.readByte();
            chunkId[2] = (char) dataInputStream.readByte();
            chunkId[3] = (char) dataInputStream.readByte();
            //little
            chunkSize |= (dataInputStream.readByte() & 0xff);
            chunkSize |= ((dataInputStream.readByte() << 8) & 0xff00);
            chunkSize |= ((dataInputStream.readByte() << 16) & 0xff0000);
            chunkSize |= ((dataInputStream.readByte() << 24) & 0xff000000);
            //big
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

        private char[] subChunk1ID; //4
        private int subChunk1Size; //4
        private int audioFormat; //2
        private int numChannels; //2
        private int sampleRate; //4
        private int byteRate; //4
        private int blockAlign; //2
        private int bitsPerSample; //2

        public FormatChunk(char[] subChunk1ID, int subChunk1Size, int audioFormat, int numChannels, int sampleRate, int byteRate, int blockAlign, int bitsPerSample) {
            this.subChunk1ID = subChunk1ID;
            this.subChunk1Size = subChunk1Size;
            this.audioFormat = audioFormat;
            this.numChannels = numChannels;
            this.sampleRate = sampleRate;
            this.byteRate = byteRate;
            this.blockAlign = blockAlign;
            this.bitsPerSample = bitsPerSample;
        }

        public char[] getSubChunk1ID() {
            return subChunk1ID;
        }

        public int getSubChunk1Size() {
            return subChunk1Size;
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

        public DataChunk(char[] subChunk2ID, int subChunk2Size, byte[] data) {
            this.subChunk2ID = subChunk2ID;
            this.subChunk2Size = subChunk2Size;
            this.data = data;
        }


        public char[] getSubChunk2ID() {
            return subChunk2ID;
        }

        public int getSubChunk2Size() {
            return subChunk2Size;
        }

        public byte[] getData() {
            return data;
        }

        private char[] subChunk2ID; //4
        private int subChunk2Size; //4
        private byte[] data; //subChunk2Size
    }


    public WavReader(String file) throws IOException {
        this.mFile = file;

        dataInputStream = new DataInputStream(new FileInputStream(mFile));
    }

    private WavReader() {
    }
}
