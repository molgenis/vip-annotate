package org.molgenis.vipannotate.annotation;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.Logger;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnotationDbDownloader implements AutoCloseable {
  private static final String BASE_URL =
      "https://download.molgeniscloud.org/downloads/vip-annotate/latest/";
  private final HttpClient httpClient;
  private final MessageDigest messageDigest;

  public void download(Path outputDir, @Nullable Boolean force) {
    String manifest = downloadManifest();
    List<ManifestEntry> manifestEntries = parseManifest(manifest);

    try {
      Files.createDirectories(outputDir);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    for (ManifestEntry manifestEntry : manifestEntries) {
      Path outputFile = outputDir.resolve(manifestEntry.filename());
      if (Files.exists(outputFile)) {
        if (force != null && force) {
          try {
            Files.delete(outputFile);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        }
      }
      downloadAndVerify(manifestEntry, outputFile);
    }
  }

  @SuppressWarnings("DataFlowIssue")
  private String downloadManifest() {
    HttpRequest request =
        HttpRequest.newBuilder().uri(URI.create(BASE_URL + "SHA256SUMS")).GET().build();

    HttpResponse<String> response;
    try {
      response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return response.body();
  }

  record ManifestEntry(String sha256, String filename) {}

  private static List<ManifestEntry> parseManifest(String manifest) {
    return Arrays.stream(manifest.split("\\R"))
        .map(
            line -> {
              String[] parts = line.split("\\s+", 2);
              return new ManifestEntry(parts[0], parts[1]);
            })
        .toList();
  }

  @SuppressWarnings("DataFlowIssue")
  private void downloadAndVerify(ManifestEntry manifestEntry, Path outputFile) {
    URL url;
    try {
      url = URI.create(BASE_URL + manifestEntry.filename()).toURL();
    } catch (MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
    Logger.debug("downloading %s...", url);

    try (DigestInputStream inputStream =
            new DigestInputStream(new BufferedInputStream(url.openStream()), messageDigest);
        OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(outputFile))) {
      inputStream.transferTo(outputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    String sha256 = HexFormat.of().formatHex(messageDigest.digest());
    messageDigest.reset();

    if (!sha256.equalsIgnoreCase(manifestEntry.sha256())) {
      throw new UncheckedIOException(
          new IOException(
              "Checksum mismatch for %s: expected: %s, actual: %s"
                  .formatted(outputFile.getFileName(), manifestEntry.sha256(), sha256)));
    }
  }

  @Override
  public void close() {
    ClosableUtils.close(httpClient);
  }

  @SuppressWarnings("DataFlowIssue")
  public static AnnotationDbDownloader create() {
    HttpClient httpClient =
        HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    return new AnnotationDbDownloader(httpClient, messageDigest);
  }
}
