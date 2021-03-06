package com.mygcc.datacollection;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;

public class CourseNameParserTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(CourseNameParser.class);
    }

    @Test
    public void testBio() {
        assert CourseNameParser.courseNameToReadable(
                        "GENERAL BIOLOGY I",
                        null).equals("Biology I");
    }

    @Test
    public void testLab() {
        assert CourseNameParser.courseNameToReadable(
                "LABORATORY",
                null).equals("Lab");
    }

    @Test
    public void testProgramming2() {
        assert CourseNameParser.courseNameToReadable(
                "COMP PROGRAMMING II",
                null).equals("Programming II");
    }

    @Test
    public void testCivLit() {
        assert CourseNameParser.courseNameToReadable(
                "CIV/LITERATURE",
                null).equals("Civ. Lit.");
    }

    @Test
    public void testDiscrete() {
        assert CourseNameParser.courseNameToReadable(
                "DISCRETE MATH/COMP SCI",
                null).equals("Discrete");
    }

    @Test
    public void testCalc3() {
        assert CourseNameParser.courseNameToReadable(
                "CALCULUS III",
                null).equals("Calculus III");
    }

    @Test
    public void testPrinciplesOfAccounting() {
        assert CourseNameParser.courseNameToReadable(
                "PRINCIPLES OF ACCOUNTING I",
                null).equals("Accounting I");
    }

    @Test
    public void testIntermediateAccounting() {
        assert CourseNameParser.courseNameToReadable(
                "INTERMEDIATE ACCOUNTING I",
                null).equals("Accounting I");
    }

    @Test
    public void testHistArt() {
        assert CourseNameParser.courseNameToReadable(
                "HIST & APPREC OF ART I",
                null).equals("Hist Of Art I");
    }

    @Test
    public void testGalleryArt() {
        assert CourseNameParser.courseNameToReadable(
                "STUDY: GALLERY STUDIES",
                null).equals("Gallery Studies");
    }

    @Test
    public void testWriting() {
        assert CourseNameParser.courseNameToReadable(
                "FOUNDATIONS OF ACADEMIC DISCOURSE",
                null).equals("Writing");
    }
}
