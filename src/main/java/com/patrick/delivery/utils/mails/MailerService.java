/*
 * The MIT License
 *
 * Copyright 2016 Ken Gichia.
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

import com.patrick.delivery.utils.templates.Console;
import com.patrick.delivery.utils.templates.View;
import com.sendgrid.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This interface defines the basic utilities that will be used to send an email
 * to a respective client
 *
 * @author Thomas Mwania
 * @version 1.0.0
 * @category Mails
 * @package checksmart
 * @since 01-08-2016
 */
@Transactional
@Service("sendGridMailService")
public class MailerService implements MailerServiceInterface {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${spring.sendgrid.api-key}")
    private String mailAPIKey;

    // The object used to send the email content
    private SendGrid sendGrid;

    @PostConstruct
    private void initialize(){
        sendGrid = new SendGrid(mailAPIKey);
    }

    /**
     * Get the object that will be used to send email via the SendGrid API
     *
     * @return Email
     */
    @Override
    public MailOptions sendGridConfig() {
        return new MailOptions();
    }

    /**
     * Send the mail message.
     *
     * @param email
     * @return Boolean
     */
    @Override
    public boolean sendMail(MailOptions email) {
        View view = new View("");
        StringBuilder error = new StringBuilder();
        error
                .append("Sendgrid responded with the following message when sending email to ")
                .append(email.getReceipients()).append(" : ");
        try {
            //Sign the email and add year
            email
                    .addAttribute("__Sign", view.getMessage("mail.sign"))
                    .addAttribute("__Year", String.valueOf(LocalDateTime.now().getYear()));
            Mail mail = email.init();
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

//            loggerService.debug("Mail Request: "+ request );
            logger.info("Mail request: " + request);

            //Retrieve response
            Response response = sendGrid.api(request);

//            loggerService.debug("Mail Response: "+ response );
            logger.info("Mail Response: " + response);

            logger.info(response.getStatusCode()+"\n"+response.getBody()+"\n"+response.getHeaders()+"\n");

            if (202 != response.getStatusCode()) {
                logger.info(response.getBody());
//                appAuditLogRepo.mailError(response. body, userId);
            }

            return true;
        } catch (Exception ex) {
            logger.error("An error occurred while sending the email: ", ex);
            return false;
        }
    }

    /**
     * Validate an email address using the mail exchange service
     *
     * @param address
     * @return Booelan
     */
    @Override
    public boolean isAddressValid(String address) {
        return ValidateEmail(address);
    }

    /**
     * Validate email address using Mailgun API
     *
     * @param email
     * @return boolean
     */
    public static boolean ValidateEmail(String email) {
        try {

            /**
             * TODO: Debug this piece of code
             * For some reason, this api doesn't work: a curl request responds with 'Email Validation is only available for paid accounts'
             */
//            Client client = new Client();
//            client.addFilter(new HTTPBasicAuthFilter("api", "pubkey-311c7283e12c46e505efbca097fd5028"));
//            WebResource webResource = client.resource("https://api.mailgun.net/v3/address/validate");
//            MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
//            queryParams.add("address", email);
//            ClientResponse response = webResource.queryParams(queryParams)
//                    .accept("application/json")
//                    .get(ClientResponse.class);
//
//            if (response.getStatus() != 200) {
//                return false;
//            }
//
//            String output = response.getEntity(String.class);
//            System.out.println("Output from Server .... \n");
//            System.out.println(output);
//
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode node = mapper.readTree(output);
//            System.err.println("valid:" + node.get("is_valid").asBoolean());
//            return node.get("is_valid").asBoolean();

            //Default fall back
            Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
            return matcher.find();
        } catch (Exception ex) {
            Console.printStackTrace(ex);
            return false;
        }
    }

}
