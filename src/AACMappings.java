import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import edu.grinnell.csc207.util.*;

/**
 * This class holds mappings for an AAC, which are two-level mappings. First
 * level is a category, and inside the
 * category, we store images that have text descriptions to speak.
 * 
 * @author Catie Baker & Yash Malik
 * 
 */
public class AACMappings implements AACPage {

  private AssociativeArray<String, AACCategory> categoryMap; // Stores all categories
  private AssociativeArray<String, String> categoryLabels; // Links a category to its name (e.g., "one" -> "fruit")
  private String activeCategory; // Stores the current selected category

  /**
   * Creates an object that loads categories and images from a file.
   * 
   * @param filePath the name of the file that has the mappings
   */
  public AACMappings(String filePath) {
    categoryMap = new AssociativeArray<>(); // Initialize category map
    categoryLabels = new AssociativeArray<>(); // Initialize category labels map
    activeCategory = ""; // Start with no selected category
    loadMappingsFromFile(filePath); // Load from the file
  }

  /**
   * Loads the categories and images from the file.
   * 
   * @param filePath The file that has the category and image data
   */
  private void loadMappingsFromFile(String filePath) {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
      String currentLine;
      String pendingCategory = null;

      // Read the file line by line
      while ((currentLine = bufferedReader.readLine()) != null) {
        currentLine = currentLine.trim(); // Remove spaces at the start and end

        if (!currentLine.startsWith(">")) {
          // This is a new category
          String[] tokens = currentLine.split(" ", 2);
          if (tokens.length == 2) {
            pendingCategory = tokens[0]; // First part is the category name
            String displayName = tokens[1]; // Second part is the label like "fruit"
            categoryLabels.set(pendingCategory, displayName); // Save category label

            // If the category doesn't exist, add it
            if (!categoryMap.hasKey(pendingCategory)) {
              categoryMap.set(pendingCategory, new AACCategory(pendingCategory));
            }
          }
        } else {
          // This is an item under the current category
          if (pendingCategory != null) {
            currentLine = currentLine.substring(1); // Remove the ">"
            String[] tokens = currentLine.split(" ", 2);
            if (tokens.length == 2) {
              String imgPath = tokens[0]; // First part is image location
              String caption = tokens[1]; // Second part is the description
              categoryMap.get(pendingCategory).addItem(imgPath, caption); // Add image to category
            }
          }
        }
      }
    } catch (IOException | NullKeyException | KeyNotFoundException e) {
      // Handle any error during loading
      System.err.println("Error loading mappings: " + e.getMessage());
    }
  }

  /**
   * Adds a new image and its text to the current category.
   * 
   * @param imgPath The location of the image
   * @param caption The text that should be spoken with the image
   * @throws NullKeyException if there is no selected category
   */
  @Override
  public void addItem(String imgPath, String caption) throws NullKeyException {
    if (activeCategory == null || activeCategory.isEmpty()) {
      // If no category is selected, set it to the image location
      activeCategory = imgPath;

      // If the category doesn't exist, add it
      if (!categoryMap.hasKey(activeCategory)) {
        categoryMap.set(activeCategory, new AACCategory(activeCategory));
      }
      return;
    }

    try {
      // Add the image to the selected category
      AACCategory selectedCategory = categoryMap.get(activeCategory);
      selectedCategory.addItem(imgPath, caption);
    } catch (KeyNotFoundException e) {
      throw new NoSuchElementException("Category '" + activeCategory + "' does not exist.");
    }
  }

  /**
   * Get the name of the currently selected category.
   * 
   * @return The name of the current category
   */
  @Override
  public String getCategory() {
    try {
      return categoryLabels.get(activeCategory); // Get the display name for the category
    } catch (KeyNotFoundException e) {
      return ""; // If no category is found, return an empty string
    }
  }

  /**
   * Get all the images in the current category, or top-level categories if no
   * category is selected.
   * 
   * @return an array of image locations or category names
   */
  public String[] getImageLocs() {
    // If no category is selected, return the top-level categories
    if (activeCategory == null || activeCategory.isEmpty()) {
      return categoryMap.keys(); // Get all the category names
    }

    AACCategory selectedCategory;
    try {
      selectedCategory = categoryMap.get(activeCategory); // Get the current category
    } catch (KeyNotFoundException e) {
      return new String[] {}; // Return an empty array if no category is found
    }

    return selectedCategory.getImageLocs(); // Return the images in the current category
  }

  /**
   * Reset the current category to none.
   */
  public void reset() {
    activeCategory = ""; // Clear the current category
  }

  /**
   * Choose a category or an image within a category.
   * 
   * @param imgPath The location of the image or category to select
   * @return If text is associated with the image, return it, otherwise return an
   *         empty string
   * @throws NoSuchElementException if the image or category is not found
   */
  public String select(String imgPath) {
    if (categoryMap.size() == 0) {
      throw new NoSuchElementException("No categories are available.");
    }

    try {
      // Check if the selected location is a top-level category
      if (categoryMap.hasKey(imgPath)) {
        if (activeCategory.equals(imgPath)) {
          throw new IllegalStateException("Category '" + imgPath + "' is already selected.");
        }
        activeCategory = imgPath; // Set as the current category
        return ""; // No text when selecting a category
      }

      // If no category is selected, throw an error
      if (activeCategory == null || activeCategory.isEmpty()) {
        throw new NoSuchElementException("No category is currently selected.");
      }

      // Get the selected image within the category
      AACCategory selectedCategory = categoryMap.get(activeCategory);
      if (selectedCategory.hasImage(imgPath)) {
        return selectedCategory.select(imgPath); // Get the text for the image
      } else {
        throw new NoSuchElementException("Image location not found in category: " + imgPath);
      }

    } catch (KeyNotFoundException e) {
      throw new NoSuchElementException("Category '" + activeCategory + "' does not exist.");
    }
  }

  /**
   * Check if an image is in the current category.
   * 
   * @param imgPath The location of the image
   * @return true if the image exists, false otherwise
   */
  @Override
  public boolean hasImage(String imgPath) {
    try {
      if (categoryMap.hasKey(activeCategory)) {
        return categoryMap.get(activeCategory).hasImage(imgPath);
      }
    } catch (KeyNotFoundException e) {
      System.err.println("Error: Category not found.");
    }
    return false;
  }

  /**
   * Check if the given location is a category or not.
   * 
   * @param imgPath The location of the image
   * @return true if it's a category, false if it's not
   */
  public boolean isCategory(String imgPath) {
    return categoryMap.hasKey(imgPath);
  }

  /**
   * Save the AAC mappings to a file.
   * 
   * @param filePath The file to save the mappings to
   */
  public void writeToFile(String filePath) {
    try (FileWriter fileWriter = new FileWriter(filePath);
        PrintWriter printWriter = new PrintWriter(fileWriter)) {

      // Iterate through each category in categoryMap
      for (int i = 0; i < categoryMap.size(); i++) {
        String categoryKey = categoryMap.getKey(i); // Get the category key (e.g., "one")
        String categoryName = categoryLabels.get(categoryKey); // Get the user-friendly category name (e.g., "fruit")

        // Write the category line (e.g., "one fruit")
        printWriter.println(categoryKey + " " + categoryName);

        // Get the AACCategory object for this category
        AACCategory category = categoryMap.get(categoryKey);
        String[] imgPaths = category.getImageLocs(); // Get all image locations within the category

        // Write each image and its associated description (e.g., ">a apple")
        for (String imgPath : imgPaths) {
          String caption = category.select(imgPath); // Get the description for the image
          printWriter.println(">" + imgPath + " " + caption); // Write the image and description
        }
      }

    } catch (IOException | KeyNotFoundException e) {
      // Handle exceptions during writing
      System.err.println("Error writing to file: " + e.getMessage());
    }
  }

  /**
   * Get all the top-level categories.
   * 
   * @return an array of category names
   */
  public String[] getTopLevelCategories() {
    return categoryMap.keys();
  }
}
