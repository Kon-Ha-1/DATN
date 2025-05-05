package com.example.fsneaker.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ReadFileUtils extends HttpServlet{

    public final String path = "D:/DATN/Fsneaker/src/main/resources/static/images/product";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String imageUrl = request.getRequestURI();
        int repIndex = imageUrl.indexOf("/repository");
        String relativeImagePath = null;
        if (repIndex != -1) {
            repIndex += "/repository".length();
            relativeImagePath = imageUrl.substring(repIndex);
        }
        ServletOutputStream outStream;
        outStream = response.getOutputStream();
        FileInputStream fileInputStream = new FileInputStream(path + relativeImagePath);
        BufferedInputStream bin = new BufferedInputStream(fileInputStream);
        BufferedOutputStream bout = new BufferedOutputStream(outStream);
        int ch = 0;
        while ((ch = bin.read()) != -1)
            bout.write(ch);
        bin.close();
        fileInputStream.close();
        bout.close();
        outStream.close();
    }
}
