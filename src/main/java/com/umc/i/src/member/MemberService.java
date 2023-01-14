package com.umc.i.src.member;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    //의존성 주입 -> 필요한 객체 가져오기
    private final JavaMailSender eMailSender;
    // 타임리프를 사용하기 위한 객체
    private final SpringTemplateEngine templateEngine;
    private String authCode; //인증코드

    // 랜덤 인증코드 생성
    public void createCode() {
        Random random = new Random();
        int checkNum = random.nextInt(899999) + 100000; // 인증번호 범위: 100000 ~ 999999
        System.out.println("인증번호: " + checkNum);
        authCode = Integer.toString(checkNum);
    }

    // 메일 양식 작성
    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException {
        createCode();   // 인증코드 생성
        String setFrom = "amanda010926@gmail.com";  // 보내는 사람 이메일
        String toEmail = email; //받는 사람
        String title = "아이 - 아름답게 이별하는 법 본인 인증 코드";    // 메일 제목

        MimeMessage message = eMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, toEmail);     //보낼 이메일 설정
        message.setSubject(title);      // 제목 설정   
        message.setFrom(setFrom);       // 보내는 이메일
        message.setText(setContext(authCode), "utf-8", "html");

        return message;
    }

    // 메일 전송
    public String sendEmail(String toEmail) throws MessagingException, UnsupportedEncodingException {
        // 메일 전송에 필요한 정보 설정
        MimeMessage emailForm = createEmailForm(toEmail);
        // 실제 메일 전송
        eMailSender.send(emailForm);

        return authCode;
    }

    // 타임리프를 이용한 context 설정
    public String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("signup", context);     // signup.html
    }

    @SuppressWarnings("unchecked")
	public String send_msg(String tel) {
        String hostNameUrl = "https://sens.apigw.ntruss.com";     		// 호스트 URL
        String requestUrl= "/sms/v2/services/";                   		// 요청 URL
        String requestUrlType = "/messages";                      		// 요청 URL
        String accessKey = "WmOvHZnmnkfla7ApfDhy";                     	// 개인 인증키
        String secretKey = "IPPjyEWY0gITTQVhNNs7TeeGzF9lGXs1IC2ZghAs";  // 2차 인증을 위해 서비스마다 할당되는 service secret
        String serviceId = "ncp:sms:kr:299805410270:umc-i";        		// 프로젝트에 할당된 SMS 서비스 ID
        String method = "POST";											// 요청 method
        String timestamp = Long.toString(System.currentTimeMillis()); 	// current timestamp (epoch)
        requestUrl += serviceId + requestUrlType;
        String apiUrl = hostNameUrl + requestUrl;

        // JSON 을 활용한 body data 생성

        JSONObject bodyJson = new JSONObject();
        JSONObject toJson = new JSONObject();
	    JSONArray  toArr = new JSONArray();

	    toJson.put("content","아이 - 아름답게 이별하는 법 본인인증 ["+authCode+"]");		// 난수와 함께 전송
	    toJson.put("to",tel);
	    toArr.add(toJson);
	    
	    bodyJson.put("type","sms");	// 메시지 Type (sms | lms)
	    bodyJson.put("contentType","COMM");
	    bodyJson.put("countryCode","82");
	    bodyJson.put("from","");	// 발신번호 * 사전에 인증/등록된 번호만 사용할 수 있습니다.		
	    bodyJson.put("messages", toArr);		
	    

	    String body = bodyJson.toJSONString();
	    
	    System.out.println(body);
	    
        try {
            URL url = new URL(apiUrl);

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("content-type", "application/json");
            con.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
            con.setRequestProperty("x-ncp-iam-access-key", accessKey);
            con.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(requestUrl, timestamp, method, accessKey, secretKey));
            con.setRequestMethod(method);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            
            wr.write(body.getBytes());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            BufferedReader br;
            System.out.println("responseCode" +" " + responseCode);
            if(responseCode==202) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            
            System.out.println(response.toString());

        } catch (Exception e) {
            System.out.println(e);
        }

        return authCode;
    }
	
	public static String makeSignature(String url, String timestamp, String method, String accessKey, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
	    String space = " "; 
	    String newLine = "\n"; 
	    

	    String message = new StringBuilder()
	        .append(method)
	        .append(space)
	        .append(url)
	        .append(newLine)
	        .append(timestamp)
	        .append(newLine)
	        .append(accessKey)
	        .toString();

	    SecretKeySpec signingKey;
	    String encodeBase64String;
		try {
			signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
		    encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);
		} catch (UnsupportedEncodingException e) {
			encodeBase64String = e.toString();
		}
	    

	  return encodeBase64String;
	}
    
}
