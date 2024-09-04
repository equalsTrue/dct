package com.dct.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * 发送邮件工具类.
 *
 * @author magic
 * @data 2022/4/20
 */
@Component
public class EmailUtil {


//    @Autowired(required = true)
    @Resource
    private JavaMailSender mailSender;

    @Autowired
    Configuration configuration;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送带模板的邮件.
     *
     * @param template
     * @param toEmail
     * @param subject
     * @param model
     * @throws Exception
     */
    @Async
    public void sendMimeMessageMail(String template, String toEmail, String subject,
                                    Map<String, Object> model) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(subject);

            // freeMarker template
            Template t = configuration.getTemplate(template);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            return;
        }
    }

    /**
     * 发送 SMTP 邮件并抄送.
     *
     * @param template
     * @param toEmail
     * @param subject
     * @param model
     * @param cc
     * @throws Exception
     */
    @Async
    public void sendMimeMessageMail(String template, String toEmail, String subject, Map<String, Object> model, String[] cc) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(toEmail);
        helper.setCc(cc);
        helper.setSubject(subject);

        // freeMarker template
        Template t = configuration.getTemplate(template);
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
        helper.setText(content, true);
        mailSender.send(mimeMessage);
    }

}

