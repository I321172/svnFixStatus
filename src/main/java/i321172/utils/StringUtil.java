package i321172.utils;

public class StringUtil
{

    public static boolean isNullOrEmpty(String source)
    {
        return isNull(source) || isEmpty(source);
    }

    public static boolean isNull(String source)
    {
        return source == null;
    }

    public static boolean isEmpty(String source)
    {
        return source.equals("");
    }

}
