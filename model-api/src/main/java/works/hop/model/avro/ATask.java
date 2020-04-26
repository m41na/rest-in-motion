/**
 * Autogenerated by Avro
 * <p>
 * DO NOT EDIT DIRECTLY
 */
package works.hop.model.avro;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;

@org.apache.avro.specific.AvroGenerated
public class ATask extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ATask\",\"namespace\":\"works.hop.model.avro\",\"fields\":[{\"name\":\"id\",\"type\":\"long\"},{\"name\":\"task\",\"type\":\"string\"},{\"name\":\"completed\",\"type\":\"boolean\"}]}");
    private static final long serialVersionUID = 1247910501024330408L;
    private static SpecificData MODEL$ = new SpecificData();
    private static final BinaryMessageEncoder<ATask> ENCODER =
            new BinaryMessageEncoder<ATask>(MODEL$, SCHEMA$);
    private static final BinaryMessageDecoder<ATask> DECODER =
            new BinaryMessageDecoder<ATask>(MODEL$, SCHEMA$);
    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<ATask>
            WRITER$ = (org.apache.avro.io.DatumWriter<ATask>) MODEL$.createDatumWriter(SCHEMA$);
    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<ATask>
            READER$ = (org.apache.avro.io.DatumReader<ATask>) MODEL$.createDatumReader(SCHEMA$);
    @Deprecated
    public long id;
    @Deprecated
    public java.lang.CharSequence task;
    @Deprecated
    public boolean completed;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public ATask() {
    }

    /**
     * All-args constructor.
     * @param id The new value for id
     * @param task The new value for task
     * @param completed The new value for completed
     */
    public ATask(java.lang.Long id, java.lang.CharSequence task, java.lang.Boolean completed) {
        this.id = id;
        this.task = task;
        this.completed = completed;
    }

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<ATask> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<ATask> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<ATask> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<ATask>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Deserializes a ATask from a ByteBuffer.
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a ATask instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static ATask fromByteBuffer(
            java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    /**
     * Creates a new ATask RecordBuilder.
     * @return A new ATask RecordBuilder
     */
    public static works.hop.model.avro.ATask.Builder newBuilder() {
        return new works.hop.model.avro.ATask.Builder();
    }

    /**
     * Creates a new ATask RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new ATask RecordBuilder
     */
    public static works.hop.model.avro.ATask.Builder newBuilder(works.hop.model.avro.ATask.Builder other) {
        if (other == null) {
            return new works.hop.model.avro.ATask.Builder();
        } else {
            return new works.hop.model.avro.ATask.Builder(other);
        }
    }

    /**
     * Creates a new ATask RecordBuilder by copying an existing ATask instance.
     * @param other The existing instance to copy.
     * @return A new ATask RecordBuilder
     */
    public static works.hop.model.avro.ATask.Builder newBuilder(works.hop.model.avro.ATask other) {
        if (other == null) {
            return new works.hop.model.avro.ATask.Builder();
        } else {
            return new works.hop.model.avro.ATask.Builder(other);
        }
    }

    /**
     * Serializes this ATask to a ByteBuffer.
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    public org.apache.avro.specific.SpecificData getSpecificData() {
        return MODEL$;
    }

    public org.apache.avro.Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0:
                return id;
            case 1:
                return task;
            case 2:
                return completed;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                id = (java.lang.Long) value$;
                break;
            case 1:
                task = (java.lang.CharSequence) value$;
                break;
            case 2:
                completed = (java.lang.Boolean) value$;
                break;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    /**
     * Gets the value of the 'id' field.
     * @return The value of the 'id' field.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the value of the 'id' field.
     * @param value the value to set.
     */
    public void setId(long value) {
        this.id = value;
    }

    /**
     * Gets the value of the 'task' field.
     * @return The value of the 'task' field.
     */
    public java.lang.CharSequence getTask() {
        return task;
    }

    /**
     * Sets the value of the 'task' field.
     * @param value the value to set.
     */
    public void setTask(java.lang.CharSequence value) {
        this.task = value;
    }

    /**
     * Gets the value of the 'completed' field.
     * @return The value of the 'completed' field.
     */
    public boolean getCompleted() {
        return completed;
    }

    /**
     * Sets the value of the 'completed' field.
     * @param value the value to set.
     */
    public void setCompleted(boolean value) {
        this.completed = value;
    }

    @Override
    public void writeExternal(java.io.ObjectOutput out)
            throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @Override
    public void readExternal(java.io.ObjectInput in)
            throws java.io.IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

    @Override
    protected boolean hasCustomCoders() {
        return true;
    }

    @Override
    public void customEncode(org.apache.avro.io.Encoder out)
            throws java.io.IOException {
        out.writeLong(this.id);

        out.writeString(this.task);

        out.writeBoolean(this.completed);

    }

    @Override
    public void customDecode(org.apache.avro.io.ResolvingDecoder in)
            throws java.io.IOException {
        org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
        if (fieldOrder == null) {
            this.id = in.readLong();

            this.task = in.readString(this.task instanceof Utf8 ? (Utf8) this.task : null);

            this.completed = in.readBoolean();

        } else {
            for (int i = 0; i < 3; i++) {
                switch (fieldOrder[i].pos()) {
                    case 0:
                        this.id = in.readLong();
                        break;

                    case 1:
                        this.task = in.readString(this.task instanceof Utf8 ? (Utf8) this.task : null);
                        break;

                    case 2:
                        this.completed = in.readBoolean();
                        break;

                    default:
                        throw new java.io.IOException("Corrupt ResolvingDecoder.");
                }
            }
        }
    }

    /**
     * RecordBuilder for ATask instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ATask>
            implements org.apache.avro.data.RecordBuilder<ATask> {

        private long id;
        private java.lang.CharSequence task;
        private boolean completed;

        /** Creates a new Builder */
        private Builder() {
            super(SCHEMA$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         * @param other The existing Builder to copy.
         */
        private Builder(works.hop.model.avro.ATask.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.id)) {
                this.id = data().deepCopy(fields()[0].schema(), other.id);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.task)) {
                this.task = data().deepCopy(fields()[1].schema(), other.task);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
            if (isValidValue(fields()[2], other.completed)) {
                this.completed = data().deepCopy(fields()[2].schema(), other.completed);
                fieldSetFlags()[2] = other.fieldSetFlags()[2];
            }
        }

        /**
         * Creates a Builder by copying an existing ATask instance
         * @param other The existing instance to copy.
         */
        private Builder(works.hop.model.avro.ATask other) {
            super(SCHEMA$);
            if (isValidValue(fields()[0], other.id)) {
                this.id = data().deepCopy(fields()[0].schema(), other.id);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.task)) {
                this.task = data().deepCopy(fields()[1].schema(), other.task);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.completed)) {
                this.completed = data().deepCopy(fields()[2].schema(), other.completed);
                fieldSetFlags()[2] = true;
            }
        }

        /**
         * Gets the value of the 'id' field.
         * @return The value.
         */
        public long getId() {
            return id;
        }


        /**
         * Sets the value of the 'id' field.
         * @param value The value of 'id'.
         * @return This builder.
         */
        public works.hop.model.avro.ATask.Builder setId(long value) {
            validate(fields()[0], value);
            this.id = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'id' field has been set.
         * @return True if the 'id' field has been set, false otherwise.
         */
        public boolean hasId() {
            return fieldSetFlags()[0];
        }


        /**
         * Clears the value of the 'id' field.
         * @return This builder.
         */
        public works.hop.model.avro.ATask.Builder clearId() {
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'task' field.
         * @return The value.
         */
        public java.lang.CharSequence getTask() {
            return task;
        }


        /**
         * Sets the value of the 'task' field.
         * @param value The value of 'task'.
         * @return This builder.
         */
        public works.hop.model.avro.ATask.Builder setTask(java.lang.CharSequence value) {
            validate(fields()[1], value);
            this.task = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'task' field has been set.
         * @return True if the 'task' field has been set, false otherwise.
         */
        public boolean hasTask() {
            return fieldSetFlags()[1];
        }


        /**
         * Clears the value of the 'task' field.
         * @return This builder.
         */
        public works.hop.model.avro.ATask.Builder clearTask() {
            task = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        /**
         * Gets the value of the 'completed' field.
         * @return The value.
         */
        public boolean getCompleted() {
            return completed;
        }


        /**
         * Sets the value of the 'completed' field.
         * @param value The value of 'completed'.
         * @return This builder.
         */
        public works.hop.model.avro.ATask.Builder setCompleted(boolean value) {
            validate(fields()[2], value);
            this.completed = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /**
         * Checks whether the 'completed' field has been set.
         * @return True if the 'completed' field has been set, false otherwise.
         */
        public boolean hasCompleted() {
            return fieldSetFlags()[2];
        }


        /**
         * Clears the value of the 'completed' field.
         * @return This builder.
         */
        public works.hop.model.avro.ATask.Builder clearCompleted() {
            fieldSetFlags()[2] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public ATask build() {
            try {
                ATask record = new ATask();
                record.id = fieldSetFlags()[0] ? this.id : (java.lang.Long) defaultValue(fields()[0]);
                record.task = fieldSetFlags()[1] ? this.task : (java.lang.CharSequence) defaultValue(fields()[1]);
                record.completed = fieldSetFlags()[2] ? this.completed : (java.lang.Boolean) defaultValue(fields()[2]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (java.lang.Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }
}










