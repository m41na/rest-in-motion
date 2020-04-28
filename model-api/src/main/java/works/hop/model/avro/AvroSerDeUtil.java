package works.hop.model.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvroSerDeUtil {

    private AvroSerDeUtil() {
    }

    public static Schema loadSchemaFile(String file) throws IOException {
        Schema schema = new Schema.Parser().parse(file);
        return schema;
    }

    public static <A extends GenericRecord> void writeAvroFile(File file, Class<A> tClass, List<A> record) throws IOException {
        Schema schema = null;
        DatumWriter<GenericRecord> datumWriter = null;

        try {
            schema = tClass.newInstance().getSchema();
            datumWriter = new GenericDatumWriter<>(schema);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IOException(e);
        }

        try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(schema, file);
            for (A rec : record) {
                dataFileWriter.append(rec);
            }
        }
    }

    public static List<GenericRecord> readAvroFile(File file, Schema schema) throws IOException {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
        List<GenericRecord> ouputList = new ArrayList<>();
        try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(file, datumReader)) {
            GenericRecord record = null;
            while (dataFileReader.hasNext()) {
                record = dataFileReader.next(record);
                ouputList.add(record);
            }
        }
        return ouputList;
    }

    public static byte[] jsonToAvro(String json, Schema schema) throws IOException {
        DatumReader<Object> reader = new GenericDatumReader<>(schema);
        GenericDatumWriter<Object> writer = new GenericDatumWriter<>(schema);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Decoder decoder = DecoderFactory.get().jsonDecoder(schema, json);
            Encoder encoder = EncoderFactory.get().binaryEncoder(output, null);
            Object datum = reader.read(null, decoder);
            writer.write(datum, encoder);
            encoder.flush();
            return output.toByteArray();
        }
    }

    public static String avroToJson(byte[] avro, Schema schema) throws IOException {
        boolean pretty = false;
        GenericDatumReader<Object> reader = new GenericDatumReader<>(schema);
        DatumWriter<Object> writer = new GenericDatumWriter<>(schema);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            JsonEncoder encoder = EncoderFactory.get().jsonEncoder(schema, output, pretty);
            Decoder decoder = DecoderFactory.get().binaryDecoder(avro, null);
            Object datum = reader.read(null, decoder);
            writer.write(datum, encoder);
            encoder.flush();
            output.flush();
            return new String(output.toByteArray(), "UTF-8");
        }
    }

    public static <A extends GenericRecord> byte[] convertAvroPOJOtoByteArray(A avropojo, Schema schema) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            DatumWriter<A> writer = new SpecificDatumWriter<>(schema);
            writer.write(avropojo, encoder);
            encoder.flush();
            out.flush();
            return out.toByteArray();
        }
    }

    public static <A extends GenericRecord> A convertByteArraytoAvroPojo(byte[] bytes, Schema schema) throws IOException {
        Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        DatumReader<A> reader = new SpecificDatumReader<>(schema);
        return reader.read(null, decoder);
    }
}
