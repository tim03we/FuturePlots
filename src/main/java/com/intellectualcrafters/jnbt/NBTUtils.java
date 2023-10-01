package com.intellectualcrafters.jnbt;

import java.util.Map;

/**
 * A class which contains NBT-related utility methods.
 */
public final class NBTUtils {

    /**
     * Default private constructor.
     */
    private NBTUtils() {}

    /**
     * Gets the type name of a tag.
     *
     * @param clazz the tag class
     *
     * @return The type name.
     */
    public static String getTypeName(Class<? extends Tag> clazz) {
        if (clazz.equals(ByteArrayTag.class)) {
            return "TAG_Byte_Array";
        } else if (clazz.equals(ByteTag.class)) {
            return "TAG_Byte";
        } else if (clazz.equals(CompoundTag.class)) {
            return "TAG_Compound";
        } else if (clazz.equals(DoubleTag.class)) {
            return "TAG_Double";
        } else if (clazz.equals(EndTag.class)) {
            return "TAG_End";
        } else if (clazz.equals(FloatTag.class)) {
            return "TAG_Float";
        } else if (clazz.equals(IntTag.class)) {
            return "TAG_Int";
        } else if (clazz.equals(ListTag.class)) {
            return "TAG_List";
        } else if (clazz.equals(LongTag.class)) {
            return "TAG_Long";
        } else if (clazz.equals(ShortTag.class)) {
            return "TAG_Short";
        } else if (clazz.equals(StringTag.class)) {
            return "TAG_String";
        } else if (clazz.equals(IntArrayTag.class)) {
            return "TAG_Int_Array";
        } else {
            throw new IllegalArgumentException("Invalid tag class (" + clazz.getName() + ").");
        }
    }

    /**
     * Gets the type code of a tag class.
     *
     * @param clazz the tag class
     *
     * @return The type code.
     *
     * @throws IllegalArgumentException if the tag class is invalid.
     */
    public static int getTypeCode(Class<? extends Tag> clazz) {
        if (clazz.equals(ByteArrayTag.class)) {
            return NBTConstants.TYPE_BYTE_ARRAY;
        } else if (clazz.equals(ByteTag.class)) {
            return NBTConstants.TYPE_BYTE;
        } else if (clazz.equals(CompoundTag.class)) {
            return NBTConstants.TYPE_COMPOUND;
        } else if (clazz.equals(DoubleTag.class)) {
            return NBTConstants.TYPE_DOUBLE;
        } else if (clazz.equals(EndTag.class)) {
            return NBTConstants.TYPE_END;
        } else if (clazz.equals(FloatTag.class)) {
            return NBTConstants.TYPE_FLOAT;
        } else if (clazz.equals(IntTag.class)) {
            return NBTConstants.TYPE_INT;
        } else if (clazz.equals(ListTag.class)) {
            return NBTConstants.TYPE_LIST;
        } else if (clazz.equals(LongTag.class)) {
            return NBTConstants.TYPE_LONG;
        } else if (clazz.equals(ShortTag.class)) {
            return NBTConstants.TYPE_SHORT;
        } else if (clazz.equals(StringTag.class)) {
            return NBTConstants.TYPE_STRING;
        } else if (clazz.equals(IntArrayTag.class)) {
            return NBTConstants.TYPE_INT_ARRAY;
        } else {
            throw new IllegalArgumentException("Invalid tag class (" + clazz.getName() + ").");
        }
    }

    /**
     * Gets the class of a type of tag.
     *
     * @param type the type
     *
     * @return The class.
     *
     * @throws IllegalArgumentException if the tag type is invalid.
     */
    public static Class<? extends Tag> getTypeClass(int type) {
        switch (type) {
            case NBTConstants.TYPE_END:
                return EndTag.class;
            case NBTConstants.TYPE_BYTE:
                return ByteTag.class;
            case NBTConstants.TYPE_SHORT:
                return ShortTag.class;
            case NBTConstants.TYPE_INT:
                return IntTag.class;
            case NBTConstants.TYPE_LONG:
                return LongTag.class;
            case NBTConstants.TYPE_FLOAT:
                return FloatTag.class;
            case NBTConstants.TYPE_DOUBLE:
                return DoubleTag.class;
            case NBTConstants.TYPE_BYTE_ARRAY:
                return ByteArrayTag.class;
            case NBTConstants.TYPE_STRING:
                return StringTag.class;
            case NBTConstants.TYPE_LIST:
                return ListTag.class;
            case NBTConstants.TYPE_COMPOUND:
                return CompoundTag.class;
            case NBTConstants.TYPE_INT_ARRAY:
                return IntArrayTag.class;
            default:
                throw new IllegalArgumentException("Invalid tag type : " + type + ".");
        }
    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items    the map to read from
     * @param key      the key to look for
     * @param expected the expected NBT class type
     * @param <T>
     *
     * @return child tag
     */
    public static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected)
            throws IllegalArgumentException {
        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
}
