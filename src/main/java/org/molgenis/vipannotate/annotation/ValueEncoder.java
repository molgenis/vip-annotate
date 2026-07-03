package org.molgenis.vipannotate.annotation;

public sealed interface ValueEncoder permits DoubleToIntValueEncoder, StringToByteValueEncoder {}
