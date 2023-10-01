package com.intellectualcrafters.json;

import java.io.IOException;
import java.io.Writer;

/**
 * JSONWriter provides a quick and convenient way of producing JSON text. The texts produced strictly conform to JSON
 * syntax rules. No whitespace is added, so the results are ready for transmission or storage. Each instance of
 * JSONWriter can produce one JSON text.
 *
 * A JSONWriter instance provides a <code>value</code> method for appending values to the text, and a <code>key</code>
 * method for adding keys before values in objects. There are <code>array</code> and <code>endArray</code> methods that
 * make and bound array values, and <code>object</code> and <code>endObject</code> methods which make and bound object
 * values. All of these methods return the JSONWriter instance, permitting a cascade style. For example,
 *
 *
 * <pre>
 * new JSONWriter(myWriter).object().key(&quot;JSON&quot;).value(&quot;Hello, World!&quot;).endObject();
 * </pre>
 *
 * which writes
 *
 *
 * <pre>
 * {"JSON":"Hello, World!"}
 * </pre>
 *
 * The first method called must be <code>array</code> or <code>object</code>. There are no methods for adding commas or
 * colons. JSONWriter adds them for you. Objects and arrays can be nested up to 20 levels deep.
 *
 * This can sometimes be easier than using a JSONObject to build a string.
 *
 * @author JSON.org
 * @version 2011-11-24
 */
public class JSONWriter {
    private static final int maxdepth = 200;
    /**
     * The writer that will receive the output.
     */
    protected final Writer writer;
    /**
     * The object/array stack.
     */
    private final JSONObject stack[];
    /**
     * The current mode. Values: 'a' (array), 'd' (done), 'i' (initial), 'k' (key), 'o' (object).
     */
    protected char mode;
    /**
     * The comma flag determines if a comma should be output before the next value.
     */
    private boolean comma;
    /**
     * The stack top index. A value of 0 indicates that the stack is empty.
     */
    private int top;
    
    /**
     * Make a fresh JSONWriter. It can be used to build one JSON text.
     */
    public JSONWriter(final Writer w) {
        comma = false;
        mode = 'i';
        stack = new JSONObject[maxdepth];
        top = 0;
        writer = w;
    }
    
    /**
     * Append a value.
     *
     * @param string A string value.
     *
     * @return this
     *
     * @throws JSONException If the value is out of sequence.
     */
    private JSONWriter append(final String string) throws JSONException {
        if (string == null) {
            throw new JSONException("Null pointer");
        }
        if ((mode == 'o') || (mode == 'a')) {
            try {
                if (comma && (mode == 'a')) {
                    writer.write(',');
                }
                writer.write(string);
            } catch (final IOException e) {
                throw new JSONException(e);
            }
            if (mode == 'o') {
                mode = 'k';
            }
            comma = true;
            return this;
        }
        throw new JSONException("Value out of sequence.");
    }
    
    /**
     * Begin appending a new array. All values until the balancing <code>endArray</code> will be appended to this array.
     * The <code>endArray</code> method must be called to mark the array's end.
     *
     * @return this
     *
     * @throws JSONException If the nesting is too deep, or if the object is started in the wrong place (for example as
     *                       a key or after the end of the outermost array or object).
     */
    public JSONWriter array() throws JSONException {
        if ((mode == 'i') || (mode == 'o') || (mode == 'a')) {
            push(null);
            append("[");
            comma = false;
            return this;
        }
        throw new JSONException("Misplaced array.");
    }
    
    /**
     * End something.
     *
     * @param mode Mode
     * @param c    Closing character
     *
     * @return this
     *
     * @throws JSONException If unbalanced.
     */
    private JSONWriter end(final char mode, final char c) throws JSONException {
        if (this.mode != mode) {
            throw new JSONException(mode == 'a' ? "Misplaced endArray." : "Misplaced endObject.");
        }
        pop(mode);
        try {
            writer.write(c);
        } catch (final IOException e) {
            throw new JSONException(e);
        }
        comma = true;
        return this;
    }
    
