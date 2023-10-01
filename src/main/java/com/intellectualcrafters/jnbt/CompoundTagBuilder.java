package com.intellectualcrafters.jnbt;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helps create compound tags.
 */
public class CompoundTagBuilder {

    private final Map<String, Tag> entries;

    /**
     * Create a new instance.
     */
    CompoundTagBuilder() {
        this.entries = new HashMap<>();
    }

    /**
     * Create a new instance and use the given map (which will be modified).
     *
     * @param value the value
     */
    CompoundTagBuilder(Map<String, Tag> value) {
        checkNotNull(value);
        this.entries = value;
    }

    /**
     * Create a new builder instance.
     *
     * @return a new builder
     */
    public static CompoundTagBuilder create() {
        return new CompoundTagBuilder();
    }

    /**
     * Put the given key and tag into the compound tag.
     *
     * @param key   they key
     * @param value the value
     *
     * @return this object
     */
    public CompoundTagBuilder put(String key, Tag value) {
        checkNotNull(key);
        checkNotNull(value);
        this.entries.put(key, value);
        return this;
    }

    /**
     * Put the given key and value into the compound tag as a {@code ByteArrayTag}.
     *
     * @param key   they key
     * @param value the value
     *
     * @return this object
     */
    public CompoundTagBuilder putByteArray(String key, byte[] value) {
        return put(key, new ByteArrayTag(key, value));
    }

    /**
     * Put the given key and value into the compound tag as a {@code ByteTag}.
     *
     * @param key   they key
     * @param value the value
     *
     * @return this object
     */
    public CompoundTagBuilder putByte(String key, byte value) {
        return put(key, new ByteTag(key, value));
    }

    /**
     * Put the given key and value into the compound tag as a {@code DoubleTag}.
     *
     * @param key   they key
     * @param value the value
     *
     * @return this object
     */
    public CompoundTagBuilder putDouble(String key, double value) {
        return put(key, new DoubleTag(key, value));
    }

    /**
     * Put the given key and value into the compound tag as a {@code FloatTag}.
     *
     * @param key   they key
     * @param value the value
     *
     * @return this object
     */
    public CompoundTagBuilder putFloat(String key, float value) {
        return put(key, new FloatTag(key, value));
    }

    /**
     * Put the given key and value into the compound tag as a {@code IntArrayTag}.
     *
     * @param key   they key
     * @param value the value
     *
     * @return this object
     */
    public CompoundTagBuilder putIntArray(String key, int[] value) {
        return put(key, new IntArrayTag(key, value));
    }

    /**
     * Put the given key and value into the compound tag as an {@code IntTag}.
     *
     * @param key   they key
     * @param value the value
     *
     * @return this object
     */
    public CompoundTagBuilder putInt(String key, int value) {
        return put(key, new IntTag(key, value));
    }

    /**
     * Put the given key and value into the compound tag as a {@code LongTag}.
     *
     * @param key   they key
     * @param value the value
     *
     * @return this object
     */
    public CompoundTagBuilder putLong(String key, long value) {
        return put(key, new LongTag(key, value));
    }

    /**
     * Put the given key and value into the compound tag as a {@code ShortTag}.
     *
     * @param key   they key
     * @param value the value
     *
     * @return this object
     */
    public CompoundTagBuilder putShort(String key, short value) {
        return put(key, new ShortTag(key, value));
    }

    /**
     * Put the given key and value into the compound tag as a {@code StringTag}.
     *
     * @param key   they key
     * @param value the value
     *
     * @return this object
     */
    public CompoundTagBuilder putString(String key, String value) {
        return put(key, new StringTag(key, value));
    }

    /**
     * Put all the entries from the given map into this map.
     *
     * @param value the map of tags
     *
     * @return this object
     */
    public CompoundTagBuilder putAll(Map<String, ? extends Tag> value) {
        checkNotNull(value);
        for (Map.Entry<String, ? extends Tag> entry : value.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Build an unnamed compound tag with this builder's entries.
     *
     * @return the new compound tag
     */
    public CompoundTag build() {
        return new CompoundTag(new HashMap<String, Tag>(this.entries));
    }

    /**
     * Build a new compound tag with this builder's entries.
     *
     * @param name the name of the tag
     *
     * @return the created compound tag
     */
    public CompoundTag build(String name) {
        return new CompoundTag(name, new HashMap<String, Tag>(this.entries));
    }
}
