package com.selventa.sdp;

import static com.selventa.sdp.Util.equalMD5;
import static com.selventa.sdp.Util.extract;
import static com.selventa.sdp.Util.makeExecutable;
import static com.selventa.sdp.Util.resource;
import static com.selventa.sdp.Util.windows;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

/**
 * Launches cytoscape with SDP / OpenBEL plugins.
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
     * Environment variable for terminal setting (unix / linux).
     */
    private static final String TERMINAL_ENV = "TERMINAL";

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
    public static void main(String[] args) {
	    File cypath = new File(INSTALL_FOLDER);
	    if (!cypath.exists()) {
	        cypath.mkdirs();
	    }

        final Log log = new Log(new File(cypath, "scy3-launch.log"));
        log.info(format("Setup installation folder - %s", cypath.getAbsolutePath()));

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
            } catch (IOException e) {
                log.warn("Failure to compare hash of zip file; try to replace", e);
                replace = true;
            }
	    }

	    if (replace) {
            log.info(format("Update of zip file required - %s", onDiskZip.getAbsolutePath()));
	        try (InputStream inJar = resource("/" + CY_ZIP)) {
                Files.copy(inJar, onDiskZip.toPath(), REPLACE_EXISTING);
                extract(onDiskZip, cypath);
                log.info("Successfully updated to latest zip file");
            } catch (IOException e) {
                log.fail(format("Failure to replace zip file - %s", onDiskZip.getAbsolutePath()));
                log.fail("Launch continuing with old cytoscape installation");
            }
	    } else {
            log.info(format("Existing zip file is the latest - %s", onDiskZip.getAbsolutePath()));
        }

        File execFile = get(cypath.getAbsolutePath(), CY_FOLDER,
                SCRIPT).toFile();
        makeExecutable(execFile);
        log.dbug(format("Set executable for file - %s", execFile.getAbsolutePath()));
        File karafFile = get(cypath.getAbsolutePath(), CY_FOLDER,
                KARAF_SCRIPT).toFile();
        makeExecutable(karafFile);
        log.dbug(format("Set executable for file - %s", karafFile.getAbsolutePath()));

        File wd = get(cypath.getAbsolutePath(), CY_FOLDER).toFile();
        final Process process;
        try {
            process = makeProcess(wd, execFile, log);
            log.info("Started process successfully; waiting for process to end");
            int code = process.waitFor();
            log.info(format("Process ended, happily? (code %d)", code));
            System.exit(code);
        } catch (IOException e) {
            log.fail(format("Cannot start process; exiting (code 100)"));
            System.exit(100);
        } catch (InterruptedException e) {
            log.fail("Process interrupted", e);
        }
    }

    private static Process makeProcess(File workingDirectory, File scriptFile, Log log) throws IOException {
        final ProcessBuilder bldr = new ProcessBuilder().directory(workingDirectory);
        if (windows) {
            String osName = System.getProperty("os.name");
            if (osName.indexOf("9") != -1 || osName.indexOf("Me") != -1) {
                bldr.command("command.com", "/c", "start", scriptFile.getAbsolutePath());
            } else {
                bldr.command("cmd.exe", "/c", "start", scriptFile.getAbsolutePath());
            }
        } else {
            String term = System.getenv(TERMINAL_ENV);
            if (term != null) {
                log.dbug(format("TERMINAL env set as %s", term));
                bldr.command(term, "-e", scriptFile.getAbsolutePath());
            } else {
                log.dbug(format("TERMINAL env not set", term));
                bldr.command(scriptFile.getAbsolutePath());
            }
        }
        log.info(format("Set process command to - %s", Arrays.toString(bldr.command().toArray())));

        if (localJvmPresent(new File(INSTALL_FOLDER))) {
            String JAVA_HOME = Paths.get(INSTALL_FOLDER, "java_vm").toString();
            log.info(format("Local JVM exists, courtesy of getdown - %s", JAVA_HOME));
            Map<String,String> env = bldr.environment();
            env.put("JAVA_HOME", JAVA_HOME);
            log.info(format("Set JAVA_HOME to local JVM - %s", JAVA_HOME));
        } else {
            log.info("System JVM meets requirements; no local JVM");
        }

        return bldr.start();
    }

    private static boolean localJvmPresent(File directory) {
        return new File(directory, "java_vm").exists();
    }
}