    /**
     * End an array. This method most be called to balance calls to <code>array</code>.
     *
     * @return this
     *
     * @throws JSONException If incorrectly nested.
     */
    public JSONWriter endArray() throws JSONException {
        return end('a', ']');
    }
    
    /**
     * End an object. This method most be called to balance calls to <code>object</code>.
     *
     * @return this
     *
     * @throws JSONException If incorrectly nested.
     */
    public JSONWriter endObject() throws JSONException {
        return end('k', '}');
    }
    
    /**
     * Append a key. The key will be associated with the next value. In an object, every value must be preceded by a
     * key.
     *
     * @param string A key string.
     *
     * @return this
     *
     * @throws JSONException If the key is out of place. For example, keys do not belong in arrays or if the key is
     *                       null.
     */
    public JSONWriter key(final String string) throws JSONException {
        if (string == null) {
            throw new JSONException("Null key.");
        }
        if (mode == 'k') {
            try {
                stack[top - 1].putOnce(string, Boolean.TRUE);
                if (comma) {
                    writer.write(',');
                }
                writer.write(JSONObject.quote(string));
                writer.write(':');
                comma = false;
                mode = 'o';
                return this;
            } catch (final IOException e) {
                throw new JSONException(e);
            }
        }
        throw new JSONException("Misplaced key.");
    }
    
    /**
     * Begin appending a new object. All keys and values until the balancing <code>endObject</code> will be appended to
     * this object. The <code>endObject</code> method must be called to mark the object's end.
     *
     * @return this
     *
     * @throws JSONException If the nesting is too deep, or if the object is started in the wrong place (for example as
     *                       a key or after the end of the outermost array or object).
     */
    public JSONWriter object() throws JSONException {
        if (mode == 'i') {
            mode = 'o';
        }
        if ((mode == 'o') || (mode == 'a')) {
            append("{");
            push(new JSONObject());
            comma = false;
            return this;
        }
        throw new JSONException("Misplaced object.");
    }
    
    /**
     * Pop an array or object scope.
     *
     * @param c The scope to close.
     *
     * @throws JSONException If nesting is wrong.
     */
    private void pop(final char c) throws JSONException {
        if (top <= 0) {
            throw new JSONException("Nesting error.");
        }
        final char m = stack[top - 1] == null ? 'a' : 'k';
        if (m != c) {
            throw new JSONException("Nesting error.");
        }
        top -= 1;
        mode = top == 0 ? 'd' : stack[top - 1] == null ? 'a' : 'k';
    }
    
    /**
     * Push an array or object scope.
     *
     * @param jo The scope to open.
     *
     * @throws JSONException If nesting is too deep.
     */
    private void push(final JSONObject jo) throws JSONException {
        if (top >= maxdepth) {
            throw new JSONException("Nesting too deep.");
        }
        stack[top] = jo;
        mode = jo == null ? 'a' : 'k';
        top += 1;
    }
    
    /**
     * Append either the value <code>true</code> or the value <code>false</code> .
     *
     * @param b A boolean.
     *
     * @return this
     *
     * @throws JSONException
     */
    public JSONWriter value(final boolean b) throws JSONException {
        return append(b ? "true" : "false");
    }
    
    /**
     * Append a double value.
     *
     * @param d A double.
     *
     * @return this
     *
     * @throws JSONException If the number is not finite.
     */
    public JSONWriter value(final double d) throws JSONException {
        return this.value(new Double(d));
    }
    
    /**
     * Append a long value.
     *
     * @param l A long.
     *
     * @return this
     *
     * @throws JSONException
     */
    public JSONWriter value(final long l) throws JSONException {
        return append(Long.toString(l));
    }
    
    /**
     * Append an object value.
     *
     * @param object The object to append. It can be null, or a Boolean, Number, String, JSONObject, or JSONArray, or an
     *               object that implements JSONString.
     *
     * @return this
     *
     * @throws JSONException If the value is out of sequence.
     */
    public JSONWriter value(final Object object) throws JSONException {
        return append(JSONObject.valueToString(object));
    }
}
