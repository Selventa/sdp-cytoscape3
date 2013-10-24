package com.selventa.sdp;

import static java.lang.Integer.toHexString;
import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Util {

    private static MessageDigest md5;
    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@code true} if the platform is Windows; {@code false} otherwise.
     */
    public static boolean windows = System.getProperty("os.name").toLowerCase()
            .indexOf("win") >= 0;

    public static void makeExecutable(File f) {
        f.setExecutable(true, false);
    }

    public static File sdpCytoscapeLocation() {
        return new File(System.getProperty("user.home") + File.separator
                + "sdp-cytoscape");
    }

    public static boolean equalMD5(InputStream is1, InputStream is2)
            throws IOException {
        return md5(is1).equals(md5(is2));
    }

    /**
     * Returns the hexadecimal MD5 {@link String hash} of the
     * {@link InputStream} bytes.
     *
     * <p>
     * The {@link InputStream} is left open for the caller to manage.
     *
     * @param is
     *            {@link InputStream}
     * @return {@link String}
     * @throws IOException
     *             when an IO error occurs reading from {@code is}
     */
    public static String md5(InputStream is) throws IOException {
        requireNonNull(is);

        try {
            byte[] dataBytes = new byte[2048];
            int nread = 0;
            while ((nread = is.read(dataBytes)) != -1) {
                md5.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md5.digest();

            StringBuilder b = new StringBuilder(32);
            for (int i = 0; i < mdbytes.length; i++) {
                b.append(toHexString(0xFF & mdbytes[i]));
            }
            return b.toString();
        } finally {
            md5.reset();
        }
    }

    /**
     * Returns the {@link InputStream} for the {@link String classpathLocation}
     * found on the classpath. An absolute location on the classpath must begin
     * with a {@code /} otherwise the location will be interpreted relative to
     * {@link Util this class}.
     *
     * <p>
     * A {@code null} is returned if the resource at {@link String
     * classpathLocation} does not exist.
     *
     * @param classpathLocation
     *            {@link String}
     * @return {@link InputStream}
     */
    public static InputStream resource(String classpathLocation) {
        requireNonNull(classpathLocation);
        return Util.class.getResourceAsStream(classpathLocation);
    }

    public static void extract(File source, File dest) throws IOException {
        requireNonNull(source);
        requireNonNull(dest);
        if (!source.exists())
            throw new IllegalArgumentException("source does not exist");
        if (!source.canRead())
            throw new IllegalArgumentException("source file cannot be read");
        if (!dest.exists())
            throw new IllegalArgumentException("dest does not exist");
        if (!dest.isDirectory())
            throw new IllegalArgumentException("dest is not a directory");
        if (!dest.canWrite())
            throw new IllegalArgumentException(
                    "dest directory cannot be written to");

        try (ZipInputStream in = new ZipInputStream(new FileInputStream(source))) {
            ZipEntry entry = null;

            while ((entry = in.getNextEntry()) != null) {
                String name = entry.getName();

                // Open the output file
                if (entry.isDirectory()) {
                    new File(dest, name).mkdirs();
                } else {
                    try (OutputStream out = new FileOutputStream(new File(dest, name))) {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0)
                            out.write(buf, 0, len);
                    }
                }
            }
        }
    }

    public static void appendIfMissing(String line, File f) throws IOException {
        if (f == null) throw new NullPointerException();
        if (!f.canRead() || !f.canWrite())
            throw new IllegalStateException(
                    "Cannot read / write to "+ f.getAbsolutePath());

        // does the file contain the line in question?...
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(f));
            String l = null;
            while ((l = r.readLine()) != null) {
                if (l.equals(line)) return;
            }
        } finally {
            if (r != null)
                r.close();
        }

        // no, so append it
        PrintWriter w = null;
        try {
            w = new PrintWriter(new FileWriter(f, true));
            w.println(line);
        } finally {
            if (w != null)
                w.close();
        }
    }

    private Util() {

    }
}
