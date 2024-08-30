package com.getcapacitor.shin;

import android.content.Context;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SegmentCache {
  private Context context;
  private long ttl; // Time-to-live in milliseconds
  private ScheduledExecutorService scheduler;
  private ExecutorService downloadExecutor;

  public SegmentCache(Context context, long ttl) {
    this.context = context;
    this.ttl = ttl;
    this.scheduler = Executors.newScheduledThreadPool(1);
    this.downloadExecutor = Executors.newSingleThreadExecutor();

    // Schedule cleanup task to run periodically
    scheduler.scheduleAtFixedRate(this::cleanUpExpiredFiles, ttl, ttl, TimeUnit.MILLISECONDS);
  }

  public static String sha256(String input) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");

    byte[] hashBytes = digest.digest(input.getBytes());

    StringBuilder hexString = new StringBuilder();
    for (byte b : hashBytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }

    return hexString.toString();
  }

  public void cache(String urlString, String hash) {
    downloadExecutor.submit(() -> {
      try {
        URI uri = URI.create(urlString);
        URL url = uri.toURL();

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();

        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
          throw new IOException("Failed to download file: " + urlConnection.getResponseMessage());
        }

        File cacheDir = context.getCacheDir();
        File cacheFile = new File(cacheDir, hash);

        if (cacheFile.exists()) {
          return; // File already exists
        }

        // Download the file and save it
        try (InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            FileOutputStream outputStream = new FileOutputStream(cacheFile)) {

          byte[] buffer = new byte[1024];
          int bytesRead;
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
          }
        } finally {
          urlConnection.disconnect();
        }

        // Update the last modified time
        cacheFile.setLastModified(System.currentTimeMillis());

      } catch (IOException e) {
        e.printStackTrace(); // Handle the error as needed
      }
    });
  }

  public String get(String hash) {
    File cacheDir = context.getCacheDir();
    File cacheFile = new File(cacheDir, hash);

    if (cacheFile.exists()) {
      return cacheFile.getAbsolutePath();
    }
    return null;
  }

  private boolean isExpired(File file) {
    long lastModified = file.lastModified();
    long currentTime = System.currentTimeMillis();
    return currentTime - lastModified > ttl;
  }

  private void cleanUpExpiredFiles() {
    File cacheDir = context.getCacheDir();
    File[] files = cacheDir.listFiles();
    if (files != null) {
      for (File file : files) {
        if (isExpired(file)) {
          file.delete();
        }
      }
    }
  }

  public void shutdown() {
    scheduler.shutdown();
  }
}
