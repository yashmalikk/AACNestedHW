import java.util.NoSuchElementException;
import edu.grinnell.csc207.util.*;

/**
 * Represents the mappings for a specific category of items that should be displayed.
 * 
 * Implements the AACPage interface to define the operations for adding and selecting items.
 * 
 * @author Catie Baker & Yash Malik
 */
public class AACCategory implements AACPage {

  private String categoryName;

  private AssociativeArray<String, String> itemMappings; // Stores the mappings of image locations to their corresponding text descriptions

  /**
   * Initializes a new category with the specified name.
   * 
   * @param categoryName the name of the category
   */
  public AACCategory(String categoryName) {
    this.categoryName = categoryName;
    this.itemMappings = new AssociativeArray<>();
  }

  /**
   * Adds an image location and its corresponding text description to the category.
   * 
   * @param imageLocation the location of the image
   * @param description   the text that should be associated with the image
   */
  @Override
  public void addItem(String imageLocation, String description) {
    try {
      itemMappings.set(imageLocation, description);
    } catch (NullKeyException e) {
      System.err.println("Null key provided for imageLocation: " + imageLocation);
    }
  }

  /**
   * Retrieves an array of all image locations in the category.
   * 
   * @return an array of image locations; if no images exist, returns an empty array
   */
  @Override
  public String[] getImageLocs() {
    String[] imageLocations = new String[itemMappings.size()];
    for (int i = 0; i < itemMappings.size(); i++) {
      imageLocations[i] = itemMappings.getKey(i);
    }
    return imageLocations;
  }

  /**
   * Retrieves the name of the category.
   * 
   * @return the name of the category
   */
  @Override
  public String getCategory() {
    return this.categoryName;
  }

  /**
   * Retrieves the text associated with a given image in this category.
   * 
   * @param imageLocation the location of the image
   * @return the associated text description
   * @throws NoSuchElementException if the specified image is not found in the category
   */
  @Override
  public String select(String imageLocation) {
    try {
      return itemMappings.get(imageLocation); // Retrieve the text description for the image
    } catch (KeyNotFoundException e) {
      throw new NoSuchElementException("Image location not found: " + imageLocation);
    }
  }

  /**
   * Checks if the specified image is present in the category.
   * 
   * @param imageLocation the location of the image
   * @return true if the image is found in the category, false otherwise
   */
  @Override
  public boolean hasImage(String imageLocation) {
    return itemMappings.hasKey(imageLocation);
  }
}
