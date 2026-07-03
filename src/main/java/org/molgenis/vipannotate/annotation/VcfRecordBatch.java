package org.molgenis.vipannotate.annotation;

import java.util.List;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

public record VcfRecordBatch(List<VcfRecord> batch) {}
