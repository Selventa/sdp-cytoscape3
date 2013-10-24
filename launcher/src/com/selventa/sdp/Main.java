package com.selventa.sdp;

import static com.selventa.sdp.Util.appendIfMissing;
import static com.selventa.sdp.Util.equalMD5;
import static com.selventa.sdp.Util.extract;
import static com.selventa.sdp.Util.makeExecutable;
import static com.selventa.sdp.Util.resource;
import static com.selventa.sdp.Util.windows;
import static java.lang.System.getProperty;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Launches cytoscape 3.0.
 *
 * <p>
 * The {@code user.home} system property is overridden to avoid clobbering
 * the {@code .cytoscape} folder.  This allows a user installation of Cytoscape
 * alongside this distribution.
 */
public class Main {

    /**
     * Root directory where this Cytoscape distribution will exist.
     */
    private static final String INSTALL_FOLDER = getProperty("user.home") +
            File.separator + "sdp-cytoscape3";
    /**
     * Zip file containing the packaged Cytoscape installation with custom
     * plugins.  This will get extracted and used as the Cytoscape
     * installation.
     */
    private static final String CY_ZIP = "cytoscape.zip";
    /**
     * Directory that results from extracting {@link #CY_ZIP}.
     */
    private static final String CY_FOLDER = "cytoscape";
    /**
     * The os-dependent name of the Cytoscape script.This is computed based
     * on {@link Util#windows}.
     */
    private static final String SCRIPT;
    /**
     * The os-dependent name of the Karaf script.This is computed based on
     * {@link Util#windows}.
     */
    private static final String KARAF_SCRIPT;

    /**
     * Compute os-independent values.
     */
    static {
        if (windows) {
            SCRIPT = "cytoscape.bat";
            KARAF_SCRIPT = "framework/bin/karaf.bat";
        } else {
            SCRIPT = "cytoscape.sh";
            KARAF_SCRIPT = "framework/bin/karaf";
        }
    }

    /**
     * Configure and fork the cytoscape process in an os-dependent manner.
     *
     * @param args {@code String[]}
     * @throws IOException when an IO error occurs reading, writing, or
     * starting the Cytoscape process
     * @throws InterruptedException when this process is interrupted waiting
     * for the Cytoscape process to finish
     */
    public static void main(String[] args) throws IOException,
            InterruptedException {
	    File cypath = new File(INSTALL_FOLDER);
	    if (!cypath.exists()) {
	        cypath.mkdirs();
	    }

	    File onDiskZip = new File(cypath, CY_ZIP);
	    boolean replace = false;
	    if (!onDiskZip.exists()) {
            replace = true;
	    } else {
	        try (InputStream inJar = resource("/" + CY_ZIP);
	                InputStream onDisk = new FileInputStream(onDiskZip)) {
	            if (!equalMD5(inJar, onDisk)) {
                    replace = true;
	            }
	        }
	    }

	    if (replace) {
	        try (InputStream inJar = resource("/" + CY_ZIP)) {
                Files.copy(inJar, onDiskZip.toPath(), REPLACE_EXISTING);
                extract(onDiskZip, cypath);
	        }
	    }

        File execFile = get(cypath.getAbsolutePath(), CY_FOLDER,
                SCRIPT).toFile();
        makeExecutable(execFile);
        File karafFile = get(cypath.getAbsolutePath(), CY_FOLDER,
                KARAF_SCRIPT).toFile();
        makeExecutable(karafFile);

        final Process process = new ProcessBuilder()
                .command(execFile.getAbsolutePath())
                .directory(get(cypath.getAbsolutePath(), CY_FOLDER).toFile())
                .start();
        int code = process.waitFor();
        System.exit(code);
	}
}
