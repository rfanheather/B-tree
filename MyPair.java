public class MyPair {
    private final Double key;
    private final String value;

    public MyPair(Double aKey, String aValue)
    {
        key   = aKey;
        value = aValue;
    }

    /**
     * return the key of the pair
     *
     * @return key information
     */
    public Double key()   { return key; }

    /**
     * return the value of the pair
     *
     * @return value information
     */
    public String value() { return value; }
}
