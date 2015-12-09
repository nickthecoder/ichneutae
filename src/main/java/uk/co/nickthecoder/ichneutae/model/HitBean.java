package uk.co.nickthecoder.ichneutae.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import org.apache.lucene.document.Document;

/**
 * A single search result i.e. a hit
 */

public class HitBean
{
    public QueryBean queryBean;

    public float score;

    private String summary;

    private Document document;

    public HitBean(QueryBean queryBean, Document document, float score)
    {
        this.queryBean = queryBean;
        this.document = document;
        this.score = score;
    }

    public int getScorePercentage()
    {
        return (int) (this.score * 100);
    }

    protected String getField(String fieldName)
    {
        try {
            String result = document.get(fieldName);
            return result == null ? "" : result;
        } catch (Exception e) {
            return "";
        }
    }

    public String getTitle()
    {
        return getField("title");
    }

    public String getURLString()
    {
        return getField("url");
    }

    public String getMimeType()
    {
        return getField("mimetype");
    }

    public String getContent()
    {
        return getField("content");
    }

    public String[] getCategoryCodes()
    {
        return this.document.getValues("category");
    }

    private String getSummary()
    {
        if (summary == null) {
            summary = createSummary();
        }
        return summary;
    }

    public String toString()
    {
        return "URL: " + this.getURLString() + "\nTitle: " + this.getTitle() + "\nSummary: " + this.getSummary();
    }
    
    private String createSummary()
    {
        // System.out.println( "Summary section for : " + getContent() );

        String content = getContent();
        String contentUpper = content.toUpperCase();
        TreeSet<SummarySection> matchedSections = new TreeSet<SummarySection>();

        for (Iterator<String> i = this.queryBean.words.iterator(); i.hasNext();) {
            String toMatch = i.next().toUpperCase();

            int matchIndex = contentUpper.indexOf(toMatch);
            if (matchIndex >= 0) {
                matchedSections.add(createSummarySection(content, toMatch, matchIndex));
            }
        }

        int previousEnd = -1;
        StringBuffer result = new StringBuffer();
        for (Iterator<SummarySection> i = matchedSections.iterator(); i.hasNext();) {
            SummarySection section = (SummarySection) i.next();

            if (previousEnd >= section.getFrom()) {
                if (previousEnd > section.getTo()) {
                    // Do nothing
                } else {
                    // Add just the extra bit
                    result.append(content.substring(previousEnd, section.getTo()));
                    previousEnd = section.getTo();
                }
            } else {

                if (section.getFrom() != 0) {
                    result.append(" ... ");
                }

                result.append(section.toString());
                previousEnd = section.getTo();

            }

        }
        if (previousEnd < content.length()) {
            result.append(" ... ");
        }

        // System.out.println( "Summary : "+ result );

        return result.toString();
    }

    private SummarySection createSummarySection(String content, String word, int matchIndex)
    {
        int start = walkBackwards(content, matchIndex);
        int end = walkForwards(content, matchIndex + word.length());

        return new SummarySection(content, start, end);
    }

    private int walkBackwards(String text, int matchIndex)
    {
        if (matchIndex <= 1) {
            return 0;
        }

        // Configuration information
        int requiredWords = 10;
        int requiredCharacters = 100;

        boolean prevWasSpace = false;

        int doneCharacters = 0;
        int doneWords = 0;

        int i = matchIndex - 1;
        while (i >= 0) {

            char c = text.charAt(i);

            // Whitespace
            if (Character.isWhitespace(c)) {
                if (!prevWasSpace) {
                    doneWords++;
                    if (doneWords > requiredWords) {
                        // System.out.println( "Stopped back after word : " +
                        // doneWords );
                        return i;
                    }

                }
                prevWasSpace = true;

            } else {

                doneCharacters++;
                if (doneCharacters > requiredCharacters) {
                    // System.out.println( "Stopped back after character : " +
                    // doneCharacters );
                    return i;
                }

                prevWasSpace = false;
            }

            // Full stop
            if ((c == '.') && prevWasSpace && (doneWords >= requiredWords)) {
                // System.out.println(
                // "Stopped back after full stop - words = : " + doneWords );
                return i;
            }

            i--;
        }

        // System.out.println( "Exited loop : i =  " + i );
        return 0;
    }

