package com.libraryapp.test.functional;

import static com.libraryapp.test.utils.TestUtils.businessTestFile;
import static com.libraryapp.test.utils.TestUtils.currentTest;
import static com.libraryapp.test.utils.TestUtils.testReport;
import static com.libraryapp.test.utils.TestUtils.yakshaAssert;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import mainapp.MyApp;

public class MainFunctionalTest {

    @AfterAll
    public static void afterAll() {
        testReport();
    }

    @Test
    @Order(1)
    public void testFileContentAfterChanges() throws IOException {
        try {
            // Check if the files have the correct content after the changes
            String checkFileContentResult = MyApp.checkFileContent();

            // Check if the reflog contains the commit message for corrected content
            String result = MyApp.checkReflogForCommitMessage("Corrected content in index.txt and about.txt");

            // Assert that the commit message is found in the reflog
            yakshaAssert(currentTest(), checkFileContentResult.equals("true") && result.equals("true"), businessTestFile);
        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }
    }

    @Test
    @Order(2)
    public void testHardResetAndReflog() throws IOException {
        try {
            // Step 1: Check if the commit hash of "Add index and about text files" exists
            String commitMessage = "Add index and about text files";
            String expectedHash = MyApp.getCommitHashForMessage(commitMessage);

            // Step 2: Check if "moving to <hash>" exists in the reflog
            String reflogResult = MyApp.checkReflogForMovingToHash(expectedHash);

            // Step 3: Ensure "Update content in index.txt and about.txt" is NOT in git log --oneline
            String gitLogResult = MyApp.checkGitLogForMessage("Update content in index.txt and about.txt");

            // Assert that the commit is NOT in the git log
            yakshaAssert(currentTest(), expectedHash != null && !expectedHash.isEmpty() && reflogResult.equals("true") && gitLogResult.equals("false"), businessTestFile);

        } catch (Exception ex) {
            // If any exception occurs, assert false
            yakshaAssert(currentTest(), false, businessTestFile);
        }
    }

    @Test
    @Order(3)
    public void testSoftResetAndReflogCommitMessage() throws IOException {
        try {
            // Step 1: Get the commit hash for "Add index and about text files"
            String commitMessage = "Add index and about text files";
            String expectedHash = MyApp.getCommitHashForMessage(commitMessage);

            // Step 2: Check if "moving to <hash>" exists in the reflog
            String reflogResult = MyApp.checkReflogForMovingToHash(expectedHash);

            // Step 3: Ensure "Add index and about text files" is STILL in git log --oneline after soft reset
            String gitLogResult = MyApp.checkGitLogForMessage(commitMessage);

            // Assert that the commit is still in the git log
            yakshaAssert(currentTest(), expectedHash != null && !expectedHash.isEmpty() && reflogResult.equals("true") && gitLogResult.equals("true"), businessTestFile);

        } catch (Exception ex) {
            // If any exception occurs, assert false
            yakshaAssert(currentTest(), false, businessTestFile);
        }
    }

    @Test
    @Order(4)
    public void testReflogForCorrectCommitMessage() throws IOException {
        try {
            // Commit message to search in the reflog
            String commitMessage = "Corrected content in index.txt and about.txt";
            
            // Check if the commit message exists in the git reflog
            String reflogResult = MyApp.checkReflogForCommitMessage(commitMessage);

            // Assert that the commit message is found in the reflog
            yakshaAssert(currentTest(), reflogResult.equals("true"), businessTestFile);

        } catch (Exception ex) {
            // If any exception occurs, assert false
            yakshaAssert(currentTest(), false, businessTestFile);
        }
    }
}
