package uk.co.nickthecoder.ichneutae.filter;

public class EqualsFilter<T> extends FluentFilter<T>
{
    private T matching;
    
    public EqualsFilter( T matching )
    {
        this.matching = matching;
    }
    
    @Override
    public boolean accept( T t )
    {
        if ((t == null) && (this.matching == null)) {
            return true;
        }
        
        if (t == null) {
            return false;
        }
        
        return t.equals(t);
    }
}
