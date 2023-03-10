package com.umc.i.src.member;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.umc.i.config.Constant;
import com.umc.i.src.member.model.get.GetMemRes;
import com.umc.i.src.member.model.patch.PatchMemReq;
import com.umc.i.src.member.model.post.PostAuthNumberReq;
import com.umc.i.src.member.model.post.PostJoinReq;
import com.umc.i.src.member.model.post.PostJoinRes;
import com.umc.i.src.member.model.post.PostMemblockReq;
import com.umc.i.utils.S3Storage.UploadImageS3;

import com.umc.i.utils.UserSha256;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.member.model.post.PostAuthRes;

import lombok.RequiredArgsConstructor;

import static com.umc.i.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    @Autowired
    private final UploadImageS3 uploadImageS3;

    private final AmazonS3 amazonS3;

    //????????? ?????? -> ????????? ?????? ????????????
    private final JavaMailSender eMailSender;
    // ??????????????? ???????????? ?????? ??????
    private final SpringTemplateEngine templateEngine;

    @Autowired
    private MemberDao memberDao;
    private String authCode; //????????????

    // ?????? ???????????? ??????
    public void createCode() {
        Random random = new Random();
        int checkNum = random.nextInt(899999) + 100000; // ???????????? ??????: 100000 ~ 999999
        System.out.println("????????????: " + checkNum);
        authCode = Integer.toString(checkNum);
    }

    // ?????? ?????? ??????
    public MimeMessage createEmailForm(String email, String authCode) throws BaseException {
        String setFrom = "umcteami215@gmail.com";  // ????????? ?????? ?????????
        String toEmail = email; //?????? ??????
        String title = "?????? - ???????????? ???????????? ??? ?????? ?????? ??????";    // ?????? ??????

        try {
            MimeMessage message = eMailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, toEmail);     //?????? ????????? ??????
            message.setSubject(title);      // ?????? ??????
            message.setFrom(setFrom);       // ????????? ?????????
            message.setText(setContext(authCode), "utf-8", "html");
            return message;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.POST_AUTH_SEND_FAIL);
        }
    }

    // ?????? ??????
    public PostAuthRes sendEmail(String toEmail, int isFind) throws BaseException {
        // ?????? ?????? ??????
        if(memberDao.checkEmail(toEmail) == 0 || isFind == 1) {
            createCode();   // ???????????? ??????

            // ?????? ????????? ????????? ?????? ??????
            MimeMessage emailForm = createEmailForm(toEmail, authCode);

            // ?????? ?????? ??????
            eMailSender.send(emailForm);
            int autoIdx = memberDao.createAuth(1, authCode);

            return new PostAuthRes(autoIdx);
        }
        return null;
    }

    // ??????????????? ????????? context ??????
    public String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("signup", context);     // signup.html
    }

    // ???????????? ??????
    @SuppressWarnings("unchecked")
	public PostAuthRes send_msg(String tel, int isFind) throws BaseException {
        //???????????? ?????? ??????
        if(memberDao.checkPhone(tel) == 0 || isFind == 1) {
            // ???????????? ??????
            createCode();

            String hostNameUrl = "https://sens.apigw.ntruss.com";     		// ????????? URL
            String requestUrl= "/sms/v2/services/";                   		// ?????? URL
            String requestUrlType = "/messages";                      		// ?????? URL
            String accessKey = "WmOvHZnmnkfla7ApfDhy";                     	// ?????? ?????????
            String secretKey = "IPPjyEWY0gITTQVhNNs7TeeGzF9lGXs1IC2ZghAs";  // 2??? ????????? ?????? ??????????????? ???????????? service secret
            String serviceId = "ncp:sms:kr:299805410270:umc-i";        		// ??????????????? ????????? SMS ????????? ID
            String method = "POST";											// ?????? method
            String timestamp = Long.toString(System.currentTimeMillis()); 	// current timestamp (epoch)
            requestUrl += serviceId + requestUrlType;
            String apiUrl = hostNameUrl + requestUrl;

            // JSON ??? ????????? body data ??????
            JSONObject bodyJson = new JSONObject();
            JSONObject toJson = new JSONObject();
            JSONArray  toArr = new JSONArray();

            toJson.put("to",tel);
            toArr.add(toJson);

            bodyJson.put("type","sms");	// ????????? Type (sms | lms)
            bodyJson.put("contentType","COMM");
            bodyJson.put("countryCode","82");
            bodyJson.put("from","");	// ???????????? * ????????? ??????/????????? ????????? ????????? ??? ????????????.
            bodyJson.put("messages", toArr);
            bodyJson.put("content", "?????? - ???????????? ???????????? ???\n???????????? ????????? [" + authCode + "] ?????????.");

            String body = bodyJson.toJSONString();

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

                wr.write(body.getBytes("UTF-8"));
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                BufferedReader br;
                System.out.println("responseCode" +" " + responseCode);
                if(responseCode==202) { // ?????? ??????
                    br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                } else {  // ?????? ??????
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                }

                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();

            } catch (Exception e) {
                throw new BaseException(BaseResponseStatus.POST_AUTH_SEND_FAIL);
            }

            int autoIdx = memberDao.createAuth(2, authCode);

            return new PostAuthRes(autoIdx);
        }

        return null;
    }

    public static String makeSignature(String url, String timestamp, String method, String accessKey, String secretKey) throws BaseException {
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
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.POST_AUTH_SEND_FAIL);
        }

        return encodeBase64String;
    }

    // ???????????? ????????????
    public PostAuthNumberReq getSignAuthNumberObject(int authIdx) {
        return memberDao.findByAuthIdx(authIdx)
                .filter(o -> o.getAuthIdx() == authIdx)
                .orElse(null);
    }

    // ???????????? ?????? ?????? ??????
    public static boolean isExpired(PostAuthNumberReq postAuthNumberReq) {
        Date createdAt = postAuthNumberReq.getCreatedAt();
        Date date = new Date();

        long diffSec = (date.getTime() - createdAt.getTime()) / (1000);

        return diffSec <= Constant.NUMBER_AUTH_TIME_LIMIT;
    }

    //????????????
    public BaseResponseStatus createMem(PostJoinReq postJoinReq, MultipartFile profile) throws BaseException {
        try {
            //?????? ????????? ??????(0,1)
            int checkNick = memberDao.checkNick(postJoinReq.getNick());
            if(checkNick != 0){return PATCH_MEMBER_NICK_DOUBLE;}

            String saveFilePath = null;
            if(!profile.isEmpty()) {  // ?????? ????????? ?????????
                String fileName = "image" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

                // ????????? ??? ??????
                long time = System.currentTimeMillis();
                String originalFilename = profile.getOriginalFilename();
                String saveFileName = String.format("%d_%s", time, originalFilename.replaceAll(" ", ""));

                // ????????? ?????????
                saveFilePath = uploadImageS3.upload(profile, fileName, saveFileName);
            }

            //?????????
            String encryptPwd = UserSha256.encrypt(postJoinReq.getPw());
            postJoinReq.setPw(encryptPwd);

            if(profile.isEmpty()) {
                return memberDao.createMem(postJoinReq, null);
            }
            return memberDao.createMem(postJoinReq, uploadImageS3.getS3(saveFilePath));

        } catch (Exception exception) { // ???????????? ?????????
            exception.printStackTrace();
            throw new BaseException(POST_MEMBER_JOIN);
        }
    }

    //?????? ?????? ??????
    public BaseResponseStatus editMem(int memIdx,PatchMemReq patchMemReq,MultipartFile profile) throws BaseException, IOException {
        try {
            GetMemRes mem = memberDao.getMem(memIdx);
            Boolean editNick = false;
            if(!mem.getNick().equals(patchMemReq.getNick())) {  // ???????????? ????????????
                editNick = true;
                int editNickNum = memberDao.editNickNum(memIdx);
                if(editNickNum > 2){
                    return PATCH_MEMBER_NICKNUM_OVER;
                }

                int checkNick = memberDao.checkNick(patchMemReq.getNick());
                if(checkNick != 0){return PATCH_MEMBER_NICK_DOUBLE;}
            }

            //????????? ??????
            String saveFilePath = "";
            if(!profile.getOriginalFilename().equals("basic.jpg")) {  //?????? ???????????? ????????? + ?????? ????????? ?????? ???????????? ????????????
                String fileName = "image" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

                // ????????? ??? ??????
                long time = System.currentTimeMillis();
                String originalFilename = profile.getOriginalFilename();
                String saveFileName = String.format("%d_%s", time, originalFilename.replaceAll(" ", ""));

                // ????????? ?????????
                saveFilePath = uploadImageS3.upload(profile, fileName, saveFileName);
            }

            memberDao.editMem(memIdx,patchMemReq,uploadImageS3.getS3(saveFilePath), editNick); // ?????? ????????? ????????? ???????????? True(1), ????????? ????????? False(0)?????????.
            return SUCCESS;
        } catch (Exception exception) { // ????????? ??????
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //???????????? ??????
    public BaseResponseStatus editPw(int memIdx,String pw)throws BaseException{
        try {
            //?????????
            String encryptPwd = UserSha256.encrypt(pw);

            memberDao.editPw(encryptPwd,memIdx);
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //?????? ??????
    public GetMemRes getMem(int memIdx)throws BaseException{
        try {
            GetMemRes getMemRes = memberDao.getMem(memIdx);
            return getMemRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }

    //?????? ??????
    public void postWithdraw(int memIdx)throws BaseException{
        try {
            memberDao.postWithdraw(memIdx);
        }catch (BaseException exception){
            throw new BaseException(POST_MEMBER_WITHDRAW);
        }
    }
    //?????? ??????
    public void postMemblock(PostMemblockReq postMemblockReq)throws BaseException{
        try {
            memberDao.postMemblock(postMemblockReq);
        }catch (BaseException exception){
            throw new BaseException(POST_NEMBER_BLOCK_DOUBLE);
        }
    }

    // ???????????? ?????????
    public BaseResponseStatus changePw(String email, String pw) throws BaseException {
        try {
            // ?????????
            String encryptPwd = UserSha256.encrypt(pw);
            memberDao.changePw(memberDao.getMemIdx(email), encryptPwd);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }

    }
}