    private int walkForwards(String text, int matchIndex)
    {
        int end = text.length();
        if (matchIndex >= end - 1) {
            return end;
        }

        // Configuration information
        int requiredWords = 10;
        int requiredCharacters = 100;

        boolean prevWasSpace = false;
        boolean prevWasFullStop = false;

        int doneCharacters = 0;
        int doneWords = 0;

        int i = matchIndex;
        while (i < end) {

            char c = text.charAt(i);

            // Whitespace
            if (Character.isWhitespace(c)) {

                if (prevWasFullStop && (doneWords >= requiredWords)) {
                    // System.out.println(
                    // "Stopped forward after full stop - words = : " +
                    // doneWords );
                    return i - 1;
                }

                if (!prevWasSpace) {
                    doneWords++;
                    if (doneWords > requiredWords) {
                        // System.out.println( "Stopped back after word : " +
                        // doneWords );
                        return i;
                    }

                }
                prevWasSpace = true;

            } else {

                doneCharacters++;
                if (doneCharacters > requiredCharacters) {
                    // System.out.println( "Stopped back after character : " +
                    // doneCharacters );
                    return i;
                }

                prevWasSpace = false;
            }

            // Full stop
            prevWasFullStop = (c == '.');

            i++;
        }

        // System.out.println( "Exited loop : i =  " + i );
        return end;
    }

    /**
     * Returns an Iterator of SummarySection objects.
     */
    public Iterator<SummarySection> getSummarySections()
    {
        String summary = getSummary();
        String upperSummary = summary.toUpperCase();
        TreeSet<SummarySection> matchedSections = new TreeSet<SummarySection>();

        // System.out.println( "Matched Only Run" );

        for (Iterator<String> i = this.queryBean.words.iterator(); i.hasNext();) {
            String toMatch = ((String) i.next()).toUpperCase();

            int nextIndex = 0;
            while ((nextIndex = upperSummary.indexOf(toMatch, nextIndex)) >= 0) {
                matchedSections.add(new SummarySection(summary, nextIndex, nextIndex + toMatch.length(), true));
                nextIndex = nextIndex + toMatch.length();
            }
        }

        // System.out.println( "Final Run" );

        LinkedList<SummarySection> allSections = new LinkedList<SummarySection>();
        int doneIndex = 0;
        for (Iterator<SummarySection> i = matchedSections.iterator(); i.hasNext();) {
            SummarySection matchedSection = (SummarySection) i.next();

            if (matchedSection.getFrom() > doneIndex) {
                // Add an unmatched section.
                allSections.add(new SummarySection(summary, doneIndex, matchedSection.getFrom(), false));
                doneIndex = matchedSection.getFrom();
            }

            if (matchedSection.getTo() <= doneIndex) {
                // Throw it away (do nothing)
            } else {
                // Add the matched section
                allSections.add(new SummarySection(summary, doneIndex, matchedSection.getTo(), true));
                doneIndex = matchedSection.getTo();
            }

        }

        // Add the remaining unmatched section at the end.
        if (doneIndex < summary.length()) {
            allSections.add(new SummarySection(summary, doneIndex, summary.length(), false));
        }

        return allSections.iterator();
    }

    // -------------------- [[Inner Class SummarySection]] --------------------

    public class SummarySection implements Comparable<SummarySection>
    {

        private String _text;
        private int _from;
        private int _to;
        private boolean _isMatched;

        public SummarySection(String text, int from, int to)
        {
            this(text, from, to, true);
        }

        public SummarySection(String text, int from, int to, boolean isMatched)
        {
            _text = text;
            _from = from;
            _to = to;
            _isMatched = isMatched;
            // System.out.println( "From " + _from + " to " + _to + " - " +
            // _isMatched );
            // System.out.println( this );
        }

        public boolean isMatched()
        {
            return _isMatched;
        }

        public String toString()
        {
            return _text.substring(_from, _to);
        }

        public int getFrom()
        {
            return _from;
        }

        public int getTo()
        {
            return _to;
        }

        public int compareTo(SummarySection o)
        {
            return _from - o.getFrom();
        }

    }

    // -------- [[End Of Inner Class SummarySection]] --------

}
