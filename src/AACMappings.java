import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import edu.grinnell.csc207.util.*;

/**
 * This class represents a mapping for an AAC (Augmentative and Alternative Communication) system,
 * using a two-level mapping structure. The first level represents categories, and each category
 * stores images with corresponding text descriptions.
 * 
 * @authors Catie Baker & Yash Malik
 */
public class AACMappings implements AACPage {

  private AssociativeArray<String, AACCategory> categoryMap; // Stores all the categories
  private AssociativeArray<String, String> categoryLabels; // Links category identifiers to user-friendly names (e.g., "one" -> "fruit")
  private String activeCategory; // Tracks the currently active category

  /**
   * Initializes the object and loads categories and their associated images from a file.
   * 
   * @param filePath The path to the file containing the mappings data
   */
  public AACMappings(String filePath) {
    categoryMap = new AssociativeArray<>(); // Initialize the category map
    categoryLabels = new AssociativeArray<>(); // Initialize the map for category labels
    activeCategory = ""; // Initially no category is selected
    loadMappings(filePath); // Load the mappings from the provided file
  }

  /**
   * Reads and loads categories and their corresponding images from a specified file.
   * 
   * @param filePath The file containing the category and image data
   */
  private void loadMappings(String filePath) {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
      String currentLine;
      String pendingCategory = null;

      // Process each line of the file
      while ((currentLine = bufferedReader.readLine()) != null) {
        currentLine = currentLine.trim(); // Remove leading and trailing spaces

        if (!currentLine.startsWith(">")) {
          // The line indicates a new category
          String[] tokens = currentLine.split(" ", 2);
          if (tokens.length == 2) {
            pendingCategory = tokens[0]; // The first token is the category identifier
            String displayName = tokens[1]; // The second token is the display name (e.g., "fruit")
            categoryLabels.set(pendingCategory, displayName); // Save the category name

            // Add the category to the map if it doesn't exist yet
            if (!categoryMap.hasKey(pendingCategory)) {
              categoryMap.set(pendingCategory, new AACCategory(pendingCategory));
            }
          }
        } else {
          // The line represents an item under the current category
          if (pendingCategory != null) {
            currentLine = currentLine.substring(1); // Remove the ">" symbol
            String[] tokens = currentLine.split(" ", 2);
            if (tokens.length == 2) {
              String imgPath = tokens[0]; // The first part is the image path
              String caption = tokens[1]; // The second part is the caption text
              categoryMap.get(pendingCategory).addItem(imgPath, caption); // Add the image to the category
            }
          }
        }
      }
    } catch (IOException | NullKeyException | KeyNotFoundException e) {
      // Log errors encountered during file loading
      System.err.println("Error loading mappings: " + e.getMessage());
    }
  }

  /**
   * Adds a new image and its caption to the currently active category.
   * 
   * @param imgPath The path to the image
   * @param caption The text to be spoken when the image is selected
   * @throws NullKeyException if no category is currently active
   */
  @Override
  public void addItem(String imgPath, String caption) throws NullKeyException {
    if (activeCategory == null || activeCategory.isEmpty()) {
      // If no category is active, set the image path as the active category
      activeCategory = imgPath;

      // If the active category doesn't exist, create it
      if (!categoryMap.hasKey(activeCategory)) {
        categoryMap.set(activeCategory, new AACCategory(activeCategory));
      }
      return;
    }

    try {
      // Add the image and caption to the active category
      AACCategory selectedCategory = categoryMap.get(activeCategory);
      selectedCategory.addItem(imgPath, caption);
    } catch (KeyNotFoundException e) {
      throw new NoSuchElementException("Category '" + activeCategory + "' is not found.");
    }
  }

  /**
   * Retrieves the name of the currently selected category.
   * 
   * @return The display name of the active category
   */
  @Override
  public String getCategory() {
    try {
      return categoryLabels.get(activeCategory); // Fetch the user-friendly name of the category
    } catch (KeyNotFoundException e) {
      return ""; // Return an empty string if the category is not found
    }
  }

  /**
   * Retrieves all the image locations within the active category, or top-level categories
   * if no category is selected.
   * 
   * @return An array of image paths or category names
   */
  public String[] getImageLocs() {
    // If no category is selected, return the top-level categories
    if (activeCategory == null || activeCategory.isEmpty()) {
      return categoryMap.keys(); // Return all category keys
    }

    AACCategory selectedCategory;
    try {
      selectedCategory = categoryMap.get(activeCategory); // Retrieve the current active category
    } catch (KeyNotFoundException e) {
      return new String[] {}; // Return an empty array if no category is found
    }

    return selectedCategory.getImageLocs(); // Return the image paths within the category
  }

  /**
   * Resets the currently active category to no category selected.
   */
  public void reset() {
    activeCategory = ""; // Clear the active category
  }

  /**
   * Selects a category or image based on the given path, and returns the associated text.
   * 
   * @param imgPath The image or category path to select
   * @return The caption text if an image is selected, or an empty string if a category is selected
   * @throws NoSuchElementException if the selected category or image does not exist
   */
  public String select(String imgPath) {
    if (categoryMap.size() == 0) {
      throw new NoSuchElementException("No categories available.");
    }

    try {
      // Check if the selected path is a category
      if (categoryMap.hasKey(imgPath)) {
        if (activeCategory.equals(imgPath)) {
          throw new IllegalStateException("Category '" + imgPath + "' is already active.");
        }
        activeCategory = imgPath; // Set the category as active
        return ""; // Return an empty string for category selection
      }

      // If no category is selected, raise an error
      if (activeCategory == null || activeCategory.isEmpty()) {
        throw new NoSuchElementException("No category is currently active.");
      }

      // Check if the selected image exists within the active category
      AACCategory selectedCategory = categoryMap.get(activeCategory);
      if (selectedCategory.hasImage(imgPath)) {
        return selectedCategory.select(imgPath); // Return the associated caption
      } else {
        throw new NoSuchElementException("Image not found in category: " + imgPath);
      }

    } catch (KeyNotFoundException e) {
      throw new NoSuchElementException("Category '" + activeCategory + "' not found.");
    }
  }

  /**
   * Checks if a specific image is part of the currently active category.
   * 
   * @param imgPath The image path to check
   * @return true if the image exists in the current category, false otherwise
   */
  @Override
  public boolean hasImage(String imgPath) {
    try {
      if (categoryMap.hasKey(activeCategory)) {
        return categoryMap.get(activeCategory).hasImage(imgPath);
      }
    } catch (KeyNotFoundException e) {
      System.err.println("Error: Active category not found.");
    }
    return false;
  }

  /**
   * Determines if the given path corresponds to a category.
   * 
   * @param imgPath The image or category path
   * @return true if the path is a category, false otherwise
   */
  public boolean isCategory(String imgPath) {
    return categoryMap.hasKey(imgPath); // Check if the path corresponds to a category
  }

  /**
   * Saves the AAC mappings to a file.
   * 
   * @param filePath The file path where the mappings should be saved
   */
  public void writeToFile(String filePath) {
    try (FileWriter fileWriter = new FileWriter(filePath);
        PrintWriter printWriter = new PrintWriter(fileWriter)) {

      // Iterate through each category and save it to the file
      for (int i = 0; i < categoryMap.size(); i++) {
        String categoryKey = categoryMap.getKey(i); // Get the category identifier (e.g., "one")
        String categoryName = categoryLabels.get(categoryKey); // Get the category name (e.g., "fruit")

        // Write the category to the file (e.g., "one fruit")
        printWriter.println(categoryKey + " " + categoryName);

        // Retrieve the AACCategory object for this category
        AACCategory category = categoryMap.get(categoryKey);
        String[] imgPaths = category.getImageLocs(); // Get all image paths within the category

        // Write each image and its description to the file
        for (String imgPath : imgPaths) {
          String caption = category.select(imgPath); // Get the caption for the image
          printWriter.println(">" + imgPath + " " + caption); // Write the image and caption
        }
      }

    } catch (IOException | KeyNotFoundException e) {
      // Log any errors that occur during the file writing process
      System.err.println("Error saving mappings to file: " + e.getMessage());
    }
  }

  /**
   * Retrieves the names of all top-level categories.
   * 
   * @return An array of category names
   */
  public String[] getTopLevelCategories() {
    return categoryMap.keys(); // Return all the category names
  }
}