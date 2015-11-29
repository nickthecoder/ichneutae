package uk.co.nickthecoder.ichneutae.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexFilter extends FluentFilter<String>
{
    private Pattern pattern;

    public RegexFilter(String regex)
    {
        this(Pattern.compile(regex));
    }

    public RegexFilter(String regex, int flags)
    {
        this(Pattern.compile(regex, flags));
    }

    public RegexFilter(Pattern pattern)
    {
        this.pattern = pattern;
    }

    @Override
    public boolean accept(String string)
    {
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
}
