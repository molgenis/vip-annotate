// package org.molgenis.vcf.annotate.util;
//
// import org.molgenis.vcf.annotate.db.model.ClosedInterval;
// import org.molgenis.vcf.annotate.db.model.Exon;
//
// public class ExonUtils {
//  public static void main(String[] args) {
//    //    NC_000001.11	BestRefSeq	exon	1890820	1891087	.	-	.
//    //
//	ID=exon-NM_001282539.2-1;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//    //    NC_000001.11	BestRefSeq	exon	1825397	1825499	.	-	.
//    //
//	ID=exon-NM_001282539.2-2;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//    //    NC_000001.11	BestRefSeq	exon	1817837	1817875	.	-	.
//    //
//	ID=exon-NM_001282539.2-3;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//    //    NC_000001.11	BestRefSeq	exon	1815756	1815862	.	-	.
//    //
//	ID=exon-NM_001282539.2-4;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//    //    NC_000001.11	BestRefSeq	exon	1806475	1806538	.	-	.
//    //
//	ID=exon-NM_001282539.2-5;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//    //    NC_000001.11	BestRefSeq	exon	1804419	1804581	.	-	.
//    //
//	ID=exon-NM_001282539.2-6;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//    //    NC_000001.11	BestRefSeq	exon	1793245	1793311	.	-	.
//    //
//	ID=exon-NM_001282539.2-7;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//    //    NC_000001.11	BestRefSeq	exon	1790395	1790596	.	-	.
//    //
//	ID=exon-NM_001282539.2-8;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//    //    NC_000001.11	BestRefSeq	exon	1789053	1789269	.	-	.
//    //
//	ID=exon-NM_001282539.2-9;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//    //    NC_000001.11	BestRefSeq	exon	1787322	1787437	.	-	.
//    //
//	ID=exon-NM_001282539.2-10;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//    //    NC_000001.11	BestRefSeq	exon	1785286	1787053	.	-	.
//    //
//	ID=exon-NM_001282539.2-11;Parent=rna-NM_001282539.2;Dbxref=GeneID:2782,GenBank:NM_001282539.2,HGNC:HGNC:4396,MIM:139380;gbkey=mRNA;gene=GNB1;product=G protein subunit beta 1%2C transcript variant 2;transcript_id=NM_001282539.2
//
//    Exon[] exons = new Exon[11];
//    int i = 0;
//    exons[i++] = Exon.builder().start(1890820).length(268).build();
//    exons[i++] = Exon.builder().start(1825397).length(103).build();
//    exons[i++] = Exon.builder().start(1817837).length(39).build();
//    exons[i++] = Exon.builder().start(1815756).length(107).build();
//    exons[i++] = Exon.builder().start(1806475).length(64).build();
//    exons[i++] = Exon.builder().start(1804419).length(163).build();
//    exons[i++] = Exon.builder().start(1793245).length(67).build();
//    exons[i++] = Exon.builder().start(1790395).length(202).build();
//    exons[i++] = Exon.builder().start(1789053).length(217).build();
//    exons[i++] = Exon.builder().start(1787322).length(116).build();
//    exons[i++] = Exon.builder().start(1785286).length(1768).build();
//
//    int low = 0, high = exons.length - 1;
//    while (low <= high) {
//      int mid = low + (high - low) / 2;
//
//      // Check if x is present at mid
//      if (exons[mid] == x) return mid;
//
//      // If x greater, ignore left half
//      if (exons[mid] < x) low = mid + 1;
//
//      // If x is smaller, ignore right half
//      else high = mid - 1;
//    }
//
//    // If we reach here, then element was
//    // not present
//    return -1;
//  }
// }
