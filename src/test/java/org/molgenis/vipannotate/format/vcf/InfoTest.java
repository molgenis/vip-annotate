package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

class InfoTest {
  @Test
  void append() throws IOException {
    Info info = Info.wrap("KEY=VALUE");
    info.append("KEY2", "VALUE2");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY=VALUE;KEY2=VALUE2", writer.toString());
    }
  }

  @Test
  void appendToFlag() throws IOException {
    Info info = Info.wrap("KEY");
    info.append("KEY2", "VALUE2");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY;KEY2=VALUE2", writer.toString());
    }
  }

  @Test
  void appendToEmpty() throws IOException {
    Info info = Info.wrap();
    info.append("KEY", "VALUE");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY=VALUE", writer.toString());
    }
  }

  @Test
  void appendFlag() throws IOException {
    Info info = Info.wrap("KEY=VALUE");
    info.append("KEY2");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY=VALUE;KEY2", writer.toString());
    }
  }

  @Test
  void appendFlagToEmpty() throws IOException {
    Info info = Info.wrap();
    info.append("KEY");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY", writer.toString());
    }
  }

  @Test
  void appendAfterReset() throws IOException {
    Info info = Info.wrap("KEY=VALUE");
    info.append("KEY2", "VALUE2");
    info.reset("KEY3=VALUE3", 0, "KEY3=VALUE3".length());
    info.append("KEY4", "VALUE4");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY3=VALUE3;KEY4=VALUE4", writer.toString());
    }
  }

  @Test
  void appendMultiple() throws IOException {
    Info info = Info.wrap();
    info.append("KEY", "VALUE");
    info.append("KEY2", "VALUE2");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY=VALUE;KEY2=VALUE2", writer.toString());
    }
  }

  @Test
  void remove() throws IOException {
    Info info = Info.wrap("KEY=VALUE");
    info.remove("KEY");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals(".", writer.toString());
    }
  }

  @Test
  void removeNonExisting() throws IOException {
    Info info = Info.wrap("KEY=VALUE");
    info.remove("KEY2");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY=VALUE", writer.toString());
    }
  }

  @Test
  void removeFlag() throws IOException {
    Info info = Info.wrap("KEY");
    info.remove("KEY");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals(".", writer.toString());
    }
  }

  @Test
  void removeFlagNonExisting() throws IOException {
    Info info = Info.wrap("KEY");
    info.remove("KEY2");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY", writer.toString());
    }
  }

  @Test
  void put() throws IOException {
    Info info = Info.wrap("KEY=VALUE");
    info.put("KEY", "VALUE2");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY=VALUE2", writer.toString());
    }
  }

  @Test
  void putToFlag() throws IOException {
    Info info = Info.wrap("KEY");
    info.put("KEY2", "VALUE2");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY;KEY2=VALUE2", writer.toString());
    }
  }

  @Test
  void putToEmpty() throws IOException {
    Info info = Info.wrap();
    info.put("KEY", "VALUE");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY=VALUE", writer.toString());
    }
  }

  @Test
  void putFlag() throws IOException {
    Info info = Info.wrap("KEY=VALUE");
    info.put("KEY2");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY=VALUE;KEY2", writer.toString());
    }
  }

  @Test
  void putFlagToEmpty() throws IOException {
    Info info = Info.wrap();
    info.put("KEY");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY", writer.toString());
    }
  }

  @Test
  void putAfterReset() throws IOException {
    Info info = Info.wrap("KEY=VALUE");
    info.put("KEY2", "VALUE2");
    info.reset("KEY3=VALUE3", 0, "KEY3=VALUE3".length());
    info.put("KEY4", "VALUE4");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY3=VALUE3;KEY4=VALUE4", writer.toString());
    }
  }

  @Test
  void writeUnmodified() throws IOException {
    Info info = Info.wrap("KEY=VALUE");
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals("KEY=VALUE", writer.toString());
    }
  }

  @Test
  void writeUnmodifiedEmpty() throws IOException {
    Info info = Info.wrap();
    try (StringWriter writer = new StringWriter()) {
      info.write(writer);
      assertEquals(".", writer.toString());
    }
  }
}
