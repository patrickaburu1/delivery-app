/*
 * The MIT License
 *
 * Copyright 2016 Anthony Mwawughanga
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.patrick.delivery.utils.mails;


import com.sendgrid.Attachments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The parameters used to send an attachment
 *
 * @author Anthony Mwawughanga
 * @version 1.0.0
 * @category Security
 * @package Teke
 * @since 11-08-2016
 */
public class Attachment {
    //Allowed dispositions
    public final static String DISPOSITION_INLINE = "inline";
    public final static String DISPOSITION_ATTACHMENT = "attachment";

    //Commonly used attachments
    public final static String MIMETYPE_PDF = "application/pdf";
    public final static String MIMETYPE_PNG = "image/png";

    private final Attachments _attachment;
    private String _templateName;
    private final Map<String, Object> _params;

    /**
     * Init default values
     */
    public Attachment() {
        //Intiliaze the attachment object
        _attachment = new Attachments();
        // Initialise the params
        _params = new HashMap<>();
    }

    /**
     * Initialise this object with the template to use
     *
     * @param templateName
     */
    public Attachment(String templateName) {
        //Intiliaze the attachment object
        _attachment = new Attachments();

        // Set the default template
        _templateName = templateName;

        // Initialise the params
        _params = new HashMap<>();
    }


    /**
     * Fetch an instance of populated Sendgrid Attachments
     *
     * @return Attachments
     */
    public Attachments getAttachment() {
        //Attach an encoded content
        _attachment.setContent(encodeFile());
        return _attachment;
    }

    /**
     * Set the HTML template to use
     *
     * @param templateName
     * @return Attachment
     */
    public Attachment setTemplate(String templateName) {
        _templateName = templateName;
        return this;
    }

    /**
     * MIME type of the attachment
     *
     * @param type
     * @return Attachment
     */
    public Attachment setType(String type) {
        _attachment.setType(type);
        return this;
    }

    /**
     * The filename of the attachment
     *
     * @param filename
     * @return Attachment
     */
    public Attachment setFileName(String filename) {
        _attachment.setFilename(filename);
        return this;
    }

    /**
     * Content disposition of the attachment
     *
     * @param disposition
     * @return Attachment
     */
    public Attachment setDisposition(String disposition) {
        _attachment.setDisposition(disposition);
        return this;
    }

    /**
     * A unique id for the attachment
     * <p>
     * Used mainly when the disposition is 'inline' and the attachment is an image
     * to be displayed in the body of the email. Exmaple:
     * <code>
     * <img src="cid:ii_139db99fdb5c3704"></img>
     * </code>
     * </p>
     *
     * @param contentId
     * @return Attachment
     */
    public Attachment setContentId(String contentId) {
        _attachment.setContentId(contentId);
        return this;
    }

    /**
     * Set an attribute that will be used in the template provided.
     *
     * @param key
     * @param value
     * @return Attachment
     */
    public Attachment addAttribute(String key, Object value) {
        // Check if the key has been set`11
        _params.put(key, value);

        // Allow the chaining
        return this;
    }

    /**
     * Set multiple attributes that will be used in the template
     * provided.
     *
     * @param map
     * @return Attachment
     */
    public Attachment mergeAttributes(Map<String, Object> map) {
        for (Map.Entry<String, Object> p : map.entrySet())
            _params.put(p.getKey(), p.getValue());

        // Allow the chaining
        return this;
    }

    /**
     * Encode the HTML template Base64
     *
     * @param file
     * @return String Base64 string
     */
    public String encodeFile() {
        String encodedFile = "";
        try {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setPrefix("templates/sendgrid/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("LEGACYHTML5");
            templateResolver.setCharacterEncoding("UTF-8");

            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            Context ctx = new Context();

            //Bind the dynamic values in the template
            Iterator it = _params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> pair = (Map.Entry<String, Object>) it.next();
                ctx.setVariable(pair.getKey(), pair.getValue());
            }

            //Template
            String htmlContent = templateEngine.process(_templateName, ctx);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            ITextFontResolver fontResolver = renderer.getFontResolver();

//            ClassPathResource regular = new ClassPathResource("/META-INF/fonts/LiberationSerif-Regular.ttf");
//            fontResolver.addFont(regular.getURL().toString(), BaseFont.IDENTITY_H, true);

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(os);

            byte[] pdfAsBytes = os.toByteArray();
            os.close();

            encodedFile = Base64.encode(pdfAsBytes);

        } catch (IOException | com.lowagie.text.DocumentException e) {
            e.printStackTrace();
        }
        return encodedFile;
    }

}
