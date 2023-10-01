package com.intellectualcrafters.jnbt;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * This class writes <strong>NBT</strong>, or <strong>Named Binary Tag</strong>
 * {@code Tag} objects to an underlying {@code OutputStream}.
 *
 * <p> The NBT format was created by Markus Persson, and the specification may
 * be found at @linktourl http://www.minecraft.net/docs/NBT.txt
 * </p>
 *
 * @author Graham Edgecombe
 */
public final class NBTOutputStream implements Closeable {

    /**
     * The output stream.
     */
    private final DataOutputStream os;

    /**
     * Creates a new {@code NBTOutputStream}, which will write data to the
     * specified underlying output stream.
     *
     * @param os The output stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    public NBTOutputStream(OutputStream os) {
        this.os = new DataOutputStream(os);
    }

    /**
     * Writes a tag.
     *
     * @param tag The tag to write.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void writeTag(Tag tag) throws IOException {
        int type = NBTUtils.getTypeCode(tag.getClass());
        String name = tag.getName();
        byte[] nameBytes = name.getBytes(NBTConstants.CHARSET);
        this.os.writeByte(type);
        this.os.writeShort(nameBytes.length);
        this.os.write(nameBytes);
        if (type == NBTConstants.TYPE_END) {
            throw new IOException("Named TAG_End not permitted.");
        }
        writeTagPayload(tag);
    }

    /**
     * Writes tag payload.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeTagPayload(Tag tag) throws IOException {
        int type = NBTUtils.getTypeCode(tag.getClass());
        switch (type) {
            case NBTConstants.TYPE_END:
                writeEndTagPayload((EndTag) tag);
                break;
            case NBTConstants.TYPE_BYTE:
                writeByteTagPayload((ByteTag) tag);
                break;
            case NBTConstants.TYPE_SHORT:
                writeShortTagPayload((ShortTag) tag);
                break;
            case NBTConstants.TYPE_INT:
                writeIntTagPayload((IntTag) tag);
                break;
            case NBTConstants.TYPE_LONG:
                writeLongTagPayload((LongTag) tag);
                break;
            case NBTConstants.TYPE_FLOAT:
                writeFloatTagPayload((FloatTag) tag);
                break;
            case NBTConstants.TYPE_DOUBLE:
                writeDoubleTagPayload((DoubleTag) tag);
                break;
            case NBTConstants.TYPE_BYTE_ARRAY:
                writeByteArrayTagPayload((ByteArrayTag) tag);
                break;
            case NBTConstants.TYPE_STRING:
                writeStringTagPayload((StringTag) tag);
                break;
            case NBTConstants.TYPE_LIST:
                writeListTagPayload((ListTag) tag);
                break;
            case NBTConstants.TYPE_COMPOUND:
                writeCompoundTagPayload((CompoundTag) tag);
                break;
            case NBTConstants.TYPE_INT_ARRAY:
                writeIntArrayTagPayload((IntArrayTag) tag);
                break;
            default:
                throw new IOException("Invalid tag type: " + type + '.');
        }
    }

    /**
     * Writes a {@code TAG_Byte} tag.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeByteTagPayload(ByteTag tag) throws IOException {
        this.os.writeByte(tag.getValue());
    }

    /**
     * Writes a {@code TAG_Byte_Array} tag.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeByteArrayTagPayload(ByteArrayTag tag) throws IOException {
        byte[] bytes = tag.getValue();
        this.os.writeInt(bytes.length);
        this.os.write(bytes);
    }

    /**
     * Writes a {@code TAG_Compound} tag.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeCompoundTagPayload(CompoundTag tag) throws IOException {
        for (Tag childTag : tag.getValue().values()) {
            writeTag(childTag);
        }
        this.os.writeByte((byte) 0); // end tag - better way?
    }

    /**
     * Writes a {@code TAG_List} tag.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeListTagPayload(ListTag tag) throws IOException {
        Class<? extends Tag> clazz = tag.getType();
        List<Tag> tags = tag.getValue();
        int size = tags.size();
        this.os.writeByte(NBTUtils.getTypeCode(clazz));
        this.os.writeInt(size);
        for (Tag tag1 : tags) {
            writeTagPayload(tag1);
        }
    }

    /**
     * Writes a {@code TAG_String} tag.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeStringTagPayload(StringTag tag) throws IOException {
        byte[] bytes = tag.getValue().getBytes(NBTConstants.CHARSET);
        this.os.writeShort(bytes.length);
        this.os.write(bytes);
    }

    /**
     * Writes a {@code TAG_Double} tag.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeDoubleTagPayload(DoubleTag tag) throws IOException {
        this.os.writeDouble(tag.getValue());
    }

    /**
     * Writes a {@code TAG_Float} tag.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeFloatTagPayload(FloatTag tag) throws IOException {
        this.os.writeFloat(tag.getValue());
    }

    /**
     * Writes a {@code TAG_Long} tag.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeLongTagPayload(LongTag tag) throws IOException {
        this.os.writeLong(tag.getValue());
    }

    /**
     * Writes a {@code TAG_Int} tag.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeIntTagPayload(IntTag tag) throws IOException {
        this.os.writeInt(tag.getValue());
    }

    /**
     * Writes a {@code TAG_Short} tag.
     *
     * @param tag The tag.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeShortTagPayload(ShortTag tag) throws IOException {
        this.os.writeShort(tag.getValue());
    }

    /**
     * Writes a {@code TAG_Empty} tag.
     *
     * @param tag The tag.
     */
    private void writeEndTagPayload(EndTag tag) {
        /* empty */
    }

    private void writeIntArrayTagPayload(IntArrayTag tag) throws IOException {
        int[] data = tag.getValue();
        this.os.writeInt(data.length);
        for (int element : data) {
            this.os.writeInt(element);
        }
    }

    @Override
    public void close() throws IOException {
        this.os.close();
    }

    /**
     * Flush output.
     * @throws IOException
     */
    public void flush() throws IOException {
        this.os.flush();
    }
}
