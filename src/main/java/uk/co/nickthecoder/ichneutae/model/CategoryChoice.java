package uk.co.nickthecoder.ichneutae.model;

import uk.co.nickthecoder.ichneutae.Category;

/**
 * Used during searching to show if each category is selected or not.
 */
public class CategoryChoice
{
    public Category category;
    
    public boolean selected;
    
    
    public CategoryChoice( Category category, boolean selected )
    {
        this.category = category;
        this.selected = selected;
    }
    
    public Category getCategory()
    {
        return this.category;
    }
    
    public boolean isSelected()
    {
        return this.selected;
    }
}
