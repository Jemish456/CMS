package com.globalvox.clinicmanagementsystem.service;
import com.lowagie.text.DocumentException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class PdfGenerateServiceImpl implements PdfGenerateService {
    private Logger logger = LoggerFactory.getLogger(PdfGenerateServiceImpl.class);

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${pdf.directory}")
    private String pdfDirectory;

    /* Generate pdf file using ITextRender*/
    @Override
    public File generatePdfFile(String templateName, Map<String, Object> data, String pdfFileName) {
        Context context=new Context();
        context.setVariables(data);
        String htmlContent=templateEngine.process(templateName,context);
        try {

            /* if pdf directory not exists then create it*/
            File exportPath=new File(pdfDirectory);
            if (!exportPath.exists()){
                exportPath.mkdirs();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(pdfDirectory + pdfFileName);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(fileOutputStream, false);
            renderer.finishPDF();
            fileOutputStream.close();
            return new File(pdfDirectory + pdfFileName);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (DocumentException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
