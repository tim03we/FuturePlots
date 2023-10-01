package com.intellectualcrafters.jnbt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helps create list tags.
 */
public class ListTagBuilder {

    private final Class<? extends Tag> type;
    private final List<Tag> entries;

    /**
     * Create a new instance.
     *
     * @param type of tag contained in this list
     */
    ListTagBuilder(Class<? extends Tag> type) {
        checkNotNull(type);
        this.type = type;
        this.entries = new ArrayList<>();
    }

    /**
     * Create a new builder instance.
     *
     * @param type
     *
     * @return a new builder
     */
    public static ListTagBuilder create(Class<? extends Tag> type) {
        return new ListTagBuilder(type);
    }

    /**
     * Create a new builder instance.
     *
     * @param entries
     * @param <T>
     *
     * @return a new builder
     */
    @SafeVarargs
    public static <T extends Tag> ListTagBuilder createWith(T... entries) {
        checkNotNull(entries);
        if (entries.length == 0) {
            throw new IllegalArgumentException("This method needs an array of at least one entry");
        }
        Class<? extends Tag> type = entries[0].getClass();
        for (int i = 1; i < entries.length; i++) {
            if (!type.isInstance(entries[i])) {
                throw new IllegalArgumentException("An array of different tag types was provided");
            }
        }
        ListTagBuilder builder = new ListTagBuilder(type);
        builder.addAll(Arrays.asList(entries));
        return builder;
    }

    /**
     * Add the given tag.
     *
     * @param value the tag
     *
     * @return this object
     */
    public ListTagBuilder add(Tag value) {
        checkNotNull(value);
        if (!this.type.isInstance(value)) {
            throw new IllegalArgumentException(value.getClass().getCanonicalName() + " is not of expected type " + this.type.getCanonicalName());
        }
        this.entries.add(value);
        return this;
    }

    /**
     * Add all the tags in the given list.
     *
     * @param value a list of tags
     *
     * @return this object
     */
    public ListTagBuilder addAll(Collection<? extends Tag> value) {
        checkNotNull(value);
        for (Tag v : value) {
            add(v);
        }
        return this;
    }

    /**
     * Build an unnamed list tag with this builder's entries.
     *
     * @return the new list tag
     */
    public ListTag build() {
        return new ListTag(this.type, new ArrayList<Tag>(this.entries));
    }

    /**
     * Build a new list tag with this builder's entries.
     *
     * @param name the name of the tag
     *
     * @return the created list tag
     */
    public ListTag build(String name) {
        return new ListTag(name, this.type, new ArrayList<Tag>(this.entries));
    }
}
