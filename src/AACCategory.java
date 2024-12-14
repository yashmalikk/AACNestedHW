import java.util.NoSuchElementException;
import edu.grinnell.csc207.util.*;

/**
 * Represents the mappings for a single page of items that should
 * be displayed.
 * 
 * Implements the AACPage interface.
 * 
 * @author Catie Baker & Sunjae Kim
 */
public class AACCategory implements AACPage {

  private String name;

  private AssociativeArray<String, String> items; // Stores image locations and their corresponding text

  /**
   * Creates a new empty category with the given name.
   * 
   * @param name the name of the category
   */
  public AACCategory(String name) {
    this.name = name;
    this.items = new AssociativeArray<>();
  }

  /**
   * Adds the image location, text pairing to the category.
   * 
   * @param imageLoc the location of the image
   * @param text     the text that image should speak
   */
  @Override
  public void addItem(String imageLoc, String text) {
    try {
      items.set(imageLoc, text);
    } catch (NullKeyException e) {
      System.err.println("Null key provided for imageLoc: " + imageLoc);
    }
  }

  /**
   * Returns an array of all the images in the category.
   * 
   * @return the array of image locations; if there are no images, it returns an
   *         empty array
   */
  @Override
  public String[] getImageLocs() {
    String[] imageLocs = new String[items.size()];
    for (int i = 0; i < items.size(); i++) {
      imageLocs[i] = items.getKey(i);
    }
    return imageLocs;
  }

  /**
   * Returns the name of the category.
   * 
   * @return the name of the category
   */
  @Override
  public String getCategory() {
    return this.name;
  }

  /**
   * Returns the text associated with the given image in this category.
   * 
   * @param imageLoc the location of the image
   * @return the text associated with the image
   * @throws NoSuchElementException if the image provided is not in the current
   *                                category
   */
  @Override
  public String select(String imageLoc) {
    try {
      return items.get(imageLoc); // Fetch the text associated with the image
    } catch (KeyNotFoundException e) {
      throw new NoSuchElementException("Image location not found: " + imageLoc);
    }
  }

  /**
   * Determines if the provided image is stored in the category.
   * 
   * @param imageLoc the location of the image
   * @return true if it is in the category, false otherwise
   */
  @Override
  public boolean hasImage(String imageLoc) {
    return items.hasKey(imageLoc);
  }
}