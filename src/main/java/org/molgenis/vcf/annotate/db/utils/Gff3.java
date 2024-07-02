package org.molgenis.vcf.annotate.db.utils;

import java.util.*;
import lombok.NonNull;

public class Gff3 implements Iterable<Map.Entry<String, Gff3.Features>> {
  private final Map<String, Features> sequenceToFeaturesMap;

  public Gff3() {
    sequenceToFeaturesMap = new LinkedHashMap<>();
  }

  @Override
  public @NonNull Iterator<Map.Entry<String, Features>> iterator() {
    return sequenceToFeaturesMap.entrySet().iterator();
  }

  public void addFeature(Gff3Parser.Feature feature) {
    sequenceToFeaturesMap.computeIfAbsent(feature.seqId(), Features::new).addFeature(feature);
  }

  public static class Features implements Iterable<Gff3Parser.Feature> {
    private final List<Gff3Parser.Feature> features;
    // features can be multiline
    private final Map<String, List<Gff3Parser.Feature>> idToFeatureMap;

    public Features(String sequenceId) {
      this.features = new ArrayList<>();
      this.idToFeatureMap = new HashMap<>();
    }

    @Override
    public @NonNull Iterator<Gff3Parser.Feature> iterator() {
      return features.iterator();
    }

    public Gff3Parser.Feature getParent(Gff3Parser.Feature feature) {
      List<String> parentIds = feature.getAttributeParent();
      if (parentIds == null) return null;
      if (parentIds.size() > 1) throw new RuntimeException();
      List<Gff3Parser.Feature> featureList = idToFeatureMap.get(parentIds.getFirst());
      if (featureList.size() != 1)
        throw new RuntimeException(); // FIXME how to handle multi-line features?
      return featureList.getFirst();
    }

    public void addFeature(Gff3Parser.Feature feature) {
      features.add(feature);

      String featureId = feature.getAttributeId();
      if (featureId != null) {
        idToFeatureMap.computeIfAbsent(featureId, k -> new ArrayList<>(1)).add(feature);
      }
    }
  }
}
