package i321172.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String getMatchString(String source, String pattern)
    {
        if (source != null)
        {
            Pattern pat = Pattern.compile(pattern);
            Matcher matcher = pat.matcher(source);

            if (matcher.find())
            {
                return source.substring(matcher.start(), matcher.end());
            }
        }

        return null;
    }

}
