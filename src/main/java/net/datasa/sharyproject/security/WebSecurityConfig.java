package net.datasa.sharyproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //환경설정을 담당하는 자바 클래스에 붙이는 애노테이션
@EnableWebSecurity //시큐리티를 사용가능하게 해주는 애노테이션
public class WebSecurityConfig {
    //로그인 없이 접근 가능 경로
    private static final String[] PUBLIC_URLS = {
            "/**"                 //메인화면(root)
            , "/imagefile/**"   //**: 해당 경로 아래의 모든 경로 허용
            , "css/**"
            , "js/**"
            , "thymeleaf"
            , "/member/loginForm"     // 로그인 페이지
            , "/member/join"      // 회원가입 처리 경로
            , "/member/joinForm"  // 회원가입 페이지
            , "/share/**"
    };

    @Bean // 해당 메서드가 리턴하는 객체를 메모리에 미리 만들어놓아 메서드를 따로 호출하지 않아도 시큐리티에서 사용 가능
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {

            //요청에 대한 권한 설정
         /*   .authorizeHttpRequests(author -> author
                .requestMatchers(PUBLIC_URLS).permitAll()//permitAll():모두 접근 허용.(PUBLIC_URLS)안에 배열이 들어가야 하지만 코드가 길어지므로 따로 객체 생성함
                                                        //권한별로 접근 가능한 경로를 제어 가능
                .anyRequest().authenticated()               //그 외의 모든 요청은 인증 필요
            )*/
        

         http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(PUBLIC_URLS).permitAll()  // 공개 URL 설정
                                .requestMatchers("/subscribe", "/send", "/msgTest").authenticated() // 인증된 사용자만 접근
                                .anyRequest().authenticated()
                )


                //HTTP Basic 인증을 사용하도록 설정
            /*.httpBasic(Customizer.withDefaults())*/
            //폼 로그인 설정
/*            .formLogin(formLogin -> formLogin
                    .loginPage("/member/loginForm")          //로그인폼 페이지 경로.로그인 해야지만 들어갈 수 있는 페이지를 클릭했을 때 로그인 폼으로 이동
                    .usernameParameter("memberId")          //폼의 ID 파라미터 이름(name)
                    .passwordParameter("memberPw")    //폼의 비밀번호 파라미터 이름(name)
                    .loginProcessingUrl("/member/login")     //로그인폼 제출하여 처리할 경로(form의 action)
                    .defaultSuccessUrl("/",true)           //로그인 성공 시 이동할 경로
                    .permitAll()                      //로그인 페이지는 모두 접근 허용
            )*/
                .formLogin(formLogin -> formLogin
                        .loginPage("/member/loginForm")
                        .usernameParameter("memberId")
                        .passwordParameter("memberPw")
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/member/category", true)
                        .permitAll()
                )


            //로그아웃 설정
            .logout(logout -> logout
                    .logoutUrl("/member/logout")                   //로그아웃 처리 경로
                    .logoutSuccessUrl("/")                  //로그아웃 성공 시 이동할 경로
            )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);  // CSRF 비활성화

        return http.build();

        /*http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable);*/

    }

    //비밀번호 암호화를 위한 인코더를 빈으로 등록
    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
