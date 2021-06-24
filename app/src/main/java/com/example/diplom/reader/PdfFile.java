package com.example.diplom.reader;

public class PdfFile {
    private String fileName;
    private String filePath;

    PdfFile(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    PdfFile()
    {
        this.fileName = "Нет необходимых разрешений, пожалуйста, установите нужные разрешения и попробуйте снова";
        this.filePath = "";
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}