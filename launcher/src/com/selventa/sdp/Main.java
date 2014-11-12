package com.selventa.sdp;

import org.apache.log4j.Logger;

import javax.swing.*;

import static com.selventa.sdp.Util.equalMD5;
import static com.selventa.sdp.Util.extract;
import static com.selventa.sdp.Util.resource;
import static com.selventa.sdp.Util.windows;
import static com.selventa.sdp.Util.macos;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
     * Logger for launch debugging.
     */
    private static final Logger log = Logger.getRootLogger();

    private static final String LAUNCH_LOG = "Launching sdp-cytoscape3..." +
            "ip: %s" + ", host: %s" + ", user.home: %s" + ", user.dir: %s" +
            ", user.timezone: %s" + ", file.encoding: %s" + ", os.name: %s" +
            ", os.arch: %s" + ", os.version: %s" + ", java.vm.name: %s" +
            ", java.vm.vendor: %s" + ", java.vm.info: %s" + ", java.version: %s";

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

    private static final String UPDATE_ERROR_TITLE   = "Update Error";

    private static final String UPDATE_ERROR_MESSAGE =
            "Could not update Cytoscape installation (sdp-cytoscape3/cytoscape). " +
            "Please close the running Cytoscape and try again. \nException: %s";

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
     */
    public static void main(String[] args) {
        // log the machine launching...
        String addr = "N/A";
        String host = "N/A";

        try {
            addr = InetAddress.getLocalHost().getHostAddress();
            host = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException _) {}

        log.warn(format(LAUNCH_LOG, addr, host, getProperty("user.home"),
                getProperty("user.dir"), getProperty("user.timezone"),
                getProperty("file.encoding"), getProperty("os.name"),
                getProperty("os.arch"), getProperty("os.version"),
                getProperty("java.vm.name"), getProperty("java.vm.vendor"),
                getProperty("java.vm.info"), getProperty("java.version")));

	    File cypath = new File(INSTALL_FOLDER);
	    if (!cypath.exists()) {
	        cypath.mkdirs();
            if (!cypath.exists()) {
                log.error(format("Installation folder does not exist - %s", cypath.getAbsolutePath()));
            } else if (!cypath.canRead()) {
                log.error(format("Installation folder cannot be read from - %s", cypath.getAbsolutePath()));
            } else if (!cypath.canWrite()) {
                log.error(format("Installation folder cannot be written to - %s", cypath.getAbsolutePath()));
            }
	    }

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
                Path cytoscapeDir = new File(cypath, CY_FOLDER).toPath();
                try {
                    removeDirectory(cytoscapeDir);
                } catch (IOException ex) {
                    log.error(format("Exception removing cytoscape folder - %s", cytoscapeDir), ex);
                    JOptionPane.showMessageDialog(null, format(UPDATE_ERROR_MESSAGE, ex.getMessage()),
                            UPDATE_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }

                Files.copy(inJar, onDiskZip.toPath(), REPLACE_EXISTING);
                extract(onDiskZip, cypath);
                log.info("Successfully updated to latest zip file");
            } catch (IOException e) {
                log.error(format("Failure to replace zip file - %s", onDiskZip.getAbsolutePath()), e);
                log.error("Launch continuing with old cytoscape installation");
            }
	    } else {
            log.info(format("Existing zip file is the latest - %s", onDiskZip.getAbsolutePath()));
        }

        File execFile = get(cypath.getAbsolutePath(), CY_FOLDER, SCRIPT).toFile();
        setExecutable(execFile);
        setExecutable(get(cypath.getAbsolutePath(), CY_FOLDER, KARAF_SCRIPT).toFile());

        File wd = get(cypath.getAbsolutePath(), CY_FOLDER).toFile();
        final Process process;
        try {
            process = makeProcess(wd, execFile, log);
            log.info("Started process successfully; waiting for process to end");
            int code = process.waitFor();
            log.info(format("Process ended, happily? (code %d)", code));
            System.exit(code);
        } catch (IOException e) {
            log.error(format("Cannot start process; exiting (code 100)"), e);
            System.exit(100);
        } catch (InterruptedException e) {
            log.error("Process interrupted", e);
        }
    }

    private static Process makeProcess(File workingDirectory, File scriptFile, Logger log) throws IOException {
        final ProcessBuilder bldr = new ProcessBuilder().directory(workingDirectory);
        if (windows) {
            String osName = System.getProperty("os.name");
            if (osName.contains("9") || osName.contains("Me")) {
                bldr.command("command.com", "/c", "start", scriptFile.getAbsolutePath());
            } else {
                bldr.command("cmd.exe", "/c", scriptFile.getAbsolutePath());
            }
        } else if (macos) {
            bldr.command("/bin/sh", "-c", ("\"" + scriptFile.getAbsolutePath() + "\""));
        } else {
            bldr.command(scriptFile.getAbsolutePath());
        }
        log.info(format("Set process command to - %s", Arrays.toString(bldr.command().toArray())));

        if (localJvmPresent(new File(INSTALL_FOLDER))) {
            String JAVA_HOME = get(INSTALL_FOLDER, "java_vm").toString();
            log.info(format("Local JVM exists, courtesy of getdown - %s", JAVA_HOME));

            if (macos) {
                setExecutable(get(JAVA_HOME, "bin", "java").toFile());
                setExecutable(get(JAVA_HOME, "bin", "javaw").toFile());
            }

            Map<String, String> env = bldr.environment();
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

    private static void setExecutable(File f) {
        if (f.setExecutable(true, false)) {
            log.debug(format("Set executable bit for file - %s", f.getAbsolutePath()));
        } else {
            log.error(format("Failure to set executable bit for file - %s", f.getAbsolutePath()));
            log.error(format("File status, exists: %s, readable: %s, writable: %s, hidden: %s",
                      f.exists(), f.canRead(), f.canWrite(), f.isHidden()));
        }
    }

    private static void removeDirectory(Path p) throws IOException {
        log.info(format("Removing directory recursively - %s", p.toString()));
        Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes _) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException _) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        log.info(format("Successfully removed - %s", p.toString()));
    }
}
