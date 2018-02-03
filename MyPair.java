public class MyPair {
    private final Double key;
    private final String value;

    public MyPair(Double aKey, String aValue)
    {
        key   = aKey;
        value = aValue;
    }

    /**
     * TODO return the key of the pair
     *
     * @return key information
     */
    public Double key()   { return key; }

    /**
     * TODO return the value of the pair
     *
     * @return value information
     */
    public String value() { return value; }
}
