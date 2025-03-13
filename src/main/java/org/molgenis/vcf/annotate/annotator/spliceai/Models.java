package org.molgenis.vcf.annotate.annotator.spliceai;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.tensorflow.SavedModelBundle;

public class Models implements AutoCloseable, Iterable<SavedModelBundle> {
  private final List<SavedModelBundle> models;

  private Models(List<SavedModelBundle> models) {
    this.models = requireNonNull(models);
  }

  public int count() {
    return models.size();
  }

  public static Models load(String... exportDirs) {
    long start = System.currentTimeMillis();
    List<SavedModelBundle> models = new ArrayList<>(exportDirs.length);
    for (String exportDir : exportDirs) {
      SavedModelBundle model = SavedModelBundle.load(exportDir, SavedModelBundle.DEFAULT_TAG);
      // System.out.println(model.metaGraphDef().getSignatureDefMap().get("serving_default"));
      models.add(model);
    }
    long end = System.currentTimeMillis();
    System.out.println("loading models took " + (end - start) + "ms");
    return new Models(models);
  }

  @Override
  public void close() {
    models.forEach(SavedModelBundle::close);
  }

  @Override
  public Iterator<SavedModelBundle> iterator() {
    return models.iterator();
  }
}
