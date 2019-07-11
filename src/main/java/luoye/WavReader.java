package luoye;

import java.io.*;

/**
 * @author luoyesiqiu
 */
public class WavReader {
    private RandomAccessFile randomAccessFile;

    public void seekToStart(){
        if(randomAccessFile!=null){
            try {
                randomAccessFile.seek(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void skipBytes(int bytes){
        if(randomAccessFile!=null){
            try {
                randomAccessFile.skipBytes(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void read(byte[] buffer,int pos,int len){
        if(randomAccessFile!=null){
            try {
                randomAccessFile.read(buffer,pos,len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public DataChunk readDataChunk() {
        char[] dataChunkID = new char[4]; //4
        int dataChunkSize = 0; //4
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            //discard non-data chunk
            do {
                dataChunkSize = 0;
                //big endian
                dataChunkID[0] = (char) randomAccessFile.readByte();
                dataChunkID[1] = (char) randomAccessFile.readByte();
                dataChunkID[2] = (char) randomAccessFile.readByte();
                dataChunkID[3] = (char) randomAccessFile.readByte();
                //little endian
                dataChunkSize |= (randomAccessFile.readByte() & 0xff);
                dataChunkSize |= ((randomAccessFile.readByte() << 8) & 0xff00);
                dataChunkSize |= ((randomAccessFile.readByte() << 16) & 0xff0000);
                dataChunkSize |= ((randomAccessFile.readByte() << 24) & 0xff000000);
                if(!new String(dataChunkID).equals("data")) {
                    randomAccessFile.skipBytes(dataChunkSize);
                }
            }
            while(!new String(dataChunkID).equals("data"));

            byte[] buf = new byte[8192];
            int len = -1;
            while((len = randomAccessFile.read(buf))!=-1){
                byteArrayOutputStream.write(buf,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new DataChunk(dataChunkID, dataChunkSize, byteArrayOutputStream.toByteArray());
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
            formatChunkID[0] = (char) randomAccessFile.readByte();
            formatChunkID[1] = (char) randomAccessFile.readByte();
            formatChunkID[2] = (char) randomAccessFile.readByte();
            formatChunkID[3] = (char) randomAccessFile.readByte();

            if(!new String(formatChunkID).equals("fmt ")){
                throw new IOException("read error: It not a format chunk.");
            }
            //little endian
            formatChunkSize |= (randomAccessFile.readByte() & 0xff);
            formatChunkSize |= ((randomAccessFile.readByte() << 8) & 0xff00);
            formatChunkSize |= ((randomAccessFile.readByte() << 16) & 0xff0000);
            formatChunkSize |= ((randomAccessFile.readByte() << 24) & 0xff000000);

            formatChunkSizeTotal = formatChunkSize;

            //little endian
            audioFormat |= (randomAccessFile.readByte() & 0xff);
            audioFormat |= ((randomAccessFile.readByte() << 8) & 0xff00);

            //little endian
            numChannels |= (randomAccessFile.readByte() & 0xff);
            numChannels |= ((randomAccessFile.readByte() << 8) & 0xff00);


            //little endian
            sampleRate |= (randomAccessFile.readByte() & 0xff);
            sampleRate |= ((randomAccessFile.readByte() << 8) & 0xff00);
            sampleRate |= ((randomAccessFile.readByte() << 16) & 0xff0000);
            sampleRate |= ((randomAccessFile.readByte() << 24) & 0xff000000);

            //little endian
            byteRate |= (randomAccessFile.readByte() & 0xff);
            byteRate |= ((randomAccessFile.readByte() << 8) & 0xff00);
            byteRate |= ((randomAccessFile.readByte() << 16) & 0xff0000);
            byteRate |= ((randomAccessFile.readByte() << 24) & 0xff000000);

            //little endian
            blockAlign |= (randomAccessFile.readByte() & 0xff);
            blockAlign |= ((randomAccessFile.readByte() << 8) & 0xff00);

            //little endian
            bitsPerSample |= (randomAccessFile.readByte() & 0xff);
            bitsPerSample |= ((randomAccessFile.readByte() << 8) & 0xff00);

            formatChunkSizeTotal -= 16;
            if (formatChunkSizeTotal > 0) {
                randomAccessFile.skipBytes(formatChunkSizeTotal);
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
            chunkId[0] = (char) randomAccessFile.readByte();
            chunkId[1] = (char) randomAccessFile.readByte();
            chunkId[2] = (char) randomAccessFile.readByte();
            chunkId[3] = (char) randomAccessFile.readByte();

            if(!new String(chunkId).startsWith("RIFF")){
                throw new IOException("read error: It not a riff chunk.");
            }
            //little endian
            chunkSize |= (randomAccessFile.readByte() & 0xff);
            chunkSize |= ((randomAccessFile.readByte() << 8) & 0xff00);
            chunkSize |= ((randomAccessFile.readByte() << 16) & 0xff0000);
            chunkSize |= ((randomAccessFile.readByte() << 24) & 0xff000000);
            //big endian
            chunkFormat[0] = (char) randomAccessFile.readByte();
            chunkFormat[1] = (char) randomAccessFile.readByte();
            chunkFormat[2] = (char) randomAccessFile.readByte();
            chunkFormat[3] = (char) randomAccessFile.readByte();
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
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public WavReader(String file) throws IOException {
        randomAccessFile = new RandomAccessFile(file,"r");
    }

    private WavReader() {
    }
}
