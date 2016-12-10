package com.workshop.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;

@Configuration
@EnableGlobalMethodSecurity
@EnableConfigurationProperties
public class SecurityConfig {

    @Configuration
    @EnableWebSecurity
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        private static final String USERS_BY_USERNAME_QUERY =
                "SELECT username, password, enabled FROM accounts WHERE username = ?";
        private static final String AUTHORITIES_BY_USERNAME_QUERY =
                "SELECT username, authority FROM authorities WHERE username = ?";
        private final DataSource dataSource;

        public WebSecurityConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        public UserDetailsService userDetailsService() {
            JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
            jdbcDao.setDataSource(dataSource);
            jdbcDao.setUsersByUsernameQuery(USERS_BY_USERNAME_QUERY);
            jdbcDao.setAuthoritiesByUsernameQuery(AUTHORITIES_BY_USERNAME_QUERY);

            return jdbcDao;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .userDetailsService(userDetailsService())
                    .passwordEncoder(passwordEncoder());
        }

        @Bean("authManager")
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfig extends ResourceServerConfigurerAdapter {
        private final TokenStore tokenStore;

        public ResourceServerConfig(TokenStore tokenStore) {
            this.tokenStore = tokenStore;
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId("wjc-res").tokenStore(tokenStore);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .csrf()
                        .disable()
                    .authorizeRequests()
                    .antMatchers("/oauth/token").permitAll()
                    .antMatchers("/jcombat/**").permitAll()
                    .antMatchers("/**").hasRole("USER")
                    .antMatchers("/**")
                    .access("#oauth2.hasScope('write') and #oauth2.hasScope('trust') and hasRole('ROLE_USER')");
        }

        @Configuration
        @EnableAuthorizationServer
        protected static class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
            private final AuthenticationManager authenticationManager;
            private final UserDetailsService userDetailsService;

            public AuthServerConfig(@Qualifier("authManager") AuthenticationManager authenticationManager,
                                    UserDetailsService userDetailsService) {
                this.authenticationManager = authenticationManager;
                this.userDetailsService = userDetailsService;
            }

            @Bean
            public JwtAccessTokenConverter accessTokenConverter() {
                ClassPathResource resource = new ClassPathResource("wjc.jks");
                KeyPair keyPair = new KeyStoreKeyFactory(resource, "wjcsec".toCharArray()).getKeyPair("wjc");

                JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
                converter.setKeyPair(keyPair);

                return converter;
            }

            @Bean
            public TokenStore tokenStore() {
                return new JwtTokenStore(accessTokenConverter());
            }

            @Override
            public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
                security
                        .realm("wjc/client")
                        .tokenKeyAccess("permitAll()")
                        .checkTokenAccess("isAuthenticated()");

            }

            @Override
            public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
                clients
                        .inMemory()
                        .withClient("trustedClient")
                        .secret("wjcsec")
                        .authorizedGrantTypes("password", "refresh_token")
                        .scopes("trust", "read", "write")
                        .authorities("ROLE_USER", "ROLE_ADMIN")
                        .accessTokenValiditySeconds(300)
                        .refreshTokenValiditySeconds(86400);
            }

            @Override
            public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
                endpoints
                        .tokenStore(tokenStore())
                        .authenticationManager(authenticationManager)
                        .accessTokenConverter(accessTokenConverter())
                        .userDetailsService(userDetailsService);
            }
        }
    }
}
