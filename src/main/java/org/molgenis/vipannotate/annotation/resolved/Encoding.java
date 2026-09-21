package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = EnumEncoding.class, name = "enum")})
public sealed interface Encoding permits EnumEncoding {}
