/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class LicenseHeaderUpdate {
    public static final String AGPL_LICENSE_TEXT = "The Kuali Financial System, a comprehensive financial management system for higher education.\n" +
        "\n" +
        "Copyright 2005-2017 Kuali, Inc.\n" +
        "\n" +
        "This program is free software: you can redistribute it and/or modify\n" +
        "it under the terms of the GNU Affero General Public License as\n" +
        "published by the Free Software Foundation, either version 3 of the\n" +
        "License, or (at your option) any later version.\n" +
        "\n" +
        "This program is distributed in the hope that it will be useful,\n" +
        "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
        "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
        "GNU Affero General Public License for more details.\n" +
        "\n" +
        "You should have received a copy of the GNU Affero General Public License\n" +
        "along with this program.  If not, see <http://www.gnu.org/licenses/>.";

    private final static Pattern RTRIM = Pattern.compile("\\s+$");

    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final List<String> blacklistedFiles = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        setupBlackList();
        handleJavaStyleComments(args[0]);
        handleJSPStyleComments(args[0]);
        handleXMLStyleComments(args[0]);
        handlePropertyStyleComments(args[0]);
        handleSQLStyleComments(args[0]);
    }

    public static void setupBlackList() {
        String sep = File.separator;
        blacklistedFiles.add("kfs-core" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "OJB-logging.properties");
        blacklistedFiles.add("kfs-core" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "OJB.properties");
        blacklistedFiles.add("kfs-core" + sep + "src" + sep + "main" + sep + "java" + sep + "org" + sep + "springframework" + sep + "beans" + sep + "factory" + sep + "support" + sep + "DefaultListableBeanFactory.java");
        blacklistedFiles.add("kfs-kns" + sep + "src" + sep + "main" + sep + "java" + sep + "org" + sep + "kuali" + sep + "kfs" + sep + "krad" + sep + "datadictionary" + sep + "DefaultListableBeanFactory.java");
    }

    public static void handleJavaStyleComments(String baseDir) throws Exception {
        IOFileFilter sourceFileFilter = FileFilterUtils.orFileFilter(
            FileFilterUtils.suffixFileFilter("java"),
            FileFilterUtils.suffixFileFilter("js"));
        sourceFileFilter = FileFilterUtils.orFileFilter(
            sourceFileFilter,
            FileFilterUtils.suffixFileFilter("jsx"));
        sourceFileFilter = FileFilterUtils.orFileFilter(
            sourceFileFilter,
            FileFilterUtils.suffixFileFilter("css"));
        sourceFileFilter = FileFilterUtils.orFileFilter(
            sourceFileFilter,
            FileFilterUtils.suffixFileFilter("groovy"));
        sourceFileFilter = FileFilterUtils.makeFileOnly(sourceFileFilter);

        LicensableFileDirectoryWalker dw = new LicensableFileDirectoryWalker(sourceFileFilter, "/*", " * ", " */");
        Collection<String> results = dw.run(baseDir);
        System.out.println(results);
    }

    public static void handleJSPStyleComments(String baseDir) throws Exception {
        IOFileFilter sourceFileFilter = FileFilterUtils.orFileFilter(
            FileFilterUtils.suffixFileFilter("jsp"),
            FileFilterUtils.suffixFileFilter("tag"));
        sourceFileFilter = FileFilterUtils.orFileFilter(
            sourceFileFilter,
            FileFilterUtils.suffixFileFilter("inc"));
        sourceFileFilter = FileFilterUtils.makeFileOnly(sourceFileFilter);

        LicensableFileDirectoryWalker dw = new LicensableFileDirectoryWalker(sourceFileFilter, "<%--", "   - ", "--%>");
        Collection<String> results = dw.run(baseDir);
        System.out.println(results);
    }

    /**
     * Do not add vm files to this set.  Velocity files do not support comments.
     */
    public static void handlePropertyStyleComments(String baseDir) throws Exception {
        IOFileFilter sourceFileFilter = FileFilterUtils.orFileFilter(
            FileFilterUtils.suffixFileFilter("properties"),
            FileFilterUtils.suffixFileFilter("cmd"));
        sourceFileFilter = FileFilterUtils.orFileFilter(
            sourceFileFilter,
            FileFilterUtils.suffixFileFilter("sh"));
        sourceFileFilter = FileFilterUtils.makeFileOnly(sourceFileFilter);

        LicensableFileDirectoryWalker dw = new LicensableFileDirectoryWalker(sourceFileFilter, "########################################", "# ", "########################################");
        Collection<String> results = dw.run(baseDir);
        System.out.println(results);
    }

    public static void handleSQLStyleComments(String baseDir) throws Exception {
        IOFileFilter sourceFileFilter = FileFilterUtils.suffixFileFilter("sql");
        sourceFileFilter = FileFilterUtils.makeFileOnly(sourceFileFilter);

        LicensableFileDirectoryWalker dw = new LicensableFileDirectoryWalker(sourceFileFilter, "--", "-- ", LINE_SEPARATOR);
        Collection<String> results = dw.run(baseDir);
        System.out.println(results);
    }

    public static void handleXMLStyleComments(String baseDir) throws Exception {
        IOFileFilter sourceFileFilter = FileFilterUtils.orFileFilter(
            FileFilterUtils.suffixFileFilter("xml"),
            FileFilterUtils.suffixFileFilter("jrxml"));
        sourceFileFilter = FileFilterUtils.orFileFilter(
            sourceFileFilter,
            FileFilterUtils.suffixFileFilter("html"));
        sourceFileFilter = FileFilterUtils.orFileFilter(
            sourceFileFilter,
            FileFilterUtils.suffixFileFilter("htm"));
        sourceFileFilter = FileFilterUtils.orFileFilter(
            sourceFileFilter,
            FileFilterUtils.suffixFileFilter("xsd"));
        sourceFileFilter = FileFilterUtils.orFileFilter(
            sourceFileFilter,
            FileFilterUtils.suffixFileFilter("tld"));
        sourceFileFilter = FileFilterUtils.makeSVNAware(sourceFileFilter);
        sourceFileFilter = FileFilterUtils.makeFileOnly(sourceFileFilter);

        LicensableFileDirectoryWalker dw = new LicensableFileDirectoryWalker(sourceFileFilter, "<!--", "   - ", " -->");
        Collection<String> results = dw.run(baseDir);
        System.out.println(results);
    }

    public static class LicensableFileDirectoryWalker extends DirectoryWalker {
        String firstLine;
        String lastLine;
        String linePrefix;

        public LicensableFileDirectoryWalker(IOFileFilter fileFilter, String firstLine, String linePrefix, String lastLine) {
            super(HiddenFileFilter.VISIBLE, fileFilter, 100);
            this.firstLine = firstLine;
            this.linePrefix = linePrefix;
            this.lastLine = lastLine;
        }

        @Override
        protected void handleDirectoryStart(File directory, int depth, @SuppressWarnings("rawtypes") Collection results) throws IOException {
            System.out.println("Directory: " + directory.getAbsolutePath());
        }

        @Override
        protected boolean handleDirectory(File directory, int depth, Collection results) throws IOException {
            if (directory.getAbsolutePath().contains("target")) {
                return false;
            }
            if (directory.getAbsolutePath().contains("node_modules")) {
                return false;
            }
            if (directory.getName().equals("META-INF")) {
                return false;
            }
            return true;
        }

        private String rtrim(String s) {
            return RTRIM.matcher(s).replaceAll("");
        }

        @Override
        protected void handleFile(File file, int depth, @SuppressWarnings("rawtypes") Collection results) throws IOException {
            if (isBlacklisted(file.getAbsolutePath())) {
                System.err.println("Found blacklisted file, skipping file: " + file.getAbsolutePath());
                results.add("FILE SKIPPED - BLACKLISTED: " + file.getAbsolutePath());
                return;
            }
            System.out.println("Handing File: " + file.getAbsolutePath());
            BufferedReader r = new BufferedReader(new FileReader(file));
            String currentLine = null;
            String savedFirstLine = null;
            // check the initial line of the file
            String line1 = r.readLine();
            // if the file is empty, just skip
            if (line1 == null) {
                results.add("FILE SKIPPED - EMPTY: " + file.getAbsolutePath());
                return;
            }
            if (line1.startsWith("<?")) { // special hack for XML files
                savedFirstLine = line1;
                line1 = r.readLine();
            }
            if (line1.trim().equals(firstLine.trim())) {
                // if found, skip until find a line containing the lastLine
                while ((currentLine = r.readLine()) != null) {
                    // throw away the lines
                    if (currentLine.trim().equals(lastLine.trim())) {
                        break;
                    }
                }
                if (currentLine == null) {
                    // we reached the end of the file before finding the end of the comment, ABORT!
                    System.err.println("Unable to find end of existing header section, skipping file: " + file.getAbsolutePath());
                    results.add("FILE SKIPPED - UNABLE TO FIND END OF HEADER: " + file.getAbsolutePath());
                    return;
                }
            }
            File outputFile = new File(file.getAbsolutePath() + "-out");
            BufferedWriter w = new BufferedWriter(new FileWriter(outputFile));
            if (savedFirstLine != null) {
                w.write(savedFirstLine);
                w.write(LINE_SEPARATOR);
            }
            // now, write the new header file
            w.write(firstLine);
            w.write(LINE_SEPARATOR);
            BufferedReader headerReader = new BufferedReader(new StringReader(AGPL_LICENSE_TEXT));
            while ((currentLine = headerReader.readLine()) != null) {
                StringBuffer s = new StringBuffer(linePrefix);
                s.append(currentLine);

                w.write(rtrim(s.toString()));
                w.write(LINE_SEPARATOR);
            }
            if (lastLine.equals(LINE_SEPARATOR)) { // special hack for SQL files
                w.write("--" + LINE_SEPARATOR);
                w.write(LINE_SEPARATOR);
            } else {
                w.write(lastLine);
                w.write(LINE_SEPARATOR);
            }
            headerReader.close();
            if (!line1.trim().equals(firstLine.trim())) {
                w.write(line1);
                w.write(LINE_SEPARATOR);
            }

            while ((currentLine = r.readLine()) != null) {
                w.write(rtrim(currentLine));
                w.write(LINE_SEPARATOR);
            }
            // delete the original file and replace with the completed output file
            w.close();
            r.close();
            FileUtils.deleteQuietly(file);
            FileUtils.moveFile(outputFile, file);
        }

        public Collection<String> run(String projectDir) throws IOException {
            Collection<String> results = new ArrayList<>();
            walk(new File(projectDir), results);
            return results;
        }

        protected boolean isBlacklisted(String file) {
            for (String str : blacklistedFiles) {
                if (file.endsWith(str)) {
                    return true;
                }
            }
            return false;
        }
    }
}
