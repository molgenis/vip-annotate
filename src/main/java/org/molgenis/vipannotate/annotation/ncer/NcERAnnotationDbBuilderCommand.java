package org.molgenis.vipannotate.annotation.ncer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.molgenis.vipannotate.annotation.AnnotationSpecReader;
import org.molgenis.vipannotate.annotation.ContigRegistry;
import org.molgenis.vipannotate.annotation.Region;
import org.molgenis.vipannotate.annotation.spec.AnnotationSpec;
import org.molgenis.vipannotate.cli.Command;
import org.molgenis.vipannotate.cli.DbBuildSubCommandArgs;
import org.molgenis.vipannotate.cli.DbBuildSubCommandArgsParser;
import org.molgenis.vipannotate.cli.RegionParser;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexParser;
import org.molgenis.vipannotate.format.vdb.*;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Input;
import org.molgenis.vipannotate.util.Logger;

// TODO dedup with *AnnotationDbBuilderCommand
public class NcERAnnotationDbBuilderCommand implements Command {
  private static final String COMMAND = "ncer";

  @Override
  public void run(String[] args) {
    DbBuildSubCommandArgs commandArgs = new DbBuildSubCommandArgsParser(COMMAND).parse(args);

    String spec =
        """
              {
                "id": "ncer",
                "version": "1.0.0",
                "input": {
                 "type": "bed",
                 "from": "name"
                },
                "schema": {
                  "annotation_type": "position",
                  "supported_variant_types": ["snv", "mnv", "indel", "insertion", "deletion"],
                  "annotation_datasets": [{
                    "id": "score",
                    "annotation_value": {
                      "storage_type": {
                        "scalar_type": "u16"
                      },
                      "logical_type": {
                        "scalar_type": "f64",
                        "nullable": true
                      },
                      "encoding": {
                        "type": "quantized",
                        "range": {
                          "min": 0,
                          "max": 100
                        },
                        "levels": {
                          "min": 1,
                          "max": 65535
                        },
                        "null_code": 0
                      }
                    }
                  }],
                  "annotation_selector": "max_value"
                },
                "output": {
                  "type": "vcf",
                  "infoId": "ncER",
                  "infoNumber": "A",
                  "infoType": "Float",
                  "infoDescription": "ncER score",
                  "infoVersion": "1.0.0"
                }
              }
              """;
    byte[] bytes = spec.getBytes(StandardCharsets.UTF_8);
    try (MemoryBuffer memoryBuffer =
        MemoryBuffer.wrap(new byte[bytes.length + MemoryBuffer.VAR_INT_MAX_BYTE_SIZE])) {
      memoryBuffer.putByteArray(bytes);
      memoryBuffer.flip();
      AnnotationSpec annotationSpec = AnnotationSpecReader.create().readSpec(memoryBuffer);

      Input ncERInput = commandArgs.input();
      Path faiFile = commandArgs.faiFile();
      Path dbOutput = commandArgs.output();

      Logger.debug("creating database ...");
      long startCreateDb = System.currentTimeMillis();

      FastaIndex fastaIndex = FastaIndexParser.create(faiFile);
      ContigRegistry contigRegistry = ContigRegistry.create(fastaIndex);
      String regionsStr = commandArgs.regionsStr();
      List<Region> regions =
          regionsStr != null ? new RegionParser(contigRegistry).parse(regionsStr) : null;

      boolean force = commandArgs.force() != null && commandArgs.force();
      VdbMemoryBufferFactory memBufferFactory = new VdbMemoryBufferFactory();
      VdbArchiveWriter vdbArchiveWriter =
          VdbArchiveWriterFactory.create(memBufferFactory).create(dbOutput, force);
      try (PartitionedVdbArchiveWriter archiveWriter =
          PartitionedVdbArchiveWriter.create(vdbArchiveWriter, memBufferFactory)) {
        archiveWriter.write("spec", Compression.ZSTD, IoMode.BUFFERED, memoryBuffer);
        //        new AnnotationDbBuilder()
        //            .create(annotationSpec, ncERInput, regions, fastaIndex, archiveWriter);
      }

      long endCreateDb = System.currentTimeMillis();
      Logger.debug("creating database done in %sms", endCreateDb - startCreateDb);
    }
  }
}
