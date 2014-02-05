package com.selventa.sdp;

import java.io.*;
import java.util.Date;

import static java.lang.String.format;
import static java.lang.System.getProperty;

public class Log {

    public static final String DBUG = "dbug";
    public static final String INFO = "info";
    public static final String WARN = "warn";
    public static final String FAIL = "fail";
    private static final String LINE_SEPARATOR = getProperty("line.separator");
    private final File location;

    public Log(File location) {
        this.location = location;
    }

    public void dbug(String msg) {
        final PrintWriter w = writer();
        try {
            w.print(prepare(DBUG, msg, null));
        } finally {
            w.close();
        }
    }

    public void dbug(String msg, Throwable t) {
        final PrintWriter w = writer();
        try {
            w.print(prepare(DBUG, msg, t));
        } finally {
            w.close();
        }
    }

    public void info(String msg) {
        final PrintWriter w = writer();
        try {
            w.print(prepare(INFO, msg, null));
        } finally {
            w.close();
        }
    }

    public void info(String msg, Throwable t) {
        final PrintWriter w = writer();
        try {
            w.print(prepare(INFO, msg, t));
        } finally {
            w.close();
        }
    }

    public void warn(String msg) {
        final PrintWriter w = writer();
        try {
            w.print(prepare(WARN, msg, null));
        } finally {
            w.close();
        }
    }

    public void warn(String msg, Throwable t) {
        final PrintWriter w = writer();
        try {
            w.print(prepare(WARN, msg, t));
        } finally {
            w.close();
        }
    }

    public void fail(String msg) {
        final PrintWriter w = writer();
        try {
            w.print(prepare(FAIL, msg, null));
        } finally {
            w.close();
        }
    }

    public void fail(String msg, Throwable t) {
        final PrintWriter w = writer();
        try {
            w.print(prepare(FAIL, msg, t));
        } finally {
            w.close();
        }
    }

    private String prepare(String level, String msg, Throwable t) {
        String log = format("[%s] %s: %s%s", new Date(), level, msg, LINE_SEPARATOR);
        if (t == null) return log;

        // handle optional throwable
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        return format("%s%s%s", log, sw.toString(), LINE_SEPARATOR);
    }

    private PrintWriter writer() {
        if (!location.exists()) {
            File parent = location.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new RuntimeException(new IOException("Cannot create log file for writing."));
            }
        }

        try {
            return new PrintWriter(new FileWriter(location, true));
        } catch (IOException e) {
            throw new RuntimeException("Cannot create writer.", e);
        }
    }
}
