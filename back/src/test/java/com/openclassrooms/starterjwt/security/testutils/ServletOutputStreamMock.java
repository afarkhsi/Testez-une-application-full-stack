package com.openclassrooms.starterjwt.security.testutils;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ServletOutputStreamMock extends ServletOutputStream {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {}

    @Override
    public void write(int b) throws IOException {
        baos.write(b);
    }

    public String getWrittenContent() {
        return baos.toString();
    }
}