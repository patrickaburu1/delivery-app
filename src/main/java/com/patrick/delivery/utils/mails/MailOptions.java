/*
 * The MIT License
 *
 * Copyright 2016 Anthony Mwawughanga.
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

import com.sendgrid.*;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The parameters used to send the email to the client
 *
 * @author Nerd
 * @version 1.0.0
 * @category Mailing
 * @package Checksmart
 * @since 2016-08-11
 */
public class MailOptions {
    private Email _email;
    private final Personalization _personalization;
    private final Mail _mail;

    public enum Templates {
        NEW_ACCOUNT_TEMPLATE("50a37242-a7d2-42e5-8afb-8a6842dd6c6f"),
        PASSWORD_RESET_NOTIFICATION_TEMPLATE("001ff2e4-656d-4c80-984f-d465d4ac09fc"),
        PASSWORD_RESET_TEMPLATE("cc4b5a11-0973-497c-b476-bee9ca26986b");

        public final String template;

        Templates(String template) {
            this.template = template;
        }
    }

    /**
     * Init default values
     */
    MailOptions() {
        _mail = new Mail();
        _personalization = new Personalization();
        String email = "noreply@ganjipayments.com";
        //Default mail originator
        _email = new Email(email, "SWIGGO");
        _mail.setFrom(_email);
    }

    /**
     * Fetch a packed Mail instance
     *
     * @return Mail
     */
    public Mail init() {
        _mail.addPersonalization(_personalization);
        return _mail;
    }

    /**
     * When there is need to override the default sender
     *
     * @param email
     * @param name
     * @return MailOptions
     */
    public MailOptions setFrom(String email, String name) {
        _email = new Email(email, name);
        _mail.setFrom(_email);
        return this;
    }

    /**
     * Recipient's information
     *
     * @param email
     * @return MailOptions
     */
    public MailOptions setTo(String email) {
        _email = new Email(email);
        _personalization.addTo(_email);
        return this;
    }

    /**
     * Recipient's information
     *
     * @param email
     * @param name
     * @return MailOptions
     */
    public MailOptions setTo(String email, String name) {
        _email = new Email(email, name);
        _personalization.addTo(_email);
        return this;
    }

    /**
     * Recipients' information
     *
     * @param emails
     * @return MailOptions
     */
    public MailOptions setTo(List<String> emails) {
        emails.stream().forEach((email) -> {
            _email = new Email(email);
            _personalization.addTo(_email);
        });
        return this;
    }

    /**
     * Recipient's information for a carbon copy
     *
     * @param email
     * @return MailOptions
     */
    public MailOptions setCc(String email) {
        _email = new Email(email);
        _personalization.addCc(_email);
        return this;
    }

    /**
     * Recipient's information for a carbon copy
     *
     * @param email
     * @param name
     * @return MailOptions
     */
    public MailOptions setCc(String email, String name) {
        _email = new Email(email, name);
        _personalization.addCc(_email);
        return this;
    }

    /**
     * Recipients' information for a carbon copy
     *
     * @param emails
     * @return MailOptions
     */
    public MailOptions setCc(List<String> emails) {
        emails.stream().forEach((email) -> {
            _email = new Email(email);
            _personalization.addCc(_email);
        });
        return this;
    }

    /**
     * Recipient information for a blind copy
     *
     * @param email
     * @return MailOptions
     */
    public MailOptions setBcc(String email) {
        _email = new Email(email);
        _personalization.addBcc(_email);
        return this;
    }

    /**
     * Recipients' information for a blind copy
     *
     * @param emails
     * @return MailOptions
     */
    public MailOptions setBcc(List<String> emails) {
        emails.stream().forEach((email) -> {
            _email = new Email(email);
            _personalization.addBcc(_email);
        });
        return this;
    }

    /**
     * Sets the email subject
     *
     * @param subject
     * @return MailOptions
     */
    public MailOptions setSubject(String subject) {
        _personalization.setSubject(subject);
        return this;
    }

    /**
     * Set the template to use when sending the email
     *
     * @param templateId
     * @return MailOptions
     */
    public MailOptions setTemplateId(String templateId) {
        _mail.setTemplateId(templateId);
        return this;
    }

    /**
     * In the event that one will specify the content directly to the service
     *
     * @param content
     * @return MailOptions
     */
    public MailOptions setContent(String content) {
        Content _content;
        if (null == content) {
            _content = new Content("text/plain", content);
        } else {
            _content = new Content("text/html", " ");
        }
        _mail.addContent(_content);
        return this;
    }

    /**
     * Populate dynamic values when using a template
     *
     * @param key
     * @param value
     * @return MailOptions
     */
    public MailOptions addAttribute(String key, String value) {
        _personalization.addSubstitution(key, HtmlUtils.htmlEscape(value));
        return this;
    }

    /**
     * Populate dynamic values when using a template
     *
     * @param map
     * @return MailOptions
     */
    public MailOptions addAttribute(Map<String, Object> map) {
        return addAttribute(map, true);
    }

    /**
     * Populate dynamic values when using a template
     *
     * @param map
     * @return MailOptions
     */
    public MailOptions addAttribute(Map<String, Object> map, boolean htmlEscape) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if ((entry.getValue()) instanceof BigDecimal) {
                _personalization.addSubstitution(entry.getKey(), (entry.getValue()).toString());
            } else if ((entry.getValue()) instanceof Long) {
                _personalization.addSubstitution(entry.getKey(), (entry.getValue()).toString());
            } else if (entry.getValue() instanceof ArrayList) {
            } else {
                if (htmlEscape)
                    _personalization.addSubstitution(entry.getKey(), HtmlUtils.htmlEscape((String) entry.getValue()));
                else {
                    _personalization.addSubstitution(entry.getKey(), (String) entry.getValue());
                }
            }
        }
        return this;
    }

    /**
     * Append an attachment to the email
     *
     * @param attachment
     * @return MailOptions
     */
    public MailOptions setAttachments(Attachment attachment) {
        _mail.addAttachments(attachment.getAttachment());
        return this;
    }

    public MailOptions setAttachments(Attachments attachments) {
        _mail.addAttachments(attachments);
        return this;
    }

    /**
     * Append attachments to the email
     *
     * @param attachments
     * @return MailOptions
     */
    public MailOptions setAttachments(List<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            _mail.addAttachments(attachment.getAttachment());
        }
        return this;
    }

    public String getReceipients() {
        List<Email> emails = _personalization.getTos();
        StringBuilder nodes = new StringBuilder();
        for (Email node : emails) {
            if (nodes.length() > 1) {
                nodes.append(", ").append(node.getEmail());
            } else {
                nodes.append(node.getEmail());
            }
        }

        return nodes.toString();
    }
}
