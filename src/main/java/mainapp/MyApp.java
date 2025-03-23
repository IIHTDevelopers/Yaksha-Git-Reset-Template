package mainapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;  // Added missing import for FileInputStream
import java.io.InputStreamReader;
import java.io.IOException; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyApp {

    public static String getCommitHashForMessage(String message) {
        try {
            System.out.println("Getting commit hash for message: " + message);

            // Execute 'git log --oneline' to get the commit history
            String gitLog = executeCommand("git log --oneline").trim();

            // Split the log into individual commits
            String[] commits = gitLog.split("\n");

            // Create a pattern to match the message
            Pattern pattern = Pattern.compile(Pattern.quote(message));

            // Loop through commits and match the message
            for (String commit : commits) {
                Matcher matcher = pattern.matcher(commit);
                if (matcher.find()) {
                    // Return the commit hash (first part of the line)
                    return commit.split(" ")[0];
                }
            }
            return null; // If not found
        } catch (Exception e) {
            System.out.println("Error in getCommitHashForMessage method: " + e.getMessage());
            return null;
        }
    }

    public static String checkReflogForMovingToHash(String hash) {
        try {
            System.out.println("Checking if 'moving to " + hash + "' is in the reflog...");

            // Get the reflog using 'git reflog'
            String reflog = executeCommand("git reflog").trim();

            // Create a pattern to match "moving to <hash>"
            String patternString = "moving to " + Pattern.quote(hash);
            Pattern pattern = Pattern.compile(patternString);

            // Check if the pattern is found in the reflog
            Matcher matcher = pattern.matcher(reflog);
            if (matcher.find()) {
                System.out.println("Found 'moving to " + hash + "' in reflog.");
                return "true";  // Found in reflog
            } else {
                System.out.println("'moving to " + hash + "' not found in reflog.");
                return "false"; // Not found in reflog
            }
        } catch (Exception e) {
            System.out.println("Error in checkReflogForMovingToHash method: " + e.getMessage());
            return "";
        }
    }

    public static String checkGitLogForMessage(String message) {
        try {
            System.out.println("Checking if commit message '" + message + "' exists in git log...");

            // Get the git log history using 'git log --oneline'
            String gitLog = executeCommand("git log --oneline").trim();

            // Create a pattern to match the commit message
            Pattern pattern = Pattern.compile(Pattern.quote(message));

            // Check if the message is found in the git log using pattern matching
            Matcher matcher = pattern.matcher(gitLog);
            if (matcher.find()) {
                System.out.println("Found commit message '" + message + "' in git log.");
                return "true";  // Found in git log
            } else {
                System.out.println("Commit message '" + message + "' NOT found in git log.");
                return "false"; // Not found in git log
            }
        } catch (Exception e) {
            System.out.println("Error in checkGitLogForMessage method: " + e.getMessage());
            return "";
        }
    }

    // Method to check if the files have the expected content after changes
    public static String checkFileContent() {
        try {
            System.out.println("Checking file contents...");

            // Read the content of the files
            String indexContent = readFileContent("index.txt").trim();
            String aboutContent = readFileContent("about.txt").trim();

            // Expected contents
            String expectedIndexContent = "Corrected content for the homepage";
            String expectedAboutContent = "Corrected about page content";

            // Log the actual contents read from the files
            System.out.println("Actual index.txt content: " + indexContent);
            System.out.println("Actual about.txt content: " + aboutContent);

            // Pattern matching to check if the actual content matches the expected content
            Pattern indexPattern = Pattern.compile(Pattern.quote(expectedIndexContent));
            Matcher indexMatcher = indexPattern.matcher(indexContent);

            Pattern aboutPattern = Pattern.compile(Pattern.quote(expectedAboutContent));
            Matcher aboutMatcher = aboutPattern.matcher(aboutContent);

            // Check if the content matches using pattern matching
            if (indexMatcher.find() && aboutMatcher.find()) {
                System.out.println("Both index.txt and about.txt content are correct.");
                return "true";  // Content matches
            } else {
                // Log what went wrong if the content does not match
                if (!indexMatcher.find()) {
                    System.out.println("index.txt content is incorrect. Expected: " + expectedIndexContent + ", Found: " + indexContent);
                }
                if (!aboutMatcher.find()) {
                    System.out.println("about.txt content is incorrect. Expected: " + expectedAboutContent + ", Found: " + aboutContent);
                }
                return "false"; // Content does not match
            }

        } catch (Exception e) {
            System.out.println("Error in checkFileContent method: " + e.getMessage());
            return "";
        }
    }

    // Helper method to read file content
    private static String readFileContent(String fileName) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        return content.toString();
    }

    // Method to check if the reflog contains a specific commit message
    public static String checkReflogForCommitMessage(String message) {
        try {
            System.out.println("Checking if commit message is in the reflog...");

            // Get the reflog using 'git reflog'
            String reflog = executeCommand("git reflog").trim();

            // Log the actual reflog content
            // System.out.println("Actual reflog content:\n" + reflog);

            // Pattern to match the commit message in the reflog
            Pattern messagePattern = Pattern.compile(Pattern.quote(message));
            Matcher messageMatcher = messagePattern.matcher(reflog);

            // Check if the specific commit message is in the reflog
            if (messageMatcher.find()) {
                System.out.println("Commit message '" + message + "' found in reflog.");
                return "true";  // Commit message found
            } else {
                System.out.println("Commit message '" + message + "' not found in the reflog.");
                return "false"; // Commit message not found
            }

        } catch (Exception e) {
            System.out.println("Error in checkReflogForCommitMessage method: " + e.getMessage());
            return "";
        }
    }

    // Helper method to execute git commands
    private static String executeCommand(String command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(".")); // Ensure this is the correct directory where Git repo is located
        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitVal = process.waitFor();
        if (exitVal == 0) {
            return output.toString();
        } else {
            System.out.println("Command failed with exit code: " + exitVal);
            throw new RuntimeException("Failed to execute command: " + command);
        }
    }

    public static void main(String[] args) {
        try {
            // Checking if the files have the expected content
            String fileCheckResult = checkFileContent();
            System.out.println("File contents check: " + fileCheckResult);

            // Checking if the reflog contains the specific commit message
            String reflogCheckResult = checkReflogForCommitMessage("Corrected content in index.txt and about.txt");
            System.out.println("Reflog contains the commit message: " + reflogCheckResult);

        } catch (Exception e) {
            System.out.println("Error in main method: " + e.getMessage());
        }
    }
}
