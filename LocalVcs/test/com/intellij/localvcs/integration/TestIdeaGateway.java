package com.intellij.localvcs.integration;

import com.intellij.mock.MockDocument;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TestIdeaGateway extends IdeaGateway {
  private String myPhysicalContent;
  private List<MyDocument> myUnsavedDocuments = new ArrayList<MyDocument>();

  public TestIdeaGateway() {
    super(null);
  }

  @Override
  protected FileFilter createFileFilter() {
    return new TestFileFilter();
  }

  public void setFileFilter(FileFilter f) {
    myFileFilter = f;
  }

  @Override
  public <T> T performCommandInsideWriteAction(String name, Callable<T> c) {
    try {
      return c.call();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean ensureFilesAreWritable(VirtualFile... ff) {
    return true;
  }

  @Override
  public byte[] getPhysicalContent(VirtualFile f) throws IOException {
    if (myPhysicalContent == null) return f.contentsToByteArray();
    return myPhysicalContent.getBytes();
  }

  public void setPhysicalContane(String c) {
    myPhysicalContent = c;
  }

  @Override
  public byte[] getDocumentByteContent(VirtualFile f) {
    try {
      return f.contentsToByteArray();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Document[] getUnsavedDocuments() {
    return myUnsavedDocuments.toArray(new Document[0]);
  }

  public void addUnsavedDocument(String name, String content, long timestamp) {
    MyDocument d = new MyDocument(name, content, timestamp);
    myUnsavedDocuments.remove(d);
    myUnsavedDocuments.add(d);
  }

  @Override
  public VirtualFile getDocumentFile(Document d) {
    return ((MyDocument)d).getFile();
  }

  private class MyDocument extends MockDocument {
    private String myName;
    private String myContent;
    private long myTimestamp;

    public MyDocument(String name, String content, long timestamp) {
      myName = name;
      myContent = content;
      myTimestamp = timestamp;
    }

    @Override
    public String getText() {
      return myContent;
    }

    public VirtualFile getFile() {
      return new TestVirtualFile(myName, null, myTimestamp);
    }

    @Override
    public boolean equals(Object o) {
      return myName.equals(((MyDocument)o).myName);
    }
  }
}
