package com.umc.i;

import com.umc.i.member.jwt.JdbcTemplateJwtRepository;
import com.umc.i.member.jwt.JwtRepository;
import com.umc.i.member.login.JdbcTemplateMemberRepository;
import com.umc.i.member.login.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    private final DataSource dataSource;

    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Bean
    public MemberRepository memberRepository() {
        return new JdbcTemplateMemberRepository(dataSource);
    }

    @Bean
    public JwtRepository jwtRepository() {
        return new JdbcTemplateJwtRepository(dataSource);
    }


}
