package com.epam.esm.security;

import com.epam.esm.impl.UserService;
import com.epam.esm.security.filter.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    ApplicationUserDetailsService userDetailsService;


//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider(){
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService());
//        provider.setPasswordEncoder(new BCryptPasswordEncoder(10));
//        provider.setAuthoritiesMapper(authoritiesMapper());
//        return provider;
//    }
//
//
//    @Bean
//    GrantedAuthoritiesMapper authoritiesMapper() {
//        SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
//        authorityMapper.setConvertToUpperCase(true);
//        authorityMapper.setDefaultAuthority("GUEST");
//        return authorityMapper;

//    }
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(daoAuthenticationProvider());
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.
               csrf().disable()
               .authorizeRequests()
               .antMatchers("/api/v1/users/signin").permitAll()
               .antMatchers("/api/v1/users/signup").permitAll()
//               .antMatchers(HttpMethod.GET).permitAll()
//               .antMatchers(HttpMethod.POST).permitAll()
//               .antMatchers(HttpMethod.PUT).permitAll()
//               .antMatchers(HttpMethod.DELETE).permitAll()
               .anyRequest().authenticated()
               .and()
               .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
               .and()
               .httpBasic();
       http.addFilterBefore(new JwtTokenFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class);
    }

//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        return super.userDetailsService();
//    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}
