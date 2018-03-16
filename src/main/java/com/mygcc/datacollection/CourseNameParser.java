package com.mygcc.datacollection;

import org.apache.commons.text.WordUtils;

/**
 * Course name parser converts myGCC course names to human-readable names.
 */
public final class CourseNameParser {
    /**
     * Terms to remove from course names.
     */
    private static final String[] DESCTERMS = {
            "accompanying",
            "administration in",
            "advanced",
            "adv",
            "analytical",
            "applications",
            "applied",
            "basic",
            "beginner",
            "beginning",
            "college",
            "culturally relevant",
            "early childhood",
            "elementary",
            "for engineers",
            "for mus ed majors",
            "foundations of",
            "foundations",
            "general",
            "intermediate",
            "intro to",
            "intro to the",
            "introduction to",
            "introduction to the",
            "math",
            "mthds of",
            "observational",
            "principles of",
            "prin of",
            "prin",
            "professional",
            "teaching",
            "tech in",
            "topics:",
            "topics in",
            "& apprec ",
            "comp ",

            "^study:",

            ":*",
            "-*",
    };

    /**
     * Converts a course name to a human readable string.
     * @param className class name (e.g. "GENERAL BIOLOGY I")
     * @param courseCode course code (e.g. "BIOL101A")
     * @return human readable course name
     */
    public static String courseNameToReadable(final String className,
                                              final String courseCode) {
        String repCN = className.toLowerCase();

        // Specific class name replacements
        repCN = repCN.replaceAll("civ/literature", "Civ. Lit.");
        repCN = repCN.replaceAll("civ/the arts", "Civ. Arts");
        repCN = repCN.replaceAll("civ/biblical revelation", "Bib. Rev.");
        repCN = repCN.replaceAll("foundations of academic discourse",
                "Writing");
        repCN = repCN.replaceAll("fitness [&|and] wellness .*", "Fitwell");

        // If there is a slash in the name
        String[] slashed = repCN.split("/");
        if (slashed.length > 1) {
            // Divide the last part of the string by spaces
            String[] words = repCN.trim().split(" ");
            if (words[words.length - 1].matches("i*$")) {
                repCN = slashed + " " + words[words.length - 1].toUpperCase();
            } else {
                repCN = slashed[0];
            }
        }

        // Remove all descriptive and unnecessary words
        for (String adj : DESCTERMS) {
            repCN = repCN.replaceAll(adj, "");
        }

        repCN = repCN.replaceAll("laboratory", "lab");
        repCN = repCN.replaceAll("literature", "lit.");
        repCN = repCN.replaceAll("psychology", "psych.");

        // Most general replacements
        repCN = repCN.replaceAll("(&|and).*?(?=I)", "");

        //Remove any words after "I" (e.g. Physics II-Engineering)
        repCN = repCN.replaceAll(" iii.*", " iii");
        repCN = repCN.replaceAll(" ii[^i].*", " ii");
        repCN = repCN.replaceAll(" i[^ii].*", " i");

        // Check if the last word is made up of i's and if so capitalize all i's
        // in the last word of the string
        String[] words = repCN.trim().split(" ");
        if (words[words.length - 1].matches("i*$")) {
            StringBuilder strBd = new StringBuilder();
            for (int i = 0; i < words.length - 1; i++) {
                strBd.append(WordUtils.capitalize(words[i])).append(" ");
            }
            return strBd
                    .append(words[words.length - 1]
                    .toUpperCase())
                    .toString();
        }

        return WordUtils.capitalizeFully(repCN.trim());
    }

    /**
     * Private constructor.
     */
    private CourseNameParser() {
    }
}
