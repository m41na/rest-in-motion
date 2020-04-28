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

@org.apache.avro.specific.AvroGenerated
public class AProfile extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AProfile\",\"namespace\":\"works.hop.model.avro\",\"fields\":[{\"name\":\"firstName\",\"type\":[\"null\",\"string\"]},{\"name\":\"lastName\",\"type\":[\"null\",\"string\"]},{\"name\":\"age\",\"type\":[\"null\",\"int\"]},{\"name\":\"height\",\"type\":[\"null\",\"float\"]},{\"name\":\"registered\",\"type\":[\"null\",\"boolean\"]},{\"name\":\"phoneNumbers\",\"type\":{\"type\":\"array\",\"items\":[{\"type\":\"record\",\"name\":\"APhone\",\"fields\":[{\"name\":\"type\",\"type\":\"string\"},{\"name\":\"number\",\"type\":\"string\"}]}]}},{\"name\":\"integers\",\"type\":{\"type\":\"array\",\"items\":\"int\"}},{\"name\":\"strings\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},{\"name\":\"floats\",\"type\":{\"type\":\"array\",\"items\":\"float\"}},{\"name\":\"booleans\",\"type\":{\"type\":\"array\",\"items\":\"boolean\"}}]}");
    private static final long serialVersionUID = 1137685277759938013L;
    private static SpecificData MODEL$ = new SpecificData();
    private static final BinaryMessageEncoder<AProfile> ENCODER =
            new BinaryMessageEncoder<AProfile>(MODEL$, SCHEMA$);
    private static final BinaryMessageDecoder<AProfile> DECODER =
            new BinaryMessageDecoder<AProfile>(MODEL$, SCHEMA$);
    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<AProfile>
            WRITER$ = (org.apache.avro.io.DatumWriter<AProfile>) MODEL$.createDatumWriter(SCHEMA$);
    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<AProfile>
            READER$ = (org.apache.avro.io.DatumReader<AProfile>) MODEL$.createDatumReader(SCHEMA$);
    @Deprecated
    public java.lang.CharSequence firstName;
    @Deprecated
    public java.lang.CharSequence lastName;
    @Deprecated
    public java.lang.Integer age;
    @Deprecated
    public java.lang.Float height;
    @Deprecated
    public java.lang.Boolean registered;
    @Deprecated
    public java.util.List<java.lang.Object> phoneNumbers;
    @Deprecated
    public java.util.List<java.lang.Integer> integers;
    @Deprecated
    public java.util.List<java.lang.CharSequence> strings;
    @Deprecated
    public java.util.List<java.lang.Float> floats;
    @Deprecated
    public java.util.List<java.lang.Boolean> booleans;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public AProfile() {
    }

    /**
     * All-args constructor.
     *
     * @param firstName    The new value for firstName
     * @param lastName     The new value for lastName
     * @param age          The new value for age
     * @param height       The new value for height
     * @param registered   The new value for registered
     * @param phoneNumbers The new value for phoneNumbers
     * @param integers     The new value for integers
     * @param strings      The new value for strings
     * @param floats       The new value for floats
     * @param booleans     The new value for booleans
     */
    public AProfile(java.lang.CharSequence firstName, java.lang.CharSequence lastName, java.lang.Integer age, java.lang.Float height, java.lang.Boolean registered, java.util.List<java.lang.Object> phoneNumbers, java.util.List<java.lang.Integer> integers, java.util.List<java.lang.CharSequence> strings, java.util.List<java.lang.Float> floats, java.util.List<java.lang.Boolean> booleans) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.height = height;
        this.registered = registered;
        this.phoneNumbers = phoneNumbers;
        this.integers = integers;
        this.strings = strings;
        this.floats = floats;
        this.booleans = booleans;
    }

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     *
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<AProfile> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     *
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<AProfile> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     *
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<AProfile> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<AProfile>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Deserializes a AProfile from a ByteBuffer.
     *
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a AProfile instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static AProfile fromByteBuffer(
            java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    /**
     * Creates a new AProfile RecordBuilder.
     *
     * @return A new AProfile RecordBuilder
     */
    public static works.hop.model.avro.AProfile.Builder newBuilder() {
        return new works.hop.model.avro.AProfile.Builder();
    }

    /**
     * Creates a new AProfile RecordBuilder by copying an existing Builder.
     *
     * @param other The existing builder to copy.
     * @return A new AProfile RecordBuilder
     */
    public static works.hop.model.avro.AProfile.Builder newBuilder(works.hop.model.avro.AProfile.Builder other) {
        if (other == null) {
            return new works.hop.model.avro.AProfile.Builder();
        } else {
            return new works.hop.model.avro.AProfile.Builder(other);
        }
    }

    /**
     * Creates a new AProfile RecordBuilder by copying an existing AProfile instance.
     *
     * @param other The existing instance to copy.
     * @return A new AProfile RecordBuilder
     */
    public static works.hop.model.avro.AProfile.Builder newBuilder(works.hop.model.avro.AProfile other) {
        if (other == null) {
            return new works.hop.model.avro.AProfile.Builder();
        } else {
            return new works.hop.model.avro.AProfile.Builder(other);
        }
    }

    /**
     * Serializes this AProfile to a ByteBuffer.
     *
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
                return firstName;
            case 1:
                return lastName;
            case 2:
                return age;
            case 3:
                return height;
            case 4:
                return registered;
            case 5:
                return phoneNumbers;
            case 6:
                return integers;
            case 7:
                return strings;
            case 8:
                return floats;
            case 9:
                return booleans;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                firstName = (java.lang.CharSequence) value$;
                break;
            case 1:
                lastName = (java.lang.CharSequence) value$;
                break;
            case 2:
                age = (java.lang.Integer) value$;
                break;
            case 3:
                height = (java.lang.Float) value$;
                break;
            case 4:
                registered = (java.lang.Boolean) value$;
                break;
            case 5:
                phoneNumbers = (java.util.List<java.lang.Object>) value$;
                break;
            case 6:
                integers = (java.util.List<java.lang.Integer>) value$;
                break;
            case 7:
                strings = (java.util.List<java.lang.CharSequence>) value$;
                break;
            case 8:
                floats = (java.util.List<java.lang.Float>) value$;
                break;
            case 9:
                booleans = (java.util.List<java.lang.Boolean>) value$;
                break;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    /**
     * Gets the value of the 'firstName' field.
     *
     * @return The value of the 'firstName' field.
     */
    public java.lang.CharSequence getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the 'firstName' field.
     *
     * @param value the value to set.
     */
    public void setFirstName(java.lang.CharSequence value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the 'lastName' field.
     *
     * @return The value of the 'lastName' field.
     */
    public java.lang.CharSequence getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the 'lastName' field.
     *
     * @param value the value to set.
     */
    public void setLastName(java.lang.CharSequence value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the 'age' field.
     *
     * @return The value of the 'age' field.
     */
    public java.lang.Integer getAge() {
        return age;
    }

    /**
     * Sets the value of the 'age' field.
     *
     * @param value the value to set.
     */
    public void setAge(java.lang.Integer value) {
        this.age = value;
    }

    /**
     * Gets the value of the 'height' field.
     *
     * @return The value of the 'height' field.
     */
    public java.lang.Float getHeight() {
        return height;
    }

    /**
     * Sets the value of the 'height' field.
     *
     * @param value the value to set.
     */
    public void setHeight(java.lang.Float value) {
        this.height = value;
    }

    /**
     * Gets the value of the 'registered' field.
     *
     * @return The value of the 'registered' field.
     */
    public java.lang.Boolean getRegistered() {
        return registered;
    }

    /**
     * Sets the value of the 'registered' field.
     *
     * @param value the value to set.
     */
    public void setRegistered(java.lang.Boolean value) {
        this.registered = value;
    }

    /**
     * Gets the value of the 'phoneNumbers' field.
     *
     * @return The value of the 'phoneNumbers' field.
     */
    public java.util.List<java.lang.Object> getPhoneNumbers() {
        return phoneNumbers;
    }

    /**
     * Sets the value of the 'phoneNumbers' field.
     *
     * @param value the value to set.
     */
    public void setPhoneNumbers(java.util.List<java.lang.Object> value) {
        this.phoneNumbers = value;
    }

    /**
     * Gets the value of the 'integers' field.
     *
     * @return The value of the 'integers' field.
     */
    public java.util.List<java.lang.Integer> getIntegers() {
        return integers;
    }

    /**
     * Sets the value of the 'integers' field.
     *
     * @param value the value to set.
     */
    public void setIntegers(java.util.List<java.lang.Integer> value) {
        this.integers = value;
    }

    /**
     * Gets the value of the 'strings' field.
     *
     * @return The value of the 'strings' field.
     */
    public java.util.List<java.lang.CharSequence> getStrings() {
        return strings;
    }

    /**
     * Sets the value of the 'strings' field.
     *
     * @param value the value to set.
     */
    public void setStrings(java.util.List<java.lang.CharSequence> value) {
        this.strings = value;
    }

    /**
     * Gets the value of the 'floats' field.
     *
     * @return The value of the 'floats' field.
     */
    public java.util.List<java.lang.Float> getFloats() {
        return floats;
    }

    /**
     * Sets the value of the 'floats' field.
     *
     * @param value the value to set.
     */
    public void setFloats(java.util.List<java.lang.Float> value) {
        this.floats = value;
    }

    /**
     * Gets the value of the 'booleans' field.
     *
     * @return The value of the 'booleans' field.
     */
    public java.util.List<java.lang.Boolean> getBooleans() {
        return booleans;
    }

    /**
     * Sets the value of the 'booleans' field.
     *
     * @param value the value to set.
     */
    public void setBooleans(java.util.List<java.lang.Boolean> value) {
        this.booleans = value;
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

    /**
     * RecordBuilder for AProfile instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AProfile>
            implements org.apache.avro.data.RecordBuilder<AProfile> {

        private java.lang.CharSequence firstName;
        private java.lang.CharSequence lastName;
        private java.lang.Integer age;
        private java.lang.Float height;
        private java.lang.Boolean registered;
        private java.util.List<java.lang.Object> phoneNumbers;
        private java.util.List<java.lang.Integer> integers;
        private java.util.List<java.lang.CharSequence> strings;
        private java.util.List<java.lang.Float> floats;
        private java.util.List<java.lang.Boolean> booleans;

        /**
         * Creates a new Builder
         */
        private Builder() {
            super(SCHEMA$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         *
         * @param other The existing Builder to copy.
         */
        private Builder(works.hop.model.avro.AProfile.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.firstName)) {
                this.firstName = data().deepCopy(fields()[0].schema(), other.firstName);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.lastName)) {
                this.lastName = data().deepCopy(fields()[1].schema(), other.lastName);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
            if (isValidValue(fields()[2], other.age)) {
                this.age = data().deepCopy(fields()[2].schema(), other.age);
                fieldSetFlags()[2] = other.fieldSetFlags()[2];
            }
            if (isValidValue(fields()[3], other.height)) {
                this.height = data().deepCopy(fields()[3].schema(), other.height);
                fieldSetFlags()[3] = other.fieldSetFlags()[3];
            }
            if (isValidValue(fields()[4], other.registered)) {
                this.registered = data().deepCopy(fields()[4].schema(), other.registered);
                fieldSetFlags()[4] = other.fieldSetFlags()[4];
            }
            if (isValidValue(fields()[5], other.phoneNumbers)) {
                this.phoneNumbers = data().deepCopy(fields()[5].schema(), other.phoneNumbers);
                fieldSetFlags()[5] = other.fieldSetFlags()[5];
            }
            if (isValidValue(fields()[6], other.integers)) {
                this.integers = data().deepCopy(fields()[6].schema(), other.integers);
                fieldSetFlags()[6] = other.fieldSetFlags()[6];
            }
            if (isValidValue(fields()[7], other.strings)) {
                this.strings = data().deepCopy(fields()[7].schema(), other.strings);
                fieldSetFlags()[7] = other.fieldSetFlags()[7];
            }
            if (isValidValue(fields()[8], other.floats)) {
                this.floats = data().deepCopy(fields()[8].schema(), other.floats);
                fieldSetFlags()[8] = other.fieldSetFlags()[8];
            }
            if (isValidValue(fields()[9], other.booleans)) {
                this.booleans = data().deepCopy(fields()[9].schema(), other.booleans);
                fieldSetFlags()[9] = other.fieldSetFlags()[9];
            }
        }

        /**
         * Creates a Builder by copying an existing AProfile instance
         *
         * @param other The existing instance to copy.
         */
        private Builder(works.hop.model.avro.AProfile other) {
            super(SCHEMA$);
            if (isValidValue(fields()[0], other.firstName)) {
                this.firstName = data().deepCopy(fields()[0].schema(), other.firstName);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.lastName)) {
                this.lastName = data().deepCopy(fields()[1].schema(), other.lastName);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.age)) {
                this.age = data().deepCopy(fields()[2].schema(), other.age);
                fieldSetFlags()[2] = true;
            }
            if (isValidValue(fields()[3], other.height)) {
                this.height = data().deepCopy(fields()[3].schema(), other.height);
                fieldSetFlags()[3] = true;
            }
            if (isValidValue(fields()[4], other.registered)) {
                this.registered = data().deepCopy(fields()[4].schema(), other.registered);
                fieldSetFlags()[4] = true;
            }
            if (isValidValue(fields()[5], other.phoneNumbers)) {
                this.phoneNumbers = data().deepCopy(fields()[5].schema(), other.phoneNumbers);
                fieldSetFlags()[5] = true;
            }
            if (isValidValue(fields()[6], other.integers)) {
                this.integers = data().deepCopy(fields()[6].schema(), other.integers);
                fieldSetFlags()[6] = true;
            }
            if (isValidValue(fields()[7], other.strings)) {
                this.strings = data().deepCopy(fields()[7].schema(), other.strings);
                fieldSetFlags()[7] = true;
            }
            if (isValidValue(fields()[8], other.floats)) {
                this.floats = data().deepCopy(fields()[8].schema(), other.floats);
                fieldSetFlags()[8] = true;
            }
            if (isValidValue(fields()[9], other.booleans)) {
                this.booleans = data().deepCopy(fields()[9].schema(), other.booleans);
                fieldSetFlags()[9] = true;
            }
        }

        /**
         * Gets the value of the 'firstName' field.
         *
         * @return The value.
         */
        public java.lang.CharSequence getFirstName() {
            return firstName;
        }


        /**
         * Sets the value of the 'firstName' field.
         *
         * @param value The value of 'firstName'.
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder setFirstName(java.lang.CharSequence value) {
            validate(fields()[0], value);
            this.firstName = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'firstName' field has been set.
         *
         * @return True if the 'firstName' field has been set, false otherwise.
         */
        public boolean hasFirstName() {
            return fieldSetFlags()[0];
        }


        /**
         * Clears the value of the 'firstName' field.
         *
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder clearFirstName() {
            firstName = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'lastName' field.
         *
         * @return The value.
         */
        public java.lang.CharSequence getLastName() {
            return lastName;
        }


        /**
         * Sets the value of the 'lastName' field.
         *
         * @param value The value of 'lastName'.
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder setLastName(java.lang.CharSequence value) {
            validate(fields()[1], value);
            this.lastName = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'lastName' field has been set.
         *
         * @return True if the 'lastName' field has been set, false otherwise.
         */
        public boolean hasLastName() {
            return fieldSetFlags()[1];
        }


        /**
         * Clears the value of the 'lastName' field.
         *
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder clearLastName() {
            lastName = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        /**
         * Gets the value of the 'age' field.
         *
         * @return The value.
         */
        public java.lang.Integer getAge() {
            return age;
        }


        /**
         * Sets the value of the 'age' field.
         *
         * @param value The value of 'age'.
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder setAge(java.lang.Integer value) {
            validate(fields()[2], value);
            this.age = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /**
         * Checks whether the 'age' field has been set.
         *
         * @return True if the 'age' field has been set, false otherwise.
         */
        public boolean hasAge() {
            return fieldSetFlags()[2];
        }


        /**
         * Clears the value of the 'age' field.
         *
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder clearAge() {
            age = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        /**
         * Gets the value of the 'height' field.
         *
         * @return The value.
         */
        public java.lang.Float getHeight() {
            return height;
        }


        /**
         * Sets the value of the 'height' field.
         *
         * @param value The value of 'height'.
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder setHeight(java.lang.Float value) {
            validate(fields()[3], value);
            this.height = value;
            fieldSetFlags()[3] = true;
            return this;
        }

        /**
         * Checks whether the 'height' field has been set.
         *
         * @return True if the 'height' field has been set, false otherwise.
         */
        public boolean hasHeight() {
            return fieldSetFlags()[3];
        }


        /**
         * Clears the value of the 'height' field.
         *
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder clearHeight() {
            height = null;
            fieldSetFlags()[3] = false;
            return this;
        }

        /**
         * Gets the value of the 'registered' field.
         *
         * @return The value.
         */
        public java.lang.Boolean getRegistered() {
            return registered;
        }


        /**
         * Sets the value of the 'registered' field.
         *
         * @param value The value of 'registered'.
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder setRegistered(java.lang.Boolean value) {
            validate(fields()[4], value);
            this.registered = value;
            fieldSetFlags()[4] = true;
            return this;
        }

        /**
         * Checks whether the 'registered' field has been set.
         *
         * @return True if the 'registered' field has been set, false otherwise.
         */
        public boolean hasRegistered() {
            return fieldSetFlags()[4];
        }


        /**
         * Clears the value of the 'registered' field.
         *
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder clearRegistered() {
            registered = null;
            fieldSetFlags()[4] = false;
            return this;
        }

        /**
         * Gets the value of the 'phoneNumbers' field.
         *
         * @return The value.
         */
        public java.util.List<java.lang.Object> getPhoneNumbers() {
            return phoneNumbers;
        }


        /**
         * Sets the value of the 'phoneNumbers' field.
         *
         * @param value The value of 'phoneNumbers'.
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder setPhoneNumbers(java.util.List<java.lang.Object> value) {
            validate(fields()[5], value);
            this.phoneNumbers = value;
            fieldSetFlags()[5] = true;
            return this;
        }

        /**
         * Checks whether the 'phoneNumbers' field has been set.
         *
         * @return True if the 'phoneNumbers' field has been set, false otherwise.
         */
        public boolean hasPhoneNumbers() {
            return fieldSetFlags()[5];
        }


        /**
         * Clears the value of the 'phoneNumbers' field.
         *
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder clearPhoneNumbers() {
            phoneNumbers = null;
            fieldSetFlags()[5] = false;
            return this;
        }

        /**
         * Gets the value of the 'integers' field.
         *
         * @return The value.
         */
        public java.util.List<java.lang.Integer> getIntegers() {
            return integers;
        }


        /**
         * Sets the value of the 'integers' field.
         *
         * @param value The value of 'integers'.
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder setIntegers(java.util.List<java.lang.Integer> value) {
            validate(fields()[6], value);
            this.integers = value;
            fieldSetFlags()[6] = true;
            return this;
        }

        /**
         * Checks whether the 'integers' field has been set.
         *
         * @return True if the 'integers' field has been set, false otherwise.
         */
        public boolean hasIntegers() {
            return fieldSetFlags()[6];
        }


        /**
         * Clears the value of the 'integers' field.
         *
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder clearIntegers() {
            integers = null;
            fieldSetFlags()[6] = false;
            return this;
        }

        /**
         * Gets the value of the 'strings' field.
         *
         * @return The value.
         */
        public java.util.List<java.lang.CharSequence> getStrings() {
            return strings;
        }


        /**
         * Sets the value of the 'strings' field.
         *
         * @param value The value of 'strings'.
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder setStrings(java.util.List<java.lang.CharSequence> value) {
            validate(fields()[7], value);
            this.strings = value;
            fieldSetFlags()[7] = true;
            return this;
        }

        /**
         * Checks whether the 'strings' field has been set.
         *
         * @return True if the 'strings' field has been set, false otherwise.
         */
        public boolean hasStrings() {
            return fieldSetFlags()[7];
        }


        /**
         * Clears the value of the 'strings' field.
         *
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder clearStrings() {
            strings = null;
            fieldSetFlags()[7] = false;
            return this;
        }

        /**
         * Gets the value of the 'floats' field.
         *
         * @return The value.
         */
        public java.util.List<java.lang.Float> getFloats() {
            return floats;
        }


        /**
         * Sets the value of the 'floats' field.
         *
         * @param value The value of 'floats'.
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder setFloats(java.util.List<java.lang.Float> value) {
            validate(fields()[8], value);
            this.floats = value;
            fieldSetFlags()[8] = true;
            return this;
        }

        /**
         * Checks whether the 'floats' field has been set.
         *
         * @return True if the 'floats' field has been set, false otherwise.
         */
        public boolean hasFloats() {
            return fieldSetFlags()[8];
        }


        /**
         * Clears the value of the 'floats' field.
         *
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder clearFloats() {
            floats = null;
            fieldSetFlags()[8] = false;
            return this;
        }

        /**
         * Gets the value of the 'booleans' field.
         *
         * @return The value.
         */
        public java.util.List<java.lang.Boolean> getBooleans() {
            return booleans;
        }


        /**
         * Sets the value of the 'booleans' field.
         *
         * @param value The value of 'booleans'.
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder setBooleans(java.util.List<java.lang.Boolean> value) {
            validate(fields()[9], value);
            this.booleans = value;
            fieldSetFlags()[9] = true;
            return this;
        }

        /**
         * Checks whether the 'booleans' field has been set.
         *
         * @return True if the 'booleans' field has been set, false otherwise.
         */
        public boolean hasBooleans() {
            return fieldSetFlags()[9];
        }


        /**
         * Clears the value of the 'booleans' field.
         *
         * @return This builder.
         */
        public works.hop.model.avro.AProfile.Builder clearBooleans() {
            booleans = null;
            fieldSetFlags()[9] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public AProfile build() {
            try {
                AProfile record = new AProfile();
                record.firstName = fieldSetFlags()[0] ? this.firstName : (java.lang.CharSequence) defaultValue(fields()[0]);
                record.lastName = fieldSetFlags()[1] ? this.lastName : (java.lang.CharSequence) defaultValue(fields()[1]);
                record.age = fieldSetFlags()[2] ? this.age : (java.lang.Integer) defaultValue(fields()[2]);
                record.height = fieldSetFlags()[3] ? this.height : (java.lang.Float) defaultValue(fields()[3]);
                record.registered = fieldSetFlags()[4] ? this.registered : (java.lang.Boolean) defaultValue(fields()[4]);
                record.phoneNumbers = fieldSetFlags()[5] ? this.phoneNumbers : (java.util.List<java.lang.Object>) defaultValue(fields()[5]);
                record.integers = fieldSetFlags()[6] ? this.integers : (java.util.List<java.lang.Integer>) defaultValue(fields()[6]);
                record.strings = fieldSetFlags()[7] ? this.strings : (java.util.List<java.lang.CharSequence>) defaultValue(fields()[7]);
                record.floats = fieldSetFlags()[8] ? this.floats : (java.util.List<java.lang.Float>) defaultValue(fields()[8]);
                record.booleans = fieldSetFlags()[9] ? this.booleans : (java.util.List<java.lang.Boolean>) defaultValue(fields()[9]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (java.lang.Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

}